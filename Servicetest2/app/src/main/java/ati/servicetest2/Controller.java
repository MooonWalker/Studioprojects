package ati.servicetest2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller
{

    public static final int MAX_ATTEMPTS = 5;
    public static final int BACKOFF_MILLI_SECONDS = 2000;
    public static final Random random = new Random();

    // Register this account with the server.
   public static int register(String name, String email, final String regId)
    {
        Log.i(Config.TAG, "registering device (regId = " + regId + ")");
        String serverUrl = Config.YOUR_SERVER_URL;
        int jsonres=0;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("name", name);
        params.put("email", email);

        try
        {
                // Post registration values to web server
                jsonres=post(serverUrl, params).getInt("successfromreg");
                return jsonres;
        }
        catch (JSONException je)
        {
               Log.e(Config.TAG, "Jsonerror :" + je);
            return 0;
        }
        catch (IOException e)
        {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on recoverable errors
                // (like HTTP error code 503).
                Log.e(Config.TAG, "Failed to register: " + e);
            return 0;
        }
    }

    // Unregister this account/device pair within the server.
    void unregister(final Context context, final String regId)
    {
        Log.i(Config.TAG, "unregistering device (regId = " + regId + ")");

        String serverUrl = Config.YOUR_SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);

        try
        {
            post(serverUrl, params);
            //GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            displayMessageOnScreen(context, message);
        }
        catch (IOException e)
        {
            // At this point the device is unregistered from GCM, but still
            // registered in the our server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.

            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            displayMessageOnScreen(context, message);
        }
    }

    // Issue a POST request to the server.
    private static JSONObject post(String serverUrl, Map<String, String> params)
            throws IOException
    {
        JSONObject jObj=new JSONObject();
        URL url;
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        try
        {
            url = new URL(serverUrl);
        }
        catch (MalformedURLException e)
        {
            throw new IllegalArgumentException("invalid url: " + serverUrl);
        }

        // constructs the POST body using the parameters
        while (iterator.hasNext())
        {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext())
            {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();

        Log.v(Config.TAG, "GCMPosting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();

        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));
        HttpURLConnection conn = null;
        try
        {
            Log.e("URL", "> " + url);
            //conn = (HttpURLConnection) url.openConnection(proxy);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();

            InputStream iS= conn.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(iS));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }
            // handle the response
            reader.close();

            try
            {
                //get response from server as JSON object
                jObj= JSONParser.parse(response.toString());
                //JSONObject jObj = new JSONObject(response.toString());
                return jObj;
            }
            catch (JSONException e)
            {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            int status = conn.getResponseCode();

            // If response is not success
            if (status != 200)
            {
                throw new IOException("Post failed with error code " + status);
            }
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
        return jObj;
    }

    // Checking for all possible internet providers
    public static boolean isConnectingToInternet(Context context)
    {
        ConnectivityManager connectivity =(ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }

    // Notifies UI to display a message.
    static void displayMessageOnScreen(Context context, String message)
    {
        Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Config.EXTRA_MESSAGE, message);

        // Send Broadcast to Broadcast receiver with message
        context.sendBroadcast(intent);

    }
    //Function to display simple Alert Dialog
    public static void showAlertDialog(final Context context, String title, String message,
                                       Boolean status)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Set Dialog Title
        alertDialog.setTitle(title);
        // Set Dialog Message
        alertDialog.setMessage(message);
        if(status != null)
            // Set alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Set OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity.activity.finish();
            }
        });

        // Show Alert Message
        alertDialog.show();
    }

    private PowerManager.WakeLock wakeLock;

    public  void acquireWakeLock(Context context)
    {
        if (wakeLock != null) wakeLock.release();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");

        wakeLock.acquire();
    }

    public  void releaseWakeLock()
    {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}
