package org.droid.zero.multitenantaipayrollsystem.modules.user;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCaseAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmailIgnoreCase(String email);

    @SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
    Set<User> findByTenantIdAndUserCredentialsRoleIn(
            UUID tenant_id,
            @NotEmpty(message = "role is required") Set<UserRole> userCredentials_role
    );
}