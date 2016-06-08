package herudi.com.aplikasieuro.app;

import android.app.Application;
import android.util.Log;

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
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("herudi.realm")
                .schemaVersion(2)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
//        helperDelete();
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

    private class MyMigration implements RealmMigration {
//        private String
//                homeText,
//                homeScore,
//                versus,
//                date,
//                time,
//                awayText,
//                awayScore,
//                match,
//                status;
//        private int homeImage, awayImage;
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
//            private String teamName,teamPlayed,teamGoal,teamGoalAga,teamGoalDif,teamRank,grup;
//            private int teamImage;

        }
    }

}
