package ati.lunarmessages;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class Bootreceiver extends WakefulBroadcastReceiver
{
    public Bootreceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent!=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    Toaster.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
        }
    }
}
