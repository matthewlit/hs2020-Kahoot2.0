package com.honorsmobileapps.matthewkelleher.kahoot21;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateScreen extends AppCompatActivity {

    Button createSA;
    Button createMC;
    Button createTF;
    ImageButton back;
    ImageButton done;
    EditText title;
    RecyclerView mRecyclerView;
    QuestionAdapter mAdapter;

    List<Question> questions;
    Quiz quiz;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final static String EXTRA_QUIZ = "com.honorsmobileapps.matthewkelleher.kahoot21.quizId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_screen);
        quiz = new Quiz();
        if (getIntent().getSerializableExtra(EXTRA_QUIZ) != null)
            quiz = (Quiz) getIntent().getSerializableExtra(EXTRA_QUIZ);
        questions = quiz.getQuestions();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new QuestionAdapter(questions);
        mRecyclerView.setAdapter(mAdapter);

        if (getIntent().getSerializableExtra(EXTRA_QUIZ) != null) {
            DocumentReference docRef = db.collection("quizzes").document(quiz.getId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> map = document.getData();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (entry.getKey().equals("questions")) {
                                    for (int i = 0; i < ((ArrayList<Map>) entry.getValue()).size(); i++) {
                                        Map hashMap = ((Map) ((ArrayList<Map>) entry.getValue()).get(i));
                                        String type = (String) hashMap.get("type");
                                        System.out.println("Type is " + type);
                                        if (type.equals("TF")) {
                                            mAdapter.addItem(new TrueFalseQuestion((String) hashMap.get("prompt"), (Boolean) hashMap.get("answer"), type));
                                            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                                        } else if (type.equals("SA")) {
                                            mAdapter.addItem(new ShortAnswerQuestion((String) hashMap.get("prompt"), (String) hashMap.get("answer"), type));
                                            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                                        } else if (type.equals("MC")) {
                                            mAdapter.addItem(new MultipleChoiceQuestion((String) hashMap.get("prompt"),
                                                    (String) hashMap.get("answer"), ((String) hashMap.get("wrongAnswerOne"))
                                                    , ((String) hashMap.get("wrongAnswerTwo")), ((String) hashMap.get("wrongAnswerThree")), type));
                                            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
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
        createSA = (Button) findViewById(R.id.newSAQuestion);
        createMC = (Button) findViewById(R.id.newMCQuestion);
        createTF = (Button) findViewById(R.id.newTFQuestion);
        back = (ImageButton) findViewById(R.id.back);
        done = (ImageButton) findViewById(R.id.done);
        title = (EditText) findViewById(R.id.title);

        title.setText(quiz.getQuizTitle());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == back.getId()) {
                    finish();
                }
                if (v.getId() == done.getId()) {
                    int i = 0;
                    while (i < quiz.getQuestions().size()) {
                        if (quiz.getQuestions().get(i).getPrompt().equals(""))
                            quiz.removeQuestion(quiz.getQuestions().get(i));
                        else if (quiz.getQuestions().get(i) instanceof ShortAnswerQuestion) {
                            if (((ShortAnswerQuestion) quiz.getQuestions().get(i)).getAnswer().equals(""))
                                quiz.removeQuestion(quiz.getQuestions().get(i));
                        } else if (quiz.getQuestions().get(i) instanceof MultipleChoiceQuestion) {
                            if (((MultipleChoiceQuestion) quiz.getQuestions().get(i)).getAnswer().equals("") ||
                                    ((MultipleChoiceQuestion) quiz.getQuestions().get(i)).getWrongAnswerOne().equals("") ||
                                    ((MultipleChoiceQuestion) quiz.getQuestions().get(i)).getWrongAnswerTwo().equals("") ||
                                    ((MultipleChoiceQuestion) quiz.getQuestions().get(i)).getWrongAnswerThree().equals(""))
                                quiz.removeQuestion(quiz.getQuestions().get(i));
                        }
                        i++;
                    }
                    if (!(quiz.getQuizTitle().equals("")) && quiz.getQuestions().size() != 0) {
                        if (quiz.getId().equals("")) {
                            DocumentReference newQuizRef = db.collection("quizzes").document();
                            quiz.setId(newQuizRef.getId());
                            newQuizRef.set(quiz);
                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                    .collection("quizzes").document(quiz.getId()).set(quiz);
                        } else {
                            db.collection("quizzes").document(quiz.getId()).set(quiz);
                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                    .collection("quizzes").document(quiz.getId()).set(quiz);
                        }
                    }
                    startActivity(MyQuizzesScreen.newIntent(CreateScreen.this));
                }
                if (v.getId() == createSA.getId()) {
                    mAdapter.addItem(new ShortAnswerQuestion("SA"));
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
                if (v.getId() == createMC.getId()) {
                    mAdapter.addItem(new MultipleChoiceQuestion("MC"));
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
                if (v.getId() == createTF.getId()) {
                    mAdapter.addItem(new TrueFalseQuestion("TF"));
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        };
        createSA.setOnClickListener(listener);
        createMC.setOnClickListener(listener);
        createTF.setOnClickListener(listener);
        back.setOnClickListener(listener);
        done.setOnClickListener(listener);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                quiz.setQuizTitle(s + "");
            }
        });

        quiz.setQuizAuthor(FirebaseAuth.getInstance().
                getCurrentUser().getDisplayName() + "");
    }

    public static Intent newIntent(Context pakageContext) {
        Intent i = new Intent(pakageContext, CreateScreen.class);
        return i;
    }

    public static Intent newIntent(Context pakageContext, Quiz quiz) {
        Intent i = new Intent(pakageContext, CreateScreen.class);
        i.putExtra(EXTRA_QUIZ, quiz);
        return i;
    }

    private class QuestionViewHolder extends RecyclerView.ViewHolder {
        public Button mTandFFalseButton;
        public Button mTandFTrueButton;
        public TextView mMCAnswerOther3TextView;
        public TextView mMCAnswerOther2TextView;
        public TextView mMCAnswerOther1TextView;
        public TextView mMCAnswerTextView;
        public TextView mPromptTextView;
        public TextView mAnswerTextView;
        public Question mQuestion;
        public TextView mQuestionNum;
        public ImageButton mDeleteButton;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            mPromptTextView = (TextView) itemView.findViewById(R.id.textViewQuestionPrompt);
            mAnswerTextView = (TextView) itemView.findViewById(R.id.textViewShortAnswerAnswer);
            mMCAnswerTextView = (TextView) itemView.findViewById(R.id.textViewMCAnswer);
            mMCAnswerOther1TextView = (TextView) itemView.findViewById(R.id.textViewMCAnswerOther1);
            mMCAnswerOther2TextView = (TextView) itemView.findViewById(R.id.textViewMCAnswerOther2);
            mMCAnswerOther3TextView = (TextView) itemView.findViewById(R.id.textViewMCAnswerOther3);
            mTandFTrueButton = (Button) itemView.findViewById(R.id.ButtonTandFTrue);
            mTandFFalseButton = (Button) itemView.findViewById(R.id.ButtonTandFFalse);
            mQuestionNum = (TextView) itemView.findViewById(R.id.questionNum);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.delete);
        }

        public void bindCourse(Question question) {
            mQuestion = question;
            mPromptTextView.setText(mQuestion.getPrompt());
            if (mQuestion instanceof ShortAnswerQuestion) {
                mAnswerTextView.setVisibility(View.VISIBLE);
                mTandFFalseButton.setVisibility(View.GONE);
                mTandFTrueButton.setVisibility(View.GONE);
                mMCAnswerTextView.setVisibility(View.GONE);
                mMCAnswerOther1TextView.setVisibility(View.GONE);
                mMCAnswerOther2TextView.setVisibility(View.GONE);
                mMCAnswerOther3TextView.setVisibility(View.GONE);
                mPromptTextView.setText(mQuestion.getPrompt());
                mAnswerTextView.setText(((ShortAnswerQuestion) mQuestion).getAnswer());
                mAnswerTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((ShortAnswerQuestion) mQuestion).setAnswer(s + "");
                    }
                });
            }
            else if (mQuestion instanceof TrueFalseQuestion) {
                mTandFFalseButton.setVisibility(View.VISIBLE);
                mTandFTrueButton.setVisibility(View.VISIBLE);
                mMCAnswerTextView.setVisibility(View.GONE);
                mMCAnswerOther1TextView.setVisibility(View.GONE);
                mMCAnswerOther2TextView.setVisibility(View.GONE);
                mMCAnswerOther3TextView.setVisibility(View.GONE);
                mAnswerTextView.setVisibility(View.GONE);
                mPromptTextView.setText(question.getPrompt());
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == mTandFFalseButton.getId()) {
                            ((TrueFalseQuestion) mQuestion).setAnswer(false);
                            if (((TrueFalseQuestion) mQuestion).getAnswer()) {
                                mTandFFalseButton.setBackgroundColor(getResources().getColor(R.color.red));
                                mTandFTrueButton.setBackgroundColor(getResources().getColor(R.color.green));
                            } else {
                                mTandFFalseButton.setBackgroundColor(getResources().getColor(R.color.green));
                                mTandFTrueButton.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        } else {
                            ((TrueFalseQuestion) mQuestion).setAnswer(true);
                            if (((TrueFalseQuestion) mQuestion).getAnswer()) {
                                mTandFFalseButton.setBackgroundColor(getResources().getColor(R.color.red));
                                mTandFTrueButton.setBackgroundColor(getResources().getColor(R.color.green));
                            } else {
                                mTandFFalseButton.setBackgroundColor(getResources().getColor(R.color.green));
                                mTandFTrueButton.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        }
                    }
                };
                mTandFFalseButton.setOnClickListener(listener);
                mTandFTrueButton.setOnClickListener(listener);
                if (((TrueFalseQuestion) mQuestion).getAnswer()) {
                    mTandFFalseButton.setBackgroundColor(getResources().getColor(R.color.red));
                    mTandFTrueButton.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    mTandFFalseButton.setBackgroundColor(getResources().getColor(R.color.green));
                    mTandFTrueButton.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
            else if (mQuestion instanceof MultipleChoiceQuestion) {
                mMCAnswerTextView.setVisibility(View.VISIBLE);
                mMCAnswerOther1TextView.setVisibility(View.VISIBLE);
                mMCAnswerOther2TextView.setVisibility(View.VISIBLE);
                mMCAnswerOther3TextView.setVisibility(View.VISIBLE);
                mTandFFalseButton.setVisibility(View.GONE);
                mTandFTrueButton.setVisibility(View.GONE);
                mAnswerTextView.setVisibility(View.GONE);
                mPromptTextView.setText(mQuestion.getPrompt());
                mMCAnswerTextView.setText(((MultipleChoiceQuestion) mQuestion).getAnswer());
                mMCAnswerOther1TextView.setText(((MultipleChoiceQuestion) mQuestion).getWrongAnswerOne());
                mMCAnswerOther2TextView.setText(((MultipleChoiceQuestion) mQuestion).getWrongAnswerTwo());
                mMCAnswerOther3TextView.setText(((MultipleChoiceQuestion) mQuestion).getWrongAnswerThree());
                mMCAnswerTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((MultipleChoiceQuestion) mQuestion).setAnswer(s + "");
                    }
                });
                mMCAnswerOther1TextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((MultipleChoiceQuestion) mQuestion).setWrongAnswerOne(s + "");
                    }
                });
                mMCAnswerOther2TextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((MultipleChoiceQuestion) mQuestion).setWrongAnswerTwo(s + "");
                    }
                });
                mMCAnswerOther3TextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((MultipleChoiceQuestion) mQuestion).setWrongAnswerThree(s + "");
                    }
                });
            }
            else{
                quiz.getQuestions().remove(question);
            }
            mPromptTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mQuestion.setPrompt(s + "");
                }
            });
            mQuestionNum.setText("Question #" + (mAdapter.getPosition(mQuestion) + 1));
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.removeItem(mQuestion);
                }
            });
        }
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder> {
        private List<Question> mQuestions;

        public QuestionAdapter(List<Question> questions) {
            mQuestions = questions;
        }

        @Override
        public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(CreateScreen.this);
            View view = layoutInflater.inflate(R.layout.question_list_item, parent, false);
            return new QuestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(QuestionViewHolder holder, int position) {
            Question question = mQuestions.get(position);
            holder.bindCourse(question);
        }

        @Override
        public int getItemCount() {
            return mQuestions.size();
        }

        public int getPosition(Question question) {
            return mQuestions.indexOf(question);
        }

        public void addItem(Question question) {
            quiz.addQuestion(question);
            mAdapter.notifyDataSetChanged();
        }

        public void removeItem(Question question) {
            quiz.removeQuestion(question);
            mAdapter.notifyDataSetChanged();
        }

        public List<Question> getmQuestions() {
            return mQuestions;
        }
    }
}
