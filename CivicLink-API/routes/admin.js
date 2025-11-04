// routes/admin.js

const express = require('express');
const mysql = require('mysql2/promise');
const router = express.Router();

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE
};


router.get('/workers', async (req, res) => {
    try {
        const connection = await mysql.createConnection(dbConfig);
        const [workers] = await connection.execute("SELECT * FROM workers");
        await connection.end();
        res.status(200).json(workers);
    } catch (error) {
        console.error("Error fetching workers:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

router.put('/issue/:issueId', async (req, res) => {
    try {
        const { issueId } = req.params;
        const { status, priority, assignedTo } = req.body;

        
        if (!status || !priority) {
            return res.status(400).json({ message: "Status and priority are required." });
        }

        
        const workerId = assignedTo ? assignedTo : null;

        const connection = await mysql.createConnection(dbConfig);
        const sql = "UPDATE issues SET status = ?, priority = ?, assignedTo = ? WHERE id = ?";
        await connection.execute(sql, [status, priority, workerId, issueId]);
        await connection.end();

        res.status(200).json({ message: "Issue updated successfully!" });
    } catch (error) {
        console.error("Error updating issue:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

module.exports = router;
