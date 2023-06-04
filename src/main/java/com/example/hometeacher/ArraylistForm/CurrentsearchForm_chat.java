package com.example.hometeacher.ArraylistForm;

//보류
//데이터를 저장하고 comparator에 던져줄때 사용.
public class CurrentsearchForm_chat {
    public String key;
    public String name;
    public String currentmsgregdate;
    public CurrentsearchForm_chat(String key, String name, String currentmsgregdate) {
        this.key = key;
        this.name = name;
        this.currentmsgregdate = currentmsgregdate;
    }
    @Override
    public String toString() {
        return "[ " + this.key + ", "+this.name+", "+ this.currentmsgregdate + " ]";
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



    public String getcurrentmsgregdate() {
        return currentmsgregdate;
    }

    public void setcurrentmsgregdate(String regdate) {
        this.currentmsgregdate = currentmsgregdate;
    }

}

