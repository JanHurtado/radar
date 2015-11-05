package fx.dbradar.dragonradar.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.SafeDrawOverlay;
import com.mapbox.mapboxsdk.util.constants.UtilConstants;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.safecanvas.ISafeCanvas;
import com.mapbox.mapboxsdk.views.safecanvas.SafePaint;
import com.mapbox.mapboxsdk.views.util.Projection;

public class DBMarkersOverlay extends SafeDrawOverlay {

    public static final String TEST_OVERLAY = "TestOverlay";

    protected SafePaint mRedPaint = null;
    private int mRedColor = Color.rgb(255, 10, 10);

    protected SafePaint mYellowPaint = null;
    private int mYellowColor = Color.rgb(240, 240, 10);

    private LatLng[] positions;

    private final PointF mMapCoords = new PointF();


    public void setPositions(LatLng[] pos) {
        positions = pos.clone();
    }

    public DBMarkersOverlay() {
        super();

        mRedPaint = new SafePaint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setFilterBitmap(true);
        mRedPaint.setColor(mRedColor);
        mRedPaint.setStrokeWidth(1);

        mYellowPaint = new SafePaint();
        mYellowPaint.setAntiAlias(true);
        mYellowPaint.setFilterBitmap(true);
        mYellowPaint.setColor(mYellowColor);
        mYellowPaint.setStrokeWidth(1);

        this.setOverlayIndex(20);
    }

    @Override
    protected void drawSafe(ISafeCanvas c, MapView mv, boolean shadow) {

        if (shadow) {
            return;
        }

        Rect rect = c.getClipBounds();

        int cx = rect.centerX();
        int cy = rect.centerY();

        c.save();

        c.rotate(-mv.getMapOrientation(), cx, cy);

        c.drawCircle(cx, cy, 5, mRedPaint);

        c.restore();

        if (positions == null)
            return;


        for (LatLng position : positions) {
            drawLocation(c, mv, position);
        }

    }


    protected void drawLocation(final ISafeCanvas canvas, final MapView mapView, final LatLng lastFix) {

        final Projection projection = mapView.getProjection();

        projection.toMapPixels(lastFix, mMapCoords);
//        final float mapScale = 1 / mapView.getScale();

//        canvas.save();

//        canvas.scale(mapScale, mapScale, mMapCoords.x, mMapCoords.y);

            canvas.save();

            canvas.drawCircle(mMapCoords.x, mMapCoords.y, 5, mYellowPaint);

            canvas.restore();

//        canvas.restore();
    }
}
