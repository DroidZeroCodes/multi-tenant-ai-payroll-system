package org.droid.zero.multitenantaipayrollsystem.modules.deduction.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.mapper.DeductionMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.model.Deduction;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.repository.DeductionRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.DEDUCTION;


@Service
@RequiredArgsConstructor
public class DeductionServiceImpl extends BaseService implements DeductionService {

    private final DeductionRepository deductionRepository;
    private final DeductionMapper deductionMapper;

    @Transactional
    @Override
    public DeductionResponse findById(UUID deductionId) {
        Deduction requestedDeduction = this.deductionRepository.findById(deductionId )
                .orElseThrow(()-> new ResourceNotFoundException(DEDUCTION, deductionId ));

        return this.deductionMapper.toResponse(requestedDeduction);
    }

    @Transactional
    @Override
    public DeductionResponse save(DeductionRequest request) {
        new FieldDuplicateValidator()
                .addField(deductionRepository.existsByNameIgnoreCase(request.name()), "name")
                .validate(DEDUCTION);

        Deduction newDeduction = new Deduction(
                request.name(),
                request.description(),
                request.deductionType(),
                request.amount(),
                request.taxable(),
                TenantContext.getTenantId()
        );

        Deduction savedDeduction = this.deductionRepository.save(newDeduction);
        return this.deductionMapper.toResponse(savedDeduction);
    }

    @Transactional
    @Override
    public DeductionResponse update(DeductionRequest request, UUID deductionId ) {
        //Find the Deduction  from the repository
        Deduction existingDeduction = this.deductionRepository.findById(deductionId )
                .orElseThrow(()-> new ResourceNotFoundException(DEDUCTION, deductionId ));

        //Update the deduction 
        existingDeduction.updateDeductionDetails(
                request.name(),
                request.description(),
                request.deductionType(),
                request.amount(),
                request.taxable()
        );

        return this.deductionMapper.toResponse(existingDeduction);
    }

}

