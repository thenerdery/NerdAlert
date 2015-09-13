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


    // two Neighbor objects are equal if all their Strings are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Neighbor neighbor = (Neighbor) o;

        if (name != null ? !name.equals(neighbor.name) : neighbor.name != null) {
            return false;
        }
        if (tagline != null ? !tagline.equals(neighbor.tagline) : neighbor.tagline != null) {
            return false;
        }
        return !(photoUrl != null ? !photoUrl.equals(neighbor.photoUrl)
                : neighbor.photoUrl != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tagline != null ? tagline.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        return result;
    }
}
