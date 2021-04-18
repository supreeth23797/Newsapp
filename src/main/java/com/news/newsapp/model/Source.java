package com.news.newsapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    //Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
