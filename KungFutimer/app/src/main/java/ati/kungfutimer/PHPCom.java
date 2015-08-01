package ati.kungfutimer;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

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

public String execute(List<SessionH> sessions, List<BrewingH> brewings, String uuid)
    {    
    	HSessionBrew stufftosend = new HSessionBrew(sessions, brewings, uuid);
    	new UploadStatistics().execute(stufftosend);
    	return sendwascorrect;
    }
    
    //class UploadStatistics extends AsyncTask<List<SessionH>, String, String>
class UploadStatistics extends AsyncTask<HSessionBrew, String, String> 
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
    	 protected String doInBackground(HSessionBrew... stufftosend) 
    	 {
    		
				String sessionid = null;
				String teaname = null;
				String sessiondate = null;
				String brewnum = null;
				String brewtime = null;
				String uuid = null;
				List<SessionH> sessions = new ArrayList<SessionH>();
				List<BrewingH> brewings = new ArrayList<BrewingH>();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				List<NameValuePair> paramsb = new ArrayList<NameValuePair>();
				uuid = stufftosend[0].getUuid();
				sessions = stufftosend[0].getsH();
				brewings = stufftosend[0].getbH();
				List<SessionH> s_success = new ArrayList<SessionH>(sessions);
				s_success.clear();
				for (int i = 0; i < sessions.size(); i++)
				{

					sessionid = sessions.get(i).getSessionid();
					teaname = sessions.get(i).getTeaname();
					sessiondate = sessions.get(i).getSessiondate();

					params.add(new BasicNameValuePair("uuid", uuid));
					params.add(new BasicNameValuePair("sessionid", sessionid));
					params.add(new BasicNameValuePair("teaname", teaname));
					params.add(new BasicNameValuePair("sessiondate",
							sessiondate));

					JSONObject json = jsonParser.makeHttpRequest(
							url_create_session, "POST", params);
					if (json == null)
					{
						cancel(true);
						break;
					}
					Log.d("Create Response", json.toString());

					try
					{
						int success = json.getInt(TAG_SUCCESS);
						if (success == 1)
						{
							s_success.add(sessions.get(i));
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

				} //for
				
				if(isCancelled())return sendwascorrect="false";
				
				for (int i = 0; i < brewings.size(); i++)
				{
					sessionid = brewings.get(i).getSessionid();
					brewnum = brewings.get(i).getBrewnum();
					brewtime = brewings.get(i).getBrewtime();

					paramsb.add(new BasicNameValuePair("uuid", uuid));
					paramsb.add(new BasicNameValuePair("sessionid", sessionid));
					paramsb.add(new BasicNameValuePair("brewnum", brewnum));
					paramsb.add(new BasicNameValuePair("brewtime", brewtime));

					JSONObject json = jsonParser.makeHttpRequest(
							url_create_brewing, "POST", paramsb);
					Log.d("Create Response", json.toString());

					try
					{
						int success = json.getInt(TAG_SUCCESS);
						if (success == 1)
						{
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

				}
				if (s_success.size() > 0)
				{
					DatabaseHandler db = new DatabaseHandler(ctx);
					db.flagSessionsSended(s_success);
				}
				
			
			return sendwascorrect;
         }
    	 
    	 protected void onPostExecute(String file_url) 
    	 {
             
            // pDialog.dismiss();
         }

		

    }
}
