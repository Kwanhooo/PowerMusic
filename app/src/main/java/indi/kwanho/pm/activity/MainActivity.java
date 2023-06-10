package indi.kwanho.pm.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.persisitance.AppDatabase;
import indi.kwanho.pm.persisitance.dao.PlayRecordDao;
import indi.kwanho.pm.persisitance.domain.FavoriteRecord;
import indi.kwanho.pm.persisitance.domain.PlayRecord;
import indi.kwanho.pm.persisitance.repository.FavoriteRecordRepository;
import indi.kwanho.pm.persisitance.repository.PlayRecordRepository;
import indi.kwanho.pm.service.MusicPlayerService;
import indi.kwanho.pm.utils.LocalMusicUtil;

public class MainActivity extends AppCompatActivity {
    private ImageView localMusicEntranceButton;
    private ImageView favoriteMusicEntranceButton;
    private ImageView recentPlayEntranceButton;
    private FrameLayout frameLayout;
    private MusicPlayerService musicPlayerService;
    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
            musicPlayerService = binder.getService();
            isServiceBound = true;
//            musicPlayerService.startService()
            MusicPlayerManager.getInstance().setMusicPlayerService(musicPlayerService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (isServiceBound) {
//            unbindService(serviceConnection);
//            isServiceBound = false;
//        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActualViews();
        wiredWidgets();
        setUpListeners();
//        testDb();
    }

    private void testDb() {
        AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "pm")
                .allowMainThreadQueries()
                .build();
    }

    private void getActualViews() {
        localMusicEntranceButton = findViewById(R.id.local_music_entrance_button);
        favoriteMusicEntranceButton = findViewById(R.id.favorite_music_entrance_button);
        recentPlayEntranceButton = findViewById(R.id.recent_play_entrance_button);
        frameLayout = findViewById(R.id.fragment_container);
    }

    private void wiredWidgets() {
        // 创建PlayingBarFragment的实例
        PlayingBarFragment playingBarFragment = new PlayingBarFragment();

        // 将PlayingBarFragment添加到Fragment容器
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, playingBarFragment)
                .commit();
    }

    private void setUpListeners() {
        localMusicEntranceButton.setOnClickListener(v -> {
            // 跳转到LocalMusicActivity
            Intent intent = new Intent(MainActivity.this, LocalMusicActivity.class);
            startActivity(intent);
        });
        recentPlayEntranceButton.setOnClickListener(v -> {
            // 跳转到RecentPlayActivity
            LocalMusicUtil.loadRecentToDetail(this);
            Intent intent = new Intent(MainActivity.this, LocalDetailActivity.class);
            intent.putExtra("pageTitle", "最近播放");
            startActivity(intent);
        });
        favoriteMusicEntranceButton.setOnClickListener(v -> {
            // 跳转到FavoriteMusicActivity
            LocalMusicUtil.loadFavoriteToDetail(this);
            Intent intent = new Intent(MainActivity.this, LocalDetailActivity.class);
            intent.putExtra("pageTitle", "我的收藏");
            startActivity(intent);
        });
        frameLayout.setOnClickListener(v -> {
            // 跳转到PlayControlActivity
            Intent intent = new Intent(MainActivity.this, PlayControlActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}