package ati.lunarmessages;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.MailTo;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.regex.Pattern;

public class AboutDialog extends Dialog
{
    private static Context mContext = null;

    public AboutDialog(Context context)
    {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.about);

        TextView tv = (TextView)findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv = (TextView)findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.about)));
        tv.setLinkTextColor(Color.BLUE);
        Linkify.addLinks(tv, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
        Linkify.addLinks(tv, Pattern.compile("06\\d+\\S\\d+\\S\\d+"), "tel:");
    }

    public static String readRawTextFile(int id)
    {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();

        try
        {
            while (( line = buf.readLine()) != null) text.append(line);
        }
        catch (IOException e)
        {
            return null;
        }
        return  text.toString();
    }


}
