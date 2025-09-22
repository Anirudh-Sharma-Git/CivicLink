package com.book.civiclink2o;

// This is a simple data class. Its only job is to hold the data
// for a new user before we send it to the server.
public class User {
    private String name;
    private String email;
    private String password;

    // The constructor is used to create a new User object
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}