package indi.kwanho.pm.adapter.local;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.activity.LocalDetailActivity;
import indi.kwanho.pm.entity.Album;
import indi.kwanho.pm.store.LocalMusicState;
import indi.kwanho.pm.utils.LocalMusicUtil;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private List<Album> albums;

    public AlbumAdapter(List<Album> albums) {
        this.albums = albums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.albumNameTextView.setText(album.getName());
        holder.albumArtistTextView.setText(album.getArtist());
        holder.orderTextView.setText(String.valueOf(position + 1));
        // 其他绑定数据到视图的操作
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemIndex = holder.getLayoutPosition();
                Log.d("AlbumAdapter", "onClick: " + itemIndex);
                Album targetAlbum = LocalMusicState.getInstance().getAlbums().get(itemIndex);
                LocalMusicUtil.getSongsByAlbum(targetAlbum);
                // 跳转到DetailActivity
                Intent intent = new Intent(v.getContext(), LocalDetailActivity.class);
                intent.putExtra("pageTitle", targetAlbum.getName());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumNameTextView;
        TextView albumArtistTextView;
        TextView orderTextView;
        // 其他视图组件

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameTextView = itemView.findViewById(R.id.albumNameTextView);
            albumArtistTextView = itemView.findViewById(R.id.album_artistTextView);
            orderTextView = itemView.findViewById(R.id.item_album_order);
            // 初始化其他视图组件
        }
    }
}
