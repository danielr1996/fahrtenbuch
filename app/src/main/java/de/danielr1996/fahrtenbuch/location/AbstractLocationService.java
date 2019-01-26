package de.danielr1996.fahrtenbuch.location;

import java.util.function.Consumer;

import de.danielr1996.fahrtenbuch.model.Point;

public abstract class AbstractLocationService implements LocationService{
    protected Consumer<Point> callback;
    protected Consumer<Boolean> callbackActive;

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
