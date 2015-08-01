package ati.kungfutimer;

import java.util.concurrent.TimeUnit;

public class Textformatter 
{
	
	public static String MillisToMinSec(long _millis)
	{
		return String.format("%02d:%02d", 
			    TimeUnit.MILLISECONDS.toMinutes(_millis),
			    TimeUnit.MILLISECONDS.toSeconds(_millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(_millis))
			);
	}
	
	public static String MillisToSec(long _millis)
	{
		return String.format("%02d",
				TimeUnit.MILLISECONDS.toSeconds(_millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(_millis)));
	}
	
	public static String MillisToMin(long _millis)
	{
		return String.format("%02d", 
			    TimeUnit.MILLISECONDS.toMinutes(_millis));
	}
	
	public static long MinSecToMillis(int _min, int _sec)
	{
		return TimeUnit.MINUTES.toMillis(_min)+ TimeUnit.SECONDS.toMillis(_sec);
	}
	
}
