-- This first line tells MySQL which database we want to work in.
USE civiclink_db;

-- This creates the 'users' table.
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phoneNumber VARCHAR(20) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50) DEFAULT 'citizen',
    points INT DEFAULT 0,
    totalReports INT DEFAULT 0,
    resolvedIssues INT DEFAULT 0,
    memberSince TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- This creates the 'issues' table and links it to the 'users' table.
CREATE TABLE issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    imageUrl VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    status VARCHAR(50) DEFAULT 'Pending',
    upvotes INT DEFAULT 0,
    reportedBy INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reportedBy) REFERENCES users(id)
);