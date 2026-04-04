package org.droid.zero.multitenantaipayrollsystem.modules.tenant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.BaseModel;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tenants")
public class Tenant extends BaseModel {
    @Column(nullable = false, unique = true)
    @NotBlank(message = "name is required")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "contactEmail is required")
    @Email(message = "invalid contactEmail format")
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "phone is required")
    private String phone;

    @Column(nullable = false)
    @NotBlank(message = "industry is required")
    private String industry;

    @Column(name = "is_active")
    private boolean active = true;

    public Tenant(String name, String email, String phone, String industry) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.industry = industry;
    }

    public void updateTenant(String name, String email, String phone, String industry){
        if (name != null && !name.isBlank()) this.name = name;
        if (email != null && !email.isBlank()) this.email = email;
        if (phone != null && !phone.isBlank()) this.phone = phone;
        if (industry != null && !industry.isBlank())  this.industry = industry;
    }

    public void toggleActiveStatus() {
        this.active = !this.active;
    }
}