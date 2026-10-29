package pe.edu.upeu.autoandes.dto;
import jakarta.validation.constraints.*;
public record OrdenResponseDTO(Long id,String numero,java.time.LocalDateTime fecha,Long vehiculoId,String placa,String clienteNombre,Integer kilometraje,String estado,java.math.BigDecimal total,String usuarioRegistro,String motivoAnulacion,java.util.List<DetalleResponseDTO> detalles) {}
