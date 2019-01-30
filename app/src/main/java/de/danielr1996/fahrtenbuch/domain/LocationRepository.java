package de.danielr1996.fahrtenbuch.domain;

import java.util.List;

import io.reactivex.Observable;

public interface LocationRepository {
    Observable<Messung> getMessung();
    Observable<List<Messung>> getMessungen();
}
