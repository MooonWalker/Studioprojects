package ati.kungfutimer;

public  class Tea 
{ 
	private int _id;
	private String _name="";
	private String temperature ="";
	private String remark ="";
	private String _note3="";
	
	public Tea()
	{}
	
	
	public Tea(String name, String note1, String note2, String note3) 
	{
		_name = name;
		temperature = note1;
		remark = note2;
		_note3 = note3;
	}
	
	public Tea(int parseInt, String name, String note1, String note2, String note3) 
	{
		_id = parseInt;
		_name = name;
		temperature = note1;
		remark = note2;
		_note3 = note3;
		
	}
	
	
	
	public Tea(int int1) 
	{
		_id = int1;
	}


	public int getID()
		{
		return this._id;
		}
	
	public void setID(int id)
		{
		this._id= id;
		}
	
	public String getName()
	{
		return this._name;
	}
	
	public void setName(String name)
	{
		this._name=name;
	}
	
	
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature)
	{
		this.temperature = temperature;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public String get_note3() 
	{
		return _note3;
	}
	public void set_note3(String _note3) 
	{
		this._note3 = _note3;
	}
	
	@Override
	  public String toString() 
	{
	    return _name;
	}
	
	
}

