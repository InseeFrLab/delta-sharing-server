package fr.insee.delta.sharing.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import fr.insee.delta.sharing.server.delta.DeltaShareTable;
import fr.insee.delta.sharing.server.delta.DeltaShareTableLoader;
import fr.insee.delta.sharing.server.delta.Wrapper;
import fr.insee.delta.sharing.server.exception.DeltaSharingNotFoundException;
import fr.insee.delta.sharing.server.exception.InvalidTokenException;
import fr.insee.delta.sharing.server.protocol.Protocol.ListSchemasResponse;
import fr.insee.delta.sharing.server.protocol.Protocol.ListSharesResponse;
import fr.insee.delta.sharing.server.protocol.Protocol.ListTablesResponse;
import fr.insee.delta.sharing.server.protocol.Protocol.Schema;
import fr.insee.delta.sharing.server.protocol.Protocol.Share;
import fr.insee.delta.sharing.server.protocol.Protocol.Table;
import fr.insee.delta.sharing.server.service.SharedTableManager;
import io.delta.sharing.server.config.TableConfig;

@RestController
public class SharingController {

  Logger logger = LoggerFactory.getLogger(DeltaShareTable.class);

  public static final String DELTA_TABLE_VERSION = "Delta-Table-Version";
  private static final ObjectMapper OBJECT_MAPPER_DNJSON;

  static {
    OBJECT_MAPPER_DNJSON = new ObjectMapper();
    OBJECT_MAPPER_DNJSON.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    OBJECT_MAPPER_DNJSON.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private final SharedTableManager sharedTableManager;
  private final DeltaShareTableLoader deltaShareTableLoader;

  public SharingController(SharedTableManager sharedTableManager,
      DeltaShareTableLoader deltaShareTableLoader) {
    super();
    this.sharedTableManager = sharedTableManager;
    this.deltaShareTableLoader = deltaShareTableLoader;
  }

  @GetMapping(path = "/shares", produces = "application/json")
  public ListSharesResponse getShares(
      @RequestParam(value = "maxResults", defaultValue = "500") Integer maxResults,
      @RequestParam(value = "pageToken") @Nullable String pageToken)
      throws UnsupportedEncodingException, InvalidTokenException {

    final Pair<List<Share>, String> data = sharedTableManager.listShares(pageToken, maxResults);
    // we could check how to handle null value in protobuf by configuration
    final ListSharesResponse.Builder b = ListSharesResponse.newBuilder();
    if (data.getValue0() != null) {
      b.addAllItems(data.getValue0());
    }
    if (data.getValue1() != null) {
      b.setNextPageToken(data.getValue1());
    }
    return b.build();
  }

  @GetMapping(path = "/shares/{share}/schemas", produces = "application/json")
  ListSchemasResponse getSchemas(@PathVariable("share") String shareName,
      @RequestParam(value = "maxResults", defaultValue = "500") Integer maxResults,
      @RequestParam(value = "pageToken") @Nullable String pageToken)
      throws UnsupportedEncodingException, DeltaSharingNotFoundException, InvalidTokenException {

    final Pair<List<Schema>, String> data =
        sharedTableManager.listSchemas(shareName, pageToken, maxResults);
    final ListSchemasResponse.Builder b = ListSchemasResponse.newBuilder();
    if (data.getValue0() != null) {
      b.addAllItems(data.getValue0());
    }
    if (data.getValue1() != null) {
      b.setNextPageToken(data.getValue1());
    }
    return b.build();
  }

  @GetMapping(path = "/shares/{share}/schemas/{schema}/tables", produces = "application/json")
  ListTablesResponse getSchemas(@PathVariable("share") String shareName,
      @PathVariable("schema") String schemaName,
      @RequestParam(value = "maxResults", defaultValue = "500") Integer maxResults,
      @RequestParam(value = "pageToken") @Nullable String pageToken)
      throws UnsupportedEncodingException, DeltaSharingNotFoundException, InvalidTokenException {

    final Pair<List<Table>, String> data =
        sharedTableManager.listTables(shareName, schemaName, pageToken, maxResults);
    final ListTablesResponse.Builder b = ListTablesResponse.newBuilder();
    if (data.getValue0() != null) {
      b.addAllItems(data.getValue0());
    }
    if (data.getValue1() != null) {
      b.setNextPageToken(data.getValue1());
    }
    return b.build();
  }

  @RequestMapping(method = RequestMethod.HEAD,
      path = "/shares/{share}/schemas/{schema}/tables/{table}")
  public ResponseEntity<String> getTableVersion(@PathVariable("share") String shareName,
      @PathVariable("schema") String schemaName, @PathVariable("table") String tableName)
      throws DeltaSharingNotFoundException, IOException {

    final TableConfig tableConfig = sharedTableManager.getTable(shareName, schemaName, tableName);
    final DeltaShareTable table = this.deltaShareTableLoader.loadTable(tableConfig);

    return ResponseEntity.ok().header(DELTA_TABLE_VERSION, Long.toString(table.getTableVersion()))
        .build();
  }

  @GetMapping(value = "/shares/{share}/schemas/{schema}/tables/{table}/metadata",
      produces = MediaType.APPLICATION_NDJSON_VALUE)
  public ResponseEntity<ResponseBodyEmitter> getTableMetadata(
      @PathVariable("share") String shareName, @PathVariable("schema") String schemaName,
      @PathVariable("table") String tableName)
      throws DeltaSharingNotFoundException, IOException, NoSuchMethodException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    logger.info("metadata endpoint");
    final TableConfig tableConfig = sharedTableManager.getTable(shareName, schemaName, tableName);
    logger.info("tableConfig {}", tableConfig.getName());
    final DeltaShareTable table = this.deltaShareTableLoader.loadTable(tableConfig);
    logger.info("delta table loaded!", tableConfig.getName());
    final Stream<Wrapper> wrappers = table.query(false, null, null);
    final ResponseBodyEmitter responseBodyEmitter = new ResponseBodyEmitter();
    try {
      for (final var wrapper : wrappers.collect(Collectors.toList())) {
        responseBodyEmitter.send(OBJECT_MAPPER_DNJSON.writeValueAsString(wrapper));
        responseBodyEmitter.send("\n");
      }
    } finally {
      responseBodyEmitter.complete();
    }

    return ResponseEntity.ok().header(DELTA_TABLE_VERSION, Long.toString(table.getTableVersion()))
        .contentType(MediaType.APPLICATION_NDJSON).body(responseBodyEmitter);
  }



}
