package ati.kungfutimer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AddTblTea extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		 TextView textview = new TextView(this);
	        textview.setText("This is the Tea tab");
	        setContentView(textview);
	}

}
