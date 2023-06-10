package indi.kwanho.pm.store;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.entity.Album;
import indi.kwanho.pm.entity.Singer;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.entity.Folder;

public class LocalMusicState {
    // Singleton pattern
    private static final LocalMusicState instance = new LocalMusicState();

    private LocalMusicState() {
    }

    public static LocalMusicState getInstance() {
        return instance;
    }

    // State
    private List<Song> songs = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();
    private List<Song> details = new ArrayList<>();
    private List<Singer> singers = new ArrayList<>();
    private List<Folder> folders = new ArrayList<>();

    // Getter and Setter
    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Song> getDetails() {
        return details;
    }

    public void setDetails(List<Song> details) {
        this.details = details;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
