package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

import lombok.Getter;
import org.droid.zero.multitenantaipayrollsystem.system.ResourceType;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private final ResourceType resourceType;
    public ObjectNotFoundException(ResourceType resourceType, String id) {
        super("Could not find " + resourceType + " with ID '" + id + "'.");
        this.resourceType = resourceType;
    }
}
