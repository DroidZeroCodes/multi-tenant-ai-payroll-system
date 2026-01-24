package org.droid.zero.multitenantaipayrollsystem.modules.allowance.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AllowanceRepository extends JpaRepository<Allowance, UUID> {
    boolean existsByNameIgnoreCase(String name);
}