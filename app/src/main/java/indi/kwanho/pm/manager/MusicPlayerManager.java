package indi.kwanho.pm.manager;

import indi.kwanho.pm.service.MusicPlayerService;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private MusicPlayerService musicPlayerService;

    private MusicPlayerManager() {
    }

    public static MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void setMusicPlayerService(MusicPlayerService musicPlayerService) {
        this.musicPlayerService = musicPlayerService;
    }

    public MusicPlayerService getMusicPlayerService() {
        return musicPlayerService;
    }
}

