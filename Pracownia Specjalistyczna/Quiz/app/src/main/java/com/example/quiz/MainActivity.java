package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView questionTextView;
    private Button trueButton, falseButton, nextButton;
    private Question[] questions = new Question[]{
            new Question(R.string.q_activity, true),
            new Question(R.string.q_version, false),
            new Question(R.string.q_listener, true),
            new Question(R.string.q_resources, true),
            new Question(R.string.q_find_resources, false)
    };

    private int currentQuestionIndex = 0;

    private int correctAnswers = 0;

    private static final String QUIZ_TAG = "MainActivity";
    private static final String KEY_CURRENT_QUESTION_INDEX = "currentQuestionIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(QUIZ_TAG, "Wywołanie onCreate");
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.question_text_view);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);

        if (savedInstanceState != null) {
            currentQuestionIndex = savedInstanceState.getInt(KEY_CURRENT_QUESTION_INDEX);
        }

        showCurrentQuestion();

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerCorrectness(true);
            }
        });
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerCorrectness(false);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionIndex = (currentQuestionIndex + 1) % questions.length;
                showCurrentQuestion();
            }
        });
    }

    private void checkAnswerCorrectness(boolean userAnswer) {
        boolean correctAnswer = questions[currentQuestionIndex].isTrue();
        int messageId = 0;

        if (userAnswer == correctAnswer) {
            messageId = R.string.correct_answer;
            correctAnswers++;
        } else {
            messageId = R.string.incorrect_answer;
        }

        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();

        // Sprawdzenie, czy to ostatnie pytanie
        if (currentQuestionIndex == questions.length - 1)
            showQuizResult();
    }

    private void showQuizResult() {
        String resultMessage = "Wynik: " + correctAnswers + " z " + questions.length;
        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show();

        correctAnswers = 0;
    }


    private void showCurrentQuestion() {
        questionTextView.setText(questions[currentQuestionIndex].getId());

        if (currentQuestionIndex == questions.length - 1)
            nextButton.setText("Zakończ quiz");
        else
            nextButton.setText("Następne");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(QUIZ_TAG, "Wywołanie onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(QUIZ_TAG, "Wywołanie onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(QUIZ_TAG, "Wywołanie onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(QUIZ_TAG, "Wywołanie onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(QUIZ_TAG, "Wywołanie onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(QUIZ_TAG, "Wywołanie onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(QUIZ_TAG, "Wywołanie onSaveInstanceState");
        outState.putInt(KEY_CURRENT_QUESTION_INDEX, currentQuestionIndex);
    }
}