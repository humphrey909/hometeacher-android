package com.example.hometeacher.shared;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class Session extends SharedModel {

    String dbname = "";
    public Session(Context mContext){
        this.oContext = mContext; //부모클래스에 전달

        dbname = "Session"; //db name 지정

        //수정시 로그인시, 회원가입후 자동로그인시 데이터를 똑같이 맞춰주어야함.
        Field = new String[]{
                "idx", "id", "usertype", "name", "nicname", "loginregdate"
        };

    }


    //카테고리 객체를 등록한다. 하나만 저장한다.
    public String Save(ArrayList<String> userdata){
        //ArrayList<String> savedata = new ArrayList<>();
        //savedata.add(user); //사용자 키


        //ArrayList<ArrayList<ArrayList<String>>> alllist = DoReadAll(dbname);
        String key = "0";
        Log.d("고유키", key);
        Log.d("저장할 데이터", String.valueOf(userdata));



        String getkey = DoAdd_json(dbname, key, userdata); //저장하고 그 key값을 던져줌
        //String getkey = "0"; //저장하고 그 key값을 던져줌

        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("SaveWhoCategory", getkey+"저장완료");
        return getkey;
    }
    //데이터 전부 가져오기
    public ArrayList<ArrayList<ArrayList<String>>> AlllistRead(){

        ArrayList<ArrayList<ArrayList<String>>> Accountlist = DoReadAll(dbname);

        return Accountlist;
    }
    //데이터 하나 가져오기
    public  ArrayList<ArrayList<String>> Getoneinfo(String key){

        // result = DoReadOne(dbname, key);
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


    public int EditSession(String idx, int field, String data){

        int result = DoEditOne(dbname, idx, field, data); //저장하고 그 key값을 던져줌

        Log.d("TAG", result+"수정완료");

        return result;
    }


    //해당 데이터를 수정한다.
    public String Edit(String idxkey, String Couplekey, String usertab, String kindsstab, String name){
        Log.d("커플 키", Couplekey); //데이터 변경 전값을 가져와야함
        Log.d("인물 카테고리 키", String.valueOf(idxkey)); //데이터 변경 전값을 가져와야함

        ArrayList<String> changedata = new ArrayList<>();
        changedata.add(Couplekey); //커플 고유번호
        changedata.add(usertab); //인물 카테고리키
        changedata.add(kindsstab); //지불카테고리 키
        changedata.add(name); //이름

        //키값이 이미 존재하면 덮어 씌움
        String key = idxkey; //키는 리스트에서 가장 큰 숫자
        String GetWhokey = DoAdd_json(dbname, key, changedata); //저장하고 그 key값을 던져줌

        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("TAG", GetWhokey+"수정완료");


        return GetWhokey;
    }
}
