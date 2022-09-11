package com.honorsmobileapps.matthewkelleher.kahoot21;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class MainScreen extends AppCompatActivity {

    Button create;
    Button discover;
    Button myQuizzes;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView mRecyclerView;
    QuizAdapter mAdapter;
    ArrayList<Quiz> quizzes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        create = (Button) findViewById(R.id.createtabButton);
        discover = (Button) findViewById(R.id.discoverButton);
        myQuizzes = (Button) findViewById(R.id.myQuizzesButton);
        quizzes = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new QuizAdapter(quizzes);
        mRecyclerView.setAdapter(mAdapter);

        db.collection("quizzes").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                if (v.getId() == myQuizzes.getId()) {
                    startActivity(MyQuizzesScreen.newIntent(MainScreen.this));
                }
                if (v.getId() == create.getId()) {
                    startActivity(CreateScreen.newIntent(MainScreen.this));
                }
            }
        };
        create.setOnClickListener(listener);
        discover.setOnClickListener(listener);
        myQuizzes.setOnClickListener(listener);
    }

    public static Intent newIntent(Context pakageContext) {
        Intent i = new Intent(pakageContext, MainScreen.class);
        return i;
    }

    private class QuizViewHolder extends RecyclerView.ViewHolder {
        private TextView mQuizTitleTextView;
        private TextView mQuizAuthorTextView;
        private Button mQuizPlayButton;
        private Quiz mQuiz;
        private ImageButton dup;
        private Quiz newQuiz;
        private ArrayList<Question> questions;

        public QuizViewHolder(View itemView) {
            super(itemView);
            mQuizTitleTextView = (TextView) itemView.findViewById(R.id.textViewQuizTitle);
            mQuizAuthorTextView = (TextView) itemView.findViewById(R.id.textViewQuizAuthor);
            mQuizPlayButton = (Button) itemView.findViewById(R.id.playButton);
            dup = (ImageButton) itemView.findViewById(R.id.duplicate);
            questions = new ArrayList<>();
        }

        public void bindCourse(final Quiz quiz) {
            mQuiz = quiz;
            newQuiz = mQuiz;
            mQuizTitleTextView.setText(mQuiz.getQuizTitle());
            mQuizAuthorTextView.setText("By: " + mQuiz.getQuizAuthor());
            dup.setVisibility(View.VISIBLE);
            mQuizPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PlayingQuizScreen.newIntent(MainScreen.this, mQuiz));
                }
            });
            dup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newQuiz.setQuizAuthor(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    newQuiz.setQuizTitle(quiz.getQuizTitle() + " Duplicate");
                    DocumentReference docRef = db.collection("quizzes").document(quiz.getId());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            System.out.println("Success");
                            if (task.isSuccessful()) {
                                System.out.println("Success");
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    System.out.println("Success");
                                    Map<String, Object> map = document.getData();
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        if (entry.getKey().equals("questions")) {
                                            for (int i = 0; i < ((ArrayList<Map>) entry.getValue()).size(); i++) {
                                                Map hashMap = ((Map) ((ArrayList<Map>) entry.getValue()).get(i));
                                                String type = (String) hashMap.get("type");
                                                System.out.println("Type is " + type);
                                                if (type.equals("TF")) {
                                                    newQuiz.addQuestion(new TrueFalseQuestion((String) hashMap.get("prompt"), (Boolean) hashMap.get("answer"), type));

                                                } else if (type.equals("SA")) {
                                                    newQuiz.addQuestion(new ShortAnswerQuestion((String) hashMap.get("prompt"), (String) hashMap.get("answer"), type));
                                                } else if (type.equals("MC")) {
                                                    newQuiz.addQuestion(new MultipleChoiceQuestion((String) hashMap.get("prompt"),
                                                            (String) hashMap.get("answer"), ((String) hashMap.get("wrongAnswerOne"))
                                                            , ((String) hashMap.get("wrongAnswerTwo")), ((String) hashMap.get("wrongAnswerThree")), type));
                                                } else {
                                                    System.out.println("Ain't good Chief");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    DocumentReference newQuizRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .collection("quizzes").document();
                    newQuiz.setId(newQuizRef.getId());
                    newQuizRef.set(newQuiz);
                    db.collection("quizzes").document(quiz.getId()).set(newQuiz);
                    startActivity(MyQuizzesScreen.newIntent(MainScreen.this));
                }
            });
        }
    }

    private class QuizAdapter extends RecyclerView.Adapter<QuizViewHolder> {
        private List<Quiz> mQuiz;

        public QuizAdapter(List<Quiz> quizzes) {
            mQuiz = quizzes;
        }

        @Override
        public QuizViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainScreen.this);
            View view = layoutInflater.inflate(R.layout.quiz_list_item, parent, false);
            return new QuizViewHolder(view);
        }

        @Override
        public void onBindViewHolder(QuizViewHolder holder, int position) {
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

        public void addItem(Quiz quiz) {
            mQuiz.add(quiz);
            mAdapter.notifyDataSetChanged();
        }
    }
}

