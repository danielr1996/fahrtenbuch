package de.danielr1996.fahrtenbuch.application.location;

import java.util.function.Consumer;

import de.danielr1996.fahrtenbuch.domain.Punkt;


public abstract class AbstractLocationService implements LocationService{
    protected Consumer<Punkt> callback = point -> {};
    protected Consumer<Boolean> callbackActive = active->{};

    @Override
    public LocationService registerCallback(Consumer<Punkt> callback) {
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
