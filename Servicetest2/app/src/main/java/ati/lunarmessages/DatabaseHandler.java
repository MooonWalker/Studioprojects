package ati.lunarmessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RSS.db";

    // Contacts table name
    public static final String TABLE_RSSITEMS = "tblRItems";

    // Tea Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCR = "descr";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CAT = "category";
    private static final String KEY_PUBDATE = "pubdate";
    private static final String KEY_STAMP = "timestamp";

    public static final String[] TABLE_RSS_COLUMN_KEYS = {"id", "title", "link", "descr", "image",
                                    "author","category","pubdate","timestamp"};
    public static final String[] TABLE_NAMES = {TABLE_RSSITEMS};

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
                copyDataBase(dbContext, Config.DATABASE_PATH + DATABASE_NAME,0); //local copy
                this.close();
                openDataBase();
            } catch (IOException e)
            {
                throw new Error("Error copying database");
            }
            Toast.makeText(context, "Initial database is created", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_RSS_TABLE = "CREATE TABLE " + TABLE_RSSITEMS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE + " TEXT, "
                + KEY_LINK + " TEXT, " + KEY_DESCR + " TEXT, " + KEY_IMAGE
                + " TEXT, " + KEY_AUTHOR + " TEXT, " + KEY_CAT + " TEXT, "
                + KEY_PUBDATE + " TEXT, " + KEY_STAMP + " DATE)";

        db.execSQL(CREATE_RSS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

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

    //target 0=local copy, 1=copy to SD-card
    public File copyDataBase(Context context, String toFullPath, int target) throws IOException
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
                    File fileBackupDir = new File(Environment.getExternalStorageDirectory(),Config.DB_BACKUP_PATH);
                    InputStream mInput = new FileInputStream(Config.DATABASE_PATH+DATABASE_NAME);
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
        String dbPath = Config.DATABASE_PATH + DATABASE_NAME;
        dataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        this.close();
    }

    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;
        boolean exist = false;
        try
        {
            String dbPath = Config.DATABASE_PATH + DATABASE_NAME;
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

    public long addRSSItem(RssItem rssItem) throws SQLiteException
    {
        long rowid = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, rssItem.getTitle());
        values.put(KEY_PUBDATE, rssItem.getPubdate());
        values.put(KEY_AUTHOR, rssItem.getAuthor());
        values.put(KEY_CAT, rssItem.getCategory());

        // Inserting Row

        try
        {
            rowid=db.insertOrThrow(TABLE_RSSITEMS, null, values);
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

//    // Getting single RSS item
//    public RssItem getRSSItem(int id)
//    {
//        SQLiteDatabase db = this.getReadableDatabase();
//        RssItem rssItem;
//        Cursor cursor = db.query(TABLE_RSSITEMS, new String[]{KEY_ID,
//                        KEY_NAME, KEY_NOTE1, KEY_NOTE2, KEY_NOTE3}, KEY_ID + "=?",
//                new String[]{String.valueOf(id)}, null, null, null, null);
//        if (cursor != null)
//        {
//            cursor.moveToFirst();
//
//            rssItem = new RssItem(Integer.parseInt(cursor.getString(0)),
//                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
//            // return tea
//            cursor.close();
//            db.close();
//            return rssItem;
//        }
//        else return null;
//
//    }


//
//    public int getTeaIDByName(String name)
//    {
//        int id=0;
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_TEAS, new String[]{KEY_ID}, KEY_NAME + "=?",
//                new String[]{name}, null, null, null, null);
//        if (cursor != null)
//        {
//            cursor.moveToFirst();
//            Tea tea = new Tea(cursor.getInt(0));
//            id = tea.getID();
//            cursor.close();
//        }
//
//        return id;
//    }


    public Cursor fetchAllRssItems()
    {
        String selectQuery = "SELECT id as _id, name FROM " + TABLE_RSSITEMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    public List<RssItem> getAllRssItems()
    {
        List<RssItem> rssList = new ArrayList<RssItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RSSITEMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RssItem rssItem = new RssItem();
                rssItem.setAuthor(cursor.getString(1));
                rssItem.setCategory(cursor.getString(2));
                rssItem.setDescription(cursor.getString(3));
                rssItem.setImageUrl(cursor.getString(4));
                rssItem.setLink(cursor.getString(5));
                rssItem.setTitle(cursor.getString(6));
                // Adding contact to list
                rssList.add(rssItem);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        this.close();
        return rssList;
    }


    // Getting contacts Count
    public int getRssCount()
    {
        int cnt=0;
        String countQuery = "SELECT * FROM " + TABLE_RSSITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        cnt=cursor.getCount();
        cursor.close();
        db.close();
        this.close();
        return cnt;
    }

//    // Updating single tea
//    public int updateTea(RssItem rssItem)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int rows;
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, tea.getName());
//        values.put(KEY_NOTE1, tea.get_note1());
//        values.put(KEY_NOTE2, tea.get_note2());
//        values.put(KEY_NOTE3, tea.get_note3());
//
//        rows= db.update(TABLE_TEAS, values, KEY_ID + " = ?", new String[] { String.valueOf(tea.getID()) });
//        db.close();
//        this.close();
//        return rows;
//    }

    // Deleting single tea
    public void deleteTea(RssItem rssItem)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RSSITEMS, KEY_LINK + " = ?",
                new String[] { String.valueOf(rssItem.getLink()) });
        db.close();
    }

    public void deleteTea(int rssItemId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RSSITEMS, KEY_ID + " = ?", new String[]{""+rssItemId});
        db.close();
        this.close();
    }


    @Override
    public synchronized void close()
    {
        super.close();
        if(dataBase!=null)
        {
            dataBase.close();
        }
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

