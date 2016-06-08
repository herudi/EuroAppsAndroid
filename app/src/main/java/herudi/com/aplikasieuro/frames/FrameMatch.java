package herudi.com.aplikasieuro.frames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import herudi.com.aplikasieuro.R;
import herudi.com.aplikasieuro.helpers.HelperJson;
import herudi.com.aplikasieuro.models.Matches;
import herudi.com.aplikasieuro.utils.JSONParser;
import herudi.com.aplikasieuro.utils.MatchAdapter;
import herudi.com.aplikasieuro.utils.SimpleDividerItemDecoration;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Created by herudi-sahimar on 04/06/2016.
 */
public class FrameMatch extends Fragment {
    private static String url = "http://api.football-data.org/v1/soccerseasons/424/fixtures";
    private static final String TAG_OBJ = "fixtures";
    JSONArray dataJson = null;
    private RealmList<Matches> matchList = new RealmList<>();
    private RecyclerView recyclerView;
    private MatchAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    View coordinatorLayoutView;
    private Realm realm;
    private String match;

    public FrameMatch() {
    }

    @SuppressLint("ValidFragment")
    public FrameMatch(String asd) {
        match = asd;
    }

    public static FrameMatch newInstance(String _match) {
        FrameMatch fragment = new FrameMatch(_match);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mAdapter = new MatchAdapter(matchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        coordinatorLayoutView = view.findViewById(R.id.snackbarPosition);
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.black);
        realm = Realm.getDefaultInstance();
        firstLoad();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(refreshing);
            }
        });
        return view;
    }

    private void getData() {
        realodMatch();
    }

    private final Runnable refreshing = new Runnable() {
        public void run() {
            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class Helper extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject json = HelperJson.loadJSONFromAsset(getActivity(), "match" + match + ".json");
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                dataJson = json.getJSONArray(TAG_OBJ);
                for (int i = 0; i < dataJson.length(); i++) {
                    JSONObject c = dataJson.getJSONObject(i);
                    JSONObject result = c.getJSONObject("result");
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
                    int homeFlag = getContext().getResources().getIdentifier(strHomeFlag, null, getContext().getPackageName());
                    String strAwayFlag = "drawable/" + c.getString("awayTeamName").substring(0, 4).toLowerCase();
                    int awayFlag = getContext().getResources().getIdentifier(strAwayFlag, null, getContext().getPackageName());
                    m.setHomeImage(homeFlag);
                    m.setAwayImage(awayFlag);
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
            } catch (JSONException e) {
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void findByMatch() {
        matchList.clear();
        RealmResults<Matches> insert = realm.where(Matches.class).equalTo("match", match).findAll();
        matchList.addAll(insert);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void firstLoad(){
        RealmResults<Matches> result = realm.where(Matches.class).equalTo("match", match).findAll();
        if (result.size() == 0) {
            new Helper().execute();
        } else {
            if (isNetworkAvaliable(getContext())) {
                findByMatch();
            } else {
                findByMatch();
                Snackbar snackbar = Snackbar.make(coordinatorLayoutView, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

    private void realodMatch() {
        RealmResults<Matches> result = realm.where(Matches.class).equalTo("match", match).findAll();
        if (result.size() == 0) {
            new Helper().execute();
        } else {
            if (isNetworkAvaliable(getContext())) {
                new updateData().execute();
            } else {
                findByMatch();
                Snackbar snackbar = Snackbar.make(coordinatorLayoutView, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
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
            JSONObject json = jParser.getJSONFromUrl("http://api.football-data.org/v1/soccerseasons/424/fixtures?matchday=" + match);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                RealmResults<Matches> update = realm.where(Matches.class).equalTo("match", match).findAll();
                dataJson = json.getJSONArray(TAG_OBJ);
//                Log.d("update : ",String.valueOf(update.size()));
//                Log.d("json : ",String.valueOf(dataJson.length()));
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


    public boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

}
