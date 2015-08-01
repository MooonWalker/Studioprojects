package ati.kungfutimer;

public class Listelements
{
	public int teaId=0;
	public String title1;
    public String title;
    
    
    public Listelements()
    {
        super();
    }
   
    public Listelements(String title1, String title) 
    {
        super();
        this.title1 = title1;
        this.title = title;
    }

	public Listelements(int _teaId, String title1, String title) 
	{
		super();
		this.teaId = _teaId;
		this.title1 = title1;
		this.title = title;
	}
}