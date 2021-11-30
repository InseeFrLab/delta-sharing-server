package fr.insee.delta.sharing.server.delta;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;

public class DeltaShareTableLoader {

  private final Configuration hadoopConfiguration;
  private final long preSignedUrlTimeoutSeconds;
  private final Boolean evaluatePredicateHint;

  public DeltaShareTableLoader(Configuration hadoopConfiguration, Boolean evaluatePredicateHint,
      long preSignedUrlTimeoutSeconds) {
    this.evaluatePredicateHint = evaluatePredicateHint;
    this.preSignedUrlTimeoutSeconds = preSignedUrlTimeoutSeconds;
    this.hadoopConfiguration = hadoopConfiguration;
  }

  public DeltaShareTable loadTable(Table table) throws IOException {
    return new DeltaShareTable(this.preSignedUrlTimeoutSeconds, this.evaluatePredicateHint, table,
        hadoopConfiguration);
  }

}
