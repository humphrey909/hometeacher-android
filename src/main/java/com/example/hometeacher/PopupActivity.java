package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.hometeacher.shared.GlobalClass;

public class PopupActivity extends AppCompatActivity {
    TextView txtText;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스
    static Activity oActivity_popup;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication();
        oActivity_popup = this;
        
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        txtText.setText(data);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        //Intent intent = new Intent();
        //intent.putExtra("result", "Close Popup");
        //setResult(RESULT_OK, intent);
        //GlobalClass.popnum = 0;
        //액티비티(팝업) 닫기
        finish();
    }
    
    //다른 액티비티에서 닫기
    public void mOnClose(){
        //데이터 전달하기
        //Intent intent = new Intent();
        //intent.putExtra("result", "Close Popup");
        //setResult(RESULT_OK, intent);
        GlobalClass.popnum = 0;
        //액티비티(팝업) 닫기
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}