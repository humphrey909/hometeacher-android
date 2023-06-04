package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hometeacher.ArraylistForm.ImgFormMulti;
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

//리뷰 작성 칸
//해당 별점 항목 4개
public class MyclassReviewwrite extends AppCompatActivity {

    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    String Selecttype, roomidx, teacheruid, reviewidx;
    String professional_val = "";
    String lecturepower_val = "";
    String lectureready_val = "";
    String lectureontime_val = "";

    ImageView closetab;
    EditText documentedit;
    RatingBar professional_star, lecturepower_star, lectureready_star, lectureontime_star;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    int pagenum = 0;
    int limitnum = 10;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ArrayList<JSONObject> Reviewinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_reviewwrite);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기


        division();

    }

    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;
        documentedit = (EditText) this.findViewById(R.id.documentedit);

        //데이터 받음
        Intent intent = getIntent();
        Selecttype = intent.getExtras().getString("type"); // 1 추가, 2 수정
        roomidx = intent.getExtras().getString("roomidx"); // 방 고유번호
        teacheruid = intent.getExtras().getString("teacheruid"); // 선생님 공유번호
        Log.d("-------roomidx-------", String.valueOf(roomidx));
        Log.d("-------teacheruid-------", String.valueOf(teacheruid));




        if(Selecttype.equals("2")){ //수정

            reviewidx = intent.getExtras().getString("reviewidx"); //리뷰 고유번호


//            Selectpid = intent.getExtras().getString("pid");
//
////            //해당 nid에 대한 정보를 가져오기
////            try {
////                nboardgetdata(0, 0);
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
//            Problemlist = new ArrayList<>();
//            //Selectpid에 대한 내용가져오기

            Reviewinfo = new ArrayList<>();
            GetReviewinfo();
        }else { //추가

        }

        //게시글 닫기
        closetab = (ImageView) this.findViewById(R.id.closetab);
        closetab.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                finish();
            }
        });

        professional_star = (RatingBar) this.findViewById(R.id.professional_star);
        professional_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.d("v", String.valueOf(v));
                professional_val = String.valueOf(v);
            }
        });
        lecturepower_star = (RatingBar) this.findViewById(R.id.lecturepower_star);
        lecturepower_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.d("v", String.valueOf(v));
                lecturepower_val = String.valueOf(v);
            }
        });
        lectureready_star = (RatingBar) this.findViewById(R.id.lectureready_star);
        lectureready_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.d("v", String.valueOf(v));
                lectureready_val = String.valueOf(v);
            }
        });
        lectureontime_star = (RatingBar) this.findViewById(R.id.lectureontime_star);
        lectureontime_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.d("v", String.valueOf(v));
                lectureontime_val = String.valueOf(v);
            }
        });
    }

    //내 리뷰 수정하기 위해 정보 가져오기
    public void GetReviewinfo(){
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody reviewidx_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(reviewidx));
        requestMap.put("reviewidx", reviewidx_);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getreviewlist(
                0,
                0,
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //레트로핏 라이브러리를 사용해 보낼 양식 만들어 놓음
    public void RestapiStart(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송

    }

    //레트로핏 데이터 응답 부분 : 인증번호 가져옴
    public void RestapiResponse(){
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

                    if (urlget2.equals("1")) { //리뷰 저장
                        Log.d("onResponse ? ", "리뷰 저장 : " + resultlist);
                        try {
                            JSONObject jobj = new JSONObject(String.valueOf(resultlist));
                            Log.d("onResponse ? ", "리뷰 저장 : " + jobj.get("result"));

                            if(String.valueOf(jobj.get("result")).equals("true")){
                                Log.d("onResponse ? ", "리뷰 저장 : " + "저장됨.");
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }else if(urlget2.equals("2")) {//리뷰 정보 가져오기
                        Log.d("onResponse ? ", "리뷰 정보 가져오기 : " + resultlist);

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "리뷰 정보 가져오기 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "리뷰 정보 가져오기 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Reviewinfo.add(jobj);
                            }

                            Log.d("onResponse ? ", "리뷰 정보 가져오기 : all" + String.valueOf(Reviewinfo)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        try {
                            SetReviewinfo(Reviewinfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("3")){ //리뷰 수정
                        Log.d("onResponse ? ", "리뷰 수정 : " + resultlist);
                        try {
                            JSONObject jobj = new JSONObject(String.valueOf(resultlist));
                            Log.d("onResponse ? ", "리뷰 수정 : " + jobj.get("result"));

                            if(String.valueOf(jobj.get("result")).equals("true")){
                                Log.d("onResponse ? ", "리뷰 수정 : " + "수정됨.");
                                finish();
                            }


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

    //서버에서 가져온데이터 입력
    @SuppressLint("SetTextI18n")
    public void SetReviewinfo(ArrayList<JSONObject> ReviewInfojsonarray) throws JSONException {

        documentedit.setText(String.valueOf(ReviewInfojsonarray.get(0).get("reviewtext")));

        professional_val = String.valueOf(ReviewInfojsonarray.get(0).get("professional"));
        professional_star.setRating(Float.parseFloat(String.valueOf(ReviewInfojsonarray.get(0).get("professional"))));

        lecturepower_val = String.valueOf(ReviewInfojsonarray.get(0).get("lecturepower"));
        lecturepower_star.setRating(Float.parseFloat(String.valueOf(ReviewInfojsonarray.get(0).get("lecturepower"))));

        lectureready_val = String.valueOf(ReviewInfojsonarray.get(0).get("lectureready"));
        lectureready_star.setRating(Float.parseFloat(String.valueOf(ReviewInfojsonarray.get(0).get("lectureready"))));

        lectureontime_val = String.valueOf(ReviewInfojsonarray.get(0).get("lectureontime"));
        lectureontime_star.setRating(Float.parseFloat(String.valueOf(ReviewInfojsonarray.get(0).get("lectureontime"))));

    }


    //리뷰 데이터를 저장한다.
    public void Reviewsave(){
        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(roomidx));
        requestMap.put("roomidx", roomidx_);
        RequestBody teacheruid_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(teacheruid));
        requestMap.put("teacheruid", teacheruid_);
        RequestBody writeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("writeuid", writeuid);

        RequestBody reviewtext = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(documentedit.getText()));
        requestMap.put("reviewtext", reviewtext);

        RequestBody professional = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(professional_val));
        requestMap.put("professional", professional);
        RequestBody lecturepower = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lecturepower_val));
        requestMap.put("lecturepower", lecturepower);
        RequestBody lectureready = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lectureready_val));
        requestMap.put("lectureready", lectureready);
        RequestBody lectureontime = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lectureontime_val));
        requestMap.put("lectureontime", lectureontime);

        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        call = retrofitService.reviewsave(
                1, requestMap
        );
        RestapiResponse(); //응답
    }

    //리뷰 데이터를 수정한다.
    public void Reviewupdate(){
        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        

        RequestBody reviewidx_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(reviewidx));//리뷰 고유번호
        requestMap.put("reviewidx", reviewidx_);    
//        RequestBody roomidx_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(roomidx));
//        requestMap.put("roomidx", roomidx_);
//        RequestBody teacheruid_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(teacheruid));
//        requestMap.put("teacheruid", teacheruid_);
//        RequestBody writeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
//        requestMap.put("writeuid", writeuid);

        RequestBody reviewtext = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(documentedit.getText()));
        requestMap.put("reviewtext", reviewtext);

        RequestBody professional = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(professional_val));
        requestMap.put("professional", professional);
        RequestBody lecturepower = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lecturepower_val));
        requestMap.put("lecturepower", lecturepower);
        RequestBody lectureready = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lectureready_val));
        requestMap.put("lectureready", lectureready);
        RequestBody lectureontime = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lectureontime_val));
        requestMap.put("lectureontime", lectureontime);

        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        call = retrofitService.reviewupdate(
                3, requestMap
        );
        RestapiResponse(); //응답
    }

    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.problemwritemenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();

                break;
            case R.id.completebtn: //수정 완료, 작성 완료
                Log.d("","--------drawertab---------");




                if(!String.valueOf(documentedit.getText()).equals("") && !professional_val.equals("") && !lecturepower_val.equals("") && !lectureready_val.equals("") && !lectureontime_val.equals("")) {

                    if(Selecttype.equals("2")) { //2. 수정
                        Reviewupdate();
                    }else{ // 1. 추가
                        Reviewsave();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}