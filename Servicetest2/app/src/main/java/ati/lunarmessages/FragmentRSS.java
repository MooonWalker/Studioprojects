package ati.lunarmessages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentRSS extends Fragment
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
        this.generateDummyData();

        ListView listView = (ListView)view.findViewById(R.id.fRSS);
        PostItemAdapter itemAdapter = new PostItemAdapter(view.getContext(), R.layout.postitem, listData);
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

//    private class RssDataController extends AsyncTask<String, Integer, ArrayList<PostData>>
//    {
//        private RSSXMLTag currentTag;
//
//        @Override
//        protected ArrayList<PostData> doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            String urlStr = params[0];
//            InputStream is = null;
//            ArrayList<PostData> postDataList = new ArrayList<PostData>();
//            try {
//                URL url = new URL(urlStr);
//                HttpURLConnection connection = (HttpURLConnection) url
//                        .openConnection();
//                connection.setReadTimeout(10 * 1000);
//                connection.setConnectTimeout(10 * 1000);
//                connection.setRequestMethod("GET");
//                connection.setDoInput(true);
//                connection.connect();
//                int response = connection.getResponseCode();
//                Log.d("debug", "The response is: " + response);
//                is = connection.getInputStream();
//
//                // parse xml after getting the data
//                XmlPullParserFactory factory = XmlPullParserFactory
//                        .newInstance();
//                factory.setNamespaceAware(true);
//                XmlPullParser xpp = factory.newPullParser();
//                xpp.setInput(is, null);
//
//                int eventType = xpp.getEventType();
//                PostData pdData = null;
//                SimpleDateFormat dateFormat = new SimpleDateFormat(
//                        "EEE, DD MMM yyyy HH:mm:ss");
//                while (eventType != XmlPullParser.END_DOCUMENT) {
//                    if (eventType == XmlPullParser.START_DOCUMENT) {
//
//                    } else if (eventType == XmlPullParser.START_TAG) {
//                        if (xpp.getName().equals("item")) {
//                            pdData = new PostData();
//                            currentTag = RSSXMLTag.IGNORETAG;
//                        } else if (xpp.getName().equals("title")) {
//                            currentTag = RSSXMLTag.TITLE;
//                        } else if (xpp.getName().equals("link")) {
//                            currentTag = RSSXMLTag.LINK;
//                        } else if (xpp.getName().equals("pubDate")) {
//                            currentTag = RSSXMLTag.DATE;
//                        }
//                    } else if (eventType == XmlPullParser.END_TAG) {
//                        if (xpp.getName().equals("item")) {
//                            // format the data here, otherwise format data in
//                            // Adapter
//                            Date postDate = dateFormat.parse(pdData.postDate);
//                            pdData.postDate = dateFormat.format(postDate);
//                            postDataList.add(pdData);
//                        } else {
//                            currentTag = RSSXMLTag.IGNORETAG;
//                        }
//                    } else if (eventType == XmlPullParser.TEXT) {
//                        String content = xpp.getText();
//                        content = content.trim();
//                        Log.d("debug", content);
//                        if (pdData != null) {
//                            switch (currentTag) {
//                                case TITLE:
//                                    if (content.length() != 0) {
//                                        if (pdData.postTitle != null) {
//                                            pdData.postTitle += content;
//                                        } else {
//                                            pdData.postTitle = content;
//                                        }
//                                    }
//                                    break;
//                                case LINK:
//                                    if (content.length() != 0) {
//                                        if (pdData.postLink != null) {
//                                            pdData.postLink += content;
//                                        } else {
//                                            pdData.postLink = content;
//                                        }
//                                    }
//                                    break;
//                                case DATE:
//                                    if (content.length() != 0) {
//                                        if (pdData.postDate != null) {
//                                            pdData.postDate += content;
//                                        } else {
//                                            pdData.postDate = content;
//                                        }
//                                    }
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                    }
//
//                    eventType = xpp.next();
//                }
//                Log.v("tst", String.valueOf((postDataList.size())));
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (XmlPullParserException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (ParseException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            return postDataList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<PostData> result) {
//            // TODO Auto-generated method stub
//            for (int i = 0; i < result.size(); i++) {
//                listData.add(result.get(i));
//            }
//
//            postAdapter.notifyDataSetChanged();
//        }
//    }

    private class HTTPDownloadTask extends AsyncTask<String, Integer, PostData[]>
    {
        @Override
        protected PostData[] doInBackground(String... params)
        {
            // TODO Auto-generated method stub
            String urlStr = params[0];
            InputStream is = null;
            try
            {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10*1000);
                connection.setConnectTimeout(10*1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                Log.d(Config.TAG, "The response is: " + response);
                is = connection.getInputStream();

                //read string
                final int bufferSize = 1024;
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                while(true)
                {
                    int count = is.read(buffer, 0, bufferSize);
                    if(count == -1)
                    {
                        break;
                    }

                    os.write(buffer);
                }
                os.close();

                String result = new String(os.toByteArray(), "UTF-8");
                Log.d(Config.TAG, result);
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}
