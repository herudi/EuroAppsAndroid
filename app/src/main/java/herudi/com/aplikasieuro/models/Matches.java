package herudi.com.aplikasieuro.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by herudi-sahimar on 04/06/2016.
 */
public class Matches extends RealmObject {
    private String
            homeText,
            homeScore,
            versus,
            date,
            time,
            awayText,
            awayScore,
            match,
            status;
    private int homeImage, awayImage;

    public Matches() {
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getHomeText() {
        return homeText;
    }

    public void setHomeText(String homeText) {
        this.homeText = homeText;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public String getVersus() {
        return versus;
    }

    public void setVersus(String versus) {
        this.versus = versus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAwayText() {
        return awayText;
    }

    public void setAwayText(String awayText) {
        this.awayText = awayText;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public int getHomeImage() {
        return homeImage;
    }

    public void setHomeImage(int homeImage) {
        this.homeImage = homeImage;
    }

    public int getAwayImage() {
        return awayImage;
    }

    public void setAwayImage(int awayImage) {
        this.awayImage = awayImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
