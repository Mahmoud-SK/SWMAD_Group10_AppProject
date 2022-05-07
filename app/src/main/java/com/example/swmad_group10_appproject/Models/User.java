package com.example.swmad_group10_appproject.Models;

public class User {

    private String Email;
    private String Password;
    private String Username;
    private int Radius;

    public User(String email, String password, String username, int radius) {
        Email = email;
        Password = password;
        Username = username;
        Radius = radius;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }


}
