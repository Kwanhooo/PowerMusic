package indi.kwanho.pm.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.activity.LocalMusicActivity;
import indi.kwanho.pm.entity.Album;
import indi.kwanho.pm.entity.Folder;
import indi.kwanho.pm.entity.Singer;
import indi.kwanho.pm.entity.Song;
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

}
