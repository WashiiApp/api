package br.com.washii.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public StandardError handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public StandardError handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public StandardError handleBusiness(
            BusinessException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(ValidationException.class)
    public StandardError handleValidation(
            ValidationException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public StandardError handleExternalService(
            ExternalServiceException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public StandardError handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Os dados enviados são inválidos.";
        }

        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public StandardError handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public StandardError handleMalformedRequest(
            Exception exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        return error(HttpStatus.BAD_REQUEST, "A requisição contém dados ausentes ou inválidos.", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public StandardError handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não permitido para este recurso.", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public StandardError handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        return error(HttpStatus.NOT_FOUND, "Endpoint não encontrado.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public StandardError handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        return error(HttpStatus.CONFLICT, "A operação viola uma regra de integridade dos dados.", request);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public StandardError handleAuthenticationProviderError(
            RestClientResponseException exception,
            HttpServletRequest request
    ) {
        return error(
                HttpStatus.BAD_GATEWAY,
                "O provedor externo não pôde concluir a operação.",
                request
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public StandardError handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getReason() == null
                ? status.getReasonPhrase()
                : exception.getReason();

        return error(status, message, request);
    }

    @ExceptionHandler(Exception.class)
    public StandardError handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                request
        );
    }

    private StandardError error(HttpStatus status, String message, HttpServletRequest request) {
        return new StandardError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }
}
