package fx.dbradar.dragonradar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fx.dbradar.dragonradar.overlay.DBMarkersOverlay;
import fx.dbradar.dragonradar.overlay.GridOverlay;
import fx.dbradar.dragonradar.overlay.MapEventsOverlay;


public class RadarActivity extends Activity {

    private String username;
    private String lat, lng;

    DBMarkersOverlay dbMarkersOverlay;

    public MapView mv;

    private Switch switch1;
    private SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    int counter;

    @Override
    public void onStart(){
        super.onStart();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }
    @Override
    protected void onStop(){
        super.onStop();
        //audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy() de Radar", Toast.LENGTH_LONG).show();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        switch1 = (Switch) findViewById(R.id.switch1);

        mv = (MapView) findViewById(R.id.mapview);

        mv.setMinZoomLevel(mv.getTileProvider().getMinimumZoomLevel());
        mv.setMaxZoomLevel(mv.getTileProvider().getMaximumZoomLevel());

        mv.setZoom(10);
        mv.setCenter(new LatLng(-71, -16));

        mv.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(Color.argb(255, 2, 54, 32));
        mv.getOverlayManager().getTilesOverlay().setLoadingLineColor(Color.argb(255, 2, 54, 32));

        mv.setDiskCacheEnabled(true);

        GridOverlay gridOverlay = new GridOverlay();
        mv.getOverlays().add(gridOverlay);

        dbMarkersOverlay = new DBMarkersOverlay();
        mv.getOverlayManager().add(dbMarkersOverlay);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, new MapEventsReceiver() {
            @Override
            public boolean singleTapUpHelper(ILatLng p) {
                return true;
            }

            @Override
            public boolean longPressHelper(ILatLng p) {
                return false;
            }
        });
        mv.getOverlayManager().add(mapEventsOverlay);

        //mv.setUserLocationEnabled(true);
        //mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW_BEARING);

        mv.setDiskCacheEnabled(true);

        Intent intent = getIntent();
        username = intent.getStringExtra("fx.username");

        Log.d("RadarActivity", username);

        /////

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener(this);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


        /////
        int delay = 1000; // delay for 5 sec.
        int period = 5000; // repeat every 10 secs.

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {



            }

        }, delay, period);


        ////
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        counter = 0;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                soundPool.play(soundID, volume, volume, 1, -1, 1f);
            }
        });
        soundID = soundPool.load(this, R.raw.ti_ti_ti, 1);
        //soundPool.pause(AudioManager.STREAM_MUSIC);
        switch1.setChecked(true);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                } else {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                }

            }
        });
        //soundPool.pause(soundID);
        ////
    }


    public void playLoop() {
        // Is the sound loaded does it already play?
        if (loaded && !plays) {
            // the sound will play for ever if we put the loop parameter -1
            soundPool.play(soundID, volume, volume, 1, -1, 1f);
            counter = counter++;
            Toast.makeText(this, "Plays loop", Toast.LENGTH_SHORT).show();
            plays = true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_radar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        private RadarActivity radarActivity;

        public MyLocationListener(RadarActivity radarActivity) {
            this.radarActivity = radarActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v("RadarActivity", longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v("RadarActivity", latitude);

            lat = "" + loc.getLatitude();
            lng = "" + loc.getLongitude();

            new UpdatePositionTask(this.radarActivity).execute(username, lat, lng);

            radarActivity.mv.setCenter(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private class UpdatePositionTask extends AsyncTask<String, Void, LatLng> {

        RadarActivity radarActivity;

        public UpdatePositionTask(RadarActivity radarActivity) {
            this.radarActivity = radarActivity;
        }

        @Override
        protected LatLng doInBackground(String... params) {
            return null;

        }

        @Override
        protected void onPostExecute(LatLng result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
