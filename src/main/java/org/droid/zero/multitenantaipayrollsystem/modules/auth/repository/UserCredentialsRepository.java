package org.droid.zero.multitenantaipayrollsystem.modules.auth.repository;

import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {

    Optional<UserCredentials> findByUserId(UUID userId);

}