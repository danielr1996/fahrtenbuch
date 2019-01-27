package de.danielr1996.fahrtenbuch.support;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.Feature;
import org.geojson.FeatureCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.danielr1996.fahrtenbuch.model.Point;
import one.util.streamex.StreamEx;

public class Util {
    public static FeatureCollection readFeatureCollection(File file) {
        try {
            return readFeatureCollection(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FeatureCollection readFeatureCollection(InputStream is) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(is, FeatureCollection.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFeatureCollection(FeatureCollection collection, OutputStream os) {
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(os, collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFeatureCollection(FeatureCollection collection, File file) {
        ObjectMapper om = new ObjectMapper();
        try {
            if (!file.exists()) file.createNewFile();
            om.writeValue(file, collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FeatureCollection reducePoints(FeatureCollection in, int every) {
        FeatureCollection out = new FeatureCollection();

        List<Feature> features = in.getFeatures();
        IntStream.range(0, features.size())
                .filter(n -> n % every == 0)
                .mapToObj(features::get)
                .forEach(out::add);
        return out;
    }

    public static List<Double> getSpeed(FeatureCollection featureCollection) {
        return StreamEx.of(featureCollection.getFeatures())
                .pairMap((p1, p2) -> {
                    LocalDateTime start = LocalDateTime.parse(p1.getProperty("date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    LocalDateTime end = LocalDateTime.parse(p2.getProperty("date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    long timeInSeconds = ChronoUnit.SECONDS.between(start, end);
                    double distanceInKm = Point.fromGeoJsonPoint((org.geojson.Point) p1.getGeometry()).distanceInKm(Point.fromGeoJsonPoint((org.geojson.Point) p2.getGeometry()));
                    double speedInKmS = distanceInKm / timeInSeconds;
                    double speedInKmH = speedInKmS * 3600;
                    return speedInKmH;
                }).collect(Collectors.toList());
    }

    public static String speedToCsv(List<Double> speeds) {
        Stream<Integer> intStream = IntStream.range(0, speeds.size()).mapToObj(Integer::new);
        Stream<Double> speedStream = speeds.stream();

        return
                StreamEx.of(intStream)
                        .zipWith(speedStream, (Integer index, Double speed) -> {
                            if (speed.isInfinite()) {
                                return index + ", " + -1;
                            }
                            return index + ", " + speed;
                        }).collect(Collectors.joining("\n"));
    }

    public static FeatureCollection getSampleFeatures(){
        FeatureCollection featureCollection = new FeatureCollection();
        org.geojson.Point point = new org.geojson.Point(11.254119873046875,
                49.496563034621225);
        org.geojson.Point point2 = new org.geojson.Point(11.206398010253906,
                49.48184374892171);
        Feature feature1 = new Feature();
        feature1.setProperty("date", LocalDateTime.of(2019, 1, 1, 12, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        feature1.setGeometry(point);

        Feature feature2 = new Feature();
        feature2.setProperty("date", LocalDateTime.of(2019, 1, 1, 12, 30, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        feature2.setGeometry(point2);
        featureCollection.add(feature1);
        featureCollection.add(feature2);
        return featureCollection;
    }

    public static void main(String[] args) {
        FeatureCollection featureCollection = readFeatureCollection(new File("C:\\Users\\Daniel\\Google Drive\\fahrtenbuch (1).json"));
//        writeFeatureCollection(reducePoints(featureCollection, 20), new File("C:\\Users\\Daniel\\Desktop\\featurecollection.json"));

    }
}
