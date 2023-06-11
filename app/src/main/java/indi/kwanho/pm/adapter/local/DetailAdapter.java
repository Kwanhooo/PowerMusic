package indi.kwanho.pm.adapter.local;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.service.MusicPlayerService;
import indi.kwanho.pm.store.PlayState;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private List<Song> songs;

    public DetailAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());
        holder.orderTextView.setText(String.valueOf(position + 1));
        // 其他绑定数据到视图的操作
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerService musicPlayerService = MusicPlayerManager.getInstance().getMusicPlayerService();
                if (musicPlayerService != null) {
                    // 在这里调用音乐播放服务进行播放
                    Log.d("song", "song" + song.toString());
                    PlayState.getInstance().setPlayingSong(song);
                    PlayState.getInstance().setPlaying(true);
                    PlayState.getInstance().setPlayingList(songs);
                    PlayState.getInstance().setPlayingIndex(position);
                    musicPlayerService.playSong(song.getFilePath());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        TextView orderTextView;
        // 其他视图组件

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            orderTextView = itemView.findViewById(R.id.item_music_order);
        }
    }
}

