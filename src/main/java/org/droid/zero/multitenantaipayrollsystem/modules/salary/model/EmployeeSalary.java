package org.droid.zero.multitenantaipayrollsystem.modules.salary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.system.context.UserContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ValidationException;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "employee_salary_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"salary_structure_id", "employee_id", "tenant_id"})
        }
)
public class EmployeeSalary extends TenantScopedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "salary_structure_id",
            nullable = false,
            updatable = false
    )
    private SalaryStructure salaryStructure;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            updatable = false
    )
    private Employee employee;

    @NotNull(message = "Effective date is required")
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Assigned by is required")
    @Column(name = "assigned_by", nullable = false)
    private String assignedBy;

    public EmployeeSalary(
            SalaryStructure salaryStructure,
            Employee employee,
            LocalDate effectiveDate,
            UUID tenantId
    ) {
        this.salaryStructure = salaryStructure;
        this.employee = employee;
        this.effectiveDate = effectiveDate;
        this.tenantId = tenantId;
    }

    public EmployeeSalary changeSalary(
            SalaryStructure newSalaryStructure,
            LocalDate newSalaryEffectiveDate,
            LocalDate oldSalaryEndDate
    ){
        if (newSalaryEffectiveDate.isBefore(effectiveDate)) {
            throw new ValidationException("New salary effective date cannot be before old salary effective date");
        }

        this.endDate = oldSalaryEndDate;

        return new EmployeeSalary(
                newSalaryStructure,
                this.employee,
                newSalaryEffectiveDate,
                this.tenantId
        );
    }

    public void updateSalaryEffectiveDate(LocalDate effectiveDate){
        this.effectiveDate = effectiveDate;
    }

    public void updateSalaryEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    @PrePersist
    @PreUpdate
    private void validateDatesAndAssignAssignedBy() {
        if (effectiveDate.isAfter(endDate))
            throw new ValidationException("Effective date cannot be after end date");

        User user = UserContext.getCurrentUser();
        if (user == null)
            throw new IllegalStateException("User not found");

        this.assignedBy = user.getFirstName() + " " + user.getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeSalary that = (EmployeeSalary) o;
        return salaryStructure.equals(that.salaryStructure) &&
               employee.equals(that.employee) &&
               tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(salaryStructure, employee, tenantId);
    }

    @Override
    public String toString() {
        return "EmployeeSalary{" +
                "salaryStructure=" + salaryStructure +
                ", employee=" + employee +
                ", effectiveDate=" + effectiveDate +
                ", endDate=" + endDate +
                ", assignedBy='" + assignedBy + '\'' +
                '}';
    }
}





