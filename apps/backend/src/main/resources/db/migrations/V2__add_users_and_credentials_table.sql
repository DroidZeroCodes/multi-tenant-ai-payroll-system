CREATE TABLE user_credentials
(
    id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version    INTEGER,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(255),
    is_active  BOOLEAN,
    verified   BOOLEAN      NOT NULL,
    last_login TIMESTAMP WITHOUT TIME ZONE,
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    CONSTRAINT pk_user_credentials PRIMARY KEY (id)
);

CREATE TABLE users
(
    id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version    INTEGER,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    tenant_id UUID NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE user_credentials
    ADD CONSTRAINT uc_user_credentials_user UNIQUE (user_id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE user_credentials
    ADD CONSTRAINT FK_USER_CREDENTIALS_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE user_credentials
    ADD CONSTRAINT FK_USER_CREDENTIALS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);