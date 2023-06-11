package indi.kwanho.pm.adapter.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.entity.PlaylistItem;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<PlaylistItem> playlistItems;

    public PlaylistAdapter(List<PlaylistItem> playlistItems) {
        this.playlistItems = playlistItems;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem playlistItem = playlistItems.get(position);
        holder.bind(playlistItem);
    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView countTextView;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.play_list_item_title);
            countTextView = itemView.findViewById(R.id.play_list_item_count);
        }

        public void bind(PlaylistItem playlistItem) {
            titleTextView.setText(playlistItem.getTitle());
            countTextView.setText(playlistItem.getCount());
            itemView.setOnClickListener(v -> {
                // 跳入歌单详情页
            });
        }
    }
}

