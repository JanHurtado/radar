package fx.dbradar.dragonradar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

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

        mv.setUserLocationEnabled(true);
        mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);

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

                String url="http://179.43.127.156:9001/get_friends_positions";

                System.setProperty("http.proxyHost", "proxy.example.com");
                System.setProperty("http.proxyPort", "8080");

                LatLng[] positions;

                ///
                try {
                    URL object = new URL(url);

                    HttpURLConnection con = (HttpURLConnection) object.openConnection();

                    con.setDoOutput(true);
                    con.setRequestMethod("GET");
                    con.setUseCaches(false);
                    con.setDoInput(true);
                    con.setConnectTimeout(10000);
                    con.setReadTimeout(10000);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");


                    JSONObject data = new JSONObject();
                    data.put("username", username);
                    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

                    out.write(data.toString());
                    out.close();

                    StringBuilder sb = new StringBuilder();

                    int HttpResult = con.getResponseCode();

                    if(HttpResult == HttpURLConnection.HTTP_OK){

                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));

                        String line = null;

                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }

                        br.close();

                        String json = sb.toString();

                        Log.d("RadarActivity", "" + json);



                        ///

                        JSONObject jObject = new JSONObject(json);

                        JSONArray jArray = jObject.getJSONArray("positions");

                        positions = new LatLng[jArray.length()];

                        for (int i=0; i < jArray.length(); i++)
                        {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);

                                String username = oneObject.getString("username");
                                String lat = oneObject.getString("lat");
                                String lng = oneObject.getString("lng");

                                positions[i] = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                            } catch (JSONException e) {
                                // Oops
                            }
                        }

                        dbMarkersOverlay.setPositions(positions);

                        ///

                    }else{
                        Log.d("RadarActivity", "" + con.getResponseMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, delay, period);
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
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private class UpdatePositionTask extends AsyncTask<String, Void, LatLng> {

        RadarActivity radarActivity;

        public UpdatePositionTask(RadarActivity radarActivity) {
            this.radarActivity = radarActivity;
        }

        @Override
        protected LatLng doInBackground(String... params) {
            String url="http://179.43.127.156:9001/update_position";

            System.setProperty("http.proxyHost", "proxy.example.com");
            System.setProperty("http.proxyPort", "8080");

            ///
            try {
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setUseCaches(false);
                con.setDoInput(true);
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");


                JSONObject data = new JSONObject();
                data.put("username", username);
                data.put("lat", lat);
                data.put("lng", lng);

                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(data.toString());
                out.close();

                StringBuilder sb = new StringBuilder();

                int HttpResult = con.getResponseCode();

                if(HttpResult == HttpURLConnection.HTTP_OK){

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));

                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();

                    Log.d("RadarActivity", "" + sb.toString());

                }else{
                    Log.d("RadarActivity", "" + con.getResponseMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ///

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
