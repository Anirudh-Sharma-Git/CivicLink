package com.book.civiclink2o;

// This class represents the successful response we GET BACK from the server after logging in.
public class LoginResponse {
    private String message;
    private UserData user;

    public UserData getUser() {
        return user;
    }

    // This is a nested class to represent the user object inside the response
    public static class UserData {
        private int id;
        private String name;
        private String email;

        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
    }
}