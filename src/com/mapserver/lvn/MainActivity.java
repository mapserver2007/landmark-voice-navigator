package com.mapserver.lvn;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
        voiceTalker = new VoiceTalker(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 位置情報を取得
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location = new LocationController(manager);
        location.setActivity(this);
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
        voiceTalker.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onCurrentLocation(View view) {
        TextView text = (TextView) findViewById(R.id.textView1);
        // TODO 以下の処理は本番では使わないのであとで消す。
        text.setText("⊂(・∀・)⊃ﾔｯT！");
        
        // TODO 横向きにすると音が止まる
        //voiceTalker.speak("もくてきちまであとよんひゃくじゅういちめーとるです");
        //voiceTalker.speak("げんざいちはとうきょうといたばしくみなみときわだいです");
        
        ReverseGeocoding geo = new ReverseGeocoding(this);
        geo.execute("139.68867458", "35.75514038");
    }
    
    /**
     * 逆ジオコーディングのコールバック
     * @param text ひらがな表記の住所
     */
    public void onConvertAddress(String text) {
        voiceTalker.speak("げんざいちは" + text + "ですう");
    }
}
