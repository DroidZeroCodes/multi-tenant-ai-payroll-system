package org.droid.zero.multitenantaipayrollsystem.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseModel {

    @NotBlank(message = "firstName is required")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    private String email;

    @OneToOne(mappedBy = "user", optional = false, cascade = CascadeType.ALL)
    private UserCredentials userCredentials;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NotNull(message = "tenantId is required")
    private Tenant tenant;

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
        if (userCredentials != null) {
            userCredentials.setUser(this);
        }
    }
}