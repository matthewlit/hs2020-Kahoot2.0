package com.honorsmobileapps.matthewkelleher.kahoot21;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardScreen extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MyPlayerAdapter mAdapter;
    ArrayList<Map> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_screen);

        int score = getIntent().getIntExtra("Score", 0);

        HashMap hashMap = new HashMap();
        hashMap.put("Name",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        hashMap.put("Score", score);
        players.add(hashMap);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyPlayerAdapter(players);
        mRecyclerView.setAdapter(mAdapter);

    }

    public static Intent newIntent(Context pakageContext, int score) {
        Intent i = new Intent(pakageContext, MyQuizzesScreen.class);
        i.putExtra("Score",score);
        return i;
    }

    private class MyPlayerViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerNameTextView;
        private TextView mPlayerScoreTextView;
        private String mName;
        private int mScore;

        public MyPlayerViewHolder(View itemView) {
            super(itemView);
            mPlayerNameTextView = (TextView) itemView.findViewById(R.id.name);
            mPlayerScoreTextView = (TextView) itemView.findViewById(R.id.score);
        }

        public void bindPlayer(String name, int score) {
            mName = name;
            mScore = score;
            mPlayerNameTextView.setText(mName);
            mPlayerScoreTextView.setText("Score: " + score);

        }
    }
    private class MyPlayerAdapter extends RecyclerView.Adapter<MyPlayerViewHolder> {
        private List<Map> mPlayers;

        public MyPlayerAdapter(List<Map> players) {
            mPlayers = players;
        }

        @Override
        public MyPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(LeaderboardScreen.this);
            View view = layoutInflater.inflate(R.layout.player_list_item, parent, false);
            return new MyPlayerViewHolder(view);
        }

        @Override
        public void onBindViewHolder (MyPlayerViewHolder holder, int position) {
            String player = (String)players.get(position).get("Name");
            int score = (int)players.get(position).get("Score");
            holder.bindPlayer(player, score);
        }

        @Override
        public int getItemCount() {
            return mPlayers.size();
        }

        public List<Map> getmPlayers() {
            return mPlayers;
        }

    }
}

