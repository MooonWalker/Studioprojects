package ati.lunarmessages;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.Date;

public class MyPreference
{
    static final String PREFS_NAME = "Moon_push_settings";
    static final String REGID = "lRegId";
    static final String ISREGISTERED = "isregistered";
    static final String NEEDSREREG = "needsReReg";
    static final String fMESSAGE = "fMessage";
    static final String sCACHE_DIR = "sCACHE_DIR";
    static final String WELCOMESCREENSHOWN = "sWELCOMESHOWN";
    static final String DISCLAIMER_ACCEPTED = "ACCEPTED";
    static final String INSTALLED_ON ="dMemorySection";
    static final String ISFIRSTSTART="isFirstStart";



    public static Boolean getISFIRSTSTART(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(ISFIRSTSTART, true);
    }

    public static void setISFIRSTSTART(Context ctx, Boolean isFirst)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MyPreference.ISFIRSTSTART, isFirst);
        editor.apply();
    }

    public static void setInstalledOn(Context ctx, long date)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(MyPreference.INSTALLED_ON, date);
        editor.apply();
    }

    public static long getInstalledOn(Context ctx)
    {
        Date date=new Date();
        Long millis=date.getTime();
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(INSTALLED_ON, millis);
    }

    public static void setDISCLAIMER_ACCEPTED(Context ctx, Boolean acc)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MyPreference.DISCLAIMER_ACCEPTED, acc);
        editor.apply();
    }

    public static Boolean getDisclaimerAccepted(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(DISCLAIMER_ACCEPTED,false);
    }



    public static void setWELCOMESCREENSHOWN(Context ctx, Boolean shown)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MyPreference.WELCOMESCREENSHOWN, shown);
        editor.apply();
    }

    public static Boolean getWELCOMESCREENSHOWN(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(WELCOMESCREENSHOWN,false);
    }

    public static String getCACHE_DIR(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(sCACHE_DIR," ");
    }

    public static void setCACHE_DIR(Context ctx, String cachepath)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MyPreference.sCACHE_DIR, cachepath);
        editor.apply();
    }

    public static Boolean getISREGISTERED(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(ISREGISTERED,false);
    }

    public static Boolean getNEEDSREREG(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(NEEDSREREG, false);
    }

    public static String getREGID(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(REGID, " ");
    }

    public static String getfMESSAGE(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(fMESSAGE, " ");
    }

    public static void setREGID(Context ctx, String regid)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MyPreference.REGID, regid);
        editor.apply();
    }

    public static void setfMESSAGE(Context ctx, String fMESSAGE)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(MyPreference.fMESSAGE, fMESSAGE);
            editor.apply();


    }

    public static void setNEEDREREG(Context ctx, Boolean needsrereg)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MyPreference.NEEDSREREG, needsrereg);
        editor.apply();
    }

    public static void setISREGISTERED(Context ctx, Boolean isregistered)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MyPreference.ISREGISTERED, isregistered);
        // Commit the edits!
        editor.apply();
    }
}
