package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;

public record DetalleResponseDTO(
    Long id,
    Long servicioId,
    String servicioCodigo,
    String servicioNombre,
    Integer cantidad,
    java.math.BigDecimal precioUnitario,
    java.math.BigDecimal subtotal) {}
