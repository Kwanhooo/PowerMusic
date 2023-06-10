package indi.kwanho.pm.entity;


import java.util.Objects;

public class Album {
    private String name;
    private String artist;
    private int count;

    public Album(String name, String artist, int count) {
        this.name = name;
        this.artist = artist;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(name, album.name) && Objects.equals(artist, album.artist) && Objects.equals(count, album.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist, count);
    }
}
