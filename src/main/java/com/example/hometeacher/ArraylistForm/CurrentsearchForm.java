package com.example.hometeacher.ArraylistForm;

//데이터를 저장하고 comparator에 던져줄때 사용.
public class CurrentsearchForm {
    public String key;
    public String name;
    public String regdate;
    public CurrentsearchForm(String key, String name, String regdate) {
        this.key = key;
        this.name = name;
        this.regdate = regdate;
    }
    @Override
    public String toString() {
        return "[ " + this.key + ", "+this.name+", "+ this.regdate + " ]";
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

}

