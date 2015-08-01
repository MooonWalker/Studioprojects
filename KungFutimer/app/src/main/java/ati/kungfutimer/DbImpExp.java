package ati.kungfutimer;


	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.nio.channels.FileChannel;
	import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
	 
	import android.content.Context;
	import android.database.Cursor;
	import android.database.sqlite.SQLiteDatabase;
	import android.database.sqlite.SQLiteException;
	import android.os.Environment;
	import android.util.Log;
	 
	public class DbImpExp
	{
	 
		public static final String TAG = DbImpExp.class.getName();
	 
		/** Directory that files are to be read from and written to **/
		protected static final File DATABASE_DIRECTORY =new File(Environment.getExternalStorageDirectory(),"MyDirectory");
	 
		/** File path of Db to be imported **/
		protected static final File IMPORT_FILE =new File(DATABASE_DIRECTORY,"Kungfutimer.db");
	 
		public static final String PACKAGE_NAME = "ati.kungfutimer";
		public static final String DATABASE_NAME = "Kungfutimer.db";
		public static final String DATABASE_TABLE = "tblTeas";
	 
		/** Contains: /data/data/ati.kungfutimer/databases/Kungfutimer.db **/
		private static final File DATA_DIRECTORY_DATABASE =	new File(Environment.getDataDirectory() +
				"/data/" + PACKAGE_NAME +"/databases/" + DATABASE_NAME );
	 
		/** Saves the application database to the
		 * export directory under MyDb.db **/
		
		private DatabaseHandler db;
		public static HashMap<String, String[]> TABLE_COLUMS;			
		
		public DbImpExp()
		{super();}
		
		

		public DbImpExp(Context ctx)
		{
			super();					
		}
		
		private static HashMap<String, String[]> createMap()
		{
			
				Map<String, String[]> map=new HashMap<String, String[]>();
				map.put(DatabaseHandler.TABLE_TEAS, DatabaseHandler.TABLE_TEA_COLUMN_KEYS);
				map.put(DatabaseHandler.TABLE_BREWINGS, DatabaseHandler.TABLE_BREWINGS_COLUMN_KEYS);
				map.put(DatabaseHandler.TABLE_SESSIONS_H, DatabaseHandler.TABLE_SESSION_H_COLUMN_KEYS);
				map.put(DatabaseHandler.TABLE_BREWINGS_H, DatabaseHandler.TABLE_BREWINGS_H_COLUMN_KEYS);
			
			return (HashMap<String, String[]>) map;
		}
		
		protected static  boolean exportDb()
		{
			if( ! SdIsPresent() ) return false;
	 
			File dbFile = DATA_DIRECTORY_DATABASE;
			String filename = "Kungfutimer.db";
	 
			File exportDir = DATABASE_DIRECTORY;
			File file = new File(exportDir, filename);
	 
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
	 
			try {
				file.createNewFile();
				copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	 
		/** Replaces current database with the IMPORT_FILE if
		 * import database is valid and of the correct type **/
		public static boolean restoreDb(){
			if( ! SdIsPresent() ) return false;
	 
			File exportFile = DATA_DIRECTORY_DATABASE;
			File importFile = new File(Environment.getExternalStorageDirectory(),DatabaseHandler.DB_BACKUP_PATH+"/"
					+DATABASE_NAME);
	
			if (!importFile.exists()) 
			{
				Log.d(TAG, "File does not exist");
				return false;
			}
			
			if( ! checkDbIsValid(importFile) ) return false;
	 
			try 
			{
				exportFile.createNewFile();
				copyFile(importFile, exportFile);
				return true;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	 
		
	 
		/** Given an SQLite database file, this checks if the file
		 * is a valid SQLite database and that it contains all the
		 * columns represented by DbAdapter.ALL_COLUMN_KEYS **/
		protected static boolean checkDbIsValid( File db )
		{
			try{
				TABLE_COLUMS= createMap();
				SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase
					(db.getPath(), null, SQLiteDatabase.OPEN_READONLY);
				
								
				Cursor cursor;
				Iterator<Entry<String, String[]>> it = TABLE_COLUMS.entrySet().iterator();
				
				while (it.hasNext())
				{
					Map.Entry<String, String[]> pairs = (Entry<String, String[]>) it.next();
					
					cursor = sqlDb.query(true, pairs.getKey().toString(), null, null,
							null, null, null, null, null);
					
					for( String s : (String[])pairs.getValue() )
					{
						cursor.getColumnIndexOrThrow(s);
					}
					cursor.close();
					it.remove();
				}
				
				
				sqlDb.close();
			} 
			catch( IllegalArgumentException e ) 
			{
				Log.d(TAG, "Database valid but not the right type");
				e.printStackTrace();
				return false;
			} 
			catch( SQLiteException e ) 
			{
				Log.d(TAG, "Database file is invalid.");
				e.printStackTrace();
				return false;
			} 
			catch( Exception e)
			{
				Log.d(TAG, "checkDbIsValid encountered an exception");
				e.printStackTrace();
				return false;
			}
	 
			return true;
		}
	 
		private static void copyFile(File src, File dst) throws IOException {
			FileChannel inChannel = new FileInputStream(src).getChannel();
			FileChannel outChannel = new FileOutputStream(dst).getChannel();
			try {
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				if (inChannel != null)
					inChannel.close();
				if (outChannel != null)
					outChannel.close();
			}
		}
	 
		/** Returns whether an SD card is present and writable **/
		public static boolean SdIsPresent() {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		}
	
}
