// routes/issues.js - The "rulebook" for handling civic issues

const express = require('express');
const mysql = require('mysql2/promise');
const router = express.Router();

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE
};

// --- Endpoint 1: GET /api/issues ---
// This rule gets a list of ALL issues for the main Home screen feed.
router.get('/', async (req, res) => {
    try {
        const connection = await mysql.createConnection(dbConfig);
        const sql = `
            SELECT 
                i.id, i.category, i.description, i.imageUrl, i.latitude, i.longitude, i.status, i.upvotes, i.createdAt,
                u.name as reportedByName 
            FROM issues i
            JOIN users u ON i.reportedBy = u.id
            ORDER BY i.createdAt DESC
        `;
        const [issues] = await connection.execute(sql);
        await connection.end();
        res.status(200).json(issues);
    } catch (error) {
        console.error("Error fetching all issues:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

// --- THIS IS THE NEW ENDPOINT ---
// --- Endpoint 2: GET /api/issues/user/:userId ---
// This rule gets a list of issues for ONLY a specific user. This is for the "My Reports" screen.
router.get('/user/:userId', async (req, res) => {
    try {
        const userId = req.params.userId; // Get the user's ID from the URL

        const connection = await mysql.createConnection(dbConfig);
        const sql = `
            SELECT 
                i.id, i.category, i.description, i.imageUrl, i.latitude, i.longitude, i.status, i.upvotes, i.createdAt
            FROM issues i
            WHERE i.reportedBy = ? 
            ORDER BY i.createdAt DESC
        `;
        const [issues] = await connection.execute(sql, [userId]);
        await connection.end();

        res.status(200).json(issues);
    } catch (error) {
        console.error("Error fetching user issues:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});


// --- Endpoint 3: POST /api/issues ---
// This rule handles the "Submit Issue" button. (Same as before)
router.post('/', async (req, res) => {
    const { category, description, latitude, longitude, reportedBy } = req.body;
    if (!category || !description || !latitude || !longitude || !reportedBy) {
        return res.status(400).json({ message: "All fields are required." });
    }
    try {
        const connection = await mysql.createConnection(dbConfig);
        const sql = "INSERT INTO issues (category, description, latitude, longitude, reportedBy) VALUES (?, ?, ?, ?, ?)";
        const [result] = await connection.execute(sql, [category, description, latitude, longitude, reportedBy]);
        await connection.end();
        res.status(201).json({ message: "Issue reported successfully!", issueId: result.insertId });
    } catch (error) {
        console.error("Error creating issue:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

module.exports = router;