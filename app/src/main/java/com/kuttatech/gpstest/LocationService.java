package com.kuttatech.gpstest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.kuttatech.gpstest.databinding.ActivityMainBinding;

// Helper class that manages LocationListener,
// and feeds location updates to a JNI callback.
public class LocationService extends Service implements LocationListener {
    private final Context mContext;
    protected LocationManager locMgr;
    private String provider;

    // Load our JNI callback library.
    static {
        System.loadLibrary("gpstest");
    }

    // Constructor. Requests LocationService updates, which
    // will get sent to onLocationChanged().
    public LocationService(Context context) {
        this.mContext = context;
        locMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locMgr.getBestProvider(criteria, false);
        Log.i("LocationService", "Getting best provider");
        // TODO: checkPermission and/or SecurityException
        Location loc = locMgr.getLastKnownLocation(provider);
        if (loc != null) {
            Log.i("LocationService", "Provider " + provider + " selected.");

            // We have an initial position, may as well use it.
            onLocationChanged(loc);
        }

        // Ask the LocationManager to call us back whenever our position
        // changes by more than X, but not more often than Y msecs. These parameters
        // will eventually become configurable items, so let's give them names now.
        final float minDistanceM = 0.1f;
        final long minTimeMs = 1000;

        locMgr.requestLocationUpdates(provider,
                minTimeMs, minDistanceM,
                this, null);
    }

    // Called by the LocationManager when a position update is received. We also
    // get called once during construction, so as to not waste the initial position
    // fix. Be careful in here.
    @Override
    public void onLocationChanged(Location loc)
    {
        // Issue output to logcat, to confirm that we're getting callbacks
        // from LocationManager:
        // Log.i("LocationService", "onLocationChanged: loc=" + loc);

        // Pass the location to our JNI method. Users will implement
        // that method, or a variation thereof, to receive the current location.
        String s = locationChanged(loc);

        // Our JNI method returns a String, which it uses to "read back" the location we
        // provided. For troubleshooting, mostly. Send it to logcat, because why not?
        Log.i("LocationService", "locationChanged() returned: " + s);
    }


    private final IBinder binder = new LocalBinder();
    public class LocalBinder extends Binder {
        LocationService getService() {
            // Return this instance of LocalService so clients can call public methods.
            // Users may not need this, but we have to provide the method regardless.
            return LocationService.this;
        }
    }

     @Override
     public IBinder onBind(Intent intent) {
        return binder;
    }

    public native String locationChanged(Location loc);
}
