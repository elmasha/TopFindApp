package com.intech.topfindprovider.Models;

import java.util.Date;

public class TopFindRequest {
    private String User_name, Email, Phone, location, User_ID, Profile_image,Request_ID,Sender_ID,Request_message;
       private Date  timestamp;
    public TopFindRequest() {
    //empty
    }

    public TopFindRequest(String user_name, String email, String phone, String location,
                          String user_ID, String profile_image, String request_ID, String sender_ID, String request_message, Date timestamp) {
        User_name = user_name;
        Email = email;
        Phone = phone;
        this.location = location;
        User_ID = user_ID;
        Profile_image = profile_image;
        Request_ID = request_ID;
        Sender_ID = sender_ID;
        Request_message = request_message;
        this.timestamp = timestamp;
    }


    public String getRequest_message() {
        return Request_message;
    }

    public String getSender_ID() {
        return Sender_ID;
    }

    public String getRequest_ID() {
        return Request_ID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getUser_name() {
        return User_name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public String getLocation() {
        return location;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public String getProfile_image() {
        return Profile_image;
    }
}
