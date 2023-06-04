package com.example.hometeacher.ArraylistForm;

import android.net.Uri;

//내부 저장소에 저장된 파일을 지우기 위해 innerFilename을 추가해주었다.
public class ImgFormMulti_2 {

    private Uri outputuri;
    private Uri copyuri;
    private String imgidx;
    private String innerFilename;
    private boolean isSelected;

    public ImgFormMulti_2(Uri outputuri, Uri copyuri, String imgidx, String innerFilename, boolean isSelected) { // isSelected : true = 서버에 저장할 데이터, false = 서버에서 가져온 데이터
        this.outputuri = outputuri;
        this.copyuri = copyuri;
        this.imgidx = imgidx;
        this.innerFilename = innerFilename;
        this.isSelected = isSelected;
    }
    public Uri getoutputUri() {
        return outputuri;
    }

    public void setoutputUri(Uri outputuri) {
        this.outputuri = outputuri;
    }

    public Uri getcopyUri() {
        return copyuri;
    }

    public void setcopyUri(Uri outputuri) {
        this.copyuri = copyuri;
    }




    public String getImgidx() {
        return imgidx;
    }

    public void setImgidx(String imgidx) {
        this.imgidx = imgidx;
    }

    public String getinnerFilename() {
        return innerFilename;
    }

    public void setinnerFilename(String innerFilename) {
        this.innerFilename = innerFilename;
    }



    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}