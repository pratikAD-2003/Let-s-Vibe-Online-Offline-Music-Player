package com.example.musicplayer.Model;

public class TrackModel {
    long id;
    String imgUri;
    String title;

    public TrackModel(long id, String imgUri, String title) {
        this.id = id;
        this.imgUri = imgUri;
        this.title = title;
    }

    public TrackModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
