package indi.kwanho.pm.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.activity.MainActivity;
import indi.kwanho.pm.entity.Album;
import indi.kwanho.pm.entity.Folder;
import indi.kwanho.pm.entity.Singer;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.persisitance.repository.FavoriteRecordRepository;
import indi.kwanho.pm.persisitance.repository.PlayRecordRepository;
import indi.kwanho.pm.store.LocalMusicState;

public class LocalMusicUtil {
    public static void scanLocalMusic(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // 查询本地音乐的URI
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // 定义要查询的字段
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        // 查询本地音乐并按指定字段排序
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(musicUri, projection, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            // 创建用于存储分组数据的集合
            List<Song> songs = new ArrayList<>();
            List<Album> albums = new ArrayList<>();
            List<Singer> singers = new ArrayList<>();
            List<Folder> folders = new ArrayList<>();

            do {
                // 从游标中读取音乐信息
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("Range") String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                // 创建音乐对象
                Song song = new Song(title, album, artist, filePath);
                songs.add(song);

                // 创建专辑对象
                Album albumObj = new Album(album, artist, 0);
                if (!albums.contains(albumObj)) {
                    albums.add(albumObj);
                }

                // 创建歌手对象
                Singer singerObj = new Singer(artist);
                if (!singers.contains(singerObj)) {
                    singers.add(singerObj);
                }

                // 获取文件所在的文件夹路径
                String folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
                String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1);

                // 创建文件夹对象
                Folder folder = new Folder(folderName, folderPath);
                if (!folders.contains(folder)) {
                    folders.add(folder);
                }
            } while (cursor.moveToNext());

            cursor.close();

            LocalMusicState.getInstance().setSongs(songs);
            LocalMusicState.getInstance().setAlbums(albums);
            LocalMusicState.getInstance().setSingers(singers);
            LocalMusicState.getInstance().setFolders(folders);
        }
    }

    public static void getSongsByAlbum(Album album) {
        List<Song> songs = LocalMusicState.getInstance().getSongs();
        List<Song> songsByAlbum = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbum().equals(album.getName())) {
                songsByAlbum.add(song);
            }
        }
        LocalMusicState.getInstance().setDetails(songsByAlbum);
    }

    public static void getSongsBySinger(Singer singer) {
        List<Song> songs = LocalMusicState.getInstance().getSongs();
        List<Song> songsBySinger = new ArrayList<>();
        for (Song song : songs) {
            if (song.getArtist().equals(singer.getName())) {
                songsBySinger.add(song);
            }
        }
        LocalMusicState.getInstance().setDetails(songsBySinger);
    }

    public static void loadRecentToDetail(Context context){
        PlayRecordRepository playRecordRepository = new PlayRecordRepository(context);
        playRecordRepository.getRecentPlayRecords().observe((MainActivity) context, playRecords -> {
            List<Song> songs = new ArrayList<>();
            for (int i = 0; i < playRecords.size(); i++) {
                Log.d("recent", playRecords.get(i).getTitle());
                Song song = new Song(playRecords.get(i).getTitle(), playRecords.get(i).getAlbum(), playRecords.get(i).getArtist(), playRecords.get(i).getFilePath());
                songs.add(song);
            }
            LocalMusicState.getInstance().setDetails(songs);
        });
    }

    public static void loadFavoriteToDetail(Context context){
        FavoriteRecordRepository favoriteRecordRepository = new FavoriteRecordRepository(context);
        favoriteRecordRepository.getAllFavoriteRecords().observe((MainActivity) context, favoriteRecords -> {
            List<Song> songs = new ArrayList<>();
            for (int i = 0; i < favoriteRecords.size(); i++) {
                Log.d("favorite", favoriteRecords.get(i).getTitle());
                Song song = new Song(favoriteRecords.get(i).getTitle(), favoriteRecords.get(i).getAlbum(), favoriteRecords.get(i).getArtist(), favoriteRecords.get(i).getFilePath());
                songs.add(song);
            }
            LocalMusicState.getInstance().setDetails(songs);
        });
    }

}
