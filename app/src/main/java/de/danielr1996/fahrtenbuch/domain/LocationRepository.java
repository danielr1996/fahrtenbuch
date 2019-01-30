package de.danielr1996.fahrtenbuch.domain;

import java.util.List;

import io.reactivex.Flowable;

public interface LocationRepository {
    Flowable<Messung> getMessung();
    Flowable<List<Messung>> getMessungen();
}
