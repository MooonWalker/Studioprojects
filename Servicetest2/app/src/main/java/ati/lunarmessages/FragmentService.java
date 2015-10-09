package ati.lunarmessages;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FragmentService extends Fragment
{
    public FragmentService(){/* Required empty public constructor*/ }
    //private PostData[] listData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
    // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_layout_service, container, false);
    //Check internet connection here to not destroy the service
        if (Controller.isConnectingToInternet(MainActivity.ctx))
        {

        }
        else
        {

//            TextView textView= new TextView(getActivity());
//            textView.setText(R.string.no_internet);
//            textView.setPadding(10,10,10,10);
//            FrameLayout frameLayout=(FrameLayout)view.findViewById(R.id.rssFramelayout);
//            frameLayout.addView(textView);

        }
        return view;
    }


}
