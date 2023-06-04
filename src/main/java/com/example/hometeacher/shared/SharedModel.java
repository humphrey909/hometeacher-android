package com.example.hometeacher.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/*
*
*
* */
public class SharedModel {

    Context oContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String[] Field;

    SharedModel(){
    }

    //추가는 키가 중복되지 않게 넣어야하고
    //기존 db의 마지막 데이터의 고유값이 필요.
    //값을 어떻게 넣어줄건데?
    //1. 한key에 한 회원이라고 했을때 value에 회원에 대한 여러개의 정보들을 ,로 구분해서 넣어준다.
    public String DoAdd(String dbname, String key, String[] data){
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE); //db 공간 만듬
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(oContext);
        Map<String, ?> List = sharedPreferences.getAll();
        //Log.d("add List", String.valueOf(List));
        //Log.d("add List", String.valueOf(List.size()));

        //데이터 가공
        String Memberdata = "";
        for(int i=0; i<data.length; i++){
            Memberdata += data[i]+",";
        }
        Memberdata = Memberdata.substring(0, Memberdata.length()-1); // 콤마 자르기
        //Log.d("data make", Memberdata);

        //내부저장소에 저장하기
        editor = sharedPreferences.edit(); //해당 공간에 편집을 하겠다고 선언
        //editor.putString(String.valueOf(List.size()+1), Memberdata);
        editor.putString(key, Memberdata);
        editor.apply(); //저장

        return key; //저장후 해당 키를 던져줄 것.
    }

    //json을 이용한 추가하기
    //데이터베이스 이름, 변경할 키 이름, 변경할 리스트 데이터
    public String DoAdd_json(String dbname, String key, ArrayList<String> data) {
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE); //db 공간 만듬, 해당 db 불러옴
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(oContext);
       // Map<String, ?> List = sharedPreferences.getAll();
        //Log.d("add List", String.valueOf(List));
        //Log.d("add List", String.valueOf(List.size()));

        //json형태로 만들어서 저장
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        for (int i = 0; i < Field.length; i++){
            jsonObject.addProperty(Field[i], data.get(i)); //추가할 리스트를 jsonobject에 key:value형태로 넣음. 그리고 jsonarray로 감싼다.
        }

       // jsonObject.addProperty("pw", data.get(0));
       // jsonObject.addProperty("name", data.get(1));
       // jsonObject.addProperty("couplecode", data.get(2));
       // jsonObject.addProperty("couplechk", data.get(3));


        jsonArray.add(jsonObject);

        //gson = new Gson();
        // JsonObject를 Json 문자열로 변환
        String datajson = new Gson().toJson(jsonArray); //json 형태로 바꿔서 저장한다.
        Log.d("DoAdd_json value",datajson);

        //내부저장소에 저장하기
        editor = sharedPreferences.edit(); //해당 공간에 편집을 하겠다고 선언

        if (!data.isEmpty()) {
            editor.putString(key, datajson);
        } else {
            editor.putString(key, null);
        }

        editor.apply(); //저장

        return key; //저장후 해당 키를 던져줄 것.
    }


    //수정은 해당키를 지정해서 값을 변경해주는 것.
    public int DoEdit(String dbname, String[] data){
        //내부저장소에 저장하기
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE); //db 공간 만듬
        editor = sharedPreferences.edit(); //해당 공간에 편집을 하겠다고 선언

        //Joindata for문으로 put 하기
        for(int i=0; i<data.length; i++){
            editor.putString(String.valueOf(i), data[i]);
        }

        editor.apply(); //저장
        return 1;
    }

    //해당 키값, 필드 순서를 정해서 수정한다.
    //데이터베이스 이름, 키 이름, 변경할 필드 번호, 변경할 데이터
    public int DoEditOne(String dbname, String key, int fieldnum, String data){
        ArrayList<ArrayList<String>> origindata = DoReadOne(dbname, key);

        //Log.d("----------origindata-----------", String.valueOf(origindata));
        origindata.get(1).set(fieldnum, data);

        //바뀐 arraylist 생성
        ArrayList<String> Changedata = origindata.get(1);

        //json형태로 만들어서 저장
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        for (int i = 0; i < Field.length; i++){
            jsonObject.addProperty(Field[i], Changedata.get(i)); //변경될 리스트를 jsonobject에 key:value형태로 넣음. 그리고 jsonarray로 감싼다.
        }
        //jsonObject.addProperty("pw", Changedata.get(0));
        //jsonObject.addProperty("name", Changedata.get(1));
        //jsonObject.addProperty("couplecode", Changedata.get(2));
        //jsonObject.addProperty("couplechk", Changedata.get(3));
        jsonArray.add(jsonObject);

        //gson = new Gson();
        // JsonObject를 Json 문자열로 변환
        String datajson = new Gson().toJson(jsonArray); //json 형태로 바꿔서 저장한다.
        Log.d("DoAdd_json value",datajson);

        //내부저장소에 저장하기
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE); //db 공간 만듬
        editor = sharedPreferences.edit(); //해당 공간에 편집을 하겠다고 선언

        //editor.putString(key, Changedata.toString());
        if (!data.isEmpty()) {
            editor.putString(key, datajson);
        } else {
            editor.putString(key, null);
        }

        editor.apply(); //저장
        return 1;
    }

    //모든 값 가져오기
    //데이터베이스 이름에 해당된 데이터를 전부 가져옴. arraylist로 변환
    //예) D/arraylist 전체 내용: [[[root@naver.com], [7777, k1, 261845, 0]], [[root3@naver.com], [7777, k2, 391845, 0]], [[root2@naver.com], [7777, k2, 876310, 0]]]
    public ArrayList<ArrayList<ArrayList<String>>> DoReadAll(String dbname){
        //sharedPreferences= PreferenceManager.getDefaultSharedPreferences(oContext);
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE); //해당 db불러옴
        Map<String, ?> data = sharedPreferences.getAll(); //전체 값 가져옴

        ArrayList<ArrayList<ArrayList<String>>> userlist = new ArrayList<>();

        //Log.d("총 갯수?????", String.valueOf(data.size()));
        //Log.d("총 갯수?????", String.valueOf(data));
        Set<String> keySet = data.keySet();
        for (String key : keySet) { //유저 수

            String Val = (String) data.get(key);

            ArrayList<ArrayList<String>> usereach = new ArrayList<>(); //키 값 리스트를 하나의 리스트로 묶음
            ArrayList<String> keyarr = new ArrayList<String>(); //키를 리스트로 만듬
            ArrayList<String> valarr = new ArrayList<String>(); //값을 리스트로 만듬

            try {
                JSONArray jsonArray = new JSONArray(Val);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                keyarr.add(key);

                for (int i = 0; i < Field.length; i++){
                    valarr.add(jsonObject.getString(Field[i])); //json의 key값으로 value를 불러와서 list에 넣음
                }

                usereach.add(keyarr);
                usereach.add(valarr);

                userlist.add(usereach);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Log.d("Shard 전체 리스트", String.valueOf(userlist));

        return userlist;
    }

    //하나의 값 가져오기
    //데이터베이스 이름, 키 이름으로 해당된 데이터를 가져옴. arraylist로 변환
    //예) D/선택한 데이터: [[root@naver.com], [7777, k1, 261845, 0]]
    public ArrayList<ArrayList<String>> DoReadOne(String dbname, String key){
        //sharedPreferences= PreferenceManager.getDefaultSharedPreferences(oContext);
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(key, "-1");

        ArrayList<ArrayList<String>> userlist = new ArrayList<>();

        ArrayList<String> keyarr = new ArrayList<String>();
        ArrayList<String> valarr = new ArrayList<String>();
        try {
            assert data != null;
            if(!data.equals("-1")) {

                JSONArray jsonArray = new JSONArray(data);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                keyarr.add(key);

                for (int i = 0; i < Field.length; i++) {
                    //jsonObject.addProperty(Field[i], Changedata.get(i));
                    valarr.add(jsonObject.getString(Field[i])); //json의 key값으로 value를 불러와서 list에 넣음
                }
                userlist.add(keyarr);
                userlist.add(valarr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d(key, String.valueOf(userlist));
        //Log.d("Shard 선택한 데이터", String.valueOf(userlist));
        return userlist;
    }


    //전부 삭제?

    //key 지정 삭제 - 여러개
    public int DoDeleteArray(String dbname, String[] data){

        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //data for문
        for(int i=0; i<data.length; i++){
            editor.remove(data[i]);
        }

        editor.apply();

        return 0;
    }

    //db 내용 전부 삭제
    public int DoDeleteAll(String dbname){
        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return 1;
    }
    //db 내용 전부 삭제
    /*
    public int DoDeleteAll2(String dbname){
        sharedPreferences = oContext.getPreferences(oContext);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return 0;
    }*/

/*
    //데이터 삭제 - 한개
    public int DoDeleteOne(String dbname, String data){

        sharedPreferences = oContext.getSharedPreferences(dbname, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(data);
        editor.apply();

        return 0;
    }

 */

}
