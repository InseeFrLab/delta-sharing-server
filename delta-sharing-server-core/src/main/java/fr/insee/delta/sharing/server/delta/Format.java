package fr.insee.delta.sharing.server.delta;

import javax.annotation.Nonnull;

public class Format {

  enum Provider {
    parquet
  }

  @Nonnull
  Provider provider = Provider.parquet;

  public Format() {
    super();
  }

  public Provider getProvider() {
    return provider;
  }

  public void setProvider(Provider provider) {
    this.provider = provider;
  }


}
