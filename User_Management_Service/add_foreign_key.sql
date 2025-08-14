-- Add foreign key constraint to existing user_mgnt table
ALTER TABLE user_mgnt 
ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES user_role(role_id);