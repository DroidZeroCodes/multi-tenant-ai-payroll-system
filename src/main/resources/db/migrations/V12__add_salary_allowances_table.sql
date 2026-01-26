CREATE TABLE salary_allowances
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    deleted_at     TIMESTAMP WITHOUT TIME ZONE,
    version        INTEGER,
    salary_structure_id UUID NOT NULL,
    allowance_id UUID NOT NULL,
    effective_date date         NOT NULL,
    expiry_date    date         NOT NULL,
    assigned_by    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_salary_allowances PRIMARY KEY (id)
);

ALTER TABLE salary_allowances
    ADD CONSTRAINT uc_salary_allowance_tenant UNIQUE (salary_structure_id, allowance_id, tenant_id);

ALTER TABLE salary_allowances
    ADD CONSTRAINT FK_SALARY_ALLOWANCES_ON_ALLOWANCE FOREIGN KEY (allowance_id) REFERENCES allowances (id);

ALTER TABLE salary_allowances
    ADD CONSTRAINT FK_SALARY_ALLOWANCES_ON_SALARY_STRUCTURE FOREIGN KEY (salary_structure_id) REFERENCES salary_structures (id);

ALTER TABLE salary_allowances
    ADD CONSTRAINT FK_SALARY_ALLOWANCES_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);