package ati.kungfutimer;

public class Brewing 
{
	private int brewnr, brewtime, teaID = 0;

	public Brewing(int brewnr, int brewtime, int teaID) 
	{
		super();
		this.brewnr = brewnr;
		this.brewtime = brewtime;
		this.teaID = teaID;
	}

	public Brewing() 
	{
	
	}

	public int getBrewnr() {
		return brewnr;
	}

	public void setBrewnr(int brewnr) {
		this.brewnr = brewnr;
	}

	public int getBrewtime() {
		return brewtime;
	}

	public void setBrewtime(int brewtime) {
		this.brewtime = brewtime;
	}

	public int getTeaID() {
		return teaID;
	}

	public void setTeaID(int teaID) {
		this.teaID = teaID;
	}
	
}
