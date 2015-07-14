package ati.lunarmessages;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class RegRefresher extends IntentService
{
    private String mes;
    private Handler handler;

    public RegRefresher(){super("RegRefresher");}

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
        mes = "Updateddd";
        handler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });

//        // Remove the stored GCM registration ID
//        clearGcmRegistrationId();
//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//        String regId = gcm.register(getGcmSenderId());
//        // You should send the registration ID to your server over HTTP,
//        // so it can use GCM/HTTP or CCS to send messages to your app.
//        // The request to your server should be authenticated if your app
//        // is using accounts.
//        sendRegistrationIdToBackend(regId);
//        // store the regId locally somewhere (e.g. SharedPreferences)
//        storeGcmRegistrationId(regId);
//        // Release the wake lock provided by the
        Updatereceiver.completeWakefulIntent(intent);
    }
}
