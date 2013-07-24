package com.mapserver.lvn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.Toast;
import aquestalk2.AquesTalk2;

public class VoiceTalker implements AudioTrack.OnPlaybackPositionUpdateListener {
    /** wavファイルヘッダ長 */
    private static final int WAV_HEAD_LENGTH = 44;
    /** 無音区間バイト */
    private static final int SPACER = 2000;
    /** サンプリングレート */
    private static final int SAMPLING_RATE = 8000;
    
    /** コンテキスト */
    private Context context;
    /** 音声データバッファサイズ */
    //private int phont;
    private int bufferSize;
    /** オーディオデータ */
    private AudioTrack track;
    /** Aquestalkオブジェクト */
    private AquesTalk2 aquestalk;
    
    public VoiceTalker(Context context) {
        this.context = context;
        audioTrack();
    }
    
    /**
     * コンストラクタ
     */
    private void audioTrack() {
        // 音声データバッファサイズ
        bufferSize = SAMPLING_RATE * 2 * 10;
        // AquesTalkのインスタンスを生成
        aquestalk = new AquesTalk2();
        // AudioTrackのインスタンス生成
        track = new AudioTrack(AudioManager.STREAM_MUSIC, // 音楽再生用のオーディオストリーム
                               SAMPLING_RATE, // サンプリングレート
                               AudioFormat.CHANNEL_OUT_MONO, // モノラル
                               AudioFormat.ENCODING_PCM_16BIT, // 16bit PCM
                               bufferSize,// 合計バッファサイズ
                               AudioTrack.MODE_STREAM); // ストリームモード
        // コールバックの登録
        track.setPlaybackPositionUpdateListener(this);
    }
    
    /**
     * 音声データ名を設定する
     * @param phont 音声データ名
     */
//    public void setPhont(int phont) {
//        this.phont = phont;
//    }

    
    public void speak(String text) {
        // TODO マジックナンバーなおす
        stop();
        // 音声合成
        byte[] wav = aquestalk.syntheWav(text, 100, loadPhont());
        // エラーかどうか判定
        // 長さ1の場合エラーコードが先頭に含まれている
        if (wav.length == 1) {
            Log.d("lvn", "AquesTalk2 Synthe Error: " + wav[0]);
            Toast.makeText(context, "音声テキストはひらがなのみ受け付けます" + wav[0], Toast.LENGTH_LONG).show();
        }
        else {
            // 音声出力
            // 先頭の44バイトはWAVヘッダなので除外
            int length = wav.length - WAV_HEAD_LENGTH;
            // AudioTrackのバッファサイズを超える場合は切り捨てる
            if (length > bufferSize - SPACER) {
                length = bufferSize - SPACER;
            }
            // audioTrackのバッファに残っているデータが最後に再生されるのを防ぐ
            // 波形データの後ろに無音区間を追加して、それを含めた長さをwrite()。
            // setNotificationMarkerPosition()では、無音区間を含まない長さを指定。
            // これで、setNotificationMarkerPosition()のコールバックでstop()が遅延しても、ゴミが出力されない。
            byte[] b = new byte[length + SPACER];
            Log.d("lvn", String.valueOf(wav.length));
            Log.d("lvn", String.valueOf(length));
            System.arraycopy(wav, 44, b, 0, length);
            Arrays.fill(b, length, length + SPACER, (byte)0);
            
            // バッファを一旦読み込む必要がある
            track.reloadStaticData();
            // バッファに書き込む
            // 無音区間を含めて書きこむ
            track.write(b, 0, length + SPACER);
            // 指定の長さの再生が終わってもstatusが「PLAYING」なので指定位置の再生後にコールバックを実行する
            // 16bitデータなのでサンプル数は1/2
            track.setNotificationMarkerPosition(length / 2);
            // 再生
            track.play();
        }
    }
    
    public void stop() {
        if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
            track.stop(); // 発声中なら停止
        }
    }
    
    /**
     * 解放処理
     */
    public void destroy() {
        track.release();
    }
    
    
    
    
    
    
    /**
     * 音声データを読み込む
     * @return 音声データ
     */
    private byte[] loadPhont() {
        try {
            // リソースIDを取得
            String packageName = context.getResources().getResourcePackageName(R.raw.aq_robo);
            String typeName = context.getResources().getResourceTypeName(R.raw.aq_robo);
            int resourceId = context.getResources().getIdentifier("aq_robo", typeName, packageName);
            if (resourceId == 0) {
                return null;
            }
            // 音声データを開く
            InputStream is = context.getResources().openRawResource(resourceId);
            int size = is.available();
            byte[] phont = new byte[size];
            is.read(phont);
            return phont;
        }
        catch (IOException e) {
            Log.d("lvn", e.getMessage());
            return null;
        }
    }

    @Override
    public void onMarkerReached(AudioTrack arg0) {
        // TODO 自動生成されたメソッド・スタブ
    }
    
    /**
     * 再生完了時のコールバック
     */
    @Override
    public void onPeriodicNotification(AudioTrack track) {
        stop();
    }
}
