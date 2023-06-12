package indi.kwanho.pm.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
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
    private ImageButton setAsRingtoneButton;
    private TextView playControlCurrentTimeTextView;
    private TextView playControlTotalTimeTextView;

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
        getPermissions();
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // 应用已经具有WRITE_SETTINGS权限
                Log.d("PlayControlActivity", "应用已经具有WRITE_SETTINGS权限");
            } else {
                // 应用没有WRITE_SETTINGS权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
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
        this.setAsRingtoneButton = findViewById(R.id.set_as_ringtone_button);
        this.playControlCurrentTimeTextView = findViewById(R.id.play_control_current_time);
        this.playControlTotalTimeTextView = findViewById(R.id.play_control_total_time);
    }

    private void initData() {
        updateSongImage();
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
        setAsRingtoneButton.setOnClickListener(v -> {
            handleSetAsRingtone(PlayState.getInstance().getPlayingSong());
        });
    }

    private void handleSetAsRingtone(Song playingSong) {
        getPermissions();
        Log.d("PlayingBarFragment", "handleSetAsRingtone: " + playingSong.getFilePath());

        File audioFile = new File(playingSong.getFilePath());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Music/Ringtones");
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, playingSong.getTitle() + ".mp3");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                values.put(MediaStore.Audio.Media.IS_ALARM, false);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                ContentResolver resolver = getContentResolver();
                Uri audioUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

                if (audioUri != null) {
                    OutputStream outputStream = resolver.openOutputStream(audioUri);
                    if (outputStream != null) {
                        FileInputStream inputStream = new FileInputStream(audioFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();

                        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, audioUri);
                        Toast.makeText(this, "铃声设置成功", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, audioFile.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, playingSong.getTitle());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");

                Uri audioUri = MediaStore.Audio.Media.getContentUriForPath(audioFile.getAbsolutePath());
                Uri newUri = getContentResolver().insert(audioUri, values);

                RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
                Toast.makeText(this, "铃声设置成功", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "铃声设置失败", Toast.LENGTH_SHORT).show();
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
                PlaylistState.getInstance().notifyObservers();
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
        updateSongImage();
    }

    private void updateSongImage() {
        if (PlayState.getInstance().getPlayingSong() == null) {
            // 使用默认图片
            ImageView imageView = findViewById(R.id.play_control_song_image);
            imageView.setImageResource(R.drawable.album_image);
            return;
        }
        String musicFilePath = PlayState.getInstance().getPlayingSong().getFilePath();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(musicFilePath);

        byte[] albumArtBytes = retriever.getEmbeddedPicture();
        Bitmap albumArt;

        if (albumArtBytes != null && albumArtBytes.length > 0) {
            Log.d("albumArtBytes", "使用专辑图片");
            albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
        } else {
            Log.d("albumArtBytes", "使用默认图片");
            albumArt = BitmapFactory.decodeResource(getResources(), R.drawable.album_image);
        }

        Log.d("musicPath", "updateSongImage: " + musicFilePath);
        ImageView imageView = findViewById(R.id.play_control_song_image);
        imageView.setImageBitmap(albumArt);
    }

    private void updateSeekBarProgress() {
        handler.postDelayed(() -> {
            if (!isSeekBarTracking) {
                MediaPlayer mediaPlayer = MusicPlayerManager.getInstance()
                        .getMusicPlayerService()
                        .getMediaPlayer();

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // 获取当前播放进度和总时长
                    int progress = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    // 更新 SeekBar 的进度
                    seekBar.setProgress(progress);
                    seekBar.setMax(duration);

                    // 更新当前播放时长和总时长的 TextView
                    String currentDurationStr = convertMillisecondsToTime(progress);
                    String totalDurationStr = convertMillisecondsToTime(duration);
                    playControlCurrentTimeTextView.setText(currentDurationStr);
                    playControlTotalTimeTextView.setText(totalDurationStr);
                }
            }

            // 继续更新 SeekBar 的进度和时长数据
            updateSeekBarProgress();
        }, 1000); // 每隔一秒更新一次
    }

    // 辅助方法：将毫秒转换为时间字符串（格式：mm:ss）
    private String convertMillisecondsToTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds);
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
