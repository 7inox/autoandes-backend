package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import org.springframework.security.access.prepost.PreAuthorize; import lombok.RequiredArgsConstructor; import pe.edu.upeu.autoandes.service.CatalogoService; import pe.edu.upeu.autoandes.dto.*;
@RestController @RequestMapping("/api/v1/clientes") @RequiredArgsConstructor public class ClienteController {private final CatalogoService service; @GetMapping @PreAuthorize("hasAuthority('VEHICULO_LEER')") public java.util.List<ClienteResponseDTO> listar(){return service.clientes();}}
