package fx.dbradar.dragonradar.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.SafeDrawOverlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.safecanvas.ISafeCanvas;
import com.mapbox.mapboxsdk.views.safecanvas.SafePaint;

public class DBMarkersOverlay extends SafeDrawOverlay {

    public static final String TEST_OVERLAY = "TestOverlay";

    protected SafePaint mRedPaint = null;
    private int mRedColor = Color.rgb(255, 10, 10);

    protected SafePaint mYellowPaint = null;
    private int mYellowColor = Color.rgb(10, 240, 240);

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



    }
}
