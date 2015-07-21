package ati.lunarmessages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;

public class MainActivity extends ActionBarActivity
{
    static Context ctx;
    Button btnDereg;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid="";
    String strRegid=""; //regid from sharedpreferences
    String strMsgText=""; //message text
    int regres=0;
    public static Activity activity = null;
    private boolean isRegistered=false;
    private ViewPager viewPager;
    ActionBar actionBar;
    FragmentMsgs fragmentMsgs;
    FragmentRSS fragmentRSS;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        fragmentMsgs = new FragmentMsgs();
        fragmentRSS = new FragmentRSS();
        setUpTabs(savedInstanceState);

        activity=this;
        ctx=this;

        isRegistered=MyPreference.getISREGISTERED(this);
        strRegid=MyPreference.getREGID(this);

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

        if(!isRegistered) //nem regisztrált
        {
            doReg(); //force register
            Toast.makeText(this,"Device registered", Toast.LENGTH_LONG).show();
        }
        else //már regisztrált
        {
            Toast.makeText(this,"Already registered", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        SharedPreferences pref = this.getPreferences(0);
        //fragmentMsgs.tMsg.setText(pref.getString(MyPreference.fMESSAGE," "));
        fragmentMsgs.tMsg.setText(strMsgText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //            save the selected tab's index so it's re-selected on orientation change
        outState.putInt("tabIndex", getSupportActionBar().getSelectedNavigationIndex());
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
            case GcmMessageHandler.TOUCH_ACTION:
                strMsgText = intent.getStringExtra("handover");
                break;
        }
        return;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID=item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (itemID)
        {
            case R.id.action_unsubscribe:
                doDereg();
                break;
        }

        if (itemID == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                Toast.makeText(MainActivity.this,"A készülék leiratkozott",Toast.LENGTH_LONG).show();
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
                        //Send regid to webserver...
                        regres = Controller.register("Ati", "moonsurveyor@gmail.com", regid);
                        if(regres==1)
                        {
                            isRegistered = true;
                            MyPreference.setREGID(MainActivity.ctx, regid);
                            MyPreference.setNEEDREREG(MainActivity.ctx,false);
                        }
                        if(regres!=1)
                        {
                            MyPreference.setNEEDREREG(MainActivity.ctx,true);
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
                            MyPreference.setNEEDREREG(MainActivity.ctx,true);
                            break;

                        default:
                            msg = "Error :" + ex.getMessage();
                            msg+="Fatal error! \n Contact the developer!";
                            MyPreference.setNEEDREREG(MainActivity.ctx,true);
                    }
                    Log.i(Config.TAG, msg);
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg)
            {
                //etRegId.setText(msg + "\n");
                Toast.makeText(MainActivity.this,"A feliratkozás sikerült.",Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);    //new Asynctask

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        MyPreference.setISREGISTERED(this, isRegistered);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

    }

    private void setUpTabs(Bundle savedInstanceState)
    {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab_one = actionBar.newTab();
        Tab tab_two = actionBar.newTab();


        tab_one.setText(R.string.tab1_name)
                .setContentDescription("The first tab")
                .setTabListener(
                        new MyTabListener<FragmentMsgs>(
                                fragmentMsgs));

        tab_two.setText(R.string.tab2_name)
                .setContentDescription("The second tab")
                .setTabListener(
                        new MyTabListener<FragmentRSS>(
                                fragmentRSS));

        actionBar.addTab(tab_one);
        actionBar.addTab(tab_two);

        if (savedInstanceState != null)
        {
            Log.i(Config.TAG, "setting selected tab from saved bundle");
            //  get the saved selected tab's index and set that tab as selected
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabIndex", 0));
        }
    }
}
