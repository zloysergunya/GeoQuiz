package com.bignerdranch.android.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private LinearLayout mBgElement;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mCheatButton;
    private ImageButton mRestartGame;
    private TextView mQuestionTextView;
    private TextView mCurrentQuestionTextView;
    private TextView mCountHintsTextView;
    private TextView mTrueAnswers;
    private ImageView mMainQuestionImageView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, R.drawable.australia_01, true),
            new Question(R.string.question_oceans, R.drawable.australia_01, true),
            new Question(R.string.question_mideast, R.drawable.australia_01, false),
            new Question(R.string.question_day, R.drawable.australia_01, true),
            new Question(R.string.question_africa, R.drawable.australia_01, false),
            new Question(R.string.question_americas, R.drawable.australia_01, true),
            new Question(R.string.question_continents, R.drawable.australia_01, true),
            new Question(R.string.question_penguins, R.drawable.australia_01, false),
            new Question(R.string.question_asia, R.drawable.australia_01, true),
            new Question(R.string.question_island_australia, R.drawable.australia_01, false)
    };

    private int mCurrentIndex = 0;
    private int mCountAnswers = 0;
    private int mCountTrueAnswers = 0;
    private int mCountHints = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mBgElement = (LinearLayout) findViewById(R.id.bgElement);
        mBgElement .setOnTouchListener(new OnSwipeTouchListener(QuizActivity.this) {
            @Override
            public void onSwipeRight() {
                if (mCurrentIndex != 0) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                } else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                isAnswered(mCurrentIndex);
                updateQuestion();
            }
            @Override
            public void onSwipeLeft() {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                isAnswered(mCurrentIndex);
            }
        });

        mMainQuestionImageView = (ImageView) findViewById(R.id.main_question_photo);

        mRestartGame = (ImageButton) findViewById(R.id.restart_button);
        mRestartGame.setVisibility(View.GONE);
        mRestartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        mCurrentQuestionTextView = (TextView) findViewById(R.id.current_question);
        mCurrentQuestionTextView.setTypeface(null, Typeface.BOLD);
        mCurrentQuestionTextView.setText("Question " + (mCurrentIndex + 1) + " of " + mQuestionBank.length);

        mCountHintsTextView = (TextView) findViewById(R.id.count_hints);
        mCountHintsTextView.setText(mCountHints + " hint(s) left");

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mTrueAnswers = (TextView) findViewById(R.id.true_answers);
        mTrueAnswers.setTypeface(null, Typeface.BOLD);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                mCountAnswers++;
                if (mQuestionBank[mCurrentIndex].isAnswerTrue()) {
                    mCountTrueAnswers++;
                    mBgElement.setBackgroundColor(Color.rgb(144, 238, 144)); // Green
                } else {
                    mBgElement.setBackgroundColor(Color.rgb(250, 128, 114)); // Red
                }
                mTrueAnswers.setText(mCountTrueAnswers + " out of " + mQuestionBank.length + " correct answers");
                percantageOfCorrectAnswers(mCountAnswers, mCountTrueAnswers);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                mCountAnswers++;
                if (!mQuestionBank[mCurrentIndex].isAnswerTrue()) {
                    mCountTrueAnswers++;
                    mBgElement.setBackgroundColor(Color.rgb(144, 238, 144)); // Green
                } else {
                    mBgElement.setBackgroundColor(Color.rgb(250, 128, 114)); // Red
                }
                mTrueAnswers.setText(mCountTrueAnswers + " out of " + mQuestionBank.length + " correct answers");
                percantageOfCorrectAnswers(mCountAnswers, mCountTrueAnswers);
            }
        });

        mCheatButton = (ImageButton) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
        mBgElement.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mQuestionBank[mCurrentIndex].setCheatered(CheatActivity.wasAnswerShown(data));
            if (mQuestionBank[mCurrentIndex].isCheatered()) {
                mCountHints--;
                if (mCountHints <= 0) {
                    mCheatButton.setEnabled(false);
                    mCountHintsTextView.setText("Hints are over!");
                } else {
                    mCountHintsTextView.setText(mCountHints + " hint(s) left");
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int questionText = mQuestionBank[mCurrentIndex].getTextResId();
        int questionImage = mQuestionBank[mCurrentIndex].getImageResId();
        mQuestionTextView.setText(questionText);
        mMainQuestionImageView.setImageResource(questionImage);
        mBgElement.setBackgroundColor(mQuestionBank[mCurrentIndex].getBgColor());
        mCurrentQuestionTextView.setText("Question " + (mCurrentIndex + 1) + " of " + mQuestionBank.length);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if(mQuestionBank[mCurrentIndex].isCheatered()) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mQuestionBank[mCurrentIndex].setBgColor(Color.rgb(144, 238, 144)); // Green
            } else {
                messageResId = R.string.incorrect_toast;
                mQuestionBank[mCurrentIndex].setBgColor(Color.rgb(250, 128, 114)); // Red
            }
        }
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        mQuestionBank[mCurrentIndex].setAnswered(true);

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void isAnswered(int index){
        boolean isQuestionAnswered = mQuestionBank[index].isAnswered();
        mTrueButton.setEnabled(!isQuestionAnswered);
        mFalseButton.setEnabled(!isQuestionAnswered);
    }

    private void percantageOfCorrectAnswers(int countAnswers, int countTrueAnswers){
        if (countAnswers == mQuestionBank.length){
            double percent = countTrueAnswers * 100.0 / countAnswers;
            Toast.makeText(QuizActivity.this, "Percentage of correct answers "
                    + Math.round(percent) + "%", Toast.LENGTH_SHORT).show();
            mRestartGame.setVisibility(View.VISIBLE);
        }
    }

    private void restartGame() {
        this.recreate();
        mCurrentIndex = 0;
    }
}
