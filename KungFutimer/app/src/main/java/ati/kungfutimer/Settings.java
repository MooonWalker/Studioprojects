package ati.kungfutimer;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.WindowManager;
import android.widget.Toast;

public class Settings extends PreferenceActivity
{
	private Context ctx;
	private DatabaseHandler db;
	private long statcount=0;
	

	@Override
     protected void onCreate(Bundle savedInstanceState) 
	 {
			db=new DatabaseHandler(this);
			statcount = db.getBrewStatCount();
			db.close();
             super.onCreate(savedInstanceState);
             addPreferencesFromResource(R.xml.prefs);
             getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
             
             final CheckBoxPreference stayAwake=(CheckBoxPreference)findPreference("stayawake");
             final CheckBoxPreference stat=(CheckBoxPreference)findPreference("stat");
             stat.setEnabled(false);
             
             final CheckBoxPreference sendstat=(CheckBoxPreference)findPreference("sendstat");
             final Preference backup = (Preference)findPreference("backup");
             final Preference restore = (Preference)findPreference("restore");
             
             if(stat.isChecked())stat.setSummary("Written records: "+statcount);
             
             
             backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
             {
				public boolean onPreferenceClick(Preference preference)
				{
					File rPath = null;
					DatabaseHandler db = new DatabaseHandler(ctx);
					try
					{
						rPath=db.copyDataBase(ctx, "", 1);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					Toast.makeText(getBaseContext(), "Backup created!\n"+rPath.toString(), Toast.LENGTH_SHORT).show();
					return false;
				}
             });
             
             restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
			{
				
				public boolean onPreferenceClick(Preference preference)
				{
					//futó sessiont leállítani
					boolean rc=false;
					
					if(MainActivity.session!=null)MainActivity.session.stop();
					
					if(rc=DbImpExp.restoreDb())
						{
							Toast.makeText(getBaseContext(), "Database restored!", Toast.LENGTH_SHORT).show();
						}
					else 
					{
						Toast.makeText(getBaseContext(), "There was an error\n during restore!", Toast.LENGTH_SHORT).show();
					}
					
					return false;
				}
			
            
            	 
             });
             
             stat.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener()
             {
            	public boolean onPreferenceChange(Preference preference,
						Object newValue)
				{
            		if((Boolean)newValue==true)
            		{
            			stat.setSummary("Written records: "+statcount);
            		}
					return true;
            	}
             });
             
             sendstat.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener()
             {
            	public boolean onPreferenceChange(Preference preference,
						Object newValue)
				{
            		if((Boolean)newValue==true)
            		{
            			
            		}
					return true;
            	}
             });
             
             
             
             stayAwake.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener()
             {
				
				public boolean onPreferenceChange(Preference preference,
						Object newValue)
				{
					if((Boolean)newValue==true)
					{
						MainActivity.setWakelock();
					}
					
					return true;
				}
			});
             
            
     }

	
}
