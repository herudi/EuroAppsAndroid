package herudi.com.aplikasieuro.frames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import herudi.com.aplikasieuro.ActivityDetail;
import herudi.com.aplikasieuro.R;
import herudi.com.aplikasieuro.helpers.HelperJson;
import herudi.com.aplikasieuro.models.Klasmens;
import herudi.com.aplikasieuro.utils.JSONParser;
import herudi.com.aplikasieuro.utils.KlasmenAdapter;
import herudi.com.aplikasieuro.utils.RecyclerItemClickListener;
import herudi.com.aplikasieuro.utils.SimpleDividerItemDecoration;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Created by herudi-sahimar on 04/06/2016.
 */
public class FrameGrup extends Fragment {
    private static String url = "http://api.football-data.org/v1/soccerseasons/424/leagueTable";
    JSONArray dataJson = null;
    private RealmList<Klasmens> listData = new RealmList<>();
    private RecyclerView recyclerView;
    private KlasmenAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    View coordinatorLayoutView;
    private Realm realm;
    private String teamOfGrup;


    public FrameGrup() {
    }

    @SuppressLint("ValidFragment")
    public FrameGrup(String grup) {
        teamOfGrup = grup;
    }

    public static FrameGrup newInstance(String a) {
        FrameGrup fragment = new FrameGrup(a);
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
        View view = inflater.inflate(R.layout.fragment_klasmens, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mAdapter = new KlasmenAdapter(listData);
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
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ActivityDetail.team = listData.get(position).getTeamName();
                        startActivity(new Intent(getActivity(), ActivityDetail.class));
                    }
                })
        );

        return view;
    }

    private void findByGrup() {
        listData.clear();
        RealmResults<Klasmens> insert = realm.where(Klasmens.class).equalTo("grup", teamOfGrup).findAll();
        listData.addAll(insert);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void firstLoad() {
        RealmResults<Klasmens> result = realm.where(Klasmens.class).equalTo("grup", teamOfGrup).findAll();
        if (result.size() == 0) {
            new Helper().execute();
        } else {
            if (isNetworkAvaliable(getContext())) {
                findByGrup();
            } else {
                findByGrup();
                Snackbar snackbar = Snackbar.make(coordinatorLayoutView, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

    private void realodGrup() {
        RealmResults<Klasmens> result = realm.where(Klasmens.class).equalTo("grup", teamOfGrup).findAll();
        if (result.size() == 0) {
            new Helper().execute();
        } else {
            if (isNetworkAvaliable(getContext())) {
                new updateData().execute();
            } else {
                findByGrup();
                Snackbar snackbar = Snackbar.make(coordinatorLayoutView, "No internet connection!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }

    }

    private void getData() {
        realodGrup();
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
            JSONObject json = HelperJson.loadJSONFromAsset(getActivity(), "grup.json");
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject standings = json.getJSONObject("standings");
                dataJson = standings.getJSONArray(teamOfGrup);
                for (int i = 0; i < dataJson.length(); i++) {
                    JSONObject grup = dataJson.getJSONObject(i);
                    realm.beginTransaction();
                    Klasmens k = realm.createObject(Klasmens.class);
                    k.setGrup(teamOfGrup);
                    k.setTeamName(grup.getString("team"));
                    k.setTeamPlayed("GP = " + grup.getString("playedGames"));
                    k.setTeamGoal("G = " + grup.getString("goals"));
                    k.setTeamGoalAga("GA = " + grup.getString("goalsAgainst"));
                    k.setTeamGoalDif("GD = " + grup.getString("goalDifference"));
                    k.setTeamRank(grup.getString("rank"));
                    String strImg = "drawable/" + grup.getString("team").substring(0, 4).toLowerCase();
                    int img = getContext().getResources().getIdentifier(strImg, null, getContext().getPackageName());
                    k.setTeamImage(img);
                    realm.commitTransaction();

                }
                findByGrup();
            } catch (JSONException e) {

            }
        }

    }

    private class updateData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONObject standings = json.getJSONObject("standings");
                RealmResults<Klasmens> update = realm.where(Klasmens.class).equalTo("grup", teamOfGrup).findAll();
                dataJson = standings.getJSONArray(teamOfGrup);
                if (update.size() == dataJson.length()) {
                    for (int i = 0; i < dataJson.length(); i++) {
                        JSONObject grup = dataJson.getJSONObject(i);
                        realm.beginTransaction();
                        Klasmens k = update.get(i);
                        k.setGrup(teamOfGrup);
                        k.setTeamPlayed("GP = " + grup.getString("playedGames"));
                        k.setTeamGoal("G = " + grup.getString("goals"));
                        k.setTeamGoalAga("GA = " + grup.getString("goalsAgainst"));
                        k.setTeamGoalDif("GD = " + grup.getString("goalDifference"));
                        k.setTeamRank(grup.getString("rank"));
                        realm.commitTransaction();

                    }
                    findByGrup();
                }

            } catch (JSONException e) {

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
