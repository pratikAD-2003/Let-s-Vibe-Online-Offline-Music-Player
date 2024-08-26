package com.example.musicplayer.Downloaded;

import android.graphics.Bitmap;

public class DownTrackModel {
    String title;
    String uri;
    Bitmap image;
    String artistName;
    String duration;
    long id;
    public DownTrackModel(String title, String uri, Bitmap image,String artistName) {
        this.title = title;
        this.uri = uri;
        this.image = image;
        this.artistName = artistName;
    }

    public DownTrackModel(String title, String uri, Bitmap image,String artistName,String duration) {
        this.title = title;
        this.uri = uri;
        this.image = image;
        this.artistName = artistName;
        this.duration = duration;
    }

    public DownTrackModel(String title, String uri, Bitmap image,String artistName,String duration,long id) {
        this.title = title;
        this.uri = uri;
        this.image = image;
        this.artistName = artistName;
        this.duration = duration;
        this.id = id;
    }
    public DownTrackModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
