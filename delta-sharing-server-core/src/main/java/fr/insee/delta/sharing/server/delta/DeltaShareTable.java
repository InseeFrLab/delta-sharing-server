package fr.insee.delta.sharing.server.delta;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.delta.standalone.DeltaLog;
import io.delta.standalone.Snapshot;
import io.delta.standalone.internal.SnapshotImpl.State;
import scala.collection.JavaConverters;

public class DeltaShareTable {

  Logger logger = LoggerFactory.getLogger(DeltaShareTable.class);

  private final long preSignedUrlTimeoutSeconds;
  private final Boolean evaluatePredicateHint;
  private final Configuration hadoopConfiguration;
  private final Table table;
  private final DeltaLog deltaLog;


  public DeltaShareTable(long preSignedUrlTimeoutSeconds, Boolean evaluatePredicateHint,
      Table table, Configuration hadoopConfiguration) throws IOException {
    super();
    this.preSignedUrlTimeoutSeconds = preSignedUrlTimeoutSeconds;
    this.evaluatePredicateHint = evaluatePredicateHint;
    this.hadoopConfiguration = hadoopConfiguration;
    this.table = table;
    this.deltaLog = deltaLog();
    logger.info("deltalog {}" + this.deltaLog.getPath());
  }

  private DeltaLog deltaLog() throws IOException {
    final Path tablePath = new Path(table.getLocation());
    final FileSystem fs = tablePath.getFileSystem(this.hadoopConfiguration);
    if (!(fs instanceof S3AFileSystem)) {
      throw new IllegalStateException("Cannot share tables on non S3 file systems");
    }
    logger.info("deltalog instanciation");
    logger.info("table path : {}", tablePath);
    logger.info("{}", this.hadoopConfiguration);
    return DeltaLog.forTable(this.hadoopConfiguration, tablePath);
  }

  public Long getTableVersion() {
    final Snapshot snapshot = this.deltaLog.snapshot();
    validateDeltaTable(snapshot);
    return snapshot.getVersion();
  }


  private void validateDeltaTable(final Snapshot snapshot) {
    if (snapshot.getVersion() < 0) {
      throw new IllegalStateException("version not valid for " + this.table.getName());
    }
  }

  public Stream<Wrapper> query(boolean includeFiles, @Nullable List<String> predicateHits,
      Integer limitHint) throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    logger.info("query table {} as {}", this.table.getName(), this.table.getLocation());
    final Snapshot snapshot = deltaLog.snapshot();
    logger.info("version {}", snapshot.getVersion());
    final Method stateMethod = snapshot.getClass().getMethod("state", null);
    logger.info("method state", snapshot.getVersion());
    final State state = (State) stateMethod.invoke(snapshot);
    logger.info("state {}", state.protocol());
    final Protocol modelProtocol = new Protocol(state.protocol().minReaderVersion());
    final Metadata metadata =
        new Metadata(state.metadata().id(), state.metadata().name(), state.metadata().description(),
            new Format(), state.metadata().schemaString(), new ArrayList<String>(
                JavaConverters.asJavaCollection(state.metadata().partitionColumns())));


    final Stream<Wrapper> wrappers =
        Stream.of(Wrapper.builder().withProtocol(modelProtocol).build(),
            Wrapper.builder().withMetaData(metadata).build());

    // if (includeFiles) {
    // var addFiles = snapshot.allFiles().collectAsList();
    // if (snapshot.metadata().partitionColumns().nonEmpty()) {
    // final var newFiles = PartitionFilterUtils.evaluatePredicate(
    // snapshot.metadata().schemaString(), snapshot.metadata().partitionColumns(),
    // JavaConverters.collectionAsScalaIterableConverter(
    // Optional.ofNullable(predicateHits).orElse(List.of())).asScala().toSeq(),
    // JavaConverters.collectionAsScalaIterableConverter(addFiles).asScala().toSeq());
    // addFiles = JavaConverters.seqAsJavaList(newFiles);
    // }
    //
    // final var files = addFiles.stream()
    // .map(addFile -> File.builder()
    // .withId(Hashing.md5().hashString(addFile.path(), StandardCharsets.UTF_8).toString())
    // .withUrl(absolutePath(deltaLog.dataPath(), addFile.path()).toString())
    // .withSize(addFile.size())
    // .withPartitionValues(JavaConverters.asJavaCollection(addFile.partitionValues())
    // .stream().collect(Collectors.toMap(Tuple2::_1, Tuple2::_2)))
    // .withStats(addFile.stats()).build())
    // .map(file -> Wrapper.builder().withFile(file).build());


    return wrappers;
  }



}
