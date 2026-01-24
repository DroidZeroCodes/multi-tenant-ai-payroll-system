package org.droid.zero.multitenantaipayrollsystem.modules.allowance.service;

import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceResponse;

import java.util.UUID;

public interface AllowanceService {

    AllowanceResponse findById(UUID id);

    AllowanceResponse save(AllowanceRequest salary);

    AllowanceResponse update(AllowanceRequest salary, UUID salaryId);

}
