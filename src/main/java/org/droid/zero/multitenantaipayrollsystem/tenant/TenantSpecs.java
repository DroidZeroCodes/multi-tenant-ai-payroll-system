package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TenantSpecs {
    public static Specification<Tenant> hasId(UUID providedId){
        return (root, ignored, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), providedId);
    }

    public static Specification<Tenant> containsName(String providedName){
        return (root, ignored, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + providedName.toLowerCase() + "%");
    }

    public static Specification<Tenant> containsEmail(String providedEmail){
        return (root, ignored, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + providedEmail.toLowerCase() + "%");
    }

    public static Specification<Tenant> containsPhone(String providedPhone){
        return (root, ignored, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + providedPhone + "%");
    }

    public static Specification<Tenant> containsIndustry(String providedIndustry){
        return (root, ignored, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("industry")), "%" + providedIndustry.toLowerCase() + "%");
    }

    public static Specification<Tenant> hasActiveStatus(boolean active){
        return (root, ignored,criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), active);
    }
}
