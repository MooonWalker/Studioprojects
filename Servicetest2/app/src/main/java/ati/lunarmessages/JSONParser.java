package ati.lunarmessages;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class JSONParser
{

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser()
    {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public static JSONObject parse(String stringToParse) throws JSONException
    {
        jObj = new JSONObject(stringToParse);
        return jObj;
    }


}
