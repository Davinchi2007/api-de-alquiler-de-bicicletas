package com.alquiler_de_bicicletas.infraestructura.excepcion;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.alquiler_de_bicicletas.aplicacion.excepcion.AlquilerNoEncontradoException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.dominio.excepcion.AlquilerYaFinalizadoException;
import com.alquiler_de_bicicletas.dominio.excepcion.FechaDevolucionInvalidaException;
import com.alquiler_de_bicicletas.dominio.excepcion.TransicionEstadoBicicletaInvalidaException;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaErrorDTO;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(BicicletaYaExisteException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarBicicletaYaExiste(BicicletaYaExisteException exception) {
        return respuestaError(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(BicicletaNoEncontradaException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarBicicletaNoEncontrada(BicicletaNoEncontradaException exception) {
        return respuestaError(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AlquilerNoEncontradoException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarAlquilerNoEncontrado(AlquilerNoEncontradoException exception) {
        return respuestaError(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AlquilerYaFinalizadoException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarAlquilerYaFinalizado(AlquilerYaFinalizadoException exception) {
        return respuestaError(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(TransicionEstadoBicicletaInvalidaException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarTransicionBicicletaInvalida(
            TransicionEstadoBicicletaInvalidaException exception) {
        return respuestaError(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(FechaDevolucionInvalidaException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarFechaDevolucionInvalida(
            FechaDevolucionInvalidaException exception) {
        return respuestaError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarValidacion(MethodArgumentNotValidException exception) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(errorValidacion -> errorValidacion.getDefaultMessage())
                .orElse("La solicitud no es válida");
        return respuestaError(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarTipoNoValido(MethodArgumentTypeMismatchException exception) {
        return respuestaError(HttpStatus.BAD_REQUEST,
                "El valor del parámetro " + exception.getName() + " no es válido");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarCuerpoNoLegible(
            HttpMessageNotReadableException exception) {
        return respuestaError(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato válido");
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<RespuestaErrorDTO> manejarConflictoPersistencia(Exception exception) {
        return respuestaError(HttpStatus.CONFLICT,
                "La operación entra en conflicto con el estado actual de los datos");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarArgumentoIlegal(IllegalArgumentException exception) {
        return respuestaError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaErrorDTO> manejarErrorInesperado(Exception exception) {
        return respuestaError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno al procesar la solicitud");
    }

    private ResponseEntity<RespuestaErrorDTO> respuestaError(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new RespuestaErrorDTO(estado.value(), mensaje));
    }
}