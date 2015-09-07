package com.nerderylabs.android.nerdalert.model;

import com.google.gson.Gson;

public class Neighbor {

    public String name;

    public String tagline;

    public String photoUrl;

    public Neighbor() {
    }

    public Neighbor(String name, String tagline, String photoUrl) {
        this.name = name;
        this.tagline = tagline;
        this.photoUrl = photoUrl;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
