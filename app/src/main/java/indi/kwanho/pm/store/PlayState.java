package indi.kwanho.pm.store;

/**
 * 观察者模式，用于存储播放状态
 */
public class PlayState {
    private static final PlayState instance = new PlayState();
    private boolean isPlaying = false;

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
    }
}
