package de.danielr1996.fahrtenbuch.model;

public class Point {
    private double longitude;
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public Point setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Point setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double distanceInKm(Point that) {
        double lat2 = this.latitude;
        double lon2 = this.longitude;
        double lat1 = that.latitude;
        double lon1 = that.longitude;
        int radius = 6371;

        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2- lon1);

        double a = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;

        return Math.abs(d);
    }

    public static Point fromGeoJsonPoint(org.geojson.Point point){
        Point p = new Point();
        p.setLongitude(point.getCoordinates().getLongitude());
        p.setLatitude(point.getCoordinates().getLatitude());
        return p;
    }
}
