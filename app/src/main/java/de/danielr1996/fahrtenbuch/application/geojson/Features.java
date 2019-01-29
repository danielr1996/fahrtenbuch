package de.danielr1996.fahrtenbuch.application.geojson;


import org.geojson.Feature;
import org.geojson.Point;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.danielr1996.fahrtenbuch.domain.Messung;

public class Features {
    public static final String KEY_TIMESTAMP = "date";

    public static Messung toMessung(Feature feature) {
        return Messung.builder()
                .punkt(Points.toPunkt((Point) feature.getGeometry()))
                .timestamp(LocalDateTime.parse(feature.getProperty(KEY_TIMESTAMP), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public static Feature fromMessung(Messung messung) {
        Feature feature = new Feature();
        feature.setGeometry(Points.fromPunkt(messung.getPunkt()));
        feature.setProperty(KEY_TIMESTAMP, messung.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return feature;
    }
}
