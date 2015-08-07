package ati.lunarmessages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        Thread timer = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(1700);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent openMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(openMainActivity);
                }
            }//run
        }; //Thread
        timer.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();

    }
}
