package indi.kwanho.pm.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import indi.kwanho.pm.R;
import indi.kwanho.pm.fragment.general.CreatePlaylistFragment;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;
import indi.kwanho.pm.fragment.general.PlaylistFragment;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.persisitance.AppDatabase;
import indi.kwanho.pm.service.MusicPlayerService;
import indi.kwanho.pm.utils.LocalMusicUtil;

public class MainActivity extends AppCompatActivity {
    private ImageView localMusicEntranceButton;
    private ImageView favoriteMusicEntranceButton;
    private ImageView recentPlayEntranceButton;
    private ImageView playControlEntranceButton;
    private LinearLayout favoriteMusicComponent;
    private Button listenNowButton;
    private FrameLayout frameLayout;
    private FrameLayout createPlaylistFragmentContainer;
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
        playControlEntranceButton = findViewById(R.id.play_control_entrance_button);
        favoriteMusicComponent = findViewById(R.id.favorite_music_component);
        listenNowButton = findViewById(R.id.listen_now_button);
        frameLayout = findViewById(R.id.fragment_container);
        createPlaylistFragmentContainer = findViewById(R.id.create_play_list_fragment_container);
    }

    private void wiredWidgets() {
        // 创建PlayingBarFragment的实例
        PlayingBarFragment playingBarFragment = new PlayingBarFragment();
        CreatePlaylistFragment createPlaylistFragment = new CreatePlaylistFragment();
        PlaylistFragment playlistFragment = new PlaylistFragment();

        // 将PlayingBarFragment添加到Fragment容器
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, playingBarFragment)
                .add(R.id.create_play_list_fragment_container, createPlaylistFragment)
                .add(R.id.play_list_fragment_container, playlistFragment)
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
            LocalMusicUtil.gotoRecent(this);
//            Intent intent = new Intent(MainActivity.this, LocalDetailActivity.class);
//            intent.putExtra("pageTitle", "最近播放");
//            startActivity(intent);
        });
        favoriteMusicEntranceButton.setOnClickListener(v -> {
            // 跳转到FavoriteMusicActivity
            LocalMusicUtil.gotoFavorite(this);
//            Intent intent = new Intent(MainActivity.this, LocalDetailActivity.class);
//            intent.putExtra("pageTitle", "我的收藏");
//            startActivity(intent);
        });
        frameLayout.setOnClickListener(v -> {
            // 跳转到PlayControlActivity
            Intent intent = new Intent(MainActivity.this, PlayControlActivity.class);
            startActivity(intent);
        });
        playControlEntranceButton.setOnClickListener(v -> {
            // 跳转到PlayControlActivity
            Intent intent = new Intent(MainActivity.this, PlayControlActivity.class);
            startActivity(intent);
        });
        favoriteMusicComponent.setOnClickListener(v -> {
            // 跳转到FavoriteMusicActivity
            LocalMusicUtil.gotoFavorite(this);
//            Intent intent = new Intent(MainActivity.this, LocalDetailActivity.class);
//            intent.putExtra("pageTitle", "我的收藏");
//            startActivity(intent);
        });
        listenNowButton.setOnClickListener(v -> {
            // 跳转到PlayControlActivity
            LocalMusicUtil.loadAndPlayFavorite(this);
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