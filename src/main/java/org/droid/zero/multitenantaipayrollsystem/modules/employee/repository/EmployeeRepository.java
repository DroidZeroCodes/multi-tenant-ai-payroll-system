package org.droid.zero.multitenantaipayrollsystem.modules.employee.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    int countByJoiningDate(LocalDate joinDate);

    boolean existsByEmployeeCode(String employeeCode);
}