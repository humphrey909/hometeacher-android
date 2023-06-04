package com.example.hometeacher.ArraylistForm;

public class MikechkForm {

    private int rtcuid;
    private boolean muted;

    public MikechkForm(int rtcuid, boolean muted) {
        this.rtcuid = rtcuid;
        this.muted = muted;
    }
    public int getrtcuid() {
        return rtcuid;
    }
    public void setrtcuid(int rtcuid) {
        this.rtcuid = rtcuid;
    }
    public boolean getmuted() {
        return muted;
    }

    public void setmuted(boolean muted) {
        this.muted = muted;
    }
}