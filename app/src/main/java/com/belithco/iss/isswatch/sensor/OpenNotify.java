package com.belithco.iss.isswatch.sensor;

import android.util.Log;

import com.belithco.iss.isswatch.ISSMapActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;


/**
 * Open Notify API for International Space Station data.
 */
public class OpenNotify extends  _CloudTemplate {
    private static final String TAG = ISSMapActivity.ISSWATCH + OpenNotify.class.getSimpleName();

    public String getJSonData(){
        String result = null;

        // Prepare the URL
        String url = "http://api.open-notify.org/iss-now.json";

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        // Execute the request. We can do this on this thread since it is a
        // background, not UI thread.
        InputStream instream = null;
        try {
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            if (entity != null) {
                // Read the JSON
                instream = entity.getContent();
                result = convertStreamToString(instream);
                // result = string version of HTML response
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if (null != instream) {
                try {
                    instream.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return result;
    }
}
