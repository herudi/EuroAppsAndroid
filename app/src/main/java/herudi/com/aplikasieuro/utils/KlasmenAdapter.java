package herudi.com.aplikasieuro.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import herudi.com.aplikasieuro.R;
import herudi.com.aplikasieuro.models.Klasmens;
import herudi.com.aplikasieuro.models.Matches;

/**
 * Created by herudi-sahimar on 03/06/2016.
 */
public class KlasmenAdapter extends RecyclerView.Adapter<KlasmenAdapter.MyViewHolder> {
    private List<Klasmens> klasmensList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView teamName,teamPlayed,teamGoal,teamGoalAga,teamGoalDif,teamRank;
        public ImageView teamImage;

        public MyViewHolder(View view) {
            super(view);
            teamName = (TextView) view.findViewById(R.id.teamName);
            teamPlayed = (TextView) view.findViewById(R.id.teamPlayed);
            teamGoal = (TextView) view.findViewById(R.id.teamGoal);
            teamImage = (ImageView) view.findViewById(R.id.teamImage);
            teamGoalAga = (TextView) view.findViewById(R.id.teamGoalsAgainst);
            teamGoalDif = (TextView) view.findViewById(R.id.teamGoalDifference);
            teamRank = (TextView) view.findViewById(R.id.teamRank);

        }
    }

    public KlasmenAdapter(List<Klasmens> klasmensList) {
        this.klasmensList = klasmensList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.klasmen_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Klasmens m = klasmensList.get(position);
        holder.teamName.setText(m.getTeamName());
        holder.teamPlayed.setText(m.getTeamPlayed());
        holder.teamImage.setImageResource(m.getTeamImage());
        holder.teamGoal.setText(m.getTeamGoal());
        holder.teamGoalAga.setText(m.getTeamGoalAga());
        holder.teamGoalDif.setText(m.getTeamGoalDif());
        holder.teamRank.setText(m.getTeamRank());

    }

    @Override
    public int getItemCount() {
        return klasmensList.size();
    }
}
