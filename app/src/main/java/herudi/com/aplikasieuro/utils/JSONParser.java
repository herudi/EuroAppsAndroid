package herudi.com.aplikasieuro.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {
        BufferedReader reader = null;
        try {
            URL urlp = new URL(url);
            try {
                HttpURLConnection c = (HttpURLConnection) urlp.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("X-Auth-Token", "90ad437f3da640d0b1d47bc382ce12d3");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line);
                }
                json = buf.toString();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

        } catch (IOException e) {
            Log.e("Error", "Error " + e.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;

    }
}
