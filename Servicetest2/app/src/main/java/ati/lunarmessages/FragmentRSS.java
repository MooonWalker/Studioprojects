package ati.lunarmessages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private PostData[] listData;
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

//        obj = new RssReader(Config.RSS_URL);
//        obj.fetchXML();

        generateData();
        ListView listView = (ListView)view.findViewById(R.id.fRSS);
        PostItemAdapter itemAdapter = new PostItemAdapter(view.getContext(), R.layout.postitem, listData);
        //generateRssFeed(itemAdapter);
        db= new DatabaseHandler(view.getContext());
        
        listView.setAdapter(itemAdapter);
        return view;
    }

    private void generateData()
    {
        PostData data = null;
        List<PostData> postList;
        reader = new RssReader(Config.RSS_URL);
        reader.fetchXML();

        //parse the RSS in new thread
        while(reader.parsingComplete);
        postList= reader.getDataArrayList();

        listData= postList.toArray(new PostData[postList.size()]);


    }
    private void generateRssFeed(PostItemAdapter iA)
    {
        PostData data = null;
        //RssReader rssReader =new RssReader(url, "http://feeds.bbci.co.uk/news/rss.xml");

//        try
//        {
//            for (RssItem item : rssReader.getItems())
//                iA.add(item.getTitle());
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }
}
