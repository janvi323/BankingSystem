-- Backfill NULL values for new EMI columns (safe to run multiple times)
UPDATE emi SET late_fee = 0.0 WHERE late_fee IS NULL;
UPDATE emi SET partial_amount_paid = 0.0 WHERE partial_amount_paid IS NULL;
