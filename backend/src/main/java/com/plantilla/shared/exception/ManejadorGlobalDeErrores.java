package com.plantilla.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Manejo uniforme de errores para toda la API.
 *
 * <p>Todas las respuestas de error usan el formato Problem Details (RFC 9457):
 * {@code type}, {@code title}, {@code status}, {@code detail} e {@code instance}.
 * Al heredar de {@link ResponseEntityExceptionHandler}, los errores propios de
 * Spring MVC (JSON mal formado, método no permitido, ruta inexistente) ya
 * responden en ese formato.
 *
 * <p>Nunca se envían trazas internas al cliente: se registran en el log.
 */
@RestControllerAdvice
public class ManejadorGlobalDeErrores extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalDeErrores.class);

    /** 400: un DTO con {@code @Valid} no pasó la validación. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problema = ex.getBody();
        problema.setDetail(errores.values().stream().collect(Collectors.joining(" ")));
        problema.setProperty("errores", errores);
        if (request instanceof ServletWebRequest servlet) {
            problema.setInstance(URI.create(servlet.getRequest().getRequestURI()));
        }
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    /** 409: el dato viola una restricción de la base (UNIQUE, CHECK, FK...). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail restriccionViolada(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Restricción de la base de datos violada en {}", request.getRequestURI(), ex);
        return problema(HttpStatus.CONFLICT,
                "Los datos no cumplen una restricción de la base de datos.", request);
    }

    /** 500: cualquier error no previsto. El detalle técnico solo va al log. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail errorInesperado(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno. Consulte el log del backend.", request);
    }

    private static ProblemDetail problema(HttpStatus status, String detalle, HttpServletRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalle);
        problema.setInstance(URI.create(request.getRequestURI()));
        return problema;
    }
}
