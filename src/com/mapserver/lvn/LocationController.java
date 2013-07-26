package com.mapserver.lvn;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class LocationController implements LocationListener {
    /** ロケーションマネージャ */
    private LocationManager manager;
    /** View */
    private Activity activity;
    /** ロケーション情報 */
    private LocationBean location;
    
    public LocationController(Activity activity) {
        this.activity = activity;
        location = new LocationBean();
        manager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        update();
    }
    
    public void update() {
        // 10秒＋100m
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }
    
    public void remove() {
        manager.removeUpdates(this);
    }
    
    public LocationBean getLocation() {
        return location;
    }
    
    /**
     * 位置情報が変化したら実行されるコールバック
     * @param location ロケーションオブジェクト
     */
    @Override
    public void onLocationChanged(Location location) {
        this.location.setLng(location.getLongitude());
        this.location.setLat(location.getLatitude());
        
        if (activity != null) {
            TextView lng = (TextView) activity.findViewById(R.id.showLongitude),
                     lat = (TextView) activity.findViewById(R.id.showLatitude);
            lng.setText(String.valueOf(location.getLongitude()));
            lat.setText(String.valueOf(location.getLatitude()));
            // Activityに取得した位置情報を返す
            ((MainActivity) activity).onLocationChanged(this.location);
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
    

}
