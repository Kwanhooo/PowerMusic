package indi.kwanho.pm.persisitance.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "playlist_record")
public class PlaylistRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private int count;

    @NonNull
    @Override
    public String toString() {
        return "PlaylistRecord{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", count=" + count +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistRecord that = (PlaylistRecord) o;
        return id == that.id && count == that.count && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, count);
    }
}
