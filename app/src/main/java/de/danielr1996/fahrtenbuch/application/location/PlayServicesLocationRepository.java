package de.danielr1996.fahrtenbuch.application.location;

import android.content.Context;

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
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;
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
    private Subject<Boolean> activeSubject = BehaviorSubject.create();

    public PlayServicesLocationRepository(Context context) {
        this.client = LocationServices.getFusedLocationProviderClient(context);
        activeSubject.onNext(false);
        activeSubject.doOnSubscribe(System.out::println);
        messungenSubject.doOnSubscribe(System.out::println);
    }

    public void start() throws SecurityException{
        client.requestLocationUpdates(locationRequest, listener, null);
        activeSubject.onNext(true);
    }

    public void stop() {
        client.removeLocationUpdates(listener);
        activeSubject.onNext(false);
    }

    public Flowable<Boolean> isActive(){
        return activeSubject.toFlowable(BackpressureStrategy.LATEST);
    }


    @Override
    public Flowable<Messung> getMessung(){
        return messungSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Messung>> getMessungen() {
        return messungenSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    protected class PlayServicesLocationListner extends LocationCallback{

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
