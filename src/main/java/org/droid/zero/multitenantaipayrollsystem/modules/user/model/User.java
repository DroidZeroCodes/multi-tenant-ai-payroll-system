package org.droid.zero.multitenantaipayrollsystem.modules.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.EMPLOYEE;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseModel implements UserDetails {

    @NotBlank(message = "firstName is required")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "contactEmail is required")
    @Email(message = "invalid contactEmail format")
    private String contactEmail;

    @Column(name = "is_active")
    private boolean active = true; // set to true for now

    @Column(name = "is_verified")
    private boolean verified = true; // set to true for now

    @OneToOne(mappedBy = "user", optional = false, cascade = CascadeType.ALL)
    private UserCredentials userCredentials;

    @OneToMany(mappedBy = "user",  fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<UserTenantRole> userTenantRoles = new HashSet<>();

    @Transient
    private final Set<UserRole> activeRoles = new HashSet<>();

    public User(
            String firstName,
            String lastName,
            String contactEmail,
            Set<UserRole> roles,
            UserCredentials userCredentials,
            UUID tenantId
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactEmail = contactEmail;
        this.userCredentials = userCredentials;
        userCredentials.linkToUser(this);

        Set<UserRole> roleSet = roles != null ? new HashSet<>(roles) : Set.of(EMPLOYEE);
        this.userTenantRoles.add(new UserTenantRole(roleSet,this, tenantId));
    }

    public void updateProfile(String firstName, String lastName, String contactEmail) {
        if (firstName != null && !firstName.isBlank()) this.firstName = firstName;
        if (lastName != null && !lastName.isBlank()) this.lastName = lastName;
        if (contactEmail != null && !contactEmail.isBlank()) this.contactEmail = contactEmail;
    }

    public void changeEmail(String contactEmail) {
        if (contactEmail == null || contactEmail.isBlank()) throw new IllegalArgumentException("Email cannot be null");
        this.contactEmail = contactEmail;
    }

    public void verifyUser() {
        this.active = true;
        this.verified = true;
    }

    public Set<UUID> getTenantIds(){
        return userTenantRoles.stream().map(TenantScopedEntity::getTenantId).collect(Collectors.toSet());
    }

    public Set<UserRole> getRolesForTenant(UUID tenantId) {
        UserTenantRole tenantRoles = userTenantRoles
                .stream()
                .filter(userTenantRole -> userTenantRole.getTenantId().equals(tenantId))
                .findFirst()
                .orElse(null);

        return tenantRoles != null ? tenantRoles.getRoles() : Set.of();
    }

    public void loadActiveRoles() {
        UUID tenantId = TenantContext.getTenantId();
        if (tenantId != null) activeRoles.addAll(getRolesForTenant(tenantId));
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return activeRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.value()))
                .toList();
    }

    @Override
    public String getPassword() {
        return userCredentials.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return userCredentials.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }

    @Override
    public boolean isAccountNonLocked() {
        return verified && active;
    }
}