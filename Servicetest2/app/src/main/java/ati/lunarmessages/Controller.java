package ati.lunarmessages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller
{

    public static final int MAX_ATTEMPTS = 5;
    public static final int BACKOFF_MILLI_SECONDS = 2000;
    public static final Random random = new Random();
    public static final int TO_YEAR=1;
    public static final int TO_MONTH=2;
    public static final int TO_DAY=3;
    public static final int TO_HOUR=4;
    public static final int TO_MINUTE=5;


    //Unregister the given regid
    public static int deregister(String name, String email, String strRegid)
    {
        Log.i(Config.TAG, "Deregistering device (regId = " + strRegid + ")");
        String serverUrl = Config.DEREGISTER_URL;
        int jsonres;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", strRegid);
        params.put("name", name);
        params.put("email", email);

        try
        {
            // Post registration values to web server
            jsonres=post(serverUrl, params).getInt("successfromdel");
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
            Log.e(Config.TAG, "Failed to deregister: " + e);
            e.printStackTrace();
            return 0;
        }
    }


    public static File getCacheFolder(Context context)
    {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if(!cacheDir.isDirectory())
            {
                cacheDir.mkdirs();
            }
        }
        if(!cacheDir.isDirectory())
        {
            cacheDir = context.getCacheDir(); //get system cache folder
        }
        return cacheDir;
    }

    public static void loadImageFromCache(Context ctx)
    {
        File cacheDir = ctx.getCacheDir();
        //TODO filename
        File cacheFile = new File(cacheDir, "localFileName.jpg");
        InputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        //bitmapOptions.inSampleSize = scale;
        bitmapOptions.inJustDecodeBounds = false;
        Bitmap wallpaperBitmap = BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
        //ImageView imageView = (ImageView)this.findViewById(R.id.preview);
        //imageView.setImageBitmap(wallpaperBitmap);

    }
    public static void downloadImageToCache(String imageurlStr, Context ctx)
    {
        URL imageUrl = null;
        try
        {
            imageUrl = new URL(imageurlStr);
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        try
        {
            URLConnection connection = imageUrl.openConnection();
            InputStream inputStream = new BufferedInputStream(imageUrl.openStream(), 10240);
            File cacheDir = getCacheFolder(ctx);
            File cacheFile = new File(cacheDir, imageurlStr.substring(imageurlStr.lastIndexOf("/")+1));
            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            byte buffer[] = new byte[1024];
            int dataSize;
            int loadedSize = 0;
            while ((dataSize = inputStream.read(buffer)) != -1)
            {
                loadedSize += dataSize;
                // TODO publishProgress(loadedSize);
                outputStream.write(buffer, 0, dataSize);
            }
            outputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

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
            Log.i(Config.TAG, "> " + url);
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
