package com.example.hometeacher.navigation;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.hometeacher.Adapter.MyClassroomRecyclerAdapter;
import com.example.hometeacher.Adapter.RequestClassRecyclerAdapter;
import com.example.hometeacher.Alertpage;
import com.example.hometeacher.Classinquiryroomactivity;
import com.example.hometeacher.MainActivity;
import com.example.hometeacher.MyclassmakeroomActivity;
import com.example.hometeacher.Myclassroomactivity;
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

public class MyclassFragment extends Fragment {

    Session oSession; //자동로그인을 위한 db
    Context oContext;

    ProgressBar progressBar;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;

    MainActivity mainActivity;

    ArrayList<ArrayList<String>> Sessionlist;
    Toolbar appbar;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    int pagenum = 0;
    int limitnum = 10;

    int AlertCount;
    ArrayList<JSONObject> ChatRoomlist; //내 과외 방 전체 리스트
    RecyclerView MyClassRecyclerView;
    NestedScrollView nestedScrollView;

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    TextView classrequestcategoreytext;
    ImageButton classrequestcategoreybtn;
    ImageButton.OnClickListener clickListener;
    ArrayList<String> Maincategoreylist;
    int MainCategoreyType = 0; //카테고리 타입

    AutoCompleteTextView autoCompleteTextView; //자동 검색 검색어
    String SearchData = "";

    public MyclassFragment() {
    }

    public static MyclassFragment newInstance(String param1, String param2) {
        MyclassFragment fragment = new MyclassFragment();
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
        view = inflater.inflate(R.layout.fragment_myclass, container, false);


        ((MainActivity) getActivity()).Firstaccesschk(4);

        division();

        // Inflate the layout for this fragment
        return view;
    }

    public void division() {
        setHasOptionsMenu(true);

        oContext = view.getContext();
        oSession = new Session(oContext);

        //툴바 설정
        appbar = (Toolbar) view.findViewById(R.id.toolbarbox);

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        classrequestcategoreytext = (TextView) view.findViewById(R.id.classrequestcategoreytext);
        classrequestcategoreybtn = (ImageButton) view.findViewById(R.id.classrequestcategoreybtn);

        MyClassRecyclerView = (RecyclerView) view.findViewById(R.id.MyClassRecyclerView);
        autoCompleteTextView  = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

        //progressBar.setVisibility(View.GONE);
        //classrequestcategoreytext.setText("전체");

        //자동 로그인 하기
        Sessionlist = oSession.Getoneinfo("0");


        if(!Sessionlist.isEmpty()) { //값이 있다.
            Log.d("Sessionlist", String.valueOf(Sessionlist));

        }else{ //값이 없으면
            //세션이 없으면 로그인 페이지 띄울 것

            // Intent intent = new Intent(getActivity(), Loginpage.class);
            // startActivity(intent);
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

                    // Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                    GetMyclassroomlist(limitnum, offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));

                }
            }
        });


        CategoreySeletbtn(1);
    }

    //최상단으로 올라감
    public void upscroll(){
        nestedScrollView.pageScroll(View.FOCUS_UP);
        nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
    }
    //카테고리 선택시 나타는 다이얼로그
    public void CategoreySeletbtn(int type){

        classrequestcategoreytext.setText("전체");

        Maincategoreylist= new ArrayList<>();
        Maincategoreylist.add("전체");
        //Maincategoreylist.add("읽음");
        Maincategoreylist.add("안 읽음");

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

                        //리스트를 불러와서 noreadchk 가 있는 값만 모으면 안읽음 리스트 이고 noreadchk가 없는 값만 모으면 읽음 리스트 이다.
                        if(MainCategoreyType == 0) { //0. 전체
                            Log.d("pagenum-----", String.valueOf(MainCategoreyType));
                            classrequestcategoreytext.setText("전체");
                        }//else if(MainCategoreyType == 1){ //1. 읽음 리스트
                        //    classrequestcategoreytext.setText("읽음");
                        else{ //2. 안읽음 리스트
                            Log.d("pagenum-----", String.valueOf(MainCategoreyType));
                            classrequestcategoreytext.setText("안읽음");
                        }

                        try {
                            Changeroomlist_tocategorey(MainCategoreyType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        };
        classrequestcategoreytext.setOnClickListener(clickListener);
        classrequestcategoreybtn.setOnClickListener(clickListener);

        //검색 클릭
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :

                        Log.d("MainActivity", "입력 내용 : " + autoCompleteTextView.getText().toString());

                        SearchData = autoCompleteTextView.getText().toString();

                        ChatRoomlist.clear();
                        GetMyclassroomlist(limitnum, 0);

                        //
                }
                return true;
            }
        });
    }

    ArrayList<JSONObject> ChangeChatRoomlist; //카테고리로 변경된 리스트
    //카테고리에 맞게 리스트를 변경해준다.
    public void Changeroomlist_tocategorey(int type) throws JSONException {

        ChangeChatRoomlist = new ArrayList<>();

        if(type == 0) { //전체
            for(int i = 0; i<ChatRoomlist.size();i++){
                Log.d("onResponse ? ", "과외방 리스트 ----- : " + String.valueOf(ChatRoomlist.get(i)));
               // Log.d("onResponse ? ", "과외방 리스트 ----- : " + String.valueOf(ChatRoomlist.get(i).get("noreadchk")));

                ChangeChatRoomlist.add(ChatRoomlist.get(i));
            }
//        }else if(type == 1){ //읽음
//            for(int i = 0; i<ChatRoomlist.size();i++){
//               // Log.d("onResponse ? ", "과외방 리스트 ----- : " + String.valueOf(ChatRoomlist.get(i).get("noreadchk")));
//
//                //읽음 메세지 모으기
//                if(!String.valueOf(ChatRoomlist.get(i).get("noreadchk")).equals("1")){
//                    ChangeChatRoomlist.add(ChatRoomlist.get(i));
//                }
//            }
        }else { //안읽음
            for(int i = 0; i<ChatRoomlist.size();i++){
                //Log.d("onResponse ? ", "과외방 리스트 ----- : " + String.valueOf(ChatRoomlist.get(i).get("noreadchk")));

                //안읽음 메세지 모으기
                if(String.valueOf(ChatRoomlist.get(i).get("noreadchk")).equals("1")){
                    ChangeChatRoomlist.add(ChatRoomlist.get(i));
                }
            }
        }

        //데이터를 바로 출력시킬것
        MakeMyClassrecycle(ChangeChatRoomlist);
    }

    Intent intent;
    //fragment에 menu 추가
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

       //유저 타입에 맞게 툴바를 변경
        if(Sessionlist.get(1).get(2).equals("1")){ //선생님
            appbar.inflateMenu(R.menu.myclassalertmenu);
        }else{ //학생
            appbar.inflateMenu(R.menu.mainalertmenu);
        }

        appbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.addtab:
                    intent = new Intent(getActivity(), MyclassmakeroomActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.alerttab:
                    intent = new Intent(getActivity(), Alertpage.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });
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



        pagenum = 0; //페이징 초기화
        //SearchData = ""; //검색어 초기화
//        autoCompleteTextView.setText(""); //검색어 초기화
//
        ChatRoomlist = new ArrayList<>();
        GetMyclassroomlist(limitnum, 0);
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
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //나의 과외 방 리스트
    public void GetMyclassroomlist(int limit, int offset) {

        //유저를 불러오고 방을 조인해서 방의 정보를 뿌려주어야한다...!!!!
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid);


        Log.d("MainActivity", "입력 내용 : " + SearchData);
        //검색어 전송
        RequestBody searchdata = RequestBody.create(MediaType.parse("text/plain"), SearchData);
        requestMap.put("searchdata", searchdata);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getmyclassuserlist(
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

                    if (urlget2.equals("1")) { //알림 체크하는 부분
                        Log.d("onResponse ? ", "알림 체크하는 부분 : " + resultlist);

                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","알림 체크하는 부분  : " + String.valueOf(jobj.get("count")));

                            AlertCount = Integer.parseInt(String.valueOf(jobj.get("count")));
                            if(AlertCount == 0){
                                appbar.getMenu().clear();

                                //유저 타입에 맞게 툴바를 변경
                                if(Sessionlist.get(1).get(2).equals("1")){ //선생님
                                    appbar.inflateMenu(R.menu.myclassalertmenu);
                                }else{ //학생
                                    appbar.inflateMenu(R.menu.mainalertmenu);
                                }
                            }else{
                                appbar.getMenu().clear();

                                //유저 타입에 맞게 툴바를 변경
                                if(Sessionlist.get(1).get(2).equals("1")){ //선생님
                                    appbar.inflateMenu(R.menu.myclassalertmenu_ck);
                                }else{ //학생
                                    appbar.inflateMenu(R.menu.mainalertmenu_ck);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //내 과외 방 리스트 가져오기
                        Log.d("onResponse ? ", "내 과외 방 리스트 가져오기 : " + resultlist);


                        progressBar.setVisibility(View.GONE);

                        Log.d("onResponse ? ", "내 과외 방 리스트 : " + resultlist);
                        Log.d("onResponse ? ", "내 과외 방 리스트 : " + resultlist.length());

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "내 과외 방 리스트 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "내 과외 방 리스트 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                ChatRoomlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "내 과외 방 리스트 : all" + String.valueOf(ChatRoomlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //데이터를 바로 출력시킬것
                        MakeMyClassrecycle(ChatRoomlist);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    //내 과외 리스트를 만든다.
    public void MakeMyClassrecycle(ArrayList<JSONObject> MyclassRoomlistjsonarray){
        Log.d("내 과외 방 리스트 이다.", String.valueOf(MyclassRoomlistjsonarray));

        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity()); //그리드 매니저 선언
        MyClassroomRecyclerAdapter oMyClassroomRecyclerAdapter = new MyClassroomRecyclerAdapter(mainActivity.getApplicationContext(), mainActivity); //내가만든 어댑터 선언
        MyClassRecyclerView.setLayoutManager(LinearLayoutManager);

        oMyClassroomRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oMyClassroomRecyclerAdapter.setRecycleList(MyclassRoomlistjsonarray); //arraylist 연결
        MyClassRecyclerView.setAdapter(oMyClassroomRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oMyClassroomRecyclerAdapter.setOnItemClickListener(new MyClassroomRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list) throws JSONException {

                Intent intent = new Intent(getActivity(), Myclassroomactivity.class);
                intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
                intent.putExtra("roomidx", String.valueOf(list.get(position).get("rid"))); //룸 고유번호
                intent.putExtra("Tchatcount", String.valueOf(list.get(position).get("totalchatcount"))); //총 채팅 갯수

                //중복 클릭 방지를 위해 설정
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUESTCODE);
            }
        });

    }

    //룸 리스트를 다시 불러옴 - 서비스에서 채팅을 수신 받았을때
    public void InitRoomlist(){
        //Log.d("---------GetChatroomlist----------","GetChatroomlist");
        //ChatRoomlist = new ArrayList<>();
        ChatRoomlist.clear();
        GetMyclassroomlist(limitnum, 0);
    }

    //toolbar 변경할때 사용 - 서비스에서 알림이 오면 변경함
    public void ChangeToolbarmenu(){
        appbar.getMenu().clear();
        appbar.inflateMenu(R.menu.myclassalertmenu_ck);
    }
}