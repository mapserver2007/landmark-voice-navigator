package com.mapserver.lvn;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * ロケーションコントローラクラス        
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class LocationController implements LocationListener {
    /** ロケーションマネージャ */
    private LocationManager manager;
    /** View */
    private Activity activity;
    
    public LocationController(Activity activity) {
        this.activity = activity;
        manager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        update();
    }
    
    public void update() {
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }
    
    public void remove() {
        manager.removeUpdates(this);
    }

    /**
     * 位置情報が変化したら実行されるコールバック
     * @param location ロケーションオブジェクト
     */
    @Override
    public void onLocationChanged(Location location) {
        if (activity == null) {
            return;
        }
        LocationBean bean = new LocationBean();
        bean.setLng(location.getLongitude());
        bean.setLat(location.getLatitude());
        // Activityに取得した位置情報を返す
        ((MainActivity) activity).onLocationChanged(bean);
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
