-- Insert test users for authentication
INSERT INTO employee (first_name, last_name, email_id, mobile_no, password) VALUES 
('Admin', 'User', 'admin@company.com', '1234567890', 'password'),
('John', 'Employee', 'employee@company.com', '0987654321', 'password')
ON CONFLICT (email_id) DO NOTHING;