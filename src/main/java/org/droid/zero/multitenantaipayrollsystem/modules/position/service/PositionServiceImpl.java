package org.droid.zero.multitenantaipayrollsystem.modules.position.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.position.mapper.PositionMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.position.repository.PositionRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.POSITION;


@Service
@RequiredArgsConstructor
public class PositionServiceImpl extends BaseService implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    @Transactional
    @Override
    public PositionResponse findById(UUID positionId) {
        Position requestedPosition = this.positionRepository.findById(positionId)
                .orElseThrow(()-> new ObjectNotFoundException(POSITION, positionId));

        return this.positionMapper.toResponse(requestedPosition);
    }

    @Transactional
    @Override
    public PositionResponse save(PositionRequest request) {
        new FieldDuplicateValidator()
                .addField(positionRepository.existsByTitleIgnoreCase(request.title()), "title")
                .validate(POSITION);

        Position newPosition = new Position(
                request.title(),
                request.description(),
                request.level(),
                TenantContext.getTenantId()
        );

        Position savedPosition = this.positionRepository.save(newPosition);
        return this.positionMapper.toResponse(savedPosition);
    }

    @Transactional
    @Override
    public PositionResponse update(PositionRequest request, UUID positionId) {
        //Find the Position from the repository
        Position existingPosition = this.positionRepository.findById(positionId)
                .orElseThrow(()-> new ObjectNotFoundException(POSITION, positionId));

        //Update the position
        existingPosition.updatePositionDetails(
                request.title(),
                request.description(),
                request.level()
        );

        return this.positionMapper.toResponse(existingPosition);
    }

    @Transactional
    @Override
    public void togglePositionStatus(UUID positionId) {
        Position existingPosition = this.positionRepository.findById(positionId)
                .orElseThrow(()-> new ObjectNotFoundException(POSITION, positionId));

        existingPosition.toggleActiveStatus();
    }
}
