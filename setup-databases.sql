-- Create databases for the banking system and credit score service
CREATE DATABASE loan_db;
CREATE DATABASE credit_score_db;

-- Grant all privileges to postgres user
GRANT ALL PRIVILEGES ON DATABASE loan_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE credit_score_db TO postgres;