package com.example.musicplayer.Model;

public class ArtistsTrackModel {
    long id;
    String name;
    String imgUri;
    long songsId;
    String artistName;
    long duration;

    public ArtistsTrackModel(long id, String name, String imgUri, long songsId) {
        this.id = id;
        this.name = name;
        this.imgUri = imgUri;
        this.songsId = songsId;
    }

    public ArtistsTrackModel(long id, String name, String imgUri, long songsId, String artistName) {
        this.id = id;
        this.name = name;
        this.imgUri = imgUri;
        this.songsId = songsId;
        this.artistName = artistName;
    }

    public ArtistsTrackModel() {
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getSongsId() {
        return songsId;
    }

    public void setSongsId(long songsId) {
        this.songsId = songsId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }
}
