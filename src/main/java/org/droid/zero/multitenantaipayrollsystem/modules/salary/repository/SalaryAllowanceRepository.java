package org.droid.zero.multitenantaipayrollsystem.modules.salary.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryAllowanceRepository extends JpaRepository<SalaryAllowance, UUID> {
   boolean existsBySalaryStructureIdAndAllowanceId(UUID salaryStructureId, UUID allowanceId);

    Optional<SalaryAllowance> findBySalaryStructureIdAndAllowanceId(UUID salaryStructureId, UUID allowanceId);
}