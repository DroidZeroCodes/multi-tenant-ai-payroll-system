package org.droid.zero.multitenantaipayrollsystem.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.repository.AllowanceRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.department.repository.DepartmentRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.position.repository.PositionRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryAllowance;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.EmployeeSalaryRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryAllowanceRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryStructureRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.listener.TenantScopedEntityListener;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DBDataInitializer {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final AllowanceRepository allowanceRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final SalaryAllowanceRepository salaryAllowanceRepository;
    private final EmployeeSalaryRepository employeeSalaryRepository;

    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void devStartup() {
        TenantScopedEntityListener.runAsRootTenant(() -> {
            log.info("✅ Dev application is ready");
            log.info("🚩 Starting Database cleanup...");

            employeeSalaryRepository.deleteAll();
            salaryAllowanceRepository.deleteAll();
            salaryStructureRepository.deleteAll();
            allowanceRepository.deleteAll();
            employeeRepository.deleteAll();
            positionRepository.deleteAll();
            departmentRepository.deleteAll();
            userRepository.deleteAll();
            tenantRepository.deleteAll();


            log.info("✅ Database cleanup completed");
            log.info("🚩 Starting Database initialization...");

            log.info("➡️ Seeding Tenant Data...");
            Tenant tenant = new Tenant(
                    "Payroll Company",
                    "payroll.company@email.com",
                    "+63 9123456789",
                    "Technology"
            );
            tenantRepository.save(tenant);
            UUID tenantId = tenant.getId();
            log.info("✅ Successfully Created Tenant Data");

            TenantContext.setTenantId(tenantId);


            log.info("➡️ Seeding User Data...");
            User superAdminUser = new User(
                    "John",
                    "Doe",
                    "jDoe@email.com",
                    Set.of(UserRole.SUPER_ADMIN),
                    new UserCredentials(
                            "jDoe@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                    UsernamePasswordAuthenticationToken.authenticated(superAdminUser,null,superAdminUser.getAuthorities())
            );


            User tenantAdminUser = new User(
                    "Sence",
                    "Montana",
                    "sMontana@email.com",
                    Set.of(UserRole.TENANT_ADMIN),
                    new UserCredentials(
                            "sMontana@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            User hrUser = new User(
                    "Jude",
                    "Luanne",
                    "jLuanne@email.com",
                    Set.of(UserRole.HR_OFFICER),
                    new UserCredentials(
                            "jLuanne@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            User employeeUser = new User(
                    "Amar",
                    "Tariq",
                    "aTariq@email.com",
                    Set.of(UserRole.EMPLOYEE),
                    new UserCredentials(
                            "aTariq@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            userRepository.save(superAdminUser);
            userRepository.save(tenantAdminUser);
            userRepository.save(hrUser);
            userRepository.save(employeeUser);

            log.info("✅ Successfully Created User Data");

            log.info("➡️ Seeding Department Data...");
            Department engineeringDept = new Department(
                    "Engineering",
                    "Software development and technology department",
                    tenantId
            );
            Department hrDept = new Department(
                    "Human Resources",
                    "Employee management and welfare department",
                    tenantId
            );
            Department financeDept = new Department(
                    "Finance",
                    "Financial planning and accounting department",
                    tenantId
            );
            
            departmentRepository.save(engineeringDept);
            departmentRepository.save(hrDept);
            departmentRepository.save(financeDept);
            log.info("✅ Successfully Created Department Data");

            log.info("➡️ Seeding Position Data...");
            Position seniorDev = new Position(
                    "Senior Software Engineer",
                    "Experienced software developer",
                    "Senior",
                    tenantId
            );
            Position hrManager = new Position(
                    "HR Manager",
                    "Human resources department manager",
                    "Manager",
                    tenantId
            );
            Position financeAnalyst = new Position(
                    "Financial Analyst",
                    "Financial analysis and reporting",
                    "Analyst",
                    tenantId
            );
            
            positionRepository.save(seniorDev);
            positionRepository.save(hrManager);
            positionRepository.save(financeAnalyst);
            log.info("✅ Successfully Created Position Data");

            log.info("➡️ Seeding Employee Data...");
            Employee employee1 = new Employee(
                    employeeUser.getId(),
                    "EMP001",
                    "Amar",
                    "Tariq",
                    "aTariq@email.com",
                    "+63 9123456789",
                    "123 Main St, City",
                    "Male",
                    LocalDate.of(1990, 5, 15),
                    LocalDate.of(2023, 1, 15),
                    EMPLOYMENT_TYPE.FULL_TIME,
                    EMPLOYEE_STATUS.ACTIVE,
                    engineeringDept.getId(),
                    seniorDev.getId(),
                    tenantId
            );
            
            Employee employee2 = new Employee(
                    hrUser.getId(),
                    "EMP002",
                    "Jude",
                    "Luanne",
                    "jLuanne@email.com",
                    "+63 9123456790",
                    "456 Oak Ave, City",
                    "Female",
                    LocalDate.of(1985, 8, 22),
                    LocalDate.of(2022, 6, 1),
                    EMPLOYMENT_TYPE.FULL_TIME,
                    EMPLOYEE_STATUS.ACTIVE,
                    hrDept.getId(),
                    hrManager.getId(),
                    tenantId
            );
            
            employeeRepository.save(employee1);
            employeeRepository.save(employee2);
            log.info("✅ Successfully Created Employee Data");

            log.info("➡️ Seeding Allowance Data...");
            Allowance healthAllowance = new Allowance(
                    "Health Insurance",
                    "Monthly health insurance allowance",
                    "HEALTH",
                    5000.00,
                    true,
                    tenantId
            );
            Allowance transportAllowance = new Allowance(
                    "Transportation",
                    "Monthly transportation allowance",
                    "TRANSPORT",
                    2000.00,
                    false,
                    tenantId
            );
            Allowance mealAllowance = new Allowance(
                    "Meal Allowance",
                    "Monthly meal allowance",
                    "MEAL",
                    1500.00,
                    false,
                    tenantId
            );
            
            allowanceRepository.save(healthAllowance);
            allowanceRepository.save(transportAllowance);
            allowanceRepository.save(mealAllowance);
            log.info("✅ Successfully Created Allowance Data");

            log.info("➡️ Seeding Salary Structure Data...");
            SalaryStructure devSalaryStructure = new SalaryStructure(
                    "Senior Developer Structure",
                    "Salary structure for senior software developers",
                    50000.00,
                    LocalDate.now(),
                    tenantId
            );
            devSalaryStructure.toggleActiveStatus();
            
            SalaryStructure hrSalaryStructure = new SalaryStructure(
                    "HR Manager Structure",
                    "Salary structure for HR managers",
                    45000.00,
                    LocalDate.now(),
                    tenantId
            );
            hrSalaryStructure.toggleActiveStatus();
            
            salaryStructureRepository.save(devSalaryStructure);
            salaryStructureRepository.save(hrSalaryStructure);
            log.info("✅ Successfully Created Salary Structure Data");

            log.info("➡️ Seeding Salary Allowance Data...");
            SalaryAllowance devHealthAllowance = new SalaryAllowance(
                    devSalaryStructure,
                    healthAllowance,
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    tenantId
            );
            SalaryAllowance devTransportAllowance = new SalaryAllowance(
                    devSalaryStructure,
                    transportAllowance,
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    tenantId
            );
            SalaryAllowance hrHealthAllowance = new SalaryAllowance(
                    hrSalaryStructure,
                    healthAllowance,
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    tenantId
            );
            
            salaryAllowanceRepository.save(devHealthAllowance);
            salaryAllowanceRepository.save(devTransportAllowance);
            salaryAllowanceRepository.save(hrHealthAllowance);
            log.info("✅ Successfully Created Salary Allowance Data");

            log.info("➡️ Seeding Employee Salary Data...");
            EmployeeSalary employee1Salary = new EmployeeSalary(
                    devSalaryStructure,
                    employee1,
                    LocalDate.now(),
                    tenantId
            );
            employee1Salary.updateSalaryEndDate(LocalDate.now().plusYears(1));
            
            EmployeeSalary employee2Salary = new EmployeeSalary(
                    hrSalaryStructure,
                    employee2,
                    LocalDate.now(),
                    tenantId
            );
            employee2Salary.updateSalaryEndDate(LocalDate.now().plusYears(1));
            
            employeeSalaryRepository.save(employee1Salary);
            employeeSalaryRepository.save(employee2Salary);
            log.info("✅ Successfully Created Employee Salary Data");

            TenantContext.clear();
            SecurityContextHolder.clearContext();

            log.info("✅ Database initialization completed");
        });
    }
}


