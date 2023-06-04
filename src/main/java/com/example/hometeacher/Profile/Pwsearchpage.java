package com.example.hometeacher.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Pwsearchpage extends AppCompatActivity {

    Button emailsendbtn, certifibtn;
    TextView certifitimer, emailchktext, certifichktext;
    EditText emailedit, certifiedit;
    LinearLayout certifylinear;

    int emailchk; //이메일 체크 변수
    int CertifiComplete = 0; // 인증번호 인증 완료여부 / 0. 스레드 시작 전

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    String sendemail = "";//humphrey1858@gmail.com
    RetrofitService retrofitService;
    Call<String> call;

    String CertificationNumber;
    String CertificationEmail;
    int initTimer = 180; //타이머 초기화 변수
    int CertificationTimer = 180; // 스레드로 인해 변화되는 타이머 변수
    Handler CertifiHandler; // 인증번호 핸들러
    Thread Certifithread; // 인증번호 스레드
    int Threadstart = 0; //인증번호 전송에서 스레드 처음에만 실행시키기 위해 처음을 알기위한 변수

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwsearchpage);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        division();
    }


    public void division(){

        certifylinear = (LinearLayout) this.findViewById(R.id.certifylinear);
        certifylinear.setVisibility(View.GONE);
        emailsendbtn = (Button)findViewById(R.id.emailsendbtn);
        certifibtn = (Button)findViewById(R.id.certifibtn);
        certifibtn.setVisibility(View.GONE);

        certifitimer = (TextView) this.findViewById(R.id.certifitimer);
        emailchktext = (TextView) this.findViewById(R.id.emailchktext);
        emailchktext.setVisibility(View.GONE);

        certifichktext = (TextView) this.findViewById(R.id.certifichktext);
        certifichktext.setVisibility(View.GONE);

        certifiedit = (EditText) this.findViewById(R.id.certifiedit); //인증번호 체크
        //certifiedit.setEnabled(false);
        certifiedit.setVisibility(View.GONE);

        MakeHandler();

        //이메일 체크
        emailedit = (EditText) this.findViewById(R.id.emailedit);
        emailedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                String email = charSequence.toString();
                Log.d("입력한 이메일", email);

                //Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

                if(isEmail(email)){
                    Log.d("입력한 이메일", "합격");
                    //이메일 맞음!
                    emailchk = 1;
                   // emailedit.setBackgroundResource(R.drawable.inputborder);
                    emailsendbtn.setBackgroundResource(R.drawable.btndesign2);

                } else {
                    Log.d("입력한 이메일", "불합격");
                    //이메일 아님!
                    emailchk = 0;
                    //emailedit.setBackgroundResource(R.drawable.inputborder2);



                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });



        //certifibtn 인증번호 확인 버튼
        certifibtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Log.d("certifichk", "인증여부 - "+CertificationNumber);
                Log.d("certifichk", "인증여부 - "+String.valueOf(certifiedit.getText()));

                //인증번호 맞을 경우
                if(CertificationNumber.equals(String.valueOf(certifiedit.getText()))){
                    Log.d("certifichk", "확인");

                    emailedit.setEnabled(false);
                    certifiedit.setEnabled(false);

                    emailsendbtn.setText("비밀번호 변경하기");
                    emailsendbtn.setBackgroundResource(R.drawable.btndesign2);
                    certifichktext.setVisibility(View.GONE);
                    certifitimer.setVisibility(View.GONE);

                    certifibtn.setText("인증 완료");
                    certifibtn.setBackgroundResource(R.drawable.btndesign1);


                    //타이머 종료
                    CertifiComplete = 1; //인증 완료
                    Log.d("CertifiComplete", "CertifiComplete - "+CertifiComplete);
                }else{
                    CertifiComplete = 2; //인증번호가 틀렸을때
                    Log.d("CertifiComplete", "CertifiComplete - "+CertifiComplete);
                    Log.d("certifichk", "틀림 - "+CertificationNumber);
                    Log.d("certifichk", "틀림 - "+String.valueOf(certifiedit.getText()));
                }
            }
        });

        //메일전송
        emailsendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //입력한 이메일이 로그인 정보와 맞는지 체크한다.
                if(emailchk == 1) {


                    if(CertifiComplete == 1){//이메일, 인증번호 확인이 됐다면 비밀번호 변경 페이지로 이동한다.
                         Intent intent = new Intent(getApplicationContext(), Pwchangepage.class);
                         intent.putExtra("email", CertificationEmail); //유저 아이디 보낼 것.
                         startActivityForResult(intent, REQUESTCODE);
                    }else{ //인증전 이메일로 인증번호 보내는 구간
                        sendemail = String.valueOf(emailedit.getText());
                        Log.d("ttt", sendemail);
                        //Log.d("ttt", "1");
                        RestapiStart(); //레트로핏 빌드
                        //보낼값
                        HashMap<String, RequestBody> requestMap = new HashMap<>();
                        RequestBody sendemailval = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sendemail));
                        RequestBody certifytype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(2)); //회원가입에서는 이메일이 없어야 하고, 비밀번호 찾기에서는 이메일이 등록되어 있어야함.

                        requestMap.put("sendemail", sendemailval);
                        requestMap.put("certifytype", certifytype);


                        call = retrofitService.emailsender(1, requestMap); //넘길 데이터를 작성
                        RestapiResponse(); //응답
                    }

                }else{
                    emailchktext.setVisibility(View.VISIBLE);
                    emailchktext.setText("이메일을 입력해주시기 바랍니다.");
                   // Toast myToast = Toast.makeText(getApplicationContext(),"이메일을 입력해주시기 바랍니다.", Toast.LENGTH_SHORT);
                   // myToast.show();
                }
            }
        });
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
                    String urlget = url.split("/")[6];
                    String urlget2 = urlget.split("=")[1];
                    //Log.d("onResponse ? ","onResponse 성공" + Arrays.toString(urlget));
                    Log.d("onResponse ? ","onResponse 성공" + urlget2);



                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제

                    String emailsendaccess_err = ""; //이메일 전송 가능 여부 - 이유
                    if(urlget2.equals("1")){ //이메일 전송
                        //CertificationNumber
                        Log.d("onResponse ? ","onResponse 이메일 전송 : " + resultlist);
                        //{"result":true,"CertifiNumber":"ldfv1b"}
                        try {
                            JSONObject obj = new JSONObject(resultlist);
                            //Log.d("onResponse ? ","onResponse 이메일 전송 : " + String.valueOf(obj));
                            //Log.d("json 파싱", String.valueOf(obj.length()));
                            //Log.d("json 파싱", String.valueOf(obj.get("result")));
                            // Log.d("json 파싱", String.valueOf(obj.get("CertifiNumber")));
                            if(obj.get("result").equals("true")){
                                CertificationNumber = obj.get("CertifiNumber").toString();
                                CertificationEmail = obj.get("CertificationEmail").toString();
                                Log.d("json 파싱", "이메일 전송 가능");


                                CertifiComplete = 3;
                                if(Threadstart == 0){ //처음에만 스레드 실행
                                    CertifiTimerThread();
                                    Threadstart = 1;
                                }else{ //변수로 체크하여 멈추는 것 처럼 보이게 할 것
                                    CertificationTimer = initTimer;
                                    certifiedit.setText("");
                                }


                                emailchktext.setVisibility(View.GONE);
                                certifibtn.setVisibility(View.VISIBLE);
                            }else{
                                emailsendaccess_err = obj.get("err").toString();
                                Log.d("json 파싱", "이메일 전송 불가능");

                                if(emailsendaccess_err.equals("no user")){
                                    emailchktext.setVisibility(View.VISIBLE);
                                    emailchktext.setText("존재하지 않는 유저입니다.");
                                }
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


    //스레드가 먼저 실행
    public void CertifiTimerThread(){
        Certifithread = new Thread(){
            public void run(){
                while(true){
                    try{
                        //0 스레드 전
                        //1 인증완료
                        //2 인증 안됨
                        //3 스레드 멈추고 다시
                        if(CertifiComplete != 1) { //인증이 완료 되었을때
                            if(CertifiComplete == 3){

                                if (CertificationTimer < 0) { //타이머 끝났을때

                                    Message msg = CertifiHandler.obtainMessage();
                                    msg.what = 2;
                                    msg.obj = "timer";
                                    CertifiHandler.sendMessage(msg);

                                    //Certifithread.interrupt();
                                }else{
                                    CertificationTimer--;

                                    //메서드로 획득한 메시지 객체에 보내고자 하는 데이터를 채우는 것
                                    //메시지의 target이 핸들러 자신으로 지정된 Message 객체 리턴
                                    Message msg = CertifiHandler.obtainMessage();
                                    msg.what = 1;
                                    // msg.obj = "timer";
                                    msg.arg1 = CertificationTimer;
                                    msg.arg2 = CertifiComplete;
                                    CertifiHandler.sendMessage(msg);

                                    Thread.sleep(1000);

                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Certifithread.start();
    }


    public void MakeHandler() {

        //광고 이미지 변경 핸들러
        CertifiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO : process device message.

                if (msg.what == 1) {
                    if(msg.arg2 == 2){ //인증번호가 틀렸을때

                        //안먹었음...
                        Log.d("CertifiHandler", Integer.toString(msg.arg2));
                        certifichktext.setVisibility(View.VISIBLE);
                        certifichktext.setText("인증번호가 맞지 않습니다.");

                      //  Toast myToast = Toast.makeText(getApplicationContext(),"인증번호가 맞지 않습니다.", Toast.LENGTH_SHORT);
                      //  myToast.show();
                    }else{
                        certifichktext.setVisibility(View.GONE);
                    }

                    emailsendbtn.setText("이메일 재전송");
                    certifylinear.setVisibility(View.VISIBLE);
                    certifiedit.setVisibility(View.VISIBLE);

                    String timerval = timertrans(msg.arg1); //변환된 타이머 문자 가져옴

                    //idtext.setText("이메일 "+timerval);
                    certifitimer.setText(timerval);
                    Log.d("타이머 숫자", timerval);
                }
            }
        };
    }
    //타이머 변환기
    public String timertrans(Integer timernum){
        int minute = timernum/60;
        int second = timernum%60;

        return minute+":"+second;
    }
    public boolean isEmail(String email){
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()){
            returnValue = true;
        }
        return returnValue;
    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.closemenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    //편집 클릭 후 리턴되는 값을 인지한다.
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULTCODE1) { //편집 클릭후 완료인지 확인하는 부분

                String backval = resultIntent.getStringExtra("backval");
                Log.d("뒤로가기 여부 ", backval);
                if(backval.equals("1")){ //편집 수정을 했을때만 서버 통신으로 다시 불러옴
                    //초기화 시킬 것
                    emailedit.setEnabled(true);
                    certifiedit.setEnabled(true);

                    emailchktext.setVisibility(View.GONE);
                    certifichktext.setVisibility(View.GONE);
                    certifiedit.setVisibility(View.GONE);
                    certifibtn.setVisibility(View.GONE);
                    certifylinear.setVisibility(View.GONE);
                    emailedit.setText("");
                    certifibtn.setText("인증");
                    certifibtn.setBackgroundResource(R.drawable.btndesign1);
                    emailsendbtn.setText("이메일 전송하기");

                    CertifiComplete = 0; //스레드가 돌고 있고 처음부터 시작
                }
            }
        }
    }
}