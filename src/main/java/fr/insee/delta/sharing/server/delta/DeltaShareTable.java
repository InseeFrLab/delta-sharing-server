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
import io.delta.sharing.server.config.TableConfig;
import io.delta.standalone.DeltaLog;
import io.delta.standalone.Snapshot;
import io.delta.standalone.internal.SnapshotImpl.State;
import scala.collection.JavaConverters;

public class DeltaShareTable {

  private final long preSignedUrlTimeoutSeconds;
  private final Boolean evaluatePredicateHint;
  private final Configuration hadoopConfiguration;
  private final TableConfig tableConfig;
  private final DeltaLog deltaLog;


  public DeltaShareTable(long l, Boolean evaluatePredicateHint, TableConfig tableConfig)
      throws IOException {
    super();
    this.preSignedUrlTimeoutSeconds = l;
    this.evaluatePredicateHint = evaluatePredicateHint;
    this.hadoopConfiguration = new Configuration();
    this.tableConfig = tableConfig;
    this.deltaLog = deltaLog();
  }

  private DeltaLog deltaLog() throws IOException {
    final Path tablePath = new Path(tableConfig.getLocation());
    final FileSystem fs = tablePath.getFileSystem(this.hadoopConfiguration);
    if (!(fs instanceof S3AFileSystem)) {
      throw new IllegalStateException("Cannot share tables on non S3 file systems");
    }
    return DeltaLog.forTable(this.hadoopConfiguration, tablePath);
  }

  public Long getTableVersion() {
    final Snapshot snapshot = this.deltaLog.snapshot();
    validateDeltaTable(snapshot);
    return snapshot.getVersion();
  }


  private void validateDeltaTable(final Snapshot snapshot) {
    if (snapshot.getVersion() < 0) {
      throw new IllegalStateException("version not valid for " + this.tableConfig.getName());
    }
  }

  public Stream<Wrapper> query(boolean includeFiles, @Nullable List<String> predicateHits,
      Integer limitHint) throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    final Snapshot snapshot = deltaLog.snapshot();
    final Method stateMethod = snapshot.getClass().getMethod("state", null);
    final State state = (State) stateMethod.invoke(snapshot);
    final Protocol modelProtocol = new Protocol(state.protocol().minReaderVersion());
    final Metadata metadata = new Metadata(state.metadata().id(), state.metadata().name(),
        state.metadata().description(), new Format(), state.metadata().schemaString(),
        new ArrayList<>(JavaConverters.asJavaCollection(state.metadata().partitionColumns())));


    final var wrappers = Stream.of(Wrapper.builder().withProtocol(modelProtocol).build(),
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
