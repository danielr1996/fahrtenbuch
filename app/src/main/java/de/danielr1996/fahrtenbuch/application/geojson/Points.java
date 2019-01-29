package de.danielr1996.fahrtenbuch.application.geojson;

import org.geojson.LngLatAlt;
import org.geojson.Point;

import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.Punkt;

public class Points {
    public static Punkt toPunkt(Point point) {
        return Punkt.builder()
                .latitude(point.getCoordinates().getLatitude())
                .altitude(point.getCoordinates().getAltitude())
                .longitude(point.getCoordinates().getLongitude())
                .build();
    }

    public static Point fromPunkt(Punkt punkt){
        Point point = new Point();
        LngLatAlt coord = new LngLatAlt();
        coord.setLongitude(punkt.getLongitude());
        coord.setLatitude(punkt.getLatitude());
        coord.setAltitude(punkt.getAltitude());
        point.setCoordinates(coord);
        return point;
    }
}
