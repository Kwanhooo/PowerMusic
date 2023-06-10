package indi.kwanho.pm.adapter.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import indi.kwanho.pm.fragment.local.AlbumFragment;
import indi.kwanho.pm.fragment.local.FolderFragment;
import indi.kwanho.pm.fragment.local.SingerFragment;
import indi.kwanho.pm.fragment.local.SingleSongFragment;

public class LocalTabAdapter extends FragmentPagerAdapter {
    public LocalTabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SingleSongFragment();
            case 1:
                return new AlbumFragment();
            case 2:
                return new SingerFragment();
            default:
                return new FolderFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "单曲";
            case 1:
                return "专辑";
            case 2:
                return "歌手";
            default:
                return "文件夹";
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

}