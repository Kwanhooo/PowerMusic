package indi.kwanho.pm.persisitance.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "play_records")
public class PlayRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String album;
    private String artist;
    private String filePath;
    private Date lastPlayTime;

    // 构造函数、getter 和 setter 方法
    public PlayRecord(String title, String album, String artist, String filePath, Date lastPlayTime) {
//        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.filePath = filePath;
        this.lastPlayTime = lastPlayTime;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(Date lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayRecord{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", filePath='" + filePath + '\'' +
                ", lastPlayTime=" + lastPlayTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayRecord that = (PlayRecord) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(album, that.album) && Objects.equals(artist, that.artist) && Objects.equals(filePath, that.filePath) && Objects.equals(lastPlayTime, that.lastPlayTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, album, artist, filePath, lastPlayTime);
    }
}
