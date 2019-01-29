package de.danielr1996.fahrtenbuch.application.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MessungDBO.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessungDao messungDao();
}
