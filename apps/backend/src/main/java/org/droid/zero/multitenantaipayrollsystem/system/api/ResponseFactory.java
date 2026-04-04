package org.droid.zero.multitenantaipayrollsystem.system.api;

import java.util.List;

public record ResponseFactory<T>(
        boolean success,
        String message,
        T data,
        List<ErrorObject> errors
) {

    public static <T> ResponseFactory<T> success(String message, T data) {
        return new ResponseFactory<>(true, message, data, null);
    }

    public static <T> ResponseFactory<T> created(String message, T data) {
        return new ResponseFactory<>(true, message, data, null);
    }

    public static  <T> ResponseFactory<T> error(String message, List<ErrorObject> errors) {
        return new ResponseFactory<>(false, message, null, errors);
    }
}
