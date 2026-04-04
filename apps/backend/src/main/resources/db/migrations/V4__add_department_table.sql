CREATE TABLE department
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    deleted_at  TIMESTAMP WITHOUT TIME ZONE,
    version     INTEGER,
    name        VARCHAR(255),
    description VARCHAR(255),
    is_active   BOOLEAN,
    CONSTRAINT pk_department PRIMARY KEY (id)
);

ALTER TABLE department
    ADD CONSTRAINT FK_DEPARTMENT_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);