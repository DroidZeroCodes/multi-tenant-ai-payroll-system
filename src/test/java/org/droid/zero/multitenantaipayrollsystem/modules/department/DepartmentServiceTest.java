package org.droid.zero.multitenantaipayrollsystem.modules.department;

import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.department.mapper.DepartmentMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.department.repository.DepartmentRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.department.service.DepartmentServiceImpl;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.DEPARTMENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest extends BaseUnitTest {

    @Mock
    private DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper = Mappers.getMapper(DepartmentMapper.class);;
    private final UUID departmentId = UUID.randomUUID();

    private UUID tenantId;
    private Department department;
    private DepartmentRequest departmentRequest;

    private DepartmentServiceImpl departmentService;
    
    @BeforeEach
    void setUp() {
        departmentService = new DepartmentServiceImpl(departmentRepository, departmentMapper);

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenantId = tenant.getId();
        TenantContext.setTenantId(tenant.getId());
        
        department = new Department(
                "Department Name",
                "Department Description",
                tenantId
        );
        department.setId(departmentId);
        
        departmentRequest = new DepartmentRequest(
                "Department Name",
                "Department Description"
        );
    }

    @Test
    void findById_shouldReturnDepartment_whenDepartmentExists() {
        // Arrange
        when(departmentRepository
                .findById(departmentId))
                .thenReturn(Optional.of(department));

        // Act
        DepartmentResponse foundDepartment = departmentService.findById(departmentId);

        // Assert
        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.id()).isEqualTo(departmentId);
        assertThat(foundDepartment.description()).isEqualTo(department.getDescription());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void findById_shouldThrowException_whenDepartmentDoesNotExist() {
        // Arrange
        when(departmentRepository
                .findById(departmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> departmentService.findById(departmentId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find DEPARTMENT with ID '" + departmentId + "'.");

        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void save_shouldSaveAndReturnDepartment_whenRequestIsValid() {
        // Arrange
//        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(departmentRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        
        // Act
        DepartmentResponse savedDepartment = departmentService.save(departmentRequest);

        // Assert
        assertNotNull(savedDepartment);
        assertEquals(departmentId, savedDepartment.id());
        assertEquals("Department Name", savedDepartment.name());
        assertEquals("Department Description", savedDepartment.description());
        assertFalse(savedDepartment.active());

        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
//        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(departmentRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        // Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> departmentService.save(departmentRequest)
        );

        // Assert
        assertThat(thrown)
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("An existing DEPARTMENT already exists with the provided arguments.");

        verify(departmentRepository, times(1)).existsByNameIgnoreCase(anyString());
        verifyNoMoreInteractions(departmentRepository);

        assertThat(thrown.getFields())
                .containsExactlyInAnyOrder("name");

        assertThat(thrown.getResourceType())
                .isEqualTo(DEPARTMENT);
    }
}