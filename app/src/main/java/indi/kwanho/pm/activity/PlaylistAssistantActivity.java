package indi.kwanho.pm.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import indi.kwanho.pm.R;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;
import indi.kwanho.pm.fragment.general.PlaylistFragment;

public class PlaylistAssistantActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_detail);

        getActualElements();
        setUpListeners();
        wiredWidgets();
    }

    private void getActualElements() {

    }

    private void setUpListeners() {
    }

    private void wiredWidgets() {
        // 创建PlayingBarFragment的实例
        PlayingBarFragment playingBarFragment = new PlayingBarFragment();
        PlaylistFragment playlistFragment = new PlaylistFragment();

        // 将PlayingBarFragment添加到Fragment容器
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_playlist_container_main, playlistFragment)
                .add(R.id.activity_playlist_container_play_bar, playingBarFragment)
                .commit();
    }
}
