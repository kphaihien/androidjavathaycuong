package com.example.btladr;

public class Answer {
    private String content;
    private boolean isCorrect;

    public Answer(boolean isCorrect, String content) {
        this.isCorrect = isCorrect;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
