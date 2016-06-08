package herudi.com.aplikasieuro.helpers;


import android.support.v4.app.FragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by herudi-sahimar on 06/06/2016.
 */
public class HelperJson {

    public HelperJson() {
    }

    public static JSONObject loadJSONFromAsset(FragmentActivity fa, String data) {
        try {
            InputStream is = fa.getAssets().open(data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String a = new String(buffer, "UTF-8");
            JSONObject json = new JSONObject(a);
            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
