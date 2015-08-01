package ati.kungfutimer;

import java.util.List;

public class HSessionBrew
{
	private List<SessionH> sH;
	private List<BrewingH> bH;
	private String uuid=null;
	
	public HSessionBrew()
	{
		super();
	}
	public HSessionBrew(List<SessionH> sH, List<BrewingH> bH, String uuid)
	{
		super();
		this.sH = sH;
		this.bH = bH;
		this.setUuid(uuid);
	}
	public List<SessionH> getsH()
	{
		return sH;
	}
	public void setsH(List<SessionH> sH)
	{
		this.sH = sH;
	}
	public List<BrewingH> getbH()
	{
		return bH;
	}
	public void setbH(List<BrewingH> bH)
	{
		this.bH = bH;
	}
	public String getUuid()
	{
		return uuid;
	}
	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}
	
}
