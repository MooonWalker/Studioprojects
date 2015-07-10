package ati.lunarmessages;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;


public class Updatereceiver extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        // Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                Toaster.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(MainActivity.RESULT_OK);
    }
}
