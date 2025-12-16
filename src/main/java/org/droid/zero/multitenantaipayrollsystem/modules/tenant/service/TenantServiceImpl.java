package org.droid.zero.multitenantaipayrollsystem.modules.tenant.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.mapper.TenantMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole.SUPER_ADMIN;
import static org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole.TENANT_ADMIN;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;


@Service
@Transactional
@RequiredArgsConstructor
public class TenantServiceImpl extends BaseService implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Tenant findById(UUID tenantId) {
        UserCredentials currentUser = getCurrentUser();

        Tenant requestedTenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, tenantId));

        checkReadPermission(currentUser,  requestedTenant);

        return requestedTenant;
    }

    @Override
    public TenantResponse findByIdResponse(UUID tenantId) {
        //Use the findById method and map the result to the response dto then return
        return tenantMapper.toResponse(findById(tenantId));
    }

    @Override
    public TenantResponse save(TenantRequest request) {
        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(tenantRepository.existsByNameIgnoreCase(request.name()), "name")
                .addField(tenantRepository.existsByEmailIgnoreCase(request.email()), "email")
                .addField(tenantRepository.existsByPhone(request.phone()), "phone")
                .validate(TENANT);

        //Convert request to an entity to be persisted
        Tenant tenant = tenantMapper.toEntity(request);

        //Create the new record in the database
        Tenant savedTenant = this.tenantRepository.save(tenant);

        //Publish tenant creation event
        eventPublisher.publishEvent(new TenantCreatedEvent(
                savedTenant.getId(),
                request.name(),
                request.email(),
                "admin" + request.email() + UUID.randomUUID().getLeastSignificantBits()
        ));

        //Map the saved model to a response object, then return
        return tenantMapper.toResponse(savedTenant);
    }

    @Override
    public TenantResponse update(TenantRequest request, UUID tenantId) {
        UserCredentials currentUser = getCurrentUser();

        //Find the tenant to update, else throw an exception
        Tenant existingTenant = this.findById(tenantId);

        checkReadPermission(currentUser, existingTenant);

        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(tenantRepository.existsByNameIgnoreCaseAndIdNot(request.name(), existingTenant.getId()), "name")
                .addField(tenantRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), existingTenant.getId()), "email")
                .addField(tenantRepository.existsByPhoneAndIdNot(request.phone(), existingTenant.getId()), "phone")
                .validate(TENANT);

        //Update the fields
        existingTenant.setName(request.name());
        existingTenant.setEmail(request.email());
        existingTenant.setPhone(request.phone());
        existingTenant.setIndustry(request.industry());

        //Directly returning the mapped model since it automatically persists the changes to the database
        return tenantMapper.toResponse(existingTenant);
    }

    @Override
    public boolean toggleTenantStatus(UUID tenantId) {
        //Find the tenant to update, else throw an exception
        Tenant existingTenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, tenantId));

        return existingTenant.toggleActiveStatus();
    }

    private void checkReadPermission(UserCredentials currentUser, Tenant targetTenant) {
        // 1. Super Admin: Allow everything
        if (currentUser.getRole().contains(SUPER_ADMIN)) return;

        // 2. Tenant Admin: Allow if the same tenant
        if (currentUser.getRole().contains(TENANT_ADMIN)) {
            if (targetTenant.getId().equals(currentUser.getTenant().getId())) {
                return;
            }
        }

        // 3. Previous check failed or if Regular User: Do not allow
        throw new AccessDeniedException("You do not have access to this resource.");
    }
}
