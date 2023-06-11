package indi.kwanho.pm.fragment.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.general.PlaylistAdapter;
import indi.kwanho.pm.component.WrapContentRecyclerView;
import indi.kwanho.pm.entity.PlaylistItem;

public class PlaylistFragment extends Fragment {
    private WrapContentRecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        getActualElements(view);
        initRecyclerView();
        setUpListeners();
        return view;
    }

    private void getActualElements(View view) {
        recyclerView = view.findViewById(R.id.fragment_playlist_rv);
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // 创建歌单项数据列表
        List<PlaylistItem> playlistItems = new ArrayList<>();
        playlistItems.add(new PlaylistItem("歌单1", "10首"));
        playlistItems.add(new PlaylistItem("歌单2", "20首"));
        playlistItems.add(new PlaylistItem("歌单3", "15首"));
        playlistItems.add(new PlaylistItem("歌单4", "15首"));
        playlistItems.add(new PlaylistItem("歌单5", "15首"));
        playlistItems.add(new PlaylistItem("歌单6", "15首"));
        playlistItems.add(new PlaylistItem("歌单7", "15首"));
        playlistItems.add(new PlaylistItem("歌单8", "15首"));

        // 创建适配器并设置给 RecyclerView
        playlistAdapter = new PlaylistAdapter(playlistItems);
        recyclerView.setAdapter(playlistAdapter);
    }

    private void setUpListeners() {
    }
}
