package com.honorsmobileapps.matthewkelleher.kahoot21;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayingQuizScreen extends AppCompatActivity {

    Quiz quiz;
    List<Question> questions;
    MediaPlayer mMediaPlayer;
    static int questionNum;
    Button MCButton1;
    Button MCButton2;
    Button MCButton3;
    Button MCButton4;
    Button TFTrue;
    Button TFFalse;
    EditText SAUserAnswer;
    Button submit;
    TextView prompt;
    LinearLayout MCView;
    LinearLayout SAView;
    LinearLayout TFView;
    LinearLayout AnswerView;
    ImageView CorrectView;
    ImageView WrongView;
    TextView questionNumTextView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final static String EXTRA_QUIZ = "com.honorsmobileapps.matthewkelleher.kahoot21.quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_quiz_screen);
        quiz = new Quiz();
        if (getIntent().getSerializableExtra(EXTRA_QUIZ) != null)
            quiz = (Quiz) getIntent().getSerializableExtra(EXTRA_QUIZ);
        questions = quiz.getQuestions();
        if (getIntent().getSerializableExtra(EXTRA_QUIZ) != null) {
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
                                            quiz.addQuestion(new TrueFalseQuestion((String) hashMap.get("prompt"), (Boolean) hashMap.get("answer"), type));

                                        } else if (type.equals("SA")) {
                                            quiz.addQuestion(new ShortAnswerQuestion((String) hashMap.get("prompt"), (String) hashMap.get("answer"), type));
                                        } else if (type.equals("MC")) {
                                            quiz.addQuestion(new MultipleChoiceQuestion((String) hashMap.get("prompt"),
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
        }
        questionNum = 0;

        mMediaPlayer = MediaPlayer.create(this, R.raw.kahoot);
        mMediaPlayer.start();
        MCButton1 = (Button) findViewById(R.id.MCOptionOne);
        MCButton2 = (Button) findViewById(R.id.MCOptionTwo);
        MCButton3 = (Button) findViewById(R.id.MCOptionThree);
        MCButton4 = (Button) findViewById(R.id.MCOptionFour);
        TFTrue = (Button) findViewById(R.id.ButtonTandFTrue);
        TFFalse = (Button) findViewById(R.id.ButtonTandFFalse);
        SAUserAnswer = (EditText) findViewById(R.id.SAAnswer);
        submit = (Button) findViewById(R.id.submit);
        prompt = (TextView) findViewById(R.id.prompt);
        MCView = (LinearLayout) findViewById(R.id.MCQuestionView);
        SAView = (LinearLayout) findViewById(R.id.SAQuestionView);
        TFView = (LinearLayout) findViewById(R.id.TFQuestionView);
        questionNumTextView = (TextView) findViewById(R.id.questionNumber);

        questionNumTextView.setText("Question #" + (questionNum + 1));
        prompt.setText(questions.get(questionNum).getPrompt());
        if (questions.size() == questionNum) {
            //TODO: Show final score
        } else if (questions.get(questionNum) instanceof TrueFalseQuestion) {
            TFView.setVisibility(View.VISIBLE);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnswerView.setVisibility(View.VISIBLE);
                    if (v.getId() == TFTrue.getId()) {
                        if (((TrueFalseQuestion) questions.get(questionNum)).getAnswer() == true) {
                            CorrectView.setVisibility(View.VISIBLE);
                        } else if (((TrueFalseQuestion) questions.get(questionNum)).getAnswer() == false) {
                            WrongView.setVisibility(View.VISIBLE);
                        }
                    } else if (v.getId() == TFFalse.getId()) {
                        if (((TrueFalseQuestion) questions.get(questionNum)).getAnswer() == true) {
                            WrongView.setVisibility(View.VISIBLE);
                        } else if (((TrueFalseQuestion) questions.get(questionNum)).getAnswer() == false) {
                            CorrectView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };
            TFTrue.setOnClickListener(listener);
            TFFalse.setOnClickListener(listener);
        } else if (questions.get(questionNum) instanceof ShortAnswerQuestion) {
            SAView.setVisibility(View.VISIBLE);
            prompt.setText(questions.get(questionNum).getPrompt());
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnswerView.setVisibility(View.VISIBLE);
                    if ((SAUserAnswer.getText() + "").equals(((ShortAnswerQuestion) questions.get(questionNum)).getAnswer())) {
                        CorrectView.setVisibility(View.VISIBLE);
                    } else {
                        WrongView.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else if (questions.get(questionNum) instanceof MultipleChoiceQuestion) {
            MCView.setVisibility(View.VISIBLE);
            prompt.setText(questions.get(questionNum).getPrompt());
            int i = (int) (Math.random() * 4);
            if (i == 0) {
                MCButton1.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer());
                MCButton2.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerOne());
                MCButton3.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerTwo());
                MCButton4.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerThree());
            }
            if (i == 1) {
                MCButton2.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer());
                MCButton3.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerOne());
                MCButton4.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerTwo());
                MCButton1.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerThree());
            }
            if (i == 2) {
                MCButton3.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer());
                MCButton4.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerOne());
                MCButton2.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerTwo());
                MCButton1.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerThree());
            }
            if (i == 3) {
                MCButton4.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer());
                MCButton3.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerOne());
                MCButton2.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerTwo());
                MCButton1.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerThree());
            } else {
                MCButton2.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer());
                MCButton4.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerOne());
                MCButton3.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerTwo());
                MCButton1.setText(((MultipleChoiceQuestion) questions.get(questionNum)).getWrongAnswerThree());
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnswerView.setVisibility(View.VISIBLE);
                    if (v.getId() == MCButton1.getId()) {
                        if (MCButton1.getText().equals(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer())) {
                            CorrectView.setVisibility(View.VISIBLE);
                        } else {
                            WrongView.setVisibility(View.VISIBLE);
                        }
                    } else if (v.getId() == MCButton2.getId()) {
                        if (MCButton2.getText().equals(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer())) {
                            CorrectView.setVisibility(View.VISIBLE);
                        } else {
                            WrongView.setVisibility(View.VISIBLE);
                        }
                    } else if (v.getId() == MCButton3.getId()) {
                        if (MCButton3.getText().equals(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer())) {
                            CorrectView.setVisibility(View.VISIBLE);
                        } else {
                            WrongView.setVisibility(View.VISIBLE);
                        }
                    } else if (v.getId() == MCButton4.getId()) {
                        if (MCButton4.getText().equals(((MultipleChoiceQuestion) questions.get(questionNum)).getAnswer())) {
                            CorrectView.setVisibility(View.VISIBLE);
                        } else {
                            WrongView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };
            MCButton1.setOnClickListener(listener);
            MCButton2.setOnClickListener(listener);
            MCButton3.setOnClickListener(listener);
            MCButton4.setOnClickListener(listener);
        }
    }

    public static Intent newIntent(Context pakageContext, Quiz quiz) {
        Intent i = new Intent(pakageContext, PlayingQuizScreen.class);
        i.putExtra(EXTRA_QUIZ, quiz);
        return i;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public static void incrementQuestionNum() {
        questionNum++;
    }
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer.isPlaying()) {
            releaseMediaPlayer();
        }
    }
}
