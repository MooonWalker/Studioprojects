package ati.lunarmessages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;
import android.os.Handler;

public class FragmentRSS extends Fragment implements AdapterView.OnItemClickListener
{
    public FragmentRSS(){/* Required empty public constructor*/ }
    //private PostData[] listData;
    private RssItem[] listData;
    private RssReader reader;
    DatabaseHandler db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_layout_rss, container, false);
        ListView listView = (ListView)view.findViewById(R.id.fRSS);
        listView.setVisibility(View.GONE);
        view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        //download rss feed
        generateData();
        RssItemAdapter itemAdapter = new RssItemAdapter(view.getContext(), R.layout.postitem, listData);
        //generateRssFeed(itemAdapter);
        //db= new DatabaseHandler(view.getContext());
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(onItemClickListener);

        listView.setAdapter(itemAdapter);
        return view;
    }

    private void generateData()
    {
        List<RssItem> rssItemList;
        reader = new RssReader(Config.RSS_URL);
        reader.fetchXML();

        //parse the RSS in new thread
        while(reader.parsingComplete);
        rssItemList= reader.getDataArrayList();

        listData= rssItemList.toArray(new RssItem[rssItemList.size()]);
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
