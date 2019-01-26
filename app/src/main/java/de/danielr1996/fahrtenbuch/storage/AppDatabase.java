package de.danielr1996.fahrtenbuch.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Messung.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract MessungDao messungDao();
}
