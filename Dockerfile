# Etapa de compilacion: Gradle ya viene instalado y usa el mismo JDK 25 del proyecto.
FROM gradle:9.7.1-jdk25 AS compilacion

WORKDIR /app

# Copiar primero la configuracion de Gradle para favorecer la cache de dependencias.
COPY build.gradle settings.gradle ./

# Descargar dependencias antes de copiar el codigo fuente.
RUN gradle dependencies --no-daemon || true

COPY src ./src

# Generar el JAR ejecutable sin ejecutar las pruebas de integracion durante la imagen.
RUN gradle clean bootJar -x test --no-daemon

# Etapa de ejecucion: imagen JRE ligera, sin herramientas de compilacion.
FROM eclipse-temurin:25-jre-alpine AS ejecucion

WORKDIR /app

# Ejecutar con un usuario sin privilegios dentro del contenedor.
RUN addgroup -S aplicacion && adduser -S aplicacion -G aplicacion

COPY --from=compilacion /app/build/libs/*.jar app.jar

RUN chown aplicacion:aplicacion app.jar
USER aplicacion

EXPOSE 8080

# El endpoint de salud permite comprobar disponibilidad del contenedor.
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
