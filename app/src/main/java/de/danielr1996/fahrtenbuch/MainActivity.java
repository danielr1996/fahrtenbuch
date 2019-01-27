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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private TextView tvDistance;
    private ToggleButton btnActive;
//    private ListView lvMessungen;
    private LocationService locationService;
    private MessungDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDistance = findViewById(R.id.tv_distance);
        btnActive = findViewById(R.id.btn_active);
//        lvMessungen = findViewById(R.id.lv_messungen);

        dao = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "users").allowMainThreadQueries().fallbackToDestructiveMigration().build().messungDao();
        locationService = new AndroidLocationService(this)
                .registerCallback(point -> dao.insertAll(Messungen.fromPoint(point, LocalDateTime.now())))
                .registerCallbackActive(btnActive::setChecked);

        Disposable textViewAktualisieren = dao.getAll()
                .map(Messungen::length)
                .subscribe(distance -> runOnUiThread(() -> this.tvDistance.setText(String.format(getString(R.string.tv_kilometers), distance))));
//        Disposable listViewAktualisieren = dao.getAll()
//                .map(list -> list.stream()
//                        .map(messung -> messung.latitude + " " + messung.longitude + " " + messung.dateTime)
//                        .collect(Collectors.toList()))
//                .subscribe(list -> {
//                    ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.list_item_messung, R.id.list_item_messung, list);
//                    runOnUiThread(() -> lvMessungen.setAdapter(listAdapter));
//                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onStartMessung(MenuItem mi) {
        locationService.start();
    }

    public void onStopMessung(MenuItem mi) {
        locationService.stop();
    }

    public void onExport(MenuItem mi) {
        dao.getAllAsSingle().doOnSuccess(messungs -> GeoJsonExporter.saveToDisk(messungs, this)).subscribe();
    }

    public void onDelete(MenuItem mi) {
        dao.deleteAll();
    }

    public void openActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
