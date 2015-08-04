package ati.lunarmessages;


import android.os.Handler;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class RssReader
{

    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private ArrayList<RssItem> dataArrayList = new ArrayList();
    //private PostData postData =null;
    private RssItem rssItem =null;


    public RssReader(String url)
    {
        this.urlString=url;
    }

    public ArrayList getDataArrayList()
    {
        return dataArrayList;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser)
    {
        int event;
        String text=" ";
        try
        {
            event = myParser.getEventType();
            String currTag=null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy.MM.dd, HH:mm:ss");

            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=myParser.getName();

                if (event == XmlPullParser.START_DOCUMENT)
                {

                }
                else if (event == XmlPullParser.START_TAG)
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
                else if (event==XmlPullParser.END_TAG)
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
                        dataArrayList.add(rssItem);
                    }
                }
                else if (event==XmlPullParser.TEXT)
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
                event = myParser.next();
            } // end while
            parsingComplete = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //TODO stop that thread somehow
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
                    return;
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
