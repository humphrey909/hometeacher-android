package com.example.hometeacher.shared;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NboardviewsShared extends SharedModel implements Serializable {
   // ArrayList<ArrayList<String>> AccountList = new ArrayList<ArrayList<String>>();

    //고유번호, date, price, who, explain, subject_kindtab, subject_kind, paymentmethodtab, paymentmethod

    String dbname = "";

    public NboardviewsShared(Context mContext) {
        this.oContext = mContext; //부모클래스에 전달

        dbname = "NboardViews"; //db name 지정

        //커플고유키, 날짜, 가격, 인물 카테고리 키, 내역, 사용카테고리 키, 결재 카테고리 키, 등록 시간
        Field = new String[]{
                "nid", "regtime"
        };
    }

    //몇번째 필드인지 알려줌
    public int GetFildnum(String Fieldname){
        int Fieldnum = 0;
        for (int i = 0; i<Field.length; i++){
            if(Field[i].equals(Fieldname)){
                Fieldnum = i;
            }
        }
        return Fieldnum;
    }

    //
    //가계부 객체를 등록한다.
    //public String SaveAccountobject(String Couplekey, int selectyear, int selectmonth, int selectday, String selectdayofweek, int price, String who, String explain, String Usekindstab, String Usekinds, String Paymentkindstab, String Paymentkinds){
    public String SaveViewsobject(String nid){
        //Log.d("uid--------------------------------!", uid);

        ArrayList<String> accountdata = new ArrayList<>();
        accountdata.add(nid);
//        String Selectdate = selectyear+"/"+selectmonth+"/"+selectday+"/"+selectdayofweek; //날짜 병합
//        accountdata.add(Selectdate); //날짜
//        accountdata.add(Integer.toString(price)); //가격
//        accountdata.add(who); //누가
//        accountdata.add(explain);
//        accountdata.add(usecategorykey);
//        accountdata.add(paymentcategorykey);
//
        //리스트 전체의 마지막에 오늘 시간 넣어줌
        java.text.SimpleDateFormat mFormat = new java.text.SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date mDate = new Date();
        String todaytime = mFormat.format(mDate);
        Log.d("로그인 액티비티 오늘 날짜!", todaytime);

        accountdata.add(todaytime); //최신 시간 넣을것
//
//        accountdata.add(placename); //장소 이름
//        accountdata.add(place_x); //장소 위치
//        accountdata.add(place_y); //장소 위치

        ArrayList<ArrayList<ArrayList<String>>> Accountlist = DoReadAll(dbname);

        int maxkey = 0;
        //값이 아무것도 없을때 처리를 해줘야돼.
        if(Accountlist.isEmpty()) {
            maxkey = Accountlist.size();
        }else{
            //키값을 만들어야한다. 전체 리스트를 가져와서 가장 큰 숫자를 찾고 +1을 한다.
            ArrayList<Integer> sortdata = new ArrayList<>();
            for (int i = 0; i < Accountlist.size(); i++){
                String eachkey = Accountlist.get(i).get(0).get(0);
                sortdata.add(Integer.parseInt(eachkey));
            }
            maxkey = Collections.max(sortdata)+1; //키값으로 쓰임
        }


        //Log.d("alllist list sort", String.valueOf(sortdata));
        //Log.d("alllist list sort2", String.valueOf(maxkey));

        String key = String.valueOf(maxkey);

        Log.d("accountdata", String.valueOf(accountdata));
        String Accountkey = DoAdd_json(dbname, key, accountdata); //저장하고 그 key값을 던져줌

        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("TAG", nid+"저장완료");
        return Accountkey;
    }


    public String getDateDay(String date, String dateType) throws Exception {

        String day = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        return day;
    }



    //데이터 전부 가져오기
    public ArrayList<ArrayList<ArrayList<String>>> AccountRead(){

        ArrayList<ArrayList<ArrayList<String>>> Accountlist = DoReadAll(dbname);

        return Accountlist;
    }
    //데이터 하나 가져오기
    public  ArrayList<ArrayList<String>> Getoneinfo(String key){

        ArrayList<ArrayList<String>> result = DoReadOne(dbname, key);
        return result;
    }

    //db초기화
    public int Init(){
        //User라는 데이터베이스에 값을 추가할 것. Userdb가 없으면 만들고 putString을 하고, 있으면 찾아서 putString 할것
        //찾을 db 이름, 저장할 데이터들,

        int Accountlist = DoDeleteAll(dbname);

        return Accountlist;
    }

    //하나만 삭제
    public int Deleteone(String key){
        String[] keyarr = {key};
        DoDeleteArray(dbname, keyarr);

        return 1;
    }

    //해당 데이터를 수정한다.
    //
    public String SetAccountEdit(String Accountkey ,String Couplekey, String date, String who, int price, String explain, String usecategorykey, String paymentcategorykey, String placename, String place_x, String place_y){
        Log.d("가계부 고유 키", String.valueOf(Accountkey)); //데이터 변경 전값을 가져와야함
        Log.d("커플 키", Couplekey); //데이터 변경 전값을 가져와야함
        Log.d("변경된 날짜", date); //데이터 변경 전값을 가져와야함
        Log.d("변경된 구매인", who); //데이터 변경 전값을 가져와야함
        Log.d("변경된 가격", String.valueOf(price)); //데이터 변경 전값을 가져와야함
        Log.d("변경된 설명", String.valueOf(explain)); //그대로 저장가능

        Log.d("사용 카테고리 키", usecategorykey); //데이터 변경 전값을 가져와야함
        Log.d("결재 카테고리 키", paymentcategorykey); //데이터 변경 전값을 가져와야함
        Log.d("placename", placename); //데이터 변경 전값을 가져와야함
        Log.d("place_x", place_x); //데이터 변경 전값을 가져와야함
        Log.d("place_y", place_y); //데이터 변경 전값을 가져와야함

        //리스트 전체의 마지막에 오늘 시간 넣어줌
        java.text.SimpleDateFormat mFormat = new java.text.SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date mDate = new Date();
        String todaytime = mFormat.format(mDate);
        Log.d("로그인 액티비티 오늘 날짜!", todaytime);

        ArrayList<String> accountdata = new ArrayList<>();
        accountdata.add(Couplekey); //커플 고유번호
        accountdata.add(date); //날짜
        accountdata.add(Integer.toString(price)); //가격
        accountdata.add(who); //누가
        accountdata.add(explain);
        accountdata.add(usecategorykey);
        accountdata.add(paymentcategorykey);
        accountdata.add(todaytime);
        accountdata.add(placename);
        accountdata.add(place_x);
        accountdata.add(place_y);

        //키값이 이미 존재하면 덮어 씌움
        String key = Accountkey; //키는 리스트에서 가장 큰 숫자
        String GetAccountkey = DoAdd_json(dbname, key, accountdata); //저장하고 그 key값을 던져줌

        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("TAG", explain+"수정완료");


        return GetAccountkey;
    }

    //모든 데이터의 마지막에 현재 시간을 저장한다. - 강제로 넣어줄때 사용함.
    public void dataedit_addtime(String idx){

        ArrayList<ArrayList<String>> totallist = Getoneinfo(idx); //각 데이터 가져옴

        //리스트 전체의 마지막에 시간을 넣어줄 것.
        java.text.SimpleDateFormat mFormat = new java.text.SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date mDate = new Date();
        String todaytime = mFormat.format(mDate);
        Log.d("로그인 액티비티 오늘 날짜!", todaytime);

        Log.d("각 데이터 정보", String.valueOf(totallist));
        ArrayList<String> accountdata = new ArrayList<>();
        for (int i = 0; i < totallist.get(1).size(); i++){
            //Log.d("데이터 내용들", String.valueOf(totallist.get(1).get(i)));

            if(i == 7) {
                accountdata.add(todaytime); //각 정보들을 넣어줌
            }else if(i == 6){
                accountdata.add("-");
            }else{
                accountdata.add(totallist.get(1).get(i)); //각 정보들을 넣어줌
            }
        }
        //accountdata.add(todaytime); //마지막 시간 넣어줌

        Log.d("바뀐 데이터 정보", String.valueOf(accountdata));


        String key = String.valueOf(idx);
        String Accountkey = DoAdd_json(dbname, key, accountdata); //저장하고 그 key값을 던져줌
        /*
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE); //db 공간 만듬, 해당 db 불러옴

        //json형태로 만들어서 저장
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        String[] Field_ = new String[]{
                "couplekey", "date", "price", " who", "explain", "usecategorykey", "paymentcategorykey", "regtime"
        };
        for (int i = 0; i < Field_.length; i++){
            jsonObject.addProperty(Field_[i], accountdata.get(i)); //추가할 리스트를 jsonobject에 key:value형태로 넣음. 그리고 jsonarray로 감싼다.
        }


        jsonArray.add(jsonObject);
        String datajson = new Gson().toJson(jsonArray); //json 형태로 바꿔서 저장한다.

        //내부저장소에 저장하기
        editor = sharedPreferences.edit(); //해당 공간에 편집을 하겠다고 선언

        if (!accountdata.isEmpty()) {
            editor.putString(key, datajson);
        } else {
            editor.putString(key, null);
        }

        editor.apply(); //저장
         */

    }


    //하나의 데이터 날짜시간을 변경
    public void dataedit_addtimeone(String idx, String fildname, String datetime){

        int fild = GetFildnum(fildname);
        DoEditOne(dbname, idx, fild, datetime);
/*
        ArrayList<ArrayList<String>> totallist = Getoneinfo(idx); //각 데이터 가져옴

        Log.d("각 데이터 정보", String.valueOf(totallist));
        ArrayList<String> accountdata = new ArrayList<>();
        for (int i = 0; i < totallist.get(1).size(); i++){
            //Log.d("데이터 내용들", String.valueOf(totallist.get(1).get(i)));

            if(i == 7) {
                accountdata.add(datetime); //각 정보들을 넣어줌
            }else{
                accountdata.add(totallist.get(1).get(i)); //각 정보들을 넣어줌
            }
        }

        //키값이 이미 존재하면 덮어 씌움
        String key = idx; //키는 리스트에서 가장 큰 숫자
        String GetAccountkey = DoAdd_json(dbname, key, accountdata); //저장하고 그 key값을 던져줌
*/
        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("TAG", idx+"수정완료"+datetime);
    }
}
