package indi.kwanho.pm.persisitance.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import indi.kwanho.pm.persisitance.domain.PlayRecord;

@Dao
public interface PlayRecordDao {
    @Insert
    void insert(PlayRecord playRecord);

    @Delete
    void delete(PlayRecord playRecord);

    @Update
    void update(PlayRecord playRecord);

    @Query("SELECT * FROM play_records ORDER BY lastPlayTime DESC")
    LiveData<List<PlayRecord>> getRecentPlayRecords();

    @Query("SELECT * FROM play_records WHERE id = :id")
    LiveData<PlayRecord> getPlayRecordById(int id);

    // 清空
    @Query("DELETE FROM play_records")
    void deleteAllPlayRecords();
}
