package ati.lunarmessages;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;


public class FragmentMsgs extends Fragment
{
    public TextView tMsg;
    private String viewcontent;

    public FragmentMsgs()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewcontent= MyPreference.getfMESSAGE(MainActivity.ctx);
    }

    @Override
    public void onDetach()
    {
        MyPreference.setfMESSAGE(MainActivity.ctx,tMsg.getText().toString());
        super.onDetach();
    }

    @Override
    public void onResume()
    {
        tMsg.setLinkTextColor(Color.BLUE);
        doLinkify(tMsg);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_layout_msgs, container, false);
        tMsg =(TextView)view.findViewById(R.id.tMsg);
        tMsg.setText(viewcontent);
        tMsg.setLinkTextColor(Color.BLUE);
        doLinkify(tMsg);

        return view;
    }

    private void doLinkify(TextView tView)
    {
        Linkify.addLinks(tView, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
        Linkify.addLinks(tView, Pattern.compile("06\\d+\\S\\d+\\S\\d+"), "tel:");
    }
}
