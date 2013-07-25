package com.mapserver.lvn;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LocationController implements LocationListener {
    /** ロケーションマネージャ */
    private LocationManager manager;
    /** View */
    private Activity activity;
    
    public LocationController(LocationManager manager) {
        this.manager = manager;
    }
    
    public void update() {
        // TODO 距離の設定は画面上から取れるようにする
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }
    
    public void remove() {
        manager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (activity != null) {
            TextView lng = (TextView) activity.findViewById(R.id.showLongitude),
                     lat = (TextView) activity.findViewById(R.id.showLatitude);
            
            lng.setText(String.valueOf(location.getLongitude()));
            lat.setText(String.valueOf(location.getLatitude()));
            
            Log.i("sparhawk", String.valueOf(location.getLongitude()));
            Log.i("sparhawk", String.valueOf(location.getLatitude()));
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO 自動生成されたメソッド・スタブ
        
    }
    
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
