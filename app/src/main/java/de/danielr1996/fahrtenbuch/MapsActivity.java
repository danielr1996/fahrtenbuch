package de.danielr1996.fahrtenbuch;

import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;
import de.danielr1996.fahrtenbuch.storage.AppDatabase;
import de.danielr1996.fahrtenbuch.storage.Messung;
import de.danielr1996.fahrtenbuch.storage.MessungDao;
import de.danielr1996.fahrtenbuch.storage.Messungen;
import io.reactivex.disposables.Disposable;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MessungDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dao = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "users").allowMainThreadQueries().fallbackToDestructiveMigration().build().messungDao();
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
        mMap = googleMap;
        List<LatLng> camera = new ArrayList<>();

        Disposable mapAktualisierung = dao.getAll()
                .subscribe(list -> {
                    list.stream()
                            .map(messung -> {
                                LatLng latLng = new LatLng(messung.latitude, messung.longitude);
                                camera.add(latLng);
                                return new MarkerOptions().position(latLng).title(messung.dateTime);
                            }).forEach(marker -> runOnUiThread(() -> mMap.addMarker(marker)));
                    if (camera.size() > 0) {
                        runOnUiThread(() -> {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera.get(0),10));
                        });
                    }
                });


    }
}
