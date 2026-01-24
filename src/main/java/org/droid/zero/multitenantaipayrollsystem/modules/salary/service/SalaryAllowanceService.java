package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceDatesUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;

import java.util.UUID;

public interface SalaryAllowanceService {

    SalaryAllowanceResponse findById(UUID salaryAllowanceId);

    SalaryAllowanceResponse findBySalaryIdAndAllowanceId(UUID salaryId, UUID allowanceId);

    SalaryAllowanceResponse save(SalaryAllowanceRequest salaryAllowanceRequest);

    SalaryAllowanceResponse updateEffectiveDates(UUID salaryAllowanceId, SalaryAllowanceDatesUpdateRequest request);

}
