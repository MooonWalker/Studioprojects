package ati.kungfutimer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class Edittea extends Activity
{

	private ListView tealist, brewingslist;
	
	private TextView textView, tdTname, tdBrews, tdBtitle;
	private EditText tdTemp, tdNotes;
	private DatabaseHandler db;
	//private TeaAdapter tealistadapter;
	private Tealistadapter tealistadapter;
	private int teaid;
	private ViewFlipper flipper;
	private Button btnSave, btnAppendbrew;
	private Tea tea;
	private List<Brewing> lBrewings;
	private List<String> lBrewitem;
	private ArrayAdapter<String> blistAdapter;
	private ImageView arwRight;

	private Dialog brewingtimeDialog;

	protected List<Integer> lBrewingtimes;

	protected long brewingtime;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//setContentView(R.layout.teaoverview);
		setContentView(R.layout.teadetailsflip);
		
		tealist=new ListView(this);
		brewingslist=new ListView(this);
		textView=new TextView(this);
		tdBrews=new TextView(this);
		tdTname=new TextView(this);
		tdBtitle=new TextView(this);
		tdTemp=new EditText(this);
		tdNotes=new EditText(this);
		btnSave=new Button(this);
		btnAppendbrew=new Button(this);
		tea=new Tea();
		lBrewings=new ArrayList<Brewing>();
		lBrewingtimes= new ArrayList<Integer>();
		lBrewitem=new ArrayList<String>();
		arwRight=new ImageView(this);		
		
		flipper = new ViewFlipper(this);
		
		flipper = (ViewFlipper) findViewById(R.id.flip1);
		arwRight=(ImageView)findViewById(R.id.imageView1);
		//tealistroot = (LinearLayout)findViewById(R.id.tealistroot);
		tealist=(ListView)findViewById(android.R.id.list);
		brewingslist=(ListView)findViewById(R.id.blist1);
		
		textView=(TextView)findViewById(R.id.teaot1);
		tdBrews=(TextView)findViewById(R.id.tdBrews);
		tdTname=(TextView)findViewById(R.id.tdTname);
		tdBtitle=(TextView)findViewById(R.id.brewingtext);
		
		tdTemp=(EditText)findViewById(R.id.tdeTemp);
		tdNotes=(EditText)findViewById(R.id.tedNotes);
		btnSave=(Button)findViewById(R.id.tdSave);
		btnAppendbrew=(Button)findViewById(R.id.tdAppendb);
		
		blistAdapter=new ArrayAdapter<String>(this, R.layout.listitmsimpic, R.id.label1, lBrewitem);		
		
		//tealistroot.setBackgroundColor(Color.BLACK);
		
		textView.setBackgroundColor(Color.RED);
		textView.setText("Your teas:");
		tdBrews.setText("");
		
		db=new DatabaseHandler(this);
		List<Tea> teas = db.getAllTeas();  
		
		Listelements teak[]=new Listelements[teas.size()];
        for (int i = 0; i < teas.size(); i++)
        {    		    		
    		teak[i]= new Listelements(teas.get(i).getID(), "", teas.get(i).getName().toString());
        }
        
        tealistadapter = new Tealistadapter(this, R.layout.listview_item_pic, teak,true);
		
		tealist.setAdapter(tealistadapter);
		
		tealist.setOnItemClickListener(new ListView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) 
			{
				teaid=tealistadapter.data[pos].teaId;	
				
				initTea(teaid);
				
				flipper.setInAnimation(inFromRightAnimation());
		        flipper.setOutAnimation(outToLeftAnimation());
		        flipper.showNext();   
			}
		});
		brewingslist.setOnItemClickListener(new ListView.OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3)
			{
				
				String temp = blistAdapter.getItem(pos);
				appendBrew(pos+1, temp);
				
			}
			
		});
		
		btnSave.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				saveTea(teaid);
				
			}
		});
		btnAppendbrew.setOnClickListener(new OnClickListener()
		{			
			public void onClick(View v)
			{
				appendBrew();
			}
		});
		tdBrews.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
								
				flipper.setInAnimation(inFromRightAnimation());
		        flipper.setOutAnimation(outToLeftAnimation());
		        flipper.showNext();  
				
			}
		});
		arwRight.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				flipper.setInAnimation(inFromRightAnimation());
		        flipper.setOutAnimation(outToLeftAnimation());
		        flipper.showNext();  
				
			}
		});
		
	}
	
	protected void appendBrew(final int pos, String temp)
	{
		brewingtimeDialog=new Dialog(this);
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
		String[]sliceStrings=temp.split(":");
		
		_et1.setText(sliceStrings[0]);
		_et2.setText(sliceStrings[1].trim());
		
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
				
				lbrewingtime = Textformatter.MinSecToMillis(Integer.parseInt(_et1.getText().toString()), 
								Integer.parseInt(_et2.getText().toString()));
				
				String tmp = _et1.getText().toString()+":"+_et2.getText().toString();
				
				blistAdapter.add(tmp);
												
				
				lbrewing.setBrewnr(pos);
				lbrewing.setTeaID(teaid);
				lbrewing.setBrewtime((int)lbrewingtime);
				
				db.updateBrewing(lbrewing);				
				
				blistAdapter.notifyDataSetChanged();
				
				initBrewings(teaid);
				
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

	protected void appendBrew()
	{		
		brewingtimeDialog=new Dialog(this);
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
				Brewing lbrewing = new Brewing();
				long lbrewingtime;
				
				lbrewingtime = Textformatter.MinSecToMillis(Integer.parseInt(_et1.getText().toString()), 
								Integer.parseInt(_et2.getText().toString()));
				
				String tmp = _et1.getText().toString()+":"+_et2.getText().toString();
				
				blistAdapter.add(tmp);
												
				
				lbrewing.setBrewnr(db.getBrewingsCount(teaid)+1);
				lbrewing.setTeaID(teaid);
				lbrewing.setBrewtime((int)lbrewingtime);
				
				db.addBrewing(lbrewing);
				
				
				blistAdapter.notifyDataSetChanged();
				
				initBrewings(teaid);
				
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

	protected void saveTea(int teaid2)
	{
		tea.setTemperature(tdTemp.getText().toString());
		tea.setRemark(tdNotes.getText().toString());
		int res=db.updateTea(tea);
		Toast.makeText(this, "Tea updated!", Toast.LENGTH_SHORT).show();
		res=0;
		
	}

	protected void initTea(int teaid2)
	{		
		tea=db.getTea(teaid2);
		
		initBrewings(teaid2);		
		
		tdTname.setText(tea.getName());
		
		tdTemp.setText(tea.getTemperature());
		tdNotes.setText(tea.getRemark());
		
	}

	private void initBrewings(int teaid23)
	{
		lBrewings=db.getBrewings(teaid23);
		tdBrews.setText("");
		blistAdapter.clear();
		for (Brewing br : lBrewings)
		{
			tdBrews.setText(tdBrews.getText()+Textformatter.MillisToMinSec(br.getBrewtime())+"; ");
			blistAdapter.add(Textformatter.MillisToMinSec(br.getBrewtime()));
			blistAdapter.notifyDataSetChanged();
		}
		tdBtitle.setText("Brewing times for "+tea.getName()+":");
		brewingslist.setAdapter(blistAdapter);
		
	}

	@Override
	public void onBackPressed()
	{
		
		if(flipper.getDisplayedChild()==1||flipper.getDisplayedChild()==2)
		{
			
			 flipper.setInAnimation(inFromLeftAnimation());
	         flipper.setOutAnimation(outToRightAnimation());
	         flipper.showPrevious();	         
		}		
		else
		{
			db.close();
			super.onBackPressed();
		}
	}

	private Animation inFromRightAnimation() 
	{
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
				inFromRight.setDuration(500);
				inFromRight.setInterpolator(new AccelerateInterpolator());
				return inFromRight;
	}	
	private Animation outToLeftAnimation() 
	{
				Animation outtoLeft = new TranslateAnimation(
				 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
				 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
				outtoLeft.setDuration(500);
				outtoLeft.setInterpolator(new AccelerateInterpolator());
				return outtoLeft;
		
	}	
	private Animation inFromLeftAnimation() 
	{
		Animation inFromLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}	
	private Animation outToRightAnimation() 
	{
		Animation outtoRight = new TranslateAnimation(
		 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
		 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onPause()
	{
		db.close();
		super.onPause();
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//getListView().setSelection(currentPosition);
	}
	
}
