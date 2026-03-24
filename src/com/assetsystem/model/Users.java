package com.assetsystem.model;

public class Users {
    private int userId;
    private String username;
    private String password;
    private String role; // e.g., 'Admin' or 'Staff'

    public Users(int id, String user, String role,String password) {
        this.userId = id;
        this.username = user;
        this.password=password;
        this.role = role;
    }
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {return password;}
    public String getRole() { return role; }
}