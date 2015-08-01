package ati.kungfutimer;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class PotSpawner 
{
	
	Context ctx;
	ImageView kiskanna[];
	View kiskannaview[];
	TextView tw[];
	LayoutParams vp;
	List<Brewing> brewings;
	
	public PotSpawner(List<Brewing> brews, Context context) 
	{
		
		ctx=context;
		this.brewings=brews;
		vp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}
	
	public void placePotsOnView(LinearLayout view)
	{
		
		kiskanna=new ImageView[brewings.size()];
		
		for (int i = 0; i < brewings.size(); i++)
		{
			kiskanna[i] = new ImageView(ctx);
			kiskanna[i].setLayoutParams(vp);
			kiskanna[i].setAdjustViewBounds(true);
			kiskanna[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
			//kiskanna.setBackgroundResource(R.drawable.kicsikanna);
			kiskanna[i].setImageResource(R.drawable.kicsikanna);
			
			view.addView(kiskanna[i]);
		}
		
		
	}
	
	public void placePotsOnView2(LinearLayout view)
	{
		
		kiskannaview=new View[brewings.size()];
		tw=new TextView[brewings.size()];
		
		for (int i = 0; i < brewings.size(); i++)
		{
			
			kiskannaview[i] = ((Activity)ctx).getLayoutInflater().inflate(R.layout.teabutton,
					view, false);
			tw[i]= (TextView) kiskannaview[i].findViewById(R.id.brewtime1);
			tw[i].setTextColor(0xffFFC919);
			tw[i].setText(Textformatter.MillisToMinSec(brewings.get(i).getBrewtime()));
			view.addView(kiskannaview[i]);
			
		}
		
	}


}
