package ati.lunarmessages;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Toaster extends IntentService
{
    String mes;
    private Handler handler;
    public Toaster() {
        super("Toaster");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        mes = "Rebooted";
        showToast();

        Updatereceiver.completeWakefulIntent(intent);
    }

    public void showToast()
    {
        handler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });

    }
}
