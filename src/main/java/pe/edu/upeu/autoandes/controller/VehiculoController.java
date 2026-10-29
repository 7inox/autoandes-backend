package pe.edu.upeu.autoandes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.CatalogoService;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {
  private final CatalogoService service;

  @GetMapping
  @PreAuthorize("hasAuthority('VEHICULO_LEER')")
  public java.util.List<VehiculoResponseDTO> listar(
      @RequestParam(required = false) Long clienteId) {
    return service.vehiculos(clienteId);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('VEHICULO_LEER')")
  public VehiculoResponseDTO obtener(@PathVariable Long id) {
    return service.vehiculo(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('VEHICULO_ESCRIBIR')")
  public ResponseEntity<VehiculoResponseDTO> crear(
      @jakarta.validation.Valid @RequestBody VehiculoRequestDTO d) {
    var creado = service.crearVehiculo(d);
    return ResponseEntity.created(java.net.URI.create("/api/v1/vehiculos/" + creado.id()))
        .body(creado);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('VEHICULO_ESCRIBIR')")
  public void editar(
      @PathVariable Long id, @jakarta.validation.Valid @RequestBody VehiculoRequestDTO d) {
    service.editarVehiculo(id, d);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('VEHICULO_ESCRIBIR')")
  public void eliminar(@PathVariable Long id) {
    service.eliminarVehiculo(id);
  }
}
