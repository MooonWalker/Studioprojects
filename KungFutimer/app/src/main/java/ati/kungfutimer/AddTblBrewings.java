package ati.kungfutimer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AddTblBrewings extends Activity
{
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		
		 TextView textview = new TextView(this);
	        textview.setText("This is the brewings tab");
	        setContentView(textview);
	}
	
}
