package indi.kwanho.pm.entity;

import java.util.Objects;

@Deprecated
public class PlaylistItem {
    private String title;
    private String count;

    public PlaylistItem(String title, String count) {
        this.title = title;
        this.count = count;
    }

    public PlaylistItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistItem that = (PlaylistItem) o;
        return Objects.equals(title, that.title) && Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, count);
    }
}

