package ati.lunarmessages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FragmentRSS extends Fragment implements AdapterView.OnItemClickListener
{
    public FragmentRSS(){/* Required empty public constructor*/ }
    //private PostData[] listData;
    private RssItem[] listData;
    private RssReader reader;
    DatabaseHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_layout_rss, container, false);


        generateData();
        ListView listView = (ListView)view.findViewById(R.id.fRSS);
        PostItemAdapter itemAdapter = new PostItemAdapter(view.getContext(), R.layout.postitem, listData);
        //generateRssFeed(itemAdapter);
        //db= new DatabaseHandler(view.getContext());
        listView.setOnItemClickListener(onItemClickListener);

        listView.setAdapter(itemAdapter);
        return view;
    }

    private void generateData()
    {
        List<RssItem> postList;
        reader = new RssReader(Config.RSS_URL);
        reader.fetchXML();

        //parse the RSS in new thread
        while(reader.parsingComplete);
        postList= reader.getDataArrayList();

        listData= postList.toArray(new RssItem[postList.size()]);


    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            RssItem rssItem=listData[position];
            Bundle rssItemdetail = new Bundle();
            rssItemdetail.putString("content",rssItem.getDescription());

            Intent rssViewIntent = new Intent(MainActivity.ctx, RssViewActivity.class);
            rssViewIntent.putExtras(rssItemdetail);
            startActivity(rssViewIntent);
        }
    };
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }
}
