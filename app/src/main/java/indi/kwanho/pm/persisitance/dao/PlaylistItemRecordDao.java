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

    @Query("SELECT * FROM playlist_item_record WHERE filePath = :filePath")
    LiveData<List<PlaylistItemRecord>> getPlaylistItemRecordByFilePath(String filePath);

    // 根据播放列表id获取
    @Query("SELECT * FROM playlist_item_record WHERE playlistId = :playlistId")
    LiveData<List<PlaylistItemRecord>> getPlaylistItemRecordByPlaylistId(int playlistId);

    // 清空所有条目
    @Query("DELETE FROM playlist_item_record")
    void deleteAllPlaylistItemRecords();
}
