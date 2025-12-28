package org.droid.zero.multitenantaipayrollsystem.modules.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdAndUserTenantRoles_TenantId(UUID userId, UUID tenantId);

    boolean existsByContactEmailIgnoreCaseAndUserTenantRoles_TenantId(@NotBlank(message = "contactEmail is required") @Email(message = "invalid contactEmail format") String email, UUID tenantId);

    Optional<User> findByUserCredentials_EmailIgnoreCase(String userCredentialsEmail);

    Optional<User> findByUserCredentials_EmailIgnoreCase_AndUserTenantRoles_TenantId(String username, UUID tenantId);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.userTenantRoles utr " +
            "WHERE utr.tenantId = :tenantId " +
            "AND EXISTS (SELECT 1 FROM utr.roles r WHERE r IN :roles)")
    Set<User> findAllByTenantAndRoles(UUID tenantId, Set<UserRole> roles);

    Optional<User> findByContactEmail(String contactEmail);

}