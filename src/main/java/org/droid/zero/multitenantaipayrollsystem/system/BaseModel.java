package org.droid.zero.multitenantaipayrollsystem.system;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseModel {
    @Id
    @GeneratedValue()
    private UUID id;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}
