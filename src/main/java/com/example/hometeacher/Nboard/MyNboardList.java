package com.example.hometeacher.Nboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.hometeacher.Adapter.MyNboardViewPagerAdapter;
import com.example.hometeacher.R;
import com.example.hometeacher.shared.Session;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MyNboardList extends AppCompatActivity {
    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    Context oContext;
    Activity oActivity;

    private ViewPager2 viewPager2;
    TabLayout tabLayout;

    ArrayList<Fragment> fragmentslistbox;
    int fragmentPosition = 0;
    ArrayList<String> tabElementobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynboard_list);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        division();
    }

    public void division(){

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");
        oActivity = this;
        oContext = this;


        //뷰페이지 위치 불러오고
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager2_container);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);



        //viewpager에 들어갈 프래그먼드를 선언한다.
        fragmentslistbox = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            //세개의 페이지로 드래그시 하나씩 보여지게 됨.
            fragmentslistbox.add(MyNboardViewFragment.newInstance(i));
        }
        //뷰페이지 위치에 퓨페이지어뎁터 연결
        MyNboardViewPagerAdapter MyNboardViewPagerAdapter = new MyNboardViewPagerAdapter(this, fragmentslistbox);
        viewPager2.setAdapter(MyNboardViewPagerAdapter);


        tabElementobj = new ArrayList<>();
        tabElementobj.add("게시글");
        tabElementobj.add("댓글");
        tabElementobj.add("대댓글");

        //tablayout과 viewpager를 연결하는 역할을 한다.
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(MyNboardList.this);
                textView.setText(tabElementobj.get(position));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tab.setCustomView(textView);

            }
        }).attach();


        //뷰페이저의 위치를 지정한다.
        int targetposition = 0;
        viewPager2.setCurrentItem(targetposition, false); //처음 포지션을 정해줄 수 있음.

        //스크롤시 호출되튼 콜백 기능
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if(positionOffsetPixels  == 0){
                    viewPager2.setCurrentItem(position);
                    Log.d("onPageScrolled", String.valueOf(position));
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                fragmentPosition = position;
                Log.d("onPageSelected", String.valueOf(position)); //선택한 프래그먼트 위치를 가져온다.

            }
        });
    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if(SendType.equals("1")) { //내프로필에서 접근할때만 보여짐
        //  MenuInflater menuInflater = getMenuInflater();
        // menuInflater.inflate(R.menu.profileviewmenu, menu);
        // }else{
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.profileviewmenu2, menu);
        //}
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
        }
        return super.onOptionsItemSelected(item);
    }
}
