package indi.kwanho.pm.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import indi.kwanho.pm.R;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;
import indi.kwanho.pm.fragment.general.PlaylistFragment;

public class PlaylistAssistantActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_assistant);

        getActualElements();
        setUpListeners();
        wiredWidgets();
    }

    private void getActualElements() {
        // 获取其他元素
    }

    private void setUpListeners() {
        // 设置监听器
    }

    private void wiredWidgets() {
        // 创建PlayingBarFragment的实例
        PlayingBarFragment playingBarFragment = new PlayingBarFragment();
        PlaylistFragment playlistFragment = new PlaylistFragment();

        // 将PlayingBarFragment添加到Fragment容器
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_playlist_container_main, playlistFragment);
        transaction.replace(R.id.activity_playlist_container_play_bar, playingBarFragment);
        transaction.commit();
    }
}
