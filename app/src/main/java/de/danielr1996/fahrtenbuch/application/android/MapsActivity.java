package de.danielr1996.fahrtenbuch.application.android;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import de.danielr1996.fahrtenbuch.R;
import de.danielr1996.fahrtenbuch.application.storage.DatabaseMessungRepository;
import de.danielr1996.fahrtenbuch.domain.Messung;
import de.danielr1996.fahrtenbuch.domain.MessungRepository;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MessungRepository messungRepository;
    Disposable disposableDatabaseUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.messungRepository = DatabaseMessungRepository.getInstance(this);
    }

    public Consumer<List<Messung>> doMapAktualisierungOnDatabaseUpdate() {
        return list -> {
            List<LatLng> cameras = new ArrayList<>();
            list.stream()
                    .map(messung -> {
                        LatLng latLng = new LatLng(messung.getPunkt().getLatitude(), messung.getPunkt().getLongitude());
                        cameras.add(latLng);
                        return new MarkerOptions().position(latLng).title(messung.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }).forEach(marker -> runOnUiThread(() -> mMap.addMarker(marker)));
            if (cameras.size() > 0) {
                runOnUiThread(() -> {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameras.get(0), 10));
                });
            }
        };
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(MapsActivity.class.getName(), "onMapReady");
        mMap = googleMap;
    }

    @Override
    protected void onPause() {
        Log.i(MapsActivity.class.getName(), "onPause");
        super.onPause();
        this.disposableDatabaseUpdate.dispose();
    }

    @Override
    protected void onResume() {
        Log.i(MapsActivity.class.getName(), "onResume");
        super.onResume();
        this.disposableDatabaseUpdate = this.messungRepository.getAll()
                .doOnNext(noop -> Log.e(MapsActivity.class.getName(), "onDatabaseUpdate"))
                .subscribe(doMapAktualisierungOnDatabaseUpdate());
    }
}
