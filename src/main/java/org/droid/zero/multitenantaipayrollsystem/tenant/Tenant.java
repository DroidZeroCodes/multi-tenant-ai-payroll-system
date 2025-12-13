package org.droid.zero.multitenantaipayrollsystem.tenant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.droid.zero.multitenantaipayrollsystem.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tenants")
public class Tenant extends BaseModel {
    @Column(nullable = false, unique = true)
    @NotBlank(message = "name is required")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "phone is required")
    private String phone;

    @Column(nullable = false)
    @NotBlank(message = "industry is required")
    private String industry;

    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "tenant", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<User> user = new HashSet<>();

    public boolean toggleActiveStatus() {
        this.active = !this.active;
        return this.active;
    }
}