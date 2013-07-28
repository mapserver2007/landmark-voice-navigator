package com.mapserver.lvn;

/**
 * アプリケーション内で使用するコンテナ
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class LvnContainer {
    /** ロケーションコンテナ */
    private LocationContainer locationContainer;
    
    /**
     * ロケーションコンテナを返却する
     * @return ロケーションコンテナ
     */
    public LocationContainer getLocationContainer() {
        return locationContainer;
    }

    /**
     * ロケーションコンテナを設定する
     * @param locationContainer ロケーションコンテナ
     */
    public void setLocationContainer(LocationContainer locationContainer) {
        this.locationContainer = locationContainer;
    }
}
