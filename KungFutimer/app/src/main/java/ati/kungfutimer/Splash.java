package ati.kungfutimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class Splash extends Activity
{
	public Splash()
	{
		super();
	}

	@Override
	protected void onCreate(Bundle Kakamatyi) 
	{		
		super.onCreate(Kakamatyi);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.splash);
		
		Thread timer = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(1300);				
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				finally				
				{
					Intent openStartingPoint = new Intent("com.test.savaz.MAINACTIVITY");
					startActivity(openStartingPoint);
					
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
