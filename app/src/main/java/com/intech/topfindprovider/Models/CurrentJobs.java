package com.intech.topfindprovider.Models;

import java.util.Date;

public class CurrentJobs {
  private String User_name,Email,Phone,location,User_ID,Profile_image,job_ID,Category;
  private Date timestamp;
  private long Rating;

    public CurrentJobs() {
        //...Empty
    }

    public CurrentJobs(String user_name, String email, String phone,
                       String location, String user_ID, String profile_image, String job_ID, String category, Date timestamp, long rating) {
        User_name = user_name;
        Email = email;
        Phone = phone;
        this.location = location;
        User_ID = user_ID;
        Profile_image = profile_image;
        this.job_ID = job_ID;
        Category = category;
        this.timestamp = timestamp;
        Rating = rating;
    }

    public long getRating() {
        return Rating;
    }

    public String getCategory() {
        return Category;
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

    public String getJob_ID() {
        return job_ID;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
