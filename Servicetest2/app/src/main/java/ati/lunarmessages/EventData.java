package ati.lunarmessages;

/**
 * Created by Ati on 2015.07.31..
 * Parse input string: "2015.07.31. 20:51"
 * to integers
 */
public class EventData
{
    private int YEAR;
    private int MONTH;
    private int DAY;
    private int HOUR;
    private int MINUTE;
    private String[] strings;

    public EventData(String txt)
    {
        this.YEAR=Integer.parseInt(txt.substring(0,txt.indexOf(".")));
        this.strings=txt.split("\\.");
        this.MONTH=Integer.parseInt(strings[1])-1;
        this.DAY=Integer.parseInt(strings[2]);
        this.HOUR=Integer.parseInt(strings[3].trim().substring(0,strings[3].indexOf(":")-1));
        this.MINUTE=Integer.parseInt(strings[3].trim().substring(strings[3].indexOf(":")));
    }

    public int getYEAR()
    {
        return YEAR;
    }

    public void setYEAR(int YEAR)
    {
        this.YEAR = YEAR;
    }

    public int getMONTH()
    {
        return MONTH;
    }

    public void setMONTH(int MONTH)
    {
        this.MONTH = MONTH;
    }

    public int getDAY()
    {
        return DAY;
    }

    public void setDAY(int DAY)
    {
        this.DAY = DAY;
    }

    public int getHOUR()
    {
        return HOUR;
    }

    public void setHOUR(int HOUR)
    {
        this.HOUR = HOUR;
    }

    public int getMINUTE()
    {
        return MINUTE;
    }

    public void setMINUTE(int MINUTE)
    {
        this.MINUTE = MINUTE;
    }
}
