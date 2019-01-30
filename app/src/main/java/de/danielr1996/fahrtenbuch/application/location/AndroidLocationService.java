package de.danielr1996.fahrtenbuch.application.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.function.Consumer;

import androidx.core.content.ContextCompat;
import de.danielr1996.fahrtenbuch.domain.Punkt;
@Deprecated
public class AndroidLocationService extends AbstractLocationService {
    private LocationManager locationManager;
    private Activity activity;
    private LocationListener locationListener;

    public AndroidLocationService(Activity activity) {
        this.activity = activity;
        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void start() {
        super.start();
        ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        this.locationListener = new AndroidLocationListener(this.callback);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void stop() {
        super.stop();
        locationManager.removeUpdates(locationListener);
    }



    protected class AndroidLocationListener implements LocationListener {
        private Consumer<Punkt> callback;

        public AndroidLocationListener(Consumer<Punkt> callback){
            this.callback = callback;
        }

        public void onLocationChanged(Location location) {
            callback.accept(Punkt.builder().latitude(location.getLatitude()).longitude(location.getLongitude()).altitude(location.getAltitude()).build());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }
}
