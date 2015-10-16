package ati.lunarmessages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.Date;


public class MainActivity extends AppCompatActivity
{
    static Context ctx;
    GoogleCloudMessaging gcm;
    String regid="";
    String strRegid=""; //regid from sharedpreferences
    String strMsgText=""; //message text
    int regres=0;
    public static Activity activity = null;
    private boolean isRegistered=false;
    private static final int action_coupon=5;
    private ViewPager viewPager;
    ActionBar actionBar;
    FragmentMsgs fragmentMsgs;
    FragmentRSS fragmentRSS;
    FragmentService fragmentService;
    Boolean disclaimerShowed=false;
    Boolean accepted=false;
    Boolean isFirstStart=true;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity=this;
        ctx=this;
    //Sets up the cache directory
        Config.cacheDir=Controller.getCacheFolder(this);
        Config.logFile =Controller.getLogFile(this);
        MyPreference.setCACHE_DIR(this, Config.cacheDir.toString());
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message mesg)
            {
                throw new RuntimeException();
            }
        };
    //handle disclaimer one time
        if (!MyPreference.getWELCOMESCREENSHOWN(this))
        {
        // Check if Internet present
            if (!Controller.isConnectingToInternet(this))
            {
            // Internet Connection is not present
                Controller.showAlertDialog(this,
                        "Nincs internet kapcsolat!",
                        "Csatlakoztassa, majd indítsa újra!", false);

            // stop executing code by return
                return;
            }
            handleDisclaimer(ctx);
    //wait for confirmation
            try { Looper.loop(); }
            catch(RuntimeException e2) {}
        }

        setContentView(R.layout.activity_main_tabbed);
        fragmentMsgs = new FragmentMsgs();
        fragmentRSS = new FragmentRSS();
        fragmentService = new FragmentService();

        setUpTabs(savedInstanceState);

        strMsgText=MyPreference.getfMESSAGE(this); // restore message
        isRegistered=MyPreference.getISREGISTERED(this);
        strRegid=MyPreference.getREGID(this);

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
        // stop executing code by return
        if(!chkGooglePlayservices()) return;
    }

    private void handleDisclaimer(Context ctx)
    {
        View checkboxView=View.inflate(ctx, R.layout.checkbox, null);
        CheckBox mycheckBox=(CheckBox)checkboxView.findViewById(R.id.checkBox);
        mycheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    accepted = true;
                }
                else
                {
                    accepted = false;
                }
            }
        });

        String whatsNewTitle = getResources().getString(R.string.whatsNewTitle);
        String whatsNewText = getResources().getString(R.string.whatsNewText);
        new AlertDialog.Builder(ctx)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(whatsNewTitle)
                .setMessage(whatsNewText)
                .setView(checkboxView)
                .setPositiveButton(
                        R.string.ok, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //user not accepted the terms
                                if (!accepted)
                                {
                                    // stop executing code
                                    MainActivity.activity.finish();
                                } else
                                {
                                    MyPreference.setDISCLAIMER_ACCEPTED(MainActivity.ctx, true);
                                    MyPreference.setWELCOMESCREENSHOWN(MainActivity.ctx, true);
                                    disclaimerShowed=true;
                                    handler.sendMessage(handler.obtainMessage());
                                    dialog.dismiss();
                                }
                            }
                        })
                .show();
    }

    private boolean chkGooglePlayservices()
    {
        int ret= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ret!= ConnectionResult.SUCCESS)
        {
            Log.e("statuscode", ret + "");
            if(GooglePlayServicesUtil.isUserRecoverableError(ret))
            {
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(ret, this, 1);
                if (errorDialog != null)
                {
                    errorDialog.show();
                }
                return false;
            }
            else
            {
                Toast.makeText(this, getString(R.string.toast_google_play_services_not_found)
                        , Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(!isRegistered) //nem regisztrált
        {
            // Check if Internet present
            if (!Controller.isConnectingToInternet(this))
            {
                // Internet Connection is not present
                Controller.showAlertDialog(this,
                        "Nincs internet kapcsolat!",
                        "Csatlakoztassa, majd indítsa újra!", false);
                // stop executing code by return
                return;
            }
            else
            {
                doReg(); //force register
                Toast.makeText(this, "Feliratkozás...", Toast.LENGTH_LONG).show();
            }
        }
        else //már regisztrált
        {
            //Toast.makeText(this,"Already registered", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        chkGooglePlayservices();
    //get message text
        MyPreference.getfMESSAGE(this);
    //Handle first run
        if (MyPreference.getISFIRSTSTART(this))
        {
            Date date=new Date(System.currentTimeMillis());
            long millis=date.getTime();
            MyPreference.setInstalledOn(this,millis);
            MyPreference.setISFIRSTSTART(this,false);
        }

        strRegid=MyPreference.getREGID(this);
    //put message text to fragment
        fragmentMsgs.tMsg.setText(strMsgText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    //save the selected tab's index so it's re-selected on orientation change
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
            //get and save the message from intent
                strMsgText = intent.getStringExtra("handover");
                MyPreference.setfMESSAGE(this,strMsgText);
                break;

            case GcmMessageHandler.EVENT_ACTION:
//                String notManagerName = Context.NOTIFICATION_SERVICE;
//                NotificationManager notificationManager = (NotificationManager) getSystemService(notManagerName);
//                notificationManager.cancel(ProximityAlerts.NOTIFICATION_ID_PROXIMITY_ALERT_FIRED);
                break;
            case GcmMessageHandler.CANCEL_ACTION:
                //get and save the message from intent
                strMsgText = intent.getStringExtra("handover");
                MyPreference.setfMESSAGE(this,strMsgText);
                //cancel the notification
                String notManagerName = Context.NOTIFICATION_SERVICE;
                NotificationManager notificationManager = (NotificationManager) getSystemService(notManagerName);
                notificationManager.cancel(GcmMessageHandler.NOTIFICATION);
                break;
        }
        return;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (Config.isDISCOUNTACTIVE)
        {
            menu.add(0,action_coupon,120,R.string.coupon);
        }
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
                MyPreference.setWELCOMESCREENSHOWN(this,false);
                MyPreference.setDISCLAIMER_ACCEPTED(this,false);
                doDereg();
                break;
            case action_coupon:
                Intent openCoupon = new Intent(getApplicationContext(), CouponActivity.class);
                startActivity(openCoupon);
                break;
            case R.id.action_about:
                AboutDialog about = new AboutDialog(this);
                about.setTitle("Zetor-Vas Kft.");
                about.show();
                break;
        }

        if (itemID == R.id.action_settings)
        {
            return true;
        }
        //return super.onOptionsItemSelected(item);
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
                        MyPreference.setREGID(MainActivity.ctx, "");
                        MyPreference.setNEEDSREREG(MainActivity.ctx, false);
                        msg = getString(R.string.device_unregistered);
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
                Toast.makeText(MainActivity.this, R.string.device_unregistered,Toast.LENGTH_LONG).show();
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
                        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                        regid = instanceID.getToken((Config.GOOGLE_SENDER_ID),
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                        //regid = gcm.register(Config.GOOGLE_SENDER_ID);

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
                            MyPreference.setREGID(MainActivity.ctx, regid);
                            MyPreference.setNEEDSREREG(MainActivity.ctx, false);
                            MyPreference.setISREGISTERED(ctx,true);
                        }
                        if(regres!=1)
                        {
                            MyPreference.setNEEDSREREG(MainActivity.ctx, true);
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
                            MyPreference.setNEEDSREREG(MainActivity.ctx, true);
                            break;

                        default:
                            msg = "Error :" + ex.getMessage();
                            msg+="Fatal error! \n Contact the developer!";
                            MyPreference.setNEEDSREREG(MainActivity.ctx, true);
                    }
                    Log.i(Config.TAG, msg);
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg)
            {
                //etRegId.setText(msg + "\n");
                Toast.makeText(MainActivity.this, R.string.register_successful,Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);    //new Asynctask

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
        Tab tab_three = actionBar.newTab();


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

        tab_three.setText(R.string.tab3_name)
                .setContentDescription("The third tab")
                .setTabListener(
                        new MyTabListener<FragmentService>(
                                fragmentService));

        actionBar.addTab(tab_one);
        actionBar.addTab(tab_two);
        actionBar.addTab(tab_three);

        if (savedInstanceState != null)
        {
            Log.i(Config.TAG, "setting selected tab from saved bundle");
            //  get the saved selected tab's index and set that tab as selected
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabIndex", 0));
        }
    }

    @Override
    protected void onPause()
    {
        MyPreference.setISREGISTERED(this, isRegistered);
        MyPreference.setfMESSAGE(this, strMsgText); //save the message
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        MyPreference.setISREGISTERED(this, isRegistered);
        MyPreference.setfMESSAGE(this, strMsgText); //save the message
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        MyPreference.setISREGISTERED(this, isRegistered);
        MyPreference.setfMESSAGE(this, strMsgText); //save the message
        super.onDestroy();
    }
}
