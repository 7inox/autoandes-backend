package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import lombok.RequiredArgsConstructor; import pe.edu.upeu.autoandes.service.CatalogoService; import pe.edu.upeu.autoandes.dto.*;
 @RestController @RequestMapping("/api/v1/servicios") @RequiredArgsConstructor public class ServicioController {
 private final CatalogoService service;
 @GetMapping @PreAuthorize("hasAuthority('SERVICIO_LEER')") public java.util.List<ServicioResponseDTO> listar(){return service.servicios();}
 @GetMapping("/{id}") @PreAuthorize("hasAuthority('SERVICIO_LEER')") public ServicioResponseDTO obtener(@PathVariable Long id){return service.servicio(id);}
 @PostMapping @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')") public ResponseEntity<ServicioResponseDTO> crear(@jakarta.validation.Valid @RequestBody ServicioRequestDTO d){var creado=service.crearServicio(d);return ResponseEntity.created(java.net.URI.create("/api/v1/servicios/"+creado.id())).body(creado);}
 @PutMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')") public void editar(@PathVariable Long id,@jakarta.validation.Valid @RequestBody ServicioRequestDTO d){service.editarServicio(id,d);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')") public void eliminar(@PathVariable Long id){service.eliminarServicio(id);}
 @PatchMapping("/{id}/estado") @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')") public ServicioResponseDTO estado(@PathVariable Long id,@jakarta.validation.Valid @RequestBody EstadoRequestDTO d){return service.estadoServicio(id,d.activo());}
 }
