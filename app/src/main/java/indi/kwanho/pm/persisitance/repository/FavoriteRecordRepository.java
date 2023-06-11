package indi.kwanho.pm.persisitance.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import indi.kwanho.pm.persisitance.AppDatabase;
import indi.kwanho.pm.persisitance.dao.FavoriteDao;
import indi.kwanho.pm.persisitance.domain.FavoriteRecord;

public class FavoriteRecordRepository {
    private final FavoriteDao favoriteDao;

    public FavoriteRecordRepository(Context context) {
        // 创建数据库实例
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "favorite_records_db")
                .build();
        // 获取 PlayRecordDao
        favoriteDao = database.favoriteDao();
    }

    public void insertFavoriteRecord(FavoriteRecord favoriteRecord) {
        // 在后台线程中执行插入操作
        new Thread(() -> favoriteDao.insert(favoriteRecord)).start();
    }

    public void deletePlayRecord(FavoriteRecord favoriteRecord) {
        // 在后台线程中执行删除操作
        new Thread(() -> favoriteDao.delete(favoriteRecord)).start();
    }

    public void updatePlayRecord(FavoriteRecord favoriteRecord) {
        // 在后台线程中执行更新操作
        new Thread(() -> favoriteDao.update(favoriteRecord)).start();
    }

    public LiveData<List<FavoriteRecord>> getAllFavoriteRecords() {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return favoriteDao.getAllFavoriteRecords();
    }

    public LiveData<FavoriteRecord> getFavoriteRecordById(int id) {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return favoriteDao.getFavoriteRecordById(id);
    }

    // 清空
    public void deleteAll() {
        new Thread(favoriteDao::deleteAllFavoriteRecords).start();
    }

    public LiveData<List<FavoriteRecord>> getFavoriteRecordsByFilePath(String filePath) {
        return favoriteDao.getFavoriteRecordsByFilePath(filePath);
    }
}

