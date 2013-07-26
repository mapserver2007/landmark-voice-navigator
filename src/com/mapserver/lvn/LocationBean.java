package com.mapserver.lvn;

/**
 * 位置情報を格納するBeanクラス
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class LocationBean {
    /** 経度 */
    private double lng;
    /** 緯度 */
    private double lat;
    
    /**
     * 経度を返却する
     * @return 経度
     */
    public double getLng() {
        return lng;
    }
    /**
     * 経度を設定する
     * @param lng 経度
     */
    public void setLng(double lng) {
        this.lng = lng;
    }
    
    /**
     * 緯度を返却する
     * @return 緯度
     */
    public double getLat() {
        return lat;
    }
    
    /**
     * 緯度を設定する
     * @param lat 緯度
     */
    public void setLat(double lat) {
        this.lat = lat;
    }
}
