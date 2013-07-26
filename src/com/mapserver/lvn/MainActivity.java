package com.mapserver.lvn;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
    public void onLocationChanged(LocationBean location) {
        String prevSpeakText = getSpeakText();
        // デバッグ
        ReverseGeocoding geo = new ReverseGeocoding(this);
        geo.execute(location.getLng(), location.getLat());
        // 自動発話
        if (prevSpeakText != getSpeakText()) {
            voiceTalker.autoSpeak("げんざいちわ" + getSpeakText() + "ですう");
        }
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
     * @param view ビューオブジェクト
     */
    public void onSpeak(View view) {
        voiceTalker.speak("げんざいちわ" + getSpeakText() + "ですう");
    }
    
    /**
     * 発話するテキストを取得する
     * @return 発話テキスト
     */
    private String getSpeakText() {
        TextView textView = (TextView) findViewById(R.id.showAddressHiragana);
        return textView.getText().toString();
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
