package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

public class InvalidBearerTokenException extends RuntimeException {
    public InvalidBearerTokenException(String message) {
        super(message);
    }
}