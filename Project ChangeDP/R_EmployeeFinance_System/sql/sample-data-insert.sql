-- Insert sample expense categories
INSERT INTO expense_category (category, max_limit, is_active, created_at, updated_at) VALUES
('Travel', 50000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Food', 5000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Accommodation', 15000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Office Supplies', 2000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Training', 25000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample employees (assuming employee table exists)
INSERT INTO employee (id, first_name, last_name, email_id, mobile_no, is_active, created_at) VALUES
(1, 'John', 'Doe', 'john.doe@company.com', '9876543210', true, CURRENT_TIMESTAMP),
(2, 'Jane', 'Smith', 'jane.smith@company.com', '9876543211', true, CURRENT_TIMESTAMP),
(3, 'Mike', 'Johnson', 'mike.johnson@company.com', '9876543212', true, CURRENT_TIMESTAMP),
(4, 'Sarah', 'Wilson', 'sarah.wilson@company.com', '9876543213', true, CURRENT_TIMESTAMP),
(5, 'David', 'Brown', 'david.brown@company.com', '9876543214', true, CURRENT_TIMESTAMP),
(6, 'Admin', 'User', 'admin@company.com', '9876543215', true, CURRENT_TIMESTAMP);

-- Insert sample employee expenses
INSERT INTO employee_expense (emp_id, expense_category_id, description, amount, currency, expense_date, status, amount_inr, exchange_rate, approved_by, approval_date) VALUES
(1, 1, 'Flight tickets for client meeting', 500.00, 'USD', '2024-01-15', 'APPROVED', 41500.00, 83.00, 6, '2024-01-16'),
(2, 2, 'Team lunch with clients', 150.00, 'USD', '2024-01-20', 'PENDING', 12450.00, 83.00, NULL, NULL),
(3, 3, 'Hotel stay for conference', 300.00, 'EUR', '2024-01-25', 'APPROVED', 27000.00, 90.00, 6, '2024-01-26'),
(1, 4, 'Office stationery purchase', 75.00, 'USD', '2024-02-01', 'REJECTED', 6225.00, 83.00, 6, '2024-02-02'),
(4, 5, 'Professional training course', 800.00, 'USD', '2024-02-05', 'PENDING', 66400.00, 83.00, NULL, NULL),
(5, 1, 'Domestic travel expenses', 5000.00, 'INR', '2024-02-10', 'APPROVED', 5000.00, 1.00, 6, '2024-02-11');

-- Update rejection reason for rejected expense
UPDATE employee_expense SET rejection_reason = 'Exceeds category limit for office supplies' WHERE id = 4;

-- Insert sample expense documents
INSERT INTO employee_expense_docs (employee_expense_id, documents, is_active, created_at, updated_at) VALUES
(1, 'flight_receipt_001.pdf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'restaurant_bill_002.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'hotel_invoice_003.pdf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'stationery_receipt_004.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'training_certificate_005.pdf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'travel_receipts_006.pdf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);