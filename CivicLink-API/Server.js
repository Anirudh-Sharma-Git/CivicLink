// server.js - The main file for our API

const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const path = require('path'); // <-- THIS IS NEW: Import the built-in 'path' library
require('dotenv').config();

// Import both of our rulebooks
const authRoutes = require('./routes/auth');
const issueRoutes = require('./routes/issues');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// --- THIS IS THE NEW, CRUCIAL LINE ---
// This makes the 'uploads' folder publicly accessible.
// So, a URL like http://[your_server_url]/uploads/image-123.jpg will work.
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
// --- END OF NEW LINE ---

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
app.use('/api/issues', issueRoutes);


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
