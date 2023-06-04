package com.example.hometeacher.navigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.hometeacher.MyReviewList;
import com.example.hometeacher.PaymentlistActivity;
import com.example.hometeacher.Profile.Accountsetview;
import com.example.hometeacher.Alertpage;
import com.example.hometeacher.GradeGideline;
import com.example.hometeacher.MainActivity;
import com.example.hometeacher.Nboard.MyNboardList;
import com.example.hometeacher.Profile.Profileview;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.shared.Session;
import com.squareup.picasso.Picasso;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypageFragment extends Fragment {
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    MainActivity mainActivity;

    Session oSession; //자동로그인을 위한 db
    ArrayList<ArrayList<String>> Sessionlist;
    Context oContext;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch profileviewchkswitch;
    TextView usernametext, usertypetext, useremailtext, nametext, emailtext, profileviewchktitle, profileviewchktext;
    Button rankguidebtn, mynboardlistbtn, reviewlistbtn, paymentlistbtn;
    ImageButton accontsetbtn, profilebtn;

    LinearLayout profileviewchktitlelinear;
    RadioGroup profileviewchkgroup;
    RadioButton profileviewchk1, profileviewchk2, profileviewchk3;
    RadioButton.OnClickListener clickListener;

    ImageView profileimg;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    Toolbar appbar;

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    View view;

    String searchviewval; //과외 찾기 구함 타입 변경
    ArrayList<JSONObject> ProfileInfo;

    int AlertCount;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Object HomeFragment;

    public MypageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MypageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MypageFragment newInstance(String param1, String param2) {
        MypageFragment fragment = new MypageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setHasOptionsMenu(true);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mypage, container, false);

        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).Firstaccesschk(5);
        division(view);

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void division(View view){
        GlobalClass = (com.example.hometeacher.shared.GlobalClass) mainActivity.getApplication(); //글로벌 클래스 선언

        oContext = view.getContext();
        oSession = new Session(oContext);

        //자동 로그인 하기
        Sessionlist = oSession.Getoneinfo("0");
        //툴바 설정
        appbar = (Toolbar) view.findViewById(R.id.toolbarbox);

        //계정 설정
        profileimg = (ImageView) view.findViewById(R.id.profileimg);
        nametext = (TextView) view.findViewById(R.id.nametext);
        emailtext = (TextView) view.findViewById(R.id.emailtext);

        profileviewchkswitch = (Switch) view.findViewById(R.id.profileviewchkswitch);

        profileviewchktitlelinear = (LinearLayout) view.findViewById(R.id.profileviewchktitlelinear);
        profileviewchkgroup = (RadioGroup) view.findViewById(R.id.profileviewchkgroup);
        profileviewchk1 = (RadioButton) view.findViewById(R.id.profileviewchk1);
        profileviewchk2 = (RadioButton) view.findViewById(R.id.profileviewchk2);
        profileviewchk3 = (RadioButton) view.findViewById(R.id.profileviewchk3);
        profileviewchktext = (TextView) view.findViewById(R.id.profileviewchktext);
        mynboardlistbtn = (Button) view.findViewById(R.id.mynboardlistbtn);
        reviewlistbtn = (Button) view.findViewById(R.id.reviewlistbtn);
        paymentlistbtn = (Button) view.findViewById(R.id.paymentlistbtn);

        //회원 상태 체크
        //if(Sessionlist.isEmpty()) { //쉐어드에 값이 없다면 = 비회원상태임
         //   Log.d("Sessionlist", String.valueOf("Sessionlist"));

        //}/else{ //값이 있으면 = 회원 상태임
        //세션이 없으면 로그인 페이지 띄울 것

        if(Sessionlist.get(1).get(2).equals("1")){ //선생님
            profileviewchkswitch.setVisibility(View.VISIBLE);
            profileviewchkgroup.setVisibility(View.GONE);
            profileviewchktitlelinear.setVisibility(View.GONE);
            reviewlistbtn.setVisibility(View.GONE);
        }else{ //학생
            profileviewchkswitch.setVisibility(View.GONE);
            profileviewchkgroup.setVisibility(View.VISIBLE);
            profileviewchktitlelinear.setVisibility(View.VISIBLE);
            reviewlistbtn.setVisibility(View.VISIBLE);
        }
            Log.d("Sessionlist", String.valueOf(Sessionlist));

            String userchar = usertypeselect(Sessionlist.get(1).get(2));
            emailtext.setText(Sessionlist.get(1).get(1));
            nametext.setText(Sessionlist.get(1).get(3)+" "+userchar+ "("+Sessionlist.get(1).get(4)+")");

            resetprofileimg();
            ClassSearchViewGet();

        //프로필 보기 버튼
        profilebtn = (ImageButton) view.findViewById(R.id.profileviewbtn);
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Profileview.class); //선생일때
                intent.putExtra("sendtype", "1");  //내 유저 idx
                startActivity(intent);


                //리턴 받을 데이터를 보낸다
//                Intent intent = new Intent(getContext(), Accountsetview.class);
//                intent.putExtra("type", 1); //수정
//               // intent.putExtra("profileidx", projectidx); //수정
//                getActivity().startActivityForResult(intent, REQUESTCODE);
            }
        });

        //계정 설정 버튼
        accontsetbtn = (ImageButton) view.findViewById(R.id.accontsetbtn);
        accontsetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(getActivity(), Accountsetview.class); //선생일때
                //startActivity(intent);

                Intent intent = new Intent(getContext(), Accountsetview.class);
                intent.putExtra("type", 1); //수정
               // intent.putExtra("profileidx", projectidx); //수정
                startActivityForResult(intent, REQUESTCODE);
               // ((MainActivity) getActivity()).moveToReviewWrite();
            }
        });

//        //등급안내 버튼
//        rankguidebtn = (Button) view.findViewById(R.id.rankguidebtn);
//        rankguidebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), GradeGideline.class);
//                startActivity(intent);
//            }
//        });

        //게시판 수정 리스트
        mynboardlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyNboardList.class);
                startActivity(intent);
            }
        });

        //작성한 리뷰 보기 버튼
        reviewlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyReviewList.class);
                startActivity(intent);
            }
        });

        //결제한 정보 보기
        paymentlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentlistActivity.class);
                startActivity(intent);
            }
        });

        //과외 구함 상태 체크 - 선생일때
        profileviewchkswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked){ //true
                    searchviewval = "2";
                    Log.d("isChecked", String.valueOf(isChecked));
                    ClassSearchViewUpdate();
                }else{ //false
                    searchviewval = "1";
                    Log.d("isChecked", String.valueOf(isChecked));
                    ClassSearchViewUpdate();
                }
            }
        });


        //과외 구함 상태 체크 - 학생일때
        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.profileviewchk1:
                        searchviewval = "3";
                        profileviewchktext.setText("노출");
                        Log.d("","과외급함");
                        ClassSearchViewUpdate();
                        break;
                    case R.id.profileviewchk2:
                        searchviewval = "2";
                        profileviewchktext.setText("노출");
                        Log.d("","생각중");
                        ClassSearchViewUpdate();
                        break;
                    case R.id.profileviewchk3:
                        searchviewval = "1";
                        profileviewchktext.setText("미노출");
                        Log.d("","생각없음");
                        ClassSearchViewUpdate();
                        break;
                }
            }
        };
        profileviewchk1.setOnClickListener(clickListener);
        profileviewchk2.setOnClickListener(clickListener);
        profileviewchk3.setOnClickListener(clickListener);

    }

    //과외 찾기, 구함 보여주기 타입 변경하는 함수
    public void ClassSearchViewUpdate(){
        //문자열 변환
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid);
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(2)));
        requestMap.put("usertype", usertype);
        RequestBody searchviewval_ = RequestBody.create(MediaType.parse("text/plain"), searchviewval);
        requestMap.put("searchviewval", searchviewval_);

        //서버 접근 - 프로필 정보 하나 업데이트
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.profileupdateone(
                2, requestMap
        );
        RestapiResponse(); //응답
    }
    //과외 찾기, 구함 보여주기 타입 가져오는 함수
    public void ClassSearchViewGet(){
        //문자열 변환
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid);
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(2)));
        requestMap.put("usertype", usertype);

        //서버 접근 - 프로필 정보 하나 업데이트
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.profilegetlist(
                3, requestMap
        );
        RestapiResponse(); //응답
    }

    //서버에서 이미지를 다시 불러오는 함수
    public void resetprofileimg(){
        //문자열 변환
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid);
        RequestBody imgtype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));
        requestMap.put("imgtype", imgtype);

        //서버 접근 - 이미지 변경
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.profilegetimglist(
                1, requestMap
        );
        RestapiResponse(); //응답
    }


    //fragment에 menu 추가
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        appbar.inflateMenu(R.menu.mainalertmenu);

        appbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.alerttab:
                    Intent intent = new Intent(getActivity(), Alertpage.class);
                    startActivity(intent);
                    //
                    // Navigate to settings screen
                    return true;
                default:
                    return false;
            }
        });
    }



    public String usertypeselect(String usertype){
        String usertypechar = "";
        if(usertype.equals("1")){
            usertypechar = "선생님";
        }else if(usertype.equals("2")) {
            usertypechar = "학생";
        }
        return usertypechar;
    }



    //전체를 초기화한다.
    public void initdelete() {
        oSession.Init(); //db 초기화
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
                if(response.isSuccessful()){
                    Log.d("onResponse ? ","onResponse 성공" + response);
                    // Log.d("onResponse ? ","onResponse 성공" + response.errorBody());
                    // Log.d("onResponse ? ","onResponse 성공" + response.message());
                    // Log.d("onResponse ? ","onResponse 성공" + response.code());
                    Log.d("onResponse ? ","onResponse 성공" + response.raw().request().url());
                    String url = response.raw().request().url().toString();
                    String urlget = url.split("/")[8];
                    String urlget2 = urlget.split("=")[1];
                    //Log.d("onResponse ? ","onResponse 성공" + Arrays.toString(urlget));
                    Log.d("onResponse ? ","onResponse 성공" + urlget2);

                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제

                    if(urlget2.equals("1")){ //로그인 부분
                        try {
                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            if(jarray.get(0) != null){
                                JSONObject jsonobj = jarray.getJSONObject(0);
                                Log.d("onResponse ? ","프로필 이미지 list 리스트 : " + String.valueOf(jarray));
                                Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(jsonobj.get("basicuri")));

                                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jsonobj.get("basicuri")+jsonobj.get("src");
                                Uri imageUri = Uri.parse(imagestring);
                                //가져온 데이터 하나를 바로 출력
                                Picasso.get()
                                        .load(imageUri) // string or uri 상관없음
                                        .resize(200, 200)
                                        .centerCrop()
                                        .into(profileimg);


                                Log.d("json 파싱", "프로필 이미지 list 성공");

                            }else{
                                Log.d("json 파싱", "프로필 이미지 list  실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //프로필 과외 찾기 보여주는 값을 변경
                        try {
                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            //JSONArray jarray = new JSONArray(resultlist);
                            JSONObject obj = new JSONObject(resultlist);
                            if(obj.get("result").equals(true)){
                                //JSONObject jsonobj = jarray.getJSONObject(0);
                                Log.d("onResponse ? ","프로필 과외 찾기 보여주는 값 : " + String.valueOf(obj.get("result")));

                                Log.d("json 파싱", "프로필 이미지 list 성공");

                            }else{
                                Log.d("json 파싱", "프로필 이미지 list  실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("3")){ //내 프로필 정보 조인해서 가져오기


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
                                Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(ProfileInfo.get(0).get("getstate")));
                                //데이터를 바로 출력시킬것

                                if(ProfileInfo.get(0).get("usertype").equals("1")){ //선생님
                                    if(ProfileInfo.get(0).get("getstate").equals("1")){
                                        profileviewchkswitch.setChecked(false);
                                    }else if(ProfileInfo.get(0).get("getstate").equals("2")){
                                        profileviewchkswitch.setChecked(true);
                                    }
                                }else{//학생
                                    if(ProfileInfo.get(0).get("getstate").equals("1")){
                                        profileviewchk3.setChecked(true);
                                        profileviewchktext.setText("노출");
                                    }else if(ProfileInfo.get(0).get("getstate").equals("2")){
                                        profileviewchk2.setChecked(true);
                                        profileviewchktext.setText("노출");
                                    }else if(ProfileInfo.get(0).get("getstate").equals("3")){
                                        profileviewchk1.setChecked(true);
                                        profileviewchktext.setText("미노출");
                                    }
                                }

                                Log.d("json 파싱", "프로필 데이터 가져오기 성공");

                            } else {
                                Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("4")) { //알림 체크하는 부분
                        Log.d("onResponse ? ", "알림 체크하는 부분 : " + resultlist);

                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","알림 체크하는 부분  : " + String.valueOf(jobj.get("count")));

                            AlertCount = Integer.parseInt(String.valueOf(jobj.get("count")));
                            if(AlertCount == 0){
                                appbar.getMenu().clear();
                                appbar.inflateMenu(R.menu.mainalertmenu);
                            }else{
                                appbar.getMenu().clear();
                                appbar.inflateMenu(R.menu.mainalertmenu_ck);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    //통신 실패
                    Log.d("onResponse ? ","onResponse 실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //통신 실패
                Log.d("onFailure",t.getMessage());
            }
        });
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

    @Override
    //편집 클릭 후 리턴되는 값을 인지한다.
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
       // Log.d("뒤로가기 여부 ", "backval");
        Log.d("뒤로가기 여부 ", "backval");
        Log.d("뒤로가기 여부 ", String.valueOf(requestCode));
        Log.d("뒤로가기 여부 ", String.valueOf(resultCode));

       // String backval = resultIntent.getStringExtra("backval");
       // Log.d("완료여부 ", backval);


        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULTCODE1) { //편집 클릭후 완료인지 확인하는 부분

                String backval = resultIntent.getStringExtra("backval");
                Log.d("뒤로가기 여부 ", backval);
                if(backval.equals("1")){ //편집 수정을 했을때만 서버 통신으로 다시 불러옴

                   // Toast myToast = Toast.makeText(getContext(), "뒤로가기 리턴.", Toast.LENGTH_SHORT);
                   // myToast.show();

                    resetprofileimg();

                    division(view);


                }else{ //로그아웃 후 리턴 받는 함수
                    // Toast myToast = Toast.makeText(getContext(), "뒤로가기 리턴.", Toast.LENGTH_SHORT);
                   //  myToast.show();
                    //Log.d("----getBound3-----",String.valueOf(GlobalClass.getBound()));
                   ((MainActivity)getActivity()).replaceFragment();

                   //

                     //Intent intent = new Intent(oContext.getApplicationContext(), Loginpage.class);
                    // startActivity(intent);
                }
            }
        }
    }

    //toolbar 변경할때 사용 - 서비스에서 알림이 오면 변경함
    public void ChangeToolbarmenu(){
        appbar.getMenu().clear();
        appbar.inflateMenu(R.menu.mainalertmenu_ck);
    }
}