package indi.kwanho.pm.persisitance.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import indi.kwanho.pm.persisitance.AppDatabase;
import indi.kwanho.pm.persisitance.dao.PlayRecordDao;
import indi.kwanho.pm.persisitance.domain.PlayRecord;

public class PlayRecordRepository {
    private final PlayRecordDao playRecordDao;

    public PlayRecordRepository(Context context) {
        // 创建数据库实例
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "play_records_db")
                .build();

        // 获取 PlayRecordDao
        playRecordDao = database.playRecordDao();
    }

    public void insertPlayRecord(PlayRecord playRecord) {
        // 在后台线程中执行插入操作
        new Thread(() -> playRecordDao.insert(playRecord)).start();
    }

    public void deletePlayRecord(PlayRecord playRecord) {
        // 在后台线程中执行删除操作
        new Thread(() -> playRecordDao.delete(playRecord)).start();
    }

    public void updatePlayRecord(PlayRecord playRecord) {
        // 在后台线程中执行更新操作
        new Thread(() -> playRecordDao.update(playRecord)).start();
    }

    public LiveData<List<PlayRecord>> getRecentPlayRecords() {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playRecordDao.getRecentPlayRecords();
    }

    // 清空
    public void deleteAll() {
        new Thread(playRecordDao::deleteAllPlayRecords).start();
    }
}
