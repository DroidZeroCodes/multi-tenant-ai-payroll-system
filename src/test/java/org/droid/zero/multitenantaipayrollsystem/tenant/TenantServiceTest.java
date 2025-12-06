package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.tenant.mapper.TenantMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private TenantMapper tenantMapper;
    @InjectMocks
    private TenantServiceImpl tenantService;

    @Test
    void testSaveTenant_Success() {
        //Arrange
        CreateTenantRequest request = new CreateTenantRequest(
                "testName",
                "testEmail",
                "1234567890",
                "technology"
        );

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setName(request.name());
        tenant.setEmail(request.email());
        tenant.setPhone(request.phone());
        tenant.setIndustry(request.industry());

        TenantResponse expectedResponse = new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getEmail(),
                tenant.getPhone(),
                tenant.getIndustry(),
                true
        );

        when(tenantMapper.toEntity(request)).thenReturn(tenant);
        when(tenantRepository.save(tenant)).thenReturn(tenant);
        when(tenantMapper.toResponse(tenant)).thenReturn(expectedResponse);

        //Act
        TenantResponse savedTenant = tenantService.save(request);

        //Assert
        assertNotNull(savedTenant);
        assertNotNull(savedTenant.id());
        assertEquals(tenant.getName(), savedTenant.name());
        assertEquals(tenant.getEmail(), savedTenant.email());
        assertEquals(tenant.getPhone(), savedTenant.phone());
        assertTrue(savedTenant.active());
    }
}