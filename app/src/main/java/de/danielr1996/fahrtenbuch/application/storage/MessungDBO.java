package de.danielr1996.fahrtenbuch.application.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.Punkt;

@Entity
public class MessungDBO {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo()
    public double latitude;

    @ColumnInfo()
    public double longitude;


    @ColumnInfo()
    public double altitude;

    @ColumnInfo()
    public String dateTime;

    public static MessungDBO fromMessung(Messung messung) {
        MessungDBO dbo = new MessungDBO();
        dbo.altitude = messung.getPunkt().getAltitude();
        dbo.latitude = messung.getPunkt().getLatitude();
        dbo.longitude = messung.getPunkt().getLongitude();
        dbo.dateTime = messung.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dbo;
    }

    public Messung toMessung() {
        return Messung.builder()
                .punkt(Punkt.builder()
                        .latitude(latitude)
                        .altitude(altitude)
                        .longitude(longitude)
                        .build())
                .timestamp(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}