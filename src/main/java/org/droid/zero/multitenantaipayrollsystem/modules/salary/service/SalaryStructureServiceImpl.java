package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper.SalaryStructureMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryStructureRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.SALARY_STRUCTURE;


@Service
@RequiredArgsConstructor
public class SalaryStructureServiceImpl extends BaseService implements SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final SalaryStructureMapper salaryStructureMapper;

    @Transactional
    @Override
    public SalaryStructureResponse findById(UUID salaryId) {
        SalaryStructure requestedSalaryStructure = this.salaryStructureRepository.findById(salaryId)
                .orElseThrow(()-> new ObjectNotFoundException(SALARY_STRUCTURE, salaryId));

        return this.salaryStructureMapper.toResponse(requestedSalaryStructure);
    }

    @Transactional
    @Override
    public SalaryStructureResponse save(SalaryStructureRequest request) {
        new FieldDuplicateValidator()
                .addField(salaryStructureRepository.existsByNameIgnoreCase(request.name()), "name")
                .validate(SALARY_STRUCTURE);

        SalaryStructure newSalaryStructure = new SalaryStructure(
                request.name(),
                request.description(),
                request.baseSalary(),
                request.effectiveDate(),
                TenantContext.getTenantId()
        );

        SalaryStructure savedSalaryStructure = this.salaryStructureRepository.save(newSalaryStructure);
        return this.salaryStructureMapper.toResponse(savedSalaryStructure);
    }

    @Transactional
    @Override
    public SalaryStructureResponse update(SalaryStructureRequest request, UUID salaryId) {
        //Find the Salary from the repository
        SalaryStructure existingSalaryStructure = this.salaryStructureRepository.findById(salaryId)
                .orElseThrow(()-> new ObjectNotFoundException(SALARY_STRUCTURE, salaryId));

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
                .orElseThrow(()-> new ObjectNotFoundException(SALARY_STRUCTURE, salaryId));

        existingSalaryStructure.toggleActiveStatus();
    }
}
