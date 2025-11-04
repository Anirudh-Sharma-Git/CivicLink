USE civiclink_db;

ALTER TABLE issues
ADD COLUMN priority ENUM('None', 'Low', 'Medium', 'High') NOT NULL DEFAULT 'None';