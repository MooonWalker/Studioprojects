package ati.lunarmessages;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

public class Bootreceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // an Intent broadcast.
        Log.i(Config.TAG,"received");
        if(Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction()))
        {
            Toast.makeText(context,"rebooted",Toast.LENGTH_LONG).show();
        }
    }
}
