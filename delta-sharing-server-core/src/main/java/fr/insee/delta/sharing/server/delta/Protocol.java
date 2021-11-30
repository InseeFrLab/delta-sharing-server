package fr.insee.delta.sharing.server.delta;

import javax.annotation.Nonnull;

public class Protocol {

  @Nonnull
  int minReaderVersion;

  public Protocol(int minReaderVersion) {
    super();
    this.minReaderVersion = minReaderVersion;
  }

  public int getMinReaderVersion() {
    return minReaderVersion;
  }

  public void setMinReaderVersion(int minReaderVersion) {
    this.minReaderVersion = minReaderVersion;
  }


}
