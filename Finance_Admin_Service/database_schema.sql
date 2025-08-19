-- Finance Admin Service Database Schema
-- PostgreSQL Database Setup Script

-- Create database and user
CREATE DATABASE finance_admin_db;
CREATE USER finance_admin WITH PASSWORD 'admin123';
GRANT ALL PRIVILEGES ON DATABASE finance_admin_db TO finance_admin;

-- Connect to the database
\c finance_admin_db;

-- Create finance_admins table
CREATE TABLE finance_admins (
    admin_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    admin_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(50) DEFAULT 'FINANCE_ADMIN',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) DEFAULT 'SYSTEM',
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Create employees table
CREATE TABLE employees (
    employee_id BIGSERIAL PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    department VARCHAR(50),
    designation VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) DEFAULT 'SYSTEM',
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- Create expenses table
CREATE TABLE expenses (
    expense_id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    expense_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(100),
    approval_date DATE,
    rejection_reason VARCHAR(1000),
    amount_inr DECIMAL(12,2),
    exchange_rate DECIMAL(10,6),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) DEFAULT 'SYSTEM',
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_expense_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Insert sample finance admin users
INSERT INTO finance_admins (username, password, admin_name, email, role, is_active) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdZMfin5HjUzSvlFOaYzVPUq/2e', 'Finance Administrator', 'admin@company.com', 'FINANCE_ADMIN', true),
('finance.manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdZMfin5HjUzSvlFOaYzVPUq/2e', 'Finance Manager', 'finance.manager@company.com', 'FINANCE_ADMIN', true);

-- Insert sample employees
INSERT INTO employees (employee_name, email, department, designation) VALUES
('John Doe', 'john.doe@company.com', 'IT', 'Software Engineer'),
('Jane Smith', 'jane.smith@company.com', 'Marketing', 'Marketing Manager'),
('Mike Johnson', 'mike.johnson@company.com', 'Sales', 'Sales Executive'),
('Sarah Wilson', 'sarah.wilson@company.com', 'HR', 'HR Specialist'),
('David Brown', 'david.brown@company.com', 'Finance', 'Financial Analyst');

-- Insert sample expenses
INSERT INTO expenses (employee_id, description, amount, currency, expense_date, status, amount_inr, exchange_rate) VALUES
(1, 'Business Travel to Client Site', 1500.00, 'USD', '2024-01-15', 'PENDING', 124500.00, 83.00),
(1, 'Software License Purchase', 299.99, 'USD', '2024-01-10', 'APPROVED', 24899.17, 83.00),
(2, 'Marketing Conference Registration', 800.00, 'EUR', '2024-01-12', 'PENDING', 72000.00, 90.00),
(2, 'Promotional Materials', 450.00, 'USD', '2024-01-08', 'APPROVED', 37350.00, 83.00),
(3, 'Client Dinner', 120.00, 'GBP', '2024-01-14', 'PENDING', 12600.00, 105.00),
(3, 'Transportation', 75.50, 'USD', '2024-01-09', 'REJECTED', 6266.50, 83.00),
(4, 'Training Course Fee', 2500.00, 'INR', '2024-01-11', 'APPROVED', 2500.00, 1.00),
(4, 'Office Supplies', 180.00, 'USD', '2024-01-13', 'PENDING', 14940.00, 83.00),
(5, 'Financial Software Subscription', 99.99, 'USD', '2024-01-07', 'APPROVED', 8299.17, 83.00),
(5, 'Business Books', 45.00, 'USD', '2024-01-16', 'PENDING', 3735.00, 83.00);

-- Update approved/rejected expenses with approval details
UPDATE expenses 
SET approved_by = 'admin', approval_date = CURRENT_DATE 
WHERE status IN ('APPROVED', 'REJECTED');

UPDATE expenses 
SET rejection_reason = 'Receipt not clear and expense category not justified' 
WHERE status = 'REJECTED';

-- Create indexes for better performance
CREATE INDEX idx_expenses_employee_id ON expenses(employee_id);
CREATE INDEX idx_expenses_status ON expenses(status);
CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_finance_admins_username ON finance_admins(username);