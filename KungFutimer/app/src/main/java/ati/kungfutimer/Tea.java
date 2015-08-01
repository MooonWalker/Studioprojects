package ati.kungfutimer;

public  class Tea 
{ 
	private int _id;
	private String _name="";
	private String _note1="";
	private String _note2="";
	private String _note3="";
	
	public Tea()
	{}
	
	
	public Tea(String name, String note1, String note2, String note3) 
	{
		_name = name;
		_note1 = note1;
		_note2 = note2;
		_note3 = note3;
	}
	
	public Tea(int parseInt, String name, String note1, String note2, String note3) 
	{
		_id = parseInt;
		_name = name;
		_note1 = note1;
		_note2 = note2;
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
	
	
	public String get_note1() {
		return _note1;
	}
	public void set_note1(String _note1) 
	{
		this._note1 = _note1;
	}
	public String get_note2() 
	{
		return _note2;
	}
	public void set_note2(String _note2) 
	{
		this._note2 = _note2;
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

