-- Finance Admin Service Database Initialization Script
-- Creates sample data for testing and development

-- Insert sample finance admin users
INSERT INTO finance_admins (username, password, admin_name, email, role, is_active, created_by, updated_by, is_deleted, created_at, updated_at) 
VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdZMfin5HjUzSvlFOaYzVPUq/2e', 'Finance Administrator', 'admin@company.com', 'FINANCE_ADMIN', true, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('finance.manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdZMfin5HjUzSvlFOaYzVPUq/2e', 'Finance Manager', 'finance.manager@company.com', 'FINANCE_ADMIN', true, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Insert sample employees
INSERT INTO employees (employee_name, email, department, designation, created_by, updated_by, is_deleted, created_at, updated_at) 
VALUES 
('John Doe', 'john.doe@company.com', 'IT', 'Software Engineer', 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Smith', 'jane.smith@company.com', 'Marketing', 'Marketing Manager', 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mike Johnson', 'mike.johnson@company.com', 'Sales', 'Sales Executive', 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sarah Wilson', 'sarah.wilson@company.com', 'HR', 'HR Specialist', 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('David Brown', 'david.brown@company.com', 'Finance', 'Financial Analyst', 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Insert sample expenses
INSERT INTO expenses (employee_id, description, amount, currency, expense_date, status, amount_inr, exchange_rate, created_by, updated_by, is_deleted, created_at, updated_at) 
VALUES 
(1, 'Business Travel to Client Site', 1500.00, 'USD', '2024-01-15', 'PENDING', 124500.00, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Software License Purchase', 299.99, 'USD', '2024-01-10', 'APPROVED', 24899.17, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Marketing Conference Registration', 800.00, 'EUR', '2024-01-12', 'PENDING', 72000.00, 90.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Promotional Materials', 450.00, 'USD', '2024-01-08', 'APPROVED', 37350.00, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Client Dinner', 120.00, 'GBP', '2024-01-14', 'PENDING', 12600.00, 105.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Transportation', 75.50, 'USD', '2024-01-09', 'REJECTED', 6266.50, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Training Course Fee', 2500.00, 'INR', '2024-01-11', 'APPROVED', 2500.00, 1.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Office Supplies', 180.00, 'USD', '2024-01-13', 'PENDING', 14940.00, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Financial Software Subscription', 99.99, 'USD', '2024-01-07', 'APPROVED', 8299.17, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Business Books', 45.00, 'USD', '2024-01-16', 'PENDING', 3735.00, 83.00, 'SYSTEM', 'SYSTEM', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update approved/rejected expenses with approval details
UPDATE expenses 
SET approved_by = 'admin', approval_date = CURRENT_DATE 
WHERE status IN ('APPROVED', 'REJECTED');

UPDATE expenses 
SET rejection_reason = 'Receipt not clear and expense category not justified' 
WHERE status = 'REJECTED';