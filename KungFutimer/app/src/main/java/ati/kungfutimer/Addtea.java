package ati.kungfutimer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Addtea extends Activity implements OnClickListener
{

	ListView brewingListView;
	Button btnAddbrew, btnSave;
	EditText etTeaname, etTemperature, etNotice;
	List<Brewing>newbrewings;
	DatabaseHandler db;
	List<String> sListitem;
	List<Integer> lBrewingtimes;
	ArrayAdapter<String> listAdapter;
	Dialog brewingtimeDialog;
	long brewingtime=0;
	NewTeaWithBrewings newTeaWithBrewings;
	Brewing brewing;
	Tea newtea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtea2);
		
		brewingListView=new ListView(this);
		btnAddbrew  = new Button(this);
		btnSave=new Button(this);
		etTeaname=new EditText(this);
		etTemperature=new EditText(this);
		etNotice=new EditText(this);
		
		sListitem=new ArrayList<String>();
		lBrewingtimes= new ArrayList<Integer>();
		newbrewings= new ArrayList<Brewing>();
		
		listAdapter=new ArrayAdapter<String>(this, R.layout.listitemsimple, R.id.label, sListitem);
		
		brewingListView=(ListView)findViewById(R.id.listView2);
		btnAddbrew=(Button)findViewById(R.id.btnaddbrew);
		btnSave=(Button)findViewById(R.id.btnsaveall);
		etTeaname=(EditText)findViewById(R.id.eTeaname);
		etTemperature=(EditText)findViewById(R.id.editText2);
		etNotice=(EditText)findViewById(R.id.eNotice);
		
		
		
		brewingListView.setAdapter(listAdapter);
		
		btnAddbrew.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				
				if(!etTeaname.getText().toString().equals(""))
				{
					brewingtimeDialog=new Dialog(Addtea.this);
					brewingtimeDialog.setContentView(R.layout.popup);
					brewingtimeDialog.setTitle("Set time 'min:sec'");
					brewingtimeDialog.setCancelable(true);
					
					Button buttonOK = (Button) brewingtimeDialog.findViewById(R.id.Btn1);
					Button buttonCancel = (Button) brewingtimeDialog.findViewById(R.id.Btn2);
					Button btnup1 = (Button) brewingtimeDialog.findViewById(R.id.btnup1);
					Button btnup2 = (Button) brewingtimeDialog.findViewById(R.id.btnup2);
					Button btndn1 = (Button) brewingtimeDialog.findViewById(R.id.btndn1);
					Button btndn2 = (Button) brewingtimeDialog.findViewById(R.id.btndn2);
					
					final EditText _et1 = (EditText) brewingtimeDialog.findViewById(R.id.editText1);
					final EditText _et2 = (EditText) brewingtimeDialog.findViewById(R.id.editText2);
					_et1.setFocusable(false);
					_et2.setFocusable(false);
					_et1.setText(Textformatter.MillisToMin(0));
					_et2.setText(Textformatter.MillisToSec(0));
					
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
							
							brewingtime = Textformatter.MinSecToMillis(Integer.parseInt(_et1.getText().toString()), 
											Integer.parseInt(_et2.getText().toString()));
							
							String tmp = _et1.getText().toString()+":"+_et2.getText().toString();
							listAdapter.add(tmp);
							
							lBrewingtimes.add((int) brewingtime);
							
							
							listAdapter.notifyDataSetChanged();
							
							brewingtimeDialog.dismiss();
							
						}
					});					
					buttonCancel.setOnClickListener(new View.OnClickListener() 
					{
						public void onClick(View v) 
						{
						
							
							brewingtimeDialog.dismiss();
						}
					});
					
					brewingtimeDialog.show();
				
				}
			}
		});
		
		
		btnSave.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				int rowid;
				//if(!etTeaname.getText().toString().equals("") && sListitem.size()!=0)
				if(!etTeaname.getText().toString().equals(""))
				{
					
					
					db=new DatabaseHandler(Addtea.this);
					newtea=new Tea(etTeaname.getText().toString(), etTemperature.getText().toString(), 
							etNotice.getText().toString(), null);
					
					
					
						rowid=(int) db.addTea(newtea);
						
						
						
						if(rowid==-19) 
							{
							Toast.makeText(Addtea.this, "The name already exists! \n It must be UNIQUE!", Toast.LENGTH_LONG).show();
							}
						else
						{
							if(sListitem.size()!=0)  							//brewing exists
							{
								for (int i = 0; i < lBrewingtimes.size(); i++)
								{
									brewing = new Brewing(i+1, lBrewingtimes.get(i), rowid);
									newbrewings.add(brewing);
								}
								
								String res= db.addBrewings(newbrewings);
								if (!res.equals("")) Toast.makeText 					//error
								(Addtea.this, res, Toast.LENGTH_LONG).show();
							}
							Toast.makeText(Addtea.this, "Tea saved! \n"+newbrewings.size()
									+" brewings added", Toast.LENGTH_SHORT).show();
							listAdapter.clear();
							listAdapter.notifyDataSetChanged();
							newbrewings.clear();
							lBrewingtimes.clear();
						}
					
					
					
					
				}
				else
				{
					Toast.makeText(Addtea.this, "Name and at least one brew is \n mandantory!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		if(db!=null)
		{
			db.close();
		}
	
	}

	public void onClick(View v)
	{
	
		
	}
	
}
