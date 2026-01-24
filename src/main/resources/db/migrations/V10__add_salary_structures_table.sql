CREATE TABLE salary_structures
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    deleted_at     TIMESTAMP WITHOUT TIME ZONE,
    version        INTEGER,
    name           VARCHAR(255),
    description    VARCHAR(255),
    base_salary    DOUBLE PRECISION NOT NULL,
    effective_date date             NOT NULL,
    is_active      BOOLEAN,
    CONSTRAINT pk_salary_structures PRIMARY KEY (id)
);

ALTER TABLE salary_structures
    ADD CONSTRAINT FK_SALARY_STRUCTURES_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);