package org.droid.zero.multitenantaipayrollsystem.system.util;

import lombok.Getter;
import org.droid.zero.multitenantaipayrollsystem.system.ResourceType;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FieldDuplicateValidator {
    private final List<String> duplicateFields = new ArrayList<>();

    public FieldDuplicateValidator addField(boolean exists, String field) {
        if (exists) this.duplicateFields.add(field);
        return this;
    }

    public FieldDuplicateValidator validate(ResourceType resourceType) {
        if (!this.duplicateFields.isEmpty()) throw new DuplicateResourceException(resourceType, duplicateFields);
        return this;
    }
}
