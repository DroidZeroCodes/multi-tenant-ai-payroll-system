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

import java.sql.Date;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "salary_allowances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"salary_structure_id", "allowance_id", "tenant_id"})
        }
)
public class SalaryAllowance extends TenantScopedEntity {

    @Column(name = "salary_structure_id", nullable = false, updatable = false)
    private UUID salaryStructureId;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "salary_structure_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private SalaryStructure salaryStructure;

    @Column(name = "allowance_id", nullable = false, updatable = false)
    private UUID allowanceId;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "allowance_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Allowance allowance;

    @NotNull(message = "effective date is required")
    @Column(name = "effective_date", nullable = false)
    private Date effectiveDate;

    @NotNull(message = "expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @NotNull(message = "assigned by is required")
    @Column(name = "assigned_by", nullable = false)
    private String assignedBy;

    public SalaryAllowance(
            UUID salaryStructureId,
            UUID allowanceId,
            Date effectiveDate,
            Date expiryDate,
            UUID tenantId
    ) {
        this.salaryStructureId = salaryStructureId;
        this.allowanceId = allowanceId;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.tenantId = tenantId;
    }

    public void updateEffectiveDates(
            Date effectiveDate,
            Date expiryDate
    ){
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
    }

    @PrePersist
    @PreUpdate
    private void validateDatesAndAssignAssignedBy() {
        if (effectiveDate.after(expiryDate))
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





