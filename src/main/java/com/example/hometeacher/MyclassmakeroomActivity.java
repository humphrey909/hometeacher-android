package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.MyclassSelectImgAdapter;
import com.example.hometeacher.Profile.Profilewrite;
import com.example.hometeacher.shared.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyclassmakeroomActivity extends AppCompatActivity {
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    Button studentaddbtn, makeroombtn;
    RecyclerView adduserRecyclerView;
    EditText nameedit;
    EditText maxnumedit, paymentedit;
    LinearLayout payalertlinear;

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    ArrayList<JSONObject> AddUserlist; //선택된 회원 리스트
    ArrayList<String> AddUserIdx; //선택된 회원 idx 리스트

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SocketSend oSocketSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclassmakeroom);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기


        division();


    }

    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;

        oSocketSend = new SocketSend(GlobalClass);

        adduserRecyclerView = (RecyclerView) findViewById(R.id.adduserRecyclerView);
        nameedit = (EditText) findViewById(R.id.nameedit);
        maxnumedit = (EditText) findViewById(R.id.maxnumedit);
        paymentedit = (EditText) findViewById(R.id.paymentedit);
        payalertlinear = (LinearLayout) findViewById(R.id.payalertlinear);

        AddUserlist = new ArrayList<>();
        AddUserIdx = new ArrayList<>();


        studentaddbtn = (Button) this.findViewById(R.id.studentaddbtn);
        //유저 추가 페이지로 이동
        studentaddbtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                //최대 인원이 입력해야 넘어가도록 할 것.
                if(!String.valueOf(maxnumedit.getText()).equals("")) {
                    if(String.valueOf(maxnumedit.getText()).equals("0")) { //최대인원이 0이면
                        AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
                        builder.setTitle("알림").setMessage("0보다 큰 인원을 입력해주시기 바랍니다. ");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }else{
                        //프로필 등록이 안되어 있으면 추가로 보내.
                        Intent intent = new Intent(oContext, MyclassUseradd.class);
                        intent.putExtra("accesstype", "1"); // 1. 방생성할때 접근, 2. 방 생성 후 접근
                        intent.putExtra("AddUserlist", String.valueOf(AddUserlist));
                        intent.putExtra("maxnum", String.valueOf(maxnumedit.getText()));

                        startActivityForResult(intent, REQUESTCODE);
                    }
                }else{ //최대인원이 입력되어 있지 않을 때
                    AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
                    builder.setTitle("알림").setMessage("최대 인원을 먼저 입력해주세요. ");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        makeroombtn = (Button) this.findViewById(R.id.makeroombtn);
        //방 생성 버튼
        makeroombtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                //조건 만들기
                //1. 방제목, 2. 최대 인원, 3. 학생 추가
                if(!String.valueOf(nameedit.getText()).equals("") && !String.valueOf(maxnumedit.getText()).equals("") && !String.valueOf(paymentedit.getText()).equals("")){

                    makemyclassroom();
                }
            }
        });
    }






    @Override
    //편집 클릭 후 리턴되는 값을 인지한다.
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULTCODE1) { //편집 클릭후 완료인지 확인하는 부분

                AddUserlist.clear(); //초기화
                AddUserIdx.clear(); //초기화

                String completeval = resultIntent.getStringExtra("completeval");
                String AddUserliststring = resultIntent.getStringExtra("AddUserlist");

                Log.d("완료여부 ", completeval);
                Log.d("---- 추가된 유저 리스트 ---- ", AddUserliststring);
                if(completeval.equals("1")){ //편집 수정을 했을때만 서버 통신으로 다시 불러옴
                    try {
                        //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                        JSONArray jarray = new JSONArray(AddUserliststring);
                        Log.d("onResponse ? ","추가된 유저 리스트 : " + String.valueOf(jarray));
                        // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                        if (jarray.get(0) != null) {

                            for(int i = 0; i<jarray.length(); i++){
                                JSONObject tempJson = jarray.getJSONObject(i);
                                //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                Log.d("onResponse ? ", "추가된 유저 리스트 : " + String.valueOf(tempJson));
                                Log.d("onResponse ? ", "추가된 유저 리스트 : " + String.valueOf(tempJson.get("idx")));

                                //jsonobject형식으로 데이터를 저장한다.
                                AddUserlist.add(tempJson);

                                //저장하기 쉽게 선택된 유저 idx만 묶을 것.
                                AddUserIdx.add(String.valueOf(tempJson.get("idx")));
                            }

                            //object형식으로 arraylist를 만든것
                            //Log.d("onResponse ? ", "onResponse 선생님 리스트 : " + String.valueOf(Userlist));

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
            }
        }
    }
    //추가된 유저 리스트를 만든다
    public void MakeAddUserrecycle(ArrayList<JSONObject> Userlistjsonarray) {
        Log.d("선택된 유저 리스트 이다.", String.valueOf(Userlistjsonarray));

        LinearLayoutManager linearManager = new LinearLayoutManager(oContext, RecyclerView.HORIZONTAL, false); //가로일때
        MyclassSelectImgAdapter oMyclassSelectImgAdapter = new MyclassSelectImgAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        adduserRecyclerView.setLayoutManager(linearManager);

        oMyclassSelectImgAdapter.setdata(1); //보여주기만 가능
        oMyclassSelectImgAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        adduserRecyclerView.setAdapter(oMyclassSelectImgAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
//        oMyclassSelectImgAdapter.setOnItemClickListener(new MyclassSelectImgAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position, ArrayList<JSONObject> list) {
//
//                Log.d("-------AddUserlist list---------", String.valueOf(AddUserlist));
//                Log.d("-------list---------", String.valueOf(list));
//                Toast.makeText(oContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
//                AddUserlist.remove(position);
//
//                MakeAddUserrecycle(AddUserlist);
//
//                //해당 유저를 클릭을 하면 그유저가 전체 유저리스트에서 체크가 해제되어야함.
//                MakeUserrecycle(Userlist);
//            }
//        });

    }



    //방을 생성하면서 해당 인원들에게 초대 메시지를 전송해야한다. 초대링크 전달하기. 문의방도 하나씩 만들어야겠네.

    //나의 과외 방을 생성한다.
    public void makemyclassroom() {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0))); //방 만들 사람 id
        requestMap.put("myuid", myuid);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nameedit.getText()));
        requestMap.put("name", name);

        int finalmaxnum = Integer.parseInt(String.valueOf(maxnumedit.getText())) + 1; //방만드는 선생님 인원까지 추가하여서 등록
        RequestBody maxnum = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(finalmaxnum));
        requestMap.put("maxnum", maxnum);

        //paymentedit 결제금액 저장
        int finalpayment = Integer.parseInt(String.valueOf(paymentedit.getText())); //결제금액
        RequestBody payment = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(finalpayment));
        requestMap.put("payment", payment);

        //추가된 유저가 있을때만
        if(!AddUserIdx.isEmpty()){
            RequestBody userlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(AddUserIdx));
            requestMap.put("userlist", userlist);
        }

        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        RequestBody backcolor_ = RequestBody.create(MediaType.parse("text/plain"), "#b2c7d6");
        requestMap.put("backcolor", backcolor_);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.makemyclassroom(
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

                    if (urlget2.equals("1")) { //방 생성하기
                        Log.d("onResponse ? ", "방 생성하기 : " + resultlist);

                        JSONObject jobj = null;
                        try {
                            jobj = new JSONObject(resultlist);
                           // Log.d("onResponse ? ","onResponse 내 과외 방 생성  : " + String.valueOf(jobj));
                           // Log.d("onResponse ? ","onResponse 내 과외 방 생성  : " + String.valueOf(jobj.get("result")));
                           // Log.d("onResponse ? ","onResponse 내 과외 방 생성  : " + String.valueOf(jobj.get("err")));
                           // Log.d("onResponse ? ","onResponse 내 과외 방 생성  : " + String.valueOf(jobj.get("myclassroomidx")));
                           // Log.d("onResponse ? ","onResponse 내 과외 방 생성  : " + String.valueOf(jobj.get("requestroomidxarr")));

                            //과외문의하기 방에 메세지 전송하기.
                            //String msgdata = "초대하였습니다. 해당 과외에 참여하시려면 예를 눌러주세요. ";

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

                            finish();

                            Intent intent = new Intent(oContext, Myclassroomactivity.class);
                            intent.putExtra("roommaketype", "1"); //1. 방처음 만들때 2. 만들어진 방에 들어갈때
                            intent.putExtra("roomidx", String.valueOf(jobj.get("myclassroomidx"))); //룸 idx를 같이 보내야함.
                            startActivity(intent);

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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    //action tab 추가
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.profilewritemenu, menu);
//        return true;
//    }
    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        //Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        //Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}