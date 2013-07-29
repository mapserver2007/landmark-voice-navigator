package com.mapserverframework.lvn;

/**
 * 逆ジオコーディングコンテナ
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class ReverseGeocodingContainer {
    /** APIキー */
    private String appId;
    /** 住所 */
    private String address;
    /** 住所ひらがな */
    private String addressHiragana;
    
    /**
     * APPキーを返却する
     * @return APPキー
     */
    public String getAppId() {
        return appId;
    }
    
    /**
     * APPキーを設定する
     * @param appId APPキー
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 住所を返却する
     * @return 住所
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * 住所を設定する
     * @param address 住所
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * 住所ひらがなを返却する
     * @return 住所ひらがな
     */
    public String getAddressHiragana() {
        return addressHiragana;
    }
    
    /**
     * 住所ひらがなを設定する
     * @param addressHiragana 住所ひらがな
     */
    public void setAddressHiragana(String addressHiragana) {
        this.addressHiragana = addressHiragana;
    }
}
