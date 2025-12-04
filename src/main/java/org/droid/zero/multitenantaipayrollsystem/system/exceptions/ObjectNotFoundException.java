package org.droid.zero.multitenantaipayrollsystem.system.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectName, String message) {
        super("Could not find object with name " + objectName + ": " + message);
    }
}
