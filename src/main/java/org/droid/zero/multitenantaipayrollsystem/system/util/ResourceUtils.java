package org.droid.zero.multitenantaipayrollsystem.system.util;

import java.util.List;

public class ResourceUtils {
    public static void checkDuplicate(boolean exists, String field, List<String> dest) {
        if (exists) dest.add(field);
    }
}
