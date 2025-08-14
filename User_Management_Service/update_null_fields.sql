-- Update existing records to set default values for null updated_by and updated_date columns
UPDATE user_mgnt 
SET updated_by = 'SYSTEM', updated_date = CURRENT_TIMESTAMP 
WHERE updated_by IS NULL OR updated_date IS NULL;

UPDATE user_role 
SET updated_by = 'SYSTEM', updated_date = CURRENT_TIMESTAMP 
WHERE updated_by IS NULL OR updated_date IS NULL;