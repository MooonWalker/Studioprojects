package ati.lunarmessages;

import java.io.File;

public class Config
{
     //app name for directories
        static final String APP_NAME="zetorvas";
        static final String SERVICE_TELEPHONE="+36302786363";
        static final String YOUR_SERVER_URL =
                "http://gongfucha.info/GCM_files/register.php";
        static final String DEREGISTER_URL =
                "http://gongfucha.info/GCM_files/deregister.php";
     // Google project id
        static final String GOOGLE_SENDER_ID = "865494874332";
     // Tag used on log messages.
        static final String TAG = "GCM Ati";
        static final String DISPLAY_MESSAGE_ACTION =
                "ati.servicesteszts.DISPLAY_MESSAGE";
     //GCM message tag
        static final String EXTRA_MESSAGE = "message";
        static final String RSS_URL =
                "http://www.zetor.webcucc.hu/index.php/esemenyek?format=feed&type=rss";
        static final String DATABASE_PATH = "/data/data/ati.lunarmessages/databases/";
     //external backup path
        static final String DB_BACKUP_PATH = APP_NAME+"/backup";
     //Cachedir
        static File cacheDir=null;
        static File logDir =null;
        static File logFile=null;
        static final String CACHE_DIR= APP_NAME+"/cache";
        static final String LOG_DIR =APP_NAME+"/log";
        static final String LOG_FILE="/messagelog.log";

        static final String CONTACT_EMAIL = "iroda@zetorvas.hu";
     //setupdiscountmodule
        static final boolean isDISCOUNTACTIVE = false; // values: true or false
     //discount days
        static final int DAYS_TO_DISCOUNT1 = 2;

}
