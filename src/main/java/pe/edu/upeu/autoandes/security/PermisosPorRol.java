package pe.edu.upeu.autoandes.security;
import java.util.*; import static pe.edu.upeu.autoandes.security.Permiso.*;
public final class PermisosPorRol {
 private PermisosPorRol(){}
 private static final Map<String,Set<Permiso>> MATRIZ=Map.of("ADMIN",Set.of(Permiso.values()),"ASESOR",Set.of(SERVICIO_LEER,VEHICULO_LEER,VEHICULO_ESCRIBIR,ORDEN_LEER,ORDEN_REGISTRAR),"JEFE_TALLER",Set.of(SERVICIO_LEER,SERVICIO_ESCRIBIR,VEHICULO_LEER,ORDEN_LEER,ORDEN_ANULAR,REPORTE_VER));
 public static Set<String> permisos(Set<String> roles){Set<String> p=new TreeSet<>();roles.forEach(r->MATRIZ.getOrDefault(r,Set.of()).forEach(v->p.add(v.name())));return p;}
}
