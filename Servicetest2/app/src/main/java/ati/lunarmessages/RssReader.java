package ati.lunarmessages;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class RssReader
{
    private static final String RSS_LINK = "http://www.pcworld.com/index.rss";
    public static final String ITEMS = "items";
    public static final String RECEIVER = "receiver";

    public RssReader()
    {

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
