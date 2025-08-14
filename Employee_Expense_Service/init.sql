-- Employee Expense Service Database Initialization Script
-- Creates tables for expense management system

-- Create expense_category table
CREATE TABLE IF NOT EXISTS expense_category (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL UNIQUE,
    max_limit DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create employee_expense table
CREATE TABLE IF NOT EXISTS employee_expense (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    expense_category_id BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    amount_inr DECIMAL(10,2) NOT NULL,
    description VARCHAR(500) NOT NULL,
    date_of_expense DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    reviewed_by BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (expense_category_id) REFERENCES expense_category(id)
);

-- Create employee_expense_docs table
CREATE TABLE IF NOT EXISTS employee_expense_docs (
    id BIGSERIAL PRIMARY KEY,
    employee_expense_id BIGINT NOT NULL,
    documents VARCHAR(1000) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_expense_id) REFERENCES employee_expense(id)
);

-- Insert default expense categories
INSERT INTO expense_category (category, max_limit) VALUES
('hardware', 50000.00),
('food', 5000.00),
('certification', 25000.00),
('travel', 100000.00),
('accommodation', 15000.00)
ON CONFLICT (category) DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_employee_expense_employee_id ON employee_expense(employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_expense_status ON employee_expense(status);
CREATE INDEX IF NOT EXISTS idx_employee_expense_date ON employee_expense(date_of_expense);
CREATE INDEX IF NOT EXISTS idx_employee_expense_active ON employee_expense(is_active);
CREATE INDEX IF NOT EXISTS idx_expense_category_active ON expense_category(is_active);
CREATE INDEX IF NOT EXISTS idx_employee_expense_docs_expense_id ON employee_expense_docs(employee_expense_id);