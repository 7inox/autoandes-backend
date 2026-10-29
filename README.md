# AutoAndes API

Examen Parcial II LP2, versión A. Miguel Ángel Tinoco López, UPeU, 29/10/2026.
API de servicios, vehículos, órdenes y reportes; Oracle es la fuente de datos.

## Tecnologías y arquitectura

Java 21, Spring Boot 4.1.2, Hibernate 7.4.13, Oracle Free 26ai (driver 23.26.3),
Flyway, Spring Security 7.1.2, BCrypt, JJWT 0.13.0 y Springdoc 3.2.0.
Paquetes: config/security/entity/repository/service/controller/dto/mapper/exception.
El controlador llama al servicio; los DTO separan el API de JPA. Vehiculo → Cliente es LAZY.
La configuración única es `src/main/resources/application.yml`; no existe application.properties.

## Requisitos y ejecución

JDK 21, Oracle disponible en localhost:1522/FREEPDB1, esquema AUTOANDES y puerto 8080 libre.
El wrapper descarga Maven y las dependencias. En PowerShell:

```powershell
$env:JAVA_HOME = 'ruta/a/tu/jdk-21'
$env:DB_PASSWORD = Read-Host 'Contraseña Oracle'
$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
.\mvnw.cmd spring-boot:run
```

También puede usarse `.\run.ps1`: en esta máquina lee la configuración privada de
`.local/secrets.json`, excluida de Git. No compartir esa carpeta. En otra máquina suministrar las variables
anteriores. DB_URL y DB_USER son opcionales y permiten cambiar de esquema sin modificar YAML.
Nunca insertar la contraseña de Oracle o la clave JWT en YAML, Java, README o capturas.
Swagger: http://localhost:8080/swagger-ui/index.html. SPA permitida por CORS: http://localhost:4200.

## Flyway: dos escenarios distintos, verificados

* **AUTOANDES existente:** se auditó la estructura y la semilla y se realizó una sola vez un baseline
  explícito en versión 1. V1 NO se ejecutó sobre las tablas manuales. La configuración normal mantiene
  baseline-on-migrate=false, Hibernate ddl-auto=validate y no recrea tablas.
* **Instalación vacía:** `src/main/resources/db/migration/V1__esquema_y_semilla.sql` crea ocho tablas,
  restricciones, identidades, semilla y SEQ_ORDEN_NUMERO. Se ejecutó realmente en AUTOANDES_V1_TEST,
  creado de forma separada. Flyway registró una migración SQL aplicada y Hibernate validó el resultado.
  Ver [log auténtico](docs/instalacion-limpia-v1.txt) y [49 casos HTTP](docs/pruebas-oracle-v1-limpia.json).

Para adoptar otro esquema previamente cargado, respaldar y comparar primero su estructura con V1;
solo tras verificar equivalencia ejecutar una vez con `--spring.flyway.baseline-on-migrate=true`.
No usar esa opción globalmente ni para ocultar diferencias. En un esquema vacío no es necesaria.
No ejecutar manualmente V1 en AUTOANDES existente. Flyway conserva su historial en una tabla adicional.
Los IDs de semilla se alinean con START WITH LIMIT VALUE. Los números de orden usan una secuencia Oracle;
los rollback consumen valores y pueden dejar saltos, sin duplicar órdenes. No se reinicia esa secuencia.

## Contrato REST (prefijo /api/v1)

| Método y ruta | Permiso | Éxito |
|---|---|---|
| POST /auth/login | Público | 200 |
| GET /auth/me | Autenticado | 200 |
| GET /clientes | VEHICULO_LEER | 200 |
| GET /servicios, /servicios/{id} | SERVICIO_LEER | 200 |
| POST /servicios | SERVICIO_ESCRIBIR | 201 |
| PUT /servicios/{id}; DELETE /servicios/{id} | SERVICIO_ESCRIBIR | 204 |
| PATCH /servicios/{id}/estado {activo} | SERVICIO_ESCRIBIR | 200 |
| GET /vehiculos?clienteId, /vehiculos/{id} | VEHICULO_LEER | 200 |
| POST /vehiculos | VEHICULO_ESCRIBIR | 201 |
| PUT /vehiculos/{id}; DELETE /vehiculos/{id} | VEHICULO_ESCRIBIR | 204 |
| POST /ordenes | ORDEN_REGISTRAR | 201 |
| GET /ordenes?placa&estado&desde&hasta; /ordenes/{id} | ORDEN_LEER | 200 |
| PATCH /ordenes/{id}/anular {motivo} | ORDEN_ANULAR | 200 |
| GET /reportes/ingresos-por-servicio?desde&hasta | REPORTE_VER | 200 |

Fechas ISO yyyy-MM-dd, hasta inclusiva. Orden por fecha e ID descendentes. Error común:
timestamp/status/error/mensaje/ruta; campos solo en validación. Se distinguen 400, 401, 403, 404, 409 y 500.
No se elimina un servicio usado: DELETE devuelve 409 y PATCH permite su baja. Un vehículo con órdenes
no puede eliminarse. Precio/subtotal/total se calculan en el servicio con BigDecimal. Bloqueo de vehículo
serializa registros de kilometraje concurrentes. @Transactional revierte cabecera y líneas ante errores.
Reporte con JPQL GROUP BY y proyección; excluye ANULADAS. JWT HS256 dura 3600 s; cada petición valida
firma, emisor, vencimiento, usuario activo y permisos actuales de Oracle. API STATELESS.

## Pruebas reales

Con DB_PASSWORD y JWT_SECRET en el entorno:

```powershell
.\mvnw.cmd clean verify
```

`OracleApiIntegrationTests` hace peticiones HTTP reales a un puerto aleatorio y consulta Oracle.
Comprueba 49 casos HTTP, rollback SQL, precios, DTO, CORS y limpieza de sus IDs temporales en @AfterEach.
`docs/pruebas-oracle-main.json` contiene la ejecución sobre AUTOANDES y
`docs/pruebas-oracle-v1-limpia.json` la ejecución sobre el esquema creado por V1.
Se verificó BCrypt.matches para los cuatro usuarios: todos eran correctos y no se modificaron sus hashes.
La SPA tiene además tres escenarios Playwright en el otro repositorio.

## Alcance y límites

No se administra clientes/usuarios desde la SPA: son semilla según el examen. No hay paginación ni despliegue
en la nube, al no estar exigidos. El esquema de prueba AUTOANDES_V1_TEST queda separado para auditoría;
su credencial privada no está en Git. No correr las pruebas concurrentemente ni reutilizar sus códigos SRV-991..993
y placas ZZT-991..993 para datos manuales. Las capturas de la demostración usan registros temporales
identificables que se limpian al cerrar la entrega. Se conservan las ocho tablas y todos los registros originales.
Git: main, develop, examen-a-tinoco; etiqueta v1.0-examen-u2.

## Evaluación y permisos

| Usuario | Contraseña pública de evaluación | Rol | Estado |
|---|---|---|---|
| admin | Auto2026* | ADMIN | Activo |
| asesor | Auto2026* | ASESOR | Activo |
| jefe | Auto2026* | JEFE_TALLER | Activo |
| inactivo | Auto2026* | ASESOR | Inactivo, login rechazado |

ADMIN tiene los ocho permisos. ASESOR: SERVICIO_LEER, VEHICULO_LEER, VEHICULO_ESCRIBIR,
ORDEN_LEER y ORDEN_REGISTRAR. JEFE_TALLER: SERVICIO_LEER, SERVICIO_ESCRIBIR,
VEHICULO_LEER, ORDEN_LEER, ORDEN_ANULAR y REPORTE_VER. Varios roles unen sus permisos.

Contrato y ejemplos: [colección Postman](docs/AutoAndes.postman_collection.json).
La colección no contiene tokens activos ni secretos de infraestructura. Obtener el token mediante login.
Las escrituras manuales de Postman crean registros reales; la prueba JUnit realiza limpieza controlada.

Frontend: https://github.com/7inox/autoandes-frontend
