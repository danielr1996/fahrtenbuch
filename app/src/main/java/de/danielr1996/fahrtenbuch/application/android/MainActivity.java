package de.danielr1996.fahrtenbuch.application.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import de.danielr1996.fahrtenbuch.R;
import de.danielr1996.fahrtenbuch.application.geojson.FeatureCollections;
import de.danielr1996.fahrtenbuch.application.location.PlayServicesLocationRepository;
import de.danielr1996.fahrtenbuch.application.storage.DatabaseMessungRepository;
import de.danielr1996.fahrtenbuch.application.support.GeoJsonExporter;
import de.danielr1996.fahrtenbuch.domain.LocationRepository;
import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.MessungRepository;
import de.danielr1996.fahrtenbuch.domain.Messungen;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private TextView tvDistance, tvActive, tvSince;
    private ImageView ivActive;
    private Activity activity = this;
    Intent trackingServiceIntent;
    private boolean trackingActive = false;
    private MessungRepository messungRepository;
    private LocationRepository locationRepository;
    Disposable locationSubscription;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                System.out.println("Permission granted");
            } else {
                // Permission was denied or request was cancelled
                System.out.println("Permission denied");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MainActivity.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationRepository = new PlayServicesLocationRepository(this);
        tvDistance = findViewById(R.id.tv_distance);
        tvActive = findViewById(R.id.tv_active);
        tvSince = findViewById(R.id.tv_since);
        ivActive = findViewById(R.id.iv_active);
        messungRepository = new DatabaseMessungRepository(getApplicationContext());

        Disposable textViewAktualisieren = messungRepository.getAll()
                .map(Messungen::length)
                .subscribe(distance -> runOnUiThread(() -> this.tvDistance.setText(String.format(getString(R.string.tv_kilometers), distance))));
        Disposable sinceAktualisierung = messungRepository.getAll()
                .subscribe(messungen -> runOnUiThread(() -> {
                    if (!messungen.isEmpty()) {
                        this.tvSince.setText(messungen.get(0).getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
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
        locationSubscription = locationRepository.getMessung().subscribe(doOnMessung());
        ivActive.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_active_24px));
        tvActive.setText(getText(R.string.tv_active_active));
    }

    public void onStopMessung(MenuItem mi) {
        locationSubscription.dispose();
        ivActive.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_inactive_24px));
        tvActive.setText(getText(R.string.tv_active_inactive));
    }

    public void onExport(MenuItem mi) {
        Disposable disposable = messungRepository.getAllAsSingle()
                .map(messungen -> {
                    GeoJsonExporter.saveToDisk(FeatureCollections.fromMessungen(messungen), this);
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
        messungRepository.deleteAll();
    }

    public void onMap(MenuItem mi) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MainActivity.class.toString(), "onResume");
        if (trackingServiceIntent != null)
            stopService(trackingServiceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(MainActivity.class.toString(), "onPause");
        if (trackingActive) {
            trackingServiceIntent = new Intent(this.getApplication(), ForegroundService.class);
            this.getApplication().startForegroundService(trackingServiceIntent);
        }
    }

    public Consumer<Messung> doOnMessung() {
        return (messung) -> {
            messungRepository.insertAll(messung);
            Log.i(MainActivity.class.getName(), messung.toString());
        };
    }
}
