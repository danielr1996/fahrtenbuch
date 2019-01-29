package de.danielr1996.fahrtenbuch.application.storage;

import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.room.Room;
import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.MessungRepository;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class DatabaseMessungRepository implements MessungRepository {
    private MessungDao dao;

    public DatabaseMessungRepository(Context ctx) {
        dao = Room.databaseBuilder(ctx, AppDatabase.class, "users")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
                .messungDao();
    }

    @Override
    public Flowable<List<Messung>> getAll() {
        return dao.getAll().map(list -> list.stream()
                .map(MessungDBO::toMessung)
                .collect(Collectors.toList()));
    }

    @Override
    public Single<List<Messung>> getAllAsSingle() {
        return dao.getAllAsSingle().map(list -> list.stream()
                .map(MessungDBO::toMessung)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Long> insertAll(Messung... messungen) {
        return dao.insertAll(Stream
                .of(messungen)
                .map(MessungDBO::fromMessung)
                .collect(Collectors.toList())
                .toArray(new MessungDBO[]{}));
    }

    @Override
    public int delete(Messung messung) {
        return dao.delete(MessungDBO.fromMessung(messung));
    }

    @Override
    public void deleteAll() {
        dao.deleteAll();
    }
}
