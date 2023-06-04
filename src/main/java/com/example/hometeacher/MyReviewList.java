package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.hometeacher.Adapter.ProblemRecyclerAdapter;
import com.example.hometeacher.Adapter.ReviewRecyclerAdapter;
import com.example.hometeacher.Adapter.ReviewWriteviewRecyclerAdapter;
import com.example.hometeacher.Profile.Profileview;
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

public class MyReviewList extends AppCompatActivity {
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    int pagenum = 0;
    int limitnum = 10;

    ArrayList<JSONObject> MyReviewlist;
    RecyclerView ReviewRecyclerView;
    NestedScrollView nestedscrollbox;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review_list);

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
        ReviewRecyclerView = (RecyclerView) findViewById(R.id.ReviewRecyclerView);

        //인텐트 데이터를 받는다.
        //Intent intent = getIntent();
        //roomidx = intent.getExtras().getString("roomidx");


        MyReviewlist = new ArrayList<>();

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

                    GetReviewlist(limitnum, offsetnum);
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

                    if (urlget2.equals("1")) { //내가 작성한 리뷰 리스트
                        Log.d("onResponse ? ", "내가 작성한 리뷰 리스트 : " + resultlist);

                        progressBar.setVisibility(View.GONE);

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "내가 작성한 리뷰 리스트 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "내가 작성한 리뷰 리스트 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                MyReviewlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "내가 작성한 리뷰 리스트 : all" + String.valueOf(MyReviewlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeReviewrecycle(MyReviewlist);

                    }else if(urlget2.equals("2")){ //내가 작성한 리뷰 삭제
                        Log.d("onResponse ? ", "내가 작성한 리뷰 삭제  : " + resultlist);

                        //삭제후 새로고침 할것.

                        MyReviewlist.clear();
                        GetReviewlist(limitnum, 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //리뷰 리스트를 만든다.
    public void MakeReviewrecycle(ArrayList<JSONObject> Problemlistjsonarray){
        Log.d("리뷰 리스트 이다.", String.valueOf(Problemlistjsonarray));


        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        ReviewWriteviewRecyclerAdapter oReviewWriteviewRecyclerAdapter = new ReviewWriteviewRecyclerAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        ReviewRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        oReviewWriteviewRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oReviewWriteviewRecyclerAdapter.setRecycleList(Problemlistjsonarray); //arraylist 연결
        ReviewRecyclerView.setAdapter(oReviewWriteviewRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oReviewWriteviewRecyclerAdapter.setOnItemClickListener(new ReviewWriteviewRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list, int type) {
                if(type == 1){ // 1. 클릭시 해당 프로필로 이동

                    try {
                        Intent intent = new Intent(getApplicationContext(), Profileview.class);
                        intent.putExtra("sendtype", "2"); // 1. 나, 2. 다른유저
                        intent.putExtra("senduid",  String.valueOf(list.get(position).get("teacheruid"))); //선생님 고유번호
                        intent.putExtra("sendusertype", "1"); //유저타입 = 1. 선생님으로 고정 : 학생들이 선생님에게 리뷰를 작성하기 때문
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{ // 2. 메뉴버튼

                    //과제의 수정 삭제를 담당한다.
                    PopupMenu poup = poup = new PopupMenu(MyReviewList.this, v); //TODO 일반 사용
                    getMenuInflater().inflate(R.menu.nboardviewmenu, poup.getMenu());
                    poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                            String popup_tittle = item.getTitle().toString();
                            //TODO ==== [메뉴 선택 동작 처리] ====
                            if(popup_tittle.contains("수정하기")){
                                try {
                                    Intent intent = new Intent(getApplicationContext(), MyclassReviewwrite.class);
                                    intent.putExtra("type", "2"); //1. 추가, 2. 수정
                                    intent.putExtra("roomidx", String.valueOf(list.get(position).get("rid"))); //룸 고유번호
                                    intent.putExtra("teacheruid", String.valueOf(list.get(position).get("teacheruid"))); //룸 선생님 고유번호
                                    intent.putExtra("reviewidx", String.valueOf(list.get(position).get("idx"))); //리뷰 고유번호
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            else if(popup_tittle.contains("삭제하기")){

                                //다이어그램 띄우기
                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                                builder.setTitle("댓글 삭제").setMessage("댓글을 삭제합니다");

                                //삭제 실시
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        //과제 삭제로 진행이 되어야한다.
                                        try {
                                            setreviewdelete(String.valueOf(list.get(position).get("idx"))); //리뷰 고유번호
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Toast.makeText(view.getContext(), "Cancel Click"+id, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();

                                alertDialog.show();

                            }
                            return false;
                        }
                    });
                    poup.show(); // 메뉴를 띄우기


                }
            }
        });

    }


    //리뷰 삭제
    public void setreviewdelete(String reviewidx){


        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody reviewidx_ = RequestBody.create(MediaType.parse("text/plain"), reviewidx);
        requestMap.put("reviewidx", reviewidx_);

        call = retrofitService.reviewdelete(
                2, requestMap
        );
        RestapiResponse(); //응답
    }

    //내가 작성한 리뷰 리스트
    public void GetReviewlist(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody studentuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("studentuid", studentuid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getreviewlist(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.problemlistmenu, menu);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            //case R.id.writebtn:

//                Intent intent = new Intent(getApplicationContext(), MyclassProblemwrite.class);
//                intent.putExtra("type", 1); //룸 고유번호
//                intent.putExtra("rid", roomidx); //룸 고유번호
//                startActivity(intent);

             //   break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyReviewlist.clear();
        GetReviewlist(limitnum, 0);
    }

}