package indi.kwanho.pm.fragment.general;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.general.PlaylistAdapter;
import indi.kwanho.pm.common.PowerObserver;
import indi.kwanho.pm.component.WrapContentRecyclerView;
import indi.kwanho.pm.persisitance.domain.PlaylistRecord;
import indi.kwanho.pm.persisitance.repository.PlaylistRecordRepository;
import indi.kwanho.pm.store.PlaylistState;

public class PlaylistFragment extends Fragment implements PowerObserver {
    private WrapContentRecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        getActualElements(view);
        register();
        initData();
        setUpListeners();
        return view;
    }

    private void initData() {
        PlaylistRecordRepository playlistRecordRepository = new PlaylistRecordRepository(getActivity());
        playlistRecordRepository.getAllPlaylistRecords().observe(getViewLifecycleOwner(), playlistRecords -> {
            // 清空全局缓存歌单
            PlaylistState.getInstance().getPlaylistRecords().clear();
            playlistRecords.forEach(playlistRecord -> {
                // 将数据库中的歌单记录添加到全局缓存歌单
                PlaylistState.getInstance().getPlaylistRecords().add(playlistRecord);
            });
            // 完毕后初始化 RecyclerView
            initRecyclerView();
        });
    }

    private void getActualElements(View view) {
        recyclerView = view.findViewById(R.id.fragment_playlist_rv);
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // 创建适配器并设置给 RecyclerView
        playlistAdapter = new PlaylistAdapter(PlaylistState.getInstance().getPlaylistRecords());
        recyclerView.setAdapter(playlistAdapter);
    }

    private void setUpListeners() {
    }

    @Override
    public void register() {
        PlaylistState.getInstance().attach(this);
    }

    @Override
    public void update() {
        for (PlaylistRecord playlistRecord : PlaylistState.getInstance().getPlaylistRecords()) {
            Log.d("PlaylistFragment", playlistRecord.getTitle());
        }
        playlistAdapter.updateData(PlaylistState.getInstance().getPlaylistRecords());
    }

}
