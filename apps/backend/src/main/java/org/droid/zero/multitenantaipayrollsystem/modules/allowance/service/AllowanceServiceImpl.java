package org.droid.zero.multitenantaipayrollsystem.modules.allowance.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.mapper.AllowanceMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.repository.AllowanceRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.ALLOWANCE;


@Service
@RequiredArgsConstructor
public class AllowanceServiceImpl extends BaseService implements AllowanceService {

    private final AllowanceRepository allowanceRepository;
    private final AllowanceMapper allowanceMapper;

    @Transactional
    @Override
    public AllowanceResponse findById(UUID allowanceId) {
        Allowance requestedAllowance = this.allowanceRepository.findById(allowanceId )
                .orElseThrow(()-> new ResourceNotFoundException(ALLOWANCE, allowanceId ));

        return this.allowanceMapper.toResponse(requestedAllowance);
    }

    @Transactional
    @Override
    public AllowanceResponse save(AllowanceRequest request) {
        new FieldDuplicateValidator()
                .addField(allowanceRepository.existsByNameIgnoreCase(request.name()), "name")
                .validate(ALLOWANCE);

        Allowance newAllowance = new Allowance(
                request.name(),
                request.description(),
                request.allowanceType(),
                request.amount(),
                request.taxable(),
                TenantContext.getTenantId()
        );

        Allowance savedAllowance = this.allowanceRepository.save(newAllowance);
        return this.allowanceMapper.toResponse(savedAllowance);
    }

    @Transactional
    @Override
    public AllowanceResponse update(AllowanceRequest request, UUID allowanceId ) {
        //Find the Allowance  from the repository
        Allowance existingAllowance = this.allowanceRepository.findById(allowanceId )
                .orElseThrow(()-> new ResourceNotFoundException(ALLOWANCE, allowanceId ));

        //Update the allowance 
        existingAllowance.updateAllowanceDetails(
                request.name(),
                request.description(),
                request.allowanceType(),
                request.amount(),
                request.taxable()
        );

        return this.allowanceMapper.toResponse(existingAllowance);
    }

}

