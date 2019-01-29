package de.danielr1996.fahrtenbuch.application.geojson;

import org.geojson.FeatureCollection;

import java.util.List;

import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.Messungen;

public class FeatureCollections {
    public static FeatureCollection fromMessungen(List<Messung> messungen){
        FeatureCollection featureCollection = new FeatureCollection();
        messungen
                .stream()
                .map(Features::fromMessung)
                .forEach(featureCollection::add);
        return featureCollection;
    }
}
