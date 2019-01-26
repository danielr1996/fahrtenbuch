package de.danielr1996.fahrtenbuch.storage.geojson;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.danielr1996.fahrtenbuch.storage.Messung;
import de.danielr1996.fahrtenbuch.storage.Messungen;

public class GeoJsonExporter {
    public static boolean saveToDisk(FeatureCollection featureCollection, Activity activity) {
        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/fahrtenbuch");
        dir.mkdirs();
        File file = new File(dir, "fahrtenbuch.json");

        try {
            FileOutputStream f = new FileOutputStream(file);
            ObjectMapper om = new ObjectMapper();
            om.writeValue(f, featureCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean saveToDisk(List<Messung> messungen, Activity activity) {
        FeatureCollection featureCollection = new FeatureCollection();
        messungen.stream()
                .map(messung -> {
                    Feature feature = new Feature();
                    feature.setProperty("date", messung.dateTime);
                    Point p = new Point();
                    LngLatAlt coord = new LngLatAlt();
                    coord.setLatitude(messung.latitude);
                    coord.setLongitude(messung.longitude);
                    p.setCoordinates(coord);
                    feature.setGeometry(p);
                    return feature;
                })
                .forEach(featureCollection::add);
        return GeoJsonExporter.saveToDisk(featureCollection, activity);
    }
}