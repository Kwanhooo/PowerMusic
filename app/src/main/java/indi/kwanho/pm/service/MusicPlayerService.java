package indi.kwanho.pm.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Date;

import indi.kwanho.pm.R;
import indi.kwanho.pm.activity.MainActivity;
import indi.kwanho.pm.broadcast.NotificationButtonReceiver;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.persisitance.domain.PlayRecord;
import indi.kwanho.pm.persisitance.repository.PlayRecordRepository;
import indi.kwanho.pm.store.PlayState;
import indi.kwanho.pm.utils.PlayModeUtil;

public class MusicPlayerService extends Service implements PowerObserver {
    private static final int NOTIFICATION_ID = 114514;
    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private final IBinder binder = new MusicPlayerBinder();
    private MediaPlayer mediaPlayer;
    // private boolean isPlaying = false;
    private RemoteViews notificationLayoutExpanded;

    // 广播接收器
    private final BroadcastReceiver notificationButtonReceiver = new NotificationButtonReceiver();

    @Override
    public void onCreate() {
        Log.d("MusicPlayerService", "onCreate");
        super.onCreate();
        createNotificationChannel();
        register();
        mediaPlayer = new MediaPlayer();
        this.notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_layout);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 音乐播放完成
                Log.d("MusicPlayerService", "音乐播放完成");

                // 记录到最近播放
                PlayRecordRepository playRecordRepository = new PlayRecordRepository(getApplication());
                PlayRecord playRecord = new PlayRecord(
                        PlayState.getInstance().getPlayingSong().getTitle(),
                        PlayState.getInstance().getPlayingSong().getAlbum(),
                        PlayState.getInstance().getPlayingSong().getArtist(),
                        PlayState.getInstance().getPlayingSong().getFilePath(),
                        new Date()
                );
                playRecordRepository.insertPlayRecord(playRecord);

                PlayState.getInstance().setPlaying(false);
                PlayState.getInstance().setPlayingSong(null);

                // 播放下一首
                playSong(PlayModeUtil.next());
            }
        });

        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("NotificationPreviousButtonClicked");
        filter.addAction("NotificationPlayPauseButtonClicked");
        filter.addAction("NotificationNextButtonClicked");
        registerReceiver(notificationButtonReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }

    private Notification createNotification() {
        // 创建点击事件的 PendingIntent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // 自定义通知栏布局
        // RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_layout);

        // 设置初始化的通知栏布局内容
        notificationLayoutExpanded.setTextViewText(R.id.notification_bar_songTitle, "无播放中音乐");
        notificationLayoutExpanded.setTextViewText(R.id.notification_bar_artistAlbum, "");

        // 创建通知
        Notification customNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.play_button)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        return customNotification;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "PowerMusic",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // 其他播放音乐的相关方法
    @Override
    public void onDestroy() {
        Log.d("MusicPlayerService", "onDestroy");
        unregisterReceiver(notificationButtonReceiver);
        super.onDestroy();
        stopForeground(true);
        mediaPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 让服务成为前台服务
        startForeground(NOTIFICATION_ID, createNotification());
        return binder;
    }

    @Override
    public void register() {
        PlayState.getInstance().attach(this);
    }

    @Override
    public void update() {
        Log.d("MusicPlayerService", "update!");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        this.notificationLayoutExpanded.setImageViewResource(
                R.id.notification_bar_playPauseButton,
                PlayState.getInstance().isPlaying() ? R.drawable.pause_btn_white : R.drawable.play_btn_white
        );

        // 设置初始化的通知栏布局内容
        notificationLayoutExpanded.setTextViewText(R.id.notification_bar_songTitle, PlayState.getInstance().getPlayingSong() == null ? "无播放中音乐" : PlayState.getInstance().getPlayingSong().getTitle());
        notificationLayoutExpanded.setTextViewText(R.id.notification_bar_artistAlbum, PlayState.getInstance().getPlayingSong() == null ? "" : PlayState.getInstance().getPlayingSong().getArtist() + " - " + PlayState.getInstance().getPlayingSong().getAlbum());

        // 设置按钮点击事件广播
        // 上一首按钮点击事件
        Intent previousIntent = new Intent("NotificationPreviousButtonClicked");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.notification_bar_previousButton, previousPendingIntent);

        // 播放/暂停按钮点击事件
        Intent playPauseIntent = new Intent("NotificationPlayPauseButtonClicked");
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.notification_bar_playPauseButton, playPausePendingIntent);

        // 下一首按钮点击事件
        Intent nextIntent = new Intent("NotificationNextButtonClicked");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.notification_bar_nextButton, nextPendingIntent);

        // 创建通知
        Notification customNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.play_button)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(NOTIFICATION_ID, customNotification);
    }

    public class MusicPlayerBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    public void playSong(String filePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeSong() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void nextSong() {
        if (mediaPlayer != null) {
            PlayState.getInstance().setPlaying(false);
            PlayState.getInstance().setPlayingSong(null);

            // 播放下一首
            playSong(PlayModeUtil.next());
        }
    }

    public void prevSong() {
        if (mediaPlayer != null) {
            PlayState.getInstance().setPlaying(false);
            PlayState.getInstance().setPlayingSong(null);

            // 播放下一首
            playSong(PlayModeUtil.prev());
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


}
