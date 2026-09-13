# API de Alquiler de Bicicletas

API REST construida con Java 25 y Spring Boot para registrar bicicletas,
gestionar alquileres, calcular costos y consultar historiales.

## Tecnologias

- Java 25
- Spring Boot 4.1.1
- Gradle Wrapper
- PostgreSQL
- Spring Data JPA
- Flyway
- Bean Validation

## Requisitos

- JDK 25
- PostgreSQL disponible en `localhost:5432`
- Una base de datos llamada `alquiler_bicicletas`

Crear la base de datos con `database/create-database.sql` o ejecutando:

```sql
CREATE DATABASE alquiler_bicicletas;
```

Configurar la conexion mediante estas variables de entorno:

```text
DB_URL=jdbc:postgresql://localhost:5432/alquiler_bicicletas
DB_USERNAME=postgres
DB_PASSWORD=tu-clave
```

La aplicacion necesita las tres variables para iniciar Spring Boot y ejecutar
Flyway. En Windows PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/alquiler_bicicletas"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="tu-clave"
```

La seguridad HTTP permite los requests CORS únicamente desde los orígenes
definidos en `CORS_ALLOWED_ORIGINS`. Por defecto se permite el frontend local
`http://localhost:3000`. Para varios orígenes, separarlos por comas:

```text
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://frontend.example.com
```

La API usa headers defensivos, no mantiene sesiones y expone de Actuator solo
`/actuator/health` y `/actuator/info`.

## Observabilidad

La API usa SLF4J con Logback a traves de Spring Boot. El nivel general es
`INFO`; los detalles de diagnostico pueden habilitarse para el paquete de la
aplicacion con nivel `DEBUG` sin activar todo el ruido de Spring.

Cada solicitud recibe o propaga un `X-Correlation-Id`. El identificador se
incluye en los logs mediante MDC y se devuelve en la respuesta para facilitar
el seguimiento entre cliente, API y persistencia. El MDC se limpia siempre al
terminar la solicitud.

Los logs registran eventos de negocio y errores tecnicos sin incluir
contraseñas, tokens, credenciales ni payloads completos con datos sensibles.

Flyway crea y valida las tablas al iniciar la aplicacion.

## Ejecutar

En Windows:

```powershell
.\gradlew.bat clean build
.\gradlew.bat bootRun
```

`bootRun` requiere PostgreSQL activo, la base de datos creada y las variables
`DB_URL`, `DB_USERNAME` y `DB_PASSWORD` configuradas en la misma terminal.

La API queda disponible en `http://localhost:8080`.

Ejecutar todas las pruebas:

```powershell
.\gradlew.bat test
```

Las pruebas de integracion requieren las mismas variables de conexion a
PostgreSQL. La validacion completa se puede ejecutar con:

```powershell
.\gradlew.bat clean test
```

## Ejecutar con Docker Compose

La configuracion Docker utiliza PostgreSQL 16 y la misma version de Java 25
que define el proyecto. Docker Compose levanta primero la base de datos,
espera a que este saludable y despues inicia la API. Flyway crea las tablas
automaticamente al arrancar la aplicacion.

### Requisitos

- Docker Desktop con Docker Compose v2.
- Puertos `5432` y `8080` disponibles.

### Iniciar la aplicacion

Desde la raiz del proyecto, construye y levanta la API directamente con las
credenciales por defecto configuradas en Docker Compose:

```powershell
docker compose up --build
```

La definicion de `POSTGRES_PASSWORD` es opcional. Si no la defines, Compose
utiliza la contraseña local predeterminada `postgrespassword`. Para personalizar
la clave, define la variable antes de iniciar los servicios:

```powershell
$env:POSTGRES_PASSWORD="tu-clave-local"
docker compose up --build
```

La API queda disponible en `http://localhost:8080` y su estado se puede
comprobar en `http://localhost:8080/actuator/health`.

Para iniciar los servicios en segundo plano:

```powershell
docker compose up --build -d
```

Tambien puedes definir `POSTGRES_PASSWORD` antes de este comando si deseas
utilizar una contraseña personalizada.

Consultar los logs:

```powershell
docker compose logs -f api
docker compose logs -f base_datos
```

Detener los contenedores sin eliminar los datos:

```powershell
docker compose down
```

Detener los contenedores y eliminar tambien el volumen persistente de
PostgreSQL:

```powershell
docker compose down -v
```

Para eliminar tambien la imagen construida y los recursos no utilizados:

```powershell
docker compose down -v --rmi local
docker image prune
```

No guardes `POSTGRES_PASSWORD` en el repositorio. Para configurar otros
valores puedes definir tambien `POSTGRES_DB`, `POSTGRES_USER` y
`CORS_ALLOWED_ORIGINS` antes de ejecutar Docker Compose.

## Arquitectura

El proyecto usa arquitectura hexagonal:

- `dominio`: entidades, enumeraciones, tarifas e invariantes de negocio.
- `aplicacion`: casos de uso y puertos de entrada/salida.
- `infraestructura`: controladores REST, DTOs, mapeadores y adaptadores JPA.

El dominio no depende de Spring ni de JPA. Los servicios de aplicacion
orquestan los casos de uso y los puertos permiten sustituir la persistencia.
Las transacciones cubren el inicio y la finalizacion del alquiler. La base de
datos agrega FK, versionado optimista y unicidad para un solo alquiler activo
por bicicleta.

## Endpoints principales

Registrar una bicicleta:

```bash
curl -X POST http://localhost:8080/api/bicicletas \
  -H "Content-Type: application/json" \
  -d '{"codigo":"BIC-001","tipo":"URBANA","estado":"DISPONIBLE"}'
```

Consultar bicicletas disponibles, opcionalmente por tipo:

```bash
curl "http://localhost:8080/api/bicicletas/disponibles?tipo=MONTAÑA"
```

Iniciar un alquiler:

```bash
curl -X POST http://localhost:8080/api/alquileres \
  -H "Content-Type: application/json" \
  -d '{"codigoBicicleta":"BIC-001","nombreCliente":"Ana Perez","fechaHoraInicio":"2026-09-11T10:00:00","duracionEstimadaHoras":2}'
```

Finalizar un alquiler:

```bash
curl -X POST http://localhost:8080/api/alquileres/BIC-001/finalizar \
  -H "Content-Type: application/json" \
  -d '{"fechaHoraDevolucion":"2026-09-11T13:20:00"}'
```

Consultar el historial:

```bash
curl http://localhost:8080/api/alquileres/bicicletas/BIC-001
```

### Resumen de endpoints

| Metodo | Ruta | Uso | Respuestas principales |
|---|---|---|---|
| `POST` | `/api/bicicletas` | Registrar una bicicleta | `201`, `400`, `409` |
| `GET` | `/api/bicicletas/{codigo}` | Consultar una bicicleta | `200`, `404` |
| `GET` | `/api/bicicletas/disponibles` | Consultar disponibilidad | `200`, `400` |
| `GET` | `/api/bicicletas/disponibles?tipo=MONTAÑA` | Filtrar por tipo | `200`, `400` |
| `POST` | `/api/alquileres` | Iniciar un alquiler | `201`, `400`, `404`, `409` |
| `POST` | `/api/alquileres/{codigo}/finalizar` | Finalizar un alquiler | `200`, `400`, `404`, `409` |
| `GET` | `/api/alquileres/bicicletas/{codigo}` | Consultar historial | `200`, `404` |
| `GET` | `/actuator/health` | Consultar salud de la API | `200` |

### Ejemplos de errores

Solicitud invalida (`400`):

```json
{
  "status": 400,
  "message": "La duración estimada debe ser mayor que cero"
}
```

Bicicleta inexistente (`404`):

```json
{
  "status": 404,
  "message": "No se encontró una bicicleta con el código BIC-999"
}
```

Bicicleta no disponible (`409`):

```json
{
  "status": 409,
  "message": "La bicicleta BIC-002 no puede alquilarse porque está ALQUILADA"
}
```

La coleccion Postman esta disponible en `postman/alquiler-bicicletas.postman_collection.json`.

## Reglas implementadas

- Tarifas por hora: urbana 3500, montana 5000 y electrica 7500.
- El tiempo real se redondea hacia arriba a horas completas.
- La multa es el 50% de la tarifa por cada hora de retraso redondeada hacia arriba.
- Una bicicleta solo puede alquilarse si esta `DISPONIBLE`.
- Una bicicleta se registra con estado `DISPONIBLE`, `ALQUILADA` o `EN_MANTENIMIENTO`.
- Un alquiler finalizado no puede finalizarse de nuevo.
- Los importes se expresan en pesos colombianos enteros.

## Supuestos

- La fecha de inicio y la fecha de devolucion se reciben sin zona horaria y se
  interpretan con la zona horaria del servidor.
- La duracion estimada se expresa en horas enteras positivas.
- Las tarifas son datos de dominio fijos para esta prueba tecnica; el alquiler
  conserva la tarifa aplicada como dato historico.
- La API no implementa autenticacion de usuarios porque el enunciado no define
  usuarios, credenciales, roles ni permisos. La seguridad implementada cubre
  CORS, headers HTTP, API stateless y la exposicion limitada de Actuator.

## Datos de referencia

Los datos sugeridos por el enunciado son `BIC-001` urbana disponible,
`BIC-002` montana disponible, `BIC-003` electrica disponible, `BIC-004`
montana en mantenimiento y `BIC-005` urbana disponible. El proyecto no
inserta datos automaticamente para evitar contaminar entornos de prueba;
pueden registrarse mediante el endpoint de bicicletas.

### Registrar datos de referencia

Con la API ejecutandose en `http://localhost:8080`, registra secuencialmente
las cinco bicicletas de ejemplo:

```powershell
curl.exe -X POST http://localhost:8080/api/bicicletas `
  -H "Content-Type: application/json" `
  -d '{"codigo":"BIC-001","tipo":"URBANA","estado":"DISPONIBLE"}'

curl.exe -X POST http://localhost:8080/api/bicicletas `
  -H "Content-Type: application/json" `
  -d '{"codigo":"BIC-002","tipo":"MONTAÑA","estado":"DISPONIBLE"}'

curl.exe -X POST http://localhost:8080/api/bicicletas `
  -H "Content-Type: application/json" `
  -d '{"codigo":"BIC-003","tipo":"ELÉCTRICA","estado":"DISPONIBLE"}'

curl.exe -X POST http://localhost:8080/api/bicicletas `
  -H "Content-Type: application/json" `
  -d '{"codigo":"BIC-004","tipo":"MONTAÑA","estado":"EN_MANTENIMIENTO"}'

curl.exe -X POST http://localhost:8080/api/bicicletas `
  -H "Content-Type: application/json" `
  -d '{"codigo":"BIC-005","tipo":"URBANA","estado":"DISPONIBLE"}'
```
