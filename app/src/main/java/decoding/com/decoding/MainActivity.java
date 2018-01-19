package decoding.com.decoding;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.ALARM_SERVICE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String PREFERRED_SIM = "preferred_sim";
    public static final String SHARED_PREFERENCES = "mPreferences";
    private int INTERVAL = 30 * 1000;

    public static LatLng latLng = new LatLng(0, 0);
    public static Marker marker;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    public static GoogleMap map;
    public static TextView speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        speedTextView = (TextView) findViewById(R.id.speedText);
        Intent intent = new Intent(this, SMSListener.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //load the map
        mapFragment.getMapAsync(this);
        start(null);
//
//        new AlertDialog.Builder(this)
//                .setItems(new String[]{"SIM 1", "SIM 2"}, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putInt(PREFERRED_SIM, which).apply();
//                    }
//                }).setTitle("Choose preferred SIM").create().show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        marker = map.addMarker(new MarkerOptions().position(latLng).title("Vehicles Location"));

        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void stop(View view) {
        alarmManager.cancel(pendingIntent);
    }

    public void start(View view) {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                , INTERVAL, pendingIntent);
    }
}
