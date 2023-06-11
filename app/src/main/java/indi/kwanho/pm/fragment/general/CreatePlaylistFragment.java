package indi.kwanho.pm.fragment.general;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import indi.kwanho.pm.R;
import indi.kwanho.pm.persisitance.domain.PlaylistRecord;
import indi.kwanho.pm.persisitance.repository.PlaylistRecordRepository;
import indi.kwanho.pm.store.PlaylistState;

public class CreatePlaylistFragment extends Fragment {

    private LinearLayout createPlayListFragmentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_play_list, container, false);
        getActualElements(view);
        setUpListeners();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void getActualElements(View view) {
        this.createPlayListFragmentLayout = view.findViewById(R.id.create_play_list_fragment);
    }

    private void setUpListeners() {
        createPlayListFragmentLayout.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    private void showCreatePlaylistDialog() {
        // 创建弹窗视图
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_playlist, null);

        // 获取文本输入框和确定按钮
        EditText playlistNameEditText = dialogView.findViewById(R.id.playlistNameEditText);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // 创建弹窗
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setView(dialogView);
        AlertDialog createPlaylistDialog = dialogBuilder.create();

        // 设置确定按钮的点击事件
        confirmButton.setOnClickListener(v -> {
            String playlistName = playlistNameEditText.getText().toString();
            // 执行相应的处理逻辑，例如创建新的歌单
            createPlaylist(playlistName);

            // 关闭弹窗
            createPlaylistDialog.dismiss();
        });

        // 显示弹窗
        createPlaylistDialog.show();
    }

    private void createPlaylist(String playlistName) {
        Log.d("CreatePlaylistFragment", "createPlaylist: " + playlistName);
        PlaylistRecord playlistRecord = new PlaylistRecord();
        playlistRecord.setTitle(playlistName);
        playlistRecord.setCount(0);
        PlaylistRecordRepository repository = new PlaylistRecordRepository(requireContext());
        repository.insertPlayRecord(playlistRecord);
        Toast.makeText(requireContext(), playlistName + " 创建成功", Toast.LENGTH_SHORT).show();

        loadNewDataToPlaylistState();
    }

    private void loadNewDataToPlaylistState() {
        PlaylistRecordRepository repository = new PlaylistRecordRepository(requireContext());
        repository.getAllPlaylistRecords().observe(getViewLifecycleOwner(), playlistRecords -> {
            PlaylistState.getInstance().setPlaylistRecords(playlistRecords);
        });
    }

}
