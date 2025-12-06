package org.droid.zero.multitenantaipayrollsystem.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tenants")
public class Tenant extends BaseModel {
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "tenant name is required")
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "tenant email is required")
    @Email(message = "invalid email format")
    private String email;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "tenant phone number is required")
    private String phone;

    @Column(nullable = false)
    @NotEmpty(message = "tenant industry is required")
    private String industry;

    @Column(name = "is_active")
    private boolean active = true;


}