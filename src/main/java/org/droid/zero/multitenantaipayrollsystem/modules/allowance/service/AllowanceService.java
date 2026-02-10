package org.droid.zero.multitenantaipayrollsystem.modules.allowance.service;

import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceResponse;

import java.util.UUID;

public interface AllowanceService {

    AllowanceResponse findById(UUID id);

    AllowanceResponse save(AllowanceRequest allowance);

    AllowanceResponse update(AllowanceRequest allowance, UUID allowanceId);

}
