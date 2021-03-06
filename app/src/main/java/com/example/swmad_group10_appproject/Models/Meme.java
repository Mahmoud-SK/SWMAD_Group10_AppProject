package com.example.swmad_group10_appproject.Models;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Meme {
    public Meme(String topText, String bottomText, String memeImgURL, double latitude,
                double longitude, int userId, int score) {
        this.topText = topText;
        this.bottomText = bottomText;
        this.memeImgURL = memeImgURL;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.score = score;
    }

    public Meme(){}

    public void updateScore(int addedValue){
        score += addedValue;
    }

    public String getTopText() {
        return topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public String getMemeImgURL() {
        return memeImgURL;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return date;
    }

    @Exclude
    public String getKey() {
        return key;
    }


    public void setTopText(String topText) {
        this.topText = topText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public void setMemeImgURL(String memeImgURL) {
        this.memeImgURL = memeImgURL;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private String topText;
    private String bottomText;
    private String memeImgURL;
    private double latitude;
    private double longitude;
    private int userId;
    private int score;
    private Date date;
    @Exclude
    private String key;


}
