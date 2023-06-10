package indi.kwanho.pm.fragment.local;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.local.AlbumAdapter;
import indi.kwanho.pm.entity.Album;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.store.LocalMusicState;

public class AlbumFragment extends Fragment {
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_album, container, false);
        getActualElements(view);
        setUpAdapter();
        setUpListeners();
        return view;
    }

    private void setUpAdapter() {
        List<Album> albums = LocalMusicState.getInstance().getAlbums();
        AlbumAdapter albumAdapter = new AlbumAdapter(albums);
        recyclerView.setAdapter(albumAdapter);
        // 设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

    }

    private void setUpListeners() {
    }

    private void getActualElements(View view) {
        recyclerView = view.findViewById(R.id.fragment_local_album_recycler_view);
    }
}
