package com.demo.project.roas.demo2;

import android.widget.ImageView;

/**
 * Created by is on 2017-08-09.
 */

public class Review {

    String id;
    String content;
    String date;
    String pw;

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    int avg;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    ImageView image;

    public Review(String id, String content, int avg, String date) {
        this.id = id;
        this.content = content;
        this.avg = avg;
        this.date = date;
    }

    public Review() {}

    public Review(String content, int avg, String date)
    {
        this.content = content;
        this.avg = avg;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
