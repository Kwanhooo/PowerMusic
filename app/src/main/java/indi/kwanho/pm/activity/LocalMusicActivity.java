package indi.kwanho.pm.activity;

import static indi.kwanho.pm.common.Constants.REQUEST_CODE_STORAGE_PERMISSION;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.local.LocalTabAdapter;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;
import indi.kwanho.pm.store.LocalMusicState;
import indi.kwanho.pm.store.SingleSong;
import indi.kwanho.pm.utils.LocalMusicUtil;

public class LocalMusicActivity extends AppCompatActivity {

    private ImageButton imageButtonBackLocalMusic;
    private TabLayout tableLayoutLocalMusic;
    private ViewPager viewPagerLocalMusic;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        indexLocalMusic();
        getPermissions();
        getActualViews();
        initTabLayout();
        wiredWidgets();
        setUpListeners();
    }

    private void getPermissions() {
        // 在合适的地方（如Activity或Fragment）中请求权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果权限尚未被授予，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("LocalMusicActivity", "onRequestPermissionsResult: " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了存储权限，可以开始访问音乐文件
                indexLocalMusic();
            } else {
                // 用户拒绝了存储权限，您可以根据需要进行适当的处理
                Log.d("LocalMusicActivity", "onRequestPermissionsResult: " + "用户拒绝了存储权限");
            }
        }
    }


    private void indexLocalMusic() {
        LocalMusicUtil.scanLocalMusic(this);
        List<Song> songs = LocalMusicState.getInstance().getSongs();
        Log.d("LocalMusicActivity", "indexLocalMusic: " + songs.size());
        for (Song song : songs) {
            Log.d("LocalMusicActivity", "indexLocalMusic: " + song.toString());
        }
    }

    private void initTabLayout() {
        this.viewPagerLocalMusic.setAdapter(new LocalTabAdapter(getSupportFragmentManager()));
        this.tableLayoutLocalMusic.setupWithViewPager(this.viewPagerLocalMusic);
    }

    private void setUpListeners() {
        this.imageButtonBackLocalMusic.setOnClickListener(v -> {
            finish();
        });
        this.frameLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayControlActivity.class);
            startActivity(intent);
        });
    }

    private void getActualViews() {
        this.imageButtonBackLocalMusic = findViewById(R.id.image_button_back_local_music);
        this.tableLayoutLocalMusic = findViewById(R.id.table_layout_local_music);
        this.viewPagerLocalMusic = findViewById(R.id.view_pager_local_music);
        this.frameLayout = findViewById(R.id.fragment_container_local_music);
    }


    private void wiredWidgets() {
        // 创建PlayingBarFragment的实例
        PlayingBarFragment playingBarFragment = new PlayingBarFragment();

        // 将PlayingBarFragment添加到Fragment容器
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_local_music, playingBarFragment)
                .commit();
    }
}
