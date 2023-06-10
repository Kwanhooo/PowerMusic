package indi.kwanho.pm.entity;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Song {
    private String title;
    private String album;
    private String artist;
    private String filePath;

    public Song(String title, String album, String artist, String filePath) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @NonNull
    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) && Objects.equals(album, song.album) && Objects.equals(artist, song.artist) && Objects.equals(filePath, song.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, album, artist, filePath);
    }
}
