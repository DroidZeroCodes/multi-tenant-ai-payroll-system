package org.droid.zero.multitenantaipayrollsystem.tenant;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;


@Service
@Transactional
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantResponse findById(UUID tenantId) {
        //Find the Tenant by its ID then map it to the response object if exists, if not, throw an exception
        return this.tenantRepository.findById(tenantId)
                .map(tenantMapper::toResponse)
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, tenantId));
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

        //Map the saved model to a response object, then return
        return tenantMapper.toResponse(savedTenant);
    }

    @Override
    public TenantResponse update(TenantRequest request, UUID tenantId) {
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
}
