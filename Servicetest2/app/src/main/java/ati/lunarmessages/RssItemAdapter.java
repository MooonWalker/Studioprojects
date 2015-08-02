package ati.lunarmessages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import ati.lunarmessages.PostData;
import ati.lunarmessages.R;


public class RssItemAdapter extends ArrayAdapter<RssItem>
{
    private Activity myContext;

    private RssItem[] rssItems;

    public RssItemAdapter(Context context, int textViewResourceId, RssItem[] objects)
    {
        super(context, textViewResourceId, objects);
        myContext = (Activity) context;
        rssItems = objects;
    }


    static class ViewItem
    {
        TextView postTitleView;
        TextView postDateView;
        ImageView postThumbView;
        String rssItemThumbUrl;
        public Bitmap bitmap;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewItem viewItem;

        if (convertView == null)
        {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.postitem, null);

            viewItem = new ViewItem();
            viewItem.postThumbView = (ImageView) convertView.findViewById(R.id.postThumb);
            viewItem.postTitleView = (TextView) convertView.findViewById(R.id.postTitleLabel);
            viewItem.postDateView = (TextView) convertView.findViewById(R.id.postDateLabel);
            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItem) convertView.getTag();
        }
        // check if there is an image or invalid image extension (accepted jpg, png)
        if (rssItems[position].imageUrl == null || rssItems[position].imageUrl=="")
        {
            viewItem.postThumbView.setImageResource(R.drawable.ic_zetor_small);
        }
        else
        {
            viewItem.rssItemThumbUrl=rssItems[position].getImageUrl();
            new DownloadImageTask().execute(viewItem);
        }

        viewItem.postTitleView.setText(rssItems[position].getTitle());
        viewItem.postDateView.setText(rssItems[position].getPubdate());


        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<ViewItem, Void, ViewItem>
    {

        @Override
        protected ViewItem doInBackground(ViewItem... params)
        {
            ViewItem viewItem=params[0];
            try
            {
                URL imageURL = new URL(viewItem.rssItemThumbUrl);
                viewItem.bitmap = BitmapFactory.decodeStream(imageURL.openStream());
            }
            catch (IOException e)
            {
                Log.e("error", "Downloading Image Failed");
                viewItem.bitmap = null;
            }
            return viewItem;
        }

        @Override
        protected void onPostExecute(ViewItem result)
        {
            if (result.bitmap==null)
            {
                result.postThumbView.setImageResource(R.drawable.ic_zetor_small);
            }
            else
            {
                result.postThumbView.setImageBitmap(result.bitmap);
            }

        }
    }
}
