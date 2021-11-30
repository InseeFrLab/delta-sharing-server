package fr.insee.delta.sharing.server.delta;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class File {

  @Nonnull
  String url;
  @Nonnull
  String id;
  @Nonnull
  Map<String, String> partitionValues;
  @Nonnull
  long size;
  @Nullable
  String stats;

  public File(String url, String id, Map<String, String> partitionValues, long size, String stats) {
    super();
    this.url = url;
    this.id = id;
    this.partitionValues = partitionValues;
    this.size = size;
    this.stats = stats;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, String> getPartitionValues() {
    return partitionValues;
  }

  public void setPartitionValues(Map<String, String> partitionValues) {
    this.partitionValues = partitionValues;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getStats() {
    return stats;
  }

  public void setStats(String stats) {
    this.stats = stats;
  }

}
