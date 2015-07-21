package ati.lunarmessages;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
        SharedPreferences pref = getActivity().getPreferences(0);
        viewcontent= pref.getString(MyPreference.fMESSAGE," ");
    }

    @Override
    public void onDetach()
    {
        SharedPreferences pref = getActivity().getPreferences(0);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString(MyPreference.fMESSAGE, tMsg.getText().toString());
        edt.apply();
        super.onDetach();
    }



    public void setText(String msg)
    {
        TextView textView =(TextView)this.getView().findViewById(R.id.tMsg);
        textView.setText("kaka");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_layout_msgs, container, false);
        Button button = (Button)view.findViewById(R.id.btnMsgTest);
        tMsg =(TextView)view.findViewById(R.id.tMsg);
        tMsg.setText(viewcontent);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tMsg.setText("kaka");
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
