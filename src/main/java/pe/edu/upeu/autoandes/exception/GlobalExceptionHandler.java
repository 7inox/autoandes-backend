package pe.edu.upeu.autoandes.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  public static ErrorResponseDTO error(
      int status, String mensaje, String ruta, Map<String, String> campos) {
    return new ErrorResponseDTO(
        LocalDateTime.now(),
        status,
        HttpStatus.valueOf(status).getReasonPhrase(),
        mensaje,
        ruta,
        campos);
  }

  @ExceptionHandler(DomainException.class)
  ResponseEntity<ErrorResponseDTO> domain(DomainException e, HttpServletRequest r) {
    log.warn("Regla rechazada {} {}: {}", r.getMethod(), r.getRequestURI(), e.getMessage());
    return ResponseEntity.status(e.getStatus())
        .body(error(e.getStatus(), e.getMessage(), r.getRequestURI(), null));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorResponseDTO> validation(
      MethodArgumentNotValidException e, HttpServletRequest r) {
    Map<String, String> campos = new LinkedHashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
    return ResponseEntity.badRequest()
        .body(error(400, "Datos inválidos", r.getRequestURI(), campos));
  }

  @ExceptionHandler({
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
    org.springframework.web.bind.MissingServletRequestParameterException.class
  })
  ResponseEntity<ErrorResponseDTO> malformed(Exception e, HttpServletRequest r) {
    return ResponseEntity.badRequest()
        .body(error(400, "Formato de solicitud inválido", r.getRequestURI(), null));
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  ResponseEntity<ErrorResponseDTO> integrity(Exception e, HttpServletRequest r) {
    log.warn("Conflicto de integridad en {}", r.getRequestURI());
    return ResponseEntity.status(409)
        .body(
            error(409, "El registro está duplicado o tiene referencias", r.getRequestURI(), null));
  }

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  ResponseEntity<ErrorResponseDTO> denied(Exception e, HttpServletRequest r) {
    return ResponseEntity.status(403)
        .body(error(403, "No tiene permiso para esta operación", r.getRequestURI(), null));
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  ResponseEntity<ErrorResponseDTO> notFound(Exception e, HttpServletRequest r) {
    return ResponseEntity.status(404)
        .body(error(404, "Ruta no encontrada", r.getRequestURI(), null));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponseDTO> unexpected(Exception e, HttpServletRequest r) {
    log.error("Error inesperado en {}", r.getRequestURI(), e);
    return ResponseEntity.internalServerError()
        .body(error(500, "Error interno del servidor", r.getRequestURI(), null));
  }
}
