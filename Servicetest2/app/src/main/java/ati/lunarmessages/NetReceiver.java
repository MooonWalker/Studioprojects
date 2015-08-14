package ati.lunarmessages;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;


public class NetReceiver extends WakefulBroadcastReceiver
{
    Handler handler;
    String mes="";
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        if(intent!=null)
        {
            if (Controller.isConnectingToInternet(context))
            {
                if (MyPreference.getNEEDSREREG(context))
                {
                    //callrereg
                    // Explicitly specify that RegRefresher will handle the intent.
                    ComponentName comp = new ComponentName(context.getPackageName(),
                            RegRefresher.class.getName());
                    // Start the service, keeping the device awake while it is launching.
                    startWakefulService(context, (intent.setComponent(comp)
                            .setAction(context.getString(R.string.from_netreceiver))));
                }
                handler = new Handler();
                mes = "Networkreceiver: ";
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(context, mes + MyPreference.getNEEDSREREG(context), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                handler = new Handler();
                mes = "Networkreceiver: not connected.";
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(context, mes, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
