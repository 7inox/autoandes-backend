package pe.edu.upeu.autoandes.dto;
import jakarta.validation.constraints.*;
public record ClienteResponseDTO(Long id,String documento,String nombres,String telefono) {}
