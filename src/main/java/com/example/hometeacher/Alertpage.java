package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hometeacher.Adapter.AlertRecyclerAdapter;
import com.example.hometeacher.Adapter.CommentRecyclerAdapter;
import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.shared.NboardviewsShared;
import com.example.hometeacher.shared.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class Alertpage extends AppCompatActivity {

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    Context oContext;
    Activity oActivity;

    NestedScrollView nestedScrollView;

    RecyclerView AlertRecyclerView;
    ProgressBar progressBar;
    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    int pagenum = 0;
    int limitnum = 10;
    ArrayList<JSONObject> Alertlist; //알림 리스트


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertpage);

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

        AlertRecyclerView = (RecyclerView) findViewById(R.id.AlertRecyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);

        //스크롤뷰의 밑바당에 부딛힐때마다 프로그레스바가 돌고 데이터를 새로 가져온다. 열개씩 가져올 것.
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {

                    pagenum++;
                    progressBar.setVisibility(View.VISIBLE);

                    int offsetnum = limitnum*pagenum;

                    //Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                    getalertlist(limitnum,offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));

                }
            }
        });
    }

    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.closemenu, menu);
        return true;
    }

    //action tab 버튼 클릭시
    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case android.R.id.home: //뒤로가기
            //finish();
            //break;
            case R.id.closetab: //창 닫기
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //알림 리스트 가져오기 - alertuid 를 내 uid로 참조,
    public void getalertlist(int limit, int offset) {

        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid); // alertuid 를 내 uid로 참조

        //해당 게시물 정보를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getalertlist(
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

    //레트로핏 데이터 응답 부분 : 인증번호 가져옴
    public void RestapiResponse() {
        //데이터 응답
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    String url = response.raw().request().url().toString();
                    String urlget = url.split("/")[8];
                    String urlget2 = urlget.split("=")[1];

                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제

                    if (urlget2.equals("1")) { //알림리스트 가져오기
                        Log.d("onResponse ? ", "알림리스트 가져오기 ----- : " + String.valueOf(resultlist));

                        progressBar.setVisibility(View.GONE);

                        JSONArray jarray = null;
                        try {
                            jarray = new JSONArray(resultlist);

                            for(int i = 0; i<jarray.length(); i++) {
                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));
                                Alertlist.add(jobj);
                            }



                            SetAlertlist(Alertlist);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //알림 리스트 수정하기
                        Log.d("onResponse ? ", "알림 리스트 수정하기 ----- : " + String.valueOf(resultlist));

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void SetAlertlist(ArrayList<JSONObject> Alertlistjsonarray){
        Log.d("알림 리스트 이다.", String.valueOf(Alertlistjsonarray));

        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        AlertRecyclerAdapter oAlertRecyclerAdapter = new AlertRecyclerAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        AlertRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        //oAlertRecyclerAdapter.setneeddata(1);
        oAlertRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oAlertRecyclerAdapter.setRecycleList(Alertlistjsonarray); //arraylist 연결
        AlertRecyclerView.setAdapter(oAlertRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oAlertRecyclerAdapter.setOnItemClickListener(new AlertRecyclerAdapter.OnItemClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) throws JSONException {

                //해당 알림 click 변경
                //해당 장소로 이동
                //Toast.makeText(GlobalClass, String.valueOf(list.get(position).get("alertdocu")), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(oContext, Nboardview.class);
                intent.putExtra("nid", String.valueOf(list.get(position).get("nid")));
                intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
               // intent.putExtra("key", "데이터가 전달 되었습니다.");
                startActivity(intent);


                editalert(String.valueOf(list.get(position).get("idx")));



            }
        });
    }

    //알림 click 변경하기
    public void editalert(String alertidx) {

        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody alertidx_ = RequestBody.create(MediaType.parse("text/plain"), alertidx);
        requestMap.put("alertidx", alertidx_);
        RequestBody clickval = RequestBody.create(MediaType.parse("text/plain"), "0");
        requestMap.put("clickval", clickval);

        //해당 게시물 정보를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.editalert(
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }

    @Override
    protected void onResume() {
        super.onResume();

        Alertlist = new ArrayList<>();
        getalertlist(limitnum,0);
        pagenum = 0;

    }
}