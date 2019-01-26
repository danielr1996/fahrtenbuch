package de.danielr1996.fahrtenbuch.location;

import java.util.function.Consumer;

import de.danielr1996.fahrtenbuch.model.Point;

public interface LocationService {
    void registerCallback(Consumer<Point> callback);

    void start();

    void stop();
}
