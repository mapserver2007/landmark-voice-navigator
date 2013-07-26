package com.mapserver.lvn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * 逆ジオコーディングクラス
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class ReverseGeocoding extends AsyncTask<String, Void, String> {
    /** UserAgent */
    private static final String UA = "Mozilla/5.0 (Linux; U; Android 4.0.1; ja-jp; Galaxy Nexus Build/ITL41D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    /** ステータス */
    private static final String STATUS_STAY = "stay";
    private static final String STATUS_MOVED = "moved";
    /** View */
    private Activity activity;
    
    private String appid;
    
    private String address;
    private String addressHiragana;
    
    /**
     * コンストラクタ
     * @param lng 経度
     * @param lat 緯度
     */
    public ReverseGeocoding(Activity activity) {
        appid = "";
        this.activity = activity;
    }
    
    /**
     * 緯度経度をdouble型で指定して実行する
     * @param lng 経度
     * @param lat 緯度
     */
    protected void execute(double lng, double lat) {
        // 前回取得した住所を取得
        TextView elemAddress = (TextView) activity.findViewById(R.id.showAddress);
        TextView elemAddressHiragana = (TextView) activity.findViewById(R.id.showAddressHiragana);
        this.address = elemAddress.getText().toString();
        this.addressHiragana = elemAddressHiragana.getText().toString();
        // 変換処理実行
        execute(String.valueOf(lng), String.valueOf(lat));
    }
    
    /**
     * 住所を取得する
     * @param String リクエストパラメータ
     * @return 住所
     */
    private String getAddress(String lng, String lat) {
        Log.d("lvn", "Converting position to address");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
               .encodedAuthority("reverse.search.olp.yahooapis.jp")
               .path("OpenLocalPlatform/V1/reverseGeoCoder")
               .appendQueryParameter("appid", appid)
               .appendQueryParameter("lat", lat)
               .appendQueryParameter("lon", lng)
               .appendQueryParameter("output", "json");
        
        HttpGet request = new HttpGet(builder.build().toString());
        request.setHeader("Connection", "Keep-Alive");
        request.setHeader("UserAgent", UA);
        
        // DefaultHttpClientでないとCookieが使えないなど不具合が多い
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String address = null;
        try {
            HttpResponse response = httpClient.execute(request);
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            // results
            address = json.getJSONArray("Feature")
                          .getJSONObject(0)
                          .getJSONObject("Property")
                          .get("Address")
                          .toString();
            // 半角数字を漢数字に置換
            address = LvnUtils.deleteStreet(address);
        }
        catch (IOException e) {
            Log.d("lvn", e.getMessage());
        }
        catch (ParseException e) {
            Log.d("lvn", e.getMessage());
        }
        catch (JSONException e) {
            Log.d("lvn", e.getMessage());
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
        
        return address;
    }
    
    /**
     * 漢字表記の住所をひらがなに変換する
     * @param text 漢字を含む住所
     * @return ひらがな(ただし町名以降の丁目は含まない)
     * @throws IOException
     */
    private String getAddressHiragana(String text) throws IOException {
        Log.d("lvn", "Converting address to address of hiragana");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
               .encodedAuthority("jlp.yahooapis.jp")
               .path("FuriganaService/V1/furigana")
               .appendQueryParameter("appid", appid)
               .appendQueryParameter("sentence", text)
               .appendQueryParameter("grade", "1");
        
        HttpGet request = new HttpGet(builder.build().toString());
        request.setHeader("Connection", "Keep-Alive");
        request.setHeader("UserAgent", UA);
        
        // DefaultHttpClientでないとCookieが使えないなど不具合が多い
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            HttpResponse response = httpClient.execute(request);
            String xmlText = EntityUtils.toString(response.getEntity(), "UTF-8");
            is = new ByteArrayInputStream(xmlText.getBytes());
            
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, "UTF-8");
            
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // 対象のタグを見つけたとき
                if (eventType == XmlPullParser.START_TAG && "Furigana".equals(xpp.getName())) {
                    // 要素を取得
                    if (xpp.next() == XmlPullParser.TEXT) {
                        sb.append(xpp.getText());
                    }
                }
                eventType = xpp.next();
            }
        }
        catch (IOException e) {
            Log.d("lvn", e.getMessage());
        }
        catch (ParseException e) {
            Log.d("lvn", e.getMessage());
        }
        catch (XmlPullParserException e) {
            Log.d("lvn", "owata");
            Log.d("lvn", e.getMessage());
        }
        finally {
            is.close();
            httpClient.getConnectionManager().shutdown();
        }
        
        return sb.toString();
    }
    
    /**
     * Activityに結果を表示する
     * @param id 要素ID
     * @param text 表示するテキスト
     */
    private void showResult(int id, String text) {
        if (activity != null) {
            TextView elem = (TextView) activity.findViewById(id);
            elem.setText(text);
        }
    }

    @Override
    protected String doInBackground(String... query) {
        try {
            // 住所を取得
            String address = getAddress(query[0], query[1]);
            // 住所が変わっていなければひらがな変換は実行しない
            if (address != null && address.equals(this.address)) {
                return STATUS_STAY;
            }
            this.address = address;
            // ひらがなに変換
            addressHiragana = getAddressHiragana(address);
        }
        catch (IOException e) {
            Log.e("lvn", e.getMessage());
        }
        
        return STATUS_MOVED;
    }
    
    @Override
    protected void onPostExecute(String status) {
        showResult(R.id.showStatus, STATUS_STAY);
        if (STATUS_MOVED.equals(status)) {
            showResult(R.id.showAddress, address);
            showResult(R.id.showAddressHiragana, addressHiragana);
            ((MainActivity) activity).onAutoSpeak(addressHiragana);
        }
    }
}
