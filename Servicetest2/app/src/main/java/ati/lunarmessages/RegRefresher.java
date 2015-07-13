package ati.lunarmessages;


import android.app.IntentService;
import android.content.Intent;

public class RegRefresher extends IntentService
{
    public RegRefresher(){super("RegRefresher");}

    @Override
    protected void onHandleIntent(Intent intent)
    {
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
