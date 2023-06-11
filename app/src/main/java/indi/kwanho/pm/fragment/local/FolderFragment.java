package indi.kwanho.pm.fragment.local;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.R;
import indi.kwanho.pm.entity.Song;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.store.PlayState;

public class FolderFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> fileList;
    private String path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_folder, container, false);
        listView = view.findViewById(R.id.fragment_local_folder_list_view);
        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFile = fileList.get(position);
                // 判断是文件夹还是文件
                File clickedFile = new File(path + "/" + selectedFile);
                if (clickedFile.isDirectory()) {
                    openFolder(selectedFile);
                } else {
                    try {
                        handlePlaySong(path + "/" + selectedFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        if (hasStoragePermission()) {
            loadFiles(Environment.getExternalStorageDirectory().getPath());
        } else {
            requestStoragePermission();
        }

        return view;
    }

    private void handlePlaySong(String targetFile) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(targetFile);

        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        // 如果三个元数据有空的，就用文件名代替
        if (title == null || title.isEmpty()) {
            title = targetFile.substring(targetFile.lastIndexOf("/") + 1);
        }
        if (artist == null || artist.isEmpty()) {
            artist = "未知艺术家";
        }
        if (album == null || album.isEmpty()) {
            album = "未知专辑";
        }

        retriever.release();

        Song song = new Song(
                title, album, artist, targetFile
        );

        MusicPlayerManager.getInstance().getMusicPlayerService().playSong(song.getFilePath());
        PlayState.getInstance().setPlaying(true);
        PlayState.getInstance().setPlayingSong(song);
        PlayState.getInstance().setPlayingList(new ArrayList<>());
        PlayState.getInstance().getPlayingList().add(song);
        PlayState.getInstance().setPlayingIndex(0);
        PlayState.getInstance().setLastPlayingIndex(0);
    }


    private boolean hasStoragePermission() {
        int permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFiles(Environment.getExternalStorageDirectory().getPath());
            }
        }
    }

    private void loadFiles(String directoryPath) {
        this.path = directoryPath;
        fileList.clear();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() || isMusicFile(file)) {
                    fileList.add(file.getName());
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean isMusicFile(File file) {
        String mimeType = getMimeType(file.getPath());
        return mimeType != null && mimeType.startsWith("audio");
    }

    @SuppressLint("Range")
    private String getMimeType(String path) {
        String mimeType = null;
        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[]{MediaStore.Files.FileColumns.MIME_TYPE},
                    MediaStore.Files.FileColumns.DATA + "=?",
                    new String[]{path},
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return mimeType;
    }

    private void openFolder(String folderName) {
        String currentPath = Environment.getExternalStorageDirectory().getPath();
        String newPath = currentPath + "/" + folderName;
        loadFiles(newPath);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置返回键监听器
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("FolderFragment", "handleOnBackPressed: " + path);
                if (path.equals(Environment.getExternalStorageDirectory().getPath())) {
                    requireActivity().finish();
                } else {
                    File file = new File(path);
                    String parentPath = file.getParent();
                    Log.d("FolderFragment", "handleOnBackPressed: " + parentPath);
                    loadFiles(parentPath);
                }
            }
        });
    }
}

