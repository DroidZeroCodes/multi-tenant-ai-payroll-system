package org.droid.zero.multitenantaipayrollsystem.system.api;

import org.springframework.http.HttpStatus;

public record ErrorObject(
        int status,
        String code,
        String title,
        String detail,
        Source source
) {
    public ErrorObject(HttpStatus status, String code, String title, String detail, Source source) {
        this(status.value(), code, title, detail, source);
    }

    public record Source(String pointer) {}
}
