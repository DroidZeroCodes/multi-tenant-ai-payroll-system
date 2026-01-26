ALTER TABLE allowances
    ADD CONSTRAINT uc_allowance_name_tenant_id UNIQUE (name, tenant_id);

ALTER TABLE departments
    ADD CONSTRAINT uc_department_name_tenant_id UNIQUE (name, tenant_id);

ALTER TABLE employees
    ADD CONSTRAINT uc_employee_employee_code_tenant_id UNIQUE (employee_code, tenant_id);

ALTER TABLE employees
    ALTER COLUMN address SET NOT NULL;

ALTER TABLE allowances
    ALTER COLUMN allowance_type SET NOT NULL;

ALTER TABLE allowances
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE departments
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE salary_structures
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN dob SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN employee_code SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN first_name SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN gender SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN joining_date SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN last_name SET NOT NULL;

ALTER TABLE allowances
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE departments
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE salary_structures
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE employees
    ALTER COLUMN phone SET NOT NULL;