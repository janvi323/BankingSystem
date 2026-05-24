-- ✅ PostgreSQL Index Creation Script
-- Run this in pgAdmin or psql terminal to add indexes

-- ===== CUSTOMER TABLE INDEXES =====
CREATE INDEX IF NOT EXISTS idx_customer_email ON customer(email);
CREATE INDEX IF NOT EXISTS idx_customer_role ON customer(role);
CREATE INDEX IF NOT EXISTS idx_customer_credit_score ON customer(credit_score);
CREATE INDEX IF NOT EXISTS idx_customer_income ON customer(income);

-- ===== LOAN TABLE INDEXES =====
CREATE INDEX IF NOT EXISTS idx_loan_customer_id ON loan(customer_id);
CREATE INDEX IF NOT EXISTS idx_loan_status ON loan(status);
CREATE INDEX IF NOT EXISTS idx_loan_application_date ON loan(application_date);
CREATE INDEX IF NOT EXISTS idx_loan_customer_status ON loan(customer_id, status);

-- ===== EMI TABLE INDEXES =====
CREATE INDEX IF NOT EXISTS idx_emi_loan_id ON emi(loan_id);
CREATE INDEX IF NOT EXISTS idx_emi_due_date ON emi(due_date);
CREATE INDEX IF NOT EXISTS idx_emi_status ON emi(status);
CREATE INDEX IF NOT EXISTS idx_emi_paid_date ON emi(payment_date);

-- ===== CREDIT_SCORES TABLE INDEXES =====
CREATE INDEX IF NOT EXISTS idx_credit_customer_id ON credit_scores(customer_id);
CREATE INDEX IF NOT EXISTS idx_credit_score ON credit_scores(credit_score);
CREATE INDEX IF NOT EXISTS idx_credit_created_at ON credit_scores(created_at);

-- ===== OPTIMIZE DATABASE =====
ANALYZE;
VACUUM ANALYZE;

-- ===== VERIFY INDEXES CREATED =====
-- Run these commands to verify:
-- SELECT indexname FROM pg_indexes WHERE tablename='customer';
-- SELECT indexname FROM pg_indexes WHERE tablename='loan';
-- SELECT indexname FROM pg_indexes WHERE tablename='emi';
-- SELECT indexname FROM pg_indexes WHERE tablename='credit_scores';
