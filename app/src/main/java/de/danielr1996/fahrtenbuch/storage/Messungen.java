package de.danielr1996.fahrtenbuch.storage;

import de.danielr1996.fahrtenbuch.model.Point;
import de.danielr1996.fahrtenbuch.storage.Messung;

public class Messungen {
    public static Point toPoint(Messung messung){
        return new Point().setLatitude(messung.latitude).setLongitude(messung.longitude);
    }
}
