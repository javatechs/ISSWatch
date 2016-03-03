package com.belithco.iss.isswatch;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.belithco.iss.isswatch.sensor.OpenNotify;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This service uses a worker thread and thus does not run on the UI thread.
 */
public class ISSLocationService extends IntentService {
    private static final String TAG = ISSMapActivity.ISSWATCH + ISSLocationService.class.getSimpleName();

    // We start after 11 seconds in order to let the UI Settle
    private static final int START_AFTER_MS = 1 * 1000;
    //
    private static final int SYNC_INTERVAL_MS = 5 * 1000;
    // Inbound from alarm
    public static final String MESSENGER = "messenger";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    /**
     * Contains the request and response. These are stored in an object in order
     * to send them back to the UI handler for display.
     */
    public static class RequestResponse {
        // The URL request sent to the cloud service server
        public String send = "";
        // The response from the cloud service
        public String receive = "";
    }

    public ISSLocationService() {
        // We provide the name here so that the worker thread will be named and
        // thus easy to identify during debugging
        super(ISSLocationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve a map of extended data from the intent
        Bundle requestBundle = intent.getExtras();
        if (requestBundle != null) {
            // Create request/response capture object
            RequestResponse rr = new RequestResponse();
            // Get weather and save to Sprout DB
            String currentLocation = new OpenNotify().getJSonData();
            // Send update to UI, if available
            Messenger messenger = (Messenger) requestBundle.get(MESSENGER);
            if (null != messenger) {
                // Create a map of extended data for the UI to display
                Bundle responseBundle = new Bundle();
                // Create the message which wraps the bundle of data for the UI
                // to display
                Message msg = Message.obtain();

                try {
                    JSONObject jObj = new JSONObject(currentLocation);
                    jObj = jObj.getJSONObject("iss_position");
                    double latitude = jObj.getDouble("latitude");
                    double longitude = jObj.getDouble("longitude");
                    responseBundle.putDouble(LATITUDE, latitude);
                    responseBundle.putDouble(LONGITUDE, longitude);
                    msg.setData(responseBundle);
                    messenger.send(msg);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse object.", e);
                } catch (RemoteException e) {
                    Log.e(TAG, "Could not pass message to UI", e);
                }
            }
        }
    }

    /**
     * This method starts a repeating alarm. When the alarm happens,query the
     * current location API.
     *
     * @param context Required to manage alarms
     */
    public static void startServiceTriggering(Context context) {
        Log.d(TAG, "startService()");
        // Get alarm manager
        AlarmManager alarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        // Create an intent for the alarm receiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        // Stop any existing alarm
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,
                intent, 0);
        alarmMgr.cancel(alarmIntent);
        // Start repeating alarm which is the start of the current location query
        Log.d(TAG, "Repeating. Start after ms=" + START_AFTER_MS
                + "  Sync interval=" + SYNC_INTERVAL_MS);
//        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime() + START_AFTER_MS,
                SYNC_INTERVAL_MS, alarmIntent);
    }
}
