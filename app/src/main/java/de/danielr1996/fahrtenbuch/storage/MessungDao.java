package de.danielr1996.fahrtenbuch.storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MessungDao {
    @Query("SELECT * FROM messung")
    Flowable<List<Messung>> getAll();


    @Query("SELECT * FROM messung")
    Single<List<Messung>> getAllAsSingle();

    @Query("DELETE FROM messung")
    void deleteAll();

    @Insert
    List<Long> insertAll(Messung... messungen);

    @Delete
    int delete(Messung messung);


}
