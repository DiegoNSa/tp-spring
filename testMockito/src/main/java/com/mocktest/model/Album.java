package com.mocktest.model;

import java.util.List;

public class Album {
    private String id;
    private String name;
    private List<String> artists;

    public Album(String id, String name, List<String> artists) {
        this.id = id;
        this.name = name;
        this.artists = artists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }
}
