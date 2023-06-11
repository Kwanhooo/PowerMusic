package indi.kwanho.pm.persisitance.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import indi.kwanho.pm.persisitance.AppDatabase;
import indi.kwanho.pm.persisitance.dao.PlaylistItemRecordDao;
import indi.kwanho.pm.persisitance.domain.PlaylistItemRecord;

public class PlaylistItemRecordRepository {
    private final PlaylistItemRecordDao playlistItemRecordDao;

    public PlaylistItemRecordRepository(Context context) {
        // 创建数据库实例
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "playlist_item_records_db")
                .build();
        // 获取 PlayRecordDao
        playlistItemRecordDao = database.playlistItemRecordDao();
    }

    public void insertPlaylistItemRecord(PlaylistItemRecord playlistItemRecord) {
        // 在后台线程中执行插入操作
        new Thread(() -> playlistItemRecordDao.insert(playlistItemRecord)).start();
    }

    public void deletePlayRecord(PlaylistItemRecord playlistItemRecord) {
        // 在后台线程中执行删除操作
        new Thread(() -> playlistItemRecordDao.delete(playlistItemRecord)).start();
    }

    public void updatePlayRecord(PlaylistItemRecord playlistItemRecord) {
        // 在后台线程中执行更新操作
        new Thread(() -> playlistItemRecordDao.update(playlistItemRecord)).start();
    }

    public LiveData<List<PlaylistItemRecord>> getAllPlaylistItemRecords() {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playlistItemRecordDao.getAllPlaylistItemRecords();
    }

    public LiveData<PlaylistItemRecord> getPlaylistItemRecordById(int id) {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playlistItemRecordDao.getPlaylistItemRecordById(id);
    }

    public LiveData<List<PlaylistItemRecord>> getPlaylistItemRecordsByFilePath(String filePath) {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playlistItemRecordDao.getPlaylistItemRecordByFilePath(filePath);
    }

    // 清空
    public void deleteAll() {
        new Thread(playlistItemRecordDao::deleteAllPlaylistItemRecords).start();
    }

    // 根据播放列表id获取
    public LiveData<List<PlaylistItemRecord>> getPlaylistItemRecordByPlaylistId(int playlistId) {
        // 返回最近播放列表的 LiveData，在观察者中处理返回的数据
        return playlistItemRecordDao.getPlaylistItemRecordByPlaylistId(playlistId);
    }
}

