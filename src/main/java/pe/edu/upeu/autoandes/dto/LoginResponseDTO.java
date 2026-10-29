package pe.edu.upeu.autoandes.dto;
import jakarta.validation.constraints.*;
public record LoginResponseDTO(String token,String tipo,long expiraEnSegundos,String nombreCompleto,java.util.Set<String> roles,java.util.Set<String> permisos) {}
