package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.repository.AllowanceRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper.SalaryStructureMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryAllowance;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryStructureRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.ALLOWANCE;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.SALARY_STRUCTURE;


@Service
@RequiredArgsConstructor
public class SalaryStructureServiceImpl extends BaseService implements SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final AllowanceRepository allowanceRepository;
    private final SalaryStructureMapper salaryStructureMapper;

    @Transactional
    @Override
    public SalaryStructureResponse findById(UUID salaryId) {
        SalaryStructure requestedSalaryStructure = this.salaryStructureRepository.findById(salaryId)
                .orElseThrow(() -> new ResourceNotFoundException(SALARY_STRUCTURE, salaryId));

        return this.salaryStructureMapper.toResponse(requestedSalaryStructure);
    }

    @Transactional
    @Override
    public SalaryStructureResponse save(SalaryStructureRequest request) {
        new FieldDuplicateValidator()
                .addField(salaryStructureRepository.existsByNameIgnoreCase(request.name()), "name")
                .validate(SALARY_STRUCTURE);

        List<UUID> allowanceIds = request.allowances()
                .stream()
                .map(SalaryAllowanceRequest::allowanceId)
                .toList();

        //Create Salary Structure
        SalaryStructure newSalaryStructure = new SalaryStructure(
                request.name(),
                request.description(),
                request.baseSalary(),
                request.effectiveDate(),
                TenantContext.getTenantId()
        );

        //Validate Allowances
        if (!allowanceIds.isEmpty()) {
            List<Allowance> allowances = allowanceRepository.findAllById(allowanceIds);

            //If the size of the existing allowances is not equal to the size of the request allowance ids, throw an exception
            if (allowances.size() != allowanceIds.size())
                throw new ResourceNotFoundException(ALLOWANCE, allowanceIds
                        .stream()
                        .filter(id -> allowances
                                .stream()
                                .noneMatch(al -> al
                                        .getId()
                                        .equals(id))
                        )
                        .collect(Collectors.toList())
                );

            Set<SalaryAllowance> salaryAllowances = allowances.stream().map(allowance -> {
                        SalaryAllowanceRequest allowanceRequest = request.allowances()
                                .stream()
                                .filter(al -> al.allowanceId().equals(allowance.getId()))
                                .findFirst()
                                .orElse(null);

                        if (allowanceRequest == null)
                            throw new ResourceNotFoundException(ALLOWANCE, allowance.getId());

                        return new SalaryAllowance(
                                newSalaryStructure,
                                allowance,
                                allowanceRequest.effectiveDate(),
                                allowanceRequest.expiryDate(),
                                TenantContext.getTenantId()
                        );
                    }
            ).collect(Collectors.toSet());

            newSalaryStructure.addAllowances(salaryAllowances);
        }

        SalaryStructure savedSalaryStructure = this.salaryStructureRepository.save(newSalaryStructure);
        return this.salaryStructureMapper.toResponse(savedSalaryStructure);
    }

    @Transactional
    @Override
    public SalaryStructureResponse update(SalaryStructureRequest request, UUID salaryId) {
        //Find the Salary from the repository
        SalaryStructure existingSalaryStructure = this.salaryStructureRepository.findById(salaryId)
                .orElseThrow(() -> new ResourceNotFoundException(SALARY_STRUCTURE, salaryId));

        //Update the salary
        existingSalaryStructure.updateSalaryDetails(
                request.name(),
                request.description(),
                request.baseSalary(),
                request.effectiveDate()
        );

        return this.salaryStructureMapper.toResponse(existingSalaryStructure);
    }

    @Transactional
    @Override
    public void toggleSalaryStatus(UUID salaryId) {
        SalaryStructure existingSalaryStructure = this.salaryStructureRepository.findById(salaryId)
                .orElseThrow(() -> new ResourceNotFoundException(SALARY_STRUCTURE, salaryId));

        existingSalaryStructure.toggleActiveStatus();
    }
}
