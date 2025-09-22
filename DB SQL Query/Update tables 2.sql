-- This first line makes sure we are working in the correct database.
USE civiclink_db;

-- This command modifies our existing 'workers' table.
ALTER TABLE workers
ADD COLUMN email VARCHAR(255) NOT NULL UNIQUE,
ADD COLUMN password VARCHAR(255) NOT NULL;