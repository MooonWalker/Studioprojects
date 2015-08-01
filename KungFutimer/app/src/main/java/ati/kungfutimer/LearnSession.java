package ati.kungfutimer;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class LearnSession extends Activity implements View.OnClickListener
{
	private static Context ctx;
	
	private String tmp="", temp="", temp1="";
	private static  WakeLock mWakeLock;
	private Tea tea;
	private int teaID;
	private LearningSession newsession;
	private Dialog mydialog;
	private DatabaseHandler db;
	private Bundle gotBasket;
	private Button btnStartStop, btnSave, btnmin, btnplus;
	private TextView twTeaName, tBrewings;
	private long brewingtime;
	private Chronometer chr1;
	private long elapsedmillis=0, time;
	private boolean flag=false;
	
	public static Context getCtx()
	{
		return ctx;
	}

	public static void setCtx(Context ctx)
	{
		LearnSession.ctx = ctx;
	}
    //=====================================================================================
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_session);
        
        initActivity();
        
        
        btnStartStop.setOnClickListener((OnClickListener) ctx);        
        btnSave.setOnClickListener((OnClickListener)ctx);
        btnmin.setOnClickListener((OnClickListener)ctx);
        btnplus.setOnClickListener((OnClickListener)ctx);
        
    }

	private void initActivity()
	{
		setCtx(LearnSession.this);
		tea=new Tea();
		new Brewing();
		chr1=new Chronometer(ctx);
		db=new DatabaseHandler(LearnSession.this);
		btnStartStop = new Button(LearnSession.this);
		btnSave=new Button(LearnSession.this);
		btnmin=new Button(LearnSession.this);
		btnplus=new Button(LearnSession.this);
		twTeaName = new TextView(LearnSession.this);
		tBrewings=new TextView(LearnSession.this);
		
		chr1=(Chronometer)findViewById(R.id.chronometer1);
		twTeaName = (TextView)findViewById(R.id.txtTeaname);
		tBrewings=(TextView)findViewById(R.id.tBrewValues);
		
		time = SystemClock.elapsedRealtime()-chr1.getBase();
		btnSave =(Button)findViewById(R.id.button11);
		btnStartStop=(Button)findViewById(R.id.button12);
		btnmin=(Button)findViewById(R.id.btnmin1);
		btnplus=(Button)findViewById(R.id.btnplus1);
		
		btnmin.setVisibility(4);
		btnplus.setVisibility(4);
		gotBasket = getIntent().getExtras();
		if(MainActivity.prefs.getBoolean("stayawake", false)) setWakelock();
		
		tea=db.getTea(gotBasket.getInt("teaID"));
        db.close();
        
        teaID= tea.getID();
        twTeaName.setText(tea.getName());
        newsession= new LearningSession(teaID);
        
	}

	public void onClick(View v)
	{
		if (v.getId() == R.id.button12)				//start/stop
		{
			if (!newsession.isStopperStarted())
			{
				if(flag)
				{
					newsession.addNewBrew(elapsedmillis);					
					flag=false;
				}
				
				btnStartStop.setText("Stop");
				btnmin.setVisibility(4);
				btnplus.setVisibility(4);
				chr1.setBase(SystemClock.elapsedRealtime());
				chr1.start();
				time=SystemClock.elapsedRealtime();
				
				newsession.setStopperStarted(true);
				
//				if (session != null)
//				{
//					
//					if(session.getBrewidx()==1)
//					{
//						session.start();
//						kannasor.getChildAt(session.getBrewidx()-1).setBackgroundColor(Color.RED);
//					}
//					else if (session.getBrewidx()>1)
//					{
//						kannasor.getChildAt(session.getBrewidx()-2).setBackgroundColor(Color.TRANSPARENT);
//					}
//					
//					
//				}
				
			}
			else
			{
				elapsedmillis= SystemClock.elapsedRealtime()-chr1.getBase();
				chr1.stop();
				flag=true;
				//(time - chr1.getBase()
				
				
				btnStartStop.setText("Start");
				btnmin.setVisibility(0);
				btnplus.setVisibility(0);
				tmp = Textformatter.MillisToMinSec(elapsedmillis);
				temp=tmp;
				temp1=tmp;
				tBrewings.setText(tBrewings.getText()+tmp+"; ");
				
				newsession.setStopperStarted(false);
				
			}
		} 
		else if (v.getId() == R.id.button11)
		{
			newsession.addNewBrew(elapsedmillis);
			if(newsession.getBrewings().size()!=0)
			{
				  mydialog = new Dialog(ctx, R.style.PauseDialog);
				  mydialog.setContentView(R.layout.confirmdialog);
				  mydialog.setTitle("Overwriting brews!");
				  mydialog.setCancelable(true);
				  Button btnCancel=(Button)mydialog.findViewById(R.id.btncanel2);
				  Button btnDelete=(Button)mydialog.findViewById(R.id.deletetea);
				  btnDelete.setText("Yes, overwrite!");
				  btnDelete.setOnClickListener(new View.OnClickListener()
				  {
					
					public void onClick(View v)
					{
										
						db.deleteBrewings(teaID);
						String res= db.addBrewings(newsession.getBrewings());
						if (!res.equals("")) Toast.makeText 					//error
						(ctx, res, Toast.LENGTH_LONG).show();
						
						Toast.makeText(ctx, ""+newsession.getBrewings().size()+" brewings added", Toast.LENGTH_SHORT).show();
						db.close();
						mydialog.dismiss();
						((Activity) ctx).finish();
					}
				  });
				  
				  btnCancel.setOnClickListener(new View.OnClickListener()
				  {
					
					public void onClick(View v)
					{
						mydialog.dismiss();
					}
				  });

				  mydialog.show();
				  
				  
				
			}
		}
		else if (v.getId() == R.id.btnmin1)
		{
			if (elapsedmillis>=1000)
			{
				elapsedmillis=elapsedmillis-1000;
			}
			
			chr1.setText(Textformatter.MillisToMinSec(elapsedmillis));
			tBrewings.setText(tBrewings.getText().toString().replace(temp, Textformatter.MillisToMinSec(elapsedmillis)));
			temp=Textformatter.MillisToMinSec(elapsedmillis);
		}
		else if (v.getId() == R.id.btnplus1)
		{
			elapsedmillis=elapsedmillis+1000;
			chr1.setText(Textformatter.MillisToMinSec(elapsedmillis));
			tBrewings.setText(tBrewings.getText().toString().replace(temp, Textformatter.MillisToMinSec(elapsedmillis)));
			temp=Textformatter.MillisToMinSec(elapsedmillis);
		}
	}
	
	public static void setWakelock()
	{
		mWakeLock = ((PowerManager) ctx.getSystemService(ctx.POWER_SERVICE))		
			    .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, ctx.getClass().getName());		
		mWakeLock.acquire();
		
	}
	public static void deleteWakeLock()
	{
		if (mWakeLock!=null)
		{
			mWakeLock.release();
			mWakeLock=null;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if(mWakeLock!=null)
		{
			mWakeLock.release();
			mWakeLock=null;
		}
	}
	
}
