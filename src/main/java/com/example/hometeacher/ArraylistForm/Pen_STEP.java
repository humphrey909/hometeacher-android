package com.example.hometeacher.ArraylistForm;

import java.util.ArrayList;

public class Pen_STEP {


    String idx;
    ArrayList<Pen> TotalPen;
    ArrayList<Pen> RestPen;
    int drawcount;
    int drawcountFinal;

    //문서 idx, 전체 좌표값, 뒤로가기 좌표값, 그린횟수, 총 그린 횟수
    public Pen_STEP(String idx, ArrayList<Pen> TotalPen, ArrayList<Pen> RestPen, int drawcount, int drawcountFinal) {
        this.idx = idx;
        this.TotalPen = TotalPen;
        this.RestPen = RestPen;
        this.drawcount = drawcount;
        this.drawcountFinal = drawcountFinal;


    }

    public String getidx() {
        return idx;
    }
    public void setidx(String idx) {
        this.idx = idx;
    }

    public ArrayList<Pen> getTotalPen() {
        return TotalPen;
    }
    public void setTotalPen(ArrayList<Pen> TotalPen) {
        this.TotalPen = TotalPen;
    }

    public void addPen(Pen Pen){
        this.TotalPen.add(Pen);
    }

    public ArrayList<Pen> getRestPen() {
        return RestPen;
    }
    public void setRestPen(ArrayList<Pen> RestPen) {
        this.RestPen = RestPen;
    }


    public int getdrawcount() {
        return drawcount;
    }
    public void setdrawcount(int drawcount) {
        this.drawcount = drawcount;
    }

    public int getdrawcountFinal() {
        return drawcountFinal;
    }
    public void setdrawcountFinal(int drawcountFinal) {
        this.drawcountFinal = drawcountFinal;
    }

}