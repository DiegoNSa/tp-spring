package com.mocktest.finder;

import com.mocktest.model.Album;

import java.util.List;

public interface MusicFinder {
    List<Album> findByArtist(String artist);
    List<Album> findByName(String name);
}
