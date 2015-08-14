package ati.lunarmessages;


import android.app.IntentService;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

public class CalUpdater extends IntentService
{

    public CalUpdater()
    {
        super("CalUpdater");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2015, 9, 14, 7, 30);

        Calendar endTime = Calendar.getInstance();
        endTime.set(2015, 9, 14, 8, 45);
        Intent eintent = new Intent(Intent.ACTION_EDIT)
                .setType("vnd.android.cursor.item/event")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , false) // just included for completeness
                .putExtra(CalendarContract.Events.TITLE, "My Awesome Event")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Heading out with friends to do something awesome.")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Earth")
                .putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=1")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                .putExtra(Intent.EXTRA_EMAIL, "my.friend@example.com")
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        String strMsgText = intent.getStringExtra("handover");
        MyPreference.setfMESSAGE(this,strMsgText);
        startActivity(eintent);
    }
}
