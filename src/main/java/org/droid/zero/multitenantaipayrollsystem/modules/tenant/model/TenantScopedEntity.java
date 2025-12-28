package org.droid.zero.multitenantaipayrollsystem.modules.tenant.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.listener.TenantScopedEntityListener;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.hibernate.annotations.TenantId;

import java.util.UUID;

@MappedSuperclass
@Getter
@EntityListeners(TenantScopedEntityListener.class)
public abstract class TenantScopedEntity extends BaseModel {

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    protected UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tenant_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Tenant tenant;
}
