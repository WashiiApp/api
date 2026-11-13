
package br.com.washii.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return error(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<StandardError> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        return error(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusiness(
            BusinessException exception,
            HttpServletRequest request
    ) {
        return error(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<StandardError> handleValidation(
            ValidationException exception,
            HttpServletRequest request
    ) {
        return error(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<StandardError> handleExternalService(
            ExternalServiceException exception,
            HttpServletRequest request
    ) {
        log.error("Erro ao comunicar com serviço externo.", exception);

        return error(
                HttpStatus.BAD_GATEWAY,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(fieldError ->
                        fieldError.getField() + ": "
                                + fieldError.getDefaultMessage()
                )
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Os dados enviados são inválidos.";
        }

        return error(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(violation ->
                        violation.getPropertyPath() + ": "
                                + violation.getMessage()
                )
                .sorted()
                .collect(Collectors.joining("; "));

        return error(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<StandardError> handleMalformedRequest(
            HttpServletRequest request
    ) {
        log.warn(
                "Requisição com dados ausentes ou inválidos na rota {}.",
                request.getRequestURI()
        );

        return error(
                HttpStatus.BAD_REQUEST,
                "A requisição contém dados ausentes ou inválidos.",
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> handleMethodNotSupported(
            HttpServletRequest request
    ) {
        log.warn(
                "Método HTTP não permitido na rota {}.",
                request.getRequestURI()
        );

        return error(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP não permitido para este recurso.",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFound(
            HttpServletRequest request
    ) {
        log.warn(
                "Endpoint não encontrado: {}",
                request.getRequestURI()
        );

        return error(
                HttpStatus.NOT_FOUND,
                "Endpoint não encontrado.",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.error(
                "Violação de integridade dos dados na rota {}.",
                request.getRequestURI(),
                exception
        );

        return error(
                HttpStatus.CONFLICT,
                "A operação viola uma regra de integridade dos dados.",
                request
        );
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<StandardError> handleAuthenticationProviderError(
            RestClientResponseException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "O provedor externo retornou HTTP {} na rota {}.",
                exception.getStatusCode().value(),
                request.getRequestURI()
        );

        return error(
                HttpStatus.BAD_GATEWAY,
                "O provedor externo não pôde concluir a operação.",
                request
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<StandardError> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value()
        );

        String message = exception.getReason() == null
                ? status.getReasonPhrase()
                : exception.getReason();

        return error(
                status,
                message,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error(
                "Erro interno inesperado na rota {}.",
                request.getRequestURI(),
                exception
        );

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                request
        );
    }


    private ResponseEntity<StandardError> error(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        StandardError body = new StandardError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    public ResponseEntity<StandardError> handleOptimisticLock(Exception exception, HttpServletRequest request) {
        log.warn("Conflito de bloqueio otimista na rota {}: {}",
                request.getRequestURI(), exception.getMessage());
        return error(
                HttpStatus.CONFLICT,
                "Este agendamento foi alterado por outra operação. Atualize os dados e tente novamente.",
                request
        );
    }

}
