package pe.edu.upeu.autoandes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.CatalogoService;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {
  private final CatalogoService service;

  @GetMapping
  @PreAuthorize("hasAuthority('VEHICULO_LEER')")
  public java.util.List<ClienteResponseDTO> listar() {
    return service.clientes();
  }
}
