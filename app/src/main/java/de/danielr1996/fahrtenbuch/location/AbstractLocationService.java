package de.danielr1996.fahrtenbuch.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.function.Consumer;

import de.danielr1996.fahrtenbuch.model.Point;

public abstract class AbstractLocationService implements LocationService{
    protected Consumer<Point> callback = point -> {};
    protected Consumer<Boolean> callbackActive = active->{};

    @Override
    public LocationService registerCallback(Consumer<Point> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public LocationService registerCallbackActive(Consumer<Boolean> callback){
        this.callbackActive = callback;
        return this;
    }

    @Override
    public void start() {
        this.callbackActive.accept(true);
    }

    @Override
    public void stop() {
        this.callbackActive.accept(false);
    }
}
