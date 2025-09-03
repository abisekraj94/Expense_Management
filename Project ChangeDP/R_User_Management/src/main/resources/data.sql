-- Insert Departments
INSERT INTO department (dep_name) VALUES 
('Information Technology'),
('Human Resources'),
('Finance'),
('Operations'),
('Marketing');

-- Insert Roles
INSERT INTO roles (role_name, dep_id) VALUES 
('ADMIN', 1),
('EMPLOYEE', 1),
('HR_MANAGER', 2),
('FINANCE_MANAGER', 3),
('OPERATIONS_MANAGER', 4);

-- Insert Default Admin User (password: admin123)
INSERT INTO employee (first_name, last_name, email_id, mobile_no, password) VALUES 
('System', 'Administrator', 'admin@company.com', '9999999999', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.');

-- Insert Default Employee User (password: employee123)
INSERT INTO employee (first_name, last_name, email_id, mobile_no, password) VALUES 
('John', 'Doe', 'employee@company.com', '8888888888', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.');

-- Assign Admin Role to Admin User
INSERT INTO employee_role_mapping (emp_id, role_id) VALUES (1, 1);

-- Assign Employee Role to Employee User
INSERT INTO employee_role_mapping (emp_id, role_id) VALUES (2, 2);