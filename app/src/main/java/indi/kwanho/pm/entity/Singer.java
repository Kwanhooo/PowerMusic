package indi.kwanho.pm.entity;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Singer {
    private String name;

    public Singer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "Singer{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Singer singer = (Singer) o;
        return Objects.equals(name, singer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
