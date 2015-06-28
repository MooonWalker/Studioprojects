package ati.servicetest2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    String regid;
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
        setContentView(R.layout.activity_main);

        btnDereg=(Button)findViewById(R.id.btnDereg);
        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        etRegId = (EditText) findViewById(R.id.etRegId);

        btnRegId.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                getRegId();
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

    public void getRegId()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(Config.GOOGLE_SENDER_ID);
                    msg = "Device registered, registration ID= " + regid;
                    Log.i("GCM", msg);

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


}
