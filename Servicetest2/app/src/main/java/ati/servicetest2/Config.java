package ati.servicetest2;

public class Config
{
        // CONSTANTS
        static final String YOUR_SERVER_URL =
                "http://somejourney.info/gcm_server_files/register.php";
        static final String DEREGISTER_URL =
                "http://somejourney.info/gcm_server_files/deregister.php";
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
}
