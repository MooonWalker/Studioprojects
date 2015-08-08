package ati.lunarmessages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import java.util.ArrayList;


public class RssItemAdapter extends ArrayAdapter<RssItem>
{
    private LayoutInflater inflater;
    private Activity myContext;
    //private RssItem[] rssItems;
    private ArrayList<RssItem> rssItems;

    public RssItemAdapter(Context context, int textViewResourceId, ArrayList<RssItem> objects)
    {
        super(context, textViewResourceId, objects);
        myContext = (Activity) context;
        inflater = ((Activity) context).getLayoutInflater();
        rssItems = objects;
    }

    //viewHOLDER
    static class ViewHolder
    {
        TextView postTitleView;
        TextView postDateView;
        ImageView postThumbView;
        String rssItemThumbUrl;
        public Bitmap bitmap;
    }
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.postitem, null);

            viewHolder = new ViewHolder();
            viewHolder.postThumbView = (ImageView) convertView.findViewById(R.id.postThumb);
            viewHolder.postTitleView = (TextView) convertView.findViewById(R.id.postTitleLabel);
            viewHolder.postDateView = (TextView) convertView.findViewById(R.id.postDateLabel);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (rssItems.get(position).imageUrl == null)
        {
            viewHolder.postThumbView
                    .setImageResource(R.drawable.postthumb_loading);
        }

        viewHolder.postTitleView.setText(rssItems.get(position).getTitle());
        viewHolder.postDateView.setText(rssItems.get(position).getPubdate());

    // check if there is an image or invalid image extension (accepted jpg, png)
        if (rssItems.get(position).imageUrl == null || rssItems.get(position).imageUrl=="")
        {
            viewHolder.postThumbView.setImageResource(R.drawable.ic_zetor_small);
            return convertView;
        }
        else
        {
            viewHolder.rssItemThumbUrl=rssItems.get(position).getImageUrl();

            FileCache fileCache = new FileCache(MainActivity.ctx);
            File f = fileCache.getFile(viewHolder.rssItemThumbUrl
                    .substring(viewHolder.rssItemThumbUrl.lastIndexOf("/") + 1));
        //does it exist locally?
            if (f.exists())
            {
                Bitmap b=decodeFile(f);
                if (b!=null) viewHolder.postThumbView.setImageBitmap(b);
            }
            else
            {
                viewHolder.postThumbView.setImageResource(R.drawable.ic_zetor_small);

            //downloading resizing and saving images
                new DownloadImageTask().execute(viewHolder);
            }
            return convertView;
        }
    }

//decodes image from file
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

    private class DownloadImageTask extends AsyncTask<ViewHolder, Void, ViewHolder>
    {
        @Override
        protected ViewHolder doInBackground(ViewHolder... params)
        {
            ViewHolder viewHolder =params[0];
            try
            {
                URL imageURL = new URL(viewHolder.rssItemThumbUrl);
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds=true;
            // http://stackoverflow.com/questions/3331527/android-resize-a-large-bitmap-file-to-scaled-output-file
                BitmapFactory.decodeStream(imageURL.openStream(),null,options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;
            //target size 100*100
                options.inSampleSize=Controller.calculateInSampleSize(options,100,100);
                options.inJustDecodeBounds=false;

                viewHolder.bitmap = BitmapFactory.decodeStream(imageURL.openStream(),null,options);

                File cacheFile = new File(Config.cacheDir, viewHolder.rssItemThumbUrl
                                        .substring(viewHolder.rssItemThumbUrl.lastIndexOf("/") + 1));
            //save image as png to file
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                viewHolder.bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                if (outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                }

            }
            catch (IOException e)
            {
                Log.e("error", "Downloading Image Failed");
                viewHolder.bitmap = null;
            }
            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result)
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
