package org.droid.zero.multitenantaipayrollsystem.modules.department.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
public class Department extends TenantScopedEntity {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "description is required")
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
}