package com.book.civiclink2o;

public class LoginResponse {
    private String message;
    private UserData user;

    public UserData getUser() {
        return user;
    }

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