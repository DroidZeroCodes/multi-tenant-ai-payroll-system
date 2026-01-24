ALTER TABLE employees
    ADD CONSTRAINT uc_employees_user UNIQUE (user_id);

ALTER TABLE employees
    ALTER COLUMN user_id SET NOT NULL;