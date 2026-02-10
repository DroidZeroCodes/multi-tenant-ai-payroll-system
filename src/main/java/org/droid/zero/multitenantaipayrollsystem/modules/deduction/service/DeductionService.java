package org.droid.zero.multitenantaipayrollsystem.modules.deduction.service;

import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionResponse;

import java.util.UUID;

public interface DeductionService {

    DeductionResponse findById(UUID id);

    DeductionResponse save(DeductionRequest deductionRequest);

    DeductionResponse update(DeductionRequest deductionRequest, UUID deductionId);

}
