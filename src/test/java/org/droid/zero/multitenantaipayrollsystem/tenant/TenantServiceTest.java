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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {
    @Mock
    private TenantRepository tenantRepository;

    private final TenantMapper tenantMapper = Mappers.getMapper(TenantMapper.class);

    private TenantServiceImpl tenantService;

    private Tenant tenant;
    private TenantRequest tenantRequest;
    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        tenantService = new TenantServiceImpl(tenantRepository, tenantMapper);

        tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setName("Tenant Name");
        tenant.setEmail("test@example.com");
        tenant.setPhone("11111111111");
        tenant.setIndustry("Test Industry");

        tenantRequest = new TenantRequest(
                "Tenant Name",
                "test@example.com",
                "11111111111",
                "Test Industry"
        );
    }

    @Test
    void findById_shouldReturnTenant_whenUserExists() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        //Act
        TenantResponse foundTenant = tenantService.findById(tenantId);

        //Assert
        assertEquals(tenantId, foundTenant.id());
        assertEquals("Tenant Name", foundTenant.name());
        assertEquals("test@example.com", foundTenant.email());
        assertEquals("11111111111", foundTenant.phone());
        assertEquals("Test Industry", foundTenant.industry());
        assertTrue(foundTenant.active());

        verify(tenantRepository, times(1)).findById(tenantId);
    }

    @Test
    void findById_shouldThrowException_whenTenantDoesNotExist() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        //Act
        Throwable thrown = catchThrowable(()-> tenantService.findById(tenantId));

        //Assert
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find TENANT with ID '" + tenantId + "'.");

        verify(tenantRepository, times(1)).findById(tenantId);
    }

    @Test
    void save_shouldSaveAndReturnTenant_whenRequestIsValid() {
        //Arrange
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        //Act
        TenantResponse savedTenant = tenantService.save(tenantRequest);

        //Assert
        assertNotNull(savedTenant);
        assertEquals(tenantId, savedTenant.id());
        assertEquals(tenant.getName(), savedTenant.name());
        assertEquals(tenant.getEmail(), savedTenant.email());
        assertEquals(tenant.getPhone(), savedTenant.phone());
        assertTrue(savedTenant.active());

        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    void save_shouldThrowException_whenNameEmailPhoneAlreadyExists() {
        //Arrange
        when(tenantRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);
        when(tenantRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);
        when(tenantRepository.existsByPhone(anyString())).thenReturn(true);

        //Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> tenantService.save(tenantRequest)
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

        verify(tenantRepository, times(1)).existsByNameIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByEmailIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByPhone(anyString());
        verifyNoMoreInteractions(tenantRepository);
    }

    @Test
    void updateTenant_shouldReturnTenant_whenRequestIsValid() {
        //Arrange
        UUID existingTenantId = UUID.randomUUID();
        Tenant existingTenant = new Tenant();
        existingTenant.setId(existingTenantId);
        existingTenant.setName("oldTenantName");
        existingTenant.setEmail("old@email.com");
        existingTenant.setPhone("222222222");
        existingTenant.setIndustry("oldIndustry");

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));

        //Act
        TenantResponse updatedTenantResponse = tenantService.update(tenantRequest, existingTenantId);

        //Assert
        assertNotNull(updatedTenantResponse);
        assertEquals(existingTenantId, updatedTenantResponse.id());
        assertEquals(existingTenant.getName(), updatedTenantResponse.name());
        assertEquals(existingTenant.getEmail(), updatedTenantResponse.email());
        assertEquals(existingTenant.getPhone(), updatedTenantResponse.phone());
        assertTrue(updatedTenantResponse.active());

        verify(tenantRepository, times(1)).findById(existingTenantId);
        verify(tenantRepository, times(1)).existsByNameIgnoreCaseAndIdNot(anyString(), any(UUID.class));
        verify(tenantRepository, times(1)).existsByEmailIgnoreCaseAndIdNot(anyString(), any(UUID.class));
        verify(tenantRepository, times(1)).existsByPhoneAndIdNot(anyString(), any(UUID.class));
        verifyNoMoreInteractions(tenantRepository);
    }

    @Test
    void updateTenant_shouldThrowException_whenTenantDoesNotExist() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        //Act
        Throwable thrown = catchThrowable(()-> tenantService.update(tenantRequest,tenantId));

        //Assert
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find TENANT with ID '" + tenantId + "'.");

        verify(tenantRepository, times(1)).findById(tenantId);
        verifyNoMoreInteractions(tenantRepository);
    }

    @Test
    void updateTenant_shouldThrowException_whenNameEmailPhoneAlreadyExists() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(tenantRepository.existsByNameIgnoreCaseAndIdNot(anyString(), any(UUID.class))).thenReturn(true);
        when(tenantRepository.existsByEmailIgnoreCaseAndIdNot(anyString(),any(UUID.class))).thenReturn(true);
        when(tenantRepository.existsByPhoneAndIdNot(anyString(), any(UUID.class))).thenReturn(true);

        //Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> tenantService.update(tenantRequest, tenantId)
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


        verify(tenantRepository, times(1)).findById(tenantId);
        verify(tenantRepository, times(1)).existsByNameIgnoreCaseAndIdNot(anyString(), any(UUID.class));
        verify(tenantRepository, times(1)).existsByEmailIgnoreCaseAndIdNot(anyString(), any(UUID.class));
        verify(tenantRepository, times(1)).existsByPhoneAndIdNot(anyString(), any(UUID.class));
        verifyNoMoreInteractions(tenantRepository);
    }


    @Test
    void toggleTenantStatus_ShouldSaveAndReturnStatus_whenRequestIsValid() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        //Act
        boolean updatedStatus = tenantService.toggleTenantStatus(tenantId);

        //Assert
        assertFalse(updatedStatus);

        verify(tenantRepository, times(1)).findById(tenantId);
        verifyNoMoreInteractions(tenantRepository);
    }

    @Test
    void testToggleTenantStatus_NotFound() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        //Act
        Throwable thrown = catchThrowable(()-> tenantService.toggleTenantStatus(tenantId));

        //Assert
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find TENANT with ID '" + tenantId + "'.");

        verify(tenantRepository, times(1)).findById(tenantId);
        verifyNoMoreInteractions(tenantRepository);
    }
}