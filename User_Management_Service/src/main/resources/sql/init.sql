-- User Management Service Database Initialization Script
-- This script creates the database schema and inserts initial data

-- Create database (run this separately if needed)
-- CREATE DATABASE user_management_db;

-- Connect to the database
-- \c user_management_db;

-- Create user_role table
CREATE TABLE IF NOT EXISTS user_role (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_description VARCHAR(255),
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM',
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_mgnt table
CREATE TABLE IF NOT EXISTS user_mgnt (
    user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM',
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES user_role(role_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_mgnt_email ON user_mgnt(email);
CREATE INDEX IF NOT EXISTS idx_user_mgnt_role_id ON user_mgnt(role_id);
CREATE INDEX IF NOT EXISTS idx_user_mgnt_is_active ON user_mgnt(is_active);
CREATE INDEX IF NOT EXISTS idx_user_role_name ON user_role(role_name);

-- Insert initial roles
INSERT INTO user_role (role_name, role_description, created_by, created_date) 
VALUES 
    ('EMPLOYEE', 'Standard employee role with basic access', 'SYSTEM', CURRENT_TIMESTAMP),
    ('FINANCE_ADMIN', 'Finance administrator role with elevated access', 'SYSTEM', CURRENT_TIMESTAMP)
ON CONFLICT (role_name) DO NOTHING;

-- Insert sample users (optional - for testing)
-- Note: Password is 'password123' encoded with BCrypt
INSERT INTO user_mgnt (name, email, password, department, role_id, is_active, created_by, created_date, updated_by, updated_date)
VALUES 
    ('John Doe', 'john.doe@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5ldxjjSKNi6cBKJ1l1JG', 'IT', 
     (SELECT role_id FROM user_role WHERE role_name = 'EMPLOYEE'), TRUE, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
    ('Jane Smith', 'jane.smith@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5ldxjjSKNi6cBKJ1l1JG', 'Finance', 
     (SELECT role_id FROM user_role WHERE role_name = 'FINANCE_ADMIN'), TRUE, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
    ('Bob Johnson', 'bob.johnson@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFe5ldxjjSKNi6cBKJ1l1JG', 'HR', 
     (SELECT role_id FROM user_role WHERE role_name = 'EMPLOYEE'), TRUE, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Note: Automatic updated_date triggers removed due to Spring Boot SQL parser limitations
-- The application will handle updated_date updates programmatically

-- Grant permissions (adjust as needed for your environment)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your_app_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your_app_user;

-- Verify the setup
SELECT 'Database initialization completed successfully' as status;
SELECT COUNT(*) as role_count FROM user_role;
SELECT COUNT(*) as user_count FROM user_mgnt;