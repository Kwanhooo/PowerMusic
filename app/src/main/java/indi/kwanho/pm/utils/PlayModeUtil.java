package indi.kwanho.pm.utils;

import java.util.List;

import indi.kwanho.pm.common.PlayingMode;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.store.PlayState;

public class PlayModeUtil {
    public static String next() {
        switch (PlayState.getInstance().getPlayingMode()) {
            case PlayingMode.RANDOM:
                return PlayModeUtil.nextInRandom();
            case PlayingMode.SINGLE:
                return PlayModeUtil.nextInSingle();
            default:
                return PlayModeUtil.nextInOrder();
        }
    }

    private static String nextInSingle() {
        List<Song> songList = PlayState.getInstance().getPlayingList();
        if (songList == null) {
            return null;
        }
        int nextIndex = PlayState.getInstance().getPlayingIndex();
        Song nextSong = songList.get(nextIndex);
        PlayState.getInstance().setPlayingSong(nextSong);
        PlayState.getInstance().setPlayingIndex(nextIndex);
        PlayState.getInstance().setPlaying(true);
        return nextSong.getFilePath();
    }

    private static String nextInRandom() {
        // 如果songList为空，返回null
        List<Song> songList = PlayState.getInstance().getPlayingList();
        if (songList == null) {
            return null;
        }
        int nextIndex = (int) (Math.random() * songList.size());
        Song nextSong = songList.get(nextIndex);
        PlayState.getInstance().setPlayingSong(nextSong);
        PlayState.getInstance().setPlayingIndex(nextIndex);
        PlayState.getInstance().setPlaying(true);
        return nextSong.getFilePath();
    }

    private static String nextInOrder() {
        List<Song> songList = PlayState.getInstance().getPlayingList();
        int nextIndex = PlayState.getInstance().getPlayingIndex() + 1;
        if (nextIndex >= songList.size()) {
            nextIndex = 0;
        }
        Song nextSong = songList.get(nextIndex);
        PlayState.getInstance().setPlayingSong(nextSong);
        PlayState.getInstance().setPlayingIndex(nextIndex);
        PlayState.getInstance().setPlaying(true);
        return nextSong.getFilePath();
    }

    public static String prev() {
        // 如果是单曲循环，直接返回当前歌曲
        if (PlayState.getInstance().getPlayingMode() == PlayingMode.SINGLE)
            return nextInSingle();
        // 如果是随机播放，直接返回当前歌曲
        if (PlayState.getInstance().getPlayingMode() == PlayingMode.RANDOM)
            return nextInRandom();
        // 如果是列表循环，返回上一首歌曲
        List<Song> songList = PlayState.getInstance().getPlayingList();
        int prevIndex = PlayState.getInstance().getPlayingIndex() - 1;
        if (prevIndex < 0) {
            prevIndex = songList.size() - 1;
        }
        Song prevSong = songList.get(prevIndex);
        PlayState.getInstance().setPlayingSong(prevSong);
        PlayState.getInstance().setPlayingIndex(prevIndex);
        PlayState.getInstance().setPlaying(true);
        return prevSong.getFilePath();
    }
}
