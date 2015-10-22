package ati.kungfutimer;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TeaAdapter extends ArrayAdapter<Listelements>
{

    Context context;
    int layoutResourceId;   
    Listelements data[] = null; //Tea data
	private boolean flag=false;
   
    public TeaAdapter(Context context, int layoutResourceId, Listelements[] data) 
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        
    }
    
    public TeaAdapter(Context context, int layoutResourceId, Listelements[] data, boolean flag) 
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.flag =flag;
        
    }


	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
	{
        View row = convertView;
        Holder holder;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new Holder();
            holder.txtTitle1 = (TextView)row.findViewById(R.id.txtTitle1);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtTitle.setTextColor(0xFFDC2F0E);
            holder.txtTitle1.setTextColor(0xFFDC2F0E);
            row.setTag(holder);
        }
        else
        {
            holder = (Holder)row.getTag();
        }
       
        
        if (!flag)
		{
			if (MainActivity.isSelected[position] == true)
			{
				row.setBackgroundColor(0xffFFC919);
			}
			else
			{
				row.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		Listelements teaListelement = data[position];
        holder.txtTitle.setText(teaListelement.title);
        if (MainActivity.displaynotice)
        {
            //display a part of remark in the list
            if(teaListelement.remark.length()>0)
            {
                if (teaListelement.remark.length() > 10)
                {
                    holder.txtTitle1.setText(teaListelement.remark.substring(0, 10) + ", " + teaListelement.title1);
                } else
                {
                    holder.txtTitle1.setText(teaListelement.remark + ", " + teaListelement.title1);
                }
            }
        //no remark
            else
            {
                holder.txtTitle1.setText(teaListelement.title1);
            }
        }
        else //no display per preference
        {
            holder.txtTitle1.setText(teaListelement.title1);
        }

        return row;
    }
   
    static class Holder
    {
        TextView txtTitle1;
		TextView txtTitle;
    }
}