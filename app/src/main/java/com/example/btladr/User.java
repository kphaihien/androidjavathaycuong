package com.example.btladr;

import java.sql.Date;

public class User {
    private String name;
    private int point;
    private String playDate;


    public User(String name, int point,String playDate) {
        this.name = name;
        this.point = point;
        this.playDate=playDate;
    }

    public String getPlayDate(){return playDate;}
    public void setPlayDate(String playDate){this.playDate=playDate;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
