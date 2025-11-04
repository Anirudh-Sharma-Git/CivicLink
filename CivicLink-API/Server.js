// server.js - The main file with an Admin "department" and website

const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const authRoutes = require('./routes/auth');
const issueRoutes = require('./routes/issues');
const adminRoutes = require('./routes/admin');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());


app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE
};


app.get('/', (req, res) => {
    res.json({ message: "Welcome to the CivicLink API! It's running!" });
});


app.get('/admin', (req, res) => {
    res.sendFile(path.join(__dirname, 'admin.html'));
});


app.use('/api/auth', authRoutes);
app.use('/api/issues', issueRoutes);
app.use('/api/admin', adminRoutes); 



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

