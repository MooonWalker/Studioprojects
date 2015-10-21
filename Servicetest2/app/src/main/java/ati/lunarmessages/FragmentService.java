package ati.lunarmessages;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class FragmentService extends Fragment
{
    public FragmentService(){/* Required empty public constructor*/ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

    // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_layout_service, container, false);
        Button btnService;
//        ImageButton ibtnService;
//        ibtnService=(ImageButton) view.findViewById(R.id.imbtnService);

        btnService=(Button) view.findViewById(R.id.btnService);
        btnService.setText(R.string.serviceTelephone);
        btnService.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Perform action on click
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+Config.SERVICE_TELEPHONE));
                startActivity(callIntent);
            }
        });

        RelativeLayout relativeLayout=(RelativeLayout)view.findViewById(R.id.framelayoutService);
       // relativeLayout.addView(imageView);
        //relativeLayout.addView(btnService);

        return view;
    }


}
