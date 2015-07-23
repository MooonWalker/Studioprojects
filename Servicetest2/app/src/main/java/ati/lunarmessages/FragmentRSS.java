package ati.lunarmessages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class FragmentRSS extends Fragment implements AdapterView.OnItemClickListener
{
    public FragmentRSS(){/* Required empty public constructor*/ }
    private PostData[] listData;

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

        generateDummyData();
        ListView listView = (ListView)view.findViewById(R.id.fRSS);
        PostItemAdapter itemAdapter = new PostItemAdapter(view.getContext(), R.layout.postitem, listData);
        //generateRssFeed(itemAdapter);
        listView.setAdapter(itemAdapter);
        return view;
    }

    private void generateDummyData()
    {
        PostData data = null;

        listData = new PostData[10];
        for (int i = 0; i < 10; i++)
        {
            data = new PostData();
            data.postDate = "May 20, 2013";
            data.postTitle = "Post " + (i + 1)
                    + " Title: This is the Post Title from RSS Feed";
            data.postThumbUrl = null;
            listData[i] = data;
        }
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
