package org.droid.zero.multitenantaipayrollsystem.modules.salary.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, UUID> {
   boolean existsBySalaryStructureIdAndEmployeeId(UUID salaryStructureId, UUID employeeId);

    Optional<EmployeeSalary> findBySalaryStructureIdAndEmployeeId(UUID salaryStructureId, UUID employeeId);

    Optional<EmployeeSalary> findFirst(Specification<EmployeeSalary> specification);
}