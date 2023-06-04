package com.example.hometeacher.Profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Pwchangepage extends AppCompatActivity {

    Context oContext;
    Activity activity;

    EditText newpwedit1, newpwedit2;
    TextView pw1chktext, pw2chktext;
    Button pwchangebtn;

    int Pwconditionchk = 0; //패스워드 조건확인변수
    int Pwsamechk = 0; //패스워드 위아래 같은지 확인변수

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    String CertifyEmail;

    int RESULTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwchangepage);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기


        division();

       // pwchangecomplete();
    }
    public void division(){
        oContext = this;
        this.activity = this;
      //  oSession = new Session(oContext);
       // Sessionlist = oSession.Getoneinfo("0");
      //  this.activity = this;

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        CertifyEmail = intent.getExtras().getString("email");
        //Log.d("CertifyEmail", "----------------------건너온 정보들------------------------");
        //Log.d("CertifyEmail", String.valueOf(CertifyEmail));


        pw1chktext = (TextView) this.findViewById(R.id.pw1chktext);
        pw2chktext = (TextView) this.findViewById(R.id.pw2chktext);
        pw1chktext.setVisibility(View.GONE);
        pw2chktext.setVisibility(View.GONE);


        newpwedit1 = (EditText)findViewById(R.id.newpwedit1);
        newpwedit2 = (EditText)findViewById(R.id.newpwedit2);

        newpwedit1.addTextChangedListener(new TextWatcher() {
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

                String pw2 = newpwedit2.getText().toString();

                if(pwchk.equals("1")) { //조건이 맞을때
                    Pwconditionchk = 1;
                    pw1chktext.setVisibility(View.GONE);
                }else{ //조건이 맞지 않을때
                    Pwconditionchk = 0;
                    pw1chktext.setVisibility(View.VISIBLE);
                }

                //두번째 비밀번호가 빈칸이 아닐때
                if(!newpwedit2.getText().toString().equals("")) {
                    if (!pw1.equals(pw2)) { //조건이 맞지만 위, 아래 비밀번호가 같지 않을때
                        Pwsamechk = 0;
                        pw2chktext.setVisibility(View.VISIBLE);
                    } else {
                        Pwsamechk = 1;
                        pw2chktext.setVisibility(View.GONE);
                    }
                }

                if(Pwconditionchk == 1 && Pwsamechk == 1){
                    pwchangebtn.setBackgroundResource(R.drawable.btndesign2);
                }else{
                    pwchangebtn.setBackgroundResource(R.drawable.btndesign1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });

        newpwedit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                String pw2 = charSequence.toString();
                Log.d("입력한 인증번호", pw2);

                String pw1 = newpwedit1.getText().toString();

                if(pw2.equals(pw1)) { //패스워드가 같을때
                    Pwsamechk = 1;
                    pw2chktext.setVisibility(View.GONE);
                }else{ //패스워드가 다를때
                    Pwsamechk = 0;
                    pw2chktext.setVisibility(View.VISIBLE);
                }

                if(Pwconditionchk == 1 && Pwsamechk == 1){
                    pwchangebtn.setBackgroundResource(R.drawable.btndesign2);
                }else{
                    pwchangebtn.setBackgroundResource(R.drawable.btndesign1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });
        pwchangebtn = (Button) this.findViewById(R.id.pwchangebtn);
        pwchangebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //비밀번호변경 조건
                if(Pwconditionchk == 1 && Pwsamechk == 1) {

                    //문자열 변환 - 이메일, 새패스워드
                    HashMap<String, RequestBody> requestMap = new HashMap<>();
                    RequestBody CertifyEmailval = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(CertifyEmail));
                    requestMap.put("CertifyEmail", CertifyEmailval);
                    RequestBody newpw = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(newpwedit2.getText())); //새패스워드
                    requestMap.put("newpw", newpw);
                    RequestBody loginchk = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0)); //로그인 체크 - 1 로그인 된 상태, 0 로그아웃된 상태
                    requestMap.put("loginchk", loginchk);

                    RestapiStart(); //레트로핏 빌드
                    call = retrofitService.updatepassword(
                            1, requestMap
                    );
                    RestapiResponse(); //응답

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
    //레트로핏 데이터 응답 부분
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
                    Log.d("onResponse ? ","onResponse 저장 여부 : " + resultlist);



                    if(urlget2.equals("1")){ //비밀번호 변경
                        try {
                            JSONObject resultobj = new JSONObject(resultlist);
                            Log.d("onResponse ? ","저장 여부: " + resultobj.get("result"));

                            String alerttext = "";
                            if(String.valueOf(resultobj.get("result")).equals("true")){

                                //변경이 되면 로그인 페이지로 이동함 / 다 지우고
                                Intent intent = new Intent(getApplicationContext(), Loginpage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                Log.d("onResponse ? ","저장 여부: " + "저장되었습니다.");

                            }else{
                                //변경에 실패 하면

                                Log.d("onResponse ? ","저장 여부: " + "패스워드가 맞지 않습니다.");
                                alerttext = "패스워드가 맞지 않습니다.";

                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                                builder.setTitle("알림").setMessage(alerttext);

                                AlertDialog alertDialog = builder.create();

                                alertDialog.show();
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
                Intent resultIntent = new Intent();
                resultIntent.putExtra("backval", "1");
                activity.setResult(RESULTCODE, resultIntent);
                activity.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}