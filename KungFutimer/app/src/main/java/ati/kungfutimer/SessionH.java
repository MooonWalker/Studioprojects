package ati.kungfutimer;

public class SessionH
{
	String sessionid = null;
    String teaname = null;
    String sessiondate = null;
    
    public SessionH()
	{
    	super();
	}
	public SessionH(String sessionid, String teaname, String sessiondate)
	{
		super();
		this.sessionid = sessionid;
		this.teaname = teaname;
		this.sessiondate = sessiondate;
	}
	
	

	public String getSessionid()
	{
		return sessionid;
	}
	public void setSessionid(String sessionid)
	{
		this.sessionid = sessionid;
	}
	public String getTeaname()
	{
		return teaname;
	}
	public void setTeaname(String teaname)
	{
		this.teaname = teaname;
	}
	public String getSessiondate()
	{
		return sessiondate;
	}
	public void setSessiondate(String sessiondate)
	{
		this.sessiondate = sessiondate;
	}
     
}
