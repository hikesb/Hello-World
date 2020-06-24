package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoCollection {

    @SerializedName("photos")
    @Expose
    private PhotoPage photoPage;
    @SerializedName("stat")
    @Expose
    private String stat;

    public PhotoPage getPhotoPage() {
        return photoPage;
    }

    public void setPhotoPage(PhotoPage photoPage) {
        this.photoPage = photoPage;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

}