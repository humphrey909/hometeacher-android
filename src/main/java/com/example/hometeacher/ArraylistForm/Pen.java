package com.example.hometeacher.ArraylistForm;

public class Pen {

    public static final int STATE_START = 0;        //펜의 상태(움직임 시작)
    public static final int STATE_MOVE = 1;         //펜의 상태(움직이는 중)
    String tools;                              //도구
    float x, y, radius;                                     //펜의 좌표
    int moveStatus;                                 //현재 움직임 여부
    int color;                                      //펜 색
    int size;                                       //펜 두께
    int drawcount;                                 //그려진 순서
    int DocumentPage;                                 //그려진 페이지 번호
    String DocumentIdx;                              //문서의 고유번호
    String writeuid;                              //문서 작성자


    public Pen(String tools, float x, float y, float radius, int moveStatus, int color, int size, int drawcount, int DocumentPage, String DocumentIdx, String writeuid) {
        this.tools = tools;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.moveStatus = moveStatus;
        this.color = color;
        this.size = size;
        this.drawcount = drawcount;
        this.DocumentPage = DocumentPage;
        this.DocumentIdx = DocumentIdx;
        this.writeuid = writeuid;

    }

    /**
     * jhChoi - 201124
     * 현재 pen의 상태가 움직이는 상태인지 반환합니다.
     */
    public boolean isMove() {
        return moveStatus == STATE_MOVE;
    }
    public boolean isStart() {
        return moveStatus == STATE_START;
    }

    public String gettools() {
        return tools;
    }
    public void settools(String tools) {
        this.tools = tools;
    }


    public float getx() {
        return x;
    }
    public void setx(float x) {
        this.x = x;
    }
    public float gety() {
        return y;
    }
    public void sety(float y) {
        this.y = y;
    }
    public float getradius() {
        return radius;
    }
    public void setradius(float radius) {
        this.radius = radius;
    }
    public int getmoveStatus() {
        return moveStatus;
    }
    public void setmoveStatus(int moveStatus) {
        this.moveStatus = moveStatus;
    }
    public int getcolor() {
        return color;
    }
    public void setcolor(int color) {
        this.color = color;
    }
    public int getsize() {
        return size;
    }
    public void setsize(int size) {
        this.size = size;
    }
    public int getdrawcount() {
        return drawcount;
    }
    public void setdrawcount(int drawcount) {
        this.drawcount = drawcount;
    }
    public int getDocumentPage() {
        return DocumentPage;
    }
    public void setDocumentPage(int DocumentPage) {
        this.DocumentPage = DocumentPage;
    }

    public String getDocumentIdx() {
        return DocumentIdx;
    }
    public void setDocumentIdx(String DocumentIdx) {
        this.DocumentIdx = DocumentIdx;
    }

    public String getwriteuid() {
        return writeuid;
    }
    public void setwriteuid(String writeuid) {
        this.writeuid = writeuid;
    }
}