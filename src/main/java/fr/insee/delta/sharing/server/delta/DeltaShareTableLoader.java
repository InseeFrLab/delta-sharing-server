package fr.insee.delta.sharing.server.delta;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import io.delta.sharing.server.config.ServerConfig;
import io.delta.sharing.server.config.TableConfig;

public class DeltaShareTableLoader {

  private final ServerConfig serverConfig;
  private final Configuration hadoopConfiguration;

  public DeltaShareTableLoader(ServerConfig sc, Configuration hadoopConfiguration) {
    this.serverConfig = sc;
    this.hadoopConfiguration = hadoopConfiguration;
  }

  public DeltaShareTable loadTable(TableConfig tableConfig) throws IOException {
    return new DeltaShareTable(this.serverConfig.getPreSignedUrlTimeoutSeconds(),
        this.serverConfig.getEvaluatePredicateHints(), tableConfig, hadoopConfiguration);
  }

}
