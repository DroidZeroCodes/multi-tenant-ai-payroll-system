package org.droid.zero.multitenantaipayrollsystem.modules.deduction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.validation.constraints.DecimalMin;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "deductions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uc_deduction_name_tenant_id",
                        columnNames = {"name", "tenant_id"}
                )
        }
)
public class Deduction extends TenantScopedEntity {

    @NotBlank(message = "name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "description is required")
    @Column(name = "description", nullable = false)
    private String description;

    @NotBlank(message = "deduction type is required")
    @Column(name = "deduction_type", nullable = false)
    private String deductionType;

    @NotNull(message = "amount is required")
    @DecimalMax(value = "999999.99", message = "amount must be less than or equal to 999999.99")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "is_taxable")
    private boolean taxable = false;

    public Deduction(
            String name,
            String description,
            String deductionType,
            Double amount,
            boolean taxable,
            UUID tenantId) {
        this.name = name;
        this.description = description;
        this.deductionType = deductionType;
        this.amount = amount;
        this.taxable = taxable;
        this.tenantId = tenantId;
    }

    public void updateDeductionDetails(
            String name,
            String description,
            String deductionType,
            Double amount,
            boolean taxable
    ) {
        if (name != null && !name.isBlank()) this.name = name;
        if (description != null && !description.isBlank()) this.description = description;
        if (deductionType != null && !deductionType.isBlank()) this.deductionType = deductionType;
        if (amount != null && !amount.isNaN()) this.amount = amount;
        this.taxable = taxable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deduction that = (Deduction) o;
        return name.trim().equalsIgnoreCase(that.name.trim()) &&
                tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, tenantId);
    }

    @Override
    public String toString() {
        return "Deduction{" +
                "name=" + name + '\'' +
                ", description=" + description + '\'' +
                ", deductionType=" + deductionType + '\'' +
                ", amount=" + amount + '\'' +
                ", taxable=" + taxable + '\'' +
                ", tenantId=" + tenantId + '\'' +
                '}';
    }
}

