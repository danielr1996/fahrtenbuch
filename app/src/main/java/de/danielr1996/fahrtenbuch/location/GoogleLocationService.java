package de.danielr1996.fahrtenbuch.location;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import de.danielr1996.fahrtenbuch.MainActivity;
import de.danielr1996.fahrtenbuch.model.Point;

public class GoogleLocationService extends AbstractLocationService {
    private FusedLocationProviderClient client;
    private Activity activity;
    private LocationCallback listener;
    private LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)
            .setFastestInterval(1000);

    public GoogleLocationService(Activity activity) {
        this.client = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;
    }

    @Override
    public void start() {
        super.start();
        listener = new GoogleLocationListener(callback);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
            task.addOnSuccessListener(activity, res -> {
                client.requestLocationUpdates(locationRequest, listener, null);
            });
            task.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity,
                                    1);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                            System.out.println("Another error");
                        }
                    }
                }
            });
//            client.getLastLocation().addOnSuccessListener(new GoogleLocationListener(callback));
        }
    }

    @Override
    public void stop() {
        super.stop();
        client.removeLocationUpdates(listener);
    }

    protected class GoogleLocationListener extends LocationCallback implements OnSuccessListener<Location> {
        private Consumer<Point> callback;

        public GoogleLocationListener(Consumer<Point> callback) {
            this.callback = callback;
        }


        @Override
        public void onSuccess(Location location) {
            callback.accept(new Point().setLatitude(location.getLatitude()).setLongitude(location.getLongitude()));
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            callback.accept(new Point().setLatitude(locationResult.getLastLocation().getLatitude()).setLongitude(locationResult.getLastLocation().getLongitude()));
        }

        ;
    }


}
