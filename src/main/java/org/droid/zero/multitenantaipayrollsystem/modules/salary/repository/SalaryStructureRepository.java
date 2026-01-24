package org.droid.zero.multitenantaipayrollsystem.modules.salary.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, UUID> {

    boolean existsByNameIgnoreCase(String title);
}