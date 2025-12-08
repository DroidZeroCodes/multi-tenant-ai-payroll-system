package org.droid.zero.multitenantaipayrollsystem.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseModel {

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "firstName is required")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Column(name = "last_name")
    private String lastName;

    @NotEmpty(message = "role is required")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> role = Set.of(UserRole.EMPLOYEE);

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "last_login")
    private Instant lastLogin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Tenant tenant;

    //    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Arrays.stream(role.split(","))
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                .toList();
//    }
//
//    @Override
//    public String getPassword() {
//        return this.passwordHash;
//    }
//
//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return this.isActive;
//    }
}