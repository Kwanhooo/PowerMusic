package indi.kwanho.pm.fragment.general;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import indi.kwanho.pm.R;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.store.PlayState;

public class PlayingBarFragment extends Fragment implements PowerObserver {
    private ImageButton playPauseButton;
    private TextView titleTextView;
    private TextView artistTextView;
    private ImageView songImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playing_bar, container, false);
        getActualElements(view);
        initData();
        setUpListeners();
        register();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        PlayState playState = PlayState.getInstance();
        if (playState.getPlayingSong() != null) {
            titleTextView.setText(playState.getPlayingSong().getTitle());
            artistTextView.setText("  -  " + playState.getPlayingSong().getArtist());
        } else {
            titleTextView.setText("暂无播放中歌曲");
            artistTextView.setText("");
        }
        if (!playState.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.play_button);
        } else {
            playPauseButton.setImageResource(R.drawable.pause_button);
        }
        updateSongImage();
    }

    private void getActualElements(View view) {
        playPauseButton = view.findViewById(R.id.play_pause_button);
        titleTextView = view.findViewById(R.id.playing_bar_song_name);
        artistTextView = view.findViewById(R.id.playing_bar_artist_name);
        songImage = view.findViewById(R.id.playing_bar_song_image);
    }

    private void setUpListeners() {
        this.playPauseButton.setOnClickListener(v -> {
            // 获取PlayState实例
            PlayState playState = PlayState.getInstance();
            if (playState.getPlayingSong() == null)
                return;
            // 如果当前播放状态为暂停，则点击后开始播放
            if (!playState.isPlaying()) {
                playState.setPlaying(true);
                playPauseButton.setImageResource(R.drawable.pause_button);
                MusicPlayerManager.getInstance().getMusicPlayerService().resumeSong();
            } else {
                playState.setPlaying(false);
                playPauseButton.setImageResource(R.drawable.play_button);
                MusicPlayerManager.getInstance().getMusicPlayerService().pauseSong();
            }
        });
    }

    @Override
    public void register() {
        PlayState.getInstance().attach(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void update() {
        Log.d("PlayingBarFragment", "update: " + PlayState.getInstance().isPlaying());
        PlayState playState = PlayState.getInstance();
        if (playState.getPlayingSong() != null) {
            titleTextView.setText(playState.getPlayingSong().getTitle());
            artistTextView.setText("  -  " + playState.getPlayingSong().getArtist());
        } else {
            titleTextView.setText("暂无播放中歌曲");
            artistTextView.setText("");
        }
        if (!playState.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.play_button);
        } else {
            playPauseButton.setImageResource(R.drawable.pause_button);
        }
        updateSongImage();
    }

    private void updateSongImage() {
        if (PlayState.getInstance().getPlayingSong() == null) {
            // 使用默认图片
            this.songImage.setImageResource(R.drawable.album_image);
            return;
        }
        String musicFilePath = PlayState.getInstance().getPlayingSong().getFilePath();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(musicFilePath);

        byte[] albumArtBytes = retriever.getEmbeddedPicture();
        Bitmap albumArt;

        if (albumArtBytes != null && albumArtBytes.length > 0) {
            Log.d("albumArtBytes", "使用专辑图片");
            albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
            this.songImage.setImageBitmap(albumArt);
        } else {
            Log.d("albumArtBytes", "使用默认图片");
            this.songImage.setImageResource(R.drawable.album_image);
        }

        Log.d("musicPath", "updateSongImage: " + musicFilePath);
    }
}
