CREATE TABLE positions
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    deleted_at  TIMESTAMP WITHOUT TIME ZONE,
    version     INTEGER,
    title       VARCHAR(255),
    description VARCHAR(255),
    level       VARCHAR(255),
    is_active   BOOLEAN,
    CONSTRAINT pk_positions PRIMARY KEY (id)
);

ALTER TABLE positions
    ADD CONSTRAINT FK_POSITIONS_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);