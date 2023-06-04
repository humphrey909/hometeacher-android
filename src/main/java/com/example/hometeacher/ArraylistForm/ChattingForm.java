package com.example.hometeacher.ArraylistForm;

public class ChattingForm {

    //private String group;
    private String who;
    private String chatidx;
    private String uid;
    private String name;
    private String message;
    private String regdate;
    private String profileimg;
    private String noreadnum;
    private String noreaduidarr;
    private String imgchk;
    private String sendrid;
    private String availablilty;
    private boolean isSelected;

    public ChattingForm(String who, String chatidx, String uid, String name, String message, String regdate, String profileimg, String noreadnum, String noreaduidarr, String imgchk, String sendrid, String availablilty, boolean isSelected) {
        this.who = who;
        this.chatidx = chatidx;
        this.uid = uid;
        this.name = name;
        this.message = message;
        this.regdate = regdate;
        this.profileimg = profileimg;
        this.noreadnum = noreadnum;
        this.noreaduidarr = noreaduidarr;
        this.imgchk = imgchk;
        this.sendrid = sendrid;
        this.availablilty = availablilty;
        this.isSelected = isSelected;
    }
    public String getwho() {
        return who;
    }
    public void setwho(String who) {
        this.who = who;
    }

    public String getchatidx() {
        return chatidx;
    }
    public void setchatidx(String chatidx) {
        this.chatidx = chatidx;
    }

    public String getuid() {
        return uid;
    }
    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }

    public String getmessage() {
        return message;
    }
    public void setmessage(String message) {
        this.message = message;
    }


    public String getprofileimg() {
        return profileimg;
    }
    public void setprofileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public String getregdate() {
        return regdate;
    }
    public void setregdate(String regdate) {
        this.regdate = regdate;
    }


    public String getnoreadnum() {
        return noreadnum;
    }
    public void setnoreadnum(String noreadnum) {
        this.noreadnum = noreadnum;
    }

    public String getnoreaduidarr() {
        return noreaduidarr;
    }
    public void setnoreaduidarr(String noreaduidarr) {
        this.noreaduidarr = noreaduidarr;
    }

    public String getimgchk() {
        return imgchk;
    }
    public void setimgchk(String imgchk) {
        this.imgchk = imgchk;
    }
    public String getsendrid() {
        return sendrid;
    }
    public void setsendrid(String sendrid) {
        this.sendrid = sendrid;
    }
    public String getavailablilty() {
        return availablilty;
    }
    public void setavailablilty(String availablilty) {
        this.availablilty = availablilty;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}