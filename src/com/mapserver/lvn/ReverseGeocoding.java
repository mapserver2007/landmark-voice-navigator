package com.mapserver.lvn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class ReverseGeocoding extends AsyncTask<Uri.Builder, Void, String> {
    /** API URL */
    private static final String HOST = "reverse.search.olp.yahooapis.jp";
    private static final String PATH = "OpenLocalPlatform/V1/reverseGeoCoder";
    /** UserAgent */
    private static final String UA = "Mozilla/5.0 (Linux; U; Android 4.0.1; ja-jp; Galaxy Nexus Build/ITL41D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    /** コンテキスト */
    private Context context;
    /** 軽度 */
    private String lng;
    /** 緯度 */
    private String lat;
    
    /**
     * コンストラクタ
     * @param lng 軽度
     * @param lat 緯度
     */
    public ReverseGeocoding(Context context) {
        this.context = context;
    }
    
    public void setLng(double lng) {
        this.lng = String.valueOf(lng);
    }
    
    public void setLat(double lat) {
        this.lat = String.valueOf(lat);
    }
    
    private String convert() {
        return null;
    }
    
    protected void execute() {
        // TODO あとで隠す
        String appid = "";
        
        
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
               .encodedAuthority(HOST)
               .path(PATH)
               .appendQueryParameter("appid", appid)
               .appendQueryParameter("lat", lat)
               .appendQueryParameter("lon", lng)
               .appendQueryParameter("output", "json");
        
        execute(builder);
    }
    
    /**
     * 住所を取得する
     * @param builder リクエストパラメータ
     * @return 住所
     */
    private String getAddress(Uri.Builder builder) {
        HttpGet request = new HttpGet(builder.build().toString());
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
        
        return address;
    }

    @Override
    protected String doInBackground(Builder... builder) {
        return getAddress(builder[0]);
    }
    
    @Override
    protected void onPostExecute(String result) {
        // TODO
        Log.d("lvn", result);
    }
    
    
}
