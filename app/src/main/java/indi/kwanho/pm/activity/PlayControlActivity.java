package indi.kwanho.pm.activity;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import indi.kwanho.pm.R;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.store.PlayState;

public class PlayControlActivity extends AppCompatActivity implements PowerObserver, SeekBar.OnSeekBarChangeListener {
    private ImageButton backButton;
    private TextView songNameTextView;
    private TextView albumNameTextView;
    private TextView artistNameTextView;
    private SeekBar seekBar;
    private ImageButton playPauseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private Handler handler;
    private boolean isSeekBarTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        this.handler = new Handler();

        register();
        getActualElements();
        initData();
        setUpListeners();
        updateSeekBarProgress();
    }

    private void getActualElements() {
        this.backButton = findViewById(R.id.play_control_back_button);
        this.songNameTextView = findViewById(R.id.play_control_song_title);
        this.albumNameTextView = findViewById(R.id.play_control_album_name);
        this.artistNameTextView = findViewById(R.id.play_control_artist_name);
        this.seekBar = findViewById(R.id.play_control_seek_bar);
        this.playPauseButton = findViewById(R.id.play_control_play_pause_button);
        this.prevButton = findViewById(R.id.play_control_prev_button);
        this.nextButton = findViewById(R.id.play_control_next_button);
    }

    private void initData() {
        seekBar.setMax(MusicPlayerManager.getInstance()
                .getMusicPlayerService()
                .getMediaPlayer()
                .getDuration());
        PlayState playState = PlayState.getInstance();
        if (playState.getPlayingSong() != null) {
            songNameTextView.setText(playState.getPlayingSong().getTitle());
            albumNameTextView.setText(playState.getPlayingSong().getAlbum());
            artistNameTextView.setText(playState.getPlayingSong().getArtist());
        } else {
            songNameTextView.setText("暂无播放中歌曲");
            albumNameTextView.setText("");
            artistNameTextView.setText("");
        }
        if (!playState.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.play_btn_white);
        } else {
            playPauseButton.setImageResource(R.drawable.pause_btn_white);
        }
    }

    private void setUpListeners() {
        // 返回按钮
        this.backButton.setOnClickListener(v -> finish());
        // 播放暂停按钮
        this.playPauseButton.setOnClickListener(v -> {
            // 获取PlayState实例
            PlayState playState = PlayState.getInstance();
            if (playState.getPlayingSong() == null)
                return;
            // 如果当前播放状态为暂停，则点击后开始播放
            if (!playState.isPlaying()) {
                playState.setPlaying(true);
                playPauseButton.setImageResource(R.drawable.pause_btn_white);
                MusicPlayerManager.getInstance().getMusicPlayerService().resumeSong();
            } else {
                playState.setPlaying(false);
                playPauseButton.setImageResource(R.drawable.play_btn_white);
                MusicPlayerManager.getInstance().getMusicPlayerService().pauseSong();
            }
        });
        // 上一首按钮
        this.prevButton.setOnClickListener(v -> {
        });
        // 下一首按钮
        this.nextButton.setOnClickListener(v -> {
        });
    }

    @Override
    public void register() {
        PlayState.getInstance().attach(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void update() {
        Log.d("PlayingBarFragment", "update: " + PlayState.getInstance().isPlaying());
        PlayState playState = PlayState.getInstance();
        if (playState.getPlayingSong() != null) {
            songNameTextView.setText(playState.getPlayingSong().getTitle());
            albumNameTextView.setText(playState.getPlayingSong().getAlbum());
            artistNameTextView.setText(playState.getPlayingSong().getArtist());
        } else {
            songNameTextView.setText("暂无播放中歌曲");
            albumNameTextView.setText("");
            artistNameTextView.setText("");
        }
        if (!playState.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.play_btn_white);
        } else {
            playPauseButton.setImageResource(R.drawable.pause_btn_white);
        }
    }

    private void updateSeekBarProgress() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSeekBarTracking) {
                    // 获取当前播放进度
                    int progress = MusicPlayerManager.getInstance()
                            .getMusicPlayerService()
                            .getMediaPlayer()
                            .getCurrentPosition();

                    // 更新 SeekBar 的进度
                    seekBar.setProgress(progress);
                }

                // 继续更新 SeekBar 的进度
                updateSeekBarProgress();
            }
        }, 1000); // 每隔一秒更新一次
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 用户手动拖动 SeekBar 时不做处理
        if (fromUser) {
            return;
        }

        // 更新 MediaPlayer 的播放进度
        MusicPlayerManager.getInstance()
                .getMusicPlayerService()
                .getMediaPlayer()
                .seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // 用户开始拖动 SeekBar，设置标志位为 true
        isSeekBarTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 用户停止拖动 SeekBar，设置标志位为 false
        isSeekBarTracking = false;

        // 获取 SeekBar 当前的进度
        int progress = seekBar.getProgress();

        // 将 MediaPlayer 的播放位置设置为该进度
        MusicPlayerManager.getInstance()
                .getMusicPlayerService()
                .getMediaPlayer()
                .seekTo(progress);
    }
}
