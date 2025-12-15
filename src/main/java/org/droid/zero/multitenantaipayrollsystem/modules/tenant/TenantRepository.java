package org.droid.zero.multitenantaipayrollsystem.modules.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    boolean existsByPhoneAndIdNot(String phone, UUID id);

    Optional<Tenant> findByEmailIgnoreCase(String mail);
}