package indi.kwanho.pm.fragment.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import indi.kwanho.pm.R;
import indi.kwanho.pm.store.PlayState;

public class PlayingBarFragment extends Fragment {
    private ImageButton playPauseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playing_bar, container, false);
        getActualElements(view);
        setUpListeners();
        return view;
    }

    private void getActualElements(View view) {
        playPauseButton = view.findViewById(R.id.play_pause_button);
    }

    private void setUpListeners() {
        this.playPauseButton.setOnClickListener(v -> {
            // 获取PlayState实例
            PlayState playState = PlayState.getInstance();
            // 如果当前播放状态为暂停，则点击后开始播放
            if (!playState.isPlaying()) {
                playState.setPlaying(true);
                playPauseButton.setImageResource(R.drawable.play_button);
            } else {
                playState.setPlaying(false);
                playPauseButton.setImageResource(R.drawable.pause_button);
            }
        });
    }
}
