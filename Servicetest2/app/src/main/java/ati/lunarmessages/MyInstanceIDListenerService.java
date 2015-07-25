package ati.lunarmessages;

import android.app.IntentService;

import com.google.android.gms.iid.InstanceIDListenerService;


public class MyInstanceIDListenerService extends InstanceIDListenerService
{
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
    }

}
