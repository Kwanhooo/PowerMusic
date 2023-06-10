package indi.kwanho.pm.entity;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Folder {
    private String name;
    private String path;

    public Folder(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return Objects.equals(name, folder.name) && Objects.equals(path, folder.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}
