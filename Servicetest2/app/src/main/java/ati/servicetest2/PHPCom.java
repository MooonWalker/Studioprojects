package ati.servicetest2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by I021059 on 2015.07.04..
 */
public class PHPCom
{
    // Progress Dialog
    private  ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputPrice;
    EditText inputDesc;
    Context ctx;
    String sendwascorrect= Boolean.TRUE.toString();

    //private static String url_create_session = "http://localhost/insertsession.php";
    private static String url_create_session = "http://gongfucha.info/insertsession.php";
    private static String url_create_brewing = "http://gongfucha.info/insertsessiond.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    public PHPCom(Context _ctx)
    {
        super();
        ctx=_ctx;
    }

    public String execute(String uuid)
    {
        //HSessionBrew stufftosend = new HSessionBrew(sessions, brewings, uuid);
       // new UploadStatistics().execute(stufftosend);
        return sendwascorrect;
    }

    //class UploadStatistics extends AsyncTask<List<SessionH>, String, String>
    class UploadStatistics extends AsyncTask<List, String, String>
    {
        Boolean running;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(ctx);
            pDialog.setMessage("Uploading statistics...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            // pDialog.show();
            running=true;
        }

        @Override
        protected void onCancelled()
        {
            running=false;
            //pDialog.dismiss();
            super.onCancelled();
        }

        // protected String doInBackground(List<SessionH>... sessions)
        protected String doInBackground(List... stufftosend)
        {
            String uuid = null;
            Map<String, String> params = new HashMap<String, String>();

                JSONObject json = jsonParser.makeHttpRequest(
                        url_create_session, "POST", (List)params);
                if (json == null)
                {
                    cancel(true);
                }
                Log.d("Create Response", json.toString());

                try
                {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1)
                    {
                        //s_success.add(sessions.get(i));
                    }
                    else
                    {
                        sendwascorrect = "false";

                    }
                }
                catch (JSONException e)
                {
                    sendwascorrect = "false";
                    e.printStackTrace();

                }


            if(isCancelled())return sendwascorrect="false";


           // if (s_success.size() > 0)
            //{

            //}

            return sendwascorrect;
        }

        protected void onPostExecute(String file_url)
        {
            // pDialog.dismiss();
        }
    }
}
