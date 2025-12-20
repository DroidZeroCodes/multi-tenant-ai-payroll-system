CREATE TABLE tenants
(
    id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version    INTEGER,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    industry   VARCHAR(255) NOT NULL,
    is_active  BOOLEAN,
    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

ALTER TABLE tenants
    ADD CONSTRAINT uc_tenants_email UNIQUE (email);

ALTER TABLE tenants
    ADD CONSTRAINT uc_tenants_name UNIQUE (name);

ALTER TABLE tenants
    ADD CONSTRAINT uc_tenants_phone UNIQUE (phone);