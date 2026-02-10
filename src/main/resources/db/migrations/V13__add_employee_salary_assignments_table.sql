CREATE TABLE employee_salary_assignments
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    deleted_at     TIMESTAMP WITHOUT TIME ZONE,
    version        INTEGER,
    salary_structure_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    effective_date date         NOT NULL,
    end_date       date         NOT NULL,
    assigned_by    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_employee_salary_assignments PRIMARY KEY (id)
);

ALTER TABLE employee_salary_assignments
    ADD CONSTRAINT uc_19eb3a4653f662505ef72dc06 UNIQUE (salary_structure_id, employee_id, tenant_id);

ALTER TABLE employee_salary_assignments
    ADD CONSTRAINT FK_EMPLOYEE_SALARY_ASSIGNMENTS_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES employees (id);

ALTER TABLE employee_salary_assignments
    ADD CONSTRAINT FK_EMPLOYEE_SALARY_ASSIGNMENTS_ON_SALARY_STRUCTURE FOREIGN KEY (salary_structure_id) REFERENCES salary_structures (id);

ALTER TABLE employee_salary_assignments
    ADD CONSTRAINT FK_EMPLOYEE_SALARY_ASSIGNMENTS_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);