package indi.kwanho.pm.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import indi.kwanho.pm.R;
import indi.kwanho.pm.common.PlayingMode;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.entity.Album;
import indi.kwanho.pm.entity.Singer;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.persisitance.domain.FavoriteRecord;
import indi.kwanho.pm.persisitance.repository.FavoriteRecordRepository;
import indi.kwanho.pm.store.LocalMusicState;
import indi.kwanho.pm.store.PlayState;
import indi.kwanho.pm.utils.LocalMusicUtil;

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
    private ImageButton playControlModeButton;
    private ImageButton playControlMoreButton;
    private ImageButton favoriteButton;
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
        this.playControlModeButton = findViewById(R.id.play_control_play_mode_button);
        this.playControlMoreButton = findViewById(R.id.play_control_more_button);
        this.favoriteButton = findViewById(R.id.play_control_favorite_button);
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
        // seekBar
        seekBar.setOnSeekBarChangeListener(this);
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
            Log.d("PlayingBarFragment", "onClick: prev");
            MusicPlayerManager.getInstance().getMusicPlayerService().prevSong();
        });
        // 下一首按钮
        this.nextButton.setOnClickListener(v -> {
            Log.d("PlayingBarFragment", "onClick: next");
            MusicPlayerManager.getInstance().getMusicPlayerService().nextSong();
        });
        // 播放模式按钮
        this.playControlModeButton.setOnClickListener(v -> {
            switch (PlayState.getInstance().getPlayingMode()) {
                case PlayingMode.LIST:
                    PlayState.getInstance().setPlayingMode(PlayingMode.RANDOM);
                    playControlModeButton.setImageResource(R.drawable.random_btn);
                    break;
                case PlayingMode.RANDOM:
                    PlayState.getInstance().setPlayingMode(PlayingMode.SINGLE);
                    playControlModeButton.setImageResource(R.drawable.single_btn);
                    break;
                case PlayingMode.SINGLE:
                    PlayState.getInstance().setPlayingMode(PlayingMode.LIST);
                    playControlModeButton.setImageResource(R.drawable.list_loop_btn);
                    break;
            }
        });
        // 更多按钮
        this.playControlMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建PopupMenu对象，并传入上下文和关联的视图
                PopupMenu popupMenu = new PopupMenu(PlayControlActivity.this, playControlMoreButton);
                // 为弹窗菜单添加布局文件
                popupMenu.getMenuInflater().inflate(R.menu.favorite_menu, popupMenu.getMenu());
                // 设置菜单项的点击事件监听器
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.add_to_playlist) {
                            // 执行添加到歌单的逻辑
                            Log.d("PlayingBarFragment", "onMenuItemClick: add to playlist");
                        } else if (itemId == R.id.view_album) {
                            // 执行查看专辑的逻辑
                            Song playingSong = PlayState.getInstance().getPlayingSong();
                            List<Album> albums = LocalMusicState.getInstance().getAlbums();
                            for (Album album : albums) {
                                if (album.getName().equals(playingSong.getAlbum())) {
                                    LocalMusicUtil.getSongsByAlbum(album);
                                    Intent intent = new Intent(PlayControlActivity.this, LocalDetailActivity.class);
                                    intent.putExtra("pageTitle", album.getName());
                                    startActivity(intent);
                                    break;
                                }
                            }
                        } else if (itemId == R.id.view_artist) {
                            // 执行查看歌手的逻辑
                            Song playingSong = PlayState.getInstance().getPlayingSong();
                            List<Singer> artists = LocalMusicState.getInstance().getSingers();
                            for (Singer artist : artists) {
                                if (artist.getName().equals(playingSong.getArtist())) {
                                    LocalMusicUtil.getSongsBySinger(artist);
                                    Intent intent = new Intent(PlayControlActivity.this, LocalDetailActivity.class);
                                    intent.putExtra("pageTitle", artist.getName());
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }

                        return true;
                    }
                });
                // 显示弹窗菜单
                popupMenu.show();
            }
        });
        // 收藏按钮
        favoriteButton.setOnClickListener(v -> {
            Log.d("这里呢", "onClick: favorite");
            Song playingSong = PlayState.getInstance().getPlayingSong();
            if (playingSong == null)
                return;
            FavoriteRecordRepository favoriteRecordRepository = new FavoriteRecordRepository(this);
//            LiveData<List<FavoriteRecord>> allFavoriteRecords = favoriteRecordRepository.getAllFavoriteRecords();
//            AtomicBoolean hasFavorite = new AtomicBoolean(false);
//            allFavoriteRecords.observe(this, favoriteRecords -> {
//                for (FavoriteRecord favoriteRecord : favoriteRecords) {
//                    if (favoriteRecord.getFilePath().equals(playingSong.getFilePath())) {
//                        hasFavorite.set(true);
//                        return;
//                    }
//                }
//            });
//            if (hasFavorite.get()) {
//                return;
//            }
            Log.d("添加收藏", "onClick: " + playingSong.getTitle());
            FavoriteRecord favoriteRecord = new FavoriteRecord(
                    playingSong.getTitle(),
                    playingSong.getArtist(),
                    playingSong.getAlbum(),
                    playingSong.getFilePath()
            );
            favoriteRecordRepository.insertPlayRecord(favoriteRecord);
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
            seekBar.setProgress(0);
        }
        if (!playState.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.play_btn_white);
        } else {
            playPauseButton.setImageResource(R.drawable.pause_btn_white);
        }
    }

    private void updateSeekBarProgress() {
        handler.postDelayed(() -> {
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
        }, 1000); // 每隔一秒更新一次
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 用户手动拖动 SeekBar 时不做处理
        if (!fromUser) {
            return;
        }

        Log.d("PlayControlActivity", "onProgressChanged: " + progress);

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
    }
}
