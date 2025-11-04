-- delete all issues reported by the user with ID
DELETE FROM issues WHERE reportedBy = 17;

-- safely delete the user with ID 
DELETE FROM users WHERE id = 12;