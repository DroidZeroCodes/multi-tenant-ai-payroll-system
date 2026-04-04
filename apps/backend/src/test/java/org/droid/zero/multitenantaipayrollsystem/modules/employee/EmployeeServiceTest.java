package org.droid.zero.multitenantaipayrollsystem.modules.employee;

import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.department.repository.DepartmentRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeInfoUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmploymentUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.mapper.EmployeeMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.service.EmployeeServiceImpl;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.position.repository.PositionRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest extends BaseUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequest employeeRequest;
    private EmployeeInfoUpdateRequest employeeInfoUpdateRequest;
    private EmploymentUpdateRequest employmentUpdateRequest;
    private UUID employeeId;
    private UUID userId;
    private UUID positionId;
    private UUID departmentId;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(
                employeeRepository,
                userRepository,
                positionRepository,
                departmentRepository,
                employeeMapper
        );

        UUID tenantId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);

        userId = UUID.randomUUID();
        positionId = UUID.randomUUID();
        departmentId = UUID.randomUUID();
        employeeId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        employee = new Employee(
                user,
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                "123 Main St",
                "MALE",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 1, 1),
                EMPLOYMENT_TYPE.FULL_TIME,
                EMPLOYEE_STATUS.ACTIVE,
                tenantId
        );
        employee.setId(employeeId);

        employeeRequest = new EmployeeRequest(
                userId,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "+0987654321",
                LocalDate.of(1992, 5, 15),
                "456 Oak Ave",
                "FEMALE",
                LocalDate.of(2023, 6, 1),
                EMPLOYMENT_TYPE.PART_TIME,
                EMPLOYEE_STATUS.INACTIVE,
                departmentId,
                positionId
        );

        employeeInfoUpdateRequest = new EmployeeInfoUpdateRequest(
                "Updated",
                "Name",
                "updated@example.com",
                "+1111111111",
                LocalDate.of(1990, 2, 2),
                "789 Pine St",
                "OTHER"
        );

        employmentUpdateRequest = new EmploymentUpdateRequest(
                LocalDate.of(2023, 7, 1),
                EMPLOYMENT_TYPE.CONTRACT
        );
    }

    @Test
    void findById_shouldReturnEmployee_whenEmployeeExists() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse foundEmployee = employeeService.findById(employeeId);

        // Assert
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.id()).isEqualTo(employeeId);
        assertThat(foundEmployee.employeeCode()).isEqualTo("EMP001");
        assertThat(foundEmployee.firstName()).isEqualTo("John");
        assertThat(foundEmployee.lastName()).isEqualTo("Doe");
        assertThat(foundEmployee.email()).isEqualTo("john.doe@example.com");
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void findById_shouldThrowException_whenEmployeeDoesNotExist() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.findById(employeeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find EMPLOYEE with ID '" + employeeId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void save_shouldSaveAndReturnEmployee_whenRequestIsValid() {
        // Arrange
        User user = new User();
        user.setId(userId);
        Position position = new Position();
        position.setId(positionId);
        Department department = new Department();
        department.setId(departmentId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(employeeRepository.countByJoiningDate(any(LocalDate.class))).thenReturn(0);
        when(employeeRepository.existsByEmployeeCode(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        EmployeeResponse savedEmployee = employeeService.save(employeeRequest);

        // Assert
        assertNotNull(savedEmployee);
        assertEquals(employeeId, savedEmployee.id());
        assertEquals("John", savedEmployee.firstName());
        assertEquals("Doe", savedEmployee.lastName());
        assertEquals("john.doe@example.com", savedEmployee.email());

        verify(userRepository, times(1)).findById(userId);
        verify(positionRepository, times(1)).findById(positionId);
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(employeeRepository, times(1)).countByJoiningDate(any(LocalDate.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void save_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.save(employeeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find USER with ID '" + userId + "'.");

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(positionRepository, departmentRepository, employeeRepository);
    }

    @Test
    void save_shouldThrowException_whenPositionDoesNotExist() {
        // Arrange
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.save(employeeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find POSITION with ID '" + positionId + "'.");

        verify(userRepository, times(1)).findById(userId);
        verify(employeeRepository, times(1)).countByJoiningDate(any(LocalDate.class));
        verify(employeeRepository, times(1)).existsByEmployeeCode(anyString());
        verify(positionRepository, times(1)).findById(positionId);
        verifyNoMoreInteractions(departmentRepository, employeeRepository);
    }

    @Test
    void save_shouldThrowException_whenDepartmentDoesNotExist() {
        // Arrange
        User user = new User();
        user.setId(userId);
        Position position = new Position();
        position.setId(positionId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.save(employeeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find DEPARTMENT with ID '" + departmentId + "'.");

        verify(userRepository, times(1)).findById(userId);
        verify(employeeRepository, times(1)).countByJoiningDate(any(LocalDate.class));
        verify(employeeRepository, times(1)).existsByEmployeeCode(anyString());
        verify(positionRepository, times(1)).findById(positionId);
        verify(departmentRepository, times(1)).findById(departmentId);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void updateInfo_shouldUpdateEmployeeInfo_whenEmployeeExists() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateInfo(employeeInfoUpdateRequest, employeeId);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertEquals("Updated", updatedEmployee.firstName());
        assertEquals("Name", updatedEmployee.lastName());
        assertEquals("updated@example.com", updatedEmployee.email());

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void updateInfo_shouldThrowException_whenEmployeeDoesNotExist() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateInfo(employeeInfoUpdateRequest, employeeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find EMPLOYEE with ID '" + employeeId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void updateEmployment_shouldUpdateEmploymentDetails_whenEmployeeExists() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateEmployment(employmentUpdateRequest, employeeId);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertEquals(LocalDate.of(2023, 7, 1), updatedEmployee.joiningDate());
        assertEquals(EMPLOYMENT_TYPE.CONTRACT, updatedEmployee.employmentType());

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void updateEmployment_shouldThrowException_whenEmployeeDoesNotExist() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployment(employmentUpdateRequest, employeeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find EMPLOYEE with ID '" + employeeId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void updateEmployeeStatus_shouldUpdateStatus_whenEmployeeExists() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateEmployeeStatus(employeeId, EMPLOYEE_STATUS.SUSPENDED);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertEquals(EMPLOYEE_STATUS.SUSPENDED, updatedEmployee.status());

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void updateEmployeeStatus_shouldThrowException_whenEmployeeDoesNotExist() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployeeStatus(employeeId, EMPLOYEE_STATUS.TERMINATED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find EMPLOYEE with ID '" + employeeId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void updateEmployeePosition_shouldUpdatePosition_whenEmployeeExistsAndPositionExists() {
        // Arrange
        UUID newPositionId = UUID.randomUUID();
        Position newPosition = new Position();
        newPosition.setId(newPositionId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(positionRepository.findById(newPositionId)).thenReturn(Optional.of(newPosition));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateEmployeePosition(employeeId, newPositionId);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertEquals(newPositionId, updatedEmployee.positionId());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(positionRepository, times(1)).findById(newPositionId);
    }

    @Test
    void updateEmployeePosition_shouldThrowException_whenEmployeeDoesNotExist() {
        // Arrange
        UUID newPositionId = UUID.randomUUID();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployeePosition(employeeId, newPositionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find EMPLOYEE with ID '" + employeeId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    void updateEmployeePosition_shouldThrowException_whenPositionDoesNotExist() {
        // Arrange
        UUID newPositionId = UUID.randomUUID();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(positionRepository.findById(newPositionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployeePosition(employeeId, newPositionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find POSITION with ID '" + newPositionId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(positionRepository, times(1)).findById(newPositionId);
    }

    @Test
    void updateEmployeePosition_shouldAllowNullPositionId() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateEmployeePosition(employeeId, null);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertNull(updatedEmployee.positionId());

        verify(employeeRepository, times(1)).findById(employeeId);
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    void updateEmployeeDepartment_shouldUpdateDepartment_whenEmployeeExistsAndDepartmentExists() {
        // Arrange
        UUID newDepartmentId = UUID.randomUUID();
        Department newDepartment = new Department();
        newDepartment.setId(newDepartmentId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(newDepartmentId)).thenReturn(Optional.of(newDepartment));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateEmployeeDepartment(employeeId, newDepartmentId);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertEquals(newDepartmentId, updatedEmployee.departmentId());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(departmentRepository, times(1)).findById(newDepartmentId);
    }

    @Test
    void updateEmployeeDepartment_shouldThrowException_whenEmployeeDoesNotExist() {
        // Arrange
        UUID newDepartmentId = UUID.randomUUID();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployeeDepartment(employeeId, newDepartmentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find EMPLOYEE with ID '" + employeeId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void updateEmployeeDepartment_shouldThrowException_whenDepartmentDoesNotExist() {
        // Arrange
        UUID newDepartmentId = UUID.randomUUID();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(newDepartmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployeeDepartment(employeeId, newDepartmentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Could not find DEPARTMENT with ID '" + newDepartmentId + "'.");

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(departmentRepository, times(1)).findById(newDepartmentId);
    }

    @Test
    void updateEmployeeDepartment_shouldAllowNullDepartmentId() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse updatedEmployee = employeeService.updateEmployeeDepartment(employeeId, null);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employeeId, updatedEmployee.id());
        assertNull(updatedEmployee.departmentId());

        verify(employeeRepository, times(1)).findById(employeeId);
        verifyNoMoreInteractions(departmentRepository);
    }
}

