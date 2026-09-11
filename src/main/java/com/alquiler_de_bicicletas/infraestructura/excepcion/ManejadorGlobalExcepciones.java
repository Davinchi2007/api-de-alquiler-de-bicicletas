package com.alquiler_de_bicicletas.infraestructura.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaYaExisteException;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaErrorDTO> manejarArgumentoIlegal(IllegalArgumentException exception) {
        return respuestaError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<RespuestaErrorDTO> respuestaError(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new RespuestaErrorDTO(estado.value(), mensaje));
    }
}