package indi.kwanho.pm.store;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.common.PlayingMode;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.common.PowerState;
import indi.kwanho.pm.entity.Song;

public class PlayState implements PowerState {
    private static final PlayState instance = new PlayState();
    private final List<PowerObserver> observers = new ArrayList<>();
    private boolean isPlaying = false;
    private Song playingSong;
    private List<Song> playingList = new ArrayList<>();
    private int playingMode = PlayingMode.LIST;
    int playingIndex = 0;
    int lastPlayingIndex = 0;

    private PlayState() {
    }

    public static PlayState getInstance() {
        return instance;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        notifyObservers();
    }

    @Override
    public void attach(PowerObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(PowerObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (PowerObserver observer : observers) {
            observer.update();
        }
    }

    public Song getPlayingSong() {
        return playingSong;
    }

    public void setPlayingSong(Song playingSong) {
        this.playingSong = playingSong;
        notifyObservers();
    }

    public List<Song> getPlayingList() {
        return playingList;
    }

    public void setPlayingList(List<Song> playingList) {
        this.playingList = playingList;
        notifyObservers();
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
        notifyObservers();
    }

    public int getPlayingMode() {
        return playingMode;
    }

    public void setPlayingMode(int playingMode) {
        this.playingMode = playingMode;
        notifyObservers();
    }

    public int getLastPlayingIndex() {
        return lastPlayingIndex;
    }

    public void setLastPlayingIndex(int lastPlayingIndex) {
        this.lastPlayingIndex = lastPlayingIndex;
        notifyObservers();
    }
}
