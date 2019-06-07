package com.mocktest.service;

import com.mocktest.finder.MusicFinder;
import com.mocktest.model.Album;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MusicCatalogTest {
    private List<Album> albumsMuse;
    private List<Album> albumsRevolution;

    @Mock
    private MusicFinder musicFinder;

    @InjectMocks
    private MusicCatalog musicCatalog;

    @Before
    public void init(){
        albumsMuse = new ArrayList<>();
        albumsRevolution = new ArrayList<>();

        List<String> artistsName = new ArrayList<>();
        List<String> musicName = new ArrayList<>();
        musicName.add("EPM");
        artistsName.add("Muse");
        MockitoAnnotations.initMocks(this);
        Album museRevolution = new Album("01","Revolution",artistsName);
        albumsMuse.add(museRevolution);
        albumsMuse.add(new Album("02","Polte",artistsName));


        albumsRevolution.add(museRevolution);
        albumsRevolution.add(new Album("03","Revolution",musicName));

    }

    @Test
    public void searchAlbumWhenAllNull() {
        Assertions.assertThat(musicCatalog.searchAlbum(null,null)).isEmpty();
    }

    @Test
    public void searchAlbumWhenNameNull() {
        Mockito.when(musicFinder.findByArtist("Muse")).thenReturn(albumsMuse);
        Mockito.when(musicFinder.findByName(null)).thenReturn(new ArrayList<>());

        Assertions.assertThat(musicCatalog.searchAlbum(null,"Muse")).hasSize(2);
    }

    @Test
    public void searchAlbumWhenArtistNull() {
        Mockito.when(musicFinder.findByArtist(null)).thenReturn(new ArrayList<>());
        Mockito.when(musicFinder.findByName("Revolution")).thenReturn(albumsRevolution);

        Assertions.assertThat(musicCatalog.searchAlbum("Revolution",null)).hasSize(2);
    }

    @Test
    public void searchAlbumTest() {
        Mockito.when(musicFinder.findByArtist("Muse")).thenReturn(albumsMuse);
        Mockito.when(musicFinder.findByName("Revolution")).thenReturn(albumsRevolution);

        Assertions.assertThat(musicCatalog.searchAlbum("Revolution","Muse")).hasSize(3);
    }


}