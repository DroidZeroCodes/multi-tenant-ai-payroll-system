package org.droid.zero.multitenantaipayrollsystem.modules.tenant;

import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.mapper.TenantMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.service.TenantServiceImpl;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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
class TenantServiceTest extends BaseUnitTest {
    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private final TenantMapper tenantMapper = Mappers.getMapper(TenantMapper.class);
    private TenantServiceImpl tenantService;

    private Tenant tenant;
    private TenantRequest tenantRequest;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantService = new TenantServiceImpl(tenantRepository, tenantMapper, applicationEventPublisher);

        tenant = new Tenant(
                "Tenant Name",
                "test@example.com",
                "11111111111",
                "Test Industry"
        );
        tenant.setId(UUID.randomUUID());
        tenantId = tenant.getId();

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
        when(tenantRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(tenantRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(tenantRepository.existsByPhone(anyString())).thenReturn(false);
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

        // Verify event was published
        ArgumentCaptor<TenantCreatedEvent> eventCaptor = ArgumentCaptor.forClass(TenantCreatedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        TenantCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(tenantId, capturedEvent.tenantId());
        assertEquals(tenantRequest.name(), capturedEvent.name());
        assertEquals(tenantRequest.email(), capturedEvent.email());
        assertTrue(capturedEvent.generatedPassword().startsWith("admin" + tenantRequest.email()));

        verify(tenantRepository, times(1)).save(any(Tenant.class));
        verify(tenantRepository, times(1)).existsByNameIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByEmailIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByPhone(anyString());
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
                .containsExactlyInAnyOrder("name", "contactEmail", "phone");

        assertThat(thrown.getResourceType())
                .isEqualTo(TENANT);

        verify(tenantRepository, times(1)).existsByNameIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByEmailIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByPhone(anyString());
        verify(tenantRepository, never()).save(any(Tenant.class));
        verify(applicationEventPublisher, never()).publishEvent(any(TenantCreatedEvent.class));
    }

    @Test
    void save_shouldThrowException_whenNameAlreadyExists() {
        //Arrange
        when(tenantRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);
        when(tenantRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(tenantRepository.existsByPhone(anyString())).thenReturn(false);

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
                .containsExactly("name");

        verify(tenantRepository, times(1)).existsByNameIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByEmailIgnoreCase(anyString());
        verify(tenantRepository, times(1)).existsByPhone(anyString());
    }

    @Test
    void updateTenant_shouldReturnTenant_whenRequestIsValid() {
        //Arrange
        Tenant existingTenant = new Tenant(
                "oldTenantName",
                "old@contactEmail.com",
                "222222222",
                "oldIndustry"
        );
        existingTenant.setId(UUID.randomUUID());
        UUID existingTenantId = existingTenant.getId();

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));
        when(tenantRepository.existsByNameIgnoreCaseAndIdNot(anyString(), any(UUID.class))).thenReturn(false);
        when(tenantRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), any(UUID.class))).thenReturn(false);
        when(tenantRepository.existsByPhoneAndIdNot(anyString(), any(UUID.class))).thenReturn(false);

        //Act
        TenantResponse updatedTenantResponse = tenantService.update(tenantRequest, existingTenantId);

        //Assert
        assertNotNull(updatedTenantResponse);
        assertEquals(existingTenantId, updatedTenantResponse.id());
        assertEquals(tenantRequest.name(), updatedTenantResponse.name());
        assertEquals(tenantRequest.email(), updatedTenantResponse.email());
        assertEquals(tenantRequest.phone(), updatedTenantResponse.phone());
        assertEquals(tenantRequest.industry(), updatedTenantResponse.industry());
        assertTrue(updatedTenantResponse.active());

        verify(tenantRepository, times(1)).findById(existingTenantId);
        verify(tenantRepository, times(1)).existsByNameIgnoreCaseAndIdNot(anyString(), any(UUID.class));
        verify(tenantRepository, times(1)).existsByEmailIgnoreCaseAndIdNot(anyString(), any(UUID.class));
        verify(tenantRepository, times(1)).existsByPhoneAndIdNot(anyString(), any(UUID.class));
        verify(applicationEventPublisher, never()).publishEvent(any(TenantCreatedEvent.class));
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
        when(tenantRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), any(UUID.class))).thenReturn(true);
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
        assertFalse(updatedStatus); // Toggled from true to false

        verify(tenantRepository, times(1)).findById(tenantId);
        verifyNoMoreInteractions(tenantRepository);
    }

    @Test
    void toggleTenantStatus_ShouldToggleMultipleTimes() {
        //Arrange
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        //Act - First toggle
        boolean firstToggle = tenantService.toggleTenantStatus(tenantId);
        // Simulate the second toggle by changing the tenant's state
        boolean secondToggle = tenantService.toggleTenantStatus(tenantId);

        //Assert
        assertFalse(firstToggle); // true -> false
        assertTrue(secondToggle); // false -> true

        verify(tenantRepository, times(2)).findById(tenantId);
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

    @Test
    void updateTenant_shouldUpdateIndustry_whenRequestIsValid() {
        //Arrange
        Tenant existingTenant = new Tenant(
                "oldTenantName",
                "old@contactEmail.com",
                "222222222",
                "oldIndustry"
        );
        existingTenant.setId(UUID.randomUUID());
        UUID existingTenantId = existingTenant.getId();

        TenantRequest updateRequest = new TenantRequest(
                "newName",
                "new@contactEmail.com",
                "333333333",
                "newIndustry"
        );

        when(tenantRepository.findById(existingTenantId)).thenReturn(Optional.of(existingTenant));
        when(tenantRepository.existsByNameIgnoreCaseAndIdNot(anyString(), any(UUID.class))).thenReturn(false);
        when(tenantRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), any(UUID.class))).thenReturn(false);
        when(tenantRepository.existsByPhoneAndIdNot(anyString(), any(UUID.class))).thenReturn(false);

        //Act
        TenantResponse updatedTenant = tenantService.update(updateRequest, existingTenantId);

        //Assert
        assertEquals("newIndustry", updatedTenant.industry());
    }
}