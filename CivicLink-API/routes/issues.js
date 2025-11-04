// routes/issues.js 

const express = require('express');
const mysql = require('mysql2/promise');
const multer = require('multer');
const path = require('path');
const router = express.Router();

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE
};

//Multer Configuration
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        const uniqueSuffix = Date.now() + path.extname(file.originalname);
        cb(null, file.fieldname + '-' + uniqueSuffix);
    }
});

const upload = multer({ storage: storage });

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

router.get('/user/:userId', async (req, res) => {
    try {
        const userId = req.params.userId;
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


router.post('/', upload.single('image'), async (req, res) => {
    const { category, description, latitude, longitude, reportedBy } = req.body;
    const imageFile = req.file;

    if (!category || !description || !latitude || !longitude || !reportedBy || !imageFile) {
        return res.status(400).json({ message: "All fields, including an image, are required." });
    }

    try {
        const imageUrl = `${process.env.PUBLIC_SERVER_URL}/uploads/${imageFile.filename}`;
        const connection = await mysql.createConnection(dbConfig);
        const sql = "INSERT INTO issues (category, description, latitude, longitude, reportedBy, imageUrl) VALUES (?, ?, ?, ?, ?, ?)";
        const [result] = await connection.execute(sql, [category, description, latitude, longitude, reportedBy, imageUrl]);
        await connection.end();
        res.status(201).json({ message: "Issue reported successfully!", issueId: result.insertId });
    } catch (error) {
        console.error("Error creating issue:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

module.exports = router;

