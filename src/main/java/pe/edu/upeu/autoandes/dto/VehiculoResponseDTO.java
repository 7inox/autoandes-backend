package pe.edu.upeu.autoandes.dto;
import jakarta.validation.constraints.*;
public record VehiculoResponseDTO(Long id,String placa,String marca,String modelo,Integer anio,Long clienteId,String clienteNombre) {}
