package com.intech.topfindprovider.Models;

public class TopFinders {
    private String User_name, Email, Phone, location, device_token, User_ID, Profile_image;


    public TopFinders() {
    }

    public TopFinders(String user_name, String
            email, String phone, String location, String device_token, String user_ID, String profile_image) {
        User_name = user_name;
        Email = email;
        Phone = phone;
        this.location = location;
        this.device_token = device_token;
        User_ID = user_ID;
        Profile_image = profile_image;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getProfile_image() {
        return Profile_image;
    }

    public void setProfile_image(String profile_image) {
        Profile_image = profile_image;
    }
}
