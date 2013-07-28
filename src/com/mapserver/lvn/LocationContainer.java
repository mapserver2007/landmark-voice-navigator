package com.mapserver.lvn;

/**
 * 位置情報を格納するBeanクラス
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class LocationContainer {
    /** 経度 */
    private Double longitude;
    /** 緯度 */
    private Double latitude;
    
    /**
     * 経度を返却する
     * @return 経度
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 経度を設定する
     * @param longitude 経度
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    /**
     * 緯度を返却する
     * @return 緯度
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 緯度を設定する
     * @param latitude 緯度
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
