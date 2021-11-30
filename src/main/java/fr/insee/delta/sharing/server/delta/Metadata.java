package fr.insee.delta.sharing.server.delta;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Metadata {

  @Nonnull
  String id;
  @Nullable
  String name;
  @Nullable
  String description;
  @Nonnull
  Format format;
  @Nonnull
  String schemaString;
  @Nonnull
  List<String> partitionColumns;

  public Metadata(String id, String name, String description, Format format, String schemaString,
      List<String> partitionColumns) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.format = format;
    this.schemaString = schemaString;
    this.partitionColumns = partitionColumns;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Format getFormat() {
    return format;
  }

  public void setFormat(Format format) {
    this.format = format;
  }

  public String getSchemaString() {
    return schemaString;
  }

  public void setSchemaString(String schemaString) {
    this.schemaString = schemaString;
  }

  public List<String> getPartitionColumns() {
    return partitionColumns;
  }

  public void setPartitionColumns(List<String> partitionColumns) {
    this.partitionColumns = partitionColumns;
  }


}
