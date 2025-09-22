-- This first line makes sure we are working in the correct database.
USE civiclink_db;

-- This is the new table for the municipal workers.
-- Administrators will manage this list from their website.
CREATE TABLE workers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(255)
);

-- This command modifies our existing 'issues' table.
-- It adds a new column to track which worker is assigned to the issue.
ALTER TABLE issues
ADD COLUMN assignedTo INT NULL,
ADD FOREIGN KEY (assignedTo) REFERENCES workers(id);