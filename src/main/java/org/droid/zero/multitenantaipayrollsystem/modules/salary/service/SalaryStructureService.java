package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureResponse;

import java.util.UUID;

public interface SalaryStructureService {

    SalaryStructureResponse findById(UUID id);

    SalaryStructureResponse save(SalaryStructureRequest salary);

    SalaryStructureResponse update(SalaryStructureRequest salary, UUID salaryId);

    void toggleSalaryStatus(UUID salaryId);
}
