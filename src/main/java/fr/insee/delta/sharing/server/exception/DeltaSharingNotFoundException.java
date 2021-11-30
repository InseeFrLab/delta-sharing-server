package fr.insee.delta.sharing.server.exception;

public class DeltaSharingNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public DeltaSharingNotFoundException(String errorMessage) {
    super(errorMessage);
  }

}
