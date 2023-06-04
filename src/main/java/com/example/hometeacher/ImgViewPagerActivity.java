package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hometeacher.Adapter.ImageSliderAdapter;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ImgViewPagerActivity extends AppCompatActivity {
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    String GetimgArrChar; //받은 json으로 구성된 이미지 문자열
    int Getposition; //받은 위치

    ArrayList<String> ImgArraylistbox; //출력할 이미지 arraylist
//    private String[] images = new String[] {
//            "https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg",
//            "https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg",
//            "https://cdn.pixabay.com/photo/2020/03/08/21/41/landscape-4913841_1280.jpg",
//            "https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg",
//            "https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgview_pager);

        //뷰페이지 위치 불러오고
        sliderViewPager = (ViewPager2) findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        ImgArraylistbox = new ArrayList<>(); //이미지를 담을 변수

        //데이터 받음
        Intent intent = getIntent();
        GetimgArrChar = intent.getExtras().getString("sendimg");
        Getposition = intent.getExtras().getInt("position");
        Log.d("-------sendimg-------", String.valueOf(GetimgArrChar));

        //받은 데이터를 jsonarray로 풀리지가 않아서 array로 풀었음. ㅎ.
        String imgarr = GetimgArrChar.substring(0,GetimgArrChar.length()-1); //마지막 문자 삭제
        String imgarr2 = imgarr.substring(1); //첫번째 문자 삭제
        String imgarr3 = imgarr2.replaceAll(" ", ""); //모든 공백 제거
        String[] sendimgarr = imgarr3.split(",");
        Log.d("-------sendimg-------jarray", String.valueOf(sendimgarr[0]));

        ImgArraylistbox.addAll(Arrays.asList(sendimgarr));


        sliderViewPager.setOffscreenPageLimit(1);
        sliderViewPager.setAdapter(new ImageSliderAdapter(this, ImgArraylistbox));
        sliderViewPager.setCurrentItem(Getposition);

        //슬라이드시 위치를 알려줌
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                Log.d("position",String.valueOf(position));
            }
        });
        setupIndicators(ImgArraylistbox.size());
    }

    //밑에 몇번째인지 동그라미로 위치를 알려줌
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(Getposition);
    }



    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }
}