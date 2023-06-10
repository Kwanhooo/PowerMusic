package indi.kwanho.pm.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

import indi.kwanho.pm.R;
import indi.kwanho.pm.activity.MainActivity;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.store.PlayState;

public class MusicPlayerService extends Service implements PowerObserver {
    private static final int NOTIFICATION_ID = 114514;
    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private final IBinder binder = new MusicPlayerBinder();

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    public void onCreate() {
        Log.d("MusicPlayerService", "onCreate");
        super.onCreate();
        createNotificationChannel();
        register();
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 音乐播放完成
                Log.d("MusicPlayerService", "音乐播放完成");
                PlayState.getInstance().setPlaying(false);
                PlayState.getInstance().setPlayingSong(null);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSubText("播放中")
                .setContentTitle("暂无播放中的音乐")
                .setContentText("请在应用内选择音乐...")
                .setSmallIcon(R.drawable.album_image)
                .setContentIntent(pendingIntent);

        return builder.build();
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
        super.onDestroy();
        stopForeground(true);
        Log.d("MusicPlayerService", "onDestroy");
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
        Log.d("MusicPlayerService", "update!!!");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        // 更新通知栏的内容
        NotificationCompat.Builder builder;
        if (PlayState.getInstance().getPlayingSong() != null) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSubText("播放中")
                    .setContentTitle(PlayState.getInstance().getPlayingSong().getTitle())
                    .setContentText("由 " + PlayState.getInstance().getPlayingSong().getArtist() + " 激情演唱")
                    .setSmallIcon(R.drawable.album_image)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSubText("播放中")
                    .setContentTitle("暂无播放中的音乐")
                    .setContentText("请在应用内选择音乐...")
                    .setSmallIcon(R.drawable.album_image)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
