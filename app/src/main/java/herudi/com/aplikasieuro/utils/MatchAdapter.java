package herudi.com.aplikasieuro.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import herudi.com.aplikasieuro.R;
import herudi.com.aplikasieuro.models.Matches;

/**
 * Created by herudi-sahimar on 03/06/2016.
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyViewHolder> {
    private List<Matches> matchList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView
                homeText,
                homeScore,
                versus,
                status,
                date,
                time,
                awayText,
                awayScore;
        public ImageView homeImage, awayImage;

        public MyViewHolder(View view) {
            super(view);
            homeText = (TextView) view.findViewById(R.id.homeText);
            status = (TextView) view.findViewById(R.id.status);
            homeImage = (ImageView) view.findViewById(R.id.homeImage);
            homeScore = (TextView) view.findViewById(R.id.homeScore);
            versus = (TextView) view.findViewById(R.id.versus);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            awayText = (TextView) view.findViewById(R.id.awayText);
            awayImage = (ImageView) view.findViewById(R.id.awayImage);
            awayScore = (TextView) view.findViewById(R.id.awayScore);
        }
    }

    public MatchAdapter(List<Matches> matchList) {
        this.matchList = matchList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Matches m = matchList.get(position);
        holder.homeText.setText(m.getHomeText());
        holder.homeImage.setImageResource(m.getHomeImage());
        holder.homeScore.setText(m.getHomeScore());
        holder.versus.setText(m.getVersus());
        holder.date.setText(m.getDate());
        holder.time.setText(m.getTime());
        holder.awayText.setText(m.getAwayText());
        holder.awayImage.setImageResource(m.getAwayImage());
        holder.awayScore.setText(m.getAwayScore());
        holder.status.setText(m.getStatus());
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }
}
