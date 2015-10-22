package ati.kungfutimer;


	import android.app.Activity;
	import android.content.Context;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.ArrayAdapter;
	import android.widget.TextView;

public class Tealistadapter extends ArrayAdapter<Listelements>
	{

	    Context context;
	    int layoutResourceId;   
	    Listelements data[] = null;
		private boolean flag=false;
	   
	    public Tealistadapter(Context context, int layoutResourceId, Listelements[] data) 
	    {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	        
	    }
	    
	    public Tealistadapter(Context context, int layoutResourceId, Listelements[] data, boolean flag) 
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
	            holder.txtTea = (TextView)row.findViewById(R.id.txtTea);

	            holder.txtTea.setTextColor(0xFFDC2F0E);

	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (Holder)row.getTag();
	        }

			Listelements teaListelement = data[position];
	        holder.txtTea.setText(teaListelement.title);
	        
	        return row;
	    }
	   
	    static class Holder
	    {
	        TextView txtTea;
			//TextView txtTitle;
	    }
	}


	

