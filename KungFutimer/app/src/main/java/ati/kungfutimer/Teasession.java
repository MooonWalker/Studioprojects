package ati.kungfutimer;

import java.util.List;

public class Teasession 
{
	private Tea tea;
	private int teaID;
	private List<Brewing> brewings;
	private int brewidx=1;
	private boolean started=false;
	private int tealistpos;
	private DatabaseHandler db;
	private int counter=0;
	private long rowID=0;
	
	public Teasession(int teaID, List<Brewing> _brewings) 
	{
		super();
		this.teaID = teaID;
		this.brewings = _brewings;
		this.brewidx=1;
		this.tealistpos=0;
		this.tea=new Tea(teaID);
		this.db=new DatabaseHandler(MainActivity.ctx);
		this.tea = db.getTea(teaID);
	}

	public Teasession(int teaID, List<Brewing> brewings, int brewidx) {
		super();
		this.teaID = teaID;
		this.brewings = brewings;
		this.brewidx = brewidx;
		this.tealistpos=0;
	}

	public Teasession(Tea tea, int teaID, List<Brewing> _brewings, int brewidx) 
	{
		super();
		this.tea = tea;
		this.teaID = teaID;
		this.brewings = _brewings;
		this.brewidx = brewidx;
		this.tealistpos=0;
	}
	
	public int getTealistpos()
	{
		return tealistpos;
	}

	public void setTealistpos(int _tealistpos)
	{
		this.tealistpos = _tealistpos;
	}

	public void start()
	{
		started= true;
	}
	
	public void stop()
	{
		started=false;
		brewidx=1;
	}
	
	public int getBrewingtime(int _brewidx)
	{
	
		return brewings.get(_brewidx-1).getBrewtime();
	}
	
	public Tea getTea() 
	{
		return tea;
	}
	
	public void setTea(Tea tea) 
	{
		this.tea = tea;
	}
	
	public int getTeaID() 
	{
		return teaID;
	}
	
	public void setTeaID(int teaID) 
	{
		this.teaID = teaID;
	}
	
	public List<Brewing> getBrewings() 
	{
		return brewings;
	}
	
	public void setBrewings(List<Brewing> _brewings) 
	{
		this.brewings = _brewings;
	}
	
	public int getBrewidx() 
	{
		return brewidx;
	}
	
	public boolean setBrewidx(int brewidx) 
	{
		boolean result = false;
		if(brewidx <= this.brewings.size())
		{
			this.brewidx = brewidx;
			result=true;
		}else 
		{
			result= false;
		}
		
		return result;
		
	}
	public boolean isLastBrew()
	{
		boolean result = false;
		if((this.brewidx+1) <= this.brewings.size())
		{
			result=false;
		}
		else
		{
			result=true;
		}
		
		return result;
		
	}
	public boolean isStarted()
	{
		return started;
	}

	

	public void setNextBrew()
	{
			this.brewidx++;
	
	}

	public void writeHistory(long brewingtime)
	{
		if (brewings.size()>1)
		{	
			if(counter==0)
			{
				rowID=db.addSessionHist(tea.getName(), brewidx, brewingtime);
				db.close();
				counter++;
			}
			db.addBrewHist(rowID, tea.getName(), brewidx, brewingtime);
			db.close();
		}
		else if(brewings.size()==1)
		{
			rowID=db.addSessionHist(tea.getName(), brewidx, brewingtime);
			db.addBrewHist(rowID, tea.getName(), brewidx, brewingtime);
			db.close();
		}
		
		
		
	}
	

}
