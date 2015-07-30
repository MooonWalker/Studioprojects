package ati.lunarmessages;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ati.lunarmessages.PostData;
import ati.lunarmessages.R;


public class PostItemAdapter extends ArrayAdapter<RssItem>
{
    private Activity myContext;

    private RssItem[] rssItems;

    public PostItemAdapter(Context context, int textViewResourceId, RssItem[] objects)
    {
        super(context, textViewResourceId, objects);
        myContext = (Activity) context;
        rssItems = objects;
    }


    static class ViewHolder
    {
        TextView postTitleView;
        TextView postDateView;
        ImageView postThumbView;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            LayoutInflater inflater = myContext.getLayoutInflater();
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

        if (rssItems[position].imageUrl == null)
        {
            viewHolder.postThumbView.setImageResource(R.drawable.ic_zetor_small);
        }

        viewHolder.postTitleView.setText(rssItems[position].getTitle());
        viewHolder.postDateView.setText(rssItems[position].getPubdate());

        return convertView;
    }
}
