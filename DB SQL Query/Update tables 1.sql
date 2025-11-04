USE civiclink_db;

CREATE TABLE workers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(255)
);

ALTER TABLE issues
ADD COLUMN assignedTo INT NULL,
ADD FOREIGN KEY (assignedTo) REFERENCES workers(id);