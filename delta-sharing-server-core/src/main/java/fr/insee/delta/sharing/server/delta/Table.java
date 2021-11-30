package fr.insee.delta.sharing.server.delta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Table {

  @Nonnull
  String name;
  @Nullable
  String location;

  public Table(String name, String location) {
    super();
    this.name = name;
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

}
