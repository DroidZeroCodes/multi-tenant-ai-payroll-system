package org.droid.zero.multitenantaipayrollsystem.modules.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.user.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserCredentials extends BaseModel implements UserDetails {

    @Email
    @NotBlank(message = "email is required")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "password is required")
    @Column(nullable = false)
    private String password;

    @NotEmpty(message = "role is required")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> role = Set.of(UserRole.EMPLOYEE);

    @Column(name = "is_active")
    private boolean active = true; // set to true for now

    private boolean verified = true; // set to true for now

    @Column(name = "last_login")
    private Instant lastLogin;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(unique = true, nullable=false, updatable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NotNull(message = "tenantId is required")
    private Tenant tenant;

    @PreUpdate
    public void preUpdateEmail() {
        if (this.user != null) {
            this.user.setEmail(this.email);
        }
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.value()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }

    @Override
    public boolean isAccountNonLocked() {
        return verified && active;
    }

    public void doVerify() {
        active = true;
        this.verified = true;
    }
}