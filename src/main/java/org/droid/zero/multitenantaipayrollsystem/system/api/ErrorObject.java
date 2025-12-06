package org.droid.zero.multitenantaipayrollsystem.system.api;

import org.springframework.http.HttpStatus;

public record ErrorObject(
        HttpStatus status,
        String code,
        String title,
        String detail,
        Source source
) {
    public record Source(String pointer) {}
}
