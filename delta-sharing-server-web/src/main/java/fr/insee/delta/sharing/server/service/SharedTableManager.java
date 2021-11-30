package fr.insee.delta.sharing.server.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import fr.insee.delta.sharing.server.exception.DeltaSharingNotFoundException;
import fr.insee.delta.sharing.server.exception.InvalidTokenException;
import fr.insee.delta.sharing.server.protocol.Protocol.Schema;
import fr.insee.delta.sharing.server.protocol.Protocol.Share;
import fr.insee.delta.sharing.server.protocol.Protocol.Table;
import io.delta.sharing.server.config.SchemaConfig;
import io.delta.sharing.server.config.ServerConfig;
import io.delta.sharing.server.config.ShareConfig;
import io.delta.sharing.server.config.TableConfig;

public class SharedTableManager {

  private final ServerConfig serverConfig;

  public SharedTableManager(ServerConfig sc) {
    this.serverConfig = sc;
  }

  public Pair<List<Share>, String> listShares(String pageToken, Integer maxResults)
      throws InvalidTokenException, UnsupportedEncodingException {
    final List<ShareConfig> shares = this.serverConfig.getShares();
    final int totalSize = shares.size();
    final Page page = PageTokenUtils.getPage(pageToken, null, null, maxResults, totalSize);
    final List<Share> res = shares.subList(page.getStart(), page.getEnd()).stream()
        .map(s -> Share.newBuilder().setName(s.getName()).build()).collect(Collectors.toList());
    final Pair<List<Share>, String> result =
        new Pair<>(res, PageTokenUtils.encodePageToken(page.getNextId(), null, null));
    return result;
  }

  public Pair<List<Schema>, String> listSchemas(String share, String pageToken, Integer maxResults)
      throws DeltaSharingNotFoundException, InvalidTokenException, UnsupportedEncodingException {
    final ShareConfig shareConfig = getShare(this.serverConfig, share);
    final int totalSize = shareConfig.getSchemas().size();
    final Page page = PageTokenUtils.getPage(pageToken, share, null, maxResults, totalSize);
    final List<Schema> res = shareConfig.getSchemas().subList(page.getStart(), page.getEnd())
        .stream().map(s -> Schema.newBuilder().setName(s.getName()).build())
        .collect(Collectors.toList());
    final Pair<List<Schema>, String> result =
        new Pair<>(res, PageTokenUtils.encodePageToken(page.getNextId(), null, null));
    return result;
  }

  public Pair<List<Table>, String> listTables(String share, String schema, String pageToken,
      Integer maxResults)
      throws DeltaSharingNotFoundException, InvalidTokenException, UnsupportedEncodingException {
    final SchemaConfig schemaConfig = getSchema(this.serverConfig, share, schema);
    final int totalSize = schemaConfig.getTables().size();
    final Page page = PageTokenUtils.getPage(pageToken, share, schema, maxResults, totalSize);
    final List<Table> res =
        schemaConfig.getTables().subList(page.getStart(), page.getEnd()).stream()
            .map(s -> Table.newBuilder().setName(s.getName()).build()).collect(Collectors.toList());
    final Pair<List<Table>, String> result =
        new Pair<>(res, PageTokenUtils.encodePageToken(page.getNextId(), null, null));
    return result;
  }

  public TableConfig getTable(String share, String schema, String table)
      throws DeltaSharingNotFoundException {
    return getSchema(serverConfig, share, schema).getTables().stream()
        .filter(u -> u.getName().toLowerCase().contains(table.toLowerCase())).findFirst()
        .orElseThrow(() -> new DeltaSharingNotFoundException("No table " + table));
  }

  private ShareConfig getShare(ServerConfig serverConfig, String share)
      throws DeltaSharingNotFoundException {
    final ShareConfig shareConfig = serverConfig.getShares().stream()
        .filter(u -> u.getName().toLowerCase().contains(share.toLowerCase())).findFirst()
        .orElseThrow(() -> new DeltaSharingNotFoundException("No share " + share));
    return shareConfig;
  }

  private SchemaConfig getSchema(ServerConfig serverConfig, String share, String schema)
      throws DeltaSharingNotFoundException {
    final SchemaConfig schemaConfig = getShare(serverConfig, share).getSchemas().stream()
        .filter(u -> u.getName().toLowerCase().contains(schema.toLowerCase())).findFirst()
        .orElseThrow(() -> new DeltaSharingNotFoundException("No schema " + schema));
    return schemaConfig;
  }

}
