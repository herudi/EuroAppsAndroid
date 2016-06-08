package herudi.com.aplikasieuro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import herudi.com.aplikasieuro.models.Matches;
import herudi.com.aplikasieuro.utils.JSONParser;
import herudi.com.aplikasieuro.utils.MatchAdapter;
import herudi.com.aplikasieuro.utils.RecyclerItemClickListener;
import herudi.com.aplikasieuro.utils.SimpleDividerItemDecoration;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by herudi-sahimar on 05/06/2016.
 */
public class ActivityDetail extends AppCompatActivity {

    private static String url = "http://api.football-data.org/v1/soccerseasons/424/fixtures";
    private static final String TAG_OBJ = "fixtures";
    JSONArray dataJson = null;
    private RealmList<Matches> matchList = new RealmList<>();
    private RecyclerView recyclerView;
    private MatchAdapter mAdapter;
    public static String team = "";
    private Realm realm;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    View coordinatorLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_team);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mAdapter = new MatchAdapter(matchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();
        getSupportActionBar().setTitle(team+" Matches");
        new loadAsync().execute();
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(refreshing);
            }
        });

    }

    private final Runnable refreshing = new Runnable() {
        public void run() {
            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new updateData().execute();
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void findByMatch(){
        matchList.clear();
        RealmResults<Matches> insert = realm.where(Matches.class).equalTo("homeText", team).or().equalTo("awayText",team).findAll();
        matchList.addAll(insert);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private class loadAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            return team;
        }

        @Override
        protected void onPostExecute(String _team) {
            try {
                matchList.clear();
                RealmResults<Matches> insert = realm.where(Matches.class).equalTo("homeText", _team).or().equalTo("awayText",_team).findAll();
                matchList.addAll(insert);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
            }
        }
    }

    public class updateData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl("http://api.football-data.org/v1/soccerseasons/424/fixtures");
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                RealmResults<Matches> update = realm.where(Matches.class).findAll();
                dataJson = json.getJSONArray(TAG_OBJ);
                if (update.size() == dataJson.length()) {
                    for (int i = 0; i < dataJson.length(); i++) {
                        JSONObject c = dataJson.getJSONObject(i);
                        JSONObject result = c.getJSONObject("result");
                        realm.beginTransaction();
                        Matches m = update.get(i);
                        String s = c.getString("date");
                        String[] parts = s.split("T");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
                        String dateInString = parts[0];
                        Date date = sdf.parse(dateInString);
                        SimpleDateFormat formatDate = new SimpleDateFormat("EEEE, d MMMM yyyy");
                        String tanggal = formatDate.format(date);
                        m.setDate(String.valueOf(tanggal));
                        DateTimeZone timeZone = DateTimeZone.forID("Asia/Jakarta");
                        DateTime time = new DateTime(c.getString("date"), timeZone);
                        DateTimeFormatter formatterTime = DateTimeFormat.forPattern("HH:mm");
                        m.setTime(formatterTime.print(time) + " - RCTI");
                        if (result.getString("goalsHomeTeam").equals("null")) {
                            m.setHomeScore("0");
                        } else {
                            m.setHomeScore(result.getString("goalsHomeTeam"));
                        }
                        if (result.getString("goalsAwayTeam").equals("null")) {
                            m.setAwayScore("0");
                        } else {
                            m.setAwayScore(result.getString("goalsAwayTeam"));
                        }
                        if (c.getString("status").equals("TIMED")) {
                            m.setStatus("TIMED");
                        }else if(c.getString("status").equals("FINISHED")){
                            m.setStatus("FULLTIME");
                        }else{
                            m.setStatus("IN_PLAY");
                        }
                        realm.commitTransaction();
                    }
                    findByMatch();
                }

            } catch (JSONException e) {
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
