package ati.lunarmessages;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GcmMessageHandler extends com.google.android.gms.gcm.GcmListenerService
{
    private static final int NOTIFICATION = 1;
    String mes, excrept;
    private NotificationManager myNotificationManager=null;
    private final NotificationCompat.Builder myNotificationBuilder=new NotificationCompat.Builder(this);
    public static final String CLOSE_ACTION = "close";
    public static final String TOUCH_ACTION = "touch";
    public static final String EVENT_ACTION = "event";
    Intent intentM;
    Intent newCal;

 //   public GcmMessageHandler()
 //   {
 //       super("GcmMessageHandler");
 //   }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    private String setupNotifications(String fullMsg)
    {
        if (myNotificationManager == null)
        {
            myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        Resources res= this.getResources();

        if(fullMsg.substring(0,3).equals("[e]"))
        {
            //get the datetime part 2015.07.31. 20:51
            String dateTime = fullMsg.substring(fullMsg.indexOf("[e]")+3,fullMsg.indexOf("[/e]"));
            //Parse datetime string to integers
            EventData eventData=new EventData(dateTime);
            //get the real message text
            fullMsg=fullMsg.substring(fullMsg.indexOf("[/e]")+4);


            intentM = new Intent(this,MainActivity.class);
            intentM.putExtra("handover", fullMsg);
            intentM.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentM.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intentM.setAction(TOUCH_ACTION);

            Calendar beginTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();
            beginTime.set(eventData.getYEAR(),eventData.getMONTH()
                            ,eventData.getDAY(),eventData.getHOUR()
                            ,eventData.getMINUTE());
            endTime.set(eventData.getYEAR(),eventData.getMONTH()
                            ,eventData.getDAY(),eventData.getHOUR()+1
                            ,eventData.getMINUTE());

            newCal = new Intent(Intent.ACTION_EDIT)
                    .setType("vnd.android.cursor.item/event")
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , false) // just included for completeness
                    .putExtra(CalendarContract.Events.TITLE, getString(R.string.caleventtitle))
                    .putExtra(CalendarContract.Events.DESCRIPTION, fullMsg)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Earth")
                    .putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=10")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                    .putExtra(Intent.EXTRA_EMAIL, "my.friend@example.com")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentM, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent piCalEvent = PendingIntent.getActivity(this, 0, newCal, PendingIntent.FLAG_UPDATE_CURRENT);
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
            return fullMsg;
        }
        else
        {
            intentM = new Intent(this,MainActivity.class);
            intentM.putExtra("handover", fullMsg);
            intentM.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentM.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intentM.setAction(TOUCH_ACTION);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentM, PendingIntent.FLAG_UPDATE_CURRENT);

            myNotificationBuilder
                    .setSmallIcon(R.drawable.ic_zetor_small)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon_64))
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentTitle(getText(R.string.app_name))
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOngoing(true);
            return fullMsg;
        }
    }

    private void setupCal()
    {

    }
    private void showNotification(String excr)
    {
        myNotificationBuilder
                .setTicker(getText(R.string.message_arrived))
                .setContentText(excr);

        if (myNotificationManager != null)
        {
            myNotificationManager.notify(NOTIFICATION, myNotificationBuilder.build());
        }
    }
    @Override
    public void onMessageReceived(String from, Bundle data)
    {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        //String messageType = gcm.getMessageType(intent);
        //mes = extras.getString(Config.EXTRA_MESSAGE);
        if (data!=null)
        {
            mes = data.getString("message");
        }
        try
        {
            if (mes.length()!=0)
            {
                mes = setupNotifications(mes);
            }
            else mes = "Üres üzenet!";

            excrept = mes;
            MyPreference.setfMESSAGE(this, mes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (myNotificationManager != null)
        {
            myNotificationManager.cancel(NOTIFICATION);
            if(mes.length()>15)
            {
                excrept=mes.substring(0, 15) + "..."; //show only excrept
            }
            showNotification(excrept);
        }
        Log.i(Config.TAG, "Received : " + data.getString(Config.EXTRA_MESSAGE));
        //GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}