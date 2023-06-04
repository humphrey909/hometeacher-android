package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hometeacher.Adapter.PaymentRecyclerAdapter;
import com.example.hometeacher.Adapter.ReviewWriteviewRecyclerAdapter;
import com.example.hometeacher.Profile.Profileview;
import com.example.hometeacher.shared.Session;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import kr.co.bootpay.model.response.ResDefault;
import kr.co.bootpay.model.response.data.VerificationData;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PaymentlistActivity extends AppCompatActivity {
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

    String TokenValue; //부트페이에서 발급받은 토큰 값
    String Receipt_Id; //부트페이에서 받은 거래 영수증 id

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ArrayList<JSONObject> MyPaymentlist;
    RecyclerView PaymentRecyclerView;
    NestedScrollView nestedscrollbox;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentlist);

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
        PaymentRecyclerView = (RecyclerView) findViewById(R.id.PaymentRecyclerView);

        //인텐트 데이터를 받는다.
        //Intent intent = getIntent();
        //roomidx = intent.getExtras().getString("roomidx");


        MyPaymentlist = new ArrayList<>();

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

                    GetPaymentlist(limitnum, offsetnum);
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

                    String spliturl = url.split("/")[2];
                    if(spliturl.equals("api.bootpay.co.kr")){
                        String spliturl2 = url.split("/")[3];

                        if(spliturl2.equals("request")){ //bootpay 토큰 가져오기

                            String resultlist = response.body();
                            Log.d("onResponse ? ", "결제 토큰 받아오기 : " + resultlist);
                            try {
                                JSONObject jobj = new JSONObject(String.valueOf(resultlist));
                                Log.d("onResponse ? ", "결제 토큰 받아오기 : " + jobj.get("data"));
                                JSONObject jobj2 = new JSONObject(String.valueOf(jobj.get("data")));
                                Log.d("onResponse ? ", "결제 토큰 받아오기 : " + jobj2.get("token")); //토큰값!!!!!!!!!!


                                TokenValue = String.valueOf(jobj2.get("token"));
                                receipt_payment(TokenValue, Receipt_Id); //결제 검증하는 부분


                                Log.d("onResponse ? ", "TokenValue : " + TokenValue);
                                Log.d("onResponse ? ", "TokenValue : " + Receipt_Id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }else if(spliturl2.equals("receipt")){ //bootpay 결제 검증
                            Log.d("onResponse ? ", "결제 검증 : " + response);

                            String resultlist = response.body();
                            Log.d("onResponse ? ", "결제 검증 : " + resultlist);

                            JSONObject jobj = null;
                            try {
                                jobj = new JSONObject(String.valueOf(resultlist));
                                Log.d("onResponse ? ", "결제 토큰 받아오기 : " + jobj.get("data"));
                                JSONObject jobj2 = new JSONObject(String.valueOf(jobj.get("data")));
                               // Log.d("onResponse ? ", "결제 토큰 받아오기 : " + jobj2.get("token")); //토큰값!!!!!!!!!!


                                //jobj2  취소할때 전송해줄 데이터
                                cancle_payment(jobj2); //취소 요청하는 부분

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(spliturl2.equals("cancel.json")){ //bootpay 결제 취소
                            Log.d("onResponse ? ", "bootpay 결제 취소 : " + response);

                            String resultlist = response.body();
                            Log.d("onResponse ? ", "bootpay 결제 취소 : " + resultlist);




                            //db 결제 취소 설정 해주기
                            updatepaymentdata(Receipt_Id); //
                        }

                    }else {

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

                                    MyPaymentlist.add(jobj);
                                }

                                Log.d("onResponse ? ", "내가 작성한 리뷰 리스트 : all" + String.valueOf(MyPaymentlist)); //info

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //데이터를 바로 출력시킬것
                            MakePaymentrecycle(MyPaymentlist);

                        } else if (urlget2.equals("2")) { //결제 취소
                            Log.d("onResponse ? ", "결제 취소  : " + resultlist);

                            Toast.makeText(oContext, "환불이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            //취소 후 새로고침 할것.

                            MyPaymentlist.clear();
                            GetPaymentlist(limitnum, 0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //리뷰 리스트를 만든다.
    public void MakePaymentrecycle(ArrayList<JSONObject> Paymentlistjsonarray){
        Log.d("결제 리스트 이다.", String.valueOf(Paymentlistjsonarray));


        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        PaymentRecyclerAdapter oPaymentRecyclerAdapter = new PaymentRecyclerAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        PaymentRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        oPaymentRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oPaymentRecyclerAdapter.setRecycleList(Paymentlistjsonarray); //arraylist 연결
        PaymentRecyclerView.setAdapter(oPaymentRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oPaymentRecyclerAdapter.setOnItemClickListener(new PaymentRecyclerAdapter.OnItemClickListener() {
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

                }else{ // 2. 환불 버튼

                    try {
                        if(String.valueOf(list.get(position).get("activate")).equals("1")){
                            //다이어그램 띄우기
                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                            builder.setTitle("환불 요청").setMessage("해당 결제 내역을 환불 하시겠습니까?");

                            //삭제 실시
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    //결제 수정한다. 그리고 결제 환불 진행한다.
                                    try {
//                                        //updatepaymentdata(String.valueOf(list.get(position).get("idx")));
//
//
//                                        //결제 취소 진행할 것.
//                                        //goCancel(String.valueOf(list.get(position).get("receiptid")));

                                        Receipt_Id = String.valueOf(list.get(position).get("receiptid"));
//
                                        getTokenbootpay();
//
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public static void goCancel(String receiptId){

        //Bootpay bootpay = new Bootpay("62de592cd01c7e001b5eeed6", "UwQraZI57R65bkTT5sPp6YWDgC43RHdC29TaoH4DuMs=");




        //getTokenbootpay


//
//
//        //검증
//        try {
//            bootpay.getAccessToken(); //토큰 가져오기 - 안됨.
//            ResDefault<HashMap<String, Object>> res = bootpay.verify(receiptId); //결제 검증하기
//           // System.out.println(res.toJson());
//            Log.d("str",String.valueOf(res.toJson()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        try {
////            HttpResponse res = (HttpResponse) bootpay.verify(receiptId);
////            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
////            Log.d("str",String.valueOf(str));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//
//
//            Log.d("----receiptId----", String.valueOf(receiptId));
//
//            Cancel cancel = new Cancel();
//            cancel.receiptId = receiptId;
//            cancel.name = "관리자";
//            cancel.reason = "테스트 결제";
//    //        cancel.price = 1000.0; //부분취소 요청시
//    //        cancel.cancelId = "12342134"; //부분취소 요청시, 중복 부분취소 요청하는 실수를 방지하고자 할때 지정
//    //        RefundData refund = new RefundData(); // 가상계좌 환불 요청시, 단 CMS 특약이 되어있어야만 환불요청이 가능하다.
//    //        refund.account = "675601012341234"; //환불계좌
//    //        refund.accountholder = "홍길동"; //환불계좌주
//    //        refund.bankcode = BankCode.getCode("국민은행");//은행코드
//    //        cancel.refund = refund;
//
//        try {
//            HttpResponse res = (HttpResponse) bootpay.receiptCancel(cancel);
//            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
//            Log.d("str2",String.valueOf(str));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //토큰 발급
    public void getTokenbootpay(){

        //프로필 정보 가져오는 서버통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bootpay.co.kr/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송


        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody application_id = RequestBody.create(MediaType.parse("text/plain"), "62de592cd01c7e001b5eeed6");
        requestMap.put("application_id", application_id);
        RequestBody private_key = RequestBody.create(MediaType.parse("text/plain"), "UwQraZI57R65bkTT5sPp6YWDgC43RHdC29TaoH4DuMs=");
        requestMap.put("private_key", private_key);

        call = retrofitService.getTokenbootpay(
                requestMap
        );
        RestapiResponse(); //응답
    }

    //결제 검증
    public void receipt_payment(String TokenValue, String Receipt_Id){

        //프로필 정보 가져오는 서버통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bootpay.co.kr/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송


        call = retrofitService.receipt_payment(
                Receipt_Id,
                TokenValue
        );
        RestapiResponse(); //응답
    }

    //결제 취소 - bootpay 취소 요청
    public void cancle_payment(JSONObject receiptdata) throws JSONException { //검증후 해당 데이터 가져옴

        //프로필 정보 가져오는 서버통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bootpay.co.kr/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송

        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody receipt_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(receiptdata.get("receipt_id")));
        requestMap.put("receipt_id", receipt_id);
        RequestBody cancel_id = RequestBody.create(MediaType.parse("text/plain"), "");
        requestMap.put("cancel_id", cancel_id);
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(receiptdata.get("price")));
        requestMap.put("price", price);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(receiptdata.get("name")));
        requestMap.put("name", name);
        RequestBody reason = RequestBody.create(MediaType.parse("text/plain"), "테스트 결제 취소");
        requestMap.put("reason", reason);




        call = retrofitService.cancle_payment(
                requestMap,
                TokenValue
        );
        RestapiResponse(); //응답
    }




    //결제 취소 - db수정
    public void updatepaymentdata(String Receipt_Id){

        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        HashMap<String, RequestBody> requestMap = new HashMap<>();
        //RequestBody paymentidx_ = RequestBody.create(MediaType.parse("text/plain"), paymentidx);
       // requestMap.put("paymentidx", paymentidx_);
        RequestBody receipt_id = RequestBody.create(MediaType.parse("text/plain"), Receipt_Id);
        requestMap.put("receipt_id", receipt_id);
        RequestBody cancletype = RequestBody.create(MediaType.parse("text/plain"), "0");
        requestMap.put("cancletype", cancletype);
        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        call = retrofitService.updatepaymentdata(
                2, requestMap
        );
        RestapiResponse(); //응답
    }

    //내가 작성한 리뷰 리스트
    public void GetPaymentlist(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody giveuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("giveuid", giveuid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getpaymentlist(
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

        MyPaymentlist.clear();
        GetPaymentlist(limitnum, 0);
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