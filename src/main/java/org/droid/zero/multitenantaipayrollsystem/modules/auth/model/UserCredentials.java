package org.droid.zero.multitenantaipayrollsystem.modules.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "user_credentials")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserCredentials extends BaseModel {

    @NotBlank(message = "contactEmail is required")
    @Email(message = "invalid contactEmail format")
    private String email;

    @NotBlank(message = "password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true, nullable=false, updatable=false)
    private User user;

    public UserCredentials(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public void changeEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null");
        this.email = email;
    }

    public void changePassword(String password) {
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password cannot be null");
        this.passwordHash = password;
    }

    public void linkToUser(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        this.user = user;
    }

    public void auditLogin() {
        this.lastLoginAt = Instant.now();
    }

    public void hashPassword(String password) {
        this.passwordHash = password;
    }
}