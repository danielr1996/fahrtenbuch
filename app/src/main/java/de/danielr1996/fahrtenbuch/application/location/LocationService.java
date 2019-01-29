package de.danielr1996.fahrtenbuch.application.location;

import java.util.function.Consumer;

import de.danielr1996.fahrtenbuch.domain.Punkt;


public interface LocationService {
    LocationService registerCallback(Consumer<Punkt> callback);

    LocationService registerCallbackActive(Consumer<Boolean> callback);

    void start();

    void stop();
}
