package com.book.civiclink2o;

//hold the data of user

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