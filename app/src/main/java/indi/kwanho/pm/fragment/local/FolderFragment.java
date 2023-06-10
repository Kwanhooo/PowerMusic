package indi.kwanho.pm.fragment.local;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import indi.kwanho.pm.R;

public class FolderFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> fileList;

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
                // 如果点击的文件有后缀名
                openFolder(selectedFile);
            }
        });

        if (hasStoragePermission()) {
            loadFiles(Environment.getExternalStorageDirectory().getPath());
        } else {
            requestStoragePermission();
        }

        return view;
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
        // 是否以.mp3结尾
        return file.getName().endsWith(".mp3") || file.getName().endsWith(".flac");
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
}

