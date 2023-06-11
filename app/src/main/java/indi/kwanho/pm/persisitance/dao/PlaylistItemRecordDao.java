package indi.kwanho.pm.persisitance.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import indi.kwanho.pm.persisitance.domain.PlaylistItemRecord;

@Dao
public interface PlaylistItemRecordDao {
    @Insert
    void insert(PlaylistItemRecord playlistItemRecord);

    @Delete
    void delete(PlaylistItemRecord playlistItemRecord);

    @Update
    void update(PlaylistItemRecord playlistItemRecord);

    @Query("SELECT * FROM playlist_item_record ORDER BY id ASC")
    LiveData<List<PlaylistItemRecord>> getAllPlaylistItemRecords();

    @Query("SELECT * FROM playlist_item_record WHERE id = :id")
    LiveData<PlaylistItemRecord> getPlaylistItemRecordById(int id);
}
