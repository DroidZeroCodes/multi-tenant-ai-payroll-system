package org.droid.zero.multitenantaipayrollsystem.modules.employee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Employee extends TenantScopedEntity{

    @NotBlank(message = "employeeCode is required")
    private String employeeCode;

    @NotBlank(message = "firstName is required")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "phone is required")
    private String phone;

    @NotBlank(message = "date of birth is required")
    private Date dob;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "gender is required")
    private String gender;

    @NotBlank(message = "joining date is required")
    private Date joiningDate;

    @Enumerated(EnumType.STRING)
    private EMPLOYMENT_TYPE employmentType = EMPLOYMENT_TYPE.FULL_TIME;

    @Enumerated(EnumType.STRING)
    private EMPLOYEE_STATUS status = EMPLOYEE_STATUS.INACTIVE;

    @Column(
            name = "user_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    protected UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name="user_id",
            nullable=false,
            updatable=false,
            insertable = false
    )
    private User user;

    @Column(name = "department_id")
    protected UUID departmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name="department_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Department department;

    @Column(name = "position_id")
    protected UUID positionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name="position_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Position position;

    public Employee(
            UUID userId,
            String employeeCode,
            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            String gender,
            Date dob,
            Date joiningDate,
            EMPLOYMENT_TYPE employmentType,
            EMPLOYEE_STATUS status,
            UUID departmentId,
            UUID positionId,
            UUID tenantId
    ) {
        this.userId = userId;
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
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.tenantId = tenantId;
    }

    public void assignEmployeeCode(String employeeCode) {
        if (employeeCode == null || employeeCode.isBlank()) throw new IllegalArgumentException("employeeCode cannot be null or blank");

        this.employeeCode = employeeCode;
    }

    public void updateEmployeeInfo(
            String firstName,
            String lastName,
            String email,
            String phone,
            Date dob,
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
            Date joiningDate,
            EMPLOYMENT_TYPE employmentType
    ){
        if (joiningDate != null) this.joiningDate = joiningDate;
        if (employmentType != null) this.employmentType = employmentType;
    }

    public void assignPosition(UUID positionId) {
        if (positionId != null) this.positionId = positionId;
    }

    public void assignDepartment(UUID departmentId) {
        if (departmentId != null) this.departmentId = departmentId;
    }

    public void changeEmployeeStatus(EMPLOYEE_STATUS status){
        if (status != null) this.status = status;
    }
}