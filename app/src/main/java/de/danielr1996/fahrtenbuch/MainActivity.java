package de.danielr1996.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import de.danielr1996.fahrtenbuch.storage.AppDatabase;
import de.danielr1996.fahrtenbuch.storage.Messung;
import de.danielr1996.fahrtenbuch.storage.MessungDao;
import de.danielr1996.fahrtenbuch.storage.Messungen;
import io.reactivex.functions.Consumer;
import one.util.streamex.StreamEx;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvKilometers;
    private Button btnStart, btnStop, btnDeleteDb;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvKilometers = findViewById(R.id.tv_kilometers);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnDeleteDb = findViewById(R.id.btn_delete_db);
        MessungDao dao = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "users").allowMainThreadQueries().fallbackToDestructiveMigration().build().messungDao();

        tvKilometers.setText(String.format(getString(R.string.tv_kilometers), 530.0));

        Messung messung1 = new Messung();
        Messung messung2 = new Messung();
        Messung messung3 = new Messung();
        messung1.dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        messung1.longitude = 11.367073059082031;
        messung1.latitude = 49.514510112029;
        messung2.dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        messung2.longitude = 11.43350601196289;
        messung2.latitude = 49.50893719029439;
        messung3.dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        messung3.longitude = 11.400375366210938;
        messung3.latitude = 49.47135913524246;
        btnStart.setOnClickListener((noop) -> dao.insertAll(messung1, messung2, messung3));//9.66
        btnDeleteDb.setOnClickListener(noop -> dao.deleteAll());

        Consumer<List<Messung>> consumer = (list) -> {
            double strecke = StreamEx.of(list.stream())
                    .map(Messungen::toPoint)
            .pairMap((p1, p2)->p1.distanceInKm(p2))
            .reduce((d1, d2)->d1+d2)
            .orElse(0.0);
            if(strecke>1000){
                runOnUiThread(()->{
                    this.tvKilometers.setText(String.format(getString(R.string.tv_kilometers), strecke));

                });
            }else{
                runOnUiThread(()->{
                    this.tvKilometers.setText(String.format(getString(R.string.tv_meters), (int)strecke));

                });
            }
        };

        dao.getAll().subscribe(consumer);
    }
}
