CREATE TABLE allowances
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    deleted_at     TIMESTAMP WITHOUT TIME ZONE,
    version        INTEGER,
    name           VARCHAR(255),
    description    VARCHAR(255),
    allowance_type VARCHAR(255),
    amount         DOUBLE PRECISION NOT NULL,
    is_taxable     BOOLEAN,
    CONSTRAINT pk_allowances PRIMARY KEY (id)
);

ALTER TABLE allowances
    ADD CONSTRAINT FK_ALLOWANCES_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);