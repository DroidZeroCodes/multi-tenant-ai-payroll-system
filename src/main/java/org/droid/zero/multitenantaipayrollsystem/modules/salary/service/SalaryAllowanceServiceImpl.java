package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.repository.AllowanceRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceDatesUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper.SalaryAllowanceMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryAllowance;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryAllowanceRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryStructureRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.*;


@Service
@RequiredArgsConstructor
public class SalaryAllowanceServiceImpl extends BaseService implements SalaryAllowanceService {

    private final SalaryAllowanceRepository salaryAllowanceRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final AllowanceRepository allowanceRepository;
    private final SalaryAllowanceMapper salaryAllowanceMapper;

    @Transactional
    @Override
    public SalaryAllowanceResponse findById(UUID salaryAllowanceId) {
        SalaryAllowance requestedSalaryAllowance = this.salaryAllowanceRepository.findById(salaryAllowanceId)
                .orElseThrow(()-> new ResourceNotFoundException(SALARY_ALLOWANCE, salaryAllowanceId));

        return this.salaryAllowanceMapper.toResponse(requestedSalaryAllowance);
    }

    @Transactional
    @Override
    public SalaryAllowanceResponse findBySalaryIdAndAllowanceId(UUID salaryId, UUID allowanceId) {
        SalaryAllowance requestedSalaryAllowance = this.salaryAllowanceRepository.findBySalaryStructureIdAndAllowanceId(salaryId, allowanceId)
                .orElseThrow(()-> new ResourceNotFoundException(SALARY_ALLOWANCE, salaryId));

        return this.salaryAllowanceMapper.toResponse(requestedSalaryAllowance);
    }

    @Transactional
    @Override
    public SalaryAllowanceResponse save(UUID salaryId,SalaryAllowanceRequest request) {
        SalaryStructure salaryStructure = this.salaryStructureRepository.findById(salaryId)
                .orElseThrow(()-> new ResourceNotFoundException(SALARY_STRUCTURE, salaryId));

        Allowance allowance = this.allowanceRepository.findById(request.allowanceId())
                .orElseThrow(()-> new ResourceNotFoundException(ALLOWANCE, request.allowanceId()));

        new FieldDuplicateValidator()
                .addField(salaryAllowanceRepository.existsBySalaryStructureIdAndAllowanceId(
                        salaryId, request.allowanceId()
                ), "salaryStructureId and allowanceId")
                .validate(SALARY_ALLOWANCE);

        SalaryAllowance newSalaryAllowance = new SalaryAllowance(
                salaryStructure,
                allowance,
                request.effectiveDate(),
                request.expiryDate(),
                TenantContext.getTenantId()
        );

        SalaryAllowance savedSalaryAllowance = this.salaryAllowanceRepository.save(newSalaryAllowance);
        return this.salaryAllowanceMapper.toResponse(savedSalaryAllowance);
    }

    @Transactional
    @Override
    public SalaryAllowanceResponse updateEffectiveDates(UUID salaryAllowanceId, SalaryAllowanceDatesUpdateRequest request) {
        SalaryAllowance salaryAllowance = this.salaryAllowanceRepository.findById(salaryAllowanceId)
                .orElseThrow(()-> new ResourceNotFoundException(SALARY_ALLOWANCE, salaryAllowanceId));

        salaryAllowance.updateEffectiveDates(
                request.effectiveDate(),
                request.expiryDate()
        );

        return this.salaryAllowanceMapper.toResponse(salaryAllowance);
    }

}
