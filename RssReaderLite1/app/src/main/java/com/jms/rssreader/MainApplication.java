package com.jms.rssreader;

import java.util.HashMap;

import android.app.Application;



public class MainApplication extends Application
{
	private final String APP_TRACK_ID="UA-23293636-5";
	private final String GLOBAL_TRACK_ID="UA-23293636-5";
	private final String ECOMMERCE_TRACK_ID="UA-23293636-5";
	
	//category
	public static final String CLICK_CATEGORY = "click button";
	
	//activity
	public static final String MAIN_ACTIVITY="1-Main Activity";
	public static final String LEARNING_LIST_ACTIVITY="2-Learning Activity";
	public static final String MEMORIZE_ACTIVITY = "3-Memorize Game Activity";
	
	//action
	public static final String CLICK_LEARNING_BUTTON="1-Click Learning Button";
	public static final String CLICK_MEMORIZE_BUTTON="2-Click Memorize Button";
	public static final String CLICK_MORE_GAME_BUTTON="3-Click More Game Button";
	
	
	public enum TrackerName
    {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

}
