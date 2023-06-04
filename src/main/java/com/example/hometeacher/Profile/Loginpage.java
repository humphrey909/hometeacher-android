package com.example.hometeacher.Profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hometeacher.MainActivity;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.SocketService;
import com.example.hometeacher.shared.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Loginpage extends AppCompatActivity {

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스
    Session oSession;

    Context mContext;

    EditText idedit, pwedit;
    TextView emailchktext, pwchktext;

    int emailchk;


    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        mContext = this;

        //initdelete(); //db 전체 초기화

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기

        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언
       // GlobalClass.Init(); // 초기화


        oSession = new Session(this);

        pwchktext = (TextView) this.findViewById(R.id.pwchktext);
        pwchktext.setVisibility(View.GONE);
        emailchktext = (TextView) this.findViewById(R.id.emailchktext);
        emailchktext.setVisibility(View.GONE);

        pwedit = (EditText) this.findViewById(R.id.pwedit);


        login();
        joinmove();
        pwsearchmove();
    }

    public void login(){
        ImageButton loginbtn = (ImageButton)findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(getApplicationContext(), Joinpage.class);
               // startActivity(intent);

                if(!String.valueOf(idedit.getText()).equals("") && !String.valueOf(pwedit.getText()).equals("")){

                    //이메일 형식이 맞지 않다면
                    if(emailchk == 0) {
                        emailchktext.setVisibility(View.VISIBLE);
                        emailchktext.setText("이메일 형식이 유효하지 않습니다. ");
                    }else{
                        emailchktext.setVisibility(View.GONE);
                        pwchktext.setVisibility(View.GONE);


                        Log.d("name", String.valueOf(idedit.getText()));
                        Log.d("인증된 패스워드", String.valueOf(pwedit.getText()));

                        RestapiStart(); //레트로핏 빌드
                        //RestapiRequest(3, ); //요청
                        call = retrofitService.loginstart(
                                1,
                                String.valueOf(idedit.getText()),
                                String.valueOf(pwedit.getText())
                        );
                        RestapiResponse(); //응답



                    }
                }else{
                    //이메일이 빈칸이라면
                    if(String.valueOf(idedit.getText()).equals("")){
                        emailchktext.setVisibility(View.VISIBLE);
                        emailchktext.setText("이메일 주소를 입력해주세요. ");
                    }else if(String.valueOf(pwedit.getText()).equals("")) { //비밀번호가 빈칸이라면
                        pwchktext.setVisibility(View.VISIBLE);
                        pwchktext.setText("비밀번호를 입력해 주세요. ");
                    }

                    //이메일 형식이 유효한지 체크
                    if(emailchk == 0) {
                        emailchktext.setVisibility(View.VISIBLE);
                        emailchktext.setText("이메일 형식이 유효하지 않습니다. ");
                    }else{
                        emailchktext.setVisibility(View.GONE);
                    }

                }
            }
        });


        //이메일 체크
        idedit = (EditText) this.findViewById(R.id.idedit);
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

                    emailchktext.setVisibility(View.GONE);

                } else {
                    Log.d("입력한 이메일", "비합격");
                    //이메일 아님!
                    emailchk = 0;
                    emailchktext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });
    }


    public void joinmove(){
        Button joingo = (Button)findViewById(R.id.joingo);
        joingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                Intent intent = new Intent(getApplicationContext(), Joinpage.class);
                startActivity(intent);
            }
        });
    }

    public void pwsearchmove(){
        Button pwsearchgo = (Button)findViewById(R.id.pwsearchgo);
        pwsearchgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Pwsearchpage.class);
                startActivity(intent);
            }
        });
    }
    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.closemenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                //case android.R.id.home: //뒤로가기
                   // finish();
                    //break;
                    case R.id.closetab: //창 닫기
                        finish();
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        startActivity(intent);
                        break;
                        default:
                            break;
            }

        return super.onOptionsItemSelected(item);
    }
    //이메일 형식 맞추기
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

                    if(urlget2.equals("1")){ //로그인 부분
                        Log.d("onResponse ? ","onResponse 로그인 여부 : " + resultlist);
                        try {
                            JSONObject obj = new JSONObject(resultlist);
                            //Log.d("onResponse ? ","onResponse 이메일 전송 : " + String.valueOf(obj));
                            //Log.d("json 파싱", String.valueOf(obj.length()));
                            //Log.d("json 파싱", String.valueOf(obj.get("result")));
                            //Log.d("json 파싱", String.valueOf(obj.get("user")));
                            // Log.d("json 파싱", String.valueOf(obj.get("CertifiNumber")));
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

                                //메인으로 이동
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("MoveValue", "0");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                                startActivity(intent);

                                //소켓연결
                                ArrayList<ArrayList<String>> Sessionlist = oSession.Getoneinfo("0");
                                   // GlobalClass.getService().SocketConnect();
                                Log.d("Sessionlist_get",Sessionlist.get(1).get(0)); //잘 가져옴


                                //소켓을 연결한다.
                                Intent serviceintent;
                                serviceintent =  new Intent(getApplicationContext(), SocketService.class);
                                startService(serviceintent);
                            }else{

                                Log.d("json 파싱", "로그인 실패");
                                Log.d("json 파싱", "로그인 실패");
                                String alerttext = "";

                                if(obj.get("err").equals("wrong password")){
                                    alerttext = "맞지않은 패스워드입니다.";
                                }else if(obj.get("err").equals("no user")){
                                    alerttext = "없는 유저입니다.";
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

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

    //전체를 초기화한다.
    public void initdelete() {
        oSession.Init(); //db 초기화
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Toast myToast = Toast.makeText(getApplicationContext(), "리다이렉트.", Toast.LENGTH_SHORT);
        // myToast.show();
        Log.d("-----mBound-----","onStart");

        // Bind to LocalService
       // Intent intent = new Intent(this, SocketService.class);
        //bindService(intent, GlobalClass.connection, Context.BIND_AUTO_CREATE);
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unbindService(connection);
//        mBound = false;
//    }
}
