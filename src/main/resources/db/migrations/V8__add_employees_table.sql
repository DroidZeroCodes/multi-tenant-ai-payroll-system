CREATE TABLE employees
(
    id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    deleted_at      TIMESTAMP WITHOUT TIME ZONE,
    version         INTEGER,
    employee_code   VARCHAR(255),
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    email           VARCHAR(255),
    phone           VARCHAR(255),
    dob             TIMESTAMP WITHOUT TIME ZONE,
    address         VARCHAR(255),
    gender          VARCHAR(255),
    joining_date    TIMESTAMP WITHOUT TIME ZONE,
    employment_type VARCHAR(255),
    status          VARCHAR(255),
    user_id UUID,
    department_id UUID,
    position_id UUID,
    CONSTRAINT pk_employees PRIMARY KEY (id)
);

ALTER TABLE employees
    ADD CONSTRAINT FK_EMPLOYEES_ON_DEPARTMENT FOREIGN KEY (department_id) REFERENCES departments (id);

ALTER TABLE employees
    ADD CONSTRAINT FK_EMPLOYEES_ON_POSITION FOREIGN KEY (position_id) REFERENCES positions (id);

ALTER TABLE employees
    ADD CONSTRAINT FK_EMPLOYEES_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE employees
    ADD CONSTRAINT FK_EMPLOYEES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);