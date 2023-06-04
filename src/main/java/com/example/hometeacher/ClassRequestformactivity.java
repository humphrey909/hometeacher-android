package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.shared.ClassSearchviewsShared;
import com.example.hometeacher.shared.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ClassRequestformactivity extends AppCompatActivity {
    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    Context oContext;
    Activity oActivity;

    TextView servicepay, commission, finalpay;
    ImageView closetab;
    Button paybootbtn;
    RadioButton paymentmethod1, paymentmethod2;
    RadioButton.OnClickListener clickListener;

    String GetType;
    String Getteacheruid;
    String Getfixpayment;
    String Getroomidx;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    ArrayList<JSONObject> ProfileInfo;
    ArrayList<JSONObject> ProfileImgInfo;

    ArrayList<Uri> profilemainimglist;

    EditText negotiationpayedit;
    LinearLayout negotiationpaylinear;

    Double amountpayment = 0d; //결제금액
    String androidkey = "62de592cd01c7e001b5eeed4";
    String paymentmethod = "";

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private String editpayresult="";

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ArrayList<String> AddUserIdx; //선택된 회원 idx 리스트

    Integer FixPayment; //결제 금액

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_requestformactivity);


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기

        try {
            division();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void division() throws ParseException {

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;

        servicepay = (TextView) findViewById(R.id.servicepay);
        commission = (TextView) findViewById(R.id.commission);
        finalpay = (TextView) findViewById(R.id.finalpay);

        AddUserIdx = new ArrayList<>();

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        GetType = intent.getExtras().getString("type"); //방 타입  //1. 과외문의 2. 내과외
        Getteacheruid = intent.getExtras().getString("teacheruid"); //선생님 uid

        Log.d("-------------GetType------------",GetType);
        Log.d("-------------Getteacheruid------------",Getteacheruid);

        negotiationpayedit = (EditText) findViewById(R.id.negotiationpayedit);
        negotiationpaylinear = (LinearLayout) findViewById(R.id.negotiationpaylinear);

        if(GetType.equals("1")){ //과외문의

        }else if(GetType.equals("2")){ //내과외
            negotiationpaylinear.setVisibility(View.GONE);
            Getfixpayment = intent.getExtras().getString("fixpayment"); //결제금액
            Getroomidx = intent.getExtras().getString("roomidx"); //rid



            FixPayment = Integer.parseInt(Getfixpayment)*1000; // 결제 금액 원단위로 수정

            amountpayment = Double.parseDouble(FixPayment.toString().replaceAll(",",""));
            Log.d("입력한 amountpayment", String.valueOf(amountpayment));


            int basicpay = (int) Math.round(amountpayment);
            String basicpay_format = decimalFormat.format(basicpay);
            int commissionpay = (int) ((int) Math.round(amountpayment)*3.5/100);
            String commissionpay_format = decimalFormat.format(commissionpay);
            int finalpay_ = basicpay+commissionpay;
            String finalpay_format = decimalFormat.format(finalpay_);
            servicepay.setText(basicpay_format+"원");
            commission.setText(commissionpay_format+"원");
            finalpay.setText(finalpay_format+"원");
            amountpayment = Double.parseDouble(String.valueOf(finalpay_));

        }

        //닫기
        closetab = (ImageView) findViewById(R.id.closetab);
        closetab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        //과외금액 입력시 계산하기
        negotiationpayedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                if(charSequence.toString().length() > 0) {
                    amountpayment = Double.parseDouble(charSequence.toString().replaceAll(",",""));
                    Log.d("입력한 amountpayment", String.valueOf(amountpayment));

                    int basicpay = (int) Math.round(amountpayment);
                    String basicpay_format = decimalFormat.format(basicpay);
                    int commissionpay = (int) ((int) Math.round(amountpayment)*3.5/100);
                    String commissionpay_format = decimalFormat.format(commissionpay);
                    int finalpay_ = basicpay+commissionpay;
                    String finalpay_format = decimalFormat.format(finalpay_);

                    servicepay.setText(basicpay_format+"원");
                    commission.setText(commissionpay_format+"원");

                    finalpay.setText(finalpay_format+"원");


                    amountpayment = Double.parseDouble(String.valueOf(finalpay_));
                }

                //콤마 붙이기
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(editpayresult)){ //빈값이 아닐때 변환된 값과 같지 않을때
                    editpayresult = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    negotiationpayedit.setText(editpayresult); //변환된 값을 저장
                    negotiationpayedit.setSelection(editpayresult.length()); //숫자를 입력하면 그 숫자만큼 커서 위치를 설정
                }
                Log.d("입력한 editpayresult", String.valueOf(editpayresult));

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });

        //결제수단 선택
        //paymentmethod1 = (RadioButton) this.findViewById(R.id.paymentmethod1);
        paymentmethod2 = (RadioButton) this.findViewById(R.id.paymentmethod2);
        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
//                    case R.id.paymentmethod1:
//                        Log.d("","카드결제");
//                        paymentmethod = "카드";
//
//                        break;
                    case R.id.paymentmethod2:
                        Log.d("","카카오페이");
                        paymentmethod = "카카오페이";

                        break;
                }
            }
        };
        //paymentmethod1.setOnClickListener(clickListener);
        paymentmethod2.setOnClickListener(clickListener);

        //결제하기
        paybootbtn = (Button) findViewById(R.id.paybootbtn);
        paybootbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("---------paymentmethod-------",paymentmethod);
                if(amountpayment != 0 && !paymentmethod.equals("")){
                    PaymentTest(v);
                }

            }
        });


        profilegetdata();
        profileimggetdata();
    }

//    String androidkey = "62de592cd01c7e001b5eeed4";
//    //String paymentmethod = "카카오페이";
//    String paymentmethod = "카드";






    public void PaymentTest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)


        List items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("과외").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(amountpayment);
        //BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(1000.0);
        items.add(item1);
        //items.add(item2);

        Payload payload = new Payload();
        payload.setApplicationId(androidkey)
                .setOrderName("부트페이 결제테스트")
                .setPg("나이스페이")
                .setMethod(paymentmethod)
                .setOrderId("1234")
                .setPrice(amountpayment)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));


        //결제후 결과를 띄워주는 부분 이다.
        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("bootpay", "error: " + data);
                    }


                    @Override
                    public void onClose() {
                        //Log.d("bootpay", "close: " + data);
                       Bootpay.removePaymentWindow();
                    }

//                    @Override
//                    public void onClose(String data) {
//                        Log.d("bootpay", "close: " + data);
//                        Bootpay.removePaymentWindow();
//                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                        try {
                            JSONObject jobj = new JSONObject(data);

                            if(String.valueOf(jobj.get("event")).equals("done")){
//                             Toast.makeText(oContext, String.valueOf(jobj.get("data")), Toast.LENGTH_SHORT).show();

                                if(GetType.equals("1")) { //과외문의
                                    //과외 방 생성 후 이동 까지 함.
                                    makemyclassroom();
                                }else if(GetType.equals("2")){

                                    //결제 후 데이터를 db에 저장할 것.
                                    //paymentlist db에 저장후 뒤로 백 할 것.
                                    savepaymentdata(String.valueOf(jobj.get("data")));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }).requestPayment();
    }

    //결제 완료후 데이터 저장하기.
    public void savepaymentdata(String payment_donedata) throws JSONException {

        JSONObject jobj_done = new JSONObject(payment_donedata);
        Log.d("bootpay_data", payment_donedata);
        Log.d("bootpay_data_2", String.valueOf(jobj_done));

        Log.d("bootpay_data_3", String.valueOf(jobj_done.get("order_id")));
        Log.d("bootpay_data_3", String.valueOf(jobj_done.get("receipt_id")));
        Log.d("bootpay_data_3", String.valueOf(jobj_done.get("price")));
        Log.d("bootpay_data_3", String.valueOf(jobj_done.get("method")));

        //receipt_id
        //order_id
        //price
        //


        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), Getroomidx);
        requestMap.put("rid", rid);

        RequestBody getuid = RequestBody.create(MediaType.parse("text/plain"), Getteacheruid);
        requestMap.put("getuid", getuid); //결제 받은 사람 = 선생님

        RequestBody giveuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("giveuid", giveuid); //결제 보낸 사람 = 나

        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jobj_done.get("price")));
        requestMap.put("price", price);

        RequestBody receipt_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jobj_done.get("receipt_id")));
        requestMap.put("receiptid", receipt_id);

        RequestBody order_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jobj_done.get("order_id")));
        requestMap.put("order_id", order_id);

        RequestBody activate = RequestBody.create(MediaType.parse("text/plain"), "1");
        requestMap.put("activate", activate);


        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

       // AddUserIdx.add(String.valueOf(Sessionlist.get(1).get(0)));

//        //추가된 유저가 있을때만 - 학생
//        if(!AddUserIdx.isEmpty()){
//            RequestBody userlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(AddUserIdx));
//            requestMap.put("userlist", userlist);
//        }

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.savepaymentdata(
                4,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //나의 과외 방을 생성한다.
    public void makemyclassroom() throws JSONException {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ProfileInfo.get(0).get("idx"))); //방 만들 사람 id - 선생님
        requestMap.put("myuid", myuid);

        String roomname = String.valueOf(ProfileInfo.get(0).get("name")+" 선생님의 과외");
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), roomname);
        requestMap.put("name", name);
        RequestBody maxnum = RequestBody.create(MediaType.parse("text/plain"), "2");
        requestMap.put("maxnum", maxnum);

        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        AddUserIdx.add(String.valueOf(Sessionlist.get(1).get(0)));

        //추가된 유저가 있을때만 - 학생
        if(!AddUserIdx.isEmpty()){
            RequestBody userlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(AddUserIdx));
            requestMap.put("userlist", userlist);
        }

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.makemyclassroom(
                3,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //프로필 정보 가져오는 서버통신
    public void profilegetdata() throws ParseException {

        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Getteacheruid));
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1)); //선생님
        requestMap.put("uid", uid);
        requestMap.put("usertype", usertype);


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
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Getteacheruid));
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));

        requestMap.put("uid", uid);
        requestMap.put("usertype", usertype);

        call = retrofitService.profilegetimglist(
                2, requestMap
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

                    if (urlget2.equals("1")) { //선생님 프로필 가져온다.

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
                    }else if(urlget2.equals("2")) { //프로필 이미지 리스트
                        Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 : " + String.valueOf(resultlist));


                        try {
                            ProfileImgInfo = new ArrayList<>();

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
                    }else if(urlget2.equals("3")) { //과외방 생성
                        Log.d("onResponse ? ", "과외방 생성 : " + String.valueOf(resultlist));

                        try {
                            JSONObject jobj = new JSONObject(resultlist);

                            //방만들고 그 방으로 이동하기!!!
                          //  Intent intent = new Intent(oContext, MainActivity.class);
//                            intent.putExtra("MoveValue", "3");
//
//                            intent.putExtra("roommaketype", "1"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
//                            intent.putExtra("roomidx", String.valueOf(jobj.get("roomidx"))); //룸 고유번호
//                            //intent.putExtra("Tchatcount", String.valueOf(list.get(position).get("totalchatcount"))); //총 채팅 갯수
//
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                            startActivity(intent);


                            //방만들고 그 방으로 이동하기!!!
                            Intent intent = new Intent(oContext, Myclassroomactivity.class);
                            intent.putExtra("roommaketype", "1"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
                            intent.putExtra("roomidx", String.valueOf(jobj.get("roomidx"))); //룸 고유번호
                            //intent.putExtra("Tchatcount", String.valueOf(list.get(position).get("totalchatcount"))); //총 채팅 갯수

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            //startActivityForResult(intent, REQUESTCODE);


                            //문의하기 방 종료
                            Classinquiryroomactivity MA = (Classinquiryroomactivity)Classinquiryroomactivity.oActivity_classinquiryroom;
                            MA.finish();
                            //현재 activity 종료
                            finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //



                    }else if(urlget2.equals("4")) { //결제 완료 후 데이터 저장
                        Log.d("onResponse ? ","결제 완료 후 데이터 저장 : " + String.valueOf(resultlist));

                        finish();


                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    //프로필 데이터를 가져올때 사용한다. ProfileInfo에 값이 있어야 프로필의 정보가 보여진다.
    //서버 통신은 처음 페이지에 왔을때, 다시 돌아왔을때만 서버통신을 함. oncreate, onstart
    @SuppressLint("SetTextI18n")
    public void datachange() throws JSONException {
        Log.d("onResponse ? ", "onResponse 프로필 리스트 -3 : " + String.valueOf(ProfileInfo));
        TextView profilepay, name, schoolinfo, subjectinfo, introducedocu, schoolchk, schooldocu, campusinfo, characteredocu, subject, monthpaydocu, monthpaystandard, subjectdocu, classstyle, skillapeal, availabletime, age;

        name = (TextView) findViewById(R.id.name);
        schoolinfo = (TextView) findViewById(R.id.schoolinfo);
        subjectinfo = (TextView) findViewById(R.id.subjectinfo);
        profilepay = (TextView) findViewById(R.id.profilepay);
        name.setText(String.valueOf(ProfileInfo.get(0).get("name")+" 선생님("+ProfileInfo.get(0).get("nicname")+")"));

        String genderchar = gender_replace(String.valueOf(ProfileInfo.get(0).get("gender")));
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
        subjectinfo.setText(Subjectcomplete_);

        profilepay.setText(String.valueOf(ProfileInfo.get(0).get("minpay"))+"만원");
    }
    //프로필 이미지 데이터를 가져올때 사용한다. ProfileImgInfo에 값이 있어야 프로필의 정보가 보여진다.
    //서버 통신은 처음 페이지에 왔을때, 다시 돌아왔을때만 서버통신을 함. oncreate, onstart
    public void imgdatachange() throws JSONException {
        ImageView profilemainimg;

        profilemainimg = (ImageView) findViewById(R.id.profilemainimg);


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
    //성별 문자열로 변환
    public String gender_replace(String gender){
        if(gender.equals("1")){
            return "남";
        }else{
            return "여";
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