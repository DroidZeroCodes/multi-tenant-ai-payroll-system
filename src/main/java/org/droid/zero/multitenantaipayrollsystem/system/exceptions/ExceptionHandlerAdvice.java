package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.api.ErrorObject;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseFactory<Object> handleObjectNotFoundException(ObjectNotFoundException e) {
        ErrorObject error = new ErrorObject(
                HttpStatus.NOT_FOUND,
                "resource_not_found",
                "Resource Not Found",
                e.getMessage(),
                null
        );
        return ResponseFactory.error(
                e.getMessage(),
                Collections.singletonList(error)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseFactory<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorObject> errors = new ArrayList<>();

        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();

        objectErrors.forEach(error -> {
            String field = ((FieldError) error).getField();
            String detail = error.getDefaultMessage();
            errors.add(new ErrorObject(
                    HttpStatus.BAD_REQUEST,
                    "invalid_format",
                    "Validation Failed",
                    detail,
                    new ErrorObject.Source(field)
            ));
         });
        return ResponseFactory.error(
                "Provided arguments are invalid, see errors for details.",
                errors
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseFactory<Object> handleDuplicateResourceException(DuplicateResourceException e) {
        List<ErrorObject> errors = new ArrayList<>();

        e.getFields().forEach(field -> {
            String detail = "The provided '" + field + "' is already taken.";
            errors.add(new ErrorObject(
                    HttpStatus.CONFLICT,
                    "duplicate_value",
                    "Validation Failed",
                    detail,
                    new ErrorObject.Source(field)
            ));
        });
        return ResponseFactory.error(e.getMessage(), errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseFactory<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        List<ErrorObject> errors = new ArrayList<>();

        String message = e.getMostSpecificCause().getMessage();
        String detail = extractDetail(message);

        errors.add(new ErrorObject(
           HttpStatus.UNPROCESSABLE_ENTITY,
           "integrity_violations",
                "Unprocessable Entity",
                detail,
                new ErrorObject.Source(e.getMostSpecificCause().getCause().getMessage())
        ));

        return ResponseFactory.error(
                "Provided arguments violate data integrity, see data for details.",
                errors
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public  ResponseFactory<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        List<ErrorObject> errors = new ArrayList<>();
        String allowedMethods = Arrays.toString(e.getSupportedMethods());
        String detail = String.format("[%s] is not supported. Allowed methods: %s", e.getMethod(), allowedMethods);
        errors.add(new ErrorObject(
                HttpStatus.METHOD_NOT_ALLOWED,
                "invalid_method",
                "Method Not Allowed",
                detail,
                new ErrorObject.Source(e.getMethod())
        ));
        return ResponseFactory.error(e.getMessage(), errors);
    }



    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseFactory<Object> handleOtherExceptions(Exception e) {
        List<ErrorObject> errors = new ArrayList<>();
        errors.add(new ErrorObject(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal_server_error",
                "Server Error",
                e.getMessage(),
                new ErrorObject.Source(e.getMessage())
        ));
        return ResponseFactory.error("An internal server error occurred.", errors);
    }

    private String extractDetail(String message) {
        Pattern pattern = Pattern.compile("Detail:\\s*(.*)");
        Matcher matcher = pattern.matcher(message);

        return matcher.find() ? matcher.group(1).trim() : message;
    }
}
