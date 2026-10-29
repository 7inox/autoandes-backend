package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;

public record UsuarioResponseDTO(
    String username,
    String nombreCompleto,
    java.util.Set<String> roles,
    java.util.Set<String> permisos) {}
