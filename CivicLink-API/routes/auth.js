// routes/auth.js

const express = require('express');
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');
const router = express.Router();

const dbConfig = {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE
};

//EMAIL AUTHENTICATION
router.post('/register', async (req, res) => {
    const { name, email, password } = req.body;
    if (!name || !email || !password) { return res.status(400).json({ message: "Please provide name, email, and password." }); }
    try {
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);
        const connection = await mysql.createConnection(dbConfig);
        const sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        const [result] = await connection.execute(sql, [name, email, hashedPassword]);
        await connection.end();
        res.status(201).json({ message: "User registered successfully!", userId: result.insertId });
    } catch (error) {
        if (error.code === 'ER_DUP_ENTRY') { return res.status(409).json({ message: "An account with that email already exists." }); }
        console.error("Error during registration:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

router.post('/login', async (req, res) => {
    const { email, password } = req.body;
    if (!email || !password) { return res.status(400).json({ message: "Please provide both email and password." }); }
    try {
        const connection = await mysql.createConnection(dbConfig);
        const sql = "SELECT * FROM users WHERE email = ?";
        const [rows] = await connection.execute(sql, [email]);
        await connection.end();
        if (rows.length === 0) { return res.status(404).json({ message: "No user found with that email." });}
        const user = rows[0];
        if (!user.password) { return res.status(401).json({ message: "Account was created with phone, please log in with phone." });}
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) { return res.status(401).json({ message: "Invalid credentials." });}
        res.status(200).json({ message: "Login successful!", user: { id: user.id, name: user.name, email: user.email } });
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

//PHONE AUTHENTICATION
router.post('/send-otp', async (req, res) => {
    const { phoneNumber } = req.body;
    if (!phoneNumber) {
        return res.status(400).json({ message: "Phone number is required." });
    }
    const otp = "1234";
    console.log(`SIMULATED OTP for ${phoneNumber}: ${otp}`);
    res.status(200).json({ message: "OTP sent successfully (simulation)." });
});

router.post('/verify-otp/login', async (req, res) => {
    const { phoneNumber, otp } = req.body;
    if (!phoneNumber || !otp) { return res.status(400).json({ message: "Phone number and OTP are required." }); }
    if (otp !== "1234") { return res.status(400).json({ message: "Invalid OTP." }); }
    try {
        const connection = await mysql.createConnection(dbConfig);
        const [rows] = await connection.execute("SELECT * FROM users WHERE phoneNumber = ?", [phoneNumber]);
        await connection.end();
        if (rows.length === 0) {
            return res.status(404).json({ message: "Account not found. Please sign up." });
        }
        const user = rows[0];
        res.status(200).json({ message: "Login successful!", user: user });
    } catch (error) {
        console.error("Error during phone login:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

router.post('/verify-otp/signup', async (req, res) => {
    const { phoneNumber, otp, name } = req.body;
    if (!phoneNumber || !otp || !name) { return res.status(400).json({ message: "Name, phone number, and OTP are required." });}
    if (otp !== "1234") { return res.status(400).json({ message: "Invalid OTP." });}
    try {
        const connection = await mysql.createConnection(dbConfig);
        const [existingUsers] = await connection.execute("SELECT * FROM users WHERE phoneNumber = ?", [phoneNumber]);
        if (existingUsers.length > 0) {
            await connection.end();
            return res.status(409).json({ message: "This phone number is already registered." });
        }
        const sql = "INSERT INTO users (name, phoneNumber) VALUES (?, ?)";
        const [result] = await connection.execute(sql, [name, phoneNumber]);
        const newUser = { id: result.insertId, name: name, phoneNumber: phoneNumber };
        await connection.end();
        res.status(201).json({ message: "Sign up successful!", user: newUser });
    } catch (error) {
        console.error("Error during phone sign-up:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

//USER PROFILE MANAGEMENT
router.get('/user/:userId', async (req, res) => {
    try {
        const userId = req.params.userId;
        const connection = await mysql.createConnection(dbConfig);
        const sql = "SELECT id, name, email, phoneNumber, (password IS NOT NULL) as hasPassword FROM users WHERE id = ?";
        const [rows] = await connection.execute(sql, [userId]);
        await connection.end();
        if (rows.length === 0) {
            return res.status(404).json({ message: "User not found." });
        }
        const user = { ...rows[0], hasPassword: !!rows[0].hasPassword };
        res.status(200).json(user);
    } catch (error) {
        console.error("Error fetching user details:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

router.put('/user/:userId', async (req, res) => {
    try {
        const userId = req.params.userId;
        const { name, email, phoneNumber, currentPassword, newPassword } = req.body;
        if (!name || !email || !phoneNumber) {
            return res.status(400).json({ message: "Name, email, and phone number are required." });
        }
        const connection = await mysql.createConnection(dbConfig);
        const [rows] = await connection.execute("SELECT * FROM users WHERE id = ?", [userId]);
        if (rows.length === 0) {
            await connection.end();
            return res.status(404).json({ message: "User not found." });
        }
        const user = rows[0];
        if (user.password) {
            if (!currentPassword) {
                await connection.end();
                return res.status(401).json({ message: "Current password is required to update profile." });
            }
            const isMatch = await bcrypt.compare(currentPassword, user.password);
            if (!isMatch) {
                await connection.end();
                return res.status(401).json({ message: "Incorrect current password." });
            }
            const sql = "UPDATE users SET name = ?, email = ?, phoneNumber = ? WHERE id = ?";
            await connection.execute(sql, [name, email, phoneNumber, userId]);
        } else {
            if (!newPassword) {
                await connection.end();
                return res.status(400).json({ message: "A new password is required to secure your account." });
            }
            const salt = await bcrypt.genSalt(10);
            const hashedPassword = await bcrypt.hash(newPassword, salt);
            const sql = "UPDATE users SET name = ?, email = ?, phoneNumber = ?, password = ? WHERE id = ?";
            await connection.execute(sql, [name, email, phoneNumber, hashedPassword, userId]);
        }
        await connection.end();
        res.status(200).json({ message: "Profile updated successfully!" });
    } catch (error) {
        if (error.code === 'ER_DUP_ENTRY') {
            return res.status(409).json({ message: "Email or phone number is already in use." });
        }
        console.error("Error updating user profile:", error);
        res.status(500).json({ message: "An error occurred on the server." });
    }
});

router.get('/test', (req, res) => {
    res.status(200).json({ message: "Auth route is working!" });
});


module.exports = router;