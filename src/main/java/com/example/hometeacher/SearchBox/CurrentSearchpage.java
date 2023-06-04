package com.example.hometeacher.SearchBox;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.hometeacher.Adapter.CurrentSearchRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.CurrentsearchForm;
import com.example.hometeacher.R;
import com.example.hometeacher.RegdateComparator;
import com.example.hometeacher.shared.CurrentSearchCharShared;
import com.example.hometeacher.shared.Session;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

//최근 검색어를 보여주는 부분
public class CurrentSearchpage extends AppCompatActivity {
    Context oContext;
    Activity oActivity;
    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    RadioButton searchtype1, searchtype2, searchtype3;
    RadioButton.OnClickListener clickListener;

    AutoCompleteTextView autoCompleteTextView; //자동 검색 검색어
    RecyclerView currentSearchRecyclerView; //최근 검색어 담을 공간

    String fragtype;


    ArrayList<String> autosearchlist;

    CurrentSearchCharShared oCurrentSearchCharShared;
    ArrayList<ArrayList<ArrayList<String>>>  Searchlist;

    Button deleteall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_searchpage);

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

        autoCompleteTextView  = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        currentSearchRecyclerView = (RecyclerView) findViewById(R.id.currentSearchRecyclerView); //최근검색어 남기기

        deleteall = (Button) findViewById(R.id.deleteall);

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");
        oActivity = this;
        oContext = this;

        autosearchlist = new ArrayList<>();

        //최근검색어 가져오기
        oCurrentSearchCharShared = new CurrentSearchCharShared(oContext);
        Searchlist = oCurrentSearchCharShared.AccountRead();

        Setautosearchlist();

        //어댑터 연결
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, autosearchlist));



        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        fragtype = intent.getExtras().getString("type");
        Log.d("fragtype", "----------------------건너온 정보들------------------------");
        Log.d("fragtype", String.valueOf(fragtype));

        if(fragtype.equals("nboard")){
            autoCompleteTextView.setHint("검색어를 입력 해주세요.");
        }else if(fragtype.equals("searchclass")){
            autoCompleteTextView.setHint("원하는 과목을 입력 해주세요.");
        }


        //검색 클릭
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :
                       // Toast.makeText(CurrentSearchpage.this, "editText ACTION_SEARCH 이벤트 호출", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", "입력 내용 : " + autoCompleteTextView.getText().toString());


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

        //전체 삭제
        deleteall.setOnClickListener (new View.OnClickListener () {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {

                oCurrentSearchCharShared.Init(); //전체 삭제

                //쉐어드내용을 다시 가져와서 새로고침 해줌
                Searchlist = oCurrentSearchCharShared.AccountRead();
                Log.d("-------Searchlist------2", String.valueOf(Searchlist.size()));

                ArrayList<CurrentsearchForm> sort_Searchlist = Sortlisttoregdate(Searchlist);
                MakeCurrentSearchrecycle(sort_Searchlist);
            }
        });


        ArrayList<CurrentsearchForm> sort_Searchlist = Sortlisttoregdate(Searchlist);

        Log.d("-------Searchlist------1", String.valueOf(sort_Searchlist));
        //Log.d("-------Searchlist------1", String.valueOf(Searchlist));
        //Log.d("-------Searchlist------1", String.valueOf(Searchlist.size()));


        MakeCurrentSearchrecycle(sort_Searchlist);
    }

    public ArrayList<CurrentsearchForm> Sortlisttoregdate(ArrayList<ArrayList<ArrayList<String>>>  Searchlist){
        ArrayList<CurrentsearchForm> sort_Searchlist = new ArrayList<>();
        for (int i = 0; i < Searchlist.size(); i++){
            String key = Searchlist.get(i).get(0).get(0);
            String name = Searchlist.get(i).get(1).get(0);
            String regdate = Searchlist.get(i).get(1).get(1);
            sort_Searchlist.add(new CurrentsearchForm(key, name, regdate));
        }
        Collections.sort(sort_Searchlist, new RegdateComparator().reversed()); // 날짜로 정렬함


        return sort_Searchlist;
    }

    //자동 검색어 등록하는 부분
    public void Setautosearchlist(){
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


    //최근검색어 리스트를 만든다
    public void MakeCurrentSearchrecycle(ArrayList<CurrentsearchForm> Searchlist_){
        Log.d("최근검색어 리스트 이다.", String.valueOf(Searchlist_));

        GridLayoutManager GridlayoutManager = new GridLayoutManager(oContext, 2); //그리드 매니저 선언
        CurrentSearchRecyclerAdapter CurrentSearchAdapter = new CurrentSearchRecyclerAdapter(getApplicationContext()); //내가만든 어댑터 선언
        currentSearchRecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        CurrentSearchAdapter.setSessionval(Sessionlist); //arraylist 연결
        CurrentSearchAdapter.setRecycleList(Searchlist_); //arraylist 연결
        // WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        currentSearchRecyclerView.setAdapter(CurrentSearchAdapter); //리사이클러뷰 위치에 어답터 세팅



        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        CurrentSearchAdapter.setOnItemClickListener(new CurrentSearchRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<CurrentsearchForm> list, int type) {

                //type = 1 : 삭제버튼, type = 2 : 재검색
                if(type == 1){
                    //삭제 요청
                    oCurrentSearchCharShared.Deleteone(String.valueOf(list.get(position).getKey()));

                    //쉐어드내용을 다시 가져와서 새로고침 해줌
                    Searchlist = oCurrentSearchCharShared.AccountRead();
                    Log.d("-------Searchlist------2", String.valueOf(Searchlist.size()));

                    ArrayList<CurrentsearchForm> sort_Searchlist = Sortlisttoregdate(Searchlist);
                    MakeCurrentSearchrecycle(sort_Searchlist);
                    //MakeCurrentSearchrecycle(Searchlist);
                }else if(type == 2){ //선택한 데이터로 검색 기능

                    //삭제하고 다시 등록해서 최근검색어에서 최상위로 올리기
                    oCurrentSearchCharShared.Deleteone(String.valueOf(list.get(position).getKey()));
                    //쉐어드에 최근 검색어 저장
                    oCurrentSearchCharShared.SaveViewsobject(String.valueOf(list.get(position).getName()));

                    finish();

                    //선택한 검색어로 재검색 실행
                    Intent intent = new Intent(oContext.getApplicationContext(), DetailSearchpage.class);
                    intent.putExtra("searchtext",  String.valueOf(list.get(position).getName()));
                    intent.putExtra("fragtype",  fragtype);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });
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