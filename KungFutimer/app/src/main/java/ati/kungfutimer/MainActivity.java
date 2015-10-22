package ati.kungfutimer;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements MycountFinished, View.OnClickListener, OnClickListener, OnTouchListener, OnPreferenceChangeListener
{
	//public static TextView tw1;
  
	public static final String PREFS_NAME = "Kungfusettings";
	public static TextView tw1;
	public static  Button btn1, btn2, btn3;
	public static ToggleButton  tglbtn1;
	LinearLayout root;
	static LinearLayout kannasor;
	public static ImageView kiskanna;
	public CountDownTimer stopw1;
	public MyCount counter;
	public static ListView tealist;
	
	public static boolean timerstarted = false, kungfubrewing= false;
	boolean sendstat, vibrate, stopperstarted=false;
	public static boolean displaynotice=false;
	public static boolean[] isSelected;
	
	static long brewingtime =0;          //milliseconds
	public static long btimehist=0;
	long btimesave = 0;
	long btimesavedforsettings =0;
	Dialog mydialog;
	DatabaseHandler db, db1;
	public static TeaAdapter adapter;
	public static Teasession session;
	public static SharedPreferences prefs;
	static int teaid=0, brewidx =1;
	public static View row = null, saveselection =null, arg1backup=null;
	LinearLayout.LayoutParams vp;
	PotSpawner spawner;
	static Context ctx;
	static WakeLock mWakeLock;
	public Tea teasavas;
	private String sendWasCorrect="false";
	
	
	
//================================================================================7
	
	public long getBrewingtime()
	{
		return brewingtime;
	}

	public static void setBrewingtime(long _brewingtime)
	{
		brewingtime = _brewingtime;
	}
//==================================================================================	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		 ctx=this;
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    
	    PreferenceManager.setDefaultValues(ctx, R.xml.prefs, true);
	    prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    vibrate = prefs.getBoolean("vibrate", false);
	    sendstat = prefs.getBoolean("sendstat", true);
	    displaynotice = prefs.getBoolean("displaynotice", false);
	    brewingtime = settings.getLong("brewingtime", 127000);
	    kungfubrewing = settings.getBoolean("kungfubrewing", false);
	    
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.activity_main);
        
        new EventNotifier(this);
	
        initactivity();
        
        if(sendstat && hasConnection())sendstat();        
        
        tglbtn1.setChecked(settings.getBoolean("tglbtn", false));			//restore prev.state...        
		if (settings.getBoolean("kungfubrewing", false))
		{
			setupKungFuState();			
		}
		else 
		{
			setupNonKungfuState();
		}
		
		if(prefs.getBoolean("stayawake", false)) setWakelock();		
		
		tealist.setOnItemClickListener(new ListView.OnItemClickListener()
		{
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
						long id) 
				{
					tw1.setTextSize(55);
					for(int i = 0; i < isSelected.length; ++i)  //deselect array
					{
						isSelected[i]=false;
					}
					
					if (!timerstarted)
					{
						makeTransparent();
						
						isSelected[pos]=true;
						if(row!=null)
						{
							if(session!=null)
							{
								session.stop();
							}
						}
						
						//row = tealist.getChildAt(pos);
						row=tealist.getChildAt(tealist.indexOfChild(arg1));
						saveselection=row;
						
						kannasor.removeAllViews();
						btn3.setEnabled(false);
						btn2.setVisibility(View.INVISIBLE);
						
						arg1.setBackgroundColor(0xffFFC919);
						
						teaid= adapter.data[pos].teaId;			
						
						List<Brewing> brewings1 = db.getBrewings(teaid);
						db.close();
						
						if(brewings1.size()!=0)
						{
							session = new Teasession(teaid, brewings1); //brewing indexeles nem 0!!
							//session.setTealistpos(pos); //ez itt nem valami helyes
							session.setTealistpos(tealist.indexOfChild(arg1)); 
							
							brewingtime = session.getBrewingtime(session.getBrewidx());
							tw1.setText(Textformatter.MillisToMinSec(brewingtime)); //teszteles
						
							spawner = new PotSpawner(brewings1, MainActivity.this);
							spawner.placePotsOnView2(kannasor);
							
							kungfubrewing=true;
							btn1.setEnabled(true);
							
							if(brewings1.size()>1)btn3.setEnabled(true);
							
							btn2.setEnabled(true);
						}else 
						{
							btn1.setEnabled(false);
							btn3.setEnabled(false);
							btn2.setEnabled(false);
							Toast.makeText(MainActivity.this, "No brewings yet! \n Add brewings by long touch \n " +
									"on the tea!" , Toast.LENGTH_LONG).show();
						}
						
					}
				}

					
			
		});
		
		tw1.setOnClickListener(this);
		tglbtn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
				
		btn1.setOnClickListener(new View.OnClickListener()                          //start
		{
			public void onClick(View v) 
			{
				if(kungfubrewing)
				{
					btn2.setEnabled(true);
					btn2.setVisibility(View.VISIBLE);
				}
				
				if (!timerstarted)
				{
					tw1.setText("");
					tw1.setTextSize(55);
					btn1.setText("Pause");
					
					counter = new MyCount(brewingtime, 100, MainActivity.this);
					//btn1.setEnabled(false);
					
					if(stopperstarted==false)
					{
						btimehist=SystemClock.uptimeMillis();
						stopperstarted=true;
					}
					
					counter.start();
					
					
					timerstarted = true;

					if (session != null)
					{
						
						if(session.getBrewidx()==1)
						{
							session.start();
							kannasor.getChildAt(session.getBrewidx()-1).setBackgroundColor(0xFFDC2F0E);
						}
						else if (session.getBrewidx()>1)
						{
							kannasor.getChildAt(session.getBrewidx()-2).setBackgroundColor(Color.TRANSPARENT);
						}
						
						
					}
					
				}
				else
				{
					counter.cancel();
					btn1.setText("Start");
					timerstarted= false;
				}
				
				
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener()   //next brew
		{
			public void onClick(View v) 
			{
								
				if(session != null)
				{
					if(!MainActivity.session.isLastBrew())
					{
						MainActivity.session.setNextBrew();
						tw1.setTextSize(55);
						MainActivity.setBrewingtime(MainActivity.session.getBrewingtime(MainActivity.session.getBrewidx()));
						MainActivity.tw1.setText(Textformatter.MillisToMinSec(
								MainActivity.session.getBrewingtime(MainActivity.session.getBrewidx())));
						
						MainActivity.kannasor.getChildAt(MainActivity.session.getBrewidx()-1).setBackgroundColor(0xFFDC2F0E);
						MainActivity.kannasor.getChildAt(MainActivity.session.getBrewidx()-2).setBackgroundColor(Color.TRANSPARENT);
					}
					else		//session ended			
					{
					
					MainActivity.tw1.setTextSize(30);
					MainActivity.tw1.setText("Session ended!");
					MainActivity.kannasor.getChildAt(MainActivity.session.getBrewidx()-1).setBackgroundColor(Color.TRANSPARENT);
					MainActivity.tealist.getChildAt(MainActivity.session.getTealistpos()).setBackgroundColor(Color.TRANSPARENT);
					MainActivity.btn2.setEnabled(false);
					MainActivity.session.stop();
					}
				}
			}
		});
		
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) 
	{
		super.onSaveInstanceState(savedInstanceState);
	}	
	

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void sendstat()
	{
		db=new DatabaseHandler(this);
		PHPCom sendphp =new PHPCom(this);
		String uuid;
		List<SessionH> sessions= new ArrayList<SessionH>();
		List<BrewingH> brewings= new ArrayList<BrewingH>();
		
		sessions=db.getSessionsToSend();		 
		brewings =db.getBrewingsToSend();
		uuid=db.getUUID();
		db.close();
		
		if(sessions.size()>0 && brewings.size()>0)
		{
			sendWasCorrect=sendphp.execute(sessions, brewings, uuid);
		}
	}
	
	private boolean hasConnection() 
	{
		    ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(
		        Context.CONNECTIVITY_SERVICE);

		    NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		    if (wifiNetwork != null && wifiNetwork.isConnected()) 
		    {
		      return true;
		    }

		    NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		    if (mobileNetwork != null && mobileNetwork.isConnected()) 
		    {
		      return true;
		    }

		    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		    if (activeNetwork != null && activeNetwork.isConnected()) 
		    {
		      return true;
		    }

		    return false;
		  }
	
	public void onClick(View v)
	{
		if (v.getId() == R.id.editText1)
		{
			mydialog = new Dialog(MainActivity.this);
			mydialog.setContentView(R.layout.popup);
			mydialog.setTitle("Set timer 'min:sec'");
			mydialog.setCancelable(true);
			btimesave = brewingtime;
			Button buttonOK = (Button) mydialog.findViewById(R.id.Btn1);
			Button buttonCancel = (Button) mydialog.findViewById(R.id.Btn2);
			Button btnup1 = (Button) mydialog.findViewById(R.id.btnup1);
			Button btnup2 = (Button) mydialog.findViewById(R.id.btnup2);
			Button btndn1 = (Button) mydialog.findViewById(R.id.btndn1);
			Button btndn2 = (Button) mydialog.findViewById(R.id.btndn2);
			final EditText _et1 = (EditText) mydialog.findViewById(R.id.editText1);
			final EditText _et2 = (EditText) mydialog.findViewById(R.id.editText2);
			_et1.setFocusable(false);
			_et2.setFocusable(false);
			_et1.setText(Textformatter.MillisToMin(brewingtime));
			_et2.setText(Textformatter.MillisToSec(brewingtime));
			btnup1.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					if(Integer.parseInt(_et1.getText().toString()) < 15)
					{
						_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())+1)));
					}
					else
					{
						_et1.setText("00");
					}
					
				}
			});
			btnup1.setOnTouchListener(new View.OnTouchListener() 
			{
				private Handler mHandler;
				
				public boolean onTouch(View v, MotionEvent event) 
				{
					switch(event.getAction()) {
			        case MotionEvent.ACTION_DOWN:
			            if (mHandler != null) return true;
			            mHandler = new Handler();
			            mHandler.postDelayed(mAction, 500);
			            break;
			        case MotionEvent.ACTION_UP:
			            if (mHandler == null) return true;
			            mHandler.removeCallbacks(mAction);
			            mHandler = null;
			            break;
			        }
			        return false;
					
				}
				
				Runnable mAction = new Runnable() {
			        public void run() {
			        	if(Integer.parseInt(_et1.getText().toString()) < 15)
						{
							_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())+1)));
						}
						else
						{
							_et1.setText("00");
						}
			            mHandler.postDelayed(this, 200);
			        }
			    };

			});
			btnup2.setOnClickListener(new View.OnClickListener() 
			{					
				public void onClick(View v) 
				{
					if(Integer.parseInt(_et2.getText().toString()) < 60)
					{
						_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())+1)));
					}
					else
					{
						_et2.setText("00");
					}
					
				}
			});
			btnup2.setOnTouchListener(new View.OnTouchListener() 
			{
				private Handler mHandler;
				
				public boolean onTouch(View v, MotionEvent event) 
				{
					switch(event.getAction()) {
			        case MotionEvent.ACTION_DOWN:
			            if (mHandler != null) return true;
			            mHandler = new Handler();
			            mHandler.postDelayed(mAction, 200);
			            break;
			        case MotionEvent.ACTION_UP:
			            if (mHandler == null) return true;
			            mHandler.removeCallbacks(mAction);
			            mHandler = null;
			            break;
			        }
			        return false;
					
				}
				
				Runnable mAction = new Runnable() {
			        public void run() {
			        	if(Integer.parseInt(_et2.getText().toString()) < 60)
						{
							_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())+1)));
						}
						else
						{
							_et2.setText("00");
						}
			            mHandler.postDelayed(this, 200);
			        }
			    };

			});
			btndn1.setOnClickListener(new View.OnClickListener() 
			{					
				public void onClick(View v) 
				{
					if(Integer.parseInt(_et1.getText().toString())>0)
					{
						_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())-1)));
					}
					else
					{
						_et1.setText("15");
					}
					
				}
			});
			btndn1.setOnTouchListener(new View.OnTouchListener() 
			{
				private Handler mHandler;
				
				public boolean onTouch(View v, MotionEvent event) 
				{
					switch(event.getAction()) {
			        case MotionEvent.ACTION_DOWN:
			            if (mHandler != null) return true;
			            mHandler = new Handler();
			            mHandler.postDelayed(mAction, 200);
			            break;
			        case MotionEvent.ACTION_UP:
			            if (mHandler == null) return true;
			            mHandler.removeCallbacks(mAction);
			            mHandler = null;
			            break;
			        }
			        return false;
					
				}
				
				Runnable mAction = new Runnable() {
			        public void run() {
			        	if(Integer.parseInt(_et1.getText().toString()) > 0)
						{
							_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())-1)));
						}
						else
						{
							_et1.setText("15");
						}
			            mHandler.postDelayed(this, 200);
			        }
			    };

			});
			btndn2.setOnClickListener(new View.OnClickListener() 
			{					
				public void onClick(View v) 
				{
					if(Integer.parseInt(_et2.getText().toString())>0)
					{
						_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())-1)));
					}
					else
					{
						_et2.setText("60");
					}
					
				}
			});
			btndn2.setOnTouchListener(new View.OnTouchListener() 
			{
				private Handler mHandler;
				
				public boolean onTouch(View v, MotionEvent event) 
				{
					switch(event.getAction()) {
			        case MotionEvent.ACTION_DOWN:
			            if (mHandler != null) return true;
			            mHandler = new Handler();
			            mHandler.postDelayed(mAction, 200);
			            break;
			        case MotionEvent.ACTION_UP:
			            if (mHandler == null) return true;
			            mHandler.removeCallbacks(mAction);
			            mHandler = null;
			            break;
			        }
			        return false;
					
				}
				
				Runnable mAction = new Runnable() {
			        public void run() {
			        	if(Integer.parseInt(_et2.getText().toString()) >0)
						{
							_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())-1)));
						}
						else
						{
							_et2.setText("60");
						}
			            mHandler.postDelayed(this, 200);
			        }
			    };

			});
			buttonOK.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					tw1.setTextSize(55);
					brewingtime = Textformatter.MinSecToMillis(Integer.parseInt(_et1.getText().toString()), 
									Integer.parseInt(_et2.getText().toString()));
					
					mydialog.dismiss();
					tw1.setText(Textformatter.MillisToMinSec(brewingtime));
				}
			});
			buttonCancel.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
				
					brewingtime = btimesave;
					mydialog.dismiss();
				}
			});
			mydialog.show();
		} 
		else if (v.getId() == R.id.toggleButton1)
		{
			if(tglbtn1.isChecked())
			{
				setupKungFuState();	
				
			}
			else
			{
				setupNonKungfuState();
			}
		}
		else if (v.getId()==R.id.button2)
		{
			btn2.setVisibility(View.INVISIBLE);
			counter.cancel();
			counter.doFinish();
			
		}
		
	}

	public void setupKungFuState()
	{
		tealist.setVisibility(View.VISIBLE);
		btn1.setEnabled(false);
		btn2.setEnabled(false);
		btn2.setVisibility(View.INVISIBLE);
		btn3.setVisibility(View.VISIBLE);
		
		for(int i =0; i < tealist.getChildCount(); i++)
		{tealist.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);}
		kungfubrewing=true;
	}

	public void setupNonKungfuState()
	{
		btn1.setEnabled(true);
		btn2.setEnabled(false);
		btn3.setEnabled(false);
		btn2.setVisibility(View.INVISIBLE);
		btn3.setVisibility(View.INVISIBLE);
		tealist.setVisibility(View.INVISIBLE);
		if(session!=null)
		{
			session.stop();
			session=null;
		}
		if(kannasor!=null) kannasor.removeAllViews();				
		kungfubrewing = false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		if(v.getId()==R.id.listView1)
		{
			AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(adapter.data[info.position].title);
			String[] menuitems=getResources().getStringArray(R.array.contextmenuitems);
			
			for (int i = 0; i<menuitems.length; i++) 
			{
			     menu.add(Menu.NONE, i, i, menuitems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuitems= getResources().getStringArray(R.array.contextmenuitems);
		String menuItemName = menuitems[menuItemIndex];
		String listItemName = adapter.data[info.position].title;
		final int lteaid=adapter.data[info.position].teaId;
		  
			  switch (menuItemIndex)
			  {
			  case 0:			//    Edit/Show notice
				  
				  mydialog = new Dialog(MainActivity.this, R.style.PauseDialog);
				  mydialog.setContentView(R.layout.shownotice);
				  mydialog.setTitle("Notes for\n"+listItemName);
				  mydialog.setCancelable(true);
				  final EditText notice = (EditText) mydialog.findViewById(R.id.noticeText1);
				  Button btnSavenote =(Button) mydialog.findViewById(R.id.savenote);
				  
				  db1=new DatabaseHandler(this);
				  teasavas = new Tea();
				  teasavas = db1.getTea(adapter.data[info.position].teaId);
				  notice.setText(teasavas.getRemark());
				  
				  btnSavenote.setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View v)
						{
							teasavas.setRemark(notice.getText().toString());
							int res=db1.updateTea(teasavas);
							Toast.makeText(MainActivity.this, ""+res, Toast.LENGTH_SHORT).show();
							res=0;
							mydialog.dismiss();
						}
					});
				  
				  mydialog.setOnDismissListener(new DialogInterface.OnDismissListener()
				  {
					
					public void onDismiss(DialogInterface dialog)
					{
						db1.close();
						
					}
				  });
				  
				  mydialog.show();
				  
				  break;
				  
			  case 1:			//Delete
				  mydialog = new Dialog(MainActivity.this, R.style.PauseDialog);
				  mydialog.setContentView(R.layout.confirmdialog);
				  mydialog.setTitle("Deleting the\n"+listItemName);
				  mydialog.setCancelable(true);
				  Button btnCancel=(Button)mydialog.findViewById(R.id.btncanel2);
				  Button btnDelete=(Button)mydialog.findViewById(R.id.deletetea);
				  db1=new DatabaseHandler(this);
				  
				  btnDelete.setOnClickListener(new View.OnClickListener()
				  {
					
					public void onClick(View v)
					{
					  db1.deleteBrewings(lteaid);
					  db1.deleteTea(lteaid);
					  tealistRefresh(info.position);
					  mydialog.dismiss();
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
				  break;
				  
			  case 2:			//Append brews
					
						mydialog = new Dialog(MainActivity.this);
						mydialog.setContentView(R.layout.popup);
						mydialog.setTitle("Brewingtime 'min:sec'");
						mydialog.setCancelable(true);
						
												
						Button buttonOK = (Button) mydialog.findViewById(R.id.Btn1);
						Button buttonCancel = (Button) mydialog.findViewById(R.id.Btn2);
						Button btnup1 = (Button) mydialog.findViewById(R.id.btnup1);
						Button btnup2 = (Button) mydialog.findViewById(R.id.btnup2);
						Button btndn1 = (Button) mydialog.findViewById(R.id.btndn1);
						Button btndn2 = (Button) mydialog.findViewById(R.id.btndn2);
						
						final EditText _et1 = (EditText) mydialog.findViewById(R.id.editText1);
						final EditText _et2 = (EditText) mydialog.findViewById(R.id.editText2);
						_et1.setFocusable(false);
						_et2.setFocusable(false);
						_et1.setText(Textformatter.MillisToMin(brewingtime));
						_et2.setText(Textformatter.MillisToSec(brewingtime));
						
						btnup1.setOnClickListener(new View.OnClickListener() 
						{
							public void onClick(View v) 
							{
								if(Integer.parseInt(_et1.getText().toString()) < 15)
								{
									_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())+1)));
								}
								else
								{
									_et1.setText("00");
								}
								
							}
						});
						
						btnup1.setOnTouchListener(new View.OnTouchListener() 
						{
							private Handler mHandler;
							
							public boolean onTouch(View v, MotionEvent event) 
							{
								switch(event.getAction()) {
						        case MotionEvent.ACTION_DOWN:
						            if (mHandler != null) return true;
						            mHandler = new Handler();
						            mHandler.postDelayed(mAction, 500);
						            break;
						        case MotionEvent.ACTION_UP:
						            if (mHandler == null) return true;
						            mHandler.removeCallbacks(mAction);
						            mHandler = null;
						            break;
						        }
						        return false;
								
							}
							
							Runnable mAction = new Runnable() {
						        public void run() {
						        	if(Integer.parseInt(_et1.getText().toString()) < 15)
									{
										_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())+1)));
									}
									else
									{
										_et1.setText("00");
									}
						            mHandler.postDelayed(this, 200);
						        }
						    };

						});
						
						btnup2.setOnClickListener(new View.OnClickListener() 
						{					
							public void onClick(View v) 
							{
								if(Integer.parseInt(_et2.getText().toString()) < 60)
								{
									_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())+1)));
								}
								else
								{
									_et2.setText("00");
								}
								
							}
						});
						
						btnup2.setOnTouchListener(new View.OnTouchListener() 
						{
							private Handler mHandler;
							
							public boolean onTouch(View v, MotionEvent event) 
							{
								switch(event.getAction()) {
						        case MotionEvent.ACTION_DOWN:
						            if (mHandler != null) return true;
						            mHandler = new Handler();
						            mHandler.postDelayed(mAction, 200);
						            break;
						        case MotionEvent.ACTION_UP:
						            if (mHandler == null) return true;
						            mHandler.removeCallbacks(mAction);
						            mHandler = null;
						            break;
						        }
						        return false;
								
							}
							
							Runnable mAction = new Runnable() {
						        public void run() {
						        	if(Integer.parseInt(_et2.getText().toString()) < 60)
									{
										_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())+1)));
									}
									else
									{
										_et2.setText("00");
									}
						            mHandler.postDelayed(this, 200);
						        }
						    };

						});
						
						btndn1.setOnClickListener(new View.OnClickListener() 
						{					
							public void onClick(View v) 
							{
								if(Integer.parseInt(_et1.getText().toString())>0)
								{
									_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())-1)));
								}
								else
								{
									_et1.setText("15");
								}
								
							}
						});
						
						btndn1.setOnTouchListener(new View.OnTouchListener() 
						{
							private Handler mHandler;
							
							public boolean onTouch(View v, MotionEvent event) 
							{
								switch(event.getAction()) {
						        case MotionEvent.ACTION_DOWN:
						            if (mHandler != null) return true;
						            mHandler = new Handler();
						            mHandler.postDelayed(mAction, 200);
						            break;
						        case MotionEvent.ACTION_UP:
						            if (mHandler == null) return true;
						            mHandler.removeCallbacks(mAction);
						            mHandler = null;
						            break;
						        }
						        return false;
								
							}
							
							Runnable mAction = new Runnable() {
						        public void run() {
						        	if(Integer.parseInt(_et1.getText().toString()) > 0)
									{
										_et1.setText(String.format("%02d" ,(Integer.parseInt(_et1.getText().toString())-1)));
									}
									else
									{
										_et1.setText("15");
									}
						            mHandler.postDelayed(this, 200);
						        }
						    };

						});

						
						btndn2.setOnClickListener(new View.OnClickListener() 
						{					
							public void onClick(View v) 
							{
								if(Integer.parseInt(_et2.getText().toString())>0)
								{
									_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())-1)));
								}
								else
								{
									_et2.setText("60");
								}
								
							}
						});
						
						btndn2.setOnTouchListener(new View.OnTouchListener() 
						{
							private Handler mHandler;
							
							public boolean onTouch(View v, MotionEvent event) 
							{
								switch(event.getAction()) {
						        case MotionEvent.ACTION_DOWN:
						            if (mHandler != null) return true;
						            mHandler = new Handler();
						            mHandler.postDelayed(mAction, 200);
						            break;
						        case MotionEvent.ACTION_UP:
						            if (mHandler == null) return true;
						            mHandler.removeCallbacks(mAction);
						            mHandler = null;
						            break;
						        }
						        return false;
								
							}
							
							Runnable mAction = new Runnable() {
						        public void run() {
						        	if(Integer.parseInt(_et2.getText().toString()) >0)
									{
										_et2.setText(String.format("%02d" ,(Integer.parseInt(_et2.getText().toString())-1)));
									}
									else
									{
										_et2.setText("60");
									}
						            mHandler.postDelayed(this, 200);
						        }
						    };

						});

						
						buttonOK.setOnClickListener(new View.OnClickListener() 
						{
							public void onClick(View v) 
							{
								Brewing lbrewing = new Brewing();
								long lbrewingtime;
								DatabaseHandler db = new DatabaseHandler(MainActivity.this);
								
								lbrewingtime = Textformatter.MinSecToMillis(Integer.parseInt(_et1.getText().toString()), 
												Integer.parseInt(_et2.getText().toString()));
								
								lbrewing.setBrewnr(db.getBrewingsCount(lteaid)+1);
								lbrewing.setTeaID(lteaid);
								lbrewing.setBrewtime((int)lbrewingtime);
								
								db.addBrewing(lbrewing);
								
								tealistRefresh();
								mydialog.dismiss();
								
							}
						});

						buttonCancel.setOnClickListener(new View.OnClickListener() 
						{
							public void onClick(View v) 
							{
							
								mydialog.dismiss();
							}
						});

						mydialog.show();
						
				  break;
				  
			  case 3:															//LEARN session
				  Bundle basket = new Bundle();
				  basket.putInt("teaID", lteaid);
				  
				  startActivityForResult(new Intent(this, LearnSession.class).putExtras(basket), 1);
				  break;
			  default:
				  break;
			  }
		  
		return true;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
	}
	
	public static void makeTransparent()
	{
		
		for(int i = 0; i < isSelected.length; ++i)  //deselect array
		{
			isSelected[i]=false;
		}
		
		for(int i=0; i< tealist.getChildCount();i++)
		{
			tealist.getChildAt(i).setBackgroundColor(Color.BLACK);
		}
	}
	
	private void initactivity() 
	{
		root= new LinearLayout(this);
		kannasor= new LinearLayout(this);
		
//		tw1 = new TextView(this);
		tw1 = new TextView(this);
		btn1 = new Button(this);
		btn2 = new Button(this);
		btn3 = new Button(this);
		tglbtn1 = new  ToggleButton(this);
		tealist = new ListView(this);
		
//		db = new DatabaseHandler(this);
//        List<Tea> teas = db.getAllTeas();    
//        
//        Listelements teak[]=new Listelements[teas.size()];
        
        root = (LinearLayout)findViewById(R.id.rootlayout);
		kannasor = (LinearLayout)findViewById(R.id.kannalayout);
		
//        for (int i = 0; i < teas.size(); i++)
//	        {
//        		List<Brewing> brewings = db.getBrewings(teas.get(i).getID());
//        		
//        		teak[i]= new Listelements(teas.get(i).getID(), teas.get(i).getTemperature().toString()+"�C, Brews: "+brewings.size(),
//        				teas.get(i).getName().toString());
//	        }
        
		tw1 = (TextView) findViewById(R.id.editText1);
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		tglbtn1 = (ToggleButton) findViewById(R.id.toggleButton1);
		tealist = (ListView) findViewById(R.id.listView1);
		
		root.setBackgroundColor(Color.BLACK);
//		mWakeLock.release();
		
		//adapter = new TeaAdapter(this, R.layout.listview_item_row, teak);
		
		//WeatherAdapter adapter = new WeatherAdapter(this, R.layout.listview_item_row, teas);
	    View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
	      //  tealist.addHeaderView(header);
		
	    tealistRefresh();
		registerForContextMenu(tealist);
		
		tw1.setText(Textformatter.MillisToMinSec(brewingtime));
		btn1.setEnabled(false);
		btn3.setEnabled(false);
		btn2.setEnabled(false);
		btn2.setVisibility(View.INVISIBLE);
		
	}
	
	public void tealistRefresh()
	{
		db = new DatabaseHandler(this);
        List<Tea> teas = db.getAllTeas();  
        boolean isIndexsaved=false;
        int iBackupindex=0;
        if(isSelected!=null)
        {
        	for (int i = 0; i < isSelected.length; i++)
			{
				if (isSelected[i]) 
					{
						iBackupindex=i; 
						isIndexsaved=true;
					}
			}
        }
        
        isSelected = new boolean[teas.size()];
        for(int i=0;i < teas.size();i++)
        {
            isSelected[i]=false;
        }       
        
        if (isIndexsaved)
		{
        	isSelected[iBackupindex]=true;
        	
		}
        
        Listelements teak[]=new Listelements[teas.size()];
        for (int i = 0; i < teas.size(); i++)
        {
    		List<Brewing> brewings = db.getBrewings(teas.get(i).getID());
    		teak[i]= new Listelements(teas.get(i).getID()
									, teas.get(i).getTemperature().toString()+"°C, Brews: "+brewings.size()
									, teas.get(i).getName().toString()
									, teas.get(i).getRemark().toString()); //10 characters
        }
        adapter = new TeaAdapter(this, R.layout.listview_item_row, teak);
        tealist.setAdapter(adapter);
	}
	
	public void tealistRefresh(int selindex)
	{
		db = new DatabaseHandler(this);
        List<Tea> teas = db.getAllTeas();  
        boolean isIndexsaved=false;
        int iBackupindex=0;
        
        if(isSelected!=null)
        {
        	for (int i = 0; i < isSelected.length; i++)
			{
				if (isSelected[i]) 
					{
						iBackupindex=i; 
						isIndexsaved=true;
					}
			}
        }
        
        
        isSelected = new boolean[teas.size()];
        for(int i=0;i < teas.size();i++)
        {
            isSelected[i]=false;
        }
       
        if (isIndexsaved && (selindex+1)<=teas.size())
		{
        	isSelected[iBackupindex]=true;        	
		}
        
        
        Listelements teak[]=new Listelements[teas.size()];
        for (int i = 0; i < teas.size(); i++)
        {
    		List<Brewing> brewings = db.getBrewings(teas.get(i).getID());
    		
    		teak[i]= new Listelements(teas.get(i).getID()
									 ,teas.get(i).getTemperature().toString()+"�C, Brews: "+brewings.size()
									 ,teas.get(i).getName().toString()
									, teas.get(i).getRemark().toString()); // 10 characters
        }
        adapter = new TeaAdapter(this, R.layout.listview_item_row, teak);
        tealist.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)			//Android Men�
	{
		 if (item.getItemId() == R.id.item1) //Settings menü
		{
			if(mWakeLock!=null)
			{
				mWakeLock.release();
				mWakeLock=null;
			}
			startActivity(new Intent(this, Settings.class));
		} 
		 else if (item.getItemId() == R.id.item2)
		{
			AboutDialog about = new AboutDialog(this);
			about.setTitle("about this app");
			about.show();
		} 
		 else if (item.getItemId() == R.id.item3)
		{
			startActivity(new Intent(this, Addtea.class));
		}
		 else if (item.getItemId()== R.id.item4)
		 {
			startActivity(new Intent(this, Edittea.class));
		}

		    return true;
	}
	
	 @Override
	protected void onStop()
	 {
		 btimesavedforsettings = brewingtime;
	       super.onStop();

	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putLong("brewingtime", btimesavedforsettings);
	      editor.putBoolean("kungfubrewing", kungfubrewing);
	      editor.putBoolean("tglbtn", tglbtn1.isChecked());
	      
	      // Commit the edits!
	      editor.commit();
	      if(db!=null)
			{
				db.close();
			}
	      if(mWakeLock!=null)mWakeLock.release();
	  }
	@Override
	protected void onPause() 
	{
		super.onPause();
		if(db!=null)
		{
			db.close();
		}
		if(mWakeLock!=null)
			{
				mWakeLock.release();
				mWakeLock=null;
			}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(prefs.getBoolean("stayawake", false)) this.setWakelock();
		prefs.getString("sound", "1");
		
		
		if (kungfubrewing && session!= null)
		{
			if(session.isStarted())
			{				
			}
			else 
			{
				tealistRefresh();
				if (kannasor != null) kannasor.removeAllViews();
				btn1.setEnabled(false);
				btn2.setEnabled(false);
				btn3.setEnabled(false);				
			}
		}
		else if (kungfubrewing && session==null)
		{
			tealistRefresh();
		}
		
		
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(db!=null)
		{
			db.close();
		}
		if(mWakeLock!=null)mWakeLock.release();
	}
	public boolean onTouch(View v, MotionEvent event) 
	{
			return false;
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
	
	
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		if(mWakeLock!=null)mWakeLock.release();
		return false;
	}

	public void onClick(DialogInterface dialog, int which)
	{	
	}

	public void timerFinished()
	{
		if(prefs.getBoolean("stat", true))session.writeHistory(btimehist);
		stopperstarted=false;
	}

	

}
