-- Employee Expense Management System Database Initialization Script
-- This script creates the database schema and inserts sample data

-- Create database (run this separately if needed)
-- CREATE DATABASE expense_management_db;

-- Use the database
-- \c expense_management_db;

-- Create Employee table
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email_id VARCHAR(255) UNIQUE NOT NULL,
    mobile_no VARCHAR(20)
);

-- Create Expense Category table
CREATE TABLE IF NOT EXISTS expense_category (
    id SERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,
    max_limit NUMERIC(10, 2) NOT NULL,
    updated_at TIMESTAMP(6) NULL
);

-- Create Employee Expense table
CREATE TABLE IF NOT EXISTS employee_expense (
    id SERIAL PRIMARY KEY,
    emp_id INTEGER REFERENCES employee(id),
    approved_by INTEGER REFERENCES employee(id),
    expense_category_id INTEGER REFERENCES expense_category(id),
    description VARCHAR(500) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    expense_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    approval_date DATE NULL,
    rejection_reason VARCHAR(1000) NULL,
    amount_inr NUMERIC(12, 2) NULL,
    exchange_rate NUMERIC(10, 6) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
    created_by VARCHAR(255) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(255) DEFAULT 'SYSTEM',
    is_deleted BOOLEAN DEFAULT false NOT NULL
);

-- Create Employee Expense Documents table
CREATE TABLE IF NOT EXISTS employee_expense_docs (
    id SERIAL PRIMARY KEY,
    employee_expense_id INTEGER NOT NULL REFERENCES employee_expense(id),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    documents VARCHAR(1000) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    updated_at TIMESTAMP(6) NULL
);