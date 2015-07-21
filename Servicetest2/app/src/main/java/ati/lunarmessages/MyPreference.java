package ati.lunarmessages;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference
{
    static final String PREFS_NAME = "Moon_push_settings";
    static final String REGID = "lRegId";
    static final String ISREGISTERED = "isregistered";
    static final String NEEDSREREG = "needsReReg";
    static final String fMESSAGE = "fMessage";


    public static Boolean getISREGISTERED(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(ISREGISTERED,false);
    }

    public static Boolean getNEEDSREREG(Context ctx)
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(NEEDSREREG,false);
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
