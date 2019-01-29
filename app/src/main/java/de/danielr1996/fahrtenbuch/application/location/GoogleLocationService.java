package de.danielr1996.fahrtenbuch.application.location;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
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
import de.danielr1996.fahrtenbuch.domain.Punkt;

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
        private Consumer<Punkt> callback;

        public GoogleLocationListener(Consumer<Punkt> callback) {
            this.callback = callback;
        }


        @Override
        public void onSuccess(Location location) {
            callback.accept(Punkt.builder().latitude(location.getLatitude()).longitude(location.getLongitude()).altitude(location.getAltitude()).build());
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            callback.accept(Punkt.builder().latitude(locationResult.getLastLocation().getLatitude()).longitude(locationResult.getLastLocation().getLongitude()).altitude(locationResult.getLastLocation().getAltitude()).build());
        }

        ;
    }


}
