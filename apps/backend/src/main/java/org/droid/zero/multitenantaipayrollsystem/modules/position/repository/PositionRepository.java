package org.droid.zero.multitenantaipayrollsystem.modules.position.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    boolean existsByTitleIgnoreCase(String title);
}