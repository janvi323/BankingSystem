-- Credit Score Database Setup Script
-- Run this script in PostgreSQL to set up the credit score database

-- Create database for credit score service
CREATE DATABASE credit_score_db;

-- Connect to the database
\c credit_score_db;

-- The tables will be automatically created by Spring Boot JPA
-- This script is provided for reference and manual setup if needed

-- Sample data can be inserted after the application starts
-- Example:
/*
INSERT INTO credit_scores (customer_id, customer_name, customer_email, credit_score, score_grade, income, 
                          debt_to_income_ratio, payment_history_score, credit_utilization_ratio, 
                          credit_age_months, number_of_accounts, last_updated, created_at)
VALUES (1, 'John Doe', 'john.doe@example.com', 720, 'Good', 65000.00, 0.35, 80, 0.30, 48, 4, 
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
*/

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE credit_score_db TO postgres;