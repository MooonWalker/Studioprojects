package ati.lunarmessages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


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
        viewItem.postTitleView.setText(rssItems[position].getTitle());
        viewItem.postDateView.setText(rssItems[position].getPubdate());

        // check if there is an image or invalid image extension (accepted jpg, png)
        if (rssItems[position].imageUrl == null || rssItems[position].imageUrl=="")
        {
            viewItem.postThumbView.setImageResource(R.drawable.ic_zetor_small);
            return convertView;
        }
        else
        {
            viewItem.rssItemThumbUrl=rssItems[position].getImageUrl();

            FileCache fileCache = new FileCache(MainActivity.ctx);
            File f = fileCache.getFile(viewItem.rssItemThumbUrl
                    .substring(viewItem.rssItemThumbUrl.lastIndexOf("/") + 1));
            //does it exist locally?
            if (f.exists())
            {
                Bitmap b=decodeFile(f);
                if (b!=null) viewItem.postThumbView.setImageBitmap(b);
            }
            else
            {
                //downloading and saving images
                new DownloadImageTask().execute(viewItem);
            }
            return convertView;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f)
    {
        Bitmap ret = null;
        try
        {
            FileInputStream is = new FileInputStream(f);
            ret = BitmapFactory.decodeStream(is, null, null);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
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
                BitmapFactory.Options o = new BitmapFactory.Options();
                //o.inJustDecodeBounds=true;
                // http://stackoverflow.com/questions/3331527/android-resize-a-large-bitmap-file-to-scaled-output-file
                viewItem.bitmap = BitmapFactory.decodeStream(imageURL.openStream());

                File cacheFile = new File(Config.cacheDir, viewItem.rssItemThumbUrl
                                        .substring(viewItem.rssItemThumbUrl.lastIndexOf("/") + 1));

                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                viewItem.bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                if (outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                }

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
