package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import java.sql.Date;
import java.util.UUID;

public record SalaryAllowanceResponse(
        UUID id,
        UUID salaryStructureId,
        UUID allowanceId,
        Date effectiveDate,
        Date expiryDate,
        String assignedBy
){}
