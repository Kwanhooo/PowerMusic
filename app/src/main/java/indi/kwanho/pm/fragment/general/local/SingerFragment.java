package indi.kwanho.pm.fragment.general.local;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import indi.kwanho.pm.R;

public class SingerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_singer, container, false);
        getActualElements();
        setUpListeners();
        return view;
    }

    private void setUpListeners() {
    }

    private void getActualElements() {
    }
}
