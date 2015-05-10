package fx.dbradar.dragonradar.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.Projection;

public class MapEventsOverlay extends Overlay {

    private MapEventsReceiver mReceiver;
    private int[] zoomLevels;
    private int currentZoomLevel;

    public MapEventsOverlay(Context ctx, MapEventsReceiver receiver) {
        super(ctx);
        mReceiver = receiver;
        setOverlayIndex(MAPEVENTSOVERLAY_INDEX - 1);

        zoomLevels = new int[] {18, 15, 14, 12, 10};
        currentZoomLevel = 0;
    }

    @Override
    protected void draw(Canvas c, MapView osmv, boolean shadow) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            currentZoomLevel = (currentZoomLevel + 1) % zoomLevels.length;
            mapView.setZoom(zoomLevels[currentZoomLevel]);
        }
        return true;
    }
}
