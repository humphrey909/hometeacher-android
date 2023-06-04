package com.example.hometeacher.ArraylistForm;

public class ServerWhiteboardSendForm {
    //명령어조건
    private String commend;
    private String rid;
    private String uid;
    private String roomtype;
    private String informationdata;
    private String tooltype;
    private String drawcount;
    private String documentcount;
    private String documentidx;

    //화이트보드 같이 사용
    public ServerWhiteboardSendForm(String commend, String rid, String uid, String roomtype, String informationdata, String tooltype, String drawcount, String documentcount, String documentidx) {
        this.commend = commend;
        this.rid = rid;
        this.uid = uid;
        this.roomtype = roomtype;
        this.informationdata = informationdata;
        this.tooltype = tooltype;
        this.drawcount = drawcount;
        this.documentcount = documentcount;
        this.documentidx = documentidx;
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

    public String getuid() {
        return uid;
    }
    public void setuid(String uid) { this.uid = uid; }

    public String getroomtype() {
        return roomtype;
    }
    public void setroomtype(String roomtype) {
        this.roomtype = roomtype;
    }

    public String getinformationdata() {
        return informationdata;
    }
    public void setinformationdata(String informationdata) {
        this.informationdata = informationdata;
    }


    public String gettooltype() {
        return tooltype;
    }
    public void settooltype(String tooltype) {
        this.tooltype = tooltype;
    }


    public String getdrawcount() {
        return drawcount;
    }
    public void setdrawcount(String drawcount) {
        this.drawcount = drawcount;
    }
    public String getdocumentcount() {
        return documentcount;
    }
    public void setdocumentcount(String documentcount) {
        this.documentcount = documentcount;
    }

    public String getdocumentidx() {
        return documentidx;
    }
    public void setdocumentidx(String documentidx) {
        this.documentidx = documentidx;
    }
}