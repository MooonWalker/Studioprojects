package ati.lunarmessages;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService
{
    private static final int NOTIFICATION = 1;
    String mes, excrept;
    private NotificationManager myNotificationManager=null;
    private final NotificationCompat.Builder myNotificationBuilder=new NotificationCompat.Builder(this);
    public static final String CLOSE_ACTION = "close";
    public static final String TOUCH_ACTION = "touch";
    public static final String EVENT_ACTION = "event";
    Intent intentM;
    Intent iCal;

    public GcmMessageHandler()
    {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        //setupNotifications(" ");
    }

    private void setupNotifications(String fullMsg)
    {
        if (myNotificationManager == null)
        {
            myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        intentM = new Intent(this,MainActivity.class);
        intentM.putExtra("handover", fullMsg);
        intentM.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentM.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentM.setAction(TOUCH_ACTION);

        iCal =new Intent(this,CalUpdater.class);
        iCal.putExtra("handover", fullMsg);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentM, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piCalEvent = PendingIntent.getService(this, 0, iCal, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res= this.getResources();
        myNotificationBuilder
                .setSmallIcon(R.drawable.ic_zetor_small)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon_64))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.ic_event, getString(R.string.ntitle), piCalEvent) // API 16
                .setOngoing(true);
    }

    private void showNotification(String excr)
    {
        myNotificationBuilder
                .setTicker(getText(R.string.message_arrived))
                //.setStyle(new NotificationCompat.BigTextStyle()
                //.bigText("jio")) // API 16
                .setContentText(excr);

        if (myNotificationManager != null)
        {
            myNotificationManager.notify(NOTIFICATION, myNotificationBuilder.build());
        }
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        mes = extras.getString(Config.EXTRA_MESSAGE);
        excrept=mes;
        MyPreference.setfMESSAGE(MainActivity.ctx, mes);
        setupNotifications(mes);
        if (myNotificationManager != null)
        {
            myNotificationManager.cancel(NOTIFICATION);
            //setupNotifications(mes);
            if(mes.length()>15)
            {
                excrept=mes.substring(0, 15) + "..."; //show only excrept
            }

            showNotification(excrept);
        }
        Log.i(Config.TAG, "Received : (" + messageType + ")  " + extras.getString(Config.EXTRA_MESSAGE));
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}