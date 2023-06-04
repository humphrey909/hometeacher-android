package com.example.hometeacher.Nboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.Adapter.MyNboardCommentRecyclerAdapter;
import com.example.hometeacher.Adapter.SearchRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.CategoreyForm;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
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

public class MyNboardViewFragment extends Fragment {
    MyNboardList oMyNboardList;

    ArrayList<JSONObject> Nboardlist; //게시판 전체 리스트
    ArrayList<JSONObject> Commentlist; //댓글 리스트
    ArrayList<JSONObject> Commentnestedlist; //대댓글 리스트


    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int pagenum = 0;
    int limitnum = 10;
    RecyclerView TotalRecyclerView;

    int num;//activity에서 넘겨받은 데이터
    //String searchtext; //activity에서 넘겨받은 데이터

    View view;
    Context oContext;

    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;
    String MyUsertype;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    ArrayList<CategoreyForm> Subcategoreylist;


    LinearLayout categoreylinear;

    public MyNboardViewFragment() {
        // Required empty public constructor
    }


    public static MyNboardViewFragment newInstance(int number) {
        MyNboardViewFragment fragment = new MyNboardViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
      //  bundle.putString("searchtext", searchtext);



        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //프래그먼트 해당 번호를 넘겨 받음
            num = getArguments().getInt("number");
          //  searchtext = getArguments().getString("searchtext");

            Log.d("num", String.valueOf(num)); //프래그먼트 위치

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mynboardview, container, false);

        oContext = view.getContext();
        Nboardlist = new ArrayList<>();
        Commentlist = new ArrayList<>();
        Commentnestedlist = new ArrayList<>();

        oSession = new Session(oContext);
        Sessionlist = oSession.Getoneinfo("0");

        MyUsertype = Sessionlist.get(1).get(2);//학생, 선생님 구분


        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        Subcategoreylist = new ArrayList<>();
        categoreylinear = (LinearLayout) view.findViewById(R.id.categoreylinear);




       //여기서 num에 맞게 객체의 형태를 변형해주면 될 듯.
        if(num == 0){ //게시판
            GetNboardlist(limitnum, 0);
        }else if(num == 1){ //댓글
           GetCommentlist(limitnum, 0);
        }else if(num == 2){ //대댓글
            GetCommentnestedlist(limitnum, 0);
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

                    //Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                    Log.d("pagenum-----", String.valueOf(pagenum));


                    //여기서 num에 맞게 객체의 형태를 변형해주면 될 듯.
                    if(num == 0){ //게시판
                        GetNboardlist(limitnum, offsetnum);
                    }else if(num == 1){ //댓글
                        GetCommentlist(limitnum, offsetnum);
                    }else if(num == 2){ //대댓글
                        GetCommentnestedlist(limitnum, offsetnum);
                    }
                }
            }
        });

        return view;
    }

    //게시물 리스트
    public void GetNboardlist(int limit, int offset) {
        Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf("GetNboardlist 1111"));
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();

        if(MyUsertype.equals("1")){ //선생님 -> 선생님토론
            RequestBody maincategorey = RequestBody.create(MediaType.parse("text/plain"), "1");
            requestMap.put("maincategorey", maincategorey);
        }else if(MyUsertype.equals("2")){ //학생 -> 멘토링
            RequestBody maincategorey = RequestBody.create(MediaType.parse("text/plain"), "0");
            requestMap.put("maincategorey", maincategorey);
        }

        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함


        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getnboardlist_mypage(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //댓글 리스트
    public void GetCommentlist(int limit, int offset) {
        Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf("getcommentlist 1111"));
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함


        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getcommentlist_mypage(
                limit,
                offset,
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //대댓글 리스트
    public void GetCommentnestedlist(int limit, int offset) {
        Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf("getcommentlist 1111"));
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함


        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getcommentnestedlist_mypage(
                limit,
                offset,
                4,
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

                    if (urlget2.equals("1")) { //게시글 리스트
                        Log.d("onResponse ? ", "onResponse 게시글 리스트 : " + resultlist);

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
                    }else if(urlget2.equals("2")){ //댓글 리스트
                        Log.d("onResponse ? ", "onResponse 댓글 리스트 : " + resultlist);

                        progressBar.setVisibility(View.GONE);


                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Commentlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "댓글 정보들 : all" + String.valueOf(Nboardlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeCommentrecycle(Commentlist);
                    }else if(urlget2.equals("3")) { //댓글 삭제시
                        Log.d("onResponse ? ", "onResponse 댓글, 대댓글 삭제시 : " + resultlist);

                        pagenum = 0; //페이징 초기화
                        Commentlist.clear();
                        GetCommentlist(limitnum, 0);
                    }else if(urlget2.equals("4")) { //대댓글 리스트
                        Log.d("onResponse ? ", "onResponse 대댓글 리스트 : " + resultlist);

                        progressBar.setVisibility(View.GONE);


                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Commentnestedlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "댓글 정보들 : all" + String.valueOf(Nboardlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeCommentnestedrecycle(Commentnestedlist);


                    }else if(urlget2.equals("5")) {//대댓글 삭제시
                        Log.d("onResponse ? ", "onResponse 대댓글 삭제시 : " + resultlist);

                        pagenum = 0; //페이징 초기화
                        Commentnestedlist.clear();
                        GetCommentnestedlist(limitnum, 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

//    //유저 닉네임 검색 리스트를 만든다
//    public void MakeUserNicnamerecycle(ArrayList<JSONObject> Userlistjsonarray){
//        Log.d("유저 닉네임검색 리스트 이다.", String.valueOf(Userlistjsonarray));
//
//        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
//        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
//        SearchRecyclerAdapter SearchAdapter = new SearchRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
//        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
//
//        SearchAdapter.needdata(num, "");
//        SearchAdapter.setSessionval(Sessionlist); //arraylist 연결
//        SearchAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
//        TotalRecyclerView.setAdapter(SearchAdapter); //리사이클러뷰 위치에 어답터 세팅
//    }
//
//
//
//    //유저 리스트를 만든다
//    public void MakeUserrecycle(ArrayList<JSONObject> Userlistjsonarray){
//        Log.d("유저 리스트 이다.", String.valueOf(Userlistjsonarray));
//
//        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
//        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
//        SearchRecyclerAdapter SearchAdapter = new SearchRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
//        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
//
//        SearchAdapter.needdata(num, "");
//        SearchAdapter.setSessionval(Sessionlist); //arraylist 연결
//        SearchAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
//        TotalRecyclerView.setAdapter(SearchAdapter); //리사이클러뷰 위치에 어답터 세팅
//    }


    //게시글 리스트를 만든다.
    public void MakeNboardrecycle(ArrayList<JSONObject> Nboardlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Nboardlistjsonarray));

        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity()); //그리드 매니저 선언
        MyNboardCommentRecyclerAdapter MyNboardRecyclerAdapter = new MyNboardCommentRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        MyNboardRecyclerAdapter.needdata(num);
        MyNboardRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        MyNboardRecyclerAdapter.setRecycleList(Nboardlistjsonarray); //arraylist 연결
        TotalRecyclerView.setAdapter(MyNboardRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅



        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        MyNboardRecyclerAdapter.setOnItemClickListener(new MyNboardCommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) {

                if(type == 1){
                    Intent intent = new Intent(getActivity(), Nboardview.class);
                    try {
                        //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
                        intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
                        //intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                        intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(intent, REQUESTCODE);
                }
            }
        });

    }

    //대댓글 리스트를 만든다.
    public void MakeCommentnestedrecycle(ArrayList<JSONObject> Commentlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Commentlistjsonarray));

        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity()); //그리드 매니저 선언
        MyNboardCommentRecyclerAdapter MyNboardRecyclerAdapter = new MyNboardCommentRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        MyNboardRecyclerAdapter.needdata(num);
        MyNboardRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        MyNboardRecyclerAdapter.setRecycleList(Commentlistjsonarray); //arraylist 연결
        TotalRecyclerView.setAdapter(MyNboardRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅



        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        MyNboardRecyclerAdapter.setOnItemClickListener(new MyNboardCommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) {
                if(type == 1){
                    Intent intent = new Intent(getActivity(), Nboardview.class);
                    try {
                        intent.putExtra("nid", String.valueOf(list.get(position).get("nid")));
                        intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                        intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(intent, REQUESTCODE);
                }else if(type == 2){ //메뉴 클릭시 삭제 진행
                    PopupMenu poup = poup = new PopupMenu(oMyNboardList, v); //TODO 일반 사용
                    oMyNboardList.getMenuInflater().inflate(R.menu.mypagecommentmenu, poup.getMenu());
                    poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                            String popup_tittle = item.getTitle().toString();
                            //TODO ==== [메뉴 선택 동작 처리] ====
                            if(popup_tittle.contains("삭제하기")){

                                //다이어그램 띄우기
                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                                builder.setTitle("대댓글 삭제").setMessage("대댓글을 삭제합니다");

                                //삭제 실시
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            //댓글인지 대댓글인지 구분해야함.
                                            setcommentdelete(String.valueOf(list.get(position).get("idx")), "2");
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


    //댓글 리스트를 만든다.
    public void MakeCommentrecycle(ArrayList<JSONObject> Commentlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Commentlistjsonarray));

        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity()); //그리드 매니저 선언
        MyNboardCommentRecyclerAdapter MyNboardRecyclerAdapter = new MyNboardCommentRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        MyNboardRecyclerAdapter.needdata(num);
        MyNboardRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        MyNboardRecyclerAdapter.setRecycleList(Commentlistjsonarray); //arraylist 연결
        TotalRecyclerView.setAdapter(MyNboardRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅



        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        MyNboardRecyclerAdapter.setOnItemClickListener(new MyNboardCommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) {
                if(type == 1){
                    Intent intent = new Intent(getActivity(), Nboardview.class);
                    try {
                        intent.putExtra("nid", String.valueOf(list.get(position).get("nid")));
                        intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                        intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(intent, REQUESTCODE);
                }else if(type == 2){ //메뉴 클릭시 삭제 진행
                    PopupMenu poup = poup = new PopupMenu(oMyNboardList, v); //TODO 일반 사용
                    oMyNboardList.getMenuInflater().inflate(R.menu.mypagecommentmenu, poup.getMenu());
                    poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                            String popup_tittle = item.getTitle().toString();
                            //TODO ==== [메뉴 선택 동작 처리] ====
                            if(popup_tittle.contains("삭제하기")){

                                //다이어그램 띄우기
                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                                builder.setTitle("댓글 삭제").setMessage("댓글을 삭제합니다");

                                //삭제 실시
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            setcommentdelete(String.valueOf(list.get(position).get("idx")), "1");
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

    //댓글 삭제
    //댓글 대댓글이 합쳐져 잇음
    public void setcommentdelete(String idx, String commenttype){

        if(commenttype.equals("1")){ //댓글
            //프로필 정보 가져오는 서버통신
            RestapiStart(); //레트로핏 빌드

            HashMap<String, RequestBody> requestMap = new HashMap<>();
            RequestBody cid = RequestBody.create(MediaType.parse("text/plain"), idx);
            requestMap.put("cid", cid);

            call = retrofitService.delete_comment(
                    3, requestMap
            );
            RestapiResponse(); //응답
        }else if(commenttype.equals("2")){ //대댓글
            //프로필 정보 가져오는 서버통신
            RestapiStart(); //레트로핏 빌드

            HashMap<String, RequestBody> requestMap = new HashMap<>();
            RequestBody cnid = RequestBody.create(MediaType.parse("text/plain"), idx);
            requestMap.put("cnid", cnid);

            call = retrofitService.delete_commentnested(
                    5, requestMap
            );
            RestapiResponse(); //응답
        }
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


                 if(completeval.equals("0")){ // 0 nboardview에서 뒤로가기 했을때
                     // Toast myToast = Toast.makeText(getContext(), "게시물."+num, Toast.LENGTH_SHORT);
                     //myToast.show();

                     pagenum = 0; //페이징 초기화
                     Nboardlist.clear();
                     Commentlist.clear();
                     Commentnestedlist.clear();

                     if(num == 0){ //게시판
                         GetNboardlist(limitnum, 0);
                     }else if(num == 1){ //댓글
                         GetCommentlist(limitnum, 0);
                     }else if(num == 2){ //댓글
                         GetCommentnestedlist(limitnum, 0);
                     }
                }else if(completeval.equals("2")){// 게시글 삭제 후
                     pagenum = 0; //페이징 초기화
                     Nboardlist.clear();
                     Commentlist.clear();
                     Commentnestedlist.clear();

                     if(num == 0){ //게시판
                         GetNboardlist(limitnum, 0);
                     }else if(num == 1){ //댓글
                         GetCommentlist(limitnum, 0);
                     }else if(num == 2){ //대댓글
                         GetCommentnestedlist(limitnum, 0);
                     }
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


    //학번 문자열로 변환
    public String studentid_replace(String studentid){
        if(studentid.equals("0")){
            return "전체";
        }else if(studentid.equals("1")){
            return "22";
        }else if(studentid.equals("2")){
            return "21";
        }else if(studentid.equals("3")){
            return "20";
        }else if(studentid.equals("4")){
            return "19";
        }else if(studentid.equals("5")){
            return "18";
        }else if(studentid.equals("6")){
            return "17";
        }else if(studentid.equals("7")){
            return "16";
        }else if(studentid.equals("8")){
            return "15";
        }else if(studentid.equals("9")){
            return "14";
        }else if(studentid.equals("10")){
            return "13";
        }else if(studentid.equals("11")){
            return "12";
        }else if(studentid.equals("12")){
            return "11";
        }else if(studentid.equals("13")){
            return "10";
        }else if(studentid.equals("14")){
            return "09";
        }else if(studentid.equals("15")){
            return "08";
        }else if(studentid.equals("16")){
            return "07";
        }else if(studentid.equals("17")){
            return "06";
        }else if(studentid.equals("18")){
            return "05";
        }else if(studentid.equals("19")){
            return "04";
        }else{
            return "-";
        }
    }

    //학년 문자열로 변환
    public String studentage_replace(String studentage){
        if(studentage.equals("0")) {
            return "전체";
        }else if(studentage.equals("1")){
            return "사회인";
        }else if(studentage.equals("2")){
            return "대학생";
        }else if(studentage.equals("3")){
            return "n수생";
        }else if(studentage.equals("4")){
            return "고3";
        }else if(studentage.equals("5")){
            return "고2";
        }else if(studentage.equals("6")){
            return "고1";
        }else if(studentage.equals("7")){
            return "중3";
        }else if(studentage.equals("8")){
            return "중2";
        }else if(studentage.equals("9")){
            return "중1";
        }else if(studentage.equals("10")){
            return "초6";
        }else if(studentage.equals("11")){
            return "초5";
        }else if(studentage.equals("12")){
            return "초4";
        }else if(studentage.equals("13")){
            return "초3";
        }else if(studentage.equals("14")){
            return "초2";
        }else if(studentage.equals("15")){
            return "초1";
        }else{
            return "-";
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        oMyNboardList = (MyNboardList) getActivity();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        oMyNboardList = null;
    }

}