package de.danielr1996.fahrtenbuch.application.geojson;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import org.geojson.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.danielr1996.fahrtenbuch.application.storage.MessungDBO;

public class GeoJsonExporter {
    public static void saveToDisk(FeatureCollection featureCollection, Activity activity) throws IOException {
        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/fahrtenbuch");
        dir.mkdirs();
        File file = new File(dir, "fahrtenbuch.json");

            FileOutputStream f = new FileOutputStream(file);
            ObjectMapper om = new ObjectMapper();
            om.writeValue(f, featureCollection);
    }
}
