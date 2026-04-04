CREATE TABLE user_tenant_roles_mapping
(
    user_tenant_role_id UUID NOT NULL,
    roles VARCHAR(255)
);

ALTER TABLE user_tenant_roles_mapping
    ADD CONSTRAINT fk_user_tenant_role_mapping FOREIGN KEY (user_tenant_role_id) REFERENCES user_tenant_role (id);

ALTER TABLE user_tenant_role
    DROP COLUMN roles;