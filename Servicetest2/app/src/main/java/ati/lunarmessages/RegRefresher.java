package ati.lunarmessages;


import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.ArrayList;

public class RegRefresher extends IntentService
{
    private String mes;
    private Handler handler;
    GoogleCloudMessaging gcm;
    String strRegid=""; //regid from sharedpreferences
    String regid="";
    private Boolean isRegistered;
    private int regres=0;


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
        MyPreference.setNEEDSREREG(getApplicationContext(), true);
     //Check internet connection here to not destroy the service
        if (Controller.isConnectingToInternet(this))
        {
         //do rereg
            int delres=0;
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
                    MyPreference.setREGID(getApplicationContext(), "");
                    MyPreference.setNEEDSREREG(getApplicationContext(), true);
                    MyPreference.setISREGISTERED(getApplicationContext(), false);
                    msg = "Device deregistered.";
                    Log.i(Config.TAG, msg);
                    mes="Dereg";
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
                        }
                    });
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
                        MyPreference.setNEEDSREREG(getApplicationContext(),true);
                }
            }

            long backoff = Controller.BACKOFF_MILLI_SECONDS + Controller.random.nextInt(1000);
            for (int i = 1; i <= Controller.MAX_ATTEMPTS; i++)
            {
                Log.d(Config.TAG, "Attempt #" + i + " to register");
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                    regid = instanceID.getToken((Config.GOOGLE_SENDER_ID),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    Log.i(Config.TAG,regid);
                    break;
                }
                catch (IOException e)
                {
                    Log.e(Config.TAG, "Failed to register on attempt " + i + ":" + e);
                    if (i == Controller.MAX_ATTEMPTS)
                    {
                        break;
                    }
                    try
                    {
                        Log.d(Config.TAG, "Sleeping for " + backoff + " ms before retry");
                        Thread.sleep(backoff);
                    }
                    catch (InterruptedException e1)
                    {
                        // Activity finished before we complete - exit.
                        Log.d(Config.TAG, "Thread interrupted: abort remaining retries!");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }// end for
            try
            {
                if (regid!="")
                {
                //Send regid to webserver...
                    regres = Controller.register("Ati", "moonsurveyor@gmail.com", regid);
                    if(regres==1)
                    {
                        isRegistered = true;
                        strRegid=regid;
                        MyPreference.setREGID(getApplicationContext(), regid);
                        MyPreference.setNEEDSREREG(getApplicationContext(), false);
                        MyPreference.setISREGISTERED(getApplicationContext(), true);
                        mes="Reg";
                        handler.post(new Runnable()
                        {
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(regres!=1)
                    {
                        MyPreference.setNEEDSREREG(getApplicationContext(), true);
                        throw new IOException(); //regres 1 = SUCCESS
                    }
                }
                else
                {
                    regres=15; //No regid came back!!
                    throw new IOException();
                }
                msg = "Device registered, registration ID= " + regid;
                Log.i(Config.TAG, msg);
                Log.i(Config.TAG, String.valueOf(regres));

            }
            catch (IOException ex)
            {
                switch (regres)
                {
                    case 2:
                        msg="User already registered \n on the webserver!\n " +
                                "Please contact admin!";
                        break;
                    case 3:
                        msg="User details not arrived at webserver.";
                        break;
                    case 15:
                        msg="Error by registering on Google!\n" +
                                "No regid received from Google.";
                        MyPreference.setNEEDSREREG(getApplicationContext(), true);
                        break;

                    default:
                        msg = "Error :" + ex.getMessage();
                        msg+="Fatal error! \n Contact the developer!";
                        MyPreference.setNEEDSREREG(getApplicationContext(), true);
                }
                Log.i(Config.TAG, msg);
            }
            //MyPreference.setNEEDSREREG(getApplicationContext(),false);
        }
        else
        {
            MyPreference.setNEEDSREREG(getApplicationContext(),true);
        }

        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED))
        {
            Updatereceiver.completeWakefulIntent(intent);
        }
        else if (intent.getAction().equals(this.getString(R.string.from_netreceiver)))
        {
            NetReceiver.completeWakefulIntent(intent);
        }
    }

}
