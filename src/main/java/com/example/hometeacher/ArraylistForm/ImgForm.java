package com.example.hometeacher.ArraylistForm;

import android.net.Uri;

public class ImgForm {

    private Uri uri;
    private String imgidx;
    private boolean isSelected;

    public ImgForm(Uri uri, String imgidx, boolean isSelected) { // isSelected : true = 서버에 저장할 데이터, false = 서버에서 가져온 데이터
        this.uri = uri;
        this.imgidx = imgidx;
        this.isSelected = isSelected;
    }
    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
    public String getImgidx() {
        return imgidx;
    }

    public void setImgidx(String imgidx) {
        this.imgidx = imgidx;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}