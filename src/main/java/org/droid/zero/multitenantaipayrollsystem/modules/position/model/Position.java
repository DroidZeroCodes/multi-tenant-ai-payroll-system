package org.droid.zero.multitenantaipayrollsystem.modules.position.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
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
@Table(name = "positions")
public class Position extends TenantScopedEntity {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "level is required")
    private String level;

    @Column(name = "is_active")
    private boolean active = false;

    public Position(String title, String description, String level, UUID tenantId) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.tenantId = tenantId;
    }

    public void updatePositionDetails(String title, String description, String level) {
        if (title != null && !title.isBlank()) this.title = title;
        if (description != null && !description.isBlank()) this.description = description;
        if (level != null && !level.isBlank()) this.level = level;
    }

    public void toggleActiveStatus(){
        this.active = !this.active;
    }
}
