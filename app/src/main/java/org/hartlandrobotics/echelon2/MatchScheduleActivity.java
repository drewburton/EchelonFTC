package org.hartlandrobotics.echelon2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import org.hartlandrobotics.echelon2.blueAlliance.fragments.MatchListViewModel;
import org.hartlandrobotics.echelon2.blueAlliance.fragments.MatchesFragment;
import org.hartlandrobotics.echelon2.database.entities.Match;
import org.hartlandrobotics.echelon2.database.entities.MatchResult;
import org.hartlandrobotics.echelon2.database.repositories.EventRepo;
import org.hartlandrobotics.echelon2.database.repositories.MatchResultRepo;
import org.hartlandrobotics.echelon2.status.BlueAllianceStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchScheduleActivity extends EchelonActivity {
    EventRepo eventRepo;
    MatchResultRepo matchResultRepo;
    Map<String, List<MatchResult>> matchResultsByTeam =  new HashMap<>();
    List<MatchScheduleViewModel> viewModels = new ArrayList<>();
    RecyclerView matchRecycler;
    MatchListAdapter matchListAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MatchScheduleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_schedule);

        setupToolbar("Match Schedule");

        matchListAdapter = new MatchListAdapter(this);

      matchRecycler = findViewById(R.id.match_recycler);
      matchRecycler.setLayoutManager(new LinearLayoutManager(this));
      matchRecycler.setAdapter(matchListAdapter);
      matchRecycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        BlueAllianceStatus status = new BlueAllianceStatus(getApplicationContext());
        String currentEvent = status.getEventKey();

        eventRepo = new EventRepo(getApplication());
        matchResultRepo = new MatchResultRepo(getApplication());
        matchResultRepo.getMatchResultsByEvent(currentEvent).observe(this, matchResults -> {
            for( MatchResult matchResult : matchResults ){
                String teamKey = matchResult.getTeamKey();
                matchResultsByTeam.computeIfAbsent(teamKey,key -> new ArrayList<>());
                matchResultsByTeam.get(teamKey).add(matchResult);
            }

            eventRepo.getMatchesForEvent(currentEvent).observe(this, event -> {
                List<Match> matches = event.matches;

                for( Match match : matches ){
                    MatchScheduleViewModel matchScheduleViewModel = new MatchScheduleViewModel();

                    matchScheduleViewModel.setMatchNumber( match.getMatchNumber() );

                    int red1Average = getAverageByTeam(match.getRed1TeamKey());
                    matchScheduleViewModel.setRed1(match.getRed1TeamKey());
                    matchScheduleViewModel.setRed1Average(red1Average);

                    int red2Average = getAverageByTeam(match.getRed2TeamKey());
                    matchScheduleViewModel.setRed2(match.getRed2TeamKey());
                    matchScheduleViewModel.setRed2Average(red2Average);

                    int red3Average = getAverageByTeam(match.getRed3TeamKey());
                    matchScheduleViewModel.setRed3(match.getRed3TeamKey());
                    matchScheduleViewModel.setRed3Average(red3Average);


                    int blue1Average = getAverageByTeam(match.getBlue1TeamKey());
                    matchScheduleViewModel.setBlue1(match.getBlue1TeamKey());
                    matchScheduleViewModel.setBlue1Average(blue1Average);

                    int blue2Average = getAverageByTeam(match.getBlue2TeamKey());
                    matchScheduleViewModel.setBlue2(match.getBlue2TeamKey());
                    matchScheduleViewModel.setBlue2Average(blue2Average);

                    int blue3Average = getAverageByTeam(match.getBlue3TeamKey());
                    matchScheduleViewModel.setBlue3(match.getBlue3TeamKey());
                    matchScheduleViewModel.setBlue3Average(blue3Average);

                    viewModels.add(matchScheduleViewModel);
                }

                matchListAdapter.setMatches(viewModels);
                System.out.println("break here");
            });

        });
    }

    private int getAverageByTeam( String teamKey ){
        List<MatchResult> teamMatchResults = matchResultsByTeam.get(teamKey);
        if( teamMatchResults == null || teamMatchResults.size() == 0 ) return 0;

        int totalScore = 0;
        for( MatchResult matchResult : teamMatchResults ){
            int matchScore = 0;
            matchScore += matchResult.getAutoHighBalls() * 4;
            matchScore += matchResult.getAutoLowBalls() * 2;
            matchScore += (matchResult.getTaxiTarmac()? 1 : 0) * 2;
            matchScore += matchResult.getAutoHumanPlayerShots() * 4;

            matchScore += matchResult.getTeleOpHighBalls() * 2;
            matchScore += matchResult.getTeleOpLowBalls() * 1;

            matchScore += (matchResult.getEndHangLow()? 1 : 0 ) * 4;
            matchScore += (matchResult.getEndHangMid()? 1 : 0 ) * 6;
            matchScore += (matchResult.getEndHangHigh()? 1 : 0 ) * 10;
            matchScore += (matchResult.getEndHangTraverse()? 1 : 0 ) * 15;

            totalScore += matchScore;
        }

        int averageScore = totalScore / teamMatchResults.size();

        return averageScore;
    }

    public class MatchScheduleViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView matchNumber;
        //private MaterialTextView matchKey;
        private MaterialTextView red1;
        private MaterialTextView red2;
        private MaterialTextView red3;
        private MaterialTextView blue1;
        private MaterialTextView blue2;
        private MaterialTextView blue3;
        private MaterialTextView redPrediction;
        private MaterialTextView bluePrediction;

        private MatchScheduleViewModel matchScheduleViewModel;

        MatchScheduleViewHolder(View itemView){
            super(itemView);

            matchNumber = itemView.findViewById(R.id.match_number);
            red1 = itemView.findViewById(R.id.red1);
            red2 = itemView.findViewById(R.id.red2);
            red3 = itemView.findViewById(R.id.red3);
            blue1 = itemView.findViewById(R.id.blue1);
            blue2 = itemView.findViewById(R.id.blue2);
            blue3 = itemView.findViewById(R.id.blue3);
            redPrediction = itemView.findViewById(R.id.red_prediction);
            bluePrediction = itemView.findViewById(R.id.blue_prediction);
        }

        public void setMatch(MatchScheduleViewModel matchScheduleViewModel){
            this.matchScheduleViewModel = matchScheduleViewModel;

            matchNumber.setText(String.valueOf(matchScheduleViewModel.getMatchNumber()));
            red1.setText("1: " + matchScheduleViewModel.getRed1());
            red2.setText("2: " + matchScheduleViewModel.getRed2());
            red3.setText("3: " + matchScheduleViewModel.getRed3());
            blue1.setText("1: " + matchScheduleViewModel.getBlue1());
            blue2.setText("2: " + matchScheduleViewModel.getBlue2());
            blue3.setText("3: " + matchScheduleViewModel.getBlue3());
            redPrediction.setText( String.valueOf( matchScheduleViewModel.getRedTotal() ) );
            bluePrediction.setText( String.valueOf( matchScheduleViewModel.getBlueTotal() ) );
        }

        public void setDisplayText(String displayText){
            matchNumber.setText(displayText);
        }
    }

    public class MatchListAdapter extends RecyclerView.Adapter<MatchScheduleViewHolder>{
        private final LayoutInflater inflater;
        private List<MatchScheduleViewModel> holderViewModels;

        MatchListAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public MatchScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.list_item_match_schedule, parent, false);
            return new MatchScheduleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MatchScheduleViewHolder holder, int position) {
            if(holderViewModels != null){
                holder.setMatch(holderViewModels.get(position));
            }else{
                holder.setDisplayText("No Match Data Yet...");
            }
        }

        void setMatches(List<MatchScheduleViewModel> vms){
            holderViewModels = vms.stream()
                    .sorted(Comparator.comparingInt(m -> Integer.valueOf( m.getMatchNumber())))
                    .collect(Collectors.toList());

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return holderViewModels == null ? 0 : holderViewModels.size();
        }
    }
}