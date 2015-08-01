package ati.kungfutimer;

import java.util.List;

public class NewTeaWithBrewings
{
	Tea tea;
	List<Brewing> brewingslist;
	
	
	public NewTeaWithBrewings(Tea tea, List<Brewing> brewingslist)
	{
		super();
		this.tea = tea;
		this.brewingslist = brewingslist;
	}


	public Tea getTea()
	{
		return tea;
	}


	public void setTea(Tea tea)
	{
		this.tea = tea;
	}


	public List<Brewing> getBrewingslist()
	{
		return brewingslist;
	}


	public void setBrewingslist(List<Brewing> brewingslist)
	{
		this.brewingslist = brewingslist;
	}
	
	
}
