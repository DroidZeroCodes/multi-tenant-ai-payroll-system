package org.droid.zero.multitenantaipayrollsystem.modules.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeInfoUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmploymentUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Employee")
class EmployeeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Check findEmployeeById (GET)")
    void findEmployeeById_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();
        
        Employee employee = new Employee(
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
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        // Act & Assert
        this.mockMvc.perform(get(BASE_URL + "/employees/" + employee.getId())
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check findEmployeeById with not found (GET)")
    void findEmployeeById_NotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        this.mockMvc.perform(get(BASE_URL + "/employees/" + nonExistentId)
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Could not find EMPLOYEE with ID '" + nonExistentId + "'."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].status").value(404))
                .andExpect(jsonPath("$.errors[0].code").value("resource_not_found"))
                .andExpect(jsonPath("$.errors[0].title").value("Resource Not Found"))
                .andExpect(jsonPath("$.errors[0].detail").value("Could not find EMPLOYEE with ID '" + nonExistentId + "'."));
    }

    @Test
    @DisplayName("Check findEmployeeById (GET) - Forbidden for unauthorized Employee")
    void findEmployeeById_Forbidden_Employee() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();
        
        Employee employee = new Employee(
                user,
                "EMP002",
                "Target",
                "Employee",
                "target@example.com",
                "+1234567890",
                "123 Main St",
                "MALE",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 1, 1),
                EMPLOYMENT_TYPE.FULL_TIME,
                EMPLOYEE_STATUS.ACTIVE,
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        // Act & Assert
        this.mockMvc.perform(get(BASE_URL + "/employees/" + employee.getId())
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Check addEmployee with valid input (POST)")
    void testAddEmployee_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();

        EmployeeRequest request = new EmployeeRequest(
                user.getId(),
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
                department.getId(),
                position.getId()
        );

        // Act & Assert
        this.mockMvc.perform(post(BASE_URL + "/employees")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"))
                .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.data.employmentType").value("PART_TIME"))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check addEmployee with invalid input (POST)")
    void testAddEmployee_InvalidInput() throws Exception {
        // Arrange - missing required fields
        EmployeeRequest request = new EmployeeRequest(
                null,
                "",
                "",
                "",
                "",
                null,
                "",
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Act & Assert
        this.mockMvc.perform(post(BASE_URL + "/employees")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see errors for details."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[*].detail",
                        containsInAnyOrder(
                                "User ID is required",
                                "First name is required",
                                "Last name is required",
                                "Email is required",
                                "Phone number is required",
                                "Date of birth is required",
                                "Address is required",
                                "Gender is required",
                                "Joining date is required"
                        )));
    }

    @Test
    @DisplayName("Check addEmployee (POST) - Forbidden for Employee")
    void testAddEmployee_Forbidden_Employee() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();

        EmployeeRequest request = new EmployeeRequest(
                user.getId(),
                "Hacker",
                "Employee",
                "hacker@example.com",
                "+1234567890",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "MALE",
                LocalDate.of(2023, 1, 1),
                EMPLOYMENT_TYPE.FULL_TIME,
                EMPLOYEE_STATUS.ACTIVE,
                department.getId(),
                position.getId()
        );

        // Act & Assert
        this.mockMvc.perform(post(BASE_URL + "/employees")
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Check updateEmployeeInfo (PUT)")
    void testUpdateEmployeeInfo_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();
        
        Employee employee = new Employee(
                user,
                "EMP003",
                "Original",
                "Name",
                "original@example.com",
                "+1234567890",
                "123 Main St",
                "MALE",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 1, 1),
                EMPLOYMENT_TYPE.FULL_TIME,
                EMPLOYEE_STATUS.ACTIVE,
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        EmployeeInfoUpdateRequest updateRequest = new EmployeeInfoUpdateRequest(
                "Updated",
                "Name",
                "updated@example.com",
                "+1111111111",
                LocalDate.of(1990, 2, 2),
                "789 Pine St",
                "OTHER"
        );

        // Act & Assert
        this.mockMvc.perform(patch(BASE_URL + "/employees/" + employee.getId() + "/info")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.firstName").value("Updated"))
                .andExpect(jsonPath("$.data.lastName").value("Name"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.phone").value("+1111111111"))
                .andExpect(jsonPath("$.data.address").value("789 Pine St"))
                .andExpect(jsonPath("$.data.gender").value("OTHER"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check updateEmploymentDetails (PUT)")
    void testUpdateEmploymentDetails_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();
        
        Employee employee = new Employee(
                user,
                "EMP004",
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
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        EmploymentUpdateRequest updateRequest = new EmploymentUpdateRequest(
                LocalDate.of(2023, 7, 1),
                EMPLOYMENT_TYPE.CONTRACT
        );

        // Act & Assert
        this.mockMvc.perform(patch(BASE_URL + "/employees/" + employee.getId() + "/employment")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.joiningDate").exists())
                .andExpect(jsonPath("$.data.employmentType").value("CONTRACT"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check updateEmployeeStatus (PUT)")
    void testUpdateEmployeeStatus_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();
        
        Employee employee = new Employee(
                user,
                "EMP005",
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
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        // Act & Assert
        this.mockMvc.perform(patch(BASE_URL + "/employees/" + employee.getId() + "/status")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .param("status", "SUSPENDED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.status").value("SUSPENDED"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check updateEmployeePosition (PUT)")
    void testUpdateEmployeePosition_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();
        Position newPosition = createTestPosition();
        
        Employee employee = new Employee(
                user,
                "EMP006",
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
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        // Act & Assert
        this.mockMvc.perform(patch(BASE_URL + "/employees/" + employee.getId() + "/position")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .param("positionId", newPosition.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check updateEmployeeDepartment (PUT)")
    void testUpdateEmployeeDepartment_Success() throws Exception {
        // Arrange
        User user = createRandomTestUser();
        Department department = createTestDepartment();
        Department newDepartment = createTestDepartment();
        Position position = createTestPosition();
        
        Employee employee = new Employee(
                user,
                "EMP007",
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
                TEST_TENANT_ID
        );
        employee.assignPosition(position);
        employee.assignDepartment(department);
        employee = employeeRepository.save(employee);

        // Act & Assert
        this.mockMvc.perform(patch(BASE_URL + "/employees/" + employee.getId() + "/department")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .param("departmentId", newDepartment.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check employeeCode uniqueness is enforced")
    void testEmployeeCodeUniqueness() throws Exception {
        // Arrange
        User user1 = createRandomTestUser();
        User user2 = createRandomTestUser();
        Department department = createTestDepartment();
        Position position = createTestPosition();

        EmployeeRequest request1 = new EmployeeRequest(
                user1.getId(),
                "User",
                "One",
                "user1@example.com",
                "+1234567890",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "MALE",
                LocalDate.of(2023, 1, 1),
                EMPLOYMENT_TYPE.FULL_TIME,
                EMPLOYEE_STATUS.ACTIVE,
                department.getId(),
                position.getId()
        );

        // Act & Assert - First employee creation should succeed
        this.mockMvc.perform(post(BASE_URL + "/employees")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Arrange - Try to create another employee with same joining date (should generate different employee code)
        EmployeeRequest request2 = new EmployeeRequest(
                user2.getId(),
                "User",
                "Two",
                "user2@example.com",
                "+1234567890",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "MALE",
                LocalDate.of(2023, 1, 1),
                EMPLOYMENT_TYPE.FULL_TIME,
                EMPLOYEE_STATUS.ACTIVE,
                department.getId(),
                position.getId()
        );

        // Act & Assert - Second employee should succeed with different employee code
        this.mockMvc.perform(post(BASE_URL + "/employees")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeCode").exists());
    }
}
