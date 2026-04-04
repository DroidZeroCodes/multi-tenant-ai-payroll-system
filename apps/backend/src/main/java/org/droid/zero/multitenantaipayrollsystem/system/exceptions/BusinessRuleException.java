package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super("Business Rule Error: " + message);
    }
}