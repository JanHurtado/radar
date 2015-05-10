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

public class GridOverlay extends SafeDrawOverlay {

    public static final String TEST_OVERLAY = "TestOverlay";

    protected SafePaint mLinePaint = null;
    private int mLineColor = Color.rgb(6, 76, 0);

    public GridOverlay() {
        super();

        mLinePaint = new SafePaint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setFilterBitmap(true);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(1);

        this.setOverlayIndex(10);
    }

    @Override
    protected void drawSafe(ISafeCanvas c, MapView mv, boolean shadow) {

        if (shadow) {
            return;
        }

        Rect rect = c.getClipBounds();

        int cx = rect.centerX();
        int cy = rect.centerY();

        int delta = 50;

        c.save();

        c.rotate(-mv.getMapOrientation(), cx, cy);

        int hl = 0;
        int vl = 0;

        // Draw vertical lines from cx to the right
        for (int x = cx; x < rect.right; x += delta) {
            c.drawLine(x, rect.bottom, x, rect.top, mLinePaint);
            vl++;
        }

        // Draw vertical lines from cx to the left
        for (int x = cx - delta; x > rect.left; x -= delta) {
            c.drawLine(x, rect.bottom, x, rect.top, mLinePaint);
            vl++;
        }

        // Draw horizontal lines from cy to the bottom
        for (int y = cy; y < rect.bottom; y += delta) {
            c.drawLine(rect.left, y, rect.right, y, mLinePaint);
            hl++;
        }

        // Draw horizontal lines from cy to the top
        for (int y = cy - delta; y > rect.top; y -= delta) {
            c.drawLine(rect.left, y, rect.right, y, mLinePaint);
            hl++;
        }

        c.restore();
    }
}
