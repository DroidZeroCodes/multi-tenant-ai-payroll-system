package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

import lombok.Getter;
import org.droid.zero.multitenantaipayrollsystem.system.ResourceType;

import java.util.List;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final ResourceType resourceType;
    private final List<String> fields;

    public DuplicateResourceException(ResourceType resourceType, List<String> fields) {
        super("An existing " + resourceType.name() + " already exists with the provided arguments.");
        this.resourceType = resourceType;
        this.fields = fields;
    }
}

