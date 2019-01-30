package de.danielr1996.fahrtenbuch.application.location;

import android.location.Location;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.Punkt;

public class Locations {

    public static Messung toMessung(Location location){
        return Messung.builder()
                .punkt(Punkt.builder()
                        .altitude(location.getAltitude())
                        .longitude(location.getLongitude())
                        .latitude(location.getLatitude())
                        .build())
                .timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(location.getTime()), TimeZone.getDefault().toZoneId()))
                .build();
    }
}
