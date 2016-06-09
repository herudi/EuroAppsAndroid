package herudi.com.aplikasieuro.app;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import herudi.com.aplikasieuro.models.Klasmens;
import herudi.com.aplikasieuro.models.Matches;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;

/**
 * Created by herudi-sahimar on 06/06/2016.
 */
public class MyApplication extends Application {
    JSONArray dataJson = null;
    private static final String TAG_OBJ = "fixtures";
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("herudi.realm")
                .schemaVersion(2)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
        RealmResults<Matches> result = realm.where(Matches.class).findAll();
        if (result.size() == 0) {
            new HelperMatches().execute();
        }
//        helperDelete();
    }



    private class HelperMatches extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject json = loadJSONFromAsset("matches.json");
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                dataJson = json.getJSONArray(TAG_OBJ);
                for (int i = 0; i < dataJson.length(); i++) {
                    JSONObject c = dataJson.getJSONObject(i);
                    realm.beginTransaction();
                    Matches m = realm.createObject(Matches.class);
                    m.setMatch(c.getString("matchday"));
                    m.setHomeText(c.getString("homeTeamName"));
                    m.setAwayText(c.getString("awayTeamName"));
                    String s = c.getString("date");
                    String[] parts = s.split("T");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
                    String dateInString = parts[0];
                    Date date = sdf.parse(dateInString);
                    SimpleDateFormat formatDate = new SimpleDateFormat("EEEE, d MMMM yyyy");
                    String tanggal = formatDate.format(date);
                    m.setDate(String.valueOf(tanggal));
                    m.setVersus("VS");
                    String strHomeFlag = "drawable/" + c.getString("homeTeamName").substring(0, 4).toLowerCase();
                    int homeFlag = getResources().getIdentifier(strHomeFlag, null, getPackageName());
                    String strAwayFlag = "drawable/" + c.getString("awayTeamName").substring(0, 4).toLowerCase();
                    int awayFlag = getResources().getIdentifier(strAwayFlag, null,getPackageName());
                    m.setHomeImage(homeFlag);
                    m.setAwayImage(awayFlag);
                    DateTimeZone timeZone = DateTimeZone.forID("Asia/Jakarta");
                    DateTime time = new DateTime(c.getString("date"), timeZone);
                    DateTimeFormatter formatterTime = DateTimeFormat.forPattern("HH:mm");
                    m.setTime(formatterTime.print(time) + " - RCTI");
                    m.setHomeScore("0");
                    m.setAwayScore("0");
                    m.setStatus("TIMED");
                    realm.commitTransaction();
                }
            } catch (JSONException e) {
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject loadJSONFromAsset(String data) {
        try {
            InputStream is = getAssets().open(data);
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

    private void helperDelete(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Klasmens> results = realm.where(Klasmens.class).findAll();
        RealmResults<Matches> results2 = realm.where(Matches.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        results2.deleteAllFromRealm();
        realm.commitTransaction();
        Log.d("Delete...","Sukses");
    }

    private void helperInsert(){

    }

    private class MyMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();
            if (oldVersion == 0) {
                schema.create("Matches")
                        .addField("homeText", String.class)
                        .addField("homeScore", String.class)
                        .addField("versus", String.class)
                        .addField("date", String.class)
                        .addField("time", String.class)
                        .addField("awayText", String.class)
                        .addField("awayScore", String.class)
                        .addField("match", String.class)
                        .addField("status", String.class)
                        .addField("homeImage", int.class)
                        .addField("awayImage", int.class);
                schema.create("Klasmens")
                        .addField("teamName", String.class)
                        .addField("teamPlayed", String.class)
                        .addField("tealGoal", String.class)
                        .addField("teamGoalAga", String.class)
                        .addField("teamGoalDif", String.class)
                        .addField("teamRank", String.class)
                        .addField("grup", String.class)
                        .addField("teamImage", int.class);
                oldVersion++;
            }
        }
    }

}
