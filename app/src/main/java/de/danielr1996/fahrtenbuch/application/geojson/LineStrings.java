package de.danielr1996.fahrtenbuch.application.geojson;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LineString;
import org.geojson.Point;

public class LineStrings {
    public static LineString fromFeatureCollection(FeatureCollection featureCollection){
        LineString lineString = new LineString();
        featureCollection.getFeatures().stream()
                .map(Feature::getGeometry)
                .map(Points::fromGeometry)
                .map(Point::getCoordinates)
                .forEach(lineString::add);
        return lineString;
    }
}
