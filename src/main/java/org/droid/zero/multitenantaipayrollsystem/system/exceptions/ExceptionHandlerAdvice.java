package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.api.ErrorObject;
import org.droid.zero.multitenantaipayrollsystem.system.api.ErrorObject.Source;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseFactory<Object> handleObjectNotFoundException(ObjectNotFoundException e) {
        ErrorObject error = new ErrorObject(
                NOT_FOUND,
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
    @ResponseStatus(BAD_REQUEST)
    public ResponseFactory<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorObject> errors = new ArrayList<>();

        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();

        objectErrors.forEach(error -> {
            String field = ((FieldError) error).getField();
            String detail = error.getDefaultMessage();
            errors.add(new ErrorObject(
                    BAD_REQUEST,
                    "invalid_format",
                    "Validation Failed",
                    detail,
                    new Source(field)
            ));
         });
        return ResponseFactory.error(
                "Provided arguments are invalid, see errors for details.",
                errors
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public  ResponseFactory<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String variableName = e.getName();
        String detail = e.getMostSpecificCause().getMessage() + " for method parameter '" + variableName + "'" ;
        ErrorObject error = new ErrorObject(
                BAD_REQUEST,
                "invalid_format",
                "Validation Failed",
                detail,
                new Source(variableName)
        );
        return ResponseFactory.error(
                e.getMessage(),
                Collections.singletonList(error)
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(CONFLICT)
    public ResponseFactory<Object> handleDuplicateResourceException(DuplicateResourceException e) {
        List<ErrorObject> errors = new ArrayList<>();

        e.getFields().forEach(field -> {
            String detail = "The provided '" + field + "' is already taken.";
            errors.add(new ErrorObject(
                    CONFLICT,
                    "duplicate_value",
                    "Validation Failed",
                    detail,
                    new Source(field)
            ));
        });
        return ResponseFactory.error(e.getMessage(), errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    public ResponseFactory<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        List<ErrorObject> errors = new ArrayList<>();

        String message = e.getMostSpecificCause().getMessage();
        String detail = extractDetail(message);

        errors.add(new ErrorObject(
           UNPROCESSABLE_ENTITY,
           "integrity_violations",
                "Unprocessable Entity",
                detail,
                new Source(e.getMostSpecificCause().getCause().getMessage())
        ));

        return ResponseFactory.error(
                "Provided arguments violate data integrity, see data for details.",
                errors
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public  ResponseFactory<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        List<ErrorObject> errors = new ArrayList<>();
        String allowedMethods = Arrays.toString(e.getSupportedMethods());
        String detail = String.format("[%s] is not supported. Allowed methods: %s", e.getMethod(), allowedMethods);
        errors.add(new ErrorObject(
                METHOD_NOT_ALLOWED,
                "invalid_method",
                "Method Not Allowed",
                detail,
                new Source(e.getMethod())
        ));
        return ResponseFactory.error(e.getMessage(), errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseFactory<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorObject error = new ErrorObject(
                BAD_REQUEST,
                "invalid_format",
                "Malformed API Request",
                e.getMessage(),
                new Source("request_body")
        );
        return ResponseFactory.error(
                e.getMessage(),
                Collections.singletonList(error)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseFactory<Object> handleOtherExceptions(Exception e) {
        List<ErrorObject> errors = new ArrayList<>();
        errors.add(new ErrorObject(
                INTERNAL_SERVER_ERROR,
                "internal_server_error",
                "Server Error",
                e.getMessage(),
                new Source(e.getMessage())
        ));
        return ResponseFactory.error("An internal server error occurred.", errors);
    }

    private String extractDetail(String message) {
        Pattern pattern = Pattern.compile("Detail:\\s*(.*)");
        Matcher matcher = pattern.matcher(message);

        return matcher.find() ? matcher.group(1).trim() : message;
    }
}
