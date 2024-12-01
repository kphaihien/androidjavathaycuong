package com.example.btladr;

import java.util.*;

public class Question {
    private int number;
    private String content;
    private List<Answer> listAnswer;
    private String truehint;

    public String getTruehint() {
        return truehint;
    }

    public void setTruehint(String truehint) {
        this.truehint = truehint;
    }



    public Question(List<Answer> listAnswer, String content, int number,String truehint) {
        this.listAnswer = listAnswer;
        this.content = content;
        this.number = number;
        this.truehint=truehint;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Answer> getListAnswer() {
        return listAnswer;
    }

    public void setListAnswer(List<Answer> listAnswer) {
        this.listAnswer = listAnswer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
