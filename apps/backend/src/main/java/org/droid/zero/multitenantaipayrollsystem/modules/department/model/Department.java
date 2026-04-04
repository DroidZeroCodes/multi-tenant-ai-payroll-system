package org.droid.zero.multitenantaipayrollsystem.modules.department.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uc_department_name_tenant_id",
                        columnNames = {"name", "tenant_id"}
                )
        }
)
public class Department extends TenantScopedEntity {

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_active")
    private boolean active = false;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable=false)
//    private Employee manager;

    public Department(
            String name,
            String description,
            UUID tenantId
    ) {
        this.name = name;
        this.description = description;
        this.tenantId = tenantId;
    }

    public void updateDepartmentDetails(String name, String description) {
        if (name != null && !name.isBlank()) this.name = name;
        if (description != null && !description.isBlank()) this.description = description;
    }

    public void toggleActiveStatus(){
        this.active = !this.active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return name.trim().equalsIgnoreCase(that.name.trim()) &&
                tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, tenantId);
    }

    @Override
    public String toString() {
        return "Department{" +
                "name=" + name + '\'' +
                ", description=" + description + '\'' +
                ", active=" + active + '\'' +
                ", tenantId=" + tenantId + '\'' +
                '}';
    }
}