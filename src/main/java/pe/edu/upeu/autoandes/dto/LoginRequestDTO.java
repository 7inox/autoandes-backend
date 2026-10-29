package pe.edu.upeu.autoandes.dto;
import jakarta.validation.constraints.*;
public record LoginRequestDTO(@NotBlank String username,@NotBlank String password) {}
