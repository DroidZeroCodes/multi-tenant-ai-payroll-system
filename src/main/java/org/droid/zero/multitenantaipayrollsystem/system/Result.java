package org.droid.zero.multitenantaipayrollsystem.system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record Result<T>(
        boolean flag,
        String message,
        T data
) {

    public static <T> ResponseEntity<Result<T>> success(String message, T data) {
        return new ResponseEntity<>(new Result<>(true, message, data), HttpStatus.OK);
    }
    public static <T> ResponseEntity<Result<T>> created(String message, T data) {
        return new ResponseEntity<>(new Result<>(true, message, data), HttpStatus.CREATED);
    }
    public static <T> ResponseEntity<Result<T>> notFound(String message) {
        return new ResponseEntity<>(new Result<>( false, message, null), HttpStatus.NOT_FOUND);
    }
    public static <T> ResponseEntity<Result<T>> badRequest(String message, T data) {
        return new ResponseEntity<>(new Result<>(false, message, data), HttpStatus.BAD_REQUEST);
    }
    public static <T> ResponseEntity<Result<T>> unauthorized(String message, T data) {
        return new ResponseEntity<>(new Result<>(false, message, data), HttpStatus.UNAUTHORIZED);
    }
    public static <T> ResponseEntity<Result<T>> forbidden(String message, T data) {
        return new ResponseEntity<>(new Result<>(false, message, data), HttpStatus.FORBIDDEN);
    }
    public static <T> ResponseEntity<Result<T>> serverError(String message, T data) {
        return new ResponseEntity<>(new Result<>(false, message, data), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static <T> ResponseEntity<Result<T>> status(HttpStatus status, String message, T data) {
        return new ResponseEntity<>(new Result<>(status.value() < 400 , message, data), status);
    }
}
