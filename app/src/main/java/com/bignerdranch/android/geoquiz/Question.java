package com.bignerdranch.android.geoquiz;

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAnswered;
    private int mBgColor;
    private boolean mCheatered;
    private int mImageResId;

    public Question() {}

    public Question(int textResId, int imageResId, boolean answerTrue) {
        mTextResId = textResId;
        mImageResId = imageResId;
        mAnswerTrue = answerTrue;
        mAnswered = false;
        mCheatered = false;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public boolean isAnswered() {
        return mAnswered;
    }

    public void setAnswered(boolean answered) {
        mAnswered = answered;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int bgColor) {
        mBgColor = bgColor;
    }

    public boolean isCheatered() {
        return mCheatered;
    }

    public void setCheatered(boolean cheatered) {
        mCheatered = cheatered;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public void setImageResId(int imageResId) {
        mImageResId = imageResId;
    }
}
