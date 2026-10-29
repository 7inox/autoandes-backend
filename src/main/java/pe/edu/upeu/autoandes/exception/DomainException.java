package pe.edu.upeu.autoandes.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
  private final int status;

  public DomainException(int status, String message) {
    super(message);
    this.status = status;
  }

  public static DomainException missing(String recurso) {
    return new DomainException(404, recurso + " no encontrado");
  }

  public static DomainException conflict(String mensaje) {
    return new DomainException(409, mensaje);
  }
}
