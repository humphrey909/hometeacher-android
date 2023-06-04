package com.example.hometeacher.navigation;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.hometeacher.Adapter.SubjectRecyclerAdapter;
import com.example.hometeacher.Adapter.UserRecyclerAdapter;
import com.example.hometeacher.Alertpage;
import com.example.hometeacher.ArraylistForm.SubjectForm;
import com.example.hometeacher.SearchBox.CurrentSearchpage;
import com.example.hometeacher.MainActivity;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
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

public class SearchclassFragment extends Fragment {

    MainActivity mainActivity;

    Session oSession; //자동로그인을 위한 db
    Context oContext;
    Toolbar appbar;

    View view;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    RadioButton.OnClickListener clickListener;

    ArrayList<JSONObject> Userlist; //회원 전체 리스트
    ArrayList<SubjectForm> Subjectlist;
    ArrayList<String> SelectSubjectlist; //선택된 과목 리스트 - 검색하기위함

    RecyclerView UserRecyclerView, SubjectRecyclerView;

    ArrayList<ArrayList<String>> Sessionlist;
    String MyUsertype;

    ImageButton classsearchcategoreybtn;
    TextView classsearchcategoreytext;
    ImageButton.OnClickListener clickListenerImage;

    ArrayList<String> categoreylist; //카테고리 선택리스트 만들기

    Intent intent;
    int SelectType = 0; //선택된 메인 카테고리 타입
    int divisionlikelistchk = 0;

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int pagenum = 0;
    int limitnum = 10;

    int AlertCount;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SearchclassFragment() {
        // Required empty public constructor
    }

    public static SearchclassFragment newInstance(String param1, String param2) {
        SearchclassFragment fragment = new SearchclassFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_searchclass, container, false);

        ((MainActivity) getActivity()).Firstaccesschk(2);
        division();

        return view;
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

                    if (urlget2.equals("1")) { //회원 리스트
                        Log.d("onResponse ? ", "onResponse 회원 리스트 : " + resultlist);

                        progressBar.setVisibility(View.GONE);

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
                    }else if(urlget2.equals("2")){ //과목 카테고리 리스트 가져오기
                        Log.d("onResponse ? ", "과목 카테고리 리스트!!!");
                        Log.d("onResponse ? ", "과목 카테고리 리스트!!! : " + resultlist);

                        if(resultlist.length() > 2) { //받아온 데이터가 없으면 진행 x
                            try {
                                Subjectlist = new ArrayList<>();

                                //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                                JSONArray jarray = new JSONArray(resultlist);
                                Log.d("onResponse ? ", "onResponse 과목 카테고리 리스트 : " + String.valueOf(jarray));
                                if (jarray.get(0) != null) {

                                    for (int i = 0; i < jarray.length(); i++) {
                                        JSONObject jobj = jarray.getJSONObject(i);
                                        //jsonobject형식으로 데이터를 저장한다.
                                        SubjectForm SubjectForm = new SubjectForm(jobj.get("subjectgroup").toString(), jobj.get("subjectname").toString(), false);
                                        Subjectlist.add(i, SubjectForm);

                                        //Subjectlist.add(jobj);
                                    }

                                    //object형식으로 arraylist를 만든것
                                    Log.d("onResponse ? ", "onResponse 과목 카테고리 리스트 : " + String.valueOf(Subjectlist));

                                    //데이터를 바로 출력시킬것
                                    Subjectrecycle(Subjectlist);

                                    Log.d("json 파싱", "프로필 데이터 가져오기 성공");

                                } else {
                                    Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }else if(urlget2.equals("3")){ //내가 찜한 리스트
                        Log.d("onResponse ? ", "좋아요 유저 리스트!!! : " + resultlist);
                        progressBar.setVisibility(View.GONE);

                        if(resultlist.length() > 2){

                        try {
//                            Userlist = new ArrayList<>();
                            Userlist.clear();

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

                        }else{ //좋아요한 데이터가 없을때
                            Userlist.clear();
                            MakeUserrecycle(Userlist);
                        }
                    }else if(urlget2.equals("4")){
                        Log.d("onResponse ? ", "알림 체크하는 부분 : " + resultlist);

                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","알림 체크하는 부분  : " + String.valueOf(jobj.get("count")));

                            AlertCount = Integer.parseInt(String.valueOf(jobj.get("count")));
                            if(AlertCount == 0){
                                appbar.getMenu().clear();
                                appbar.inflateMenu(R.menu.searchalertmenu);
                            }else{
                                appbar.getMenu().clear();
                                appbar.inflateMenu(R.menu.searchalertmenu_ck);
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

    //유저 리스트를 만든다
    public void MakeUserrecycle(ArrayList<JSONObject> Userlistjsonarray){
        Log.d("유저 리스트 이다.", String.valueOf(Userlistjsonarray));

        UserRecyclerView = (RecyclerView) view.findViewById(R.id.UserRecyclerView);
        GridLayoutManager GridlayoutManager = new GridLayoutManager(getActivity(), 2); //그리드 매니저 선언
        UserRecyclerAdapter UserAdapter = new UserRecyclerAdapter(mainActivity.getApplicationContext()); //내가만든 어댑터 선언
        UserRecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        UserAdapter.setSessionval(Sessionlist, divisionlikelistchk); //arraylist 연결
        UserAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        // WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        UserRecyclerView.setAdapter(UserAdapter); //리사이클러뷰 위치에 어답터 세팅

    }


    public void Subjectrecycle(ArrayList<SubjectForm> Subjectjsonarray){
        Log.d("과목 리스트 이다.", String.valueOf(Subjectjsonarray));

        SubjectRecyclerView = (RecyclerView) view.findViewById(R.id.SubjectRecyclerView);
        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false); //가로일때
        SubjectRecyclerAdapter SubjectAdapter = new SubjectRecyclerAdapter(mainActivity.getApplicationContext()); //내가만든 어댑터 선언
        SubjectRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        SubjectAdapter.setlisttype(2); //arraylist 연결
        SubjectAdapter.setRecycleList(Subjectjsonarray); //arraylist 연결
        //WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        SubjectRecyclerView.setAdapter(SubjectAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        SubjectAdapter.setOnItemClickListener(new SubjectRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SelectSubjectlist.clear();

                //Log.d("tag", String.valueOf(Subjectlist));
                //Log.d("tag", String.valueOf(Subjectlist));
                for(int i = 0; i<Subjectlist.size();i++){
                    // Log.d("Characterlist", String.valueOf(Subjectlist.get(i).getName()));
                    //Log.d("Characterlist", String.valueOf(Subjectlist.get(i).isSelected()));

                    if(Subjectlist.get(i).isSelected()){ //true인것만
                        SelectSubjectlist.add(String.valueOf(i));
                    }
                }

                Log.d("과목 선택 리스트 - db", String.valueOf(SelectSubjectlist));
                Log.d("과목 선택 리스트 - db", String.valueOf(SelectSubjectlist.isEmpty()));

                pagenum = 0;
                Userlist.clear();

                //과목 클릭시 체크한 과목에 맞게 리사이클러뷰를 불러온다.
                if(!Sessionlist.isEmpty()) { //로그인 상태
                    if (MyUsertype.equals("1")) { //선생님
                        if(SelectType == 0){
                            //classsearchcategoreytext.setText("학생 찾기");
                            GetUserlist(2,limitnum, 0);
                        }else if(SelectType == 1){
                            //classsearchcategoreytext.setText("선생님 찾기");
                            GetUserlist(1,limitnum, 0);
                        }else if(SelectType == 2){
                            //classsearchcategoreytext.setText("나를 찜한 학생");
                            GetLikeuserlist(limitnum, 0);
                        }else if(SelectType == 3){
                           // classsearchcategoreytext.setText("선생님 랭킹");
                        }
                    } else { //학생
                        if(SelectType == 0){
                            //classsearchcategoreytext.setText("선생님 찾기");
                            GetUserlist(1,limitnum, 0);
                        }else if(SelectType == 1){
                            //classsearchcategoreytext.setText("찜한 선생님 목록");
                            GetLikeuserlist(limitnum, 0);
                        }else if(SelectType == 3){
                            //classsearchcategoreytext.setText("선생님 랭킹");
                        }
                    }
                }else{ //로그아웃 상태
                    if(SelectType == 0){
                        //classsearchcategoreytext.setText("선생님 찾기");
                        GetUserlist(1,limitnum, 0);
                    }else if(SelectType == 1){
                        //classsearchcategoreytext.setText("학생 찾기");
                        GetUserlist(2,limitnum, 0);
                    }else if(SelectType == 2){
                        //classsearchcategoreytext.setText("선생님 랭킹");
                    }
                }
            }
        });
    }

    //fragment에 menu 추가
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        appbar.inflateMenu(R.menu.searchalertmenu);

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
                    intent.putExtra("type",  "searchclass");
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });
    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void division() {
        setHasOptionsMenu(true);
        //툴바 설정
        appbar = (Toolbar) view.findViewById(R.id.toolbarbox);

        oContext = view.getContext();
        oSession = new Session(oContext);

        classsearchcategoreybtn = (ImageButton) view.findViewById(R.id.classsearchcategoreybtn);
        classsearchcategoreytext = (TextView) view.findViewById(R.id.classsearchcategoreytext);

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        //자동 로그인 하기
        Sessionlist = oSession.Getoneinfo("0");
        Log.d("-------------MyUsertype------------",String.valueOf(Sessionlist));

        pagenum = 0;

        //유저 arraylist 선언
        Userlist = new ArrayList<>();
        SelectSubjectlist = new ArrayList<>();//과목 선택 리스트 선언

        //과목 리스트를 불러옴.
        GetSubjectlist();


        categoreylist= new ArrayList<>();
       if(!Sessionlist.isEmpty()) { //세션값이 있을때만 적용
           // Log.d("-------------MyUsertype------------","MyUsertype");

           MyUsertype = Sessionlist.get(1).get(2);//학생, 선생님 구분
           //Log.d("-------------MyUsertype------------",MyUsertype);
           if (MyUsertype.equals("1")) { //선생님이면 학생 선생님 둘다 보임
               categoreylist.add("학생 찾기");
               categoreylist.add("선생님 찾기");
               categoreylist.add("나를 찜한 학생");
               //categoreylist.add("선생님 랭킹");

               CategoreySeletbtn(1);

               if(SelectType == 0){
                   classsearchcategoreytext.setText("학생 찾기");
                   GetUserlist(2,limitnum, 0);
               }else if(SelectType == 1){
                   classsearchcategoreytext.setText("선생님 찾기");
                   GetUserlist(1,limitnum, 0);
               }else if(SelectType == 2){
                   classsearchcategoreytext.setText("나를 찜한 학생");
                   GetLikeuserlist(limitnum, 0);
               }else if(SelectType == 3){
                  // classsearchcategoreytext.setText("선생님 랭킹");
               }
           } else { //학생이면 선생님만
               //카테고리 리스트 만들기
              // categoreylist = new ArrayList<>();
               categoreylist.add("선생님 찾기");
               categoreylist.add("찜한 선생님 목록");
             //  categoreylist.add("선생님 랭킹");

               CategoreySeletbtn(2);

               if(SelectType == 0){
                   classsearchcategoreytext.setText("선생님 찾기");
                   GetUserlist(1,limitnum, 0);
               }else if(SelectType == 1){
                   classsearchcategoreytext.setText("찜한 선생님 목록");
                   GetLikeuserlist(limitnum, 0);
               }else if(SelectType == 3){
                 //  classsearchcategoreytext.setText("선생님 랭킹");
               }
           }
       }else{ //세션값이 없을때

           categoreylist.add("선생님 찾기");
           categoreylist.add("학생 찾기");
         //  categoreylist.add("선생님 랭킹");

           CategoreySeletbtn(0);

           if(SelectType == 0){
               classsearchcategoreytext.setText("선생님 찾기");
               GetUserlist(1,limitnum, 0);
           }else if(SelectType == 1){
               classsearchcategoreytext.setText("학생 찾기");
               GetUserlist(2,limitnum, 0);
           }else if(SelectType == 2){
           //    classsearchcategoreytext.setText("선생님 랭킹");
           }
       }


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

                    Log.d("카테고리 선택 리스트 - db", String.valueOf(SelectSubjectlist.isEmpty()));

                    //과목 클릭시 체크한 과목에 맞게 리사이클러뷰를 불러온다.
                    if(!Sessionlist.isEmpty()) { //로그인 상태
                        if (MyUsertype.equals("1")) { //선생님
                            if(SelectType == 0){
                                //classsearchcategoreytext.setText("학생 찾기");
                                GetUserlist(2,limitnum, offsetnum);
                            }else if(SelectType == 1){
                                //classsearchcategoreytext.setText("선생님 찾기");
                                GetUserlist(1,limitnum, offsetnum);
                            }else if(SelectType == 2){
                                //classsearchcategoreytext.setText("나를 찜한 학생");
                                GetLikeuserlist(limitnum, offsetnum);
                            }else if(SelectType == 3){
                                // classsearchcategoreytext.setText("선생님 랭킹");
                            }
                        } else { //학생
                            if(SelectType == 0){
                                //classsearchcategoreytext.setText("선생님 찾기");
                                GetUserlist(1,limitnum, offsetnum);
                            }else if(SelectType == 1){
                                //classsearchcategoreytext.setText("찜한 선생님 목록");
                                GetLikeuserlist(limitnum, offsetnum);
                            }else if(SelectType == 3){
                                //classsearchcategoreytext.setText("선생님 랭킹");
                            }
                        }
                    }else{ //로그아웃 상태
                        if(SelectType == 0){
                            //classsearchcategoreytext.setText("선생님 찾기");
                            GetUserlist(1,limitnum, offsetnum);
                        }else if(SelectType == 1){
                            //classsearchcategoreytext.setText("학생 찾기");
                            GetUserlist(2,limitnum, offsetnum);
                        }else if(SelectType == 2){
                            //classsearchcategoreytext.setText("선생님 랭킹");
                        }
                    }

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

        //카테고리 선택시 나타는 다이얼로그
        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                builder.setTitle("카테고리 선택");

                builder.setItems(categoreylist.toArray(new String[0]), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        Log.d("pos", String.valueOf(pos));

                        SelectType = pos;

                        pagenum = 0; //페이징 초기화
                        GetSubjectlist(); //과목초기화
                        Userlist.clear(); //유저 초기화
                        SelectSubjectlist.clear(); //선택과목 초기화

                        if(type == 0){ //비로그인
                            if (pos == 0) { //학생찾기
                                classsearchcategoreytext.setText("선생님 찾기");
                                GetUserlist(1,limitnum, 0);
                                divisionlikelistchk = 0;
                                //Log.d("pos 선생님", String.valueOf(pos));
                            }else if(pos == 1){ //선생님찾기
                                //Log.d("pos 학생", String.valueOf(pos));
                                classsearchcategoreytext.setText("학생 찾기");
                                GetUserlist(2,limitnum, 0);

                                divisionlikelistchk = 0;
                            }else if(pos == 2){//선생님랭킹
                                classsearchcategoreytext.setText("선생님 랭킹");

                                divisionlikelistchk = 0;
                            }
                        }else if(type == 1){ //로그인 - 선생님
                            if (pos == 0) { //학생찾기
                                //Log.d("pos 선생님", String.valueOf(pos));
                                classsearchcategoreytext.setText("학생 찾기");
                                GetUserlist(2,limitnum, 0);

                                divisionlikelistchk = 0;
                            }else if(pos == 1){ //선생님찾기
                                //Log.d("pos 학생", String.valueOf(pos));
                                classsearchcategoreytext.setText("선생님 찾기");
                                GetUserlist(1,limitnum, 0);

                                divisionlikelistchk = 0;
                            }else if(pos == 2){//나를찜한학생
                                divisionlikelistchk = 1;
                                classsearchcategoreytext.setText("나를 찜한 학생");
                                GetLikeuserlist(limitnum, 0);


                            }else if(pos == 3){//선생님랭킹
                                classsearchcategoreytext.setText("선생님 랭킹");
                                divisionlikelistchk = 0;
                            }
                        }else{ //2 //로그인 - 학생
                            if (pos == 0) { //선생님찾기
                                GetUserlist(1,limitnum, 0);
                                //Log.d("pos 선생님", String.valueOf(pos));
                                classsearchcategoreytext.setText("선생님 찾기");

                                divisionlikelistchk = 0;
                            }else if(pos == 1){ //찜한선생님리스트
                                //Log.d("pos 학생", String.valueOf(pos));
                                classsearchcategoreytext.setText("찜한 선생님 목록");
                                GetLikeuserlist(limitnum, 0);

                                divisionlikelistchk = 1;
                            }else if(pos == 2){//선생님랭킹
                                classsearchcategoreytext.setText("선생님 랭킹");
                                divisionlikelistchk = 0;
                            }
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        };
        classsearchcategoreybtn.setOnClickListener(clickListener);
        classsearchcategoreytext.setOnClickListener(clickListener);


    }

    //유저리스트 / type = 1 선생님, type = 2 학생
    public void GetUserlist(int type, int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(type));
        requestMap.put("usertype", usertype);


        //과목 선택이 없으면 전체 리스트, 선택한 과목이 있으면 선택된 과목만
        if(!SelectSubjectlist.isEmpty()){
            RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectSubjectlist));
            requestMap.put("subjectlist", subjectlist);
        }else{
            //암것도 안보내면 null로 받음
        }

        Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf(SelectSubjectlist));

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


    //과목리스트
    public void GetSubjectlist(){

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody catetype = RequestBody.create(MediaType.parse("text/plain"), "2");
        requestMap.put("catetype", catetype);

        //과목 카테고리 정보 가져오는 부분
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.categoreylist(
                2, requestMap
        );
        RestapiResponse(); //응답

    }

    //찜한 목록
    public void GetLikeuserlist(int limit, int offset){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 클릭한 자, 클릭받은자, 좋아요 안좋아요 타입(좋아요하면 데이터 추가, 안좋아요하면 데이터 삭제)
        HashMap<String, RequestBody> requestMap = new HashMap<>();
         RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        //RequestBody getlikeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(FixUid));
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(2)));

        requestMap.put("uid", uid);
        //requestMap.put("getlikeuid", getlikeuid);
        requestMap.put("usertype", usertype);


        //과목 선택이 없으면 전체 리스트, 선택한 과목이 있으면 선택된 과목만
        if(!SelectSubjectlist.isEmpty()){
            RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectSubjectlist));
            requestMap.put("subjectlist", subjectlist);
        }else{
            //암것도 안보내면 null로 받음
        }


        call = retrofitService.likeadduserjoinlist(
                limit,
                offset,
                3,
                requestMap
        );
        RestapiResponse(); //응답
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
                4,
                requestMap
        );
        RestapiResponse(); //응답
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

    @Override
    public void onResume() {
        super.onResume();
        if(!Sessionlist.isEmpty()) { //세션값이 있을때만 적용
            getalertcount(); //알림 데이터 확인
        }
    }
    //toolbar 변경할때 사용 - 서비스에서 알림이 오면 변경함
    public void ChangeToolbarmenu(){
        appbar.getMenu().clear();
        appbar.inflateMenu(R.menu.searchalertmenu_ck);
    }
}