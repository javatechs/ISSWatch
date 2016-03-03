package com.belithco.iss.isswatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

/**
 * This class is a broadcast receiver. It queues up one request to the ISS
 * service to get the current observation. It is triggered by an alarm. The
 * map activity handler is passed to the service as part of the intent. This
 * handler runs in the UI thread.
 *
 * The service runs on a background thread. Since ONLY the UI thread can update
 * the UI, the service sends current observation to the handler which displays
 * the data on the UI.
 *
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = ISSMapActivity.ISSWATCH + AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Rec'd alarm.");
        // Create the a weather service intent
        Intent msgIntent = new Intent(context, ISSLocationService.class);
        // The handler should exist. It is part of the UI. If for some reason it
        // does not, e.g. Android shut down the UI, do not attempt to display
        // results.
        if (null != ISSMapActivity.handler) {
            msgIntent.putExtra(ISSLocationService.MESSENGER, new Messenger(
                    ISSMapActivity.handler));
        }
        // Start our weather service.
        context.startService(msgIntent);
    }
}
