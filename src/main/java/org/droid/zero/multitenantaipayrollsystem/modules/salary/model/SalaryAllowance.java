package org.droid.zero.multitenantaipayrollsystem.modules.salary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.system.context.UserContext;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "salary_allowances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uc_salary_allowance_tenant",
                        columnNames = {"salary_structure_id", "allowance_id", "tenant_id"}
                )
        }
)
public class SalaryAllowance extends TenantScopedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "salary_structure_id",
            nullable = false,
            updatable = false
    )
    private SalaryStructure salaryStructure;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "allowance_id",
            nullable = false,
            updatable = false
    )
    private Allowance allowance;

    @NotNull(message = "effective date is required")
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @NotNull(message = "expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @NotNull(message = "assigned by is required")
    @Column(name = "assigned_by", nullable = false)
    private String assignedBy;

    public SalaryAllowance(
            SalaryStructure salaryStructure,
            Allowance allowance,
            LocalDate effectiveDate,
            LocalDate expiryDate,
            UUID tenantId
    ) {
        this.salaryStructure = salaryStructure;
        this.allowance = allowance;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.tenantId = tenantId;
    }

    public void updateEffectiveDates(
            LocalDate effectiveDate,
            LocalDate expiryDate
    ){
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
    }

    @PrePersist
    @PreUpdate
    private void validateDatesAndAssignAssignedBy() {
        if (effectiveDate.isAfter(expiryDate))
            throw new IllegalArgumentException("Effective date cannot be after expiry date");

        User user = UserContext.getCurrentUser();
        if (user == null)
            throw new IllegalStateException("User not found");

        this.assignedBy = user.getFirstName() + " " + user.getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryAllowance that = (SalaryAllowance) o;
        return salaryStructure.equals(that.salaryStructure) &&
               allowance.equals(that.allowance) &&
               tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(salaryStructure, allowance, tenantId);
    }

    @Override
    public String toString() {
        return "SalaryAllowance{" +
                "salaryStructure=" + salaryStructure +
                ", allowance=" + allowance +
                ", effectiveDate=" + effectiveDate +
                ", expiryDate=" + expiryDate +
                ", assignedBy='" + assignedBy + '\'' +
                '}';
    }
}





