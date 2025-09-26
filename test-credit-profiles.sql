-- Test Customer Profiles for Credit Score Calculation
-- This script creates two contrasting profiles to demonstrate the credit score algorithm

-- Connect to credit_score_db database
\c credit_score_db;

-- Clear existing test data (if any)
DELETE FROM credit_scores WHERE customer_id IN (1001, 1002);

-- Profile 1: HIGH DEBT CUSTOMER (Expected Low Credit Score)
-- Sarah Wilson - Poor financial profile with high debt burden
INSERT INTO credit_scores (
    customer_id, customer_name, customer_email, credit_score, score_grade, 
    income, debt_to_income_ratio, payment_history_score, credit_utilization_ratio, 
    credit_age_months, number_of_accounts, last_updated, created_at
) VALUES (
    1001, 
    'Sarah Wilson', 
    'sarah.wilson@example.com', 
    0,  -- Will be calculated by the microservice
    'Poor', 
    35000.00,           -- Low income
    0.85,               -- Very high debt-to-income ratio (85%)
    45,                 -- Poor payment history (45/100)
    0.95,               -- Very high credit utilization (95%)
    18,                 -- Short credit history (1.5 years)
    8,                  -- Many accounts (potentially overextended)
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);

-- Profile 2: LOW DEBT CUSTOMER (Expected High Credit Score)
-- Michael Johnson - Excellent financial profile with low debt burden
INSERT INTO credit_scores (
    customer_id, customer_name, customer_email, credit_score, score_grade, 
    income, debt_to_income_ratio, payment_history_score, credit_utilization_ratio, 
    credit_age_months, number_of_accounts, last_updated, created_at
) VALUES (
    1002, 
    'Michael Johnson', 
    'michael.johnson@example.com', 
    0,  -- Will be calculated by the microservice
    'Excellent', 
    95000.00,           -- High income
    0.15,               -- Very low debt-to-income ratio (15%)
    98,                 -- Excellent payment history (98/100)
    0.05,               -- Very low credit utilization (5%)
    84,                 -- Long credit history (7 years)
    4,                  -- Moderate number of accounts
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);

-- Verify the data was inserted
SELECT 
    customer_id,
    customer_name,
    income,
    debt_to_income_ratio,
    payment_history_score,
    credit_utilization_ratio,
    credit_age_months,
    number_of_accounts
FROM credit_scores 
WHERE customer_id IN (1001, 1002)
ORDER BY customer_id;

-- Expected calculations based on the algorithm:
-- 
-- Sarah Wilson (High Debt - Expected Low Score):
-- Base: 300
-- Income: min(200, 35000/1000) = 35
-- Payment: min(150, 45*1.5) = 67
-- Debt: max(0, 100-(0.85*200)) = 0 (high debt penalty)
-- Utilization: max(0, 100-(0.95*200)) = 0 (high utilization penalty)
-- Age: min(100, 18/2) = 9
-- Accounts: min(50, 8*5) = 40
-- Expected Total: 300+35+67+0+0+9+40 = 451 (Poor)
--
-- Michael Johnson (Low Debt - Expected High Score):
-- Base: 300
-- Income: min(200, 95000/1000) = 200 (maxed out)
-- Payment: min(150, 98*1.5) = 147 (near max)
-- Debt: max(0, 100-(0.15*200)) = 70 (good debt management)
-- Utilization: max(0, 100-(0.05*200)) = 90 (excellent utilization)
-- Age: min(100, 84/2) = 42
-- Accounts: min(50, 4*5) = 20
-- Expected Total: 300+200+147+70+90+42+20 = 869 → capped at 850 (Excellent)