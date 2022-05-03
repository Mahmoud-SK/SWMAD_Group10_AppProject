package com.example.swmad_group10_appproject.Models;

public class Meme {
    public Meme(int memeId, String topText, String bottomText, int memeImgId, double latitude, double longitude, int userId) {
        this.memeId = memeId;
        this.topText = topText;
        this.bottomText = bottomText;
        this.memeImgId = memeImgId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public Meme(){}

    public int getMemeId() {
        return memeId;
    }

    public String getTopText() {
        return topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public int getMemeImgId() {
        return memeImgId;
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

    public void setMemeId(int memeId) {
        this.memeId = memeId;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public void setMemeImgId(int memeImgId) {
        this.memeImgId = memeImgId;
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

    private int memeId;
    private String topText;
    private String bottomText;
    private int memeImgId;
    private double latitude;
    private double longitude;
    private int userId;

}