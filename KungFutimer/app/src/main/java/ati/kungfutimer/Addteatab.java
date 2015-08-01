package ati.kungfutimer;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Addteatab extends TabActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtea);
		
		Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    intent = new Intent().setClass(this, AddTblTea.class);
	    spec = tabHost.newTabSpec("artists").setIndicator("Artists", res.getDrawable(R.drawable.kicsikanna1))
            .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, AddTblBrewings.class);
	    spec = tabHost.newTabSpec("albums").setIndicator("Albums",
	                      res.getDrawable(R.drawable.kicsikanna1))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    

	    tabHost.setCurrentTab(1);
	    
	}
	
	

}
