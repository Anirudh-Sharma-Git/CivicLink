-- First, delete all issues reported by the user with ID
DELETE FROM issues WHERE reportedBy = 9;

-- Now that their issues are gone, you can safely delete the user with ID 
DELETE FROM users WHERE id = 9;