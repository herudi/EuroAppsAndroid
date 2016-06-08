package herudi.com.aplikasieuro.models;

import io.realm.RealmObject;

/**
 * Created by herudi-sahimar on 05/06/2016.
 */

public class Klasmens extends RealmObject{
    private String teamName,teamPlayed,teamGoal,teamGoalAga,teamGoalDif,teamRank,grup;
    private int teamImage;

    public Klasmens() {
    }

    public String getTeamPlayed() {
        return teamPlayed;
    }

    public void setTeamPlayed(String teamPlayed) {
        this.teamPlayed = teamPlayed;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamGoal() {
        return teamGoal;
    }

    public void setTeamGoal(String teamGoal) {
        this.teamGoal = teamGoal;
    }

    public String getTeamGoalAga() {
        return teamGoalAga;
    }

    public void setTeamGoalAga(String teamGoalAga) {
        this.teamGoalAga = teamGoalAga;
    }

    public String getTeamGoalDif() {
        return teamGoalDif;
    }

    public void setTeamGoalDif(String teamGoalDif) {
        this.teamGoalDif = teamGoalDif;
    }

    public String getTeamRank() {
        return teamRank;
    }

    public void setTeamRank(String teamRank) {
        this.teamRank = teamRank;
    }

    public int getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(int teamImage) {
        this.teamImage = teamImage;
    }

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }
}
