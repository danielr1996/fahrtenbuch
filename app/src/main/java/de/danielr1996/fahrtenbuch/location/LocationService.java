package de.danielr1996.fahrtenbuch.location;

import java.util.function.Consumer;

import de.danielr1996.fahrtenbuch.model.Point;

public interface LocationService {
    LocationService registerCallback(Consumer<Point> callback);

    LocationService registerCallbackActive(Consumer<Boolean> callback);

    void start();

    void stop();
}
