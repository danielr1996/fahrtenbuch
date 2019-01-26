package de.danielr1996.fahrtenbuch.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.function.Consumer;

import androidx.core.content.ContextCompat;
import de.danielr1996.fahrtenbuch.model.Point;

public class AndroidLocationService implements LocationService {
    private LocationManager locationManager;
    private Activity activity;
    private Consumer<Point> callback;
    private LocationListener locationListener;

    public AndroidLocationService(Activity activity) {
        this.activity = activity;
        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void registerCallback(Consumer<Point> callback) {
        this.callback = callback;

    }

    @Override
    public void start() {
        ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        this.locationListener = new AndroidLocationListener(this.callback);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void stop() {
        locationManager.removeUpdates(locationListener);

    }

    class AndroidLocationListener implements LocationListener{
        private Consumer<Point> callback;

        public AndroidLocationListener(Consumer<Point> callback){
            this.callback = callback;
        }

        public void onLocationChanged(Location location) {
            callback.accept(new Point().setLatitude(location.getLatitude()).setLongitude(location.getLongitude()));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }
}
