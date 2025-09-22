-- This first line makes sure we are working in the correct database.
USE civiclink_db;

-- This command modifies our existing 'issues' table.
ALTER TABLE issues
ADD COLUMN priority ENUM('None', 'Low', 'Medium', 'High') NOT NULL DEFAULT 'None';