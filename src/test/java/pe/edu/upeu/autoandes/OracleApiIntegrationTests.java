package pe.edu.upeu.autoandes;

import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.*;
import static org.junit.jupiter.api.Assertions.*;

/** Real HTTP -> security -> service transaction -> existing Oracle schema. */
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class OracleApiIntegrationTests {
 @Value("${local.server.port}") int port;
 @Autowired ObjectMapper mapper;
 @Autowired JdbcTemplate jdbc;
 final HttpClient http=HttpClient.newHttpClient();
 final List<Map<String,Object>> evidence=new ArrayList<>();
 final List<Long> serviceIds=new ArrayList<>(),vehicleIds=new ArrayList<>(),orderIds=new ArrayList<>();
 String admin,asesor,jefe;
 JsonNode call(String label,String method,String path,Object body,String token,int expected)throws Exception{
  var req=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/v1"+path));
  req.header("Content-Type","application/json");if(token!=null)req.header("Authorization","Bearer "+token);
  req.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
  var response=http.send(req.build(),HttpResponse.BodyHandlers.ofString());
  JsonNode json=response.body().isBlank()?mapper.createObjectNode():mapper.readTree(response.body());
  var safe=json.deepCopy();if(safe.isObject()){((tools.jackson.databind.node.ObjectNode)safe).remove("token");}
  evidence.add(Map.of("caso",label,"metodo",method,"ruta",path,"esperado",expected,"observado",response.statusCode(),"respuesta",safe));
  assertEquals(expected,response.statusCode(),label+": "+safe);
  if(expected>=400){assertTrue(json.has("timestamp"));assertEquals(expected,json.get("status").asInt());assertTrue(json.has("mensaje"));assertTrue(json.has("ruta"));assertTrue(json.has("error"));}
  return json;
 }
 Map<String,Object> servicio(String code,String name){return Map.of("codigo",code,"nombre",name,"precio",37.50,"duracionMin",30);}
 Map<String,Object> vehiculo(String placa,long cliente){return Map.of("placa",placa,"marca","PruebaLP2","modelo","Integracion","anio",2026,"clienteId",cliente);}
 Map<String,Object> orden(long v,int km,List<Map<String,Object>> detalles){return Map.of("vehiculoId",v,"kilometraje",km,"detalles",detalles);}
 Map<String,Object> linea(long s,int cantidad){return Map.of("servicioId",s,"cantidad",cantidad);}
 String login(String user)throws Exception{return call("Login "+user,"POST","/auth/login",Map.of("username",user,"password","Auto2026*"),null,200).get("token").asText();}
 int count(String tabla){return jdbc.queryForObject("select count(*) from "+tabla,Integer.class);}

 @Test void contratoCompletoContraOracle()throws Exception{
  admin=login("admin");asesor=login("asesor");jefe=login("jefe");
  call("Cuenta inactiva","POST","/auth/login",Map.of("username","inactivo","password","Auto2026*"),null,401);
  call("Credencial incorrecta","POST","/auth/login",Map.of("username","admin","password","incorrecta"),null,401);
  call("Sin token","GET","/servicios",null,null,401);
  call("Token alterado","GET","/servicios",null,"x"+admin,401);
  call("Usuario actual","GET","/auth/me",null,admin,200);
  call("Asesor sin reporte","GET","/reportes/ingresos-por-servicio",null,asesor,403);
  call("Asesor sin anulacion","PATCH","/ordenes/1/anular",Map.of("motivo","Prueba de permiso denegado"),asesor,403);
  call("Jefe sin registro","POST","/ordenes",orden(1,50000,List.of(linea(1,1))),jefe,403);
  long client=call("Clientes Oracle","GET","/clientes",null,admin,200).get(0).get("id").asLong();
  var s=call("Servicio crear","POST","/servicios",servicio("SRV-991","Prueba integración A"),admin,201);long sid=s.get("id").asLong();serviceIds.add(sid);
  var s2=call("Segundo servicio","POST","/servicios",servicio("SRV-992","Prueba integración B"),admin,201);long sid2=s2.get("id").asLong();serviceIds.add(sid2);
  call("Servicio consultar","GET","/servicios/"+sid,null,admin,200);
  call("Servicio editar","PUT","/servicios/"+sid,servicio("SRV-991","Prueba integración editada"),admin,204);
  call("Codigo duplicado","POST","/servicios",servicio("SRV-991","Código duplicado"),admin,409);
  call("Formato invalido","POST","/servicios",servicio("INCORRECTO","ab"),admin,400);
  call("Recurso inexistente","GET","/servicios/99999999",null,admin,404);
  long vid=call("Vehiculo crear","POST","/vehiculos",vehiculo("ZZT-991",client),asesor,201).get("id").asLong();vehicleIds.add(vid);
  call("Vehiculo editar","PUT","/vehiculos/"+vid,vehiculo("ZZT-991",client),asesor,204);
  var vr=call("Vehiculo consultar","GET","/vehiculos/"+vid,null,admin,200);assertEquals(client,vr.get("clienteId").asLong());assertTrue(vr.has("clienteNombre"));assertFalse(vr.has("cliente"));
  call("Placa duplicada","POST","/vehiculos",vehiculo("ZZT-991",client),asesor,409);
  call("Cliente inexistente","POST","/vehiculos",vehiculo("ZZT-992",99999999),asesor,404);
  call("Vehiculo formato invalido","POST","/vehiculos",vehiculo("INVALIDO",client),asesor,400);
  call("Filtro cliente","GET","/vehiculos?clienteId="+client,null,jefe,200);
  long inactive=jdbc.queryForObject("select id from servicio where codigo='SRV-008'",Long.class);
  int beforeO=count("ORDEN_SERVICIO"),beforeD=count("DETALLE_ORDEN");
  call("Rollback tercer detalle inactivo","POST","/ordenes",orden(vid,100,List.of(linea(sid,1),linea(sid2,2),linea(inactive,1))),asesor,409);
  assertEquals(beforeO,count("ORDEN_SERVICIO"));assertEquals(beforeD,count("DETALLE_ORDEN"));
  evidence.add(Map.of("caso","Rollback SQL antes/despues","cabecerasAntes",beforeO,"cabecerasDespues",count("ORDEN_SERVICIO"),"detallesAntes",beforeD,"detallesDespues",count("DETALLE_ORDEN")));
  call("Servicios repetidos","POST","/ordenes",orden(vid,100,List.of(linea(sid,1),linea(sid,2))),asesor,400);
  call("Cantidad fuera de rango","POST","/ordenes",orden(vid,100,List.of(linea(sid,6))),asesor,400);
  var o=call("Orden correcta dos detalles","POST","/ordenes",orden(vid,100,List.of(linea(sid,1),linea(sid2,2))),asesor,201);long oid=o.get("id").asLong();orderIds.add(oid);
  assertEquals(0,new BigDecimal("112.50").compareTo(o.get("total").decimalValue()));assertEquals(2,o.get("detalles").size());
  call("Kilometraje inferior","POST","/ordenes",orden(vid,99,List.of(linea(sid,1))),asesor,409);
  call("Servicio usado no eliminable","DELETE","/servicios/"+sid,null,jefe,409);
  call("Vehiculo con orden no eliminable","DELETE","/vehiculos/"+vid,null,asesor,409);
  call("Servicio baja logica","PATCH","/servicios/"+sid+"/estado",Map.of("activo",false),jefe,200);
  call("Servicio inactivo","POST","/ordenes",orden(vid,100,List.of(linea(sid,1))),asesor,409);
  var report=call("Reporte antes de anular","GET","/reportes/ingresos-por-servicio",null,jefe,200);assertTrue(report.toString().contains("SRV-991"));
  call("Motivo corto","PATCH","/ordenes/"+oid+"/anular",Map.of("motivo","corto"),jefe,400);
  jdbc.update("update orden_servicio set fecha=systimestamp-interval '1' day where id=?",oid);
  call("Anulacion fuera del dia","PATCH","/ordenes/"+oid+"/anular",Map.of("motivo","Prueba fuera del día permitido"),jefe,409);
  jdbc.update("update orden_servicio set fecha=systimestamp where id=?",oid);
  call("Anulacion valida","PATCH","/ordenes/"+oid+"/anular",Map.of("motivo","Prueba controlada de integración"),jefe,200);
  call("Doble anulacion","PATCH","/ordenes/"+oid+"/anular",Map.of("motivo","Prueba de segunda anulación"),jefe,409);
  report=call("Reporte excluye anuladas","GET","/reportes/ingresos-por-servicio",null,jefe,200);assertFalse(report.toString().contains("SRV-991"));assertFalse(report.toString().contains("SRV-992"));
  call("Filtros combinados","GET","/ordenes?placa=ZZT-991&estado=ANULADA&desde=2026-01-01&hasta=2027-12-31",null,jefe,200);
  call("Consulta sin resultados","GET","/ordenes?placa=ZZZ-000",null,asesor,200);
  call("Rango fechas invalido","GET","/ordenes?desde=2026-12-31&hasta=2026-01-01",null,admin,400);
  long clean=call("Servicio sin uso","POST","/servicios",servicio("SRV-993","Prueba eliminación"),admin,201).get("id").asLong();serviceIds.add(clean);
  call("Servicio eliminar","DELETE","/servicios/"+clean,null,admin,204);
  long cleanv=call("Vehiculo sin orden","POST","/vehiculos",vehiculo("ZZT-993",client),asesor,201).get("id").asLong();vehicleIds.add(cleanv);
  call("Vehiculo eliminar","DELETE","/vehiculos/"+cleanv,null,asesor,204);
  var preflight=http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/v1/servicios")).header("Origin","http://localhost:4200").header("Access-Control-Request-Method","PATCH").header("Access-Control-Request-Headers","Authorization,Content-Type").method("OPTIONS",HttpRequest.BodyPublishers.noBody()).build(),HttpResponse.BodyHandlers.ofString());
  assertEquals(200,preflight.statusCode());assertEquals("http://localhost:4200",preflight.headers().firstValue("Access-Control-Allow-Origin").orElse(""));
 }
 @AfterEach void limpiarYExportar()throws Exception{
  for(long id:orderIds){jdbc.update("delete from detalle_orden where orden_id=?",id);jdbc.update("delete from orden_servicio where id=?",id);}
  for(long id:vehicleIds)jdbc.update("delete from vehiculo where id=?",id);
  for(long id:serviceIds)jdbc.update("delete from servicio where id=?",id);
  Files.createDirectories(Path.of("target/evidencia"));mapper.writerWithDefaultPrettyPrinter().writeValue(Path.of("target/evidencia/pruebas-oracle.json").toFile(),evidence);
 }
}
