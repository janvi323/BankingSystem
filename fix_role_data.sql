-- Fix existing role data in the database
-- Update lowercase 'customer' to uppercase 'CUSTOMER'
UPDATE customer SET role = 'CUSTOMER' WHERE role = 'customer';

-- Update lowercase 'admin' to uppercase 'ADMIN'
UPDATE customer SET role = 'ADMIN' WHERE role = 'admin';

-- Verify the updates
SELECT role, COUNT(*) FROM customer GROUP BY role;
