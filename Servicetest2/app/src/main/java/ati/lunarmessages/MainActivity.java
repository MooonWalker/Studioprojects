package ati.lunarmessages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    public static final String PREFS_NAME = "Moon_push_settings";
    static Context ctx;
    Button btnRegId, btnDereg;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid="";
    String strRegid=""; //regid from sharedpreferences
    int regres=0;
    public static Activity activity = null;
    private boolean isRegistered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity=this;
        ctx=this;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        //PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
        //prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isRegistered=settings.getBoolean("isregistered",false);
        strRegid=settings.getString("lRegId", "");

        // Check if Internet present
        if (!Controller.isConnectingToInternet(this))
        {
            // Internet Connection is not present
            Controller.showAlertDialog(this,
                    "Internet Connection Error",
                    "Please connect to Internet connection", false);
            // stop executing code by return
            return;
        }
        // Check if GCM configuration is set
        if (Config.YOUR_SERVER_URL == null || Config.GOOGLE_SENDER_ID == null ||
                Config.YOUR_SERVER_URL.length() == 0
                || Config.GOOGLE_SENDER_ID.length() == 0)
        {

            // GCM sernder id / server url is missing
            Controller.showAlertDialog(this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);

            // stop executing code by return
            return;
        }
        setContentView(R.layout.activity_main);

        btnDereg=(Button)findViewById(R.id.btnDereg);
        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        btnRegId.setEnabled(false);
        etRegId = (EditText) findViewById(R.id.etRegId);

        if(!isRegistered)
        {
            btnRegId.setEnabled(true);
            btnDereg.setEnabled(false);
        }
        btnRegId.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                doReg();
                btnRegId.setEnabled(false);
                btnDereg.setEnabled(true);
            }
        });
        btnDereg.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                doDereg();
                btnDereg.setEnabled(false);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action == null)
        {
            return;
        }
        switch (action)
        {
            case GcmMessageHandler.CLOSE_ACTION:
                exit();
                break;
        }
    }

    private void exit()
    {
        stopService(new Intent(this, GcmMessageHandler.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("lRegId", "");
                        editor.apply();
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
                etRegId.setText(msg + "\n");
                btnRegId.setEnabled(true);
            }
        }.execute(null, null, null);
    }

    public void doReg()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void...params)
            {
                String msg = "";
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
                        regid = gcm.register(Config.GOOGLE_SENDER_ID);
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
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("lRegId", regid);
                        editor.commit();

                        regres = Controller.register("Ati", "moonsurveyor@gmail.com", regid);
                        if(regres==1)isRegistered=true;
                        if(regres!=1)throw new IOException(); //regres 1 = SUCCESS

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
                            break;

                        default:
                            msg = "Error :" + ex.getMessage();
                            msg+="Fatal error! \n Contact the developer!";
                    }
                    Log.i(Config.TAG, msg);
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg)
            {
                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);    //new Asynctask

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isregistered", isRegistered);
        // Commit the edits!
        editor.commit();
    }

}
