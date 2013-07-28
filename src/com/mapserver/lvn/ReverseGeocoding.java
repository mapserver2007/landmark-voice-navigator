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

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
    /** コンテキスト */
    private Context context;
    /** コンテナ */
    private ReverseGeocodingContainer container;
    
    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param container コンテナ
     */
    public ReverseGeocoding(Context context, ReverseGeocodingContainer container) {
        this.context = context;
        this.container = container;
    }
    
//    /**
//     * 逆ジオコーディングを実行する
//     */
//    protected void execute(String lng, String lat) {
//        execute(String.valueOf(lng), String.valueOf(lat));
//    }
    
    /**
     * 住所を取得する
     * @param appId APPキー
     * @param lng 経度
     * @param lat 緯度
     * @return
     */
    private String getAddress(String appId, String lng, String lat) {
        Log.d("lvn", "Converting position to address");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
               .encodedAuthority("reverse.search.olp.yahooapis.jp")
               .path("OpenLocalPlatform/V1/reverseGeoCoder")
               .appendQueryParameter("appid", appId)
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
     * @param appId APPキー
     * @param address 住所
     * @return 住所ひらがな
     * @throws IOException
     */
    private String getAddressHiragana(String appId, String address) throws IOException {
        Log.d("lvn", "Converting address to address of hiragana");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
               .encodedAuthority("jlp.yahooapis.jp")
               .path("FuriganaService/V1/furigana")
               .appendQueryParameter("appid", appId)
               .appendQueryParameter("sentence", address)
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
                if (eventType == XmlPullParser.START_TAG && 
                        xpp.getDepth() == 5 && "Furigana".equals(xpp.getName())) {
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
            Log.d("lvn", e.getMessage());
        }
        catch (Exception e) {
            Log.d("lvn", e.getMessage());
        }
        finally {
            is.close();
            httpClient.getConnectionManager().shutdown();
        }
        
        return sb.toString();
    }
    @Override
    protected String doInBackground(String... query) {
        String lng = query[0],
               lat = query[1],
               appId = container.getAppId();
        try {
            // 住所を取得
            String address = getAddress(appId, lng, lat);
            // 住所が変わっていなければひらがな変換は実行しない
            if (address != null && address.equals(container.getAddress())) {
                return STATUS_STAY;
            }
            container.setAddress(address);
            // ひらがなに変換
            String addressHiragana = getAddressHiragana(appId, address);
            container.setAddressHiragana(addressHiragana);
        }
        catch (IOException e) {
            Log.e("lvn", e.getMessage());
        }
        
        return STATUS_MOVED;
    }
    
    @Override
    protected void onPostExecute(String status) {
        if (STATUS_MOVED.equals(status)) {
            ((MainActivity) context).onAutoSpeak(container);
        }
    }
}
