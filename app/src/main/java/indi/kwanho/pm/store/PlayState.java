package indi.kwanho.pm.store;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.common.PowerState;
import indi.kwanho.pm.entity.Song;

public class PlayState implements PowerState {
    private static final PlayState instance = new PlayState();
    private final List<PowerObserver> observers = new ArrayList<>();
    private boolean isPlaying = false;
    private Song playingSong;

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
}
