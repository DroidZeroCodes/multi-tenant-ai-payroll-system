ALTER TABLE user_credentials
    DROP CONSTRAINT fk_user_credentials_on_tenant;

ALTER TABLE user_credentials
    DROP CONSTRAINT fk_user_credentials_on_user;

ALTER TABLE users
    DROP CONSTRAINT fk_users_on_tenant;

CREATE TABLE user_tenant_role
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    version    INTEGER,
    user_id UUID NOT NULL,
    roles      VARCHAR(255),
    CONSTRAINT pk_usertenantrole PRIMARY KEY (id)
);

ALTER TABLE users
    RENAME COLUMN email TO contact_email;

ALTER TABLE users
    ADD is_active BOOLEAN;

ALTER TABLE users
    ADD is_verified BOOLEAN;

ALTER TABLE user_credentials
    RENAME COLUMN last_login TO last_login_at;

ALTER TABLE user_credentials
    RENAME COLUMN password TO password_hash;

ALTER TABLE user_tenant_role
    ADD CONSTRAINT FK_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE user_tenant_role
    ADD CONSTRAINT FK_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_credentials
    ADD CONSTRAINT FK_USER_CREDENTIALS_ON_ID FOREIGN KEY (id) REFERENCES users (id);

ALTER TABLE users
    DROP COLUMN tenant_id;

ALTER TABLE user_credentials
    DROP COLUMN is_active;

ALTER TABLE user_credentials
    DROP COLUMN role;

ALTER TABLE user_credentials
    DROP COLUMN tenant_id;

ALTER TABLE user_credentials
    DROP COLUMN user_id;

ALTER TABLE user_credentials
    DROP COLUMN verified;

ALTER TABLE user_credentials
    ALTER COLUMN email DROP NOT NULL;