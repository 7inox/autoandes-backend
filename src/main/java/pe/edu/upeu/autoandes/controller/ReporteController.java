package pe.edu.upeu.autoandes.controller;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.IngresoProjection;
import pe.edu.upeu.autoandes.service.OrdenService;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {
  private final OrdenService service;

  @GetMapping("/ingresos-por-servicio")
  @PreAuthorize("hasAuthority('REPORTE_VER')")
  public java.util.List<IngresoProjection> ingresos(
      @RequestParam(required = false) LocalDate desde,
      @RequestParam(required = false) LocalDate hasta) {
    return service.ingresos(desde, hasta);
  }
}
