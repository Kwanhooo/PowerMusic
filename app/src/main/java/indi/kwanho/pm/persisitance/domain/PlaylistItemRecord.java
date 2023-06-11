package indi.kwanho.pm.persisitance.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

import indi.kwanho.pm.entity.Song;

@Entity(tableName = "playlist_item_record")
public class PlaylistItemRecord extends Song {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int playlistId;

    public PlaylistItemRecord(String title, String album, String artist, String filePath, int playlistId) {
        super(title, album, artist, filePath);
        this.playlistId = playlistId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlaylistItemRecord that = (PlaylistItemRecord) o;
        return id == that.id && playlistId == that.playlistId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, playlistId);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
