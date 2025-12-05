package org.droid.zero.multitenantaipayrollsystem.tenant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tenant {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "tenant name is required")
    private String name;

    @Column(nullable = false)
    @NotEmpty(message = "tenant email is required")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "tenant phone number is required")
    private String phone;

    @Column(nullable = false)
    @NotEmpty(message = "tenant industry is required")
    private String industry;

    private boolean isActive;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}