package fx.dbradar.dragonradar.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.overlay.MapEventsReceiver;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.Projection;

import fx.dbradar.dragonradar.R;

public class MapEventsOverlay extends Overlay {

    private MapEventsReceiver mReceiver;
    private int[] zoomLevels;
    private int currentZoomLevel;

    private SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    int counter;

    public MapEventsOverlay(Context ctx, MapEventsReceiver receiver) {
        super(ctx);
        mReceiver = receiver;
        setOverlayIndex(MAPEVENTSOVERLAY_INDEX - 1);

        zoomLevels = new int[] {18, 15, 14, 12, 10};
        currentZoomLevel = 0;



        ////
        audioManager = (AudioManager) ctx.getSystemService(ctx.AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

//        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        counter = 0;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(ctx, R.raw.push_push, 1);
        ////
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

            soundPool.play(soundID, volume, volume, 1, 1, 1.f);
        }
        return true;
    }
}
