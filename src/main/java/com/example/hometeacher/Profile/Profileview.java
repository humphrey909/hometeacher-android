package com.example.hometeacher.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.ImgViewAdapter;
import com.example.hometeacher.Adapter.ReviewRecyclerAdapter;
import com.example.hometeacher.Classinquiryroomactivity;
import com.example.hometeacher.ImgbigsizeActivity;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.SearchBox.CurrentSearchpage;
import com.example.hometeacher.shared.ClassSearchviewsShared;
import com.example.hometeacher.shared.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Profileview extends AppCompatActivity {

    Context oContext;
    Activity oActivity;

    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;
    ArrayList<JSONObject> ProfileInfo;
    ArrayList<JSONObject> ProfileImgInfo;
    ArrayList<JSONObject> Likeuserlist;
    ArrayList<JSONObject> Reviewlist;

    FrameLayout framebox;

    RadioButton profiletype1, profiletype2;
    RadioButton.OnClickListener clickListener;

    String profilecate = "1";


    LinearLayout bottomlinear;
    ImageView profilemainimg, likebtn;
    TextView classpaybottomtext, name, schoolinfo, subjectinfo, introducedocu, schoolchk, schooldocu, campusinfo, characteredocu, subject, monthpaydocu, monthpaystandard, subjectdocu, classstyle, skillapeal, availabletime, age;
    TextView gendertext, toteachertalk, views, requestclassbtn, liketext;
    View view5;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    RecyclerView imagesviewRecyclerView, reviewviewRecyclerView;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<Uri> profilemainimglist;

    String SendType; //다른유저인지 내 유저인지 구븐하는 변수
    String SendUid; //유저의 고유번호를 넘겨 받는 변수
    String SendUsertype; //유저의 타입을 넘겨 받는 변수

    //고정되어 사용되는 변수
    String FixUid;
    String FixUsertype;

    String MyUid;
    String MyUsertype;

    int likenumtype = 0; //좋아요 클릭, 해제 시 반응하는 변수
    int liketotal = 0; // 게시글의 좋아요 총 갯수

    ClassSearchviewsShared oClassSearchviewsShared;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int pagenum = 0;
    int limitnum = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);



        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기


        try {
            division();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void changeView(int index) throws ParseException, JSONException {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        framebox = (FrameLayout) findViewById(R.id.framebox) ;
        if (framebox.getChildCount() > 0) {    // FrameLayout에서 뷰 삭제.
            framebox.removeViewAt(0);
        }

        View view = null ;
        switch (index) {
            case 0 :
                if(FixUsertype.equals("1")) { //선생님 프레임 입히기
                    view = inflater.inflate(R.layout.frameprofile_teacher, framebox, false) ;
                }else{ //학생 프레임 입히기
                    view = inflater.inflate(R.layout.frameprofile_student, framebox, false) ;
                }
                break ;
            case 1 :
                view = inflater.inflate(R.layout.framereview, framebox, false) ;
                break ;
        }

        // FrameLayout에 뷰 추가.
        if (view != null) {
            framebox.addView(view) ;
        }


        Functionbinding(index);
    }
    //frame마다 필요한 기능을 연결해준다.
    public void Functionbinding(int idx) throws JSONException {
        switch (idx) {
            case 0 : //프로필
               // introducedocu = (TextView) findViewById(R.id.introducedocu);
               // introducedocu.setText("wefwefweffwwfe");
                Log.d("onResponse ? ", "onResponse 프로필 리스트 -2 : " + String.valueOf(ProfileInfo));
               // Log.d("onResponse ? ", "onResponse 프로필 리스트 -2 : " + String.valueOf(ProfileInfo.get(0)));

                Log.d("onResponse ? ", "onResponse 프로필 리스트 -2 : " + String.valueOf(ProfileImgInfo));

                if(ProfileInfo != null){
                    datachange();
                }

                if(ProfileImgInfo != null){
                    imgdatachange();
                }

                if(Likeuserlist != null){
                    likedatachange();
                }else{
                    if(FixUsertype.equals("1")){
                        //데이터가 없으면 0처리
                        liketext = (TextView) findViewById(R.id.liketext);
                        liketext.setText(String.valueOf(0)); //좋아요 수 변경
                    }
                }
                break ;
            case 1 : //과외후기
                //선생님일때만 리뷰를 가져온다.
                if(FixUsertype.equals("1")){
                    Reviewlist.clear();
                    getreviewlist(limitnum,0);
                }

                break ;
        }

    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    protected void onStart() {
        super.onStart();

        profilecate = "1";
        try {
            changeView(0);
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

        //선택된 라디오버튼에 맞게 true false 지정 - 글자색 변경
        if(profilecate.equals("1")){
            profiletype1.setTextColor(getResources().getColorStateList(R.color.maincolor));
            profiletype2.setTextColor(getResources().getColorStateList(R.color.black));
        }else{
            profiletype1.setTextColor(getResources().getColorStateList(R.color.black));
            profiletype2.setTextColor(getResources().getColorStateList(R.color.maincolor));
        }

        //선택된 라디오버튼에 맞게 true false 지정
        if(profilecate.equals("1")){
            profiletype1.setChecked(true);
        }else{
            profiletype2.setChecked(true);
        }

    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SendType.equals("1")) { //내프로필에서 접근할때만 보여짐
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.profileviewmenu, menu);
        }else{
            //MenuInflater menuInflater = getMenuInflater();
            //menuInflater.inflate(R.menu.profileviewmenu2, menu);
        }
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
            case R.id.edittab: //프로필 편집




//                if(ProfileSavechk.equals("1")){ //프로필 값이 없다는 것
//                    //프로필 등록이 안되어 있으면 추가로 보내.
//                    Intent intent = new Intent(getApplicationContext(), Profilewrite.class);
//                    intent.putExtra("type", 1); //추가
//                    startActivityForResult(intent, REQUESTCODE);
               // }else{
                    String projectidx = "";
                    try {
                        Log.d("onResponse ? ", "onResponse 프로필 리스트 -2 : " + String.valueOf(ProfileInfo.get(0).get("pidx")));
                        projectidx = String.valueOf(ProfileInfo.get(0).get("pidx"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //프로필 등록이 안되어 있으면 추가로 보내.
                    Intent intent = new Intent(getApplicationContext(), Profilewrite.class);
                    intent.putExtra("type", 2); //수정
                    intent.putExtra("profileidx", projectidx); //수정
                    startActivityForResult(intent, REQUESTCODE);
              //  }



//                try {
//                    Log.d("", (String) ProfileInfo.get(0).get("pidx"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                // Intent intent = new Intent(getApplicationContext(), Profilewrite.class);
               // startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
                    String url = response.raw().request().url().toString();
                    String urlget = url.split("/")[8];
                    String urlget2 = urlget.split("=")[1];

                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제

                    if(urlget2.equals("1")) { //프로필 데이터 리스트

                        Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(resultlist));

                        try {
                            ProfileInfo = new ArrayList<>();

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);


                            Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray));
                           // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                            if (jarray.get(0) != null) {

                                    for(int i = 0; i<jarray.length(); i++){
                                        JSONObject tempJson = jarray.getJSONObject(i);
                                        //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                       // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                        //jsonobject형식으로 데이터를 저장한다.
                                        ProfileInfo.add(tempJson);
                                    }

                                    //object형식으로 arraylist를 만든것
                                Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(ProfileInfo));

                                //데이터를 바로 출력시킬것
                                datachange();

                                Log.d("json 파싱", "프로필 데이터 가져오기 성공");

                            } else {
                                Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //프로필 이미지 리스트
                        Log.d("onResponse ? ","onResponse 프로필 이미지 리스트 : " + String.valueOf(resultlist));


                        try {
                            ProfileImgInfo = new ArrayList<>();

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            Log.d("onResponse ? ","onResponse 프로필 이미지 데이터 : " + String.valueOf(jarray));
                            // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                            if (jarray.get(0) != null) {

                                for(int i = 0; i<jarray.length(); i++){
                                    JSONObject tempJson = jarray.getJSONObject(i);
                                    //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                    // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                    //jsonobject형식으로 데이터를 저장한다.
                                    ProfileImgInfo.add(tempJson);
                                }

                                //object형식으로 arraylist를 만든것
                                Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 : " + String.valueOf(ProfileImgInfo));

                                //데이터를 바로 출력시킬것
                                imgdatachange();

                                Log.d("json 파싱", "프로필 이미지 가져오기 성공");

                            } else {
                                Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("3")){ //좋아요 클릭시
                        Log.d("onResponse ? ","onResponse 프로필 좋아요, 해제 : " + String.valueOf(resultlist));


                        //좋아요 리스트를 다시 불러오기
                        getlikelist();
                    }else if(urlget2.equals("4")){ //좋아요 리스트 불러옴
                        Log.d("onResponse ? ","onResponse 프로필 좋아요 리스트  : " + String.valueOf(resultlist));
                        Log.d("onResponse ? ","onResponse 프로필 좋아요 리스트  : " + String.valueOf(resultlist.length()));
                        if(resultlist.length() != 2) { //값이 없다는 것
                            try {
                                Likeuserlist = new ArrayList<>();

                                //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                                JSONArray jarray = new JSONArray(resultlist);
                                Log.d("onResponse ? ", "onResponse 프로필 이미지 데이터 : " + String.valueOf(jarray));
                                // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                                if (jarray.get(0) != null) {

                                    for (int i = 0; i < jarray.length(); i++) {
                                        JSONObject tempJson = jarray.getJSONObject(i);
                                        //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                        // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                        //jsonobject형식으로 데이터를 저장한다.
                                        Likeuserlist.add(tempJson);
                                    }

                                    //object형식으로 arraylist를 만든것
                                    Log.d("onResponse ? ", "onResponse 좋아요 유저 리스트 : " + String.valueOf(Likeuserlist));

                                    //데이터를 바로 출력시킬것
                                    likedatachange();

                                    Log.d("json 파싱", "좋아요 유저 리스트 가져오기 성공");

                                } else {
                                    Log.d("json 파싱", "좋아요 유저 리스트 가져오기 실패");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            if(FixUsertype.equals("1")) {
                                //데이터가 없으면 0처리
                                liketext = (TextView) findViewById(R.id.liketext);
                                liketext.setText(String.valueOf(0)); //좋아요 수 변경
                            }
                        }
                    }else if(urlget2.equals("5")){ // 과외 문의 방 생성 or 과외 문의 방 존재여부 체크
                        Log.d("onResponse ? ","onResponse 과외 문의 방 생성  : " + String.valueOf(resultlist));


                        try {
                            JSONObject jobj = new JSONObject(resultlist);


                            Log.d("onResponse ? ","onResponse 과외 문의 방 생성  : " + String.valueOf(jobj));
                            Log.d("onResponse ? ","onResponse 과외 문의 방 생성  : " + String.valueOf(jobj.get("result")));
                            Log.d("onResponse ? ","onResponse 과외 문의 방 생성  : " + String.valueOf(jobj.get("err")));

                            //방을 만들고 이동
                            if(String.valueOf(jobj.get("result")).equals("true")){
                                Log.d("onResponse ? ","onResponse 과외 문의 방 생성  : " + String.valueOf(true));

                                //프로필 등록이 안되어 있으면 추가로 보내.
                                Intent intent = new Intent(getApplicationContext(), Classinquiryroomactivity.class);
                                intent.putExtra("roommaketype", "1"); //1. 방처음 만들때 2. 만들어진 방에 들어갈때
                                intent.putExtra("roomidx", String.valueOf(jobj.get("roomidx"))); //룸 idx를 같이 보내야함.
                                startActivity(intent);


                                //기존에 있던 방을 확인 후 이동
                            }else{ //false
                                Log.d("onResponse ? ","onResponse 과외 문의 방 생성  : " + String.valueOf(false));

                                if(String.valueOf(jobj.get("err")).equals("exist room")){
                                    //프로필 등록이 안되어 있으면 추가로 보내.
                                    Intent intent = new Intent(getApplicationContext(), Classinquiryroomactivity.class);
                                    intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어갈때
                                    intent.putExtra("roomidx", String.valueOf(jobj.get("roomidx"))); //룸 idx를 같이 보내야함.
                                    intent.putExtra("Tchatcount", String.valueOf(jobj.get("chatcount"))); //총 채팅 갯수

                                    startActivity(intent);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("6")) { //리뷰 리스트
                        Log.d("onResponse ? ","리뷰 리스트  : " + String.valueOf(resultlist));


                        try {
                            JSONArray jarray = new JSONArray(resultlist);

                            for(int i = 0; i<jarray.length(); i++){
                                JSONObject tempJson = jarray.getJSONObject(i);
                                //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                //jsonobject형식으로 데이터를 저장한다.
                                Reviewlist.add(tempJson);
                            }

                            Log.d("onResponse ? ", "리뷰 리스트 : " + String.valueOf(Reviewlist));

                            makereview(Reviewlist);


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

    //리뷰 만들기
    public void makereview(ArrayList<JSONObject> Reviewlist){

        Log.d("onResponse ? ", "리뷰 리스트 : " + String.valueOf(Reviewlist));

        reviewviewRecyclerView = (RecyclerView)findViewById(R.id.reviewviewRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false); //가로일때
        reviewviewRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ReviewRecyclerAdapter oReviewRecyclerAdapter = new ReviewRecyclerAdapter(this, oActivity); //내가만든 어댑터 선언
        oReviewRecyclerAdapter.setRecycleList(Reviewlist); //arraylist 연결
        reviewviewRecyclerView.setAdapter(oReviewRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oReviewRecyclerAdapter.setOnItemClickListener(new ReviewRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int picturepos, ArrayList<JSONObject> list) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.

//                Intent intent = new Intent(Profileview.this, ImgbigsizeActivity.class);
//                intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(imguri));
//                // intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));
//
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        Profileview.this,
//                        sharedImageView,
//                        Objects.requireNonNull(ViewCompat.getTransitionName(sharedImageView)));
//
//                startActivity(intent, options.toBundle());
            }
        }) ;

    }

    //좋아요 데이터 출력하는 부분
    public void likedatachange() throws JSONException {
        //Likeuserlist // [{"idx":"8","giveuid":"16","getuid":"32","regdate":"2022-06-26 14:29:29"}]
        for(int i = 0; i<Likeuserlist.size();i++) {
            Log.d("onResponse ? ", " 프로필 좋아요 리스트 -1 : " + String.valueOf(Likeuserlist.get(i)));

            Log.d("onResponse ? ", " 프로필 좋아요 리스트 -1 : " + String.valueOf(Likeuserlist.get(i).get("giveuid")));
            if(Sessionlist.get(1).get(0).equals(Likeuserlist.get(i).get("giveuid"))){
                likebtn.setImageResource(R.drawable.hearts_chk);//내가 좋아요 클릭 했으면 하트표시
                likenumtype = 1;
            }
        }
        Log.d("onResponse ? ", " 프로필 좋아요 리스트 갯수 : " + Likeuserlist.size());

        //선생님만 체크 할것
       // if(FixUsertype.equals("1")) { //선생님
            liketext = (TextView) findViewById(R.id.liketext);
            liketext.setText(String.valueOf(Likeuserlist.size())); //좋아요 수 변경
       // }
    }

    //프로필 이미지 데이터를 가져올때 사용한다. ProfileImgInfo에 값이 있어야 프로필의 정보가 보여진다.
    //서버 통신은 처음 페이지에 왔을때, 다시 돌아왔을때만 서버통신을 함. oncreate, onstart
    public void imgdatachange() throws JSONException {

        Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 -3 : " + String.valueOf(ProfileImgInfo));
        //Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 -3 : " + String.valueOf(ProfileImgInfo.get(0)));
        //Log.d("onResponse ? ",RetrofitService.MOCK_SERVER_FIRSTURL+ProfileImgInfo.get(0).get("basicuri")+ProfileImgInfo.get(0).get("src"));

        profilemainimglist = new ArrayList<>();
        ArrayList<Uri> profileimglist = new ArrayList<>();
        for(int i = 0; i<ProfileImgInfo.size();i++){
            if(ProfileImgInfo.get(i).get("type").equals("1")){ //프로필 메인 이미지


                Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+ProfileImgInfo.get(i).get("basicuri")+ProfileImgInfo.get(i).get("src"));
                profilemainimglist.add(imageUri);

                Picasso.get()
                        .load(imageUri) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(profilemainimg);

            }else{ //프로필 이미지 리스트
                Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+ProfileImgInfo.get(i).get("basicuri")+ProfileImgInfo.get(i).get("src"));
                profileimglist.add(imageUri);
            }
        }



        Imagerecycle(profileimglist);
    }
    //이미지 리스트 만듬
    public void Imagerecycle(ArrayList<Uri> imglist) {

        imagesviewRecyclerView = (RecyclerView)findViewById(R.id.imagesviewRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false); //가로일때
        imagesviewRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgViewAdapter ImgViewAdapter = new ImgViewAdapter(this, oActivity); //내가만든 어댑터 선언
        ImgViewAdapter.setRecycleList(imglist); //arraylist 연결
        imagesviewRecyclerView.setAdapter(ImgViewAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        ImgViewAdapter.setOnItemClickListener(new ImgViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int picturepos, Uri imguri, ImageView sharedImageView) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.

                Intent intent = new Intent(Profileview.this, ImgbigsizeActivity.class);
                intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(imguri));
               // intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        Profileview.this,
                        sharedImageView,
                        Objects.requireNonNull(ViewCompat.getTransitionName(sharedImageView)));

                startActivity(intent, options.toBundle());
            }
        }) ;
    }

    //프로필 데이터를 가져올때 사용한다. ProfileInfo에 값이 있어야 프로필의 정보가 보여진다.
    //서버 통신은 처음 페이지에 왔을때, 다시 돌아왔을때만 서버통신을 함. oncreate, onstart
    @SuppressLint("SetTextI18n")
    public void datachange() throws JSONException {
        Log.d("onResponse ? ", "onResponse 프로필 리스트 -3 : " + String.valueOf(ProfileInfo));

        name = (TextView) findViewById(R.id.name);
        schoolinfo = (TextView) findViewById(R.id.schoolinfo);
        subjectinfo = (TextView) findViewById(R.id.subjectinfo);

        if(FixUsertype.equals("1")){ //선생님
            introducedocu = (TextView) findViewById(R.id.introducedocu);
            gendertext = (TextView) findViewById(R.id.gendertext);
            schoolchk = (TextView) findViewById(R.id.schoolchk);
            schooldocu = (TextView) findViewById(R.id.schooldocu);
            campusinfo = (TextView) findViewById(R.id.campusinfo);
            characteredocu = (TextView) findViewById(R.id.characteredocu);
            subject = (TextView) findViewById(R.id.subject);
            monthpaydocu = (TextView) findViewById(R.id.monthpaydocu);
            monthpaystandard = (TextView) findViewById(R.id.monthpaystandard);
            subjectdocu = (TextView) findViewById(R.id.subjectdocu);
            classstyle = (TextView) findViewById(R.id.classstyle);
            skillapeal = (TextView) findViewById(R.id.skillapeal);
            availabletime = (TextView) findViewById(R.id.availabletime);
            views = (TextView) findViewById(R.id.views);

            introducedocu.setText(String.valueOf(ProfileInfo.get(0).get("introduce")));

            String genderchar = gender_replace(String.valueOf(ProfileInfo.get(0).get("gender")));
            gendertext.setText(genderchar);


            schoolchk.setText(schoolchk_replace(String.valueOf(ProfileInfo.get(0).get("universitychk"))));
            schooldocu.setText(String.valueOf(ProfileInfo.get(0).get("university"))+" "+ProfileInfo.get(0).get("universmajor")+" "+ProfileInfo.get(0).get("studentid")+"학번 입니다.");
            campusinfo.setText(String.valueOf(ProfileInfo.get(0).get("campusaddress"))+" 에 위치한 캠퍼스 입니다. ");

            //character_replace
            //성격 변환
            String character = String.valueOf(ProfileInfo.get(0).get("character")).substring(0,String.valueOf(ProfileInfo.get(0).get("character")).length()-1); //마지막 문자 삭제
            String character2 = character.substring(1); //첫번째 문자 삭제
            String character3 = character2.replaceAll(" ", ""); //모든 공백 제거
            String[] characterarr = character3.split(",");

            String Charactercomplete = "";
            for(int i=0;i<characterarr.length;i++){
                Charactercomplete += character_replace(characterarr[i])+",";
            }
            String Charactercomplete_ = Charactercomplete.substring(0,Charactercomplete.length()-1); //마지막 문자 삭제
            characteredocu.setText(Charactercomplete_);



            //subject.setText(String.valueOf(ProfileInfo.get(0).get("majorsubject")));
            monthpaydocu.setText(String.valueOf(ProfileInfo.get(0).get("minpay")));
            classpaybottomtext.setText("월"+ProfileInfo.get(0).get("minpay")+"만원");

            monthpaystandard.setText(String.valueOf(ProfileInfo.get(0).get("detailpaystandard")));
            subjectdocu.setText(String.valueOf(ProfileInfo.get(0).get("subjectdocument")));
            classstyle.setText(String.valueOf(ProfileInfo.get(0).get("classstyle")));
            skillapeal.setText(String.valueOf(ProfileInfo.get(0).get("skillappeal")));
            availabletime.setText(String.valueOf(ProfileInfo.get(0).get("availabletime")));

            name.setText(String.valueOf(ProfileInfo.get(0).get("name")+" 선생님("+ProfileInfo.get(0).get("nicname")+")"));
            schoolinfo.setText(String.valueOf(ProfileInfo.get(0).get("university"))+" "+ProfileInfo.get(0).get("universmajor")+" "+ProfileInfo.get(0).get("studentid")+" "+genderchar);

            //과목 변환
            String subjectsub = String.valueOf(ProfileInfo.get(0).get("majorsubject")).substring(0,String.valueOf(ProfileInfo.get(0).get("majorsubject")).length()-1); //마지막 문자 삭제
            String subjectsub2 = subjectsub.substring(1); //첫번째 문자 삭제
            String subjectsub3 = subjectsub2.replaceAll(" ", ""); //모든 공백 제거
            String[] subjectarr = subjectsub3.split(",");

            String Subjectcomplete = "";
            for(int i=0;i<subjectarr.length;i++){
                Subjectcomplete += subject_replace(subjectarr[i])+",";
            }
            String Subjectcomplete_ = Subjectcomplete.substring(0,Subjectcomplete.length()-1); //마지막 문자 삭제
            subject.setText(Subjectcomplete_);
            subjectinfo.setText(Subjectcomplete_);

            views.setText(String.valueOf(ProfileInfo.get(0).get("views")));

        }else{ //학생


            introducedocu = (TextView) findViewById(R.id.introducedocu);
            gendertext = (TextView) findViewById(R.id.gendertext);
            subject = (TextView) findViewById(R.id.subject);
            monthpaydocu = (TextView) findViewById(R.id.monthpaydocu);
            availabletime = (TextView) findViewById(R.id.availabletime);
            toteachertalk = (TextView) findViewById(R.id.toteachertalk);
            views = (TextView) findViewById(R.id.views);

            name.setText(String.valueOf(ProfileInfo.get(0).get("name")+" 학생("+ProfileInfo.get(0).get("nicname")+")"));

            String genderchar = gender_replace(String.valueOf(ProfileInfo.get(0).get("gender")));
            String studentagechar = studentage_replace(String.valueOf(ProfileInfo.get(0).get("studentages")));
            schoolinfo.setText(studentagechar+" "+genderchar);

            introducedocu.setText(String.valueOf(ProfileInfo.get(0).get("introduce")));
            gendertext.setText(genderchar);

            //과목 변환
            String subjectsub = String.valueOf(ProfileInfo.get(0).get("majorsubject")).substring(0,String.valueOf(ProfileInfo.get(0).get("majorsubject")).length()-1); //마지막 문자 삭제
            String subjectsub2 = subjectsub.substring(1); //첫번째 문자 삭제
            String subjectsub3 = subjectsub2.replaceAll(" ", ""); //모든 공백 제거
            String[] subjectarr = subjectsub3.split(",");

            String Subjectcomplete = "";
            for(int i=0;i<subjectarr.length;i++){
                Subjectcomplete += subject_replace(subjectarr[i])+",";
            }
            String Subjectcomplete_ = Subjectcomplete.substring(0,Subjectcomplete.length()-1); //마지막 문자 삭제
            subject.setText(Subjectcomplete_);
            subjectinfo.setText(Subjectcomplete_);

            monthpaydocu.setText("저는 월 "+String.valueOf(ProfileInfo.get(0).get("maxpay"))+"만원 까지 생각하고 있습니다. ");
            classpaybottomtext.setText("월"+ProfileInfo.get(0).get("maxpay")+"만원 이상");
            availabletime.setText(String.valueOf(ProfileInfo.get(0).get("availabletime")));
            toteachertalk.setText(String.valueOf(ProfileInfo.get(0).get("infotalk")));
            views.setText(String.valueOf(ProfileInfo.get(0).get("views")));


        }
        //age.setText(String.valueOf(ProfileInfo.get(0).get("age")));
    }


    //성별 문자열로 변환
    public String gender_replace(String gender){
        if(gender.equals("1")){
            return "남";
        }else{
            return "여";
        }
    }

    //학교 재학여부 문자열로 변환
    public String schoolchk_replace(String schoolchk){
        if(schoolchk.equals("0")){
            return "재학중";
        }else if(schoolchk.equals("1")){
            return "휴학중";
        }else if(schoolchk.equals("2")){
            return "졸업";
        }else if(schoolchk.equals("3")){
            return "중퇴";
        }else{
            return "-";
        }
    }
    
    
    //과목 문자열로 변환
    public String subject_replace(String subject){
        if(subject.equals("0")){
            return "국어";
        }else if(subject.equals("1")){
            return "영어";
        }else if(subject.equals("2")){
            return "수학";
        }else if(subject.equals("3")){
            return "사회";
        }else if(subject.equals("4")){
            return "과학";
        }else if(subject.equals("5")){
            return "자격증";
        }else{
            return "-";
        }
    }
    //성격 문자열로 변환
    public String character_replace(String characternum){
        if(characternum.equals("0")){
            return "지적인";
        }else if(characternum.equals("1")){
            return "차분한";
        }else if(characternum.equals("2")){
            return "유머있는";
        }else if(characternum.equals("3")){
            return "낙천적인";
        }else if(characternum.equals("4")){
            return "내향적인";
        }else if(characternum.equals("5")){
            return "외향적인";
        }else if(characternum.equals("6")){
            return "감성적인";
        }else if(characternum.equals("7")){
            return "상냥한";
        }else if(characternum.equals("8")){
            return "귀여운";
        }else if(characternum.equals("9")){
            return "열정적인";
        }else if(characternum.equals("10")){
            return "듬직한";
        }else if(characternum.equals("11")){
            return "개성있는";
        }else{
            return "-";
        }
    }
    //학생 학년 문자열로 변환
    public String studentage_replace(String subject){
        if(subject.equals("0")){
            return "사회인";
        }else if(subject.equals("1")){
            return "대학생";
        }else if(subject.equals("2")){
            return "n수생";
        }else if(subject.equals("3")){
            return "고3";
        }else if(subject.equals("4")){
            return "고2";
        }else if(subject.equals("5")){
            return "고1";
        }else if(subject.equals("6")){
            return "중3";
        }else if(subject.equals("7")){
            return "중2";
        }else if(subject.equals("8")){
            return "중1";
        }else if(subject.equals("9")){
            return "초6";
        }else if(subject.equals("10")){
            return "초5";
        }else if(subject.equals("11")){
            return "초4";
        }else if(subject.equals("12")){
            return "초3";
        }else if(subject.equals("13")){
            return "초2";
        }else if(subject.equals("14")){
            return "초1";
        }else{
            return "-";
        }
    }
    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void division() throws ParseException {

        oActivity = this;
        oContext = this;

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        //프로필 보기 부분 카테고리 보기 탭
        profiletype1 = (RadioButton) this.findViewById(R.id.profiletype1);
        profiletype2 = (RadioButton) this.findViewById(R.id.profiletype2);


        bottomlinear = (LinearLayout) findViewById(R.id.bottomlinear);
        likebtn = (ImageView) findViewById(R.id.likebtn);
        //liketext = (TextView) findViewById(R.id.liketext);

        classpaybottomtext = (TextView) findViewById(R.id.classpaybottomtext);
        requestclassbtn = (Button) findViewById(R.id.requestclassbtn);
        view5 = (View) findViewById(R.id.view5);


        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        SendType = intent.getExtras().getString("sendtype"); // 1. 나, 2. 다른유저

        //받은 데이터로 상대 유저와 내유저 접근을 구분한다.
        if(SendType.equals("1")){ //로그인한 내 유저 = 마이페이지에서 내 프로필 보기
            FixUid = Sessionlist.get(1).get(0);
            FixUsertype = Sessionlist.get(1).get(2);//학생, 선생님 구분

            likebtn.setVisibility(View.GONE);
            view5.setVisibility(View.GONE);
        }else if(SendType.equals("2")){ //다른 유저 = 과외찾기에서 선택한 프로필 보기
            SendUid = intent.getExtras().getString("senduid"); //프로필의 고유번호
            SendUsertype = intent.getExtras().getString("sendusertype"); //프로필의 유저 타입

            MyUid = Sessionlist.get(1).get(0);
            MyUsertype = Sessionlist.get(1).get(2);//학생, 선생님 구분
            FixUid = SendUid;
            FixUsertype = SendUsertype;

            //과외 찾기에서 내가 선생님일때와 학생일때 좋아요 버튼 보이기 안보이기
            if(MyUsertype.equals("1")){ // 선생님
                likebtn.setVisibility(View.GONE);
                view5.setVisibility(View.GONE);
            }else if(MyUsertype.equals("2")){ //학생
                likebtn.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
            }
        }
        Log.d("------------SendType-------------", String.valueOf(SendType));
        Log.d("-------------SendUid-------------",String.valueOf(SendUid));
        Log.d("-------------SendUsertype-------------",String.valueOf(SendUsertype));


        if(FixUsertype.equals("1")){ //선생님
            profiletype2.setVisibility(View.VISIBLE);
        }else{//학생
            profiletype2.setVisibility(View.GONE);
        }


        //좋아요 버튼
        likebtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                if(likenumtype == 0){ //좋아요 클릭
                    likenumtype = 1;
                    likebtn.setImageResource(R.drawable.hearts_chk);
                    likeadd();

                }else{ //좋아요 해제
                    likenumtype = 0;
                    likebtn.setImageResource(R.drawable.hearts_nchk);
                    likeadd();

                }
            }
        });

        profilemainimg = (ImageView) findViewById(R.id.profilemainimg);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.profiletype1:
                        Log.d("","프로필 보기");
                        try {
                            changeView(0);
                        } catch (ParseException | JSONException e) {
                            e.printStackTrace();
                        }
                        profilecate = "1";

                        profiletype1.setTextColor(getResources().getColorStateList(R.color.maincolor));
                        profiletype2.setTextColor(getResources().getColorStateList(R.color.black));

                        break;
                    case R.id.profiletype2:
                        Log.d("","과외후기");
                        try {
                            changeView(1);
                        } catch (ParseException | JSONException e) {
                            e.printStackTrace();
                        }
                        profilecate = "2";

                        profiletype1.setTextColor(getResources().getColorStateList(R.color.black));
                        profiletype2.setTextColor(getResources().getColorStateList(R.color.maincolor));
                        break;
                }
            }
        };
        profiletype1.setOnClickListener(clickListener);
        profiletype2.setOnClickListener(clickListener);


        //프로필 메인 이미지 클릭시 크게보기
        profilemainimg.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                Log.d("------profilemainimglist------------",String.valueOf(profilemainimglist.size()));
                if(profilemainimglist.size() != 0){


                //Log.d("------profilemainimglist------------",String.valueOf(profilemainimglist));
                //Log.d("------profilemainimglist------------",String.valueOf(profilemainimglist.get(0)));
                Intent intent = new Intent(Profileview.this, ImgbigsizeActivity.class);
                intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(profilemainimglist.get(0)));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        Profileview.this,
                        profilemainimg,
                        Objects.requireNonNull(ViewCompat.getTransitionName(profilemainimg)));
                startActivity(intent, options.toBundle());

                }
            }
        });


        //내 uid와 프로필 uid가 같지 않을때 만 보여짐
        if(SendType.equals("1")){
            requestclassbtn.setVisibility(View.GONE);
        }else{
            if(!Sessionlist.get(1).get(0).equals(SendUid)){
                requestclassbtn.setVisibility(View.VISIBLE);
            }else{
                requestclassbtn.setVisibility(View.GONE);
            }
        }


        //문의하기
        requestclassbtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                //채팅방 만드는자. 소켓을 연결한다.
                //db에 해당 방을 만들기!!!!!!!!
                //신청한 방이 있는지 체크해서 방이 있으면 그냥 방으로 이동함.

                //내 uid와 프로필 uid가 같지 않을때
                if(!Sessionlist.get(1).get(0).equals(SendUid)){
                    makerequestclassroom(); //과외 문의 방 생성
                }
            }
        });

        Reviewlist = new ArrayList<>();

        profilegetdata();
        profileimggetdata();

        if(FixUsertype.equals("1")) { //선생님
            getlikelist();
        }
    }

    //과외문의 방 생성
    public void makerequestclassroom(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 방 만들 사람 id, 방 요청 할 id, 내 닉네임 id
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0))); //방 만들 사람 id
        RequestBody requestuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUid)); //방 요청 할 id
        RequestBody roomname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(4))); //방 만들 사람 닉네임 - 방 이름이 될 것
        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        RequestBody participantnum = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(2)); //인원수 체크


        requestMap.put("myuid", myuid);
        requestMap.put("requestuid", requestuid);
        requestMap.put("roomname", roomname);
        requestMap.put("currenttime", currenttime_);
        requestMap.put("participantnum", participantnum);

        call = retrofitService.makerequestclassroom(
                5,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //리뷰 리스트 가져오기 - 선생님만
    public void getreviewlist(int limit, int offset){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - teacheruid
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody teacheruid = RequestBody.create(MediaType.parse("text/plain"), FixUid);
        requestMap.put("teacheruid", teacheruid);

        call = retrofitService.getreviewlist(
                limit,
                offset,
                6,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //프로필 정보 가져오는 서버통신
    public void profilegetdata() throws ParseException {

        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUid));
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUsertype));
        requestMap.put("uid", uid);
        requestMap.put("usertype", usertype);

        //과외 찾기에서 접근 2, 내 프로필에서 접근 1 / 2로 접근하면 조회수 1 상승하기
        if(SendType.equals("2")){

            int viewschk = 0; //조회수가 쉐어드에 저장되어있는지 여부
            oClassSearchviewsShared = new ClassSearchviewsShared(oContext);

            ArrayList<ArrayList<ArrayList<String>>>  Viewslist = oClassSearchviewsShared.AccountRead();
            //Log.d("*******Viewslist******", String.valueOf(Viewslist));
            for(int i = 0; i<Viewslist.size(); i++){

                Log.d("*******Viewslist******", String.valueOf(Viewslist.get(i).get(1).get(0)));

                //쉐어드에 게시글 nid 값이 존재한다면
                if(Viewslist.get(i).get(1).get(0).equals(FixUid)){

                    Log.d("*******Viewslist******", String.valueOf(Viewslist.get(i).get(1).get(1)));

                    String currenttime = Makecurrenttime();//현재시간 불러오기
                    String viewstime = String.valueOf(Viewslist.get(i).get(1).get(1));//저장된 조회수 시간

                    Log.d("*******Viewslist****** currenttime : ", currenttime);
                    Log.d("*******Viewslist****** viewstime : ", viewstime);
                    Date dt1 = timeFormat.parse(currenttime); //현재시간
                    Date dt2 = timeFormat.parse(viewstime); //현재시간
                    long diff = dt1.getTime() - dt2.getTime();
                    long diffhour = diff /  (3600000); // 시간 구하기

                    Log.d("*******Viewslist****** result : ", String.valueOf(diffhour));

                    if(diffhour < 24){ // 24시간이 넘지 않음
                        viewschk = -1; //조회수 시간이 아직 남아 있음.
                    }else{ // 24시간 넘음 갱신
                        viewschk = 2; //하루가 넘었고 갱신이 필요
                        oClassSearchviewsShared.Deleteone(String.valueOf(Viewslist.get(i).get(0).get(0)));

                        //쉐어드에 저장하기
                        oClassSearchviewsShared.SaveViewsobject(FixUid);
                        RequestBody viewsup = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));
                        requestMap.put("viewsup", viewsup);
                    }
                }
            }


            if(viewschk == 0){
                //쉐어드에 저장하기
                oClassSearchviewsShared.SaveViewsobject(FixUid);
                RequestBody viewsup = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));
                requestMap.put("viewsup", viewsup);

            }
        }


        call = retrofitService.profilegetlist(
                1, requestMap
        );
        RestapiResponse(); //응답
    }

    //프로필 이미지 가져오는 서버통신
    public void profileimggetdata(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUid));
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUsertype));

        requestMap.put("uid", uid);
        requestMap.put("usertype", usertype);

        call = retrofitService.profilegetimglist(
                2, requestMap
        );
        RestapiResponse(); //응답
    }

    //좋아요 리스트 가져오기
    public void getlikelist(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 클릭한 자, 클릭받은자, 좋아요 안좋아요 타입(좋아요하면 데이터 추가, 안좋아요하면 데이터 삭제)
        HashMap<String, RequestBody> requestMap = new HashMap<>();
       // RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        RequestBody getlikeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUid));

        //requestMap.put("uid", uid);
        requestMap.put("getlikeuid", getlikeuid);

        call = retrofitService.likeaddlist(
                4, requestMap
        );
        RestapiResponse(); //응답
    }


    //좋아요 추가 함수
    public void likeadd(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 클릭한 자, 클릭받은자, 좋아요 안좋아요 타입(좋아요하면 데이터 추가, 안좋아요하면 데이터 삭제)
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        RequestBody getlikeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUid));
        RequestBody liketype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(likenumtype));

        requestMap.put("uid", uid);
        requestMap.put("getlikeuid", getlikeuid);
        requestMap.put("liketype", liketype);

        call = retrofitService.likeaddbtn(
                3, requestMap
        );
        RestapiResponse(); //응답
    }

    @Override
    //편집 클릭 후 리턴되는 값을 인지한다.
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULTCODE1) { //편집 클릭후 완료인지 확인하는 부분

                String completeval = resultIntent.getStringExtra("completeval");
                Log.d("완료여부 ", completeval);
                if(completeval.equals("1")){ //편집 수정을 했을때만 서버 통신으로 다시 불러옴
                    try {
                        profilegetdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    profileimggetdata();

                    Toast myToast = Toast.makeText(this.getApplicationContext(), "프로필 데이터가 저장되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();

                }
            }
        }
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
