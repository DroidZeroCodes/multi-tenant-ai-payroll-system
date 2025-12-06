package org.droid.zero.multitenantaipayrollsystem.tenant;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;
import static org.droid.zero.multitenantaipayrollsystem.system.util.ResourceUtils.checkDuplicate;


@Service
@Transactional
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantResponse save(CreateTenantRequest request) {
        List<String> duplicateFields = new ArrayList<>();

        checkDuplicate(tenantRepository.existsByNameIgnoreCase(request.name()), "name",duplicateFields);
        checkDuplicate(tenantRepository.existsByEmailIgnoreCase(request.email()), "email",duplicateFields);
        checkDuplicate(tenantRepository.existsByPhone(request.phone()), "phone",duplicateFields);

        if (!duplicateFields.isEmpty()) throw new DuplicateResourceException(TENANT, duplicateFields);

        Tenant tenant = tenantMapper.toEntity(request);
        Tenant savedTenant = this.tenantRepository.save(tenant);
        return tenantMapper.toResponse(savedTenant);
    }
}
