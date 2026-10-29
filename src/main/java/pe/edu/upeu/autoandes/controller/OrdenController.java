package pe.edu.upeu.autoandes.controller;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.OrdenService;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
public class OrdenController {
  private final OrdenService service;

  @PostMapping
  @PreAuthorize("hasAuthority('ORDEN_REGISTRAR')")
  public ResponseEntity<OrdenResponseDTO> registrar(
      @jakarta.validation.Valid @RequestBody OrdenRequestDTO d, Authentication auth) {
    var o = service.registrar(d, auth.getName());
    return ResponseEntity.created(java.net.URI.create("/api/v1/ordenes/" + o.id())).body(o);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ORDEN_LEER')")
  public java.util.List<OrdenResponseDTO> listar(
      @RequestParam(required = false) String placa,
      @RequestParam(required = false) String estado,
      @RequestParam(required = false) LocalDate desde,
      @RequestParam(required = false) LocalDate hasta) {
    return service.listar(placa, estado, desde, hasta);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('ORDEN_LEER')")
  public OrdenResponseDTO obtener(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PatchMapping("/{id}/anular")
  @PreAuthorize("hasAuthority('ORDEN_ANULAR')")
  public OrdenResponseDTO anular(
      @PathVariable Long id, @jakarta.validation.Valid @RequestBody AnularRequestDTO d) {
    return service.anular(id, d.motivo());
  }
}
