package org.droid.zero.multitenantaipayrollsystem.modules.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.listener.TenantScopedEntityListener;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.EMPLOYEE;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(TenantScopedEntityListener.class)
public class UserTenantRole extends TenantScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private User user;

    @NotEmpty(message = "roles is required")
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_tenant_roles_mapping",
            joinColumns = @JoinColumn(name = "user_tenant_role_id"),
            foreignKey = @ForeignKey(name = "fk_user_tenant_role_mapping")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private Set<UserRole> roles = new HashSet<>(Set.of(EMPLOYEE));

    public UserTenantRole(
            Set<UserRole> roles,
            User user,
            UUID tenantId
    ) {
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>(Set.of(EMPLOYEE));
        this.user = user;
        this.tenantId = tenantId;
    }
}
