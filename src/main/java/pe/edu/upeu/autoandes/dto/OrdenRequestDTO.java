package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;

public record OrdenRequestDTO(
    @NotNull Long vehiculoId,
    @NotNull @Min(0) @Max(9999999) Integer kilometraje,
    @NotNull @Size(min = 1, max = 10)
        java.util.List<@jakarta.validation.Valid @NotNull DetalleRequestDTO> detalles) {}
