package indi.kwanho.pm.persisitance.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import indi.kwanho.pm.persisitance.domain.FavoriteRecord;

@Dao
public interface FavoriteDao {
    @Insert
    void insert(FavoriteRecord playRecord);

    @Delete
    void delete(FavoriteRecord playRecord);

    @Update
    void update(FavoriteRecord playRecord);

    @Query("SELECT * FROM favorite_record ORDER BY id DESC")
    LiveData<List<FavoriteRecord>> getAllFavoriteRecords();

    @Query("SELECT * FROM favorite_record WHERE id = :id")
    LiveData<FavoriteRecord> getFavoriteRecordById(int id);

    @Query("SELECT * FROM favorite_record WHERE filePath = :filePath")
    LiveData<List<FavoriteRecord>> getFavoriteRecordsByFilePath(String filePath);

    // 清空
    @Query("DELETE FROM favorite_record")
    void deleteAllFavoriteRecords();

    // 计数
    @Query("SELECT COUNT(*) FROM favorite_record")
    LiveData<Integer> count();
}
