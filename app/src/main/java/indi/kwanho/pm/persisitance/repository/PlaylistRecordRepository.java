package indi.kwanho.pm.persisitance.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import indi.kwanho.pm.persisitance.AppDatabase;
import indi.kwanho.pm.persisitance.dao.PlaylistRecordDao;
import indi.kwanho.pm.persisitance.domain.PlaylistRecord;

public class PlaylistRecordRepository {
    private final PlaylistRecordDao playlistRecordDao;

    public PlaylistRecordRepository(Context context) {
        // 创建数据库实例
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "playlist_records_db")
                .build();
        // 获取 PlayRecordDao
        playlistRecordDao = database.playlistRecordDao();
    }

    public void insertPlayRecord(PlaylistRecord playlistRecord) {
        // 在后台线程中执行插入操作
        new Thread(() -> playlistRecordDao.insert(playlistRecord)).start();
    }

    public void deletePlayRecord(PlaylistRecord playlistRecord) {
        // 在后台线程中执行删除操作
        new Thread(() -> playlistRecordDao.delete(playlistRecord)).start();
    }

    public void updatePlayRecord(PlaylistRecord playlistRecord) {
        // 在后台线程中执行更新操作
        new Thread(() -> playlistRecordDao.update(playlistRecord)).start();
    }

    public LiveData<List<PlaylistRecord>> getAllPlaylistRecords() {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playlistRecordDao.getAllPlaylistRecords();
    }

    public LiveData<PlaylistRecord> getPlaylistRecordById(int id) {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playlistRecordDao.getPlaylistRecordById(id);
    }

    // 清空
    public void deleteAll() {
        new Thread(playlistRecordDao::deleteAllPlaylistRecords).start();
    }
}

