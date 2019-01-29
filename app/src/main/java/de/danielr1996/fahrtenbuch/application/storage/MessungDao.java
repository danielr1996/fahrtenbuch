package de.danielr1996.fahrtenbuch.application.storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MessungDao {
    @Query("SELECT * FROM MessungDBO")
    Flowable<List<MessungDBO>> getAll();


    @Query("SELECT * FROM MessungDBO")
    Single<List<MessungDBO>> getAllAsSingle();

    @Query("DELETE FROM MessungDBO")
    void deleteAll();

    @Insert
    List<Long> insertAll(MessungDBO... messungen);

    @Delete
    int delete(MessungDBO messung);


}
