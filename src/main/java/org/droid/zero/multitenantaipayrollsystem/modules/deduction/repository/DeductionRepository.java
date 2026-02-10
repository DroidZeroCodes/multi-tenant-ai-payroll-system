package org.droid.zero.multitenantaipayrollsystem.modules.deduction.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.deduction.model.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, UUID> {
    boolean existsByNameIgnoreCase(String name);
}