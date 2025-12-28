package org.droid.zero.multitenantaipayrollsystem.modules.tenant.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.mapper.TenantMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;


@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends BaseService implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public TenantResponse findById(UUID tenantId) {
        Tenant requestedTenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, tenantId));

        return tenantMapper.toResponse(requestedTenant);
    }

    @Transactional
    @Override
    public TenantResponse findCurrent() {
        return this.findById(TenantContext.getTenantId());
    }

    @Transactional
    @Override
    public TenantResponse save(TenantRequest request) {
        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(tenantRepository.existsByNameIgnoreCase(request.name()), "name")
                .addField(tenantRepository.existsByEmailIgnoreCase(request.email()), "contactEmail")
                .addField(tenantRepository.existsByPhone(request.phone()), "phone")
                .validate(TENANT);

        //Convert request to an entity to be persisted
        Tenant tenant = new Tenant(
                request.name(),
                request.email(),
                request.phone(),
                request.industry()
        );

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

    @Transactional
    @Override
    public TenantResponse update(TenantRequest request, UUID tenantId) {
        User currentUser = getCurrentUser();

        UUID activeTenant = TenantContext.getTenantId();

        if (currentUser.getActiveRoles().contains(UserRole.TENANT_ADMIN)){
            if (!activeTenant.equals(tenantId)) throw new ObjectNotFoundException(TENANT, tenantId);
        }

        //Find the tenant to update, else throw an exception
        Tenant existingTenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, tenantId));

        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(tenantRepository.existsByNameIgnoreCaseAndIdNot(request.name(), existingTenant.getId()), "name")
                .addField(tenantRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), existingTenant.getId()), "email")
                .addField(tenantRepository.existsByPhoneAndIdNot(request.phone(), existingTenant.getId()), "phone")
                .validate(TENANT);

        //Update the fields
        existingTenant.updateTenant(request.name(),request.email(), request.phone(), request.industry());

        //Directly returning the mapped model since it automatically persists the changes to the database
        return tenantMapper.toResponse(existingTenant);
    }

    @Transactional
    @Override
    public boolean toggleTenantStatus(UUID tenantId) {
        //Find the tenant to update, else throw an exception
        Tenant existingTenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, tenantId));

        return existingTenant.toggleActiveStatus();
    }
}
