package indi.kwanho.pm.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

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
import indi.kwanho.pm.persisitance.domain.PlaylistItemRecord;
import indi.kwanho.pm.persisitance.domain.PlaylistRecord;
import indi.kwanho.pm.persisitance.repository.FavoriteRecordRepository;
import indi.kwanho.pm.persisitance.repository.PlaylistItemRecordRepository;
import indi.kwanho.pm.persisitance.repository.PlaylistRecordRepository;
import indi.kwanho.pm.store.LocalMusicState;
import indi.kwanho.pm.store.PlayState;
import indi.kwanho.pm.store.PlaylistState;
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
                    Toast.makeText(this, "切换至随机播放", Toast.LENGTH_SHORT).show();
                    break;
                case PlayingMode.RANDOM:
                    PlayState.getInstance().setPlayingMode(PlayingMode.SINGLE);
                    playControlModeButton.setImageResource(R.drawable.single_btn);
                    Toast.makeText(this, "切换至单曲循环", Toast.LENGTH_SHORT).show();
                    break;
                case PlayingMode.SINGLE:
                    PlayState.getInstance().setPlayingMode(PlayingMode.LIST);
                    playControlModeButton.setImageResource(R.drawable.list_loop_btn);
                    Toast.makeText(this, "切换至列表循环", Toast.LENGTH_SHORT).show();
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
                            // 弹出一个dialog，让用户选择要添加到的歌单，并添加到歌单中

                            AlertDialog.Builder builder = new AlertDialog.Builder(PlayControlActivity.this);
                            builder.setTitle("选择歌单");

                            // 获取现有的歌单列表
                            List<PlaylistRecord> playlists = PlaylistState.getInstance().getPlaylistRecords();
                            String[] playlistNames = new String[playlists.size()];
                            for (int i = 0; i < playlists.size(); i++) {
                                playlistNames[i] = playlists.get(i).getTitle();
                            }
                            builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 获取选中的歌单
                                    PlaylistRecord selectedPlaylist = playlists.get(which);

                                    // 执行添加到歌单的逻辑
                                    // TODO: 添加到选中的歌单中
                                    handleAddToPlaylist(PlayState.getInstance().getPlayingSong(), selectedPlaylist);
                                }
                            });

                            builder.create().show();

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
            Song playingSong = PlayState.getInstance().getPlayingSong();
            if (playingSong == null)
                return;
            handleAddToFavorite(playingSong);
        });
    }

    private void handleAddToFavorite(Song playingSong) {
        FavoriteRecordRepository favoriteRecordRepository = new FavoriteRecordRepository(this);

        AtomicBoolean isExist = new AtomicBoolean(false);
        favoriteRecordRepository.getFavoriteRecordsByFilePath(playingSong.getFilePath()).observe(this, favoriteRecords -> {
            for (FavoriteRecord favoriteRecord : favoriteRecords) {
                // 如果已经存在于收藏列表中，则不再添加
                if (favoriteRecord.getFilePath().equals(playingSong.getFilePath())) {
                    isExist.set(true);
                }
            }

            // 如果不存在于收藏列表中，则执行添加操作
            if (!isExist.get()) {
                // 在FavoriteRecordRepository中添加一条记录
                FavoriteRecord favoriteRecord = new FavoriteRecord(
                        playingSong.getTitle(),
                        playingSong.getArtist(),
                        playingSong.getAlbum(),
                        playingSong.getFilePath()
                );
                favoriteRecordRepository.insertFavoriteRecord(favoriteRecord);

                Toast.makeText(PlayControlActivity.this, "已添加到收藏列表", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlayControlActivity.this, "歌曲已存在于收藏列表中", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleAddToPlaylist(Song playingSong, PlaylistRecord selectedPlaylist) {
        PlaylistItemRecordRepository playlistItemRepository = new PlaylistItemRecordRepository(this);

        AtomicBoolean isExist = new AtomicBoolean(false);
        playlistItemRepository.getPlaylistItemRecordsByFilePath(playingSong.getFilePath()).observe(this, playlistItemRecords -> {
            for (PlaylistItemRecord playlistItemRecord : playlistItemRecords) {
                Log.d("playlistItemRecord", "handleAddToPlaylist: " + playlistItemRecord.getPlaylistId() + " " + selectedPlaylist.getId());
                // 如果已经存在于歌单中，则不再添加
                if (playlistItemRecord.getPlaylistId() == selectedPlaylist.getId()) {
                    isExist.set(true);
                }
            }

            // 如果不存在于歌单中，则执行添加操作
            if (!isExist.get()) {
                // 在PlaylistItemRepository中添加一条记录，与selectedPlaylist的id关联
                PlaylistItemRecord itemRecord = new PlaylistItemRecord(
                        playingSong.getTitle(),
                        playingSong.getArtist(),
                        playingSong.getAlbum(),
                        playingSong.getFilePath(),
                        selectedPlaylist.getId()
                );
                playlistItemRepository.insertPlaylistItemRecord(itemRecord);

                selectedPlaylist.setCount(selectedPlaylist.getCount() + 1);
                // 在PlaylistRecordRepository中更新selectedPlaylist的count
                PlaylistRecordRepository playlistRecordRepository = new PlaylistRecordRepository(this);
                playlistRecordRepository.updatePlayRecord(selectedPlaylist);

                // 从数据库重新读取一次歌单列表
                playlistRecordRepository.getAllPlaylistRecords().observe(this, playlistRecords -> {
                    PlaylistState.getInstance().setPlaylistRecords(playlistRecords);
                });

                Toast.makeText(PlayControlActivity.this, "已添加到歌单：" + selectedPlaylist.getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlayControlActivity.this, "歌曲已存在于歌单：" + selectedPlaylist.getTitle(), Toast.LENGTH_SHORT).show();
            }
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
