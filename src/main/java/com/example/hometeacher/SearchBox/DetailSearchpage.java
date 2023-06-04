package com.example.hometeacher.SearchBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.hometeacher.Adapter.DetailSearchViewPagerAdapter;
import com.example.hometeacher.R;
import com.example.hometeacher.shared.CurrentSearchCharShared;
import com.example.hometeacher.shared.Session;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

public class DetailSearchpage extends AppCompatActivity {
    Context oContext;
    Activity oActivity;
    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    String searchtext;
    String fragtype;
    private ViewPager2 viewPager2;
    TabLayout tabLayout;

    ArrayList<Fragment> fragmentslistbox;
    int fragmentPosition = 1;

    ArrayList<String> tabElementobj;

    AutoCompleteTextView autoCompleteTextView; //자동 검색 검색어
    ArrayList<String> autosearchlist;
    CurrentSearchCharShared oCurrentSearchCharShared;
    ArrayList<ArrayList<ArrayList<String>>>  Searchlist;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_searchpage);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        try {
            division();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    public void division() throws JSONException, ParseException {

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");
        oActivity = this;
        oContext = this;



        //최근검색어 가져오기
        oCurrentSearchCharShared = new CurrentSearchCharShared(oContext);
        Searchlist = oCurrentSearchCharShared.AccountRead();
        
        Setautosearchlist();

        //뷰페이지 위치 불러오고
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager2_container);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        autoCompleteTextView  = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        //자동 검색어 어댑터 연결
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, autosearchlist));

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        searchtext = intent.getExtras().getString("searchtext");
        fragtype = intent.getExtras().getString("fragtype");
        Log.d("searchtext", "----------------------건너온 정보들------------------------");
        Log.d("searchtext", String.valueOf(searchtext));
        Log.d("fragtype", String.valueOf(fragtype));
        autoCompleteTextView.setText(searchtext); //검색어 입력





        //viewpager에 들어갈 프래그먼드를 선언한다.
        fragmentslistbox = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            //세개의 페이지로 드래그시 하나씩 보여지게 됨.
            fragmentslistbox.add(DetailSearchViewFragment.newInstance(i, searchtext));
        }
        //뷰페이지 위치에 퓨페이지어뎁터 연결
        DetailSearchViewPagerAdapter detailSearchViewPagerAdapter = new DetailSearchViewPagerAdapter(this, fragmentslistbox);
        viewPager2.setAdapter(detailSearchViewPagerAdapter);


        tabElementobj = new ArrayList<>();
        tabElementobj.add("과외찾기");
        tabElementobj.add("게시판");
        tabElementobj.add("사람");

        //tablayout과 viewpager를 연결하는 역할을 한다.
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(DetailSearchpage.this);
                textView.setText(tabElementobj.get(position));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tab.setCustomView(textView);

            }
        }).attach();




        //검색 클릭시
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :
                        //Toast.makeText(DetailSearchpage.this, "editText ACTION_SEARCH 이벤트 호출", Toast.LENGTH_SHORT).show();
                        //Log.d("MainActivity", "입력 내용 : " + autoCompleteTextView.getText().toString());

                        //검색어가 빈값이 아닐때만 저장
                        if(autoCompleteTextView.getText().toString().length() != 0){

                            //같은 내용이 있으면 삭제하고 다시 저장하기
                            Log.d("MainActivity", "입력 내용 : " + Searchlist);
                            Log.d("MainActivity", "입력 내용 : " + Searchlist.size());

                            if(Searchlist.size() != 0){
                                for(int i = 0; i<Searchlist.size(); i++){
                                    //쉐어드에 최근검색어 값이 존재한다면
                                    if(Searchlist.get(i).get(1).get(0).equals(autoCompleteTextView.getText().toString())){
                                        oCurrentSearchCharShared.Deleteone(String.valueOf(Searchlist.get(i).get(0).get(0)));
                                    }
                                }
                            }
                            //쉐어드에 최근 검색어 저장
                            oCurrentSearchCharShared.SaveViewsobject(autoCompleteTextView.getText().toString());


                        }

                        //재 검색시 페이지를 다시 가져온다.
                        finish();
                        Intent intent = new Intent(oContext.getApplicationContext(), DetailSearchpage.class);
                        intent.putExtra("searchtext",  autoCompleteTextView.getText().toString());
                        intent.putExtra("fragtype",  fragtype);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);

                        break;
                }
                return true;
            }
        });

        //뷰페이저의 위치를 지정한다.
        int targetposition = 0;
        if(fragtype.equals("nboard")){
            targetposition = 1;
        }else if(fragtype.equals("searchclass")){
            targetposition = 0;
        }
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

//                if(fragmentPosition == 0){ //과외찾기
//                    //GetUserlist(1,limitnum, 0);
//                    //DetailSearchViewFragment tf = (DetailSearchViewFragment) getSupportFragmentManager().findFragmentById(R.id.viewPager2_container);
//                   // tf.GetUserlist(1,10, 0);
//                   // ((DetailSearchViewFragment) getSupportFragmentManager().findFragmentByTag("fragmentTag")).GetUserlist(1,10, 0);
//
//
//                }else if(fragmentPosition == 1){ //게시판
//
//                }else if(fragmentPosition == 2){ //사람
//
//                }


                //여기서 탭이 선택되고 선택된 것에 맞는 리스트를 가져올 수 있게 할 것.

            }
        });
    }













    //자동 검색어 등록하는 부분
    public void Setautosearchlist(){
        autosearchlist = new ArrayList<>();

        autosearchlist.add("자전거");
        autosearchlist.add("자전거2");
        autosearchlist.add("자전거3");
        autosearchlist.add("자전거4");
        autosearchlist.add("자전거5");
        autosearchlist.add("자전거6");
        autosearchlist.add("자전거7");
        autosearchlist.add("자전거8");
        autosearchlist.add("자전거9");
    }





    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  MenuInflater menuInflater = getMenuInflater();
        // menuInflater.inflate(R.menu.profileviewmenu, menu);
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