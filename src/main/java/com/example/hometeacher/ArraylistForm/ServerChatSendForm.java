package com.example.hometeacher.ArraylistForm;

public class ServerChatSendForm {
    //명령어조건, 방고유번호, 방 인원수, 유저고유번호, 문자내용, 시간
    private String commend;
    private String rid;
    private String maxnum;
    private String uid;
    private String message;
    private String regdate;
    private String imgchk;
    private String nid;
    private String roomtype;
    private String chattype;
    private String sendrid;

    public ServerChatSendForm(String commend, String rid, String maxnum, String uid, String message, String regdate, String imgchk, String nid, String roomtype, String chattype, String sendrid) {
        this.commend = commend;
        this.rid = rid;
        this.maxnum = maxnum;
        this.uid = uid;
        this.message = message;
        this.regdate = regdate;
        this.imgchk = imgchk;
        this.nid = nid;
        this.roomtype = roomtype;
        this.chattype = chattype;
        this.sendrid = sendrid;
    }
    public String getcommend() {
        return commend;
    }
    public void setcommend(String commend) {
        this.commend = commend;
    }

    public String getrid() {
        return rid;
    }
    public void setrid(String rid) {
        this.rid = rid;
    }



    public String getmaxnum() {
        return maxnum;
    }
    public void setmaxnum(String maxnum) {
        this.maxnum = maxnum;
    }

    public String getuid() {
        return uid;
    }
    public void setuid(String uid) { this.uid = uid; }

    public String getmessage() {
        return message;
    }
    public void setmessage(String message) {
        this.message = message;
    }


    public String getregdate() {
        return regdate;
    }
    public void setregdate(String regdate) {
        this.regdate = regdate;
    }

    public String getimgchk() {
        return imgchk;
    }
    public void setimgchk(String imgchk) {
        this.imgchk = imgchk;
    }

    public String getnid() {
        return nid;
    }
    public void setnid(String nid) {
        this.nid = nid;
    }

    public String getroomtype() {
        return roomtype;
    }
    public void setroomtype(String roomtype) {
        this.roomtype = roomtype;
    }

    public String getchattype() {
        return chattype;
    }
    public void setchattype(String chattype) {
        this.chattype = chattype;
    }
    public String getsendrid() {
        return sendrid;
    }
    public void setsendrid(String sendrid) {
        this.sendrid = sendrid;
    }

}