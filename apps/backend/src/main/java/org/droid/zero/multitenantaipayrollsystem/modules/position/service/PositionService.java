package org.droid.zero.multitenantaipayrollsystem.modules.position.service;

import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionResponse;

import java.util.UUID;

public interface PositionService {

    PositionResponse findById(UUID id);

    PositionResponse save(PositionRequest position);

    PositionResponse update(PositionRequest position, UUID positionId);

    void togglePositionStatus(UUID positionId);
}
