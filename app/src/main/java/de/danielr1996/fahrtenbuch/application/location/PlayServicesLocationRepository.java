package de.danielr1996.fahrtenbuch.application.location;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.danielr1996.fahrtenbuch.domain.LocationRepository;
import de.danielr1996.fahrtenbuch.domain.Messung;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class PlayServicesLocationRepository implements LocationRepository {
    private FusedLocationProviderClient client;
    private LocationCallback listener = new PlayServicesLocationListner();
    private LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)
            .setFastestInterval(1000);
    private Subject<Messung> messungSubject = PublishSubject.create();
    private Subject<List<Messung>> messungenSubject = PublishSubject.create();

    public PlayServicesLocationRepository(Context context) {
        this.client = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public Observable<Messung> getMessung() {
        return messungSubject
                .doOnDispose(() -> {
                    Log.i(PlayServicesLocationRepository.class.getName(), "getMessung.doOnDispose");
                    client.removeLocationUpdates(listener);
                })
                .doOnSubscribe(noop -> {
                    Log.i(PlayServicesLocationRepository.class.getName(), "getMessung.doOnSubscribe");
                    // FIXME: Berechtigungen überprüfen
                    client.requestLocationUpdates(locationRequest, listener, null);
                });
    }

    @Override
    public Observable<List<Messung>> getMessungen() {
        return messungenSubject.doOnSubscribe(noop -> Log.i(PlayServicesLocationRepository.class.getName(), "getMessungen.doOnSubscribe"));
    }

    protected class PlayServicesLocationListner extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            messungenSubject.onNext(locationResult.getLocations().stream().map(Locations::toMessung).collect(Collectors.toList()));
            messungSubject.onNext(Stream.of(locationResult.getLastLocation()).map(Locations::toMessung).findFirst().get());
        }

    }
}
