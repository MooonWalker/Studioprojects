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
        if(intent!=null && intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED))
        {
           // if(intent.getData().getSchemeSpecificPart().equals(context.getPackageName()))
           // {
                // Explicitly specify that RegRefresher will handle the intent.
                ComponentName comp = new ComponentName(context.getPackageName(),
                        RegRefresher.class.getName());
                // Start the service, keeping the device awake while it is launching.
                startWakefulService(context, (intent.setComponent(comp)));
                //setResultCode(MainActivity.RESULT_OK);
            //}
        }

    }
}
