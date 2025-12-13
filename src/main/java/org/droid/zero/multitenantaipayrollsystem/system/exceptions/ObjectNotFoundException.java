package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

import lombok.Getter;
import org.droid.zero.multitenantaipayrollsystem.system.ResourceType;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private final ResourceType resourceType;
    public ObjectNotFoundException(ResourceType resourceType, Object id) {
        super("Could not find " + resourceType + " with ID '" + id + "'.");
        this.resourceType = resourceType;
    }

    public ObjectNotFoundException(ResourceType resourceType, Object id, String identifier) {
        super("Could not find " + resourceType + " with " + identifier + " '" + id + "'.");
        this.resourceType = resourceType;
    }
}
