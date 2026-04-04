package org.droid.zero.multitenantaipayrollsystem.modules.user.constant;

public enum UserRole {
    SUPER_ADMIN,
    TENANT_ADMIN,
    HR_OFFICER,
    PAYROLL_OFFICER,
    EMPLOYEE;

    public String value() {
        return this.name();
    }
}