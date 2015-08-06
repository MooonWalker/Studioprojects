package ati.lunarmessages;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FragmentRSS extends Fragment implements RefreshableInterface
{
    public FragmentRSS(){/* Required empty public constructor*/ }
    //private PostData[] listData;
    private ArrayList<RssItem> listData;
    private RefreshableListView listView;
    private RssItemAdapter itemAdapter;
    private boolean isLoading = false;
    private boolean isRefreshLoading = true;
    private boolean enablePagnation = false;
    private ArrayList<String> itemUrlList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_layout_rss, container, false);
        listData=new ArrayList<RssItem>();
        itemUrlList = new ArrayList<String>();
        listView = (RefreshableListView)view.findViewById(R.id.fRSS);
        //download rss feed
        //generateData();
        itemAdapter = new RssItemAdapter(view.getContext(), R.layout.postitem, listData);
        listView.setAdapter(itemAdapter);
        listView.setOnRefresh(this);
        listView.onRefreshStart();
        listView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    private void generateData()
    {
//        ArrayList<RssItem> rssItemList;
//        reader = new RssReader(Config.RSS_URL);
//        reader.fetchXML();
//
//        //parse the RSS in new thread
//        while(reader.parsingComplete);
//        rssItemList= reader.getDataArrayList();
//
//        listData= rssItemList;//.toArray(new RssItem[rssItemList.size()])
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            RssItem rssItem=listData.get(position-1);

            Bundle rssItemdetail = new Bundle();
            rssItemdetail.putString("content",rssItem.getDescription());

            Intent rssViewIntent = new Intent(MainActivity.ctx, RssViewActivity.class);
            rssViewIntent.putExtras(rssItemdetail);
            startActivity(rssViewIntent);
        }
    };

    private class RssDataController extends AsyncTask<String, Integer, ArrayList<RssItem>>
    {
        @Override
        protected ArrayList<RssItem> doInBackground(String... params)
        {
            String urlStr = params[0];
            InputStream iStream = null;
            ArrayList<RssItem> rssItemArrayList = new ArrayList<RssItem>();
            RssItem rssItem=null;
            try
            {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(10000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response=conn.getResponseCode();
                Log.d(Config.TAG, "The response is: " + response);
                iStream = conn.getInputStream();
            //parsing xml
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                xmlFactoryObject.setNamespaceAware(true);

                XmlPullParser myParser = xmlFactoryObject.newPullParser();
                //myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                myParser.setInput(iStream, null);
            //parsing
                int eventType= myParser.getEventType();
                String text=" ";
                String currTag=null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy.MM.dd, HH:mm:ss");

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    String name=myParser.getName();

                    if (eventType == XmlPullParser.START_DOCUMENT)
                    {

                    }
                    else if (eventType == XmlPullParser.START_TAG)
                    {
                        if(name.equals("item"))
                        {
                            currTag="item";
                            rssItem = new RssItem();
                        }
                        else if(name.equals("link"))
                        {
                            currTag="link";
                        }
                        else if (name.equals("pubDate"))
                        {
                            currTag="pubDate";
                        }
                        else if (name.equals("title"))
                        {
                            currTag="title";
                        }
                        else if (name.equals("description"))
                        {
                            currTag="description";
                        }
                        else if (name.equals("categroy"))
                        {
                            currTag="category";
                        }
                    }
                    else if (eventType==XmlPullParser.END_TAG)
                    {
                        if (myParser.getName().equals("item"))
                        {
                            Date postDate = dateFormat.parse(rssItem.getPubdate());
                            rssItem.setPubdate(targetFormat.format(postDate));

                            if (rssItem.getDescription()!=null && rssItem.getDescription().length() > 0)
                            {
                                String tempString=rssItem.getDescription();
                                try
                                {
                                    tempString=tempString.substring(tempString.indexOf("src=\"") + 5
                                            , tempString.indexOf("jpg")+3);
                                }
                                catch (StringIndexOutOfBoundsException e)
                                {
                                    try
                                    {
                                        tempString = tempString.substring(tempString.indexOf("src=\"") + 5
                                                , tempString.indexOf("png") + 3);
                                    }
                                    catch (StringIndexOutOfBoundsException j)
                                    {
                                        //invalid picture file extension...
                                        tempString="";
                                    }
                                }

                                rssItem.setImageUrl(tempString);
                            }
                            rssItemArrayList.add(rssItem);
                        }
                    }
                    else if (eventType==XmlPullParser.TEXT)
                    {
                        text = myParser.getText();
                        text=text.trim();
                        if (rssItem!=null)
                        {
                            switch (currTag)
                            {
                                case "title":
                                    if (text.length() != 0)
                                    {
                                        if (rssItem.getTitle() != null)
                                        {
                                            rssItem.title += text;
                                        }
                                        else
                                        {
                                            rssItem.setTitle(text);
                                        }
                                    }
                                    break;
                                case "link":
                                    if (text.length()!=0)
                                    {
                                        if (rssItem.getLink()!=null)
                                        {
                                            rssItem.link+=text;
                                        }
                                        else
                                        {
                                            rssItem.setLink(text);
                                        }
                                    }
                                    break;

                                case "category":
                                    if (text.length() != 0)
                                    {
                                        if (rssItem.getCategory() != null)
                                        {
                                            rssItem.category += text;
                                        } else
                                        {
                                            rssItem.setCategory(text);
                                        }
                                    }
                                    break;

                                case "pubDate":
                                    if (text.length() != 0)
                                    {
                                        if (rssItem.getPubdate() != null)
                                        {
                                            rssItem.pubdate += text;
                                        }
                                        else
                                        {
                                            rssItem.setPubdate(text);
                                        }
                                    }
                                    break;

                                case "description":
                                    if (text.length() != 0)
                                    {
                                        if (rssItem.getDescription() != null)
                                        {
                                            rssItem.description += text;
                                        } else
                                        {
                                            rssItem.setDescription(text);
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    eventType = myParser.next();
                } // end while

                iStream.close();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (XmlPullParserException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            return rssItemArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<RssItem> rssItems)
        {
            boolean isupdated = false;
            int j = 0;
            for (int i = 0; i < rssItems.size(); i++)
            {
                if (itemUrlList.contains(rssItems.get(i).link))
                {
                    continue;
                }
                else
                {
                    isupdated = true;
                    itemUrlList.add(rssItems.get(i).link);
                }
                if (isRefreshLoading)
                {
                    listData.add(j, rssItems.get(i));
                    j++;
                }
                else
                {
                    listData.add(rssItems.get(i));
                }
            }

            if (isupdated)
            {
                itemAdapter.notifyDataSetChanged();
            }

            isLoading = false;

            if (isRefreshLoading)
            {
                listView.onRefreshComplete();
            }
            else
            {
                listView.onLoadingMoreComplete();
            }
            super.onPostExecute(rssItems);
        }
    }

    @Override
    public void startFresh()
    {
        if (!isLoading)
        {
            isRefreshLoading = true;
            isLoading = true;
			/*
			 * Pagination:
			 *
			 * If your rss feed looks like:
			 *
			 * "http://jmsliu.com/feed?paged="
			 *
			 * You can try follow code for pagination.
			 *
			 * new RssDataController().execute(urlString + 1);
			 */
            if(enablePagnation)
            {
                new RssDataController().execute(Config.RSS_URL + 1);
            }
            else
            {
                new RssDataController().execute(Config.RSS_URL);
            }
        }
        else
        {
            listView.onRefreshComplete();
        }
    }

    @Override
    public void startLoadMore()
    {
        if (!isLoading)
        {
            isRefreshLoading = false;
            isLoading = true;
			/*
			 * Pagination:
			 *
			 * If your rss feed source looks like "http://jmsliu.com/feed?paged=",
			 * you can try follow code for pagination:
			 *
			 * new RssDataController().execute(urlString + (++pagnation));
			 *
			 * Otherwise, please use this:
			 *
			 * new RssDataController().execute(urlString);
			 */
            if(enablePagnation)
            {
                new RssDataController().execute(Config.RSS_URL + (+1));
            }
            else
            {
                new RssDataController().execute(Config.RSS_URL);
            }
        }
        else
        {
            listView.onLoadingMoreComplete();
        }
    }
}
