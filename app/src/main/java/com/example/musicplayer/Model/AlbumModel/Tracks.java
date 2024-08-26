package com.example.musicplayer.Model.AlbumModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Tracks {

    @SerializedName("data")
    @Expose
    private List<Datum> data;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
