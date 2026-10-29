package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import org.springframework.security.access.prepost.PreAuthorize; import lombok.RequiredArgsConstructor; import pe.edu.upeu.autoandes.service.OrdenService; import pe.edu.upeu.autoandes.dto.IngresoProjection; import java.time.LocalDate;
@RestController @RequestMapping("/api/v1/reportes") @RequiredArgsConstructor public class ReporteController {private final OrdenService service;
 @GetMapping("/ingresos-por-servicio") @PreAuthorize("hasAuthority('REPORTE_VER')") public java.util.List<IngresoProjection> ingresos(@RequestParam(required=false) LocalDate desde,@RequestParam(required=false) LocalDate hasta){return service.ingresos(desde,hasta);}}
