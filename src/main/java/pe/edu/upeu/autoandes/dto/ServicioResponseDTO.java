package pe.edu.upeu.autoandes.dto;
import jakarta.validation.constraints.*;
public record ServicioResponseDTO(Long id,String codigo,String nombre,java.math.BigDecimal precio,Integer duracionMin,Boolean activo) {}
