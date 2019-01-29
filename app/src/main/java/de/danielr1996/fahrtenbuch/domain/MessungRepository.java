package de.danielr1996.fahrtenbuch.domain;

import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface MessungRepository {
    Flowable<List<Messung>> getAll();

    Single<List<Messung>> getAllAsSingle();

    List<Long> insertAll(Messung... messungen);

    int delete(Messung messung);

    void deleteAll();
}
