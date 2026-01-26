package org.droid.zero.multitenantaipayrollsystem.modules.salary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.validation.constraints.DecimalMin;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "salary_structures")
public class SalaryStructure extends TenantScopedEntity {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "base salary is required")
    @DecimalMax(value = "999999.99", message = "base salary must be less than or equal to 999999.99")
    @DecimalMin(value = "0.01", message = "base salary must be greater than 0")
    @Column(name = "base_salary")
    private Double baseSalary;

    @NotNull(message = "effective date is required")
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "is_active")
    private boolean active = false;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true, mappedBy = "salaryStructure")
    private final Set<SalaryAllowance> allowances = new HashSet<>();

    public SalaryStructure(
            String name,
            String description,
            Double baseSalary,
            LocalDate effectiveDate,
            UUID tenantId) {
        this.name = name;
        this.description = description;
        this.baseSalary = baseSalary;
        this.effectiveDate = effectiveDate;
        this.tenantId = tenantId;
    }

    public void addAllowances(Set<SalaryAllowance> allowances) {
        this.allowances.addAll(allowances);
    }

    public void removeAllowances(Set<SalaryAllowance> allowances) {
        this.allowances.removeAll(allowances);
    }

    public void updateSalaryDetails(
            String name,
            String description,
            Double baseSalary,
            LocalDate effectiveDate
    ) {
        if (name != null && !name.isBlank()) this.name = name;
        if (description != null && !description.isBlank()) this.description = description;
        if (baseSalary != null && !baseSalary.isNaN()) this.baseSalary = baseSalary;
        if (effectiveDate != null) this.effectiveDate = effectiveDate;
    }

    public void toggleActiveStatus(){
        this.active = !this.active;
    }
}



