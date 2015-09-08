package com.nerderylabs.android.nerdalert.model;

import com.google.gson.Gson;

public class Neighbor {

    public String name;

    public String tagline;

    public String photoUrl;

    private static final Gson gson = new Gson();

    public Neighbor() {
    }

    public Neighbor(String name, String tagline, String photoUrl) {
        this.name = name;
        this.tagline = tagline;
        this.photoUrl = photoUrl;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Neighbor fromJson(String json) {
        return gson.fromJson(json, Neighbor.class);
    }

}
