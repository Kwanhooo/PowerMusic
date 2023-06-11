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
import indi.kwanho.pm.entity.Singer;
import indi.kwanho.pm.store.LocalMusicState;
import indi.kwanho.pm.utils.LocalMusicUtil;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {
    private List<Singer> singers;

    public SingerAdapter(List<Singer> singers) {
        this.singers = singers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Singer singer = singers.get(position);
        holder.singerNameTextView.setText(singer.getName());
        holder.orderTextView.setText(String.valueOf(position + 1));
        // 其他绑定数据到视图的操作
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemIndex = holder.getLayoutPosition();
                Log.d("SingerAdapter", "onClick: " + itemIndex);
                Singer targetSinger = LocalMusicState.getInstance().getSingers().get(itemIndex);
                LocalMusicUtil.getSongsBySinger(targetSinger);
                // 跳转到DetailActivity
                Intent intent = new Intent(v.getContext(), LocalDetailActivity.class);
                intent.putExtra("pageTitle", targetSinger.getName());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return singers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView singerNameTextView;
        TextView orderTextView;
        // 其他视图组件

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singerNameTextView = itemView.findViewById(R.id.singerNameTextView);
            orderTextView = itemView.findViewById(R.id.item_singer_order);
            // 初始化其他视图组件
        }
    }
}
