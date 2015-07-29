package ati.lunarmessages;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RssReader
{
    private String title = "title";
    private String link = "link";
    private String description = "description";
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private ArrayList<PostData> dataArrayList = new ArrayList();
    private PostData postData =null;


    public RssReader(String url)
    {
        this.urlString=url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getLink()
    {
        return link;
    }

    public ArrayList getDataArrayList()
    {
        return dataArrayList;
    }

    public String getDescription()
    {
        return description;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser)
    {
        int event;
        String text=null;
        try
        {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=myParser.getName();

                switch (event)
                {
                    case XmlPullParser.START_TAG:
                        if(name.equals("item"))
                        {
                            postData = new PostData();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(name.equals("title"))
                        {
                            if(postData!=null)postData.postTitle=text;
                            title = text;
                        }
                        else if(name.equals("link"))
                        {
                            link = text;
                        }
                        else if(name.equals("description"))
                        {
                            description = text;
                        }
                        else if (name.equals("item"))
                        {
                            dataArrayList.add(postData);
                        }
                        else
                        {

                        }
                        break;
                }
                event = myParser.next();
            } // end while
            parsingComplete = false;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fetchXML()
    {
        final Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    public List<RssItem> getItems() throws Exception
    {
//        XMLInputFactory inputFactory = XMLInputFactory.newInstance();;
//        SAXParser saxParser = factory.newSAXParser();
//        //Creates a new RssHandler which will do all the parsing.
          RssHandler handler = new RssHandler();
//        //Pass SaxParser the RssHandler that was created.
//        saxParser.parse(String.valueOf(url), handler);
       return handler.getRssItemList();
    }
}
