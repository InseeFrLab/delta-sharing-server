package fr.insee.delta.sharing.server.delta;

import java.io.IOException;
import io.delta.sharing.server.config.ServerConfig;
import io.delta.sharing.server.config.TableConfig;

public class DeltaShareTableLoader {

  private final ServerConfig serverConfig;

  public DeltaShareTableLoader(ServerConfig sc) {
    this.serverConfig = sc;
  }

  public DeltaShareTable loadTable(TableConfig tableConfig) throws IOException {
    return new DeltaShareTable(this.serverConfig.getPreSignedUrlTimeoutSeconds(),
        this.serverConfig.getEvaluatePredicateHints(), tableConfig);
  }

}
