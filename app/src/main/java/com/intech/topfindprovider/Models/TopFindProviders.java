package com.intech.topfindprovider.Models;

public class TopFindProviders {
    private String User_name, Email, Phone, location, device_token, User_ID, Profile_image,Experience,Payment,
                    Narration,Profession;

    public TopFindProviders() {
    }

    public TopFindProviders(String user_name, String email, String phone, String location, String device_token,
                            String user_ID, String profile_image, String experience, String payment, String narration, String profession) {
        User_name = user_name;
        Email = email;
        Phone = phone;
        this.location = location;
        this.device_token = device_token;
        User_ID = user_ID;
        Profile_image = profile_image;
        Experience = experience;
        Payment = payment;
        Narration = narration;
        Profession = profession;
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

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public String getNarration() {
        return Narration;
    }

    public void setNarration(String narration) {
        Narration = narration;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }
}
