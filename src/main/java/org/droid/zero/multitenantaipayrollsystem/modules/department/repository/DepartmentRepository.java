package org.droid.zero.multitenantaipayrollsystem.modules.department.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Optional<Department> findByIdAndTenantId(UUID departmentId, UUID tenantId);

    boolean existsByNameIgnoreCase(String name);

}