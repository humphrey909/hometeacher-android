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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.shared.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Joinpage extends AppCompatActivity {

    Session oSession;

    //RadioGroup usertypegroup;
    CheckBox agreechk1, agreechk2;
    RadioButton usertype1, usertype2;
    RadioButton.OnClickListener clickListener;

    String Usertypejoin = "0";
    int Pwconditionchk = 0; //패스워드 조건확인변수
    int Pwsamechk = 0; //패스워드 위아래 같은지 확인변수

    Button emailsendbtn, contifibtn, joinbtn;
    TextView idtext, certifitimer, certifichktext, pw1chktext, pw2chktext, agreelink1, agreelink2;
    LinearLayout certifibox;
    EditText idedit, contifiedit, nameedit, pw1edit, pw2edit;
    //private GameManager m;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    String sendemail = "";//humphrey1858@gmail.com
    RetrofitService retrofitService;
    Call<String> call;

    String CertificationNumber;
    int initTimer = 180; //타이머 초기화 변수
    int CertificationTimer = 180; // 스레드로 인해 변화되는 타이머 변수

    Handler CertifiHandler; // 인증번호 핸들러
    Thread Certifithread; // 인증번호 스레드
    int Threadstart = 0; //인증번호 전송에서 스레드 처음에만 실행시키기 위해 처음을 알기위한 변수

    int emailchk; //이메일 체크 변수
    int CertifiComplete = 0; // 인증번호 인증 완료여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinpage);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        oSession = new Session(this);

        agreechk1 = (CheckBox) this.findViewById(R.id.agreechk1);
        agreechk2 = (CheckBox) this.findViewById(R.id.agreechk2);

        pw1chktext = (TextView) this.findViewById(R.id.pw1chktext);
        pw2chktext = (TextView) this.findViewById(R.id.pw2chktext);
        pw1chktext.setVisibility(View.GONE);
        pw2chktext.setVisibility(View.GONE);

        nameedit = (EditText) this.findViewById(R.id.nameedit);

        idtext = (TextView) this.findViewById(R.id.idtext);
        idedit = (EditText) this.findViewById(R.id.idedit);
        certifitimer = (TextView) this.findViewById(R.id.certifitimer);
        certifichktext = (TextView) this.findViewById(R.id.certifichktext);
        certifichktext.setVisibility(View.GONE);


        contifiedit = (EditText) this.findViewById(R.id.contifiedit);
        certifibox = (LinearLayout) this.findViewById(R.id.certifibox);
        certifibox.setVisibility(View.GONE); //인증박스 안보이게 처리

       // Log.d("Tag", String.valueOf(idedit.getText()));


        agreelink1 = (TextView) this.findViewById(R.id.agreelink1);
        agreelink1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("","이용약관");
                Intent intent = new Intent(getApplicationContext(), Agreetotermsofuse.class);
                startActivity(intent);
            }
        });

        agreelink2 = (TextView) this.findViewById(R.id.agreelink2);
        agreelink2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("","개인정보수집");
                Intent intent = new Intent(getApplicationContext(), Agreepersonalinformation.class);
                startActivity(intent);
            }
        });



        //이메일 전송
        emailsendbtn = (Button) this.findViewById(R.id.emailsendbtn);
        emailsendbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendemail = String.valueOf(idedit.getText());
                if(!sendemail.equals("") && emailchk == 1){

                    Log.d("ttt", sendemail);
                    //Log.d("ttt", "1");
                    RestapiStart(); //레트로핏 빌드

                    //보낼값
                    HashMap<String, RequestBody> requestMap = new HashMap<>();
                    RequestBody sendemailval = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sendemail));
                    RequestBody certifytype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1)); //회원가입에서는 이메일이 없어야 하고, 비밀번호 찾기에서는 이메일이 등록되어 있어야함.

                    requestMap.put("sendemail", sendemailval);
                    requestMap.put("certifytype", certifytype);

                    call = retrofitService.emailsender(1, requestMap); //넘길 데이터를 작성
                    //RestapiRequest(1,sendemail); //요청
                    RestapiResponse(); //응답

                    //Context context = getApplicationContext();
                   //CharSequence text = "이메일이 전송되었습니다.";
                   // int duration = Toast.LENGTH_SHORT;
                    //Toast toast = Toast.makeText(context, text, duration);
                }else{
                    //이메일 작성 해주세요,
                    certifichktext.setVisibility(View.VISIBLE);
                    certifichktext.setText("이메일을 확인해주시기 바랍니다.");



                    //Log.d("ttt", "2");
                    //Toast myToast = Toast.makeText(getApplicationContext(),"이메일을 입력해주시기 바랍니다.", Toast.LENGTH_SHORT);

                }
            }
        });

        //인증번호 확인
        contifibtn = (Button) this.findViewById(R.id.contifibtn);
        contifibtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //인증번호 맞을 경우
                if(CertificationNumber.equals(String.valueOf(contifiedit.getText()))){
                    Log.d("certifichk", "확인");


                    idedit.setEnabled(false);

                    emailsendbtn.setText("인증 완료");
                    emailsendbtn.setBackgroundResource(R.drawable.btndesign1);
                    //certifibox.setVisibility(View.GONE); //인증박스 안보이게 처리
                    certifichktext.setVisibility(View.GONE);
                    certifibox.setVisibility(View.GONE);


                    //타이머 종료
                    CertifiComplete = 1;


                }else{
                    CertifiComplete = 2; //인증번호가 틀렸을때
                    Log.d("certifichk", "틀림 - "+CertificationNumber);
                    Log.d("certifichk", "틀림 - "+String.valueOf(contifiedit.getText()));
                }
            }
        });

        //이메일 체크
        idedit.addTextChangedListener(new TextWatcher() {
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
                    emailsendbtn.setBackgroundResource(R.drawable.btndesign2);

                } else {
                    Log.d("입력한 이메일", "비합격");
                    //이메일 아님!
                    emailchk = 0;
                    emailsendbtn.setBackgroundResource(R.drawable.btndesign1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });


        //인증번호 갯수 체크
        contifiedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                String certifinum = charSequence.toString();
                Log.d("입력한 인증번호", certifinum);

                //Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

                //if(certifinum.length() == 6){
                    //og.d("입력한 인증번호", "합격");
                    //인증번호 갯수 확인!
                    //emailchk = 1;
                 //   contifibtn.setBackgroundResource(R.drawable.btndesign2);

                //} else {
                    //Log.d("입력한 인증번호", "비합격");
                    //인증번호 갯수 불확!
                    //emailchk = 0;
                //    contifibtn.setBackgroundResource(R.drawable.btndesign1);
               // }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });

        usertype1 = (RadioButton) this.findViewById(R.id.usertype1);
        usertype2 = (RadioButton) this.findViewById(R.id.usertype2);
        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.usertype1:
                        Log.d("","선생님 가입");
                        Usertypejoin = "1"; //품목 탭 입력
                        break;
                    case R.id.usertype2:
                        Log.d("","학생 가입");
                        Usertypejoin = "2"; //품목 탭 입력
                        break;
                }
            }
        };
        usertype1.setOnClickListener(clickListener);
        usertype2.setOnClickListener(clickListener);

        //패스워드1 영문+숫자+8자리 조합
        pw1edit = (EditText)findViewById(R.id.pw1edit);
        pw1edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                String pw1 = charSequence.toString();
                Log.d("입력한 인증번호", pw1);
                String pwchk = isValidPassword(pw1);
                Log.d("pwchk", pwchk);

                if(pwchk.equals("1")){ //조건이 맞을때
                    Pwconditionchk = 1;
                    pw1chktext.setVisibility(View.GONE);
                }else{ //조건이 맞지 않을때
                    Pwconditionchk = 0;
                    pw1chktext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });


        //패스워드2 pw1 = pw2 여부 체크
        pw2edit = (EditText)findViewById(R.id.pw2edit);
        pw2edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                String pw2 = charSequence.toString();
                Log.d("입력한 인증번호", pw2);

                String pw1 = pw1edit.getText().toString();

                if(pw2.equals(pw1)) { //패스워드가 같을때
                    Pwsamechk = 1;
                    pw2chktext.setVisibility(View.GONE);
                }else{ //패스워드가 다를때
                    Pwsamechk = 0;
                    pw2chktext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });


        //회원가입 승인
        joinbtn = (Button) this.findViewById(R.id.joinbtn);
        joinbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //회원가입 조건
                if(CertifiComplete == 1 && Pwconditionchk == 1 && Pwsamechk == 1 && agreechk1.isChecked() && agreechk2.isChecked() && !Usertypejoin.equals("0") && !String.valueOf(nameedit.getText()).equals("")){

                    //
                    Log.d("joinbtn", String.valueOf(Usertypejoin));
                    Log.d("name", String.valueOf(nameedit.getText()));
                    Log.d("email", String.valueOf(idedit.getText()));
                    Log.d("이메일 인증 여부", String.valueOf(CertifiComplete));


                    Log.d("패스워드 인증 조건확인 여부", String.valueOf(Pwconditionchk));
                    Log.d("패스워드 인증 같은지 여부", String.valueOf(Pwsamechk));
                    Log.d("인증된 패스워드", String.valueOf(pw2edit.getText()));

                    //true or false
                    Log.d("agreechk1", String.valueOf(agreechk1.isChecked()));
                    Log.d("agreechk2", String.valueOf(agreechk2.isChecked()));

                    RestapiStart(); //레트로핏 빌드
                    //RestapiRequest(3, ); //요청
                    call = retrofitService.joinstart(
                            3,
                            Integer.parseInt(Usertypejoin),
                            1,
                            String.valueOf(nameedit.getText()),
                            "jojo",
                            String.valueOf(idedit.getText()),
                            String.valueOf(pw2edit.getText())
                    );
                    RestapiResponse(); //응답

                }
            }
        });





        //핸들러 생성
        MakeHandler();
    }

    //비밀번호 규칙
    public static String isValidPassword(String password) {
        // 최소 8자, 최대 20자 상수 선언
        final int MIN = 8;
        final int MAX = 20;

        // 영어, 숫자, 특수문자 포함한 MIN to MAX 글자 정규식
        final String REGEX =
                "^((?=.*\\d)(?=.*[a-zA-Z])(?=.*[\\W]).{" + MIN + "," + MAX + "})$";
        // 3자리 연속 문자 정규식
        //final String SAMEPT = "(\\w)\\1\\1";
        // 공백 문자 정규식
        final String BLANKPT = "(\\s)";

        // 정규식 검사객체
        Matcher matcher;

        // 공백 체크
        if (password == null || "".equals(password)) {
            //return "Detected: No Password";

            return "0";
        }

        // ASCII 문자 비교를 위한 UpperCase
        String tmpPw = password.toUpperCase();
        // 문자열 길이
        int strLen = tmpPw.length();

        // 글자 길이 체크
        if (strLen > 20 || strLen < 8) {
            //return "Detected: Incorrect Length(Length: " + strLen + ")";

            return "0";
        }

        // 공백 체크
        matcher = Pattern.compile(BLANKPT).matcher(tmpPw);
        if (matcher.find()) {
            //return "Detected: Blank";
            return "0";
        }

        // 비밀번호 정규식 체크
        matcher = Pattern.compile(REGEX).matcher(tmpPw);
        if (!matcher.find()) {
            //return "Detected: Wrong Regex";

            return "0";
        }


        return "1";
        // 동일한 문자 3개 이상 체크
        //matcher = Pattern.compile(SAMEPT).matcher(tmpPw);
        //if (matcher.find()) {
        //    return "Detected: Same Word";
        //}

        // 연속된 문자 / 숫자 3개 이상 체크
        /*if(){
            // ASCII Char를 담을 배열 선언
            int[] tmpArray = new int[strLen];

            // Make Array
            for (int i = 0; i < strLen; i++) {
                tmpArray[i] = tmpPw.charAt(i);
            }

            // Validation Array
            for (int i = 0; i < strLen - 2; i++) {
                // 첫 글자 A-Z / 0-9
                if ((tmpArray[i] > 47
                        && tmpArray[i + 2] < 58)
                        || (tmpArray[i] > 64
                        && tmpArray[i + 2] < 91)) {
                    // 배열의 연속된 수 검사
                    // 3번째 글자 - 2번째 글자 = 1, 3번째 글자 - 1번째 글자 = 2
                    if (Math.abs(tmpArray[i + 2] - tmpArray[i + 1]) == 1
                            && Math.abs(tmpArray[i + 2] - tmpArray[i]) == 2) {
                        char c1 = (char) tmpArray[i];
                        char c2 = (char) tmpArray[i + 1];
                        char c3 = (char) tmpArray[i + 2];
                        return "Detected: Continuous Pattern: \"" + c1 + c2 + c3 + "\"";
                    }
                }
            }
            // Validation Complete
            return ">>> All Pass";
        }*/
    }


    //세번째 Retrofit
    //레트로핏 라이브러리를 사용해 보낼 양식 만들어 놓음
    public void RestapiStart(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송

    }
    //양식대로 restkey와 검색어를 보내고 결과값을 받음
//    public void RestapiRequest(Integer type, String sendemail){
//        if(type == 1){
//            call = retrofitService.emailsender(type, sendemail); //넘길 데이터를 작성
//        }
//    }

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

                    int emailsendaccess = 0; //이메일 전송 가능 여부
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
                                emailsendaccess = 1;
                                CertificationNumber = obj.get("CertifiNumber").toString();
                                Log.d("json 파싱", "이메일 전송 가능");
                            }else{
                                emailsendaccess = 0;
                                emailsendaccess_err = obj.get("err").toString();
                                Log.d("json 파싱", "이메일 전송 불가능");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //이메일 전송 가능 상태  = 1
                        if(emailsendaccess == 1){
                            //타이머 스레드 시작 하는 부분
                            //emailsendbtn.setText("재전송");
                            //certifibox.setVisibility(View.VISIBLE); //인증박스 보이게 처리
                            //certifichktext.setVisibility(View.GONE);


                            if(Threadstart == 0){ //처음에만 스레드 실행
                                CertifiTimerThread();
                                Threadstart = 1;
                            }else{ //변수로 체크하여 멈추는 것 처럼 보이게 할 것
                                CertificationTimer = initTimer;
                            }
                        }else{
                            //전송불가 상태
                            certifichktext.setVisibility(View.VISIBLE);
                            certifichktext.setText(emailsendaccess_err);
                        }
                    }else if(urlget2.equals("3")){ //회원가입 진행 후 retrun
                        Log.d("onResponse ? ","onResponse 회원가입여부 : " + resultlist);
                        try {
                            //회원가입 후 자동 로그인 하는 법은????
                            JSONObject obj = new JSONObject(resultlist);
                            if(obj.get("result").equals(true)){
                                //emailsendaccess = 1;
                                //CertificationNumber = obj.get("CertifiNumber").toString();
                                Log.d("json 파싱", "로그인 성공");

                                //로그인 진행.!!!!!!
                                //내부저장소에 id값을 저장함.
                                ArrayList<String> Logindata = new ArrayList<String>();
                                Logindata.add(obj.get("idx").toString());
                                Logindata.add(obj.get("id").toString());

                                Logindata.add(obj.get("usertype").toString());
                                //Logindata.add(obj.get("pid").toString());
                                Logindata.add(obj.get("name").toString());
                                Logindata.add(obj.get("nicname").toString());
                                Logindata.add(obj.get("loginregdate").toString());


                                //oSession.Save(obj.get("email").toString()); //로그인 세션 저장
                                oSession.Save(Logindata); //로그인 세션 저장

                                //finish();
                                //메인으로 이동
                                Intent intent = new Intent(getApplicationContext(), Profilewrite.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                                intent.putExtra("type", 1); //수정
                                startActivity(intent);
                            }else{
                                //emailsendaccess = 0;
                                //emailsendaccess_err = obj.get("err").toString();
                                Log.d("json 파싱", "로그인 실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
//                        startActivity(intent);

                        //oSession.Save(Logindata); //로그인 세션 저장

                        //finish();


                        //finish();
                        //바로 로그인 진행할것


                       // Log.d("onResponse ? ","onResponse 중복체크" + CertificationNumber);
                       // Log.d("onResponse ? ","onResponse 중복체크");
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
                        if (CertificationTimer < 0) { //타이머 끝났을때

                            Message msg = CertifiHandler.obtainMessage();
                            msg.what = 2;
                            msg.obj = "timer";
                            CertifiHandler.sendMessage(msg);

                            //Certifithread.interrupt();

                        }else if(CertifiComplete == 1){ //인증이 완료 되었을때


//                            Message msg = CertifiHandler.obtainMessage();
//                            msg.what = 3;
//                            msg.obj = "timer";
//                            CertifiHandler.sendMessage(msg);

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
                        Log.d("444", Integer.toString(msg.arg2));
                        certifichktext.setVisibility(View.VISIBLE);
                        certifichktext.setText("인증번호가 맞지 않습니다.");
                    }else{
                        certifichktext.setVisibility(View.GONE);
                    }

                    emailsendbtn.setText("재전송");
                    certifibox.setVisibility(View.VISIBLE); //인증박스 보이게 처리

                    String timerval = timertrans(msg.arg1); //변환된 타이머 문자 가져옴

                    //idtext.setText("이메일 "+timerval);
                    certifitimer.setText(timerval);
                    Log.d("타이머 숫자", timerval);
                }else if(msg.what == 2){
                    emailsendbtn.setText("전송");
                    certifibox.setVisibility(View.GONE); //인증박스 안보이게 처리
                }else if(msg.what == 3){
                    //emailsendbtn.setText("인증 완료");
                    //emailsendbtn.setBackgroundResource(R.drawable.btndesign1);
                    //certifibox.setVisibility(View.GONE); //인증박스 안보이게 처리
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






    public void toast(){
        Toast.makeText(this, "전송되었습니다.", Toast.LENGTH_SHORT).show();
        //finish();
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
}