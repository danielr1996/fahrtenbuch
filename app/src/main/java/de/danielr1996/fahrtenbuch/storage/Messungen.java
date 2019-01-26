package de.danielr1996.fahrtenbuch.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.danielr1996.fahrtenbuch.R;
import de.danielr1996.fahrtenbuch.model.Point;
import de.danielr1996.fahrtenbuch.storage.Messung;
import one.util.streamex.StreamEx;

public class Messungen {
    public static Point toPoint(Messung messung) {
        return new Point().setLatitude(messung.latitude).setLongitude(messung.longitude);
    }

    public static Messung fromPoint(Point point, LocalDateTime time){
        Messung messung = new Messung();
        messung.dateTime = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        messung.longitude = point.getLongitude();
        messung.latitude = point.getLatitude();

        return messung;
    }

    public static double length(List<Messung> messungen) {
        double strecke = StreamEx.of(messungen.stream())
                .map(Messungen::toPoint)
                .pairMap(Point::distanceInKm)
                .reduce((d1, d2) -> d1 + d2)
                .orElse(0.0);
        return strecke;
    }
}
