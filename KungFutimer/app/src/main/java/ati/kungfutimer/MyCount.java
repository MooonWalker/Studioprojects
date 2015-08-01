package ati.kungfutimer;


import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;

//countdowntimer is an abstract class, so extend it and fill in methods


public class MyCount extends CountDownTimer
{
	MediaPlayer mediaPlayer;
	Vibrator vibrator;
	String ns= Context.NOTIFICATION_SERVICE;
	Context ctx;
	private EventNotifier en;
	
	NotificationManager mNotificationManager;
	
public MyCount(long millisInFuture, long countDownInterval) 
{
	super(millisInFuture, countDownInterval);
	
}

public MyCount(long millisInFuture, long countDownInterval, Context cont)
{
	super(millisInFuture, countDownInterval);
	ctx= cont;
	en=new EventNotifier((MycountFinished) ctx);
	
}

@Override
public void onTick(long millisUntilFinished) 
{
	MainActivity.setBrewingtime(millisUntilFinished);
	
	//MainActivity.tw1.setText(""+millisUntilFinished/1000);
	MainActivity.tw1.setText(Textformatter.MillisToMinSec(millisUntilFinished));

}

public void stopMusic()
{
	if(mediaPlayer!=null)
	{
		mediaPlayer.stop();
	}
}


@Override
public void onFinish() 
{
	
	doFinish();
}

public void doFinish()
{
	MainActivity.btimehist=SystemClock.uptimeMillis()-MainActivity.btimehist;
	Boolean vibrate = MainActivity.prefs.getBoolean("vibrate", false);
	String uristring = "android.resource://ati.kungfutimer/raw/";
	Uri uri= null;
	
	String sound=MainActivity.prefs.getString("sound", "1");
	switch (Integer.parseInt(sound))
	{
		case 1:
			uri = Uri.parse(uristring+"ding");
			break;
	
		case 2:
			uri = Uri.parse(uristring+"sulik");
			break;
			
		case 3:
			uri = Uri.parse(uristring+"electra");
			break;
			
		case 4:
			uri = Uri.parse(uristring+"flute1");
			break;
			
		case 5:
			uri = Uri.parse(uristring+"flute2");
			break;
			
		case 6:
			uri = Uri.parse(uristring+"bowl");
			break;
			
		case 7:
			uri = Uri.parse(uristring+"guzheng");
			break;
			
		case 8:
			uri = Uri.parse(uristring+"medieval");
			break;
	}

	if(uri!=null)
	{
		mediaPlayer = MediaPlayer.create(ctx, uri);		
		mediaPlayer.start();
	}
	
	if(vibrate)
	{
		vibrator = (Vibrator) ctx.getSystemService(ctx.VIBRATOR_SERVICE);
		vibrator.vibrate(1100);
	}
	
	
	MainActivity.btn1.setText("Start");	
	MainActivity.timerstarted = false;
	
	if(MainActivity.kungfubrewing)
	{
		
		if(!MainActivity.session.isLastBrew())
		{
			en.doWork();		//write history
			MainActivity.session.setNextBrew();
			MainActivity.setBrewingtime(MainActivity.session.getBrewingtime(MainActivity.session.getBrewidx()));
			MainActivity.tw1.setText(Textformatter.MillisToMinSec(
					MainActivity.session.getBrewingtime(MainActivity.session.getBrewidx())));
			
			MainActivity.kannasor.getChildAt(MainActivity.session.getBrewidx()-1).setBackgroundColor(0xFFDC2F0E);
			MainActivity.kannasor.getChildAt(MainActivity.session.getBrewidx()-2).setBackgroundColor(Color.TRANSPARENT);
		}
		else		//session ended			
		{
			en.doWork();		//write history
			MainActivity.tw1.setTextSize(30);
			MainActivity.tw1.setText("Session ended!");
			MainActivity.kannasor.getChildAt(MainActivity.session.getBrewidx()-1).setBackgroundColor(Color.TRANSPARENT);
			//MainActivity.row.setBackgroundColor(Color.BLACK);
			//MainActivity.tealist.getChildAt(MainActivity.session.getTealistpos()).setBackgroundColor(Color.BLACK);
			MainActivity.makeTransparent();
			MainActivity.btn1.setEnabled(false);
			MainActivity.btn2.setEnabled(false);
			MainActivity.btn3.setEnabled(false);
			MainActivity.session.stop();
		}
		
		
		
	}
	else		//non kungfutimer
	{
		MainActivity.tw1.setTextSize(30);
		MainActivity.tw1.setText("Your tea is ready!");
	}
}



}
