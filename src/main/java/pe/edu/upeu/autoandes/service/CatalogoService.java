package pe.edu.upeu.autoandes.service;

import java.util.List;
import pe.edu.upeu.autoandes.dto.*;

public interface CatalogoService {
  List<ClienteResponseDTO> clientes();

  List<ServicioResponseDTO> servicios();

  ServicioResponseDTO servicio(Long id);

  ServicioResponseDTO crearServicio(ServicioRequestDTO d);

  void editarServicio(Long id, ServicioRequestDTO d);

  void eliminarServicio(Long id);

  ServicioResponseDTO estadoServicio(Long id, boolean activo);

  List<VehiculoResponseDTO> vehiculos(Long clienteId);

  VehiculoResponseDTO vehiculo(Long id);

  VehiculoResponseDTO crearVehiculo(VehiculoRequestDTO d);

  void editarVehiculo(Long id, VehiculoRequestDTO d);

  void eliminarVehiculo(Long id);
}
