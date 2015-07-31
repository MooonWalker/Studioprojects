package ati.lunarmessages;

public class Config
{
        // CONSTANTS
        static final String YOUR_SERVER_URL =
                "http://gongfucha.info/GCM_files/register.php";
        static final String DEREGISTER_URL =
                "http://gongfucha.info/GCM_files/deregister.php";
        // Google project id
        static final String GOOGLE_SENDER_ID = "865494874332";
        /**
         * Tag used on log messages.
         */
        static final String TAG = "GCM Ati";

        static final String DISPLAY_MESSAGE_ACTION =
                "ati.servicesteszts.DISPLAY_MESSAGE";
        //GCM message tag
        static final String EXTRA_MESSAGE = "message";
        static final String RSS_URL =
                "http://www.zetor.webcucc.hu/index.php/esemenyek?format=feed&type=rss";
        static final String DATABASE_PATH = "/data/data/ati.lunarmessages/databases/";
        static final String DB_BACKUP_PATH = "/lunarmessages/backup";
}
