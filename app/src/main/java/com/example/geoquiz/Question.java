package com.example.geoquiz;

public class Question {
    private int mTestResId;
    private boolean mAnswerTrue;    //答题是否正确


    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }


    public int getTestResId() {
        return mTestResId;
    }

    public void setTestResId(int testResId) {
        mTestResId = testResId;
    }

    public Question(int testResId, boolean answerTrue) {
        mTestResId = testResId;
        mAnswerTrue = answerTrue;
    }
}
