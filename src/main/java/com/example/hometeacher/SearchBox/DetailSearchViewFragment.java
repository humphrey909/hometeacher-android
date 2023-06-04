package com.example.hometeacher.SearchBox;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.hometeacher.Adapter.CategoreytoDetailSearchRecyclerAdapter;
import com.example.hometeacher.Adapter.SearchRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.CategoreyForm;
import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.shared.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DetailSearchViewFragment extends Fragment {

    ArrayList<JSONObject> Userlist; //회원 전체 리스트
    ArrayList<JSONObject> Userlist_nicname; //회원 전체 리스트
    ArrayList<JSONObject> Nboardlist; //게시판 전체 리스트


    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int pagenum = 0;
    int limitnum = 10;
    RecyclerView TotalRecyclerView;

    int num;//activity에서 넘겨받은 데이터
    String searchtext; //activity에서 넘겨받은 데이터

    View view;
    Context oContext;

    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;
    String MyUsertype;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    RecyclerView UsercategoreyRecyclerView;
    ArrayList<CategoreyForm> Subcategoreylist;

    ArrayList<String> Conditionlist_teacher = new ArrayList<>(Arrays.asList("메인 카테고리","성별", "수업료", "학번", "과목","닉네임","출신학교"));
    ArrayList<String> Conditionlist_student = new ArrayList<>(Arrays.asList("메인 카테고리","성별", "수업료", "학년", "과목","닉네임"));


    LinearLayout categoreylinear;
    Button CategoreyoneButton;

    //리스트 조건 변수들
    int SelectType = 0;
    int SelectGender = 0;
    int SelectStudentId = 0;
    int SelectStudentAges = 0;
    String Selectminpay = "";
    String Selectmaxpay = "";
    String Selectnicname = "";
    String Selectschool = "";
    ArrayList<String> SubjectSelectedItems; //선택한 과목 리스트


    public DetailSearchViewFragment() {
        // Required empty public constructor
    }


    public static DetailSearchViewFragment newInstance(int number, String searchtext) {
        DetailSearchViewFragment fragment = new DetailSearchViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        bundle.putString("searchtext", searchtext);



        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //프래그먼트 해당 번호를 넘겨 받음
            num = getArguments().getInt("number");
            searchtext = getArguments().getString("searchtext");

            Log.d("num", String.valueOf(num)); //프래그먼트 위치

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_search_view, container, false);

        oContext = view.getContext();
        Userlist = new ArrayList<>();
        Userlist_nicname = new ArrayList<>();
        Nboardlist = new ArrayList<>();

        oSession = new Session(oContext);
        Sessionlist = oSession.Getoneinfo("0");

        if(!Sessionlist.isEmpty()) { //로그인 상태
            MyUsertype = Sessionlist.get(1).get(2);//학생, 선생님 구분
        }else{ //로그아웃 상태
            MyUsertype = "0";
        }

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        UsercategoreyRecyclerView = (RecyclerView) view.findViewById(R.id.UsercategoreyRecyclerView);
        Subcategoreylist = new ArrayList<>();
        categoreylinear = (LinearLayout) view.findViewById(R.id.categoreylinear);

        //선택한 과목 담는 리스트
        SubjectSelectedItems = new ArrayList<String>();
      //  Log.d("onCreateView num", String.valueOf(num)); //프래그먼트 위치

        //유저의 타입에 따라 리스트 변경
        if(MyUsertype.equals("1")){ //선생님
            SelectType = 2;
        }else if(MyUsertype.equals("2")){ //학생
            SelectType = 1;
        }else{ //로그아웃 상태
            SelectType = 1;
        }


       //여기서 num에 맞게 객체의 형태를 변형해주면 될 듯.
        if(num == 0){ //과외찾기
            //GetUserlist_nicname(limitnum, 0);
            GetUserlist(limitnum, 0);
            categoreylinear.setVisibility(View.VISIBLE);

            //유저의 타입에 따라 카테고리 변경
            if(MyUsertype.equals("1")){ //선생님
                for (int i = 0;i<Conditionlist_student.size();i++){
                    CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_student.get(i), false);
                    Subcategoreylist.add(i, CategoreyForm);
                }
            }else if(MyUsertype.equals("2")){ //학생
                for (int i = 0;i<Conditionlist_teacher.size();i++){
                    CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_teacher.get(i), false);
                    Subcategoreylist.add(i, CategoreyForm);
                }
            }else{ //로그아웃 상태
                for (int i = 0;i<Conditionlist_student.size();i++){
                    CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_student.get(i), false);
                    Subcategoreylist.add(i, CategoreyForm);
                }
            }

            MakeCategoreyrecycle(Subcategoreylist);
        }else if(num == 1){ //게시판
            categoreylinear.setVisibility(View.GONE);
            GetNboardlist(limitnum, 0);
        }else if(num == 2){ //사람
            categoreylinear.setVisibility(View.GONE);
            GetUserlist_nicname(limitnum, 0);
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

//                    GetCommentlist(limitnum, offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));






                    //여기서 num에 맞게 객체의 형태를 변형해주면 될 듯.
                    if(num == 0){ //과외찾기
                        //GetUserlist_nicname(limitnum, 0);
                        GetUserlist(limitnum, offsetnum);
                        categoreylinear.setVisibility(View.VISIBLE);

//                        //유저의 타입에 따라 카테고리 변경
//                        if(MyUsertype.equals("1")){ //선생님
//                            for (int i = 0;i<Conditionlist_student.size();i++){
//                                CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_student.get(i), false);
//                                Subcategoreylist.add(i, CategoreyForm);
//                            }
//                        }else if(MyUsertype.equals("2")){ //학생
//                            for (int i = 0;i<Conditionlist_teacher.size();i++){
//                                CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_teacher.get(i), false);
//                                Subcategoreylist.add(i, CategoreyForm);
//                            }
//                        }else{ //로그아웃 상태
//                            for (int i = 0;i<Conditionlist_student.size();i++){
//                                CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_student.get(i), false);
//                                Subcategoreylist.add(i, CategoreyForm);
//                            }
//                        }
//
//                        MakeCategoreyrecycle(Subcategoreylist);
                    }else if(num == 1){ //게시판
                        categoreylinear.setVisibility(View.GONE);
                        GetNboardlist(limitnum, offsetnum);
                    }else if(num == 2){ //사람
                        categoreylinear.setVisibility(View.GONE);
                        GetUserlist_nicname(limitnum, offsetnum);
                    }






                }
            }
        });

        return view;
    }


    //카테고리 리스트를 만든다.
    public void MakeCategoreyrecycle(ArrayList<CategoreyForm> categoreylistjsonarray){
        Log.d("카테고리 리스트 이다.", String.valueOf(categoreylistjsonarray));

        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false); //가로일때
        CategoreytoDetailSearchRecyclerAdapter CategoreytoDetailSearchRecyclerAdapter = new CategoreytoDetailSearchRecyclerAdapter(oContext.getApplicationContext()); //내가만든 어댑터 선언
        UsercategoreyRecyclerView.setLayoutManager(linearManager);

        CategoreytoDetailSearchRecyclerAdapter.setneeddata(SelectType);
        CategoreytoDetailSearchRecyclerAdapter.setRecycleList(categoreylistjsonarray); //arraylist 연결
        UsercategoreyRecyclerView.setAdapter(CategoreytoDetailSearchRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        CategoreytoDetailSearchRecyclerAdapter.setOnItemClickListener(new CategoreytoDetailSearchRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, Button btnname) throws JSONException {

                CategoreyoneButton = btnname;

                Log.d("카테고리 설정 번호 이다.", String.valueOf(position));

                //"메인카테고리", "성별", "수업료", "학번", "과목","닉네임","출신학교"

                if(position == 0) { //메인카테고리


                    ArrayList<String> categoreylist= new ArrayList<>();

                    if(MyUsertype.equals("2")) { //학생
                        categoreylist.add("선생님 찾기");
                    }else if(MyUsertype.equals("1")){
                        categoreylist.add("선생님 찾기");
                        categoreylist.add("학생찾기");
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                    builder.setTitle("메인 카테고리 선택");

                    builder.setItems(categoreylist.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            Log.d("pos", String.valueOf(pos));
                            CategoreyoneButton.setText(categoreylist.get(pos));
                            CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                            CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));

                            ConditionValueInit(); //조건 초기화

                            Userlist.clear();
                            Subcategoreylist.clear();
                            if(pos == 0){ //선생님 찾기
                                SelectType = 1;
                                GetUserlist(limitnum, 0);

                                for (int i = 0;i<Conditionlist_teacher.size();i++){
                                    CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_teacher.get(i), false);
                                    Subcategoreylist.add(i, CategoreyForm);
                                }
                            }else if(pos == 1){ //학생 찾기
                                SelectType = 2;
                                GetUserlist(limitnum, 0);

                                for (int i = 0;i<Conditionlist_student.size();i++){
                                    CategoreyForm CategoreyForm = new CategoreyForm(Conditionlist_student.get(i), false);
                                    Subcategoreylist.add(i, CategoreyForm);
                                }
                            }

                            MakeCategoreyrecycle(Subcategoreylist);
                        }
                    });
                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                }else if(position == 1){ //성별


                    ArrayList<String> categoreylist= new ArrayList<>();
                    categoreylist.add("전체");
                    categoreylist.add("남");
                    categoreylist.add("여");

                    AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                    builder.setTitle("성별 선택");

                    builder.setItems(categoreylist.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            Log.d("pos", String.valueOf(pos));


                            Userlist.clear();
                            if(pos == 0){ //전체
                                SelectGender = 0;
                                GetUserlist(limitnum, 0);

                                CategoreyoneButton.setText("성별");
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));

                            }else if(pos == 1){ //남
                                SelectGender = 1;
                                GetUserlist(limitnum, 0);

                                CategoreyoneButton.setText(categoreylist.get(pos));
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                            }else if(pos == 2){
                                SelectGender = 2;
                                GetUserlist(limitnum, 0);

                                CategoreyoneButton.setText(categoreylist.get(pos));
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                            }

                        }
                    });
                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                }else if(position == 2){ //수업료
                    CustomDialog_pay octDialog = new CustomDialog_pay(oContext);

                    octDialog.setdata(Selectminpay, Selectmaxpay);
                    octDialog.CunstomMenuDialogStart(new CustomDialog_pay.CustomDialogClickListener() {@SuppressLint("SetTextI18n")
                    @Override
                        public void paysearchcompleteCK(String minpay, String maxpay) throws ParseException {
                            Log.d("minpay", String.valueOf(minpay));
                            Log.d("maxpay", String.valueOf(maxpay));

                            Selectminpay = minpay;
                            Selectmaxpay = maxpay;
                            Userlist.clear();


                        Log.d("pos", String.valueOf(Selectminpay));
                        Log.d("pos", String.valueOf(Selectmaxpay));

                            if(minpay.equals("") && maxpay.equals("")) {
                                CategoreyoneButton.setText("수업료");
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));
                            }else if(!minpay.equals("") && !maxpay.equals("")){
                                CategoreyoneButton.setText(minpay +"원 ~ "+maxpay+"원 ");
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                            }else if(!minpay.equals("") && maxpay.equals("")){
                                CategoreyoneButton.setText(minpay +"원 ~ ");
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                            }else if(minpay.equals("") && !maxpay.equals("")){
                                CategoreyoneButton.setText(" ~ "+maxpay+"원 ");
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                            }
                            GetUserlist(limitnum, 0);
                        }
                    });
                    octDialog.setCanceledOnTouchOutside(true);
                    octDialog.setCancelable(true);
                    octDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    octDialog.show();

                }else if(position == 3){ //학번

                    //학번 리스트
                    if(SelectType == 1){ //선생님 리스트
                        ArrayList<String> categoreylist= new ArrayList<>();
                        for(int i=0;i<=19;i++){

                            if(i == 19) {
                                String stucentidval = studentid_replace(String.valueOf(i));
                                categoreylist.add(stucentidval + "학번 이상");
                            }else if(i == 0){
                                String stucentidval = studentid_replace(String.valueOf(i));
                                categoreylist.add(stucentidval);
                            }else{
                                String stucentidval = studentid_replace(String.valueOf(i));
                                categoreylist.add(stucentidval+"학번");
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                        builder.setTitle("학번 선택");

                        builder.setItems(categoreylist.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                Log.d("pos", String.valueOf(pos));


                                Userlist.clear();
                                SelectStudentId = pos;

                                if(pos == 0){
                                    CategoreyoneButton.setText("학번");
                                    CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                    CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));
                                }else{
                                    CategoreyoneButton.setText(categoreylist.get(pos));
                                    CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                    CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                                }

                                Log.d("pos SelectStudentId", String.valueOf(SelectStudentId));
                                GetUserlist(limitnum, 0);
                            }
                        });
                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();
                    }else if(SelectType == 2){ //학생 리스트

                        //학년 리스트
                        ArrayList<String> categoreylist= new ArrayList<>();
                        for(int i=0;i<=14;i++){
                            String studentageval = studentage_replace(String.valueOf(i));
                            categoreylist.add(studentageval);
                        }

                        Log.d("categoreylist", String.valueOf(categoreylist));

                        AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                        builder.setTitle("학년 선택");

                        builder.setItems(categoreylist.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int pos) {
                                Log.d("pos", String.valueOf(pos));

                                Userlist.clear();
                                SelectStudentAges = pos;

                                if(pos == 0){
                                    CategoreyoneButton.setText("학년");
                                    CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                    CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));
                                }else{
                                    CategoreyoneButton.setText(categoreylist.get(pos));
                                    CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                    CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                                }

                                Log.d("pos SelectStudentAges", String.valueOf(SelectStudentAges));
                                GetUserlist(limitnum, 0);
                            }
                        });
                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();



                    }


                }else if(position == 4){ //과목
                    //서버에서 과목 리스트 가져와서 다이얼로그에 적용


                    GetSubjectlist();

                }else if(position == 5){ //닉네임

                    CustomDialog_nicname octDialog = new CustomDialog_nicname(oContext);

                    octDialog.setdata(Selectnicname);
                    octDialog.CunstomMenuDialogStart(new CustomDialog_nicname.CustomDialogClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void paysearchcompleteCK(String nicname) throws ParseException {
                            Log.d("nicname", String.valueOf(nicname));

                            Userlist.clear();
                            Selectnicname = nicname;

                            if(nicname.equals("")){
                                CategoreyoneButton.setText("닉네임");
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));
                            }else{
                                CategoreyoneButton.setText("닉네임 "+String.valueOf(Selectnicname));
                                CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                            }

                            GetUserlist(limitnum, 0);
                        }
                    });
                    octDialog.setCanceledOnTouchOutside(true);
                    octDialog.setCancelable(true);
                    octDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    octDialog.show();

                }else if(position == 6){ //출신학교

                    //선생님일때만
                    if(SelectType == 1){ //선생님 리스트

                        CustomDialog_school octDialog = new CustomDialog_school(oContext);

                        octDialog.setdata(Selectschool);
                        octDialog.CunstomMenuDialogStart(new CustomDialog_school.CustomDialogClickListener() {
                            @Override
                            public void paysearchcompleteCK(String school) throws ParseException {
                                Log.d("school", String.valueOf(school));

                                Userlist.clear();
                                Selectschool = school;

                                if(school.equals("")){
                                    CategoreyoneButton.setText("출신학교");
                                    CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                    CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));
                                }else{
                                    CategoreyoneButton.setText("학교 "+school);
                                    CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                    CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                                }
                                GetUserlist(limitnum, 0);
                            }
                        });
                        octDialog.setCanceledOnTouchOutside(true);
                        octDialog.setCancelable(true);
                        octDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        octDialog.show();
                    }
                }
            }
        });

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
                4, requestMap
        );
        RestapiResponse(); //응답

    }

    //유저리스트 /
    public void GetUserlist(int limit, int offset) {

        //보낼값
        //SelectType = 1 선생님, type = 2 학생
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectType));
        requestMap.put("usertype", usertype);

        //SelectGender 1 = 남 2 = 여
        if(SelectGender != 0){ //전체
            RequestBody usergender = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectGender));
            requestMap.put("usergender", usergender);
        }

        //수업료 금액 : 하나라도 값이 있으면
        if(!Selectminpay.equals("") || !Selectmaxpay.equals("")){
            RequestBody minpay = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Selectminpay));
            requestMap.put("minpay", minpay);

            RequestBody maxpay = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Selectmaxpay));
            requestMap.put("maxpay", maxpay);
        }


        //학번 & 학년
        if(SelectType == 1) { //선생님 리스트
            if(SelectStudentId != 0){
                //학번 전송
                RequestBody studentid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(studentid_replace(String.valueOf(SelectStudentId))));
                requestMap.put("studentid", studentid);
            }
        }else if(SelectType == 2){ // 학생 리스트
            //학년 전송
            if(SelectStudentAges != 0) {
                RequestBody studentages = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectStudentAges-1)); //앞에 전체항목이 있어서 -1처리 해서 순서를 맞춰줌
                requestMap.put("studentages", studentages);
            }
        }




        //과목 선택이 없으면 전체 리스트, 선택한 과목이 있으면 선택된 과목만
        if(!SubjectSelectedItems.isEmpty()){
            RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SubjectSelectedItems));
            requestMap.put("subjectlist", subjectlist);
        }


        //닉네임
        if(!Selectnicname.equals("")){
            RequestBody nicname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Selectnicname));
            requestMap.put("nicname", nicname);
        }

        //출신학교
        if(!Selectschool.equals("")){
            RequestBody university = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Selectschool));
            requestMap.put("university", university);
        }

        //과목 선택이 없으면 전체 리스트, 선택한 과목이 있으면 선택된 과목만
        // if(!SelectSubjectlist.isEmpty()){
        //RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectSubjectlist));
        //requestMap.put("subjectlist", subjectlist);
//        }else{
        //RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), "null");
        //requestMap.put("subjectlist", subjectlist);
//        }

        //검색어로 검색
        if(!searchtext.equals("")){
            RequestBody searchtext_ = RequestBody.create(MediaType.parse("text/plain"), searchtext);
            requestMap.put("searchtext", searchtext_);
        }

        //  Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf(SelectSubjectlist));

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


    //게시물 리스트
    public void GetNboardlist(int limit, int offset) {
        Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf("GetNboardlist 1111"));
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody maincategorey = RequestBody.create(MediaType.parse("text/plain"), "null");
        requestMap.put("maincategorey", maincategorey);

        //검색어로 검색
        if(!searchtext.equals("")){
            RequestBody searchtext_ = RequestBody.create(MediaType.parse("text/plain"), searchtext);
            requestMap.put("searchtext", searchtext_);
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

    //유저 리스트 - 닉네임 검색 위주
    public void GetUserlist_nicname(int limit, int offset) {
        Log.d("-----SelectSubjectlist-------GetStudentlist", String.valueOf("GetUserlist_nicname 1111"));


        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();

        //이 값은 쓰이진 않지만 아무것도 보내지 않으면 서버에 도달을 안함..
        RequestBody maincategorey = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
        requestMap.put("maincategorey", maincategorey);

        //검색어로 검색
        if(!searchtext.equals("")){
            RequestBody searchtext_ = RequestBody.create(MediaType.parse("text/plain"), searchtext);
            requestMap.put("searchtext", searchtext_);
        }


        //유저 검색 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getusersearchlist(
                limit,
                offset,
                3,
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
                        }else{ //데이터가 없으면?

                            //데이터를 바로 출력시킬것
                            MakeUserrecycle(Userlist);
                        }
                    }else if(urlget2.equals("2")){ //게시글 리스트

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

                    }else if(urlget2.equals("3")){ //유저 검색 리스트
                        Log.d("onResponse ? ", "유저 닉네임 검색 리스트 : " + resultlist);
                        Log.d("onResponse ? ", "유저 닉네임 검색 리스트 : " + resultlist);


                        progressBar.setVisibility(View.GONE);

                        if(resultlist.length() > 2){ //받아온 데이터가 없으면 진행 x
                            try {
                                //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                                JSONArray jarray = new JSONArray(resultlist);
                                Log.d("onResponse ? ","onResponse 닉네임 검색 유저 리스트 : " + String.valueOf(jarray));
                                // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                                if (jarray.get(0) != null) {

                                    for(int i = 0; i<jarray.length(); i++){
                                        JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));
                                         Log.d("onResponse ? ", "onResponse 닉네임 검색 유저 리스트 : " + String.valueOf(jobj));

                                        //jsonobject형식으로 데이터를 저장한다.
                                        Userlist_nicname.add(jobj);
                                    }

                                    //object형식으로 arraylist를 만든것
                                    Log.d("onResponse ? ", "onResponse 닉네임 검색 유저 리스트 : " + String.valueOf(Userlist_nicname));

                                    //데이터를 바로 출력시킬것
                                    MakeUserNicnamerecycle(Userlist_nicname);

                                    Log.d("json 파싱", "프로필 데이터 가져오기 성공");

                                } else {
                                    Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }





                    }else if(urlget2.equals("4")){ //과목 리스트
                        Log.d("onResponse ? ", "과목 리스트 : " + resultlist);

                        try {

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            Log.d("onResponse ? ","onResponse 과목 카테고리 리스트 : " + String.valueOf(jarray));
                            if (jarray.get(0) != null) {

                                //과목리스트 다이얼로그 만들기
                                ArrayList<String> categoreylist= new ArrayList<>(); //전체 과목 리스트
                                boolean[] categoreylistcheked = new boolean[jarray.length()]; //선택된 과목을 정하기 위함, true면 체크처리 됨

                                for(int i = 0; i<jarray.length(); i++) {
                                    JSONObject jobj = jarray.getJSONObject(i);

                                    categoreylist.add(jobj.get("subjectname").toString());


                                    for (int j = 0; j<SubjectSelectedItems.size();j++){

                                        //값이 있으면 - 해당 자리에 true 표시를 한다. true는 체크표시가 된다.
                                        if(SubjectSelectedItems.get(j).equals(String.valueOf(i))){
                                            categoreylistcheked[i] = true;
                                        }
                                    }
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
                                builder.setTitle("과목 선택");
                                builder.setMultiChoiceItems(categoreylist.toArray(new String[0]), categoreylistcheked, new DialogInterface.OnMultiChoiceClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int pos, boolean isChecked)
                                    {

                                        if(isChecked == true) // Checked 상태일 때 추가
                                        {
                                            Log.d("pos", String.valueOf(pos));
                                            SubjectSelectedItems.add(String.valueOf(pos)); //선택된 아이템을 담는 부분
                                        }
                                        else				  // Check 해제 되었을 때 제거
                                        {
                                            SubjectSelectedItems.remove(String.valueOf(pos));
                                        }

                                        Log.d("SubjectSelectedItems-----", String.valueOf(SubjectSelectedItems));
                                    }
                                });

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int pos)
                                    {
                                        Log.d("SubjectSelectedItems", String.valueOf(SubjectSelectedItems));
                                        Log.d("categoreylistcheked", String.valueOf(categoreylistcheked));



                                        if(SubjectSelectedItems.size() == 0){ //선택한 과목이 없으면
                                            CategoreyoneButton.setText("과목");
                                            CategoreyoneButton.setBackgroundResource(R.drawable.btndesign4);
                                            CategoreyoneButton.setTextColor(Color.parseColor("#3f3f3f"));


                                        }else{
                                            //선택한 과목을 숫자로 나타내서 변경해줄것.
                                            CategoreyoneButton.setText("과목 "+SubjectSelectedItems.size());
                                            CategoreyoneButton.setBackgroundResource(R.drawable.btndesign2);
                                            CategoreyoneButton.setTextColor(Color.parseColor("#FAFAFA"));
                                        }

                                        Userlist.clear();
                                        GetUserlist(limitnum, 0);
                                    }
                                });

                                AlertDialog alertDialog = builder.create();

                                alertDialog.show();

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

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //유저 닉네임 검색 리스트를 만든다
    public void MakeUserNicnamerecycle(ArrayList<JSONObject> Userlistjsonarray){
        Log.d("유저 닉네임검색 리스트 이다.", String.valueOf(Userlistjsonarray));

        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
        SearchRecyclerAdapter SearchAdapter = new SearchRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        SearchAdapter.needdata(num, searchtext);
        SearchAdapter.setSessionval(Sessionlist); //arraylist 연결
        SearchAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        TotalRecyclerView.setAdapter(SearchAdapter); //리사이클러뷰 위치에 어답터 세팅
    }



    //유저 리스트를 만든다
    public void MakeUserrecycle(ArrayList<JSONObject> Userlistjsonarray){
        Log.d("유저 리스트 이다.", String.valueOf(Userlistjsonarray));

        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
        SearchRecyclerAdapter SearchAdapter = new SearchRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        SearchAdapter.needdata(num, searchtext);
        SearchAdapter.setSessionval(Sessionlist); //arraylist 연결
        SearchAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        TotalRecyclerView.setAdapter(SearchAdapter); //리사이클러뷰 위치에 어답터 세팅
    }


    //게시글 리스트를 만든다.
    public void MakeNboardrecycle(ArrayList<JSONObject> Nboardlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Nboardlistjsonarray));

        TotalRecyclerView = (RecyclerView) view.findViewById(R.id.TotalRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity()); //그리드 매니저 선언
        SearchRecyclerAdapter SearchAdapter = new SearchRecyclerAdapter(getActivity().getApplicationContext()); //내가만든 어댑터 선언
        TotalRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        SearchAdapter.needdata(num, searchtext);
        SearchAdapter.setSessionval(Sessionlist); //arraylist 연결
        SearchAdapter.setRecycleList(Nboardlistjsonarray); //arraylist 연결
        TotalRecyclerView.setAdapter(SearchAdapter); //리사이클러뷰 위치에 어답터 세팅



        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        SearchAdapter.setOnItemClickListener(new SearchRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list) {

//                try {
//                    Log.d("", String.valueOf(list.get(position).get("idx")));
//                    Log.d("", String.valueOf(list.get(position).get("maincategorey")));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                Intent intent = new Intent(getActivity(), Nboardview.class);
                try {
                    //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
                    intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
                    intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, REQUESTCODE);

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


                 if(completeval.equals("0")){ // 0 nboardview에서 뒤로가기 했을때
                    //  Toast myToast = Toast.makeText(getContext(), "게시물이 1111.", Toast.LENGTH_SHORT);
                    // myToast.show();

                    pagenum = 0; //페이징 초기화
                    Nboardlist.clear();

                    GetNboardlist(limitnum, 0);

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

    //조건 초기화
    public void ConditionValueInit(){
        //리스트 조건 변수들
        //SelectType = 0;
        SelectGender = 0;
        SelectStudentId = 0;
        SelectStudentAges = 0;
        Selectminpay = "";
        Selectmaxpay = "";
        Selectnicname = "";
        Selectschool = "";
        SubjectSelectedItems.clear();
    }
}