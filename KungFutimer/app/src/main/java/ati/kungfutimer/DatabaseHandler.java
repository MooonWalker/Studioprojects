package ati.kungfutimer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
  
    // Database Name
    private static final String DATABASE_NAME = "Kungfutimer.db";
 
    // Contacts table name
    public static final String TABLE_TEAS = "tblTeas";
    public static final String TABLE_BREWINGS = "tblBrews";
    public static final String TABLE_SESSIONS_H = "tblSessionH";
    public static final String TABLE_BREWINGS_H = "tblBrewingsH";
    
 
    // Tea Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NOTE1 = "note1";
    private static final String KEY_NOTE2 = "note2";
    private static final String KEY_NOTE3 = "note3";
    
    // Brewings Table Columns names
    private static final String bKEY_BREWNUM = "brew_num";
    private static final String bKEY_BREWTIME = "brew_time";
    private static final String bKEY_TEAID = "tea_ID";
    
    // Session history Columns
    private static final String H_SESSION_ID ="sessionID";
    private static final String H_TEA_NAME ="teaName";
    private static final String H_SESSION_DATE="sessionDate";
    private static final String H_SESSION_EXPORTED="exported";
    
    
    // Brewings history Columns
    private static final String HB_SESSION_ID="sessionID";
    private static final String HB_BREWNUM="brewnum";
    private static final String HB_BREWTIME="brewtime";
    
    
	private static final String DATABASE_PATH = "/data/data/ati.kungfutimer/databases/";
	public static final String DB_BACKUP_PATH = "/kungfutimer/backup";

	public static final String[] TABLE_TEA_COLUMN_KEYS = {"id", "name", "note1", "note2", "note3"};
	public static final String[] TABLE_BREWINGS_COLUMN_KEYS = {"brew_num", "brew_time", "tea_ID"};
	public static final String[] TABLE_SESSION_H_COLUMN_KEYS = {"sessionID", "teaName", "sessionDate", "exported"};
	public static final String[] TABLE_BREWINGS_H_COLUMN_KEYS = {"sessionID", "brewnum", "brewtime"};
	
    public static final String[] TABLE_NAMES = {TABLE_TEAS, TABLE_BREWINGS,TABLE_BREWINGS_H,TABLE_SESSIONS_H};
   
    
	final Context dbContext;

	private SQLiteDatabase dataBase;

	 
	public DatabaseHandler(Context context) 
	{
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        dbContext = context;
	       
	        if (checkDataBase()) 
	        {
	            openDataBase();
	        } 
	        else
	        {
	        	
	        	try
				{
	                this.getReadableDatabase();
	                copyDataBase(dbContext, DATABASE_PATH + DATABASE_NAME,0); //local copy
	                this.close();
	                openDataBase();	               

	            }
				catch (IOException e)
				{
	                throw new Error("Error copying database");
	            }
	            Toast.makeText(context, "Initial database is created", Toast.LENGTH_LONG).show();
	        }

	}
	    
	

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		 String CREATE_TEAS_TABLE = "CREATE TABLE " + TABLE_TEAS + " ("
	                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT NOT NULL UNIQUE, "
	                + KEY_NOTE1 + " TEXT, " + KEY_NOTE2 + " TEXT, " + KEY_NOTE3 + " TEXT)";
	        
		 db.execSQL(CREATE_TEAS_TABLE);
		 
		 String CREATE_BREWINGS_TABLE = "CREATE TABLE " + TABLE_BREWINGS + " ("
	                + bKEY_BREWNUM + " INTEGER, " + bKEY_BREWTIME + " INTEGER, "
	                + bKEY_TEAID + " INTEGER)";
	        
		 db.execSQL(CREATE_BREWINGS_TABLE);
		 
		 String CREATE_BREWINGS_H_TABLE = "CREATE TABLE " + TABLE_BREWINGS_H + " ("
	                + HB_SESSION_ID + " INTEGER NOT NULL REFERENCES tblSessionH ( sessionID ) ON DELETE CASCADE, "
				 + HB_BREWNUM + " INTEGER NOT NULL, "
	                + HB_BREWTIME + " INTEGER NOT NULL)";
		 db.execSQL(CREATE_BREWINGS_H_TABLE);
		 
		 String CREATE_SESSIONS_H_TABLE = "CREATE TABLE " + TABLE_SESSIONS_H + " ("
	                + H_SESSION_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, "
				 + H_TEA_NAME + " TEXT, "
	                + H_SESSION_DATE + " DATE, exported BOOLEAN DEFAULT 0)";
		 db.execSQL(CREATE_SESSIONS_H_TABLE);
				
	}
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
//		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAS);
//		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_BREWINGS);
//		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_BREWINGS_H);
//		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS_H);
//		 
//		 // Create tables again:
//	     onCreate(db);
		
		
			if (oldVersion==1)
			{
				db.execSQL("ALTER TABLE " + TABLE_SESSIONS_H
						+ " ADD COLUMN exported BOOLEAN DEFAULT 0");
				db.execSQL("CREATE TABLE IF NOT EXISTS tblUUID (UUID TEXT)");
				createUUID(db);
				Toast.makeText(dbContext, "Database upgraded to v2.",
						Toast.LENGTH_LONG).show();
			}
	
		
	}
	
	private void createUUID()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		UUID uuid = UUID.randomUUID();
		ContentValues values = new ContentValues();
		
	    values.put("UUID", uuid.toString());		
		db.insertOrThrow("tblUUID", null, values);
		db.close();
		
	}
	
	private void createUUID(SQLiteDatabase db)
	{
			UUID uuid = UUID.randomUUID();
			ContentValues values = new ContentValues();
			
		    values.put("UUID", uuid.toString());		
			db.insertOrThrow("tblUUID", null, values);
			Log.v("db log", "UUID created");
		
	}

	public File copyDataBase(Context context, String toFullPath, int target) throws IOException 		//target 0=local copy, 1=copy to SD-card
	{
		File rFile = null;
		switch (target)
		{
		case 0:
			InputStream myInput = dbContext.getAssets().open(DATABASE_NAME);
		    String outFileName = toFullPath;
		    OutputStream myOutput = new FileOutputStream(outFileName);

		    byte[] buffer = new byte[1024];
		    int length;
		    while ((length = myInput.read(buffer))>0)
		    {
		        myOutput.write(buffer, 0, length);
		    }

		    myOutput.flush();
		    myOutput.close();
		    myInput.close();
		    
		    
			break;
		
		case 1:
			
			String state = Environment.getExternalStorageState();
			    if (Environment.MEDIA_MOUNTED.equals(state)) 
			    {	
			    	File fileBackupDir = new File(Environment.getExternalStorageDirectory(),DB_BACKUP_PATH);
			    	InputStream mInput = new FileInputStream(DATABASE_PATH+DATABASE_NAME);
			    	if (!fileBackupDir.exists()) 
			    	{
			            fileBackupDir.mkdirs();
			        }
			    	
				    OutputStream myOutp = new FileOutputStream(fileBackupDir+"/"+DATABASE_NAME);

				    byte[] buff = new byte[1024];
				    int lengt;
				    while ((lengt = mInput.read(buff))>0)
				    {
				        myOutp.write(buff, 0, lengt);
				    }

				    myOutp.flush();
				    myOutp.close();
				    mInput.close();	
				    
				    rFile=fileBackupDir;
				    
			    }
			    
			    		
			break;
		
		}
		return rFile;
		
	    
	}

	public void openDataBase() throws SQLException 
	{
	    String dbPath = DATABASE_PATH + DATABASE_NAME;
	    dataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
	    this.close();
	}
	
	private boolean checkDataBase() 
	{
	    SQLiteDatabase checkDB = null;
	    boolean exist = false;
	    try 
	    {
	        String dbPath = DATABASE_PATH + DATABASE_NAME;
	        checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
	    } 
	    catch (SQLiteException e) 
	    {
	        Log.v("db log", "database does't exist");
	    }

	    if (checkDB != null) 
	    {
	        exist = true;
	        checkDB.close();
	    }
	    return exist;
	}
	
	public long addTea(Tea tea) throws SQLiteException
	{	
		long rowid = 0;
		
			SQLiteDatabase db = this.getWritableDatabase();
			 
		    ContentValues values = new ContentValues();
		    values.put(KEY_NAME, tea.getName()); 
		    values.put(KEY_NOTE1, tea.get_note1());
		    values.put(KEY_NOTE2, tea.get_note2());
		    values.put(KEY_NOTE3, tea.get_note3());
		 
		    // Inserting Row
		    
		    try
			{
				rowid=db.insertOrThrow(TABLE_TEAS, null, values);
			} 
		    catch (SQLiteConstraintException e)
		    {
		    	rowid=-19;		    	
		    }
		    catch (SQLiteException e) 
		    {
		    	rowid=-1;
			}
		    finally
		    {
		    	db.close(); // Closing database connection
		    	
		    }	    
		    
		    return rowid;	
	    
	}
	
	public String addBrewings(List<Brewing> brewingsss)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		try
		{
			for (int i = 0; i < brewingsss.size(); i++)
			{
				values.put(bKEY_BREWNUM, brewingsss.get(i).getBrewnr());
				values.put(bKEY_BREWTIME, brewingsss.get(i).getBrewtime());
				values.put(bKEY_TEAID, brewingsss.get(i).getTeaID());
				db.insertOrThrow(TABLE_BREWINGS, null, values);
			}
			db.close();
			return"";
		} 
		catch (SQLiteException e)
		{
			db.close();
			return e.getMessage();
		}
		
	}
	 
	public void addBrewing(Brewing brewing)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(bKEY_BREWNUM, brewing.getBrewnr()); 
	    values.put(bKEY_BREWTIME, brewing.getBrewtime());
	    values.put(bKEY_TEAID, brewing.getTeaID());
	    
	    db.insert(TABLE_BREWINGS, null, values);
	    db.close(); // Closing database connection
	}
	// Getting single tea
	public Tea getTea(int id) 
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Tea tea;
	    Cursor cursor = db.query(TABLE_TEAS, new String[] { KEY_ID,
	            KEY_NAME, KEY_NOTE1, KEY_NOTE2, KEY_NOTE3 }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	    {
	        cursor.moveToFirst();
	 
	    tea = new Tea(Integer.parseInt(cursor.getString(0)),
	            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
	    // return tea
	    cursor.close();
	    db.close();
	    return tea;
	    }
	    else return null;
	    
	}
	
	public void addBrewHist(long rowID2, String teaName, int brewidx, long brewingtime)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues bValues = new ContentValues();
	
	    bValues.put(HB_SESSION_ID, rowID2);
	    bValues.put(HB_BREWNUM, brewidx);
	    bValues.put(HB_BREWTIME, brewingtime);
	    
	    db.insertOrThrow(TABLE_BREWINGS_H, null, bValues);
	    
	    db.close();
	}
	
	public long addSessionHist(String teaName, int brewidx, long brewingtime)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues sValues = new ContentValues();
		
		long rowid=0;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		String formattedDate = df.format(c.getTime());

	    sValues.put(H_TEA_NAME, teaName);
	    sValues.put(H_SESSION_DATE, formattedDate);
		
	    rowid=db.insertOrThrow(TABLE_SESSIONS_H, null, sValues);
	    	    	    
	    db.close();
	    
	    return rowid;
	}	
	 
	public int getTeaIDByName(String name)
	{
		int id=0;
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_TEAS, new String[] { KEY_ID }, KEY_NAME + "=?",
	            new String[] { name }, null, null, null, null);
	    if (cursor != null)
	    {
	    	cursor.moveToFirst();
	    	Tea tea = new Tea(cursor.getInt(0));
		    id = tea.getID();
		    cursor.close();
	    }
	
		return id;
	}
	
	public List<Brewing> getBrewings(int teaid)
	{
		List<Brewing> brewings=new ArrayList<Brewing>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_BREWINGS, new String[] { bKEY_BREWNUM,
	            bKEY_BREWTIME, bKEY_TEAID }, bKEY_TEAID + "=?",
	            new String[] { String.valueOf(teaid) }, null, null, null, null);
		if (cursor.moveToFirst()) {
	        do {
	            Brewing brewing = new Brewing();
	            brewing.setBrewnr(cursor.getInt(0));
	            brewing.setBrewtime(cursor.getInt(1));
	            brewing.setTeaID(cursor.getInt(2));
	            // Adding contact to list
	            brewings.add(brewing);
	        } while (cursor.moveToNext());
	    }
	   cursor.close();
	   this.close();
		return brewings;
	}
	
	public Cursor fetchAllTeas()
	{
		String selectQuery = "SELECT id as _id, name FROM " + TABLE_TEAS;
		 
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		
		return cursor;		
	}
	
	public List<Tea> getAllTeas() 
	{
		List<Tea> teaList = new ArrayList<Tea>();
	    // Select All Query
	    String selectQuery = "SELECT * FROM " + TABLE_TEAS;
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            Tea tea = new Tea();
	            tea.setID(Integer.parseInt(cursor.getString(0)));
	            tea.setName(cursor.getString(1));
	            tea.set_note1(cursor.getString(2));
	            tea.set_note2(cursor.getString(3));
	            tea.set_note3(cursor.getString(4));
	            // Adding contact to list
	            teaList.add(tea);
	        } while (cursor.moveToNext());
	    }
	    
	    // return contact list
	    cursor.close();
	    db.close();
	    this.close();
	    return teaList;
	}
	 
	public int getBrewingsCount(int teaid)
	{
		int cnt=0;
		 String countQuery = "SELECT * FROM " + TABLE_BREWINGS +" WHERE tea_ID="+teaid;
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        
	 
	        // return count
	        cnt=cursor.getCount();
	        cursor.close();
	        db.close();
	        this.close();
	     return cnt;
	}
	
	// Getting contacts Count
	public int getTeasCount() 
	{
		int cnt=0;
		 String countQuery = "SELECT * FROM " + TABLE_TEAS;
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        	 
	        // return count
	        cnt=cursor.getCount();
	        cursor.close();
	        db.close();
	        this.close();
	        return cnt;
	}
	
	//Updating single brewing
	public int updateBrewing(Brewing brewing)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rows; 
	    ContentValues values = new ContentValues();
	    //values.put(bKEY_BREWNUM, brewing.getBrewnr());
	    values.put(bKEY_BREWTIME, brewing.getBrewtime());
	    //values.put(bKEY_TEAID, brewing.getTeaID());
	    rows= db.update(TABLE_BREWINGS, values, bKEY_TEAID + " = ? AND " + bKEY_BREWNUM + " =?",
	    		new String[] { String.valueOf(brewing.getTeaID()) , String.valueOf(brewing.getBrewnr()) });
	    
	    db.close();
	    this.close();	    
	    
		return rows;				
	}
	
	// Updating single tea
	public int updateTea(Tea tea) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rows; 
	    ContentValues values = new ContentValues();
	    values.put(KEY_NAME, tea.getName());
	    values.put(KEY_NOTE1, tea.get_note1());
	    values.put(KEY_NOTE2, tea.get_note2());
	    values.put(KEY_NOTE3, tea.get_note3());
	 
	    rows= db.update(TABLE_TEAS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(tea.getID()) });
	    db.close();
	    this.close();
	    return rows;
	}
	 
	// Deleting single tea
	public void deleteTea(Tea tea) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_TEAS, KEY_ID + " = ?",
	            new String[] { String.valueOf(tea.getID()) });
	    db.close();
	}
	
	public void deleteTea(int teaid) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_TEAS, KEY_ID + " = ?", new String[]{""+teaid});
	    db.close();
	    this.close();
	}
	
	public void deleteBrewings(int teaid)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BREWINGS, bKEY_TEAID + " = ?", new String[]{""+teaid});
	    db.close();
	    this.close();
	}

	@Override
	public synchronized void close() {
		
		super.close();
		if(dataBase!=null)
		{
			dataBase.close();
		}
		
	}

	public long getBrewStatCount()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		long count=0;
		String countQuery = "SELECT * FROM " + TABLE_BREWINGS_H;
		
		Cursor cursor = db.rawQuery(countQuery, null);
		
		count = cursor.getCount();
		
		cursor.close();
		db.close();
		this.close();
		return count;				
	}

	public List<SessionH> getSessionsToSend()
	{
		List<SessionH> sessions=new ArrayList<SessionH>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SESSIONS_H, new String[] { H_SESSION_ID,
	            H_TEA_NAME, H_SESSION_DATE }, H_SESSION_EXPORTED + "=?",
	            new String[] { String.valueOf(0) }, null, null, null, null);
		if (cursor.moveToFirst()) {
	        do {
	            SessionH session = new SessionH();
	            session.setSessionid(cursor.getString(0));
	            session.setTeaname(cursor.getString(1));
	            session.setSessiondate(cursor.getString(2));
	           
	            sessions.add(session);
	            
	        } while (cursor.moveToNext());
	    }
	   cursor.close();
	   this.close();
		
	   return sessions;
	}

	public List<BrewingH> getBrewingsToSend()
	{
		List<BrewingH> brewings=new ArrayList<BrewingH>();
		SQLiteDatabase db = this.getReadableDatabase();
		String sqlString= "SELECT * FROM " + TABLE_BREWINGS_H + 
				" INNER JOIN " + TABLE_SESSIONS_H + " ON "+ TABLE_SESSIONS_H+"." + H_SESSION_ID + " = " +
				TABLE_BREWINGS_H+"."+HB_SESSION_ID +" WHERE "+TABLE_SESSIONS_H+"."+H_SESSION_EXPORTED+
				" = 0";
		
		Cursor cursor = db.rawQuery(sqlString, null);	            
		if (cursor.moveToFirst()) {
	        do {
	            BrewingH brewing = new BrewingH();
	            brewing.setSessionid(cursor.getString(0));
	            brewing.setBrewnum(cursor.getString(1));
	            brewing.setBrewtime(cursor.getString(2));
	           
	            brewings.add(brewing);
	            
	        } while (cursor.moveToNext());
	    }
	   cursor.close();
	   this.close();
		
	   return brewings;
	}

	public void flagSessionsSended(List<SessionH> sessions)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rows; 
		ContentValues values = new ContentValues();
		
		for (int i = 0; i < sessions.size(); i++)
		{
		    values.put(H_SESSION_EXPORTED, String.valueOf(1));
		    rows= db.update(TABLE_SESSIONS_H, values, H_SESSION_ID+ " = ?", 
		    		new String[] {sessions.get(i).getSessionid()});
		}
	    db.close();
	    this.close();	    
	    
		return;
	}



	public String getUUID()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		String uuid=null;
		String sQuery = "SELECT UUID FROM tblUUID";
		
		Cursor cursor = db.rawQuery(sQuery, null);
		
		cursor.moveToFirst();
		uuid = cursor.getString(0);
		
		cursor.close();
		db.close();
		this.close();
		return uuid;		
	}
	
	

}
