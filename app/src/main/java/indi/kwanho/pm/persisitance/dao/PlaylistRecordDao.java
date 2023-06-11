package indi.kwanho.pm.persisitance.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import indi.kwanho.pm.persisitance.domain.PlaylistRecord;

@Dao
public interface PlaylistRecordDao {
    @Insert
    void insert(PlaylistRecord playRecord);

    @Delete
    void delete(PlaylistRecord playRecord);

    @Update
    void update(PlaylistRecord playRecord);

    @Query("SELECT * FROM playlist_record ORDER BY id ASC")
    LiveData<List<PlaylistRecord>> getAllPlaylistRecords();

    @Query("SELECT * FROM playlist_record WHERE id = :id")
    LiveData<PlaylistRecord> getPlaylistRecordById(int id);
}
