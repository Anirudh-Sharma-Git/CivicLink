// server.js - The main file for our API

const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
require('dotenv').config();

// Import both of our rulebooks
const authRoutes = require('./routes/auth');
const issueRoutes = require('./routes/issues'); // NEW

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE
};

// --- API Routes ---
app.get('/', (req, res) => {
    res.json({ message: "Welcome to the CivicLink API! It's running!" });
});

// Use the rulebooks for their respective departments
app.use('/api/auth', authRoutes);
app.use('/api/issues', issueRoutes); // NEW


// Start the server
app.listen(PORT, async () => {
    console.log(`Server is running on port ${PORT}`);
    try {
        const connection = await mysql.createConnection(dbConfig);
        console.log("Successfully connected to the MySQL database!");
        await connection.end();
    } catch (error) {
        console.error("!!! ERROR connecting to the database !!!", error.message);
    }
});