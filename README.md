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

La seguridad HTTP permite los requests CORS únicamente desde los orígenes
definidos en `CORS_ALLOWED_ORIGINS`. Por defecto se permite el frontend local
`http://localhost:3000`. Para varios orígenes, separarlos por comas:

```text
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://frontend.example.com
```

La API usa headers defensivos, no mantiene sesiones y expone de Actuator solo
`/actuator/health` y `/actuator/info`.

Flyway crea y valida las tablas al iniciar la aplicacion.

## Ejecutar

En Windows:

```powershell
.\gradlew.bat clean build
.\gradlew.bat bootRun
```

La API queda disponible en `http://localhost:8080`.

Ejecutar todas las pruebas:

```powershell
.\gradlew.bat test
```

Las pruebas de integracion requieren las mismas variables de conexion a PostgreSQL.

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

## Datos de referencia

Los datos sugeridos por el enunciado son `BIC-001` urbana disponible,
`BIC-002` montana disponible, `BIC-003` electrica disponible, `BIC-004`
montana en mantenimiento y `BIC-005` urbana disponible. El proyecto no
inserta datos automaticamente para evitar contaminar entornos de prueba;
pueden registrarse mediante el endpoint de bicicletas.
