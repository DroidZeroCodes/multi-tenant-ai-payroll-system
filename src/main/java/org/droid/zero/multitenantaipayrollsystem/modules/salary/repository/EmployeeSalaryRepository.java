package org.droid.zero.multitenantaipayrollsystem.modules.salary.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, UUID>, JpaSpecificationExecutor<EmployeeSalary> {
   boolean existsBySalaryStructureIdAndEmployeeId(UUID salaryStructureId, UUID employeeId);
}