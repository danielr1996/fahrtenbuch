package de.danielr1996.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import de.danielr1996.fahrtenbuch.location.AndroidLocationService;
import de.danielr1996.fahrtenbuch.location.LocationService;
import de.danielr1996.fahrtenbuch.storage.AppDatabase;
import de.danielr1996.fahrtenbuch.storage.MessungDao;
import de.danielr1996.fahrtenbuch.storage.Messungen;
import de.danielr1996.fahrtenbuch.storage.geojson.GeoJsonExporter;
import io.reactivex.disposables.Disposable;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private TextView tvKilometers;
    private Button btnStart, btnStop, btnDeleteDb, btnExport;
    private ListView lvMessungen;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvKilometers = findViewById(R.id.tv_kilometers);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnExport = findViewById(R.id.btn_export);
        btnDeleteDb = findViewById(R.id.btn_delete_db);
        lvMessungen = findViewById(R.id.lv_messungen);
        MessungDao dao = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "users").allowMainThreadQueries().fallbackToDestructiveMigration().build().messungDao();
        locationService = new AndroidLocationService(this);

        locationService.registerCallback(point->dao.insertAll(Messungen.fromPoint(point, LocalDateTime.now())));

        btnDeleteDb.setOnClickListener(noop -> dao.deleteAll());
        btnStart.setOnClickListener(noop->locationService.start());
        btnStop.setOnClickListener(noop->locationService.stop());
        btnExport.setOnClickListener(noop->dao.getAllAsSingle().doOnSuccess(messungs -> GeoJsonExporter.saveToDisk(messungs, this)).subscribe());

        Disposable subscription = dao.getAll()
                .map(Messungen::length)
                .map(this::generateDistanceString)
                .subscribe(distance -> runOnUiThread(() -> this.tvKilometers.setText(distance)));
        Disposable subscription2 = dao.getAll()
                .map(list -> list.stream()
                        .map(messung -> messung.latitude + " " + messung.longitude + " " + messung.dateTime)
                        .collect(Collectors.toList()))
                .subscribe(list -> {
                    ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.list_item_messung, R.id.list_item_messung, list);
                    runOnUiThread(() -> lvMessungen.setAdapter(listAdapter));
                });
    }

    private String generateDistanceString(double distance) {
        if (distance > 1000) {
            return String.format(getString(R.string.tv_kilometers), distance);
        } else {
            return String.format(getString(R.string.tv_meters), (int) distance);
        }
    }
}
