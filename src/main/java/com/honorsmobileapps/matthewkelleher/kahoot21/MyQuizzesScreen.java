package com.honorsmobileapps.matthewkelleher.kahoot21;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


public class MyQuizzesScreen extends Activity {

    Button create;
    Button discover;
    Button myQuizzes;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView mRecyclerView;
    MyQuizAdapter mAdapter;
    ArrayList<Quiz> quizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quizzes);

        create = (Button) findViewById(R.id.createtabButton);
        discover = (Button) findViewById(R.id.discoverButton);
        myQuizzes = (Button) findViewById(R.id.myQuizzesButton);
        quizzes = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyQuizAdapter(quizzes);
        mRecyclerView.setAdapter(mAdapter);

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("quizzes").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc : value) {
                    if (!(mAdapter.getmQuiz().contains(doc.toObject(Quiz.class))))
                        mAdapter.addItem(doc.toObject(Quiz.class));
                }
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == discover.getId()) {
                    startActivity(MainScreen.newIntent(MyQuizzesScreen.this));
                }
                if (v.getId() == create.getId()) {
                    startActivity(CreateScreen.newIntent(MyQuizzesScreen.this));
                }
            }
        };
        create.setOnClickListener(listener);
        discover.setOnClickListener(listener);
        myQuizzes.setOnClickListener(listener);
    }

    public static Intent newIntent(Context pakageContext) {
        Intent i = new Intent(pakageContext, MyQuizzesScreen.class);
        return i;
    }

    private class MyQuizViewHolder extends RecyclerView.ViewHolder {
        private TextView mQuizTitleTextView;
        private TextView mQuizAuthorTextView;
        private Button mQuizPlayButton;
        private Quiz mQuiz;
        private ImageButton delete;
        private ImageButton edit;
        private ArrayList<Question> questions;

        public MyQuizViewHolder(View itemView) {
            super(itemView);
            mQuizTitleTextView = (TextView) itemView.findViewById(R.id.textViewQuizTitle);
            mQuizAuthorTextView = (TextView) itemView.findViewById(R.id.textViewQuizAuthor);
            mQuizPlayButton = (Button) itemView.findViewById(R.id.playButton);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
        }

        public void bindCourse(final Quiz quiz) {
            mQuiz = quiz;
            mQuizTitleTextView.setText(mQuiz.getQuizTitle());
            mQuizAuthorTextView.setText(mQuiz.getQuizAuthor());
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            mQuizPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PlayingQuizScreen.newIntent(MyQuizzesScreen.this, mQuiz));
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .collection("quizzes").document(mQuiz.getId()).delete();
                    db.collection("quizzes").document(mQuiz.getId()).delete();
                    mAdapter.removeItem(mQuiz);
                    startActivity(MyQuizzesScreen.newIntent(MyQuizzesScreen.this));
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = CreateScreen.newIntent(MyQuizzesScreen.this, mQuiz);
                    startActivity(i);
                }
            });
        }
    }

    private class MyQuizAdapter extends RecyclerView.Adapter<MyQuizViewHolder> {
        private List<Quiz> mQuiz;

        public MyQuizAdapter(List<Quiz> quizzes) {
            mQuiz = quizzes;
        }

        @Override
        public MyQuizViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MyQuizzesScreen.this);
            View view = layoutInflater.inflate(R.layout.quiz_list_item, parent, false);
            return new MyQuizViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyQuizViewHolder holder, int position) {
            Quiz quiz = mQuiz.get(position);
            holder.bindCourse(quiz);
        }

        @Override
        public int getItemCount() {
            return mQuiz.size();
        }

        public List<Quiz> getmQuiz() {
            return mQuiz;
        }

        public void removeItem(Quiz quiz) {
            mQuiz.remove(quiz);
            mAdapter.notifyDataSetChanged();
        }

        public void addItem(Quiz quiz) {
            mQuiz.add(quiz);
            mAdapter.notifyDataSetChanged();
        }
    }
}
