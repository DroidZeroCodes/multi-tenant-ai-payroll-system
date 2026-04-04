package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SalaryStructureResponse(
        UUID id,
        String name,
        String description,
        Double baseSalary,
        LocalDate effectiveDate,
        boolean active
){}
