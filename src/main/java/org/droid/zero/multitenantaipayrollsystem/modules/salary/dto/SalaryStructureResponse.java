package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import java.sql.Date;
import java.util.UUID;

public record SalaryStructureResponse(
        UUID id,
        String name,
        String description,
        Double baseSalary,
        Date effectiveDate,
        boolean active
){}
