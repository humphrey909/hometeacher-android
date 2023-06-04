package com.example.hometeacher.ArraylistForm;

public class conferenceUserForm {

    private int rtcuid; //참여시 agora에서 주는 고유값
    private String uid;
    private String roomidx;
    private int lifetype;
    private String name;
    private String usertype;
    private String profileimg;
    private Boolean camerachk;
    private Boolean mikechk;
    private int screenlocation;


    //이름 타입 프로필이미지 참여여부(lifetype), 마이크여부, 카메라여부
    public conferenceUserForm(String uid, String roomidx, int rtcuid, int lifetype, String name, String usertype, String profileimg, Boolean camerachk, Boolean mikechk, int screenlocation) {
        this.uid = uid;
        this.roomidx = roomidx;
        this.rtcuid = rtcuid;
        this.lifetype = lifetype;
        this.name = name;
        this.usertype = usertype;
        this.profileimg = profileimg;
        this.camerachk = camerachk;
        this.mikechk = mikechk;
        this.screenlocation = screenlocation;
    }
    public String getuid() {
        return uid;
    }
    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getroomidx() {
        return roomidx;
    }

    public void setroomidx(String roomidx) {
        this.roomidx = roomidx;
    }

    public int getrtcuid() {
        return rtcuid;
    }

    public void setrtcuid(int rtcuid) {
        this.rtcuid = rtcuid;
    }


    public int getlifetype() {
        return lifetype;
    }

    public void setlifetype(int lifemod) {
        this.lifetype = lifetype;
    }


    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }


    public String getusertype() {
        return usertype;
    }

    public void setusertype(String usertype) {
        this.usertype = usertype;
    }

    public String getprofileimg() {
        return profileimg;
    }

    public void setprofileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public Boolean getcamerachk() {
        return camerachk;
    }

    public void setcamerachk(Boolean camerachk) {
        this.camerachk = camerachk;
    }

    public Boolean getmikechk() {
        return mikechk;
    }

    public void setmikechk(Boolean mikechk) {
        this.mikechk = mikechk;
    }

    public int getscreenlocation() {
        return screenlocation;
    }

    public void setscreenlocation(int screenlocation) {
        this.screenlocation = screenlocation;
    }
}