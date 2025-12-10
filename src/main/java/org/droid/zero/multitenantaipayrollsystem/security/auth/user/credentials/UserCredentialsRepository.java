package org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {

    Optional<UserCredentials> findByEmailIgnoreCase(String email);

    Optional<UserCredentials> findByUserId(UUID userId);
}