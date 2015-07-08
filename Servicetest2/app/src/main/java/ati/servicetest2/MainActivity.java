package ati.servicetest2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;

public class MainActivity extends Activity
{
    Button btnRegId, btnDereg;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid="";
    int regres=0;
    public static Activity activity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity=this;
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
        etRegId = (EditText) findViewById(R.id.etRegId);

        btnRegId.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                doReg();
            }
        });

        btnDereg.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                doDereg();
            }
        });
    }

    public void doDereg()
    {
        new AsyncTask<Void, Void, String>()
        {
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
                    msg = "Device deregistered.";
                    Log.i("GCM: ", msg);

                } catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                etRegId.setText(msg + "\n");
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
                        regres = Controller.register("Ati", "moonsurveyor@gmail.com", regid);
                    }
                    else
                    {
                        throw new IOException();
                    }
                    msg = "Device registered, registration ID= " + regid;
                    Log.i(Config.TAG, msg);
                    Log.i(Config.TAG, String.valueOf(regres));

                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
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

}
