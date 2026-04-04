package org.droid.zero.multitenantaipayrollsystem.modules.employee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uc_employee_employee_code_tenant_id",
                        columnNames = {"employee_code", "tenant_id"}
                )
        }
)
public class Employee extends TenantScopedEntity {

    @NotBlank(message = "Employee code is required")
    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "invalid email format")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull(message = "Date of birth is required")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender", nullable = false)
    private String gender;

    @NotNull(message = "Joining date is required")
    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EMPLOYMENT_TYPE employmentType = EMPLOYMENT_TYPE.FULL_TIME;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EMPLOYEE_STATUS status = EMPLOYEE_STATUS.INACTIVE;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "department_id",
            updatable = false
    )
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "position_id",
            updatable = false
    )
    private Position position;

    public Employee(
            User user,
            String employeeCode,
            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            String gender,
            LocalDate dob,
            LocalDate joiningDate,
            EMPLOYMENT_TYPE employmentType,
            EMPLOYEE_STATUS status,
            UUID tenantId
    ) {
        this.user = user;
        this.status = status;
        this.employmentType = employmentType;
        this.joiningDate = joiningDate;
        this.gender = gender;
        this.address = address;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.employeeCode = employeeCode;
        this.tenantId = tenantId;
    }

    public void assignEmployeeCode(String employeeCode) {
        if (employeeCode == null || employeeCode.isBlank())
            throw new IllegalArgumentException("employeeCode cannot be null or blank");

        this.employeeCode = employeeCode;
    }

    public void updateEmployeeInfo(
            String firstName,
            String lastName,
            String email,
            String phone,
            LocalDate dob,
            String address,
            String gender
    ) {
        if (firstName != null && !firstName.isBlank()) this.firstName = firstName;
        if (lastName != null && !lastName.isBlank()) this.lastName = lastName;
        if (email != null && !email.isBlank()) this.email = email;
        if (phone != null && !phone.isBlank()) this.phone = phone;
        if (dob != null) this.dob = dob;
        if (address != null && !address.isBlank()) this.address = address;
        if (gender != null && !gender.isBlank()) this.gender = gender;
    }

    public void updateEmploymentDetails(
            LocalDate joiningDate,
            EMPLOYMENT_TYPE employmentType
    ) {
        if (joiningDate != null) this.joiningDate = joiningDate;
        if (employmentType != null) this.employmentType = employmentType;
    }

    public void assignPosition(Position position) {
        if (position != null) this.position = position;
    }

    public void assignDepartment(Department department) {
        if (department != null) this.department = department;
    }

    public void changeEmployeeStatus(EMPLOYEE_STATUS status) {
        if (status != null) this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee that = (Employee) o;
        return firstName.concat(lastName).trim().equalsIgnoreCase(that.firstName.concat(that.lastName).trim()) &&
                email.trim().equalsIgnoreCase(that.email.trim()) &&
                phone.trim().equalsIgnoreCase(that.phone.trim()) &&
                tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                firstName.concat(lastName).trim(),
                email.trim(),
                phone.trim(),
                tenantId
        );
    }

    @Override
    public String toString() {
        String departmentId = department != null ? department.getId().toString() : "";
        String positionId = position != null ? position.getId().toString() : "";
        return "Employee{" +
                "firstName=" + firstName + '\'' +
                ", lastName=" + lastName + '\'' +
                ", email=" + email + '\'' +
                ", phone=" + phone + '\'' +
                ", dob=" + dob + '\'' +
                ", address=" + address + '\'' +
                ", gender=" + gender + '\'' +
                ", joiningDate=" + joiningDate + '\'' +
                ", employmentType=" + employmentType + '\'' +
                ", status=" + status + '\'' +
                ", departmentId=" + departmentId + '\'' +
                ", positionId=" + positionId + '\'' +
                ", userId=" + user.getId() + '\'' +
                ", tenantId=" + tenantId + '\'' +
                '}';
    }
}