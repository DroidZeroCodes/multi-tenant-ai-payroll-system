package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.tenant.mapper.TenantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {
    @Mock
    private TenantRepository tenantRepository;

    private final TenantMapper tenantMapper = Mappers.getMapper(TenantMapper.class);

    private TenantServiceImpl tenantService;

    @BeforeEach
    void setUp() {
        tenantService = new TenantServiceImpl(tenantRepository, tenantMapper);
    }

    @Test
    void testFindById_Success() {
        //Arrange
        UUID existingTenantId = UUID.randomUUID();
        Tenant existingTenant = new Tenant();
        existingTenant.setId(existingTenantId);
        existingTenant.setName("existingTenantName");
        existingTenant.setEmail("existing@email.com");
        existingTenant.setPhone("11111111111");

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));

        //Act
        TenantResponse foundTenant = tenantService.findById(existingTenantId);

        //Assert
        assertEquals(existingTenantId, foundTenant.id());
        assertEquals("existingTenantName", foundTenant.name());
        assertEquals("existing@email.com", foundTenant.email());
        assertEquals("11111111111", foundTenant.phone());
        assertTrue(foundTenant.active());
    }

    @Test
    void testFindById_NotFound() {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();

        when(tenantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //Act
        Throwable thrown = catchThrowable(()-> tenantService.findById(nonExistentId));

        //Assert
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find TENANT with ID '" + nonExistentId + "'.");
    }

    @Test
    void testSaveTenant_Success() {
        //Arrange
        TenantRequest request = new TenantRequest(
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

        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

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

    @Test
    void testSaveTenant_UniquenessViolation() {
        //Arrange
        TenantRequest request = new TenantRequest(
                "testName",
                "testEmail",
                "1234567890",
                "technology"
        );

        when(tenantRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);
        when(tenantRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);
        when(tenantRepository.existsByPhone(anyString())).thenReturn(true);

        //Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> tenantService.save(request)
        );

        //Assert
        assertThat(thrown)
                .isNotNull()
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("An existing TENANT already exists with the provided arguments.");

        assertThat(thrown.getFields())
                .containsExactlyInAnyOrder("name", "email", "phone");

        assertThat(thrown.getResourceType())
                .isEqualTo(TENANT);
    }

    @Test
    void testUpdateTenant_Success() {
        //Arrange
        UUID existingTenantId = UUID.randomUUID();
        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "1234567890",
                "technology"
        );

        Tenant existingTenant = new Tenant();
        existingTenant.setId(existingTenantId);
        existingTenant.setName("oldTenantName");
        existingTenant.setEmail("old@email.com");
        existingTenant.setPhone("111111111");
        existingTenant.setIndustry("oldIndustry");

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));

        //Act
        TenantResponse updatedTenantResponse = tenantService.update(request, existingTenantId);

        //Assert
        assertNotNull(updatedTenantResponse);
        assertEquals(existingTenantId, updatedTenantResponse.id());
        assertEquals(existingTenant.getName(), updatedTenantResponse.name());
        assertEquals(existingTenant.getEmail(), updatedTenantResponse.email());
        assertEquals(existingTenant.getPhone(), updatedTenantResponse.phone());
        assertTrue(updatedTenantResponse.active());
    }

    @Test
    void testUpdateTenant_NotFound() {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();
        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "1234567890",
                "technology"
        );

        when(tenantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //Act
        Throwable thrown = catchThrowable(()-> tenantService.update(request,nonExistentId));

        //Assert
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find TENANT with ID '" + nonExistentId + "'.");
    }

    @Test
    void testSaveUpdate_UniquenessViolation() {
        //Arrange
        UUID existingTenantId = UUID.randomUUID();
        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "1234567890",
                "technology"
        );

        Tenant existingTenant = new Tenant();
        existingTenant.setId(existingTenantId);
        existingTenant.setName("oldTenantName");
        existingTenant.setEmail("old@email.com");
        existingTenant.setPhone("111111111");
        existingTenant.setIndustry("oldIndustry");

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));
        when(tenantRepository.existsByNameIgnoreCaseAndIdNot(anyString(), any())).thenReturn(true);
        when(tenantRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), any())).thenReturn(true);
        when(tenantRepository.existsByPhoneAndIdNot(anyString(), any())).thenReturn(true);

        //Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> tenantService.update(request, existingTenantId)
        );

        //Assert
        assertThat(thrown)
                .isNotNull()
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("An existing TENANT already exists with the provided arguments.");

        assertThat(thrown.getFields())
                .containsExactlyInAnyOrder("name", "email", "phone");

        assertThat(thrown.getResourceType())
                .isEqualTo(TENANT);
    }


    @Test
    void testToggleTenantStatus_Success() {
        //Arrange
        UUID existingTenantId = UUID.randomUUID();
        Tenant existingTenant = new Tenant();
        existingTenant.setId(existingTenantId);
        existingTenant.setName("oldTenantName");
        existingTenant.setEmail("old@email.com");
        existingTenant.setPhone("111111111");
        existingTenant.setIndustry("oldIndustry");
        existingTenant.setActive(true);

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));

        //Act
        boolean updatedStatus = tenantService.toggleTenantStatus(existingTenantId);

        //Assert
        assertFalse(updatedStatus);
        assertEquals("oldTenantName", existingTenant.getName());
        assertEquals("old@email.com", existingTenant.getEmail());
        assertEquals("111111111", existingTenant.getPhone());
        assertEquals("oldIndustry", existingTenant.getIndustry());
    }

    @Test
    void testToggleTenantStatus_NotFound() {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();

        when(tenantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //Act
        Throwable thrown = catchThrowable(()-> tenantService.toggleTenantStatus(nonExistentId));

        //Assert
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find TENANT with ID '" + nonExistentId + "'.");
    }
}