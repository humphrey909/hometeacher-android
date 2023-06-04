package com.example.hometeacher;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.CategoreytoDetailSearchRecyclerAdapter;
import com.example.hometeacher.Adapter.MyclassSelectImgAdapter;
import com.example.hometeacher.Adapter.MyclassUserSearchRecyclerAdapter;
import com.example.hometeacher.Adapter.SearchRecyclerAdapter;
import com.example.hometeacher.Adapter.UserRecyclerAdapter;
import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.shared.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyclassUseradd extends AppCompatActivity{
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;


    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    NestedScrollView scroll_view;
    ProgressBar progress_bar;
    AutoCompleteTextView autoCompleteTextView; //자동 검색 검색어
    TextView toolbartitle;

    int pagenum = 0;
    int limitnum = 0;

    ArrayList<JSONObject> Userlist; //회원 전체 리스트
    ArrayList<JSONObject> AddUserlist; //추가로 선택한 회원 리스트
    ArrayList<JSONObject> AddUserlist_Invite; //이미 선택된 유저 리스트
    RecyclerView UserRecyclerView, adduserRecyclerView;

    String SearchData = "";

    int RESULTCODE = 1;

    String accesstype; // 1. 방생성할때 접근, 2. 방 생성 후 접근
    String AddUserliststring; //추가된 유저를 가져옴
    String maxnum; //최대인원
    String rid_myclass; //과외방 rid

    SocketSend oSocketSend;
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_useradd);



        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        division();
    }
    @SuppressLint("SetTextI18n")
    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;
        oSocketSend = new SocketSend(GlobalClass);

        scroll_view = (NestedScrollView) findViewById(R.id.scroll_view);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        autoCompleteTextView  = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        toolbartitle  = (TextView) findViewById(R.id.toolbartitle);

        //유저가 추가될때마다 변경
        adduserRecyclerView = (RecyclerView) findViewById(R.id.adduserRecyclerView);
        adduserRecyclerView.setVisibility(View.GONE);

        //유저 arraylist 선언
        Userlist = new ArrayList<>();
        AddUserlist = new ArrayList<>();
        AddUserlist_Invite = new ArrayList<>();

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        accesstype = intent.getExtras().getString("accesstype");
        maxnum = intent.getExtras().getString("maxnum");
        AddUserliststring = intent.getExtras().getString("AddUserlist");




        // 1. 방생성할때 접근
        if(accesstype.equals("1")){

            Log.d("----AddUserliststring----",AddUserliststring);
            //추가된 유저가 있으면 추가 유저리스트를 만들어줌
            if(AddUserliststring.length() > 2){
            //    if(!AddUserliststring.equals("")){
                SetAddUserlist(AddUserliststring);
            }
            int changeaddusercount = AddUserlist_Invite.size()+AddUserlist.size();
            toolbartitle.setText("학생 초대 : "+changeaddusercount+"/" + maxnum + "명");
        }else{ //2. 방 생성 후 접근
            rid_myclass = intent.getExtras().getString("rid_myclass");
        }



        //검색 클릭
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :

                        Log.d("MainActivity", "입력 내용 : " + autoCompleteTextView.getText().toString());

                        SearchData = autoCompleteTextView.getText().toString();

                        Userlist.clear();
                        GetUserlist(2,limitnum, 0);

                }
                return true;
            }
        });


        //학생리스트 가져오기
        GetUserlist(2,limitnum, 0);
    }


    @SuppressLint("SetTextI18n")
    public void SetAddUserlist(String AddUserliststring){


        Log.d("-------AddUserliststring-------", AddUserliststring);
        try {
            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
            JSONArray jarray = new JSONArray(AddUserliststring);
            Log.d("onResponse ? ","추가된 유저 리스트 : " + String.valueOf(jarray));
            // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
            if (jarray.get(0) != null) {
                AddUserlist_Invite.clear();
               // AddUserlist.clear();

                for(int i = 0; i<jarray.length(); i++){
                    JSONObject tempJson = jarray.getJSONObject(i);
                    //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                    Log.d("onResponse ? ", "추가된 유저 리스트 : " + String.valueOf(tempJson));

                    if(accesstype.equals("1")) { //방만들때 추가
                        //jsonobject형식으로 데이터를 저장한다.
                        AddUserlist.add(tempJson);
                    }else{ //방에 들어와서 추가
                        //유저 리스트에 같은 값을 찾아서 그 유저리스트의 객체를 넣어줄 것.
                        for(int j = 0; j<Userlist.size(); j++){
                            if(String.valueOf(tempJson.get("uid")).equals(Userlist.get(j).get("idx"))){
                                AddUserlist_Invite.add(Userlist.get(j));
                            }
                        }
                    }
                }


                //object형식으로 arraylist를 만든것
                //Log.d("onResponse ? ", "onResponse 선생님 리스트 : " + String.valueOf(Userlist));

                adduserRecyclerView.setVisibility(View.VISIBLE);
                //데이터를 바로 출력시킬것
                MakeAddUserrecycle(AddUserlist);



                Log.d("json 파싱", "프로필 데이터 가져오기 성공");

            } else {
                Log.d("json 파싱", "프로필 데이터 가져오기 실패");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //유저리스트 / type = 1 선생님, type = 2 학생
    public void GetUserlist(int type, int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(type));
        requestMap.put("usertype", usertype);

        //이름 검색 SearchData
        if(!SearchData.equals("")){
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SearchData));
            requestMap.put("name", name);
        }

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getuserlist(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //레트로핏 라이브러리를 사용해 보낼 양식 만들어 놓음
    public void RestapiStart() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송

    }

    //레트로핏 데이터 응답 부분
    public void RestapiResponse() {
        //데이터 응답
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("onResponse ? ", "onResponse 성공" + response);
                    // Log.d("onResponse ? ","onResponse 성공" + response.errorBody());
                    // Log.d("onResponse ? ","onResponse 성공" + response.message());
                    // Log.d("onResponse ? ","onResponse 성공" + response.code());
                    Log.d("onResponse ? ", "onResponse 성공" + response.raw().request().url());
                    String url = response.raw().request().url().toString();
                    String urlget = url.split("/")[8];
                    String urlget2 = urlget.split("=")[1];
                    //Log.d("onResponse ? ","onResponse 성공" + Arrays.toString(urlget));
                    Log.d("onResponse ? ", "onResponse 성공" + urlget2);

                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제

                    if (urlget2.equals("1")) { //유저 리스트
                        Log.d("onResponse ? ", "유저 리스트 : " + resultlist);

                        progress_bar.setVisibility(View.GONE);

                        if(resultlist.length() > 2){ //받아온 데이터가 없으면 진행 x
                            try {
                                //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                                JSONArray jarray = new JSONArray(resultlist);
                                Log.d("onResponse ? ","onResponse 선생님 리스트 : " + String.valueOf(jarray));
                                // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                                if (jarray.get(0) != null) {

                                    for(int i = 0; i<jarray.length(); i++){
                                        JSONObject tempJson = jarray.getJSONObject(i);
                                        //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                        // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                        //jsonobject형식으로 데이터를 저장한다.
                                        Userlist.add(tempJson);
                                    }

                                    //object형식으로 arraylist를 만든것
                                    Log.d("onResponse ? ", "onResponse 선생님 리스트 : " + String.valueOf(Userlist));

                                    //데이터를 바로 출력시킬것
                                    MakeUserrecycle(Userlist);

                                    Log.d("json 파싱", "프로필 데이터 가져오기 성공");

                                } else {
                                    Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else if(urlget2.equals("2")){ //내 과외 추가된 유저 저장 및 과외문의 방 생성
                        Log.d("onResponse ? ", "내 과외 추가된 유저 저장 및 과외문의 방 생성 : " + resultlist);


                        JSONObject jobj = null;
                        try {
                            jobj = new JSONObject(resultlist);
//                            Log.d("onResponse ? ","내 과외 추가된 유저 저장  : " + String.valueOf(jobj));
//                            Log.d("onResponse ? ","내 과외 추가된 유저 저장  : " + String.valueOf(jobj.get("result")));
//                            Log.d("onResponse ? ","내 과외 추가된 유저 저장  : " + String.valueOf(jobj.get("err")));
//                            Log.d("onResponse ? ","내 과외 추가된 유저 저장  : " + String.valueOf(jobj.get("myclassroomidx")));
//                            Log.d("onResponse ? ","내 과외 추가된 유저 저장  : " + String.valueOf(jobj.get("requestroomidxarr")));

                            //과외문의하기 방에 메세지 전송하기.
//                            String msgdata = "초대하였습니다. 해당 과외에 참여하시려면 예를 눌러주세요. ";
//
//                            JSONArray jarray_rid = new JSONArray(String.valueOf(jobj.get("requestroomidxarr")));
//                            for(int i = 0; i<jarray_rid.length();i++){
//                                JSONObject jobj_rid = new JSONObject(String.valueOf(jarray_rid.get(i)));
//                                Log.d("onResponse ? ","---------requestrid==========  : " + String.valueOf(jobj_rid.get("makechk")));
//                                Log.d("onResponse ? ","---------requestrid==========  : " + String.valueOf(jobj_rid.get("requestrid")));
//
//                                if(String.valueOf(jobj_rid.get("makechk")).equals("1")){//지금 만든 방이면 소켓에서 방 만들기
//                                    Log.d("onResponse ? ","---------requestrid 1==========  : " + String.valueOf(jobj_rid.get("requestrid")));
//                                    oSocketSend.SendSocketData("CROOM", String.valueOf(jobj_rid.get("requestrid")), "2",Sessionlist.get(1).get(0), msgdata, "0","", "1", "2", String.valueOf(jobj.get("myclassroomidx")));
//                                }else if(String.valueOf(jobj_rid.get("makechk")).equals("0")){//원래 있던 방이면 그냥 전송
//                                    Log.d("onResponse ? ","---------requestrid 0==========  : " + String.valueOf(jobj_rid.get("requestrid")));
//                                    oSocketSend.SendSocketData("CHATSEND", String.valueOf(jobj_rid.get("requestrid")), "2", Sessionlist.get(1).get(0), msgdata, "0","", "1", "2", String.valueOf(jobj.get("myclassroomidx")));
//                                }
//                            }


                            //입장을 하나 만들자!!!!!
                            //초대 후에 입장이 된것을 소켓으로 요청해야한다. 이유는 초대후에 db에는 저장하지만 소켓에 보내는 부분이 없음.
                            //원래는 문의하기로 전송을하면서 연결을 해준건가?
                            //oSocketSend.SendSocketData("EROOM", String.valueOf(MyclassRoominfojsonarray.get(0).get("idx")), String.valueOf(MyclassRoominfojsonarray.get(0).get("maxnum")),Sessionlist.get(1).get(0), "방에 참여하였습니다.", "0","", roomtype, "1","0");


                            Log.d("onResponse ? ","---------requestrid 1==========  : " + String.valueOf(jobj));
                            JSONArray jarray_userlist = new JSONArray(String.valueOf(jobj.get("userlist")));
                            for(int i = 0; i<jarray_userlist.length();i++) {
                                Log.d("onResponse ? ","---------requestrid 2==========  : " + String.valueOf(jarray_userlist.get(i)));

                                oSocketSend.SendSocketData("INVITEROOM", String.valueOf(jobj.get("myclassroomidx")), "", String.valueOf(jarray_userlist.get(i)), "초대되었습니다. ", "0","", "2", "1", "0");

                            }

                           // oSocketSend.SendSocketData("EROOM", String.valueOf(jobj.get("myclassroomidx")), "", String.valueOf(list.get(position).get("uid")), popuptitle+"되었습니다. ", "0","", roomtype, "1", "0");



                            finish();

//                            Intent intent = new Intent(oContext, Myclassroomactivity.class);
//                            intent.putExtra("roommaketype", "1"); //1. 방처음 만들때 2. 만들어진 방에 들어갈때
//                            intent.putExtra("roomidx", String.valueOf(jobj.get("myclassroomidx"))); //룸 idx를 같이 보내야함.
//                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    ///유저리스트에서 if(accesstype.equals("2")){ 일때 해당된 리스트는 disabled 시킬 것. //AddUserlist

    //유저 리스트를 만든다
    public void MakeUserrecycle(ArrayList<JSONObject> Userlistjsonarray){

        if(accesstype.equals("2")){
            //추가된 유저가 있으면 추가 유저리스트를 만들어줌
            if(!AddUserliststring.equals("")){
                SetAddUserlist(AddUserliststring);
            }
            int changeaddusercount = AddUserlist_Invite.size()+AddUserlist.size();
            toolbartitle.setText("학생 초대 : "+changeaddusercount+"/" + maxnum + "명");
        }



        Log.d("유저 리스트 이다.", String.valueOf(Userlistjsonarray));

        UserRecyclerView = (RecyclerView) findViewById(R.id.UserRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        //GridLayoutManager GridlayoutManager = new GridLayoutManager(oContext, 2); //그리드 매니저 선언
        MyclassUserSearchRecyclerAdapter oMyclassUserSearchRecyclerAdapter = new MyclassUserSearchRecyclerAdapter(getApplicationContext()); //내가만든 어댑터 선언
        UserRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식


        //선택된 유저를 보낼 것. AddUserlist
        oMyclassUserSearchRecyclerAdapter.setSelectUser(AddUserlist, AddUserlist_Invite, accesstype);
        oMyclassUserSearchRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oMyclassUserSearchRecyclerAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        UserRecyclerView.setAdapter(oMyclassUserSearchRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oMyclassUserSearchRecyclerAdapter.setOnItemClickListener(new MyclassUserSearchRecyclerAdapter.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list, Boolean user_checkbox) {

               // Toast.makeText(oContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
               // Toast.makeText(oContext, String.valueOf(user_checkbox), Toast.LENGTH_SHORT).show();

                // 1. 방생성할때 접근
                if(accesstype.equals("1")) {
                    //true
                    if (user_checkbox) {

                        //선택된 유저 리스트에 추가
                        int changemaxnum = Integer.parseInt(maxnum)-AddUserlist_Invite.size();
                        if (AddUserlist.size() >= changemaxnum) {
                            MakeUserrecycle(Userlist);

                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
                            builder.setTitle("알림").setMessage("최대 인원수를 초과하였습니다.");
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            adduserRecyclerView.setVisibility(View.VISIBLE);
                            AddUserlist.add(list.get(position));

                            int changeaddusercount = AddUserlist_Invite.size()+AddUserlist.size();
                            toolbartitle.setText("학생 초대 : "+changeaddusercount+"/" + maxnum + "명");
                        }

                        // Log.d("-------AddUserlist list---------", String.valueOf(AddUserlist));
                    } else { //false
                        //선택된 리스트에 값이 있다면 삭제 한다.
                        for (int i = 0; i < AddUserlist.size(); i++) {
                            try {
                                Log.d("-------전체 item---------", String.valueOf(AddUserlist.get(i).get("idx")));
                                Log.d("-------선택된 item---------", String.valueOf(list.get(position).get("idx")));

                                if (AddUserlist.get(i).get("idx").equals(list.get(position).get("idx"))) {
                                    Log.d("-------선택된 item---------", String.valueOf(list.get(position)));
                                    AddUserlist.remove(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            int changeaddusercount = AddUserlist_Invite.size()+AddUserlist.size();
                            toolbartitle.setText("학생 초대 : "+changeaddusercount+"/" + maxnum + "명");
                        }
                    }
                    MakeAddUserrecycle(AddUserlist);
                }else if(accesstype.equals("2")){ //2. 방 생성 후 접근
                    //true
                    if (user_checkbox) {
                        //선택된 유저 리스트에 추가
                        int changemaxnum = Integer.parseInt(maxnum)-AddUserlist_Invite.size();
                        if (AddUserlist.size() >= changemaxnum) {
                            MakeUserrecycle(Userlist);
                            //에러나는 이유...!!! 처음에는 내과외 유저리스트에서 참조해서 가져온데이터였음. 추가할때마다 일반 유저리스트에 있는 값을 넣게 됨. 그래서 새로고침할시점에서 값에서 에러가 난 것이다.

                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
                            builder.setTitle("알림").setMessage("최대 인원수를 초과하였습니다.");
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            adduserRecyclerView.setVisibility(View.VISIBLE);
                            AddUserlist.add(list.get(position));

                            int changeaddusercount = AddUserlist_Invite.size()+AddUserlist.size();
                            toolbartitle.setText("학생 초대 : "+changeaddusercount+"/" + maxnum + "명");
                        }
                    }else{ //false
                        //선택된 리스트에 값이 있다면 삭제 한다.
                        for (int i = 0; i < AddUserlist.size(); i++) {
                            try {
                                Log.d("-------전체 item---------", String.valueOf(AddUserlist.get(i).get("idx")));
                                Log.d("-------선택된 item---------", String.valueOf(list.get(position).get("idx")));

                                if (AddUserlist.get(i).get("idx").equals(list.get(position).get("idx"))) {
                                    Log.d("-------선택된 item---------", String.valueOf(list.get(position)));
                                    AddUserlist.remove(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        int changeaddusercount = AddUserlist_Invite.size()+AddUserlist.size();
                        toolbartitle.setText("학생 초대 : "+changeaddusercount+"/" + maxnum + "명");
                    }
                    MakeAddUserrecycle(AddUserlist);

                    Log.d("-------전체item---------", String.valueOf(AddUserlist));
                }

            }
        });
    }

    //추가된 유저 리스트를 만든다
    public void MakeAddUserrecycle(ArrayList<JSONObject> Userlistjsonarray) {
        Log.d("유저 리스트 이다.", String.valueOf(Userlistjsonarray));

        LinearLayoutManager linearManager = new LinearLayoutManager(oContext, RecyclerView.HORIZONTAL, false); //가로일때
        MyclassSelectImgAdapter oMyclassSelectImgAdapter = new MyclassSelectImgAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        adduserRecyclerView.setLayoutManager(linearManager);

        oMyclassSelectImgAdapter.setdata(2); //유저 수정 가능
        oMyclassSelectImgAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        adduserRecyclerView.setAdapter(oMyclassSelectImgAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oMyclassSelectImgAdapter.setOnItemClickListener(new MyclassSelectImgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list) {

                Log.d("-------AddUserlist list---------", String.valueOf(AddUserlist));
                Log.d("-------list---------", String.valueOf(list));
                //Toast.makeText(oContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
                AddUserlist.remove(position);

                MakeAddUserrecycle(AddUserlist); //추가 데이터 다시 불러오기

                //해당 유저를 클릭을 하면 그유저가 전체 유저리스트에서 체크가 해제되어야함.
                MakeUserrecycle(Userlist);
            }
        });

    }

    //if(accesstype.equals("2")){

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;//
            case R.id.completebtn: //유저 선택 완료
                if(accesstype.equals("2")){ //방 생성 후 접근
                    Log.d("","메롱");
                    Log.d("",String.valueOf(AddUserlist));

                    //여기서 처리하는게 아니라 여기서 서버 전송을 통해 요청 방을 생성해주고 가져와야함.
                    if(AddUserlist.size() > 0){
                        makerequestclassroom();
                    }
                }else{ //새로 방 만들 때 접근
                    //선택된 유저리스트를 방만드는 activity로 보낸다.
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("completeval", "1");
                    resultIntent.putExtra("AddUserlist", String.valueOf(AddUserlist));
                    oActivity.setResult(RESULTCODE, resultIntent);
                    oActivity.finish();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(accesstype.equals("2")){
            menuInflater.inflate(R.menu.adduserinvitemenu, menu);
        }else{
            menuInflater.inflate(R.menu.profilewritemenu, menu);
        }

        return true;
    }


    //여기서는 과외방은 이미 생성되어있는 상태이니 요청방만 만들도록 요청한다.
    //유저리스트에 추가해야함.

    //나의 과외 방을 생성한다.
    public void makerequestclassroom() {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0))); //방 만들 사람 id
        requestMap.put("myuid", myuid);

        //과외방 rid
        RequestBody rid_myclass_ = RequestBody.create(MediaType.parse("text/plain"), rid_myclass);
        requestMap.put("rid_myclass", rid_myclass_);


        ArrayList<String> AddUserIdx = new ArrayList<>();; //선택된 회원 idx 리스트
        for(int i = 0; i<AddUserlist.size();i++){
            try {
                AddUserIdx.add(String.valueOf(AddUserlist.get(i).get("idx")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //추가된 유저가 있을때만
        if(!AddUserIdx.isEmpty()){
            RequestBody userlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(AddUserIdx));
            requestMap.put("userlist", userlist);
        }

        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.makemyclassroom_adduser(
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        //Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        //Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}