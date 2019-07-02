package me.kumar.bluetoothterminal;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {

    LocationManager locationManager;

    public LocationService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
        LocationListener locationListenerGPS=new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
                String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,
                2000,
                10, locationListenerGPS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class AlarmReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
            Log.e("Service_call_"  , "You are in AlarmReceive class.");
            Intent background = new Intent(context, LocationService.class);
            Log.e("AlarmReceive ","testing called broadcast called");
            context.startService(background);
            Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
        }
    }
}

