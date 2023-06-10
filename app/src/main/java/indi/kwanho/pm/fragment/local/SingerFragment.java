package indi.kwanho.pm.fragment.local;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.local.SingerAdapter;
import indi.kwanho.pm.entity.Singer;
import indi.kwanho.pm.store.LocalMusicState;

public class SingerFragment extends Fragment {
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_singer, container, false);
        getActualElements(view);
        setUpAdapter();
        setUpListeners();
        return view;
    }

    private void setUpAdapter() {
        List<Singer> singers = LocalMusicState.getInstance().getSingers();
        SingerAdapter singerAdapter = new SingerAdapter(singers);
        recyclerView.setAdapter(singerAdapter);
        // 设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setUpListeners() {
    }

    private void getActualElements(View view) {
        recyclerView = view.findViewById(R.id.fragment_local_singer_recycler_view);
    }
}
