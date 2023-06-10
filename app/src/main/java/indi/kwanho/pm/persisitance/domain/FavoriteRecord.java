package indi.kwanho.pm.persisitance.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import indi.kwanho.pm.entity.Song;

@Entity(tableName = "favorite_record")
public class FavoriteRecord extends Song {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public FavoriteRecord(String title, String album, String artist, String filePath) {
        super(title, album, artist, filePath);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
