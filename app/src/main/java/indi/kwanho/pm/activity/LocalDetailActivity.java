package indi.kwanho.pm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import indi.kwanho.pm.R;
import indi.kwanho.pm.adapter.local.DetailAdapter;
import indi.kwanho.pm.fragment.general.PlayingBarFragment;
import indi.kwanho.pm.store.LocalMusicState;

public class LocalDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView pageTitleTextView;
    private DetailAdapter detailAdapter;
    private FrameLayout frameLayout;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_detail);

        getActualElements();
        setUpPageTitle();
        setUpListeners();
        setUpAdapter();
        wiredWidgets();
    }

    private void wiredWidgets() {
        // 创建PlayingBarFragment的实例
        PlayingBarFragment playingBarFragment = new PlayingBarFragment();

        // 将PlayingBarFragment添加到Fragment容器
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_detail_activity, playingBarFragment)
                .commit();
    }

    private void setUpListeners() {
        this.backBtn.setOnClickListener(v -> {
            finish();
        });
        this.frameLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayControlActivity.class);
            startActivity(intent);
        });
    }

    private void setUpPageTitle() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pageTitle")) {
            pageTitleTextView.setText(intent.getStringExtra("pageTitle"));
        }
    }

    private void setUpAdapter() {
        detailAdapter = new DetailAdapter(LocalMusicState.getInstance().getDetails());
        recyclerView.setAdapter(detailAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getActualElements() {
        recyclerView = findViewById(R.id.activity_local_detail_rv);
        pageTitleTextView = findViewById(R.id.detail_text_view_title_local_music);
        backBtn = findViewById(R.id.detail_image_button_back_local_music);
        frameLayout = findViewById(R.id.fragment_container_detail_activity);
    }
}

