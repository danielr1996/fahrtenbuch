package de.danielr1996.fahrtenbuch.storage;

import java.time.LocalDateTime;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Messung {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo()
    public double latitude;

    @ColumnInfo()
    public double longitude;

    @ColumnInfo()
    public String dateTime;
}