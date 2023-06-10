package indi.kwanho.pm.fragment.local;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.local.MusicAdapter;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.store.LocalMusicState;

public class SingleSongFragment extends Fragment {
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_single, container, false);
        getActualElements(view);
        setUpAdapter();
        setUpListeners();
        return view;
    }

    private void setUpAdapter() {
        List<Song> songs = LocalMusicState.getInstance().getSongs();
        Log.d("SingleSongFragment", "setUpAdapter: " + songs.size());
        MusicAdapter musicAdapter = new MusicAdapter(songs);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setUpListeners() {
    }

    private void getActualElements(View view) {
        this.recyclerView = view.findViewById(R.id.fragment_single_song_recycler_view);
    }

}
