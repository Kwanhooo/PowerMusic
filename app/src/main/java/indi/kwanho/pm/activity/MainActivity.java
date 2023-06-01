package indi.kwanho.pm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import indi.kwanho.pm.R;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;

public class MainActivity extends AppCompatActivity {
    private ImageView localMusicEntranceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActualViews();
        wiredWidgets();
        setUpListeners();
    }

    private void getActualViews() {
        localMusicEntranceButton = findViewById(R.id.local_music_entrance_button);
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
    }
}