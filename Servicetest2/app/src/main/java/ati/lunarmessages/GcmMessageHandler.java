package ati.lunarmessages;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService
{
    private static final int NOTIFICATION = 1;
    String mes, excrept;
    private Handler handler;
    private NotificationManager myNotificationManager=null;
    private final NotificationCompat.Builder myNotificationBuilder=new NotificationCompat.Builder(this);
    public static final String CLOSE_ACTION = "close";
    public static final String TOUCH_ACTION = "touch";


    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler();
        setupNotifications();
        //showNotification();
    }

    private void setupNotifications()
    {
        if (myNotificationManager == null)
        {
            myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(TOUCH_ACTION), 0);
        PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(CLOSE_ACTION), 0);

        Resources res= this.getResources();
        myNotificationBuilder
                .setSmallIcon(R.drawable.ic_zetor_small)
                .setLargeIcon(BitmapFactory.decodeResource(res,R.drawable.app_icon_64))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setOngoing(true);
    }

    private void showNotification(String msg)
    {
        myNotificationBuilder
                .setTicker(getText(R.string.message_arrived))
                .setContentText(msg);

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
        if (myNotificationManager != null)
        {
            myNotificationManager.cancel(NOTIFICATION);
            if(mes.length()>10)
            {
                excrept=mes.substring(0, 10) + "..."; //show only excrept
            }
            showNotification(excrept);
        }
        showToast(mes);
        Log.i(Config.TAG, "Received : (" + messageType + ")  " + extras.getString("title"));
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void showToast(final String text)
    {
        handler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });

    }
}