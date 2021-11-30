package fr.insee.delta.sharing.server.delta;

import javax.annotation.Nullable;

public class Wrapper {

  @Nullable
  Protocol protocol;
  @Nullable
  Metadata metaData;
  @Nullable
  File file;

  private Wrapper(Builder builder) {
    this.protocol = builder.protocol;
    this.metaData = builder.metaData;
    this.file = builder.file;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Protocol protocol;
    private Metadata metaData;
    private File file;

    private Builder() {}

    public Builder withProtocol(Protocol protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder withMetaData(Metadata metaData) {
      this.metaData = metaData;
      return this;
    }

    public Builder withFile(File file) {
      this.file = file;
      return this;
    }

    public Wrapper build() {
      return new Wrapper(this);
    }
  }

}
