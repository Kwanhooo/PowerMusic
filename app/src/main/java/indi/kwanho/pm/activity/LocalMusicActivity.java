package indi.kwanho.pm.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.local.LocalTabAdapter;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;

public class LocalMusicActivity extends AppCompatActivity {

    private ImageButton imageButtonBackLocalMusic;
    private TabLayout tableLayoutLocalMusic;
    private ViewPager viewPagerLocalMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        getActualViews();
        initTabLayout();
        wiredWidgets();
        setUpListeners();
    }

    private void initTabLayout() {
        this.viewPagerLocalMusic.setAdapter(new LocalTabAdapter(getSupportFragmentManager()));
        this.tableLayoutLocalMusic.setupWithViewPager(this.viewPagerLocalMusic);
    }

    private void setUpListeners() {
        this.imageButtonBackLocalMusic.setOnClickListener(v -> {
            finish();
        });
    }

    private void getActualViews() {
        this.imageButtonBackLocalMusic = findViewById(R.id.image_button_back_local_music);
        this.tableLayoutLocalMusic = findViewById(R.id.table_layout_local_music);
        this.viewPagerLocalMusic = findViewById(R.id.view_pager_local_music);
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
