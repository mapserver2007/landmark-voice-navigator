package com.mapserver.lvn;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * メインアクティビティクラス        
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class MainActivity extends Activity {
    /** ロケーション */
    private LocationController location;
    /** 音声 */
    private VoiceTalker voiceTalker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 音声再生
        CheckBox cb = (CheckBox) findViewById(R.id.auto_speaking);
        voiceTalker = new VoiceTalker(this);
        voiceTalker.setAutoSpeak(cb.isChecked());
        // 位置情報を取得
        location = new LocationController(this);
    }
    
    /**
     * 位置情報が変化したときの処理
     * @param location 位置情報
     */
    public void onLocationChanged(LocationContainer location) {
        TextView address = (TextView) findViewById(R.id.showAddress),
                 addressHiragana = (TextView) findViewById(R.id.showAddressHiragana);
        
        String longitude = location.getLongitude().toString(),
               latitude = location.getLatitude().toString();
        
        updateText(R.id.showLongitude, longitude);
        updateText(R.id.showLatitude, latitude);
        
        ReverseGeocodingContainer container = new ReverseGeocodingContainer();
        String appId = "";
        container.setAppId(appId);
        container.setAddress(address.getText().toString());
        container.setAddressHiragana(addressHiragana.getText().toString());
        
        ReverseGeocoding geocoding = new ReverseGeocoding(this, container);
        geocoding.execute(longitude, latitude);
    }
    
    /**
     * 自動発話設定を更新
     * @param view ビューオブジェクト
     */
    public void onAutoSpeakingCheck(View view) {
        CheckBox cb = (CheckBox) findViewById(R.id.auto_speaking);
        voiceTalker.setAutoSpeak(cb.isChecked());
    }
    
    /**
     * しゃべります
     * speakボタンのコールバック
     * @param view ビューオブジェクト
     */
    public void onSpeak(View view) {
        TextView textView = (TextView) findViewById(R.id.showAddressHiragana);
        voiceTalker.speak("げんざいちわ" + textView.getText().toString() + "ですう");
    }
    
    /**
     * 自動でしゃべります
     * @param text テキスト
     */
    public void onAutoSpeak(ReverseGeocodingContainer container) {
        updateText(R.id.showAddress, container.getAddress());
        updateText(R.id.showAddressHiragana, container.getAddressHiragana());
        voiceTalker.autoSpeak("げんざいちわ" + container.getAddressHiragana() + "ですう");
    }
    
    /**
     * GPSステータスを受け取る
     * @param status GPSステータス
     */
    public void onGpsStatusChanged(String status) {
        updateText(R.id.showGpsStatus, status);
    }
    
    /**
     * ビューの表示内容を更新
     * @param id 要素ID
     * @param text 表示文字列
     */
    public void updateText(int id, String text) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        location.update();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        location.remove();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        location.remove();
        voiceTalker.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * 縦横切替時イベントのコールバック
     * AndroidManifest.xmlの<activity>タグにandroid:configChanges="orientation|screenSize"
     * を指定すると切替時にActivityの再起動をキャンセルできる
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
