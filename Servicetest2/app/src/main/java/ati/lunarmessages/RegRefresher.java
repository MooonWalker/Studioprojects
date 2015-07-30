package ati.lunarmessages;


import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class RegRefresher extends IntentService
{
    private String mes;
    private Handler handler;
    GoogleCloudMessaging gcm;
    String strRegid=""; //regid from sharedpreferences
    private Boolean isRegistered;

    public RegRefresher()
    {
        super("RegRefresher");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler();
        isRegistered=MyPreference.getISREGISTERED(this);
        strRegid=MyPreference.getREGID(this);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        mes = "Updated";
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
    public void doDereg()
    {
        new AsyncTask<Void, Void, String>()
        {
            int delres=0;
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcm.unregister();
                    delres = Controller.deregister("Ati", "moonsurveyor@gmail.com", strRegid);
                    if(delres==10)
                    {
                        isRegistered = false;
                        MyPreference.setREGID(MainActivity.ctx,"");
                        MyPreference.setNEEDREREG(MainActivity.ctx,false);
                        msg = "Device deregistered.";
                        Log.i(Config.TAG, msg);
                    }
                    if(delres!=10)throw new IOException(); //del 10 = SUCCESS
                }
                catch (IOException ex)
                {
                    switch (delres)
                    {
                        case 20:
                            msg="User could not be deleted from db.\n " +
                                    "Please contact admin!";
                            break;
                        case 30:
                            msg="RegID not arrived to webserver.";
                            break;

                        default:
                            msg = "Error :" + ex.getMessage();
                            msg+="Fatal error! \n Contact the developer!";
                    }
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                // etRegId.setText(msg + "\n");
                Toast.makeText(RegRefresher.this,"Leiratkozott",Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

}
