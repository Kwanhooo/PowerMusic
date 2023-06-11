package indi.kwanho.pm.store;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.common.PowerState;
import indi.kwanho.pm.persisitance.domain.PlaylistRecord;

public class PlaylistState implements PowerState {
    private static final PlaylistState instance = new PlaylistState();
    private final List<PowerObserver> observers = new ArrayList<>();

    /*
     * ********************* 响应式字段 ************************
     */

    private List<PlaylistRecord> playlistRecords = new ArrayList<>();

    /*
     * ********************************************************
     */

    private PlaylistState() {
    }

    public static PlaylistState getInstance() {
        return instance;
    }

    @Override
    public void attach(PowerObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(PowerObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (PowerObserver observer : observers) {
            observer.update();
        }
    }

    public List<PowerObserver> getObservers() {
        return observers;
    }

    public List<PlaylistRecord> getPlaylistRecords() {
        return playlistRecords;
    }

    public void setPlaylistRecords(List<PlaylistRecord> playlistRecords) {
        this.playlistRecords = playlistRecords;
        notifyObservers();
    }
}
