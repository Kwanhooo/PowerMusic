package indi.kwanho.pm.persisitance;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import indi.kwanho.pm.persisitance.converter.DateConverter;
import indi.kwanho.pm.persisitance.dao.FavoriteDao;
import indi.kwanho.pm.persisitance.dao.PlayRecordDao;
import indi.kwanho.pm.persisitance.dao.PlaylistItemRecordDao;
import indi.kwanho.pm.persisitance.dao.PlaylistRecordDao;
import indi.kwanho.pm.persisitance.domain.FavoriteRecord;
import indi.kwanho.pm.persisitance.domain.PlayRecord;
import indi.kwanho.pm.persisitance.domain.PlaylistItemRecord;
import indi.kwanho.pm.persisitance.domain.PlaylistRecord;

@Database(entities =
        {
                PlayRecord.class,
                FavoriteRecord.class,
                PlaylistRecord.class,
                PlaylistItemRecord.class
        },
        version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlayRecordDao playRecordDao();

    public abstract FavoriteDao favoriteDao();

    public abstract PlaylistRecordDao playlistRecordDao();

    public abstract PlaylistItemRecordDao playlistItemRecordDao();
}
