package fx.dbradar.dragonradar;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;

import fx.dbradar.dragonradar.overlay.DBMarkersOverlay;
import fx.dbradar.dragonradar.overlay.GridOverlay;
import fx.dbradar.dragonradar.overlay.MapEventsOverlay;


public class RadarActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        MapView mv = (MapView) findViewById(R.id.mapview);

        mv.setMinZoomLevel(mv.getTileProvider().getMinimumZoomLevel());
        mv.setMaxZoomLevel(mv.getTileProvider().getMaximumZoomLevel());

        mv.setZoom(10);
        mv.setCenter(new LatLng(-71, -16));

        mv.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(Color.argb(255, 0, 0, 0));
        mv.getOverlayManager().getTilesOverlay().setLoadingLineColor(Color.argb(255, 0, 0, 0));

        mv.setDiskCacheEnabled(true);

        GridOverlay gridOverlay = new GridOverlay();
        mv.getOverlays().add(gridOverlay);

        DBMarkersOverlay dbMarkersOverlay = new DBMarkersOverlay();
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
}
