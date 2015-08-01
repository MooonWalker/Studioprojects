package ati.kungfutimer;

import java.util.ArrayList;
import java.util.List;

public class LearningSession 
{
	private Teasession newsession;
	private boolean isStopperStarted;
	private List<Brewing> brewings;
	

	public LearningSession()
	{
		setStopperStarted(false);
	}
	
	public LearningSession(int teaID)
	{
		super();
		newsession= new Teasession(teaID, null);
		brewings = new ArrayList<Brewing>();
		
	}
	
	public List<Brewing> getBrewings()
	{
		return brewings;
	}
	
	private void setBrewings(List<Brewing> brewings)
	{
		this.brewings = brewings;
	}
	
	public boolean isStopperStarted()
	{
		return isStopperStarted;
	}
	
	public void setStopperStarted(boolean isStopperStarted)
	{
		this.isStopperStarted = isStopperStarted;
	}
	
	public void addNewBrew(long elapsedmillis2)
	{
		brewings.add(new Brewing(brewings.size()+1, (int)elapsedmillis2, newsession.getTeaID()));		
	}
	
	
	
}
