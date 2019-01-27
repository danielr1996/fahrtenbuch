package de.danielr1996.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private TextView tvDistance, tvActive, tvSince;
    private ImageView ivActive;
    private LocationService locationService;
    private MessungDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDistance = findViewById(R.id.tv_distance);
        tvActive = findViewById(R.id.tv_active);
        tvSince = findViewById(R.id.tv_since);
        ivActive = findViewById(R.id.iv_active);
        dao = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "users").allowMainThreadQueries().fallbackToDestructiveMigration().build().messungDao();
        locationService = new AndroidLocationService(this)
                .registerCallback(point -> dao.insertAll(Messungen.fromPoint(point, LocalDateTime.now())))
                .registerCallbackActive(active -> {
                    if (active) {
                        ivActive.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_active_24px));
                        tvActive.setText(getText(R.string.tv_active_active));
                    } else {
                        ivActive.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_inactive_24px));
                        tvActive.setText(getText(R.string.tv_active_inactive));
                    }
                });

        Disposable textViewAktualisieren = dao.getAll()
                .map(Messungen::length)
                .subscribe(distance -> runOnUiThread(() -> this.tvDistance.setText(String.format(getString(R.string.tv_kilometers), distance))));
        Disposable sinceAktualisierung = dao.getAll()
                .subscribe(messungen -> runOnUiThread(() -> {
                    if (!messungen.isEmpty()) {
                        this.tvSince.setText(LocalDateTime.parse(messungen.get(0).dateTime).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                    } else {
                        this.tvSince.setText("");
                    }
                }));
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
        Disposable disposable = dao.getAllAsSingle()
                .map(messungen -> {
                    GeoJsonExporter.saveToDisk(messungen, this);
                    return messungen;
                }).subscribe(messungen -> {
                    runOnUiThread(() -> {
                        Toast toast = Toast.makeText(getApplicationContext(), "Fahrt auf SD Karte gespeichert", Toast.LENGTH_SHORT);
                        toast.show();
                    });
                }, throwable -> {
                    runOnUiThread(() -> {
                        Toast toast = Toast.makeText(getApplicationContext(), "Fehler beim Exportieren", Toast.LENGTH_SHORT);
                        toast.show();
                    });
                });
    }

    public void onDelete(MenuItem mi) {
        dao.deleteAll();
    }

    public void onMap(MenuItem mi) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
