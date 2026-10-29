package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import org.springframework.security.core.Authentication; import lombok.RequiredArgsConstructor; import pe.edu.upeu.autoandes.service.AuthService; import pe.edu.upeu.autoandes.dto.*;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor public class AuthController {private final AuthService service;
 @PostMapping("/login") public LoginResponseDTO login(@jakarta.validation.Valid @RequestBody LoginRequestDTO d){return service.login(d);}
 @GetMapping("/me") public UsuarioResponseDTO me(Authentication auth){return service.me(auth.getName());}}
