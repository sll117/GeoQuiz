package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextview;


    private static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEAT_SIGN = "already_cheat";
    private static final int REQUEST_CODE_CHEAT = 0;

    private static int userAnsweredCorrect = 0;   //答对题数
    private static int userAnsweredWrong = 0;   //答错题数
    private int answerLength = 0;    //回答问题个数


    //问题数组
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;  //题目号
    private boolean[] mIsCheater = new boolean[mQuestionBank.length];//判断题目是否作弊过

    //显示新问题
    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTestResId();
        mQuestionTextview.setText(question);
    }

    //检测用户点击的答案是否正确
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;    //作弊不计入回答题数中
        } else {
            if (userPressedTrue == answerIsTrue) {
                mQuestionBank[mCurrentIndex].setAnswerTrue(true);
                userAnsweredCorrect++;   //答对题数加一
                messageResId = R.string.correct_toast;
            } else {
                mQuestionBank[mCurrentIndex].setAnswerTrue(false);
                userAnsweredWrong++;          //答错题数加一
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast t = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);  //显示是否正确
        t.setGravity(Gravity.TOP, 0, 0);     //消息显示在上方
        t.show();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化mIsCheater数组
        for (int i = 0; i < 5; i++) {
            mIsCheater[i] = false;
        }

        mQuestionTextview = (TextView) findViewById(R.id.question_text_view);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(MainActivity.this, answerIsTrue); //启动cheatActivity
                startActivityForResult(i, REQUEST_CODE_CHEAT);   //获取CheatActivity传过来的信息
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
//                mIsCheater=false;    删掉对下一题的初始化操作
                updateQuestion();
                //显示结果
                answerLength++;
                if (answerLength == mQuestionBank.length) {
                    String str = "答对题数：" + userAnsweredCorrect + "答错题数：" + userAnsweredWrong + "未作答：" + (mQuestionBank.length - userAnsweredWrong - userAnsweredCorrect);
                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
                }
            }
        });

        if (savedInstanceState != null) {
            //取出横屏状态下的数据
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);   //取出mCurrentIndex
            answerLength = savedInstanceState.getInt("answer", 0);
            userAnsweredCorrect = savedInstanceState.getInt("correct", 0);
            userAnsweredWrong = savedInstanceState.getInt("wrong", 0);
            mIsCheater = savedInstanceState.getBooleanArray(CHEAT_SIGN);    //取出作弊情况
        }
        updateQuestion();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //没看过答案
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
    }

    //设备旋转，需要保存的值
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);   //记录题目号
        //记录总题数。答对题数，答错题数
        savedInstanceState.putInt("answer", answerLength);
        savedInstanceState.putInt("correct", userAnsweredCorrect);
        savedInstanceState.putInt("wrong", userAnsweredWrong);
        savedInstanceState.putBooleanArray(CHEAT_SIGN, mIsCheater);//记录作弊情况
    }


}


