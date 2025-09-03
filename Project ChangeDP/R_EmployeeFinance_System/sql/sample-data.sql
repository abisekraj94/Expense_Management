-- Sample data for Employee Expense Management System

-- Insert sample employees
INSERT INTO employee (first_name, last_name, email_id, mobile_no) VALUES
('John', 'Doe', 'john.doe@company.com', '+1-555-0101'),
('Jane', 'Smith', 'jane.smith@company.com', '+1-555-0102'),
('Mike', 'Johnson', 'mike.johnson@company.com', '+1-555-0103'),
('Sarah', 'Wilson', 'sarah.wilson@company.com', '+1-555-0104'),
('David', 'Brown', 'david.brown@company.com', '+1-555-0105'),
('Finance', 'Admin', 'finance.admin@company.com', '+1-555-0201'),
('HR', 'Manager', 'hr.manager@company.com', '+1-555-0202')
ON CONFLICT (email_id) DO NOTHING;

-- Insert sample expense categories
INSERT INTO expense_category (category, max_limit, is_active) VALUES
('Travel', 50000.00, true),
('Meals', 5000.00, true),
('Office Supplies', 2000.00, true),
('Training', 25000.00, true),
('Communication', 3000.00, true),
('Transportation', 10000.00, true),
('Accommodation', 15000.00, true),
('Medical', 20000.00, true),
('Entertainment', 8000.00, true),
('Miscellaneous', 5000.00, true)
ON CONFLICT (category) DO NOTHING;

-- Insert sample expenses
INSERT INTO employee_expense (
    emp_id, expense_category_id, description, amount, currency, 
    expense_date, status, amount_inr, exchange_rate
) VALUES
-- John Doe's expenses
(1, 1, 'Business trip to Mumbai for client meeting', 25000.00, 'INR', '2024-01-15', 'APPROVED', 25000.00, 1.000000),
(1, 2, 'Team lunch during project discussion', 1200.00, 'INR', '2024-01-16', 'APPROVED', 1200.00, 1.000000),
(1, 1, 'Flight tickets for Delhi conference', 450.00, 'USD', '2024-01-20', 'PENDING', 37350.00, 83.000000),

-- Jane Smith's expenses
(2, 4, 'AWS certification training course', 300.00, 'USD', '2024-01-10', 'APPROVED', 24900.00, 83.000000),
(2, 3, 'Laptop accessories and stationery', 150.00, 'USD', '2024-01-12', 'APPROVED', 12450.00, 83.000000),
(2, 2, 'Client dinner at 5-star restaurant', 8500.00, 'INR', '2024-01-18', 'PENDING', 8500.00, 1.000000),

-- Mike Johnson's expenses
(3, 6, 'Uber rides for client visits', 2500.00, 'INR', '2024-01-08', 'APPROVED', 2500.00, 1.000000),
(3, 1, 'Hotel booking for Bangalore trip', 180.00, 'USD', '2024-01-14', 'REJECTED', 14940.00, 83.000000),
(3, 5, 'Mobile bill reimbursement', 1800.00, 'INR', '2024-01-19', 'APPROVED', 1800.00, 1.000000),

-- Sarah Wilson's expenses
(4, 7, 'Hotel stay for training program', 220.00, 'USD', '2024-01-11', 'APPROVED', 18260.00, 83.000000),
(4, 2, 'Breakfast meeting with stakeholders', 750.00, 'INR', '2024-01-17', 'APPROVED', 750.00, 1.000000),

-- David Brown's expenses
(5, 8, 'Medical checkup reimbursement', 5500.00, 'INR', '2024-01-13', 'APPROVED', 5500.00, 1.000000),
(5, 9, 'Team building activity expenses', 120.00, 'USD', '2024-01-21', 'PENDING', 9960.00, 83.000000)
ON CONFLICT DO NOTHING;