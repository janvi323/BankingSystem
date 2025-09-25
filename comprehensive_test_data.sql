-- Comprehensive test data for banking system

-- First, clean up and fix existing data
UPDATE customer SET role = 'CUSTOMER' WHERE role = 'customer';
UPDATE customer SET role = 'ADMIN' WHERE role = 'admin';

-- Insert test customers if they don't exist
INSERT INTO customer (name, email, password, role) 
SELECT 'Test Customer', 'customer@test.com', 'password123', 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'customer@test.com');

INSERT INTO customer (name, email, password, role) 
SELECT 'Admin User', 'admin@test.com', 'admin123', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'admin@test.com');

INSERT INTO customer (name, email, password, role) 
SELECT 'John Doe', 'john@test.com', 'password123', 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'john@test.com');

INSERT INTO customer (name, email, password, role) 
SELECT 'Jane Smith', 'jane@test.com', 'password123', 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'jane@test.com');

-- Insert test loans
-- Get customer IDs first
DO $$
DECLARE
    customer1_id INTEGER;
    customer2_id INTEGER;
    customer3_id INTEGER;
BEGIN
    SELECT id INTO customer1_id FROM customer WHERE email = 'customer@test.com' LIMIT 1;
    SELECT id INTO customer2_id FROM customer WHERE email = 'john@test.com' LIMIT 1;
    SELECT id INTO customer3_id FROM customer WHERE email = 'jane@test.com' LIMIT 1;
    
    -- Insert loans if customers exist
    IF customer1_id IS NOT NULL THEN
        INSERT INTO loan (amount, purpose, tenure, status, application_date, customer_id)
        SELECT 50000, 'Home Purchase', 240, 'PENDING', CURRENT_DATE - INTERVAL '5 days', customer1_id
        WHERE NOT EXISTS (SELECT 1 FROM loan WHERE customer_id = customer1_id AND amount = 50000);
        
        INSERT INTO loan (amount, purpose, tenure, status, application_date, customer_id)
        SELECT 15000, 'Car Loan', 60, 'APPROVED', CURRENT_DATE - INTERVAL '10 days', customer1_id
        WHERE NOT EXISTS (SELECT 1 FROM loan WHERE customer_id = customer1_id AND amount = 15000);
    END IF;
    
    IF customer2_id IS NOT NULL THEN
        INSERT INTO loan (amount, purpose, tenure, status, application_date, customer_id)
        SELECT 25000, 'Education', 84, 'PENDING', CURRENT_DATE - INTERVAL '3 days', customer2_id
        WHERE NOT EXISTS (SELECT 1 FROM loan WHERE customer_id = customer2_id AND amount = 25000);
        
        INSERT INTO loan (amount, purpose, tenure, status, application_date, customer_id)
        SELECT 8000, 'Personal', 36, 'REJECTED', CURRENT_DATE - INTERVAL '15 days', customer2_id
        WHERE NOT EXISTS (SELECT 1 FROM loan WHERE customer_id = customer2_id AND amount = 8000);
    END IF;
    
    IF customer3_id IS NOT NULL THEN
        INSERT INTO loan (amount, purpose, tenure, status, application_date, customer_id)
        SELECT 100000, 'Business', 120, 'PENDING', CURRENT_DATE - INTERVAL '2 days', customer3_id
        WHERE NOT EXISTS (SELECT 1 FROM loan WHERE customer_id = customer3_id AND amount = 100000);
    END IF;
END $$;

-- Display results
SELECT 'Customers:' as table_name, COUNT(*) as count FROM customer
UNION ALL
SELECT 'Loans:', COUNT(*) FROM loan;

SELECT c.name as customer_name, c.role, l.amount, l.purpose, l.status, l.application_date
FROM customer c 
LEFT JOIN loan l ON c.id = l.customer_id 
ORDER BY c.name, l.application_date DESC;