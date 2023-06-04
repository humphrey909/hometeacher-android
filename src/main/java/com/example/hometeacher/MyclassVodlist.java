package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.hometeacher.Adapter.ProblemRecyclerAdapter;
import com.example.hometeacher.Adapter.VodRecyclerAdapter;
import com.example.hometeacher.shared.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyclassVodlist extends AppCompatActivity {
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    String roomidx;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    int pagenum = 0;
    int limitnum = 10;

    ArrayList<JSONObject> Vodlist;
    RecyclerView VodRecyclerView;
    NestedScrollView nestedscrollbox;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_vodlist);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기


        division();

    }


    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;

        nestedscrollbox = (NestedScrollView) findViewById(R.id.nestedscrollbox);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        roomidx = intent.getExtras().getString("roomidx");


        Vodlist = new ArrayList<>();

        //스크롤뷰의 밑바당에 부딛힐때마다 프로그레스바가 돌고 데이터를 새로 가져온다. 열개씩 가져올 것.
        nestedscrollbox.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {

                    pagenum++;
                    progressBar.setVisibility(View.VISIBLE);

                    int offsetnum = limitnum*pagenum;

                    // Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                    GetVodlist(limitnum, offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));

                }
            }
        });
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

                    if (urlget2.equals("1")) { //vod 리스트 가져오기
                        Log.d("onResponse ? ", "vod 리스트 가져오기 : " + resultlist);

                        progressBar.setVisibility(View.GONE);

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            Log.d("onResponse ? ", "vod 리스트 가져오기 : " + String.valueOf(jarray.length()));

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "vod 리스트 가져오기 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Vodlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "vod 리스트 가져오기 : all" + String.valueOf(Vodlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeVodrecycle(Vodlist);

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    //vod 리스트
    public void GetVodlist(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getconferencevodlist(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //게시글 리스트를 만든다.
    public void MakeVodrecycle(ArrayList<JSONObject> Vodlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Vodlistjsonarray));

        VodRecyclerView = (RecyclerView) findViewById(R.id.VodRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        VodRecyclerAdapter oVodRecyclerAdapter = new VodRecyclerAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        VodRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        oVodRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oVodRecyclerAdapter.setRecycleList(Vodlistjsonarray); //arraylist 연결
        VodRecyclerView.setAdapter(oVodRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oVodRecyclerAdapter.setOnItemClickListener(new VodRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list) {

                try {
                    Intent intent = new Intent(oContext, MyclassVodview.class);
                    intent.putExtra("vodidx", String.valueOf(list.get(position).get("idx"))); //과제 고유번호
                    intent.putExtra("rid", roomidx); //룸 고유번호
                    intent.putExtra("voduri", String.valueOf(list.get(position).get("basicuri"))+String.valueOf(list.get(position).get("src"))); //룸 고유번호
                    intent.putExtra("vodregdate", String.valueOf(list.get(position).get("regdate"))); //룸 regdate
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

//        //선생님 일때만 작성
//        if(String.valueOf(Sessionlist.get(1).get(2)).equals("1")){
//            menuInflater.inflate(R.menu.problemlistmenu, menu);
//        }


        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.writebtn:

                Intent intent = new Intent(getApplicationContext(), MyclassProblemwrite.class);
                intent.putExtra("type", 1); //룸 고유번호
                intent.putExtra("rid", roomidx); //룸 고유번호
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Vodlist.clear();
        GetVodlist(limitnum, 0);
    }
}