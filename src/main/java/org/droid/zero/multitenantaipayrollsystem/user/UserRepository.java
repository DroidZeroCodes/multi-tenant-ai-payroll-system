package org.droid.zero.multitenantaipayrollsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCaseAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmailIgnoreCase(String email);
}