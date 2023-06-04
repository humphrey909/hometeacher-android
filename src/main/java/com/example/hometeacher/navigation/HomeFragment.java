package com.example.hometeacher.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.CategoreyRecyclerAdapter;
import com.example.hometeacher.Adapter.NboardRecyclerAdapter;
import com.example.hometeacher.Alertpage;
import com.example.hometeacher.ArraylistForm.CategoreyForm;
import com.example.hometeacher.SearchBox.CurrentSearchpage;
import com.example.hometeacher.MainActivity;
import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.Nboard.Nboardwrite;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.shared.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class HomeFragment extends Fragment {

    MainActivity mainActivity;

    Session oSession; //자동로그인을 위한 db
    ArrayList<ArrayList<String>> Sessionlist;
    Context oContext;
    Toolbar appbar;

    View view;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    String MyUsertype;
    ArrayList<String> Maincategoreylist;
    ArrayList<CategoreyForm> Subcategoreylist;
    int MainCategoreyType = 0; //대분류 카테고리 타입.
    ArrayList<String> SearchSubcategorey; //선택한 서브카테고리

    ImageButton nboardcategoreybtn;
    TextView nboardcategoreytext;
    ImageButton.OnClickListener clickListener;

    RecyclerView NboardcategoreyRecyclerView, NboardRecyclerView;

    FloatingActionButton nboardwritebtn;

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    ArrayList<JSONObject> Nboardlist; //게시판 전체 리스트

//    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int pagenum = 0;
    int limitnum = 10;

    Intent intent;

    int AlertCount;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    //int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity) getActivity()).Firstaccesschk(1);


        division();

        return view;
    }

    public void division(){
        setHasOptionsMenu(true);

        oContext = view.getContext();
        oSession = new Session(oContext);
        //자동 로그인 하기
        Sessionlist = oSession.Getoneinfo("0");

        //툴바 설정
        appbar = (Toolbar) view.findViewById(R.id.toolbarbox);

        nboardcategoreybtn = (ImageButton) view.findViewById(R.id.nboardcategoreybtn);
        nboardcategoreytext = (TextView) view.findViewById(R.id.nboardcategoreytext);

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        nboardwritebtn = (FloatingActionButton) view.findViewById(R.id.nboardwritebtn);

        SearchSubcategorey = new ArrayList<>(); //선택한 서브 카테고리 선언

        Maincategoreylist= new ArrayList<>();
        if(!Sessionlist.isEmpty()) { //세션값이 있을때만 적용
            // Log.d("-------------MyUsertype------------","MyUsertype");

            MyUsertype = Sessionlist.get(1).get(2);//학생, 선생님 구분
            //Log.d("-------------MyUsertype------------",MyUsertype);
            if (MyUsertype.equals("1")) { //선생님 - 멘토링, 선생님 토론

                Maincategoreylist.add("멘토링");
                Maincategoreylist.add("선생님 토론");

                CategoreySeletbtn(1);

                if(MainCategoreyType == 0){ //멘토링
                    nboardwritebtn.setVisibility(View.GONE);
                    nboardcategoreytext.setText("멘토링");
                    GetCategoreylist(1);
                }else if(MainCategoreyType == 1){ //선생님토론
                    nboardwritebtn.setVisibility(View.VISIBLE);
                    nboardcategoreytext.setText("선생님 토론");
                    GetCategoreylist(2);
                }
            } else { //학생 - 멘토링
                //카테고리 리스트 만들기
                Maincategoreylist.add("멘토링");

                CategoreySeletbtn(2);

                if(MainCategoreyType == 0){
                    nboardcategoreytext.setText("멘토링");
                    GetCategoreylist(1);
                    nboardwritebtn.setVisibility(View.VISIBLE);
                }
            }
        }else{ //세션값이 없을때

            Maincategoreylist.add("멘토링");

            CategoreySeletbtn(0);

            if(MainCategoreyType == 0){
                nboardcategoreytext.setText("멘토링");
                GetCategoreylist(1);
                nboardwritebtn.setVisibility(View.GONE);
            }
        }

        pagenum = 0; //페이징 초기화
        Nboardlist = new ArrayList<>();
        GetNboardlist(limitnum, 0);




        //새글 작성 버튼
        nboardwritebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Nboardwrite.class);
                intent.putExtra("MainCategoreyType", MainCategoreyType); //대분류 타입
                intent.putExtra("type", 1); //1 추가, 2 수정
                startActivityForResult(intent, REQUESTCODE);
            }
        });

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

                    Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                    GetNboardlist(limitnum, offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));

                }
            }
        });

    }

    //최상단으로 올라감
    public void upscroll(){
        nestedScrollView.pageScroll(View.FOCUS_UP);
        nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
    }


    //카테고리 선택시 나타는 다이얼로그
    public void CategoreySeletbtn(int type){

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                builder.setTitle("카테고리 선택");

                builder.setItems(Maincategoreylist.toArray(new String[0]), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        Log.d("pos", String.valueOf(pos));
                        MainCategoreyType = pos;

                        if(type == 0){//비로그인
                                pagenum = 0; //페이징 초기화
                                SearchSubcategorey.clear(); //서브카테고리 초기화
                                nboardcategoreytext.setText("멘토링");
                                GetCategoreylist(1);
                                Nboardlist.clear();
                                GetNboardlist(limitnum, 0);
                        }else if(type == 1) { //로그인 - 선생님
                            if(pos == 0){ //멘토링
                                pagenum = 0; //페이징 초기화
                                SearchSubcategorey.clear(); //서브카테고리 초기화
                                nboardcategoreytext.setText("멘토링");
                                GetCategoreylist(1);
                                Nboardlist.clear();
                                GetNboardlist(limitnum, 0);
                                nboardwritebtn.setVisibility(View.GONE);

                            }else{ //선생님 토론
                                pagenum = 0; //페이징 초기화
                                SearchSubcategorey.clear(); //서브카테고리 초기화
                                nboardcategoreytext.setText("선생님 토론");
                                GetCategoreylist(2);
                                Nboardlist.clear();
                                GetNboardlist(limitnum, 0);
                                nboardwritebtn.setVisibility(View.VISIBLE);

                            }
                        }else{ //로그인 - 학생
                            pagenum = 0; //페이징 초기화
                            SearchSubcategorey.clear(); //서브카테고리 초기화
                            nboardcategoreytext.setText("멘토링");
                            GetCategoreylist(1);
                            Nboardlist.clear();
                            GetNboardlist(limitnum, 0);

                        }

                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        };
        nboardcategoreybtn.setOnClickListener(clickListener);
        nboardcategoreytext.setOnClickListener(clickListener);


    }


    public void GetCategoreylist(int type) {
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody maincategoreytype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(type));
        requestMap.put("maincategoreytype", maincategoreytype);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.nboardcategoreylist(
                1, requestMap
        );
        RestapiResponse(); //응답
    }

    //게시물 리스트
    public void GetNboardlist(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody maincategorey = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(MainCategoreyType));
        requestMap.put("maincategorey", maincategorey);


        Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey));
        //서브카테고리 전송
        if(!SearchSubcategorey.isEmpty()){
            RequestBody subcategorey = RequestBody.create(MediaType.parse("text/plain"), SearchSubcategorey.get(0));
            requestMap.put("subcategorey", subcategorey);
        }else{
            //암것도 안보내면 null로 받음
        }

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getnboardlist(
                limit,
                offset,
                2,
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

                    if (urlget2.equals("1")) { //카테고리 리스트
                        Log.d("onResponse ? ", "게시판 카테고리 리스트 리스트 : " + resultlist);

                        if(resultlist.length() > 2) { //받아온 데이터가 없으면 진행 x

                            try {
                                Subcategoreylist = new ArrayList<>();

                                //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                                JSONArray jarray = new JSONArray(resultlist);
                                Log.d("onResponse ? ", "게시판 카테고리 리스트 : " + String.valueOf(jarray));
                                // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                                if (jarray.get(0) != null) {

                                    for (int i = 0; i < jarray.length(); i++) {
                                        JSONObject jobj = jarray.getJSONObject(i);
                                        //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                        // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));


                                        CategoreyForm CategoreyForm = new CategoreyForm(jobj.get("name").toString(), false);
                                        Subcategoreylist.add(i, CategoreyForm);
                                        //jsonobject형식으로 데이터를 저장한다.
                                        // Subcategoreylist.add(tempJson);
                                    }

                                    //object형식으로 arraylist를 만든것
                                    Log.d("onResponse ? ", "게시판 카테고리 리스트 : " + String.valueOf(Subcategoreylist));


                                    MakeCategoreyrecycle(Subcategoreylist);


                                    Log.d("json 파싱", "게시판 카테고리 리스트 가져오기 성공");

                                } else {
                                    Log.d("json 파싱", "게시판 카테고리 리스트 가져오기 실패");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }else if(urlget2.equals("2")) { //게시판 리스트
                        progressBar.setVisibility(View.GONE);

                        Log.d("onResponse ? ", "게시글 리스트 : " + resultlist);
                        Log.d("onResponse ? ", "게시글 리스트 : " + resultlist.length());

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.length()));
                           // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                           // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Nboardlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "게시글 정보들 : all" + String.valueOf(Nboardlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeNboardrecycle(Nboardlist);
                    }else if(urlget2.equals("3")) { //알림 체크하는 부분
                        Log.d("onResponse ? ", "알림 체크하는 부분 : " + resultlist);

                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","알림 체크하는 부분  : " + String.valueOf(jobj.get("count")));

                            AlertCount = Integer.parseInt(String.valueOf(jobj.get("count")));
                            if(AlertCount == 0){
                                appbar.getMenu().clear();
                                appbar.inflateMenu(R.menu.nboardalertmenu);
                            }else{
                                appbar.getMenu().clear();
                                appbar.inflateMenu(R.menu.nboardalertmenu_ck);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //통신 실패
                    Log.d("onResponse ? ", "onResponse 실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //통신 실패
                Log.d("onFailure", t.getMessage());
            }
        });
    }

    //게시글 리스트를 만든다.
    public void MakeNboardrecycle(ArrayList<JSONObject> Nboardlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Nboardlistjsonarray));

        NboardRecyclerView = (RecyclerView) view.findViewById(R.id.NboardRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity()); //그리드 매니저 선언
        NboardRecyclerAdapter NboardAdapter = new NboardRecyclerAdapter(mainActivity.getApplicationContext(), mainActivity); //내가만든 어댑터 선언
        NboardRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        NboardAdapter.setSessionval(Sessionlist); //arraylist 연결
        NboardAdapter.setRecycleList(Nboardlistjsonarray); //arraylist 연결
        // WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        NboardRecyclerView.setAdapter(NboardAdapter); //리사이클러뷰 위치에 어답터 세팅





        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        NboardAdapter.setOnItemClickListener(new NboardRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list) {


                //클릭시 조회수 업. 쉐어드 사용하여 시간 체크할 것,


                Intent intent = new Intent(getActivity(), Nboardview.class);
                try {
                  //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
                    intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
                    intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, REQUESTCODE);
            }
        });

    }

    //카테고리 리스트를 만든다.
    public void MakeCategoreyrecycle(ArrayList<CategoreyForm> categoreylistjsonarray){
        Log.d("유저 리스트 이다.", String.valueOf(categoreylistjsonarray));

        NboardcategoreyRecyclerView = (RecyclerView) view.findViewById(R.id.NboardcategoreyRecyclerView);
        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false); //가로일때
        CategoreyRecyclerAdapter CategoreyRecyclerAdapter = new CategoreyRecyclerAdapter(mainActivity.getApplicationContext()); //내가만든 어댑터 선언
        NboardcategoreyRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

       // CategoreyRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        CategoreyRecyclerAdapter.setRecycleList(categoreylistjsonarray); //arraylist 연결
        // WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        NboardcategoreyRecyclerView.setAdapter(CategoreyRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        CategoreyRecyclerAdapter.setOnItemClickListener(new CategoreyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) throws JSONException {

                //선택한 리스트 만들기
                SearchSubcategorey.clear(); //선택한 서브 카테고리 선언

                for(int i = 0; i<Subcategoreylist.size();i++){
                    if(Subcategoreylist.get(i).isSelected()){ //true인것만
                        SearchSubcategorey.add(String.valueOf(i));
                    }
                }

                Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey));
                Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                //페이징, 리스트 초기화 후 조건에 맞게 다시 불러옴
                pagenum = 0; //페이징 초기화
                Nboardlist.clear();
                GetNboardlist(limitnum, 0);
            }
        });

    }


    //toolbar 변경할때 사용 - 서비스에서 알림이 오면 변경함
    public void ChangeToolbarmenu(){
        appbar.getMenu().clear();
        appbar.inflateMenu(R.menu.nboardalertmenu_ck);
    }


    //fragment에 menu 추가
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //click이 하나라도 잇으면 알림이 있다는 것을 표시해 줄것.
       // if(AlertCount == 0){
            appbar.inflateMenu(R.menu.nboardalertmenu);
       // }else{
        //    appbar.inflateMenu(R.menu.nboardalertmenu_ck);
       // }


        appbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.alerttab:
                    intent = new Intent(getActivity(), Alertpage.class);
                    startActivity(intent);
                    //
                    // Navigate to settings screen
                    return true;

                case R.id.searchtab:
                    intent = new Intent(getActivity(), CurrentSearchpage.class);
                    intent.putExtra("type",  "nboard");
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    //편집 클릭 후 리턴되는 값을 인지한다.
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        Log.d("completeval 여부 ", "completeval");
        Log.d("completeval 여부 ", String.valueOf(requestCode));
        Log.d("completeval 여부 ", String.valueOf(resultCode));

        // String backval = resultIntent.getStringExtra("backval");
        // Log.d("완료여부 ", backval);


        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULTCODE1) { //편집 클릭후 완료인지 확인하는 부분

                String completeval = resultIntent.getStringExtra("completeval");
                Log.d("완료 여부 ", completeval);


                if(completeval.equals("1")){ //nboardwrite에서 완료버튼 했을때
                    Toast myToast = Toast.makeText(getContext(), "게시물이 추가되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();

                    pagenum = 0; //페이징 초기화
                    Nboardlist.clear();

                    GetNboardlist(limitnum, 0);

                }else if(completeval.equals("0")){ // 0 nboardview에서 뒤로가기 했을때
                  //  Toast myToast = Toast.makeText(getContext(), "게시물이 1111.", Toast.LENGTH_SHORT);
                   // myToast.show();

                    pagenum = 0; //페이징 초기화
                    Nboardlist.clear();

                    GetNboardlist(limitnum, 0);

                }else if(completeval.equals("2")){ // 삭제시
                    Toast myToast = Toast.makeText(getContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();

                    pagenum = 0; //페이징 초기화
                    Nboardlist.clear();

                    GetNboardlist(limitnum, 0);
                }
            }
        }
    }

    //알림 리스트 가져오기 - alertuid 를 내 uid로 참조
    public void getalertcount() {

        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid); // alertuid 를 내 uid로 참조
        RequestBody click = RequestBody.create(MediaType.parse("text/plain"), "1");
        requestMap.put("click", click); // alertuid 를 내 uid로 참조

        //해당 게시물 정보를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getalertcount(
                0,
                0,
                3,
                requestMap
        );
        RestapiResponse(); //응답
    }


    @Override
    public void onResume() {
        super.onResume();

        if(!Sessionlist.isEmpty()) { //세션값이 있을때만 적용
            getalertcount(); //알림 데이터 확인
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mainActivity = (MainActivity) getActivity();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        mainActivity = null;
    }

//    //현재시간을 생성한다.
//    public String Makecurrenttime(){
//
//        Date todaydate = new Date();
//        Log.d("test 현재 시간", String.valueOf(todaydate));
//        String todaytime = timeFormat.format(todaydate);
//        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
//        return todaytime;
//    }
}