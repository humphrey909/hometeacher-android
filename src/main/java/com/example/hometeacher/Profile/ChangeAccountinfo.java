package com.example.hometeacher.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.shared.Session;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangeAccountinfo extends AppCompatActivity {

    Session oSession; //자동로그인을 위한 db
    ArrayList<ArrayList<String>> Sessionlist;
    Context oContext;

    String edittype = "";
    String value = "";

    TextView toolbartitle;
    EditText nameedit;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    int RESULTCODE_EDIT = 1;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_accountinfo);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기



        division();
    }

    public void division(){
        oContext = this;
        oSession = new Session(oContext);
        Sessionlist = oSession.Getoneinfo("0");

        this.activity = this;

        toolbartitle = (TextView) this.findViewById(R.id.toolbartitle);
        nameedit = (EditText) this.findViewById(R.id.nameedit);


        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        edittype = intent.getExtras().getString("edittype");
        value = intent.getExtras().getString("value");

        if(edittype.equals("name")){
            toolbartitle.setText("이름 변경");
            nameedit.setText(value);
            Log.d("-----------edittype------------",edittype);
            Log.d("-----------value------------",value);
        }

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
                    Log.d("onResponse ? ", "onResponse 데이터 저장 여부 : " + resultlist);


                    if (urlget2.equals("1")) { //저장완료
                        Log.d("onResponse ? ", "유저 저장 여부 : " + resultlist);

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profilewritemenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
            finish();
            break;
            case R.id.completebtn: //완료


                //보낼값
                HashMap<String, RequestBody> requestMap = new HashMap<>();
                RequestBody value = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nameedit.getText()));
                RequestBody field = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(edittype));
                RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));

                requestMap.put("value", value);
                requestMap.put("field", field);
                requestMap.put("uid", uid);

                RestapiStart(); //레트로핏 빌드

                call = retrofitService.updateaccount(
                        1, requestMap
                );
                RestapiResponse(); //응답


                //전 activity로 값을 리턴
                Intent resultIntent = new Intent();
                resultIntent.putExtra("completeval", "1");
                activity.setResult(RESULTCODE_EDIT, resultIntent);
                activity.finish();

                //변경후 세션값을 변경하기
                oSession.EditSession("0", 3, String.valueOf(nameedit.getText()));


                //finish();
                
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}