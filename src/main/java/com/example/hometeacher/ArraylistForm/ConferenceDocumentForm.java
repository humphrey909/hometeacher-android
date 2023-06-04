package com.example.hometeacher.ArraylistForm;

import android.graphics.drawable.Drawable;

import com.example.hometeacher.ThumbnailDrawCanvas;

public class ConferenceDocumentForm {

    private String idx;
    private String uid;
    private String rid;
    private String docutype;
    private String basicuri;
    private String src;
    private ThumbnailDrawCanvas oThumbnailDrawCanvas;
    private boolean isSelected; //색상 선택 여부
    private boolean isChecked; //편집모드에서 체크하여 삭제리스트에 담는 변수 true false

    public ConferenceDocumentForm(String idx, String uid, String rid, String docutype, String basicuri, String src, ThumbnailDrawCanvas oThumbnailDrawCanvas, boolean isSelected,boolean isChecked) {
        this.idx = idx;
        this.uid = uid;
        this.rid = rid;
        this.docutype = docutype;
        this.basicuri = basicuri;
        this.src = src;
        this.oThumbnailDrawCanvas = oThumbnailDrawCanvas;
        this.isSelected = isSelected;
        this.isChecked = isChecked;
    }
    public String getidx() {
        return idx;
    }

    public void setidx(String idx) {
        this.idx = idx;
    }
    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getrid() {
        return rid;
    }

    public void setrid(String rid) {
        this.rid = rid;
    }

    public String getdocutype() {
        return docutype;
    }

    public void setdocutype(String docutype) {
        this.docutype = uid;
    }

    public String getbasicuri() {
        return basicuri;
    }

    public void setbasicuri(String basicuri) {
        this.basicuri = basicuri;
    }

    public String getsrc() {
        return src;
    }

    public void setsrc(String src) {
        this.src = src;
    }


    public ThumbnailDrawCanvas getThumbnailDrawCanvas() {
        return oThumbnailDrawCanvas;
    }

    public void setThumbnailDrawCanvas(ThumbnailDrawCanvas oThumbnailDrawCanvas) {
        this.oThumbnailDrawCanvas = oThumbnailDrawCanvas;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isCheckeded() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}