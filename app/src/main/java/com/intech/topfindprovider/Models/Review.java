package com.intech.topfindprovider.Models;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class Review {
 private String User_name, User_image, Review;
 private long Ratings;
 private Date timestamp;

    public Review() {
    }

    public Review(String user_name, String user_image, String review, long ratings, Date timestamp) {
        User_name = user_name;
        User_image = user_image;
        Review = review;
        Ratings = ratings;
        this.timestamp = timestamp;
    }


    public String getUser_name() {
        return User_name;
    }

    public String getUser_image() {
        return User_image;
    }

    public String getReview() {
        return Review;
    }

    public long getRatings() {
        return Ratings;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
