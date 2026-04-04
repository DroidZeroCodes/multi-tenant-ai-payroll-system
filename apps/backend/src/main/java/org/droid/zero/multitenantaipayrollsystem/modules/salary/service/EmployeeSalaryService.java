package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryChangeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface EmployeeSalaryService {

    EmployeeSalaryResponse findById(UUID employeeSalaryId);

    EmployeeSalaryResponse findByEmployeeIdAndDate(UUID employeeId, LocalDate date);

    EmployeeSalaryResponse save(EmployeeSalaryRequest employeeSalaryRequest);

    EmployeeSalaryResponse changeSalary(UUID employeeSalaryId, EmployeeSalaryChangeRequest request);

    EmployeeSalaryResponse updateEffectiveDate(UUID employeeSalaryId, LocalDate effectiveDate);

    EmployeeSalaryResponse updateEndDate(UUID employeeSalaryId, LocalDate endDate);

}
