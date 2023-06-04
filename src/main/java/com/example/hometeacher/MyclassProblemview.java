package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.CommentRecyclerAdapter;
import com.example.hometeacher.Adapter.ImgAdapterMulti;
import com.example.hometeacher.Adapter.ImgViewAdapter;
import com.example.hometeacher.Adapter.ProblemCommentRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.Nboard.Commentedit;
import com.example.hometeacher.Nboard.Commentnestedactivity;
import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.shared.Session;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyclassProblemview extends AppCompatActivity {

    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    String problemidx;
    String roomidx;

    ImageView closetab, mainprofileimg, commentwritebtn, camerabtn;
    EditText documentedit, commentedit;
    TextView writeinfo, timeinfo, documentinfo, commentnum; //commentpeopleinfo
    RecyclerView problemimgRecyclerView, CommentRecyclerView, CommentimgRecyclerView;
    NestedScrollView nestedscrollbox;
    ProgressBar progressBar;
    LinearLayout commentimgbox;
    Button menubtn;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    int pagenum = 0;
    int limitnum = 10;

    ArrayList<JSONObject> Probleminfo;

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgFormMulti> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String Problemwriteuid; //게시글 작성자 uid
    ArrayList<JSONObject> Commentlist; //댓글 리스트


    final  public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_problemview);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기


        division();
    }

    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;

        documentinfo = (TextView) this.findViewById(R.id.documentinfo);
        commentnum = (TextView) this.findViewById(R.id.commentnum);
        timeinfo = (TextView) this.findViewById(R.id.timeinfo);
        writeinfo = (TextView) this.findViewById(R.id.writeinfo);
        documentedit = (EditText) this.findViewById(R.id.documentedit);
        mainprofileimg = (ImageView) findViewById(R.id.mainprofileimg);
        problemimgRecyclerView = (RecyclerView) findViewById(R.id.problemimgRecyclerView);
        CommentRecyclerView = (RecyclerView) findViewById(R.id.CommentRecyclerView);
        commentimgbox = (LinearLayout) findViewById(R.id.commentimgbox);
        commentimgbox.setVisibility(View.GONE);
        commentwritebtn = (ImageView) findViewById(R.id.commentwritebtn);

        commentedit = (EditText) findViewById(R.id.commentedit);

        nestedscrollbox = (NestedScrollView) findViewById(R.id.nestedscrollbox);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        CommentimgRecyclerView = (RecyclerView) findViewById(R.id.CommentimgRecyclerView);



        //데이터 받음
        Intent intent = getIntent();
        problemidx = intent.getExtras().getString("pid"); // 문제 idx
        roomidx = intent.getExtras().getString("rid"); // 방 idx
        //Log.d("-------MainCategoreyType-------", String.valueOf(MainCategoreyType));
        Log.d("-------Selecttype-------", String.valueOf(roomidx));


        //게시글 닫기
        closetab = (ImageView) this.findViewById(R.id.closetab);
        closetab.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                finish();
            }
        });

        camerabtn = (ImageView) findViewById(R.id.camerabtn);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(oContext);

                builder.setTitle("댓글 사진 업로드");

                builder.setItems(R.array.imgckarray, new DialogInterface.OnClickListener(){
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){ //사진첩

//                            Intent intent = new Intent(Intent.ACTION_PICK);
//                            intent.setType("image/*");
//                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                            // intent.setAction(Intent.ACTION_PICK);
//                            startActivityForResult(Intent.createChooser(intent,"Select Picture"), OPEN_GALLERY);

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            // intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent,"Select Picture"), OPEN_GALLERY);

                        }else{ //카메라
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename));
                            oActivity.startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);

                        }
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });
        //스크롤뷰의 밑바당에 부딛힐때마다 프로그레스바가 돌고 데이터를 새로 가져온다. 열개씩 가져올 것.
        nestedscrollbox.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {

                    pagenum++;
                    progressBar.setVisibility(View.VISIBLE);

                    int offsetnum = limitnum*pagenum;

                    //Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));

                    GetCommentlist(limitnum, offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));

                }
            }
        });
        //댓글 전송 버튼
        commentwritebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                SetCommentinfo();

            }
        });

        //메뉴 버튼 클릭시 수정, 삭제
        menubtn = (Button) findViewById(R.id.menubtn);
        //작성자만 보이게 할 것. 임시방편으로 선생님만 보이게 햇음
        if(String.valueOf(Sessionlist.get(1).get(0)).equals("2")){
            menubtn.setVisibility(View.GONE);
        }else{
            menubtn.setVisibility(View.VISIBLE);
        }
        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //과제의 수정 삭제를 담당한다.
                PopupMenu poup = poup = new PopupMenu(MyclassProblemview.this, view); //TODO 일반 사용
                getMenuInflater().inflate(R.menu.nboardviewmenu, poup.getMenu());
                poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        String popup_tittle = item.getTitle().toString();
                        //TODO ==== [메뉴 선택 동작 처리] ====
                        if(popup_tittle.contains("수정하기")){
                            Intent intent = new Intent(getApplicationContext(), MyclassProblemwrite.class);
                            intent.putExtra("pid", problemidx);
                            intent.putExtra("rid", roomidx);
                            intent.putExtra("type", 2); //수정
                            startActivityForResult(intent, REQUESTCODE);
                        }
                        else if(popup_tittle.contains("삭제하기")){

                            //다이어그램 띄우기
                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                            builder.setTitle("댓글 삭제").setMessage("댓글을 삭제합니다");

                            //삭제 실시
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    //과제 삭제로 진행이 되어야한다.
                                    setproblemdelete();

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Toast.makeText(view.getContext(), "Cancel Click"+id, Toast.LENGTH_SHORT).show();
                                }
                            });

                            AlertDialog alertDialog = builder.create();

                            alertDialog.show();

                        }
                        return false;
                    }
                });
                poup.show(); // 메뉴를 띄우기
            }
        });

        Probleminfo = new ArrayList<>();
        Commentlist = new ArrayList<>();
    }

    //댓글 업로드
    public void SetCommentinfo(){

        if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임

            //카테고리 선택, 제목, 글 작성시 업로드 가능하도록
            if(!String.valueOf(commentedit.getText()).equals("")){



                //이미지 파일을 보낼 수 있는 형태로 변환해주는 부분
                // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
                for (int i = 0; i < imguploadlist_uri.size(); i++) {
                    //String path = imguploadlist_uri.get(i).getPath();

                    if(imguploadlist_uri.get(i).isSelected()){ //true인것만 체크할 것
                        String path = imguploadlist_uri.get(i).getcopyUri().getPath();
                        //String path = String.valueOf(imguploadlist_uri.get(i).getUri());
                        File file = new File(path);

                        Log.d("img upload file link", imguploadlist_uri.get(i).toString());
                        // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
                        // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), imguploadlist_uri.get(i).toString());
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                        // 사진 파일 이름
                        String fileName = "photo" + i + ".jpg";
                        // RequestBody로 Multipart.Part 객체 생성
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file"+i, fileName, requestFile);


                        // 추가
                        imguploadlist_multipart.add(filePart);
                    }
                }

                //프로필 정보 한번에 넘기기
                HashMap<String, RequestBody> requestMap = new HashMap<>();
                RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                RequestBody pid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(problemidx));
                RequestBody documentedit_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(commentedit.getText()));
                String currenttime = Makecurrenttime();//현재시간 불러오기
                RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
                RequestBody Problemwriteuid_ = RequestBody.create(MediaType.parse("text/plain"), Problemwriteuid); //게시글 작성자 uid
                RequestBody myname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(3))); //댓글 작성자 uid


                requestMap.put("uid", uid);
                requestMap.put("pid", pid);
                requestMap.put("document", documentedit_);
                requestMap.put("currenttime", currenttime_);
                requestMap.put("Problemwriteuid", Problemwriteuid_);
                requestMap.put("myname", myname);

                RestapiStart(); //레트로핏 빌드

                call = retrofitService.problemcommentadd(
                        3, requestMap, imguploadlist_multipart
                );
                RestapiResponse(); //응답
            }

        }else{
            Toast.makeText(oContext, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //댓글 리스트 가져오기
    public void GetCommentlist(int limit, int offset) {

        HashMap<String, RequestBody> requestMap = new HashMap<>();
       // if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
//        }else{
//            RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("null"));
//            requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
//        }

        RequestBody pid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(problemidx));
        requestMap.put("pid", pid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getproblemcommentlist(
                limit,
                offset,
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //과제 정보
    public void GetProbleminfo() {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody pid = RequestBody.create(MediaType.parse("text/plain"), problemidx);
        requestMap.put("pid", pid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getproblemlist(
                0,
                0,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //레트로핏 라이브러리를 사용해 보낼 양식 만들어 놓음
    public void RestapiStart() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송

    }
    //레트로핏 데이터 응답 부분
    public void RestapiResponse() {
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

                    if (urlget2.equals("1")) { //과제 정보 가져오기
                        Log.d("onResponse ? ", "과제 정보 가져오기 : " + resultlist);


                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "과제 정보 가져오기 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "과제 정보 가져오기 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Probleminfo.add(jobj);
                            }

                            Log.d("onResponse ? ", "과제 정보 가져오기 : all" + String.valueOf(Probleminfo)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        try {
                            SetProbleminfo(Probleminfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else if(urlget2.equals("2")){//과제 댓글 가져오기

                        Log.d("onResponse ? ", "과제 댓글 가져오기 : " + resultlist);

                        progressBar.setVisibility(View.GONE);

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "댓글 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));



                                Commentlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "댓글 정보들 : all" + String.valueOf(Commentlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeCommentrecycle(Commentlist);

                    }else if(urlget2.equals("3")){//과제 댓글 저장하기

                        Log.d("onResponse ? ", "과제 댓글 저장하기 : " + resultlist);


                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","onResponse 게시글 좋아요, 해제  : " + String.valueOf(jobj.get("alertuid")));
                            Log.d("onResponse ? ","onResponse 게시글 좋아요, 해제  : " + String.valueOf(jobj.get("alertdocu")));

//                            if(!String.valueOf(jobj.get("alertuid")).equals("null")){
//                                //자기거는 알림x
//                                if(!String.valueOf(jobj.get("alertuid")).equals(String.valueOf(Sessionlist.get(1).get(0)))) {
//                                    //전송할 상대를 uid에 입력해준다.
//                                    oSocketSend.SendSocketData("NOTISEND", "-", "-", String.valueOf(jobj.get("alertuid")), String.valueOf(jobj.get("alertdocu")), "0", FixNid, "", "0","");
//                                }
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        commentimgbox.setVisibility(View.GONE);
                        commentedit.setText("");
                        Commentlist.clear();
                        pagenum = 0;
                        GetCommentlist(limitnum, 0);
//                        try {
//                            nboardgetdata(0, 0);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }

                        imguploadlist_uri.clear(); //댓글이미지 작성 리스트 초기화
                    }else if(urlget2.equals("4")){ //댓글 삭제
                        Log.d("onResponse ? ", "댓글 삭제 : " + resultlist);

                        //댓글 리스트를 다시 불러오기
                        Commentlist.clear();
                        pagenum = 0;
                        GetCommentlist(limitnum, 0);
                    }else if(urlget2.equals("5")){ //문제 삭제
                        Log.d("onResponse ? ", "문제 삭제 : " + resultlist);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //댓글 리스트를 만든다.
    public void MakeCommentrecycle(ArrayList<JSONObject> Commentlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(Commentlistjsonarray));

        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언

        ProblemCommentRecyclerAdapter oProblemCommentRecyclerAdapter = new ProblemCommentRecyclerAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        CommentRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        oProblemCommentRecyclerAdapter.setneeddata(1);
        oProblemCommentRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oProblemCommentRecyclerAdapter.setRecycleList(Commentlistjsonarray); //arraylist 연결
        CommentRecyclerView.setAdapter(oProblemCommentRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅



        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oProblemCommentRecyclerAdapter.setOnItemClickListener(new ProblemCommentRecyclerAdapter.OnItemClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) throws JSONException {

                //type = 1 //좋아요
                if(type == 1){

//                    Log.d("댓글 좋아요 체크", String.valueOf(list.get(position).get("idx"))); //cid
//                    Log.d("댓글 좋아요 체크", String.valueOf(list.get(position).get("commentlikechk"))); //cid
//
//                    int liketype;
//                    if(list.get(position).get("commentlikechk").equals("true")){
//                        liketype = 0;
//                    }else{ // false
//                        liketype = 1;
//                    }
//
//                    //리스트마다 false true로 지정해서 당사자가 좋아요 클릭여부를 체크할 수 있도록 해야한다. 그 체크 내용이 서버에 던져지도록 변경할 것!!!!!!!!!!!!!!!!!!!!!!!!!
//                    likeadd_comment(String.valueOf(list.get(position).get("idx")), String.valueOf(list.get(position).get("uid")), liketype);

                }else if(type == 2){//type = 2 답글쓰기
//                    Intent intent = new Intent(getApplicationContext(), Commentnestedactivity.class);
//                    try {
//                        //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
//                        intent.putExtra("nid", String.valueOf(list.get(position).get("nid")));
//                        intent.putExtra("cid", String.valueOf(list.get(position).get("idx")));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    startActivityForResult(intent, REQUESTCODE);
                }else if(type == 3){ // type = 3 댓글 메뉴를 연다

                    PopupMenu poup = poup = new PopupMenu(MyclassProblemview.this, v); //TODO 일반 사용
                    getMenuInflater().inflate(R.menu.nboardviewmenu, poup.getMenu());
                    poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                            String popup_tittle = item.getTitle().toString();
                            //TODO ==== [메뉴 선택 동작 처리] ====
                            if(popup_tittle.contains("수정하기")){
                                Intent intent = new Intent(getApplicationContext(), MyclassProblemCommentedit.class);
                                try {
                                    intent.putExtra("cid", String.valueOf(list.get(position).get("idx")));
                                    //intent.putExtra("nid", String.valueOf(list.get(position).get("nid")));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }
                            else if(popup_tittle.contains("삭제하기")){

                                //다이어그램 띄우기
                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                                builder.setTitle("댓글 삭제").setMessage("댓글을 삭제합니다");

                                //삭제 실시
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            setcommentdelete(String.valueOf(list.get(position).get("idx")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Toast.makeText(view.getContext(), "Cancel Click"+id, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();

                                alertDialog.show();

                            }
                            return false;
                        }
                    });
                    poup.show(); // 메뉴를 띄우기
                }
            }
        });
    }

    //댓글 삭제
    public void setcommentdelete(String cid){


            //프로필 정보 가져오는 서버통신
            RestapiStart(); //레트로핏 빌드

            HashMap<String, RequestBody> requestMap = new HashMap<>();
            RequestBody cid_ = RequestBody.create(MediaType.parse("text/plain"), cid);
            requestMap.put("cid", cid_);

            call = retrofitService.delete_problemcomment(
                    4, requestMap
            );
            RestapiResponse(); //응답
    }


    //문제 삭제
    public void setproblemdelete(){


        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody pid_ = RequestBody.create(MediaType.parse("text/plain"), problemidx);
        requestMap.put("pid", pid_);

        call = retrofitService.problemdelete(
                5, requestMap
        );
        RestapiResponse(); //응답
    }




    //서버에서 가져온데이터 입력
    @SuppressLint("SetTextI18n")
    public void SetProbleminfo(ArrayList<JSONObject> ProblemInfojsonarray) throws JSONException {
        documentinfo.setText(String.valueOf(ProblemInfojsonarray.get(0).get("problemdocu")));
        writeinfo.setText(String.valueOf(ProblemInfojsonarray.get(0).get("name")));

        //날짜 시간을 변형한다.
        String dateStr = String.valueOf(ProblemInfojsonarray.get(0).get("regdate")); //2022-08-09 05:53:17
        String[] dateStr_split = dateStr.split(" ");
        String[] dateStr_date = dateStr_split[0].split("-");
        String[] dateStr_time = dateStr_split[1].split(":");
        System.out.println("second : " + String.valueOf(dateStr_date[0]));
        System.out.println("second : " + String.valueOf(dateStr_time[0]));

        String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
        String ampm = "";
        if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
            ampm = "오후";
        }else{ //오전
            ampm = "오전";
        }
        String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
        timeinfo.setText(datemake+timemake);

        //commentpeopleinfo.setText("·과제 수행"); //과제 수행 인원을 체크할 것 = 참여 인원중 댓글 단 인원을 체크하면 됨. 클릭시

        Problemwriteuid = String.valueOf(ProblemInfojsonarray.get(0).get("uid")); //게시글 작성자 uid
        commentnum.setText(" · 댓글 "+ String.valueOf(ProblemInfojsonarray.get(0).get("commenttotalnum")));




        //카테고리 출력
        //maincatenum =  String.valueOf(ProblemInfojsonarray.get(0).get("maincategorey"));
//        String maincatechar = maincategoreychar(String.valueOf(ProblemInfojsonarray.get(0).get("maincategorey")));
//        String subcatechar = subcategoreychar(maincatenum, String.valueOf(ProblemInfojsonarray.get(0).get("subcategorey")));
//        subcateinfo.setText(maincatechar+" > "+subcatechar);
//
//        titleinfo.setText(String.valueOf(ProblemInfojsonarray.get(0).get("title")));

       // viewsinfo.setText("· 조회 "+ ProblemInfojsonarray.get(0).get("views"));



       // liketext.setText(String.valueOf(ProblemInfojsonarray.get(0).get("liketotalnum"))); //좋아요 수 변경
        //commentnum.setText(String.valueOf(ProblemInfojsonarray.get(0).get("commenttotalnum"))); //댓글 갯수 변경

        //프로필 이미지 변경
        Uri imageUri_profile = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+ProblemInfojsonarray.get(0).get("basicuri")+ProblemInfojsonarray.get(0).get("src"));
        Picasso.get()
                .load(imageUri_profile) // string or uri 상관없음
                .resize(200, 200)
                .centerCrop()
                .into(mainprofileimg);



        //Log.d("----------게시글이미지- ------------",String.valueOf(NboardInfojsonarray.get(0).get("nboardimg")));
        if(!String.valueOf(ProblemInfojsonarray.get(0).get("problemimg")).equals("null")){
            JSONArray jarray_img = new JSONArray(String.valueOf(ProblemInfojsonarray.get(0).get("problemimg")));
            //게시글 이미지 출력
            ArrayList<Uri> probleminimgarr = new ArrayList<>();
            for(int i = 0; i < jarray_img.length(); i++){
                JSONObject jobimg = jarray_img.getJSONObject(i);
                //게시글의 idx와 img의 nid가 같은 이미지 정보만 모을것
                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jobimg.get("basicuri")+jobimg.get("src");
                Uri imageUri_nboard = Uri.parse(imagestring);
                probleminimgarr.add(imageUri_nboard);
            }

            Imagerecycle_problem(probleminimgarr);
        }
    }


    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        //Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        //Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {
                        // Bundle로 데이터를 입력
                        Bundle extras = data.getExtras(); //카메라 촬영후 가져오는 데이터

                        // Bitmap으로 컨버전
                        Bitmap imageBitmap = (Bitmap) extras.get("data"); //bitmab으로 변경
                        Uri uridata = getImageUri(this, imageBitmap); //bitmap을 uri로 변경

                        //이미지 선택후 크롭화면 나오도록 설정
                        CropImage.activity(uridata).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                                .start(MyclassProblemview.this);
                    }
                    break;

                case OPEN_GALLERY:
                    if (resultCode == RESULT_OK) {

                        Log.d("uridata", String.valueOf(data.getExtras()));
                        Log.d("uridata", String.valueOf(data.getData()));



                        // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                        if (data.getClipData() == null) { //일단 필요 x
                            //Log.i("1. single choice", String.valueOf(data.getData()));
                            //imageList.add(String.valueOf(data.getData()));

                            //uri 전용 arraylist에 저장
//                            ImgForm imgForm = new ImgForm(data.getData(),"-", true);
//                            if(imguploadlist_uri.size()>0){
//                                imguploadlist_uri.add(1, imgForm);
//                            }else{
//                                imguploadlist_uri.add(imgForm);
//                            }


                            // Imagerecycle(imguploadlist_uri); //리사이클러뷰에 출력


                        } else {

                            ClipData clipData = data.getClipData();
                            Log.d("uridata", String.valueOf(clipData));
                            Log.i("clipdata", String.valueOf(clipData.getItemCount()));

                            if (clipData.getItemCount() > 10){
                                Toast.makeText(MyclassProblemview.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }   // 멀티 선택에서 하나만 선택했을 경우
                            else if (clipData.getItemCount() == 1) {
                                String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                                Log.i("2. clipdata choice", String.valueOf(clipData.getItemAt(0).getUri()));
                                Log.i("2. single choice", clipData.getItemAt(0).getUri().getPath());
                                //imageList.add(dataStr);


                                //업로드 하기 위해 캐쉬에 저장후 uri 가져옴
                                String copyUri = getFilePathFromURI(oContext, clipData.getItemAt(0).getUri());
                                Log.i("2. ----------copyUri-----------", copyUri);

                                //uri 전용 arraylist에 저장
                                ImgFormMulti imgForm = new ImgFormMulti(Uri.parse(dataStr), Uri.parse(copyUri),"-", true);
                                if(imguploadlist_uri.size() == 0){
                                    imguploadlist_uri.add(imgForm);
                                }else{
                                    imguploadlist_uri.add(0, imgForm);
                                }

                                Imagerecycle_comment(imguploadlist_uri); //리사이클러뷰에 출력
                            } else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 10) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Log.i("3. single choice", String.valueOf(clipData.getItemAt(i).getUri()));

                                    String dataStr = String.valueOf(clipData.getItemAt(i).getUri());
                                    String copyUri = getFilePathFromURI(oContext, clipData.getItemAt(i).getUri());

                                    //uri 전용 arraylist에 저장
                                    ImgFormMulti imgForm = new ImgFormMulti(Uri.parse(dataStr),Uri.parse(copyUri),"-", true);
                                    if(imguploadlist_uri.size() == 0){
                                        imguploadlist_uri.add(imgForm);
                                    }else if(imguploadlist_uri.size() < 10){
                                        imguploadlist_uri.add(0, imgForm);
                                    }
                                }

                                Imagerecycle_comment(imguploadlist_uri); //리사이클러뷰에 출력
                            }
                        }
                    }
                    break;
//                case REQUESTCODE:
//                    //대댓글 클릭후 리턴받는 부분, 댓글 수정후
//                    if (resultCode == RESULTCODE1) {
//                        String completeval = data.getStringExtra("completeval");
//                        Log.d("뒤로가기 리턴값!!!! ", completeval);
//                        if(completeval.equals("1")){
//
//                            Commentlist.clear();
//                            pagenum = 0;
//                            GetCommentlist(limitnum, 0);
//                            nboardgetdata(0, 0);
//                        }
//                    }
//                    break;
            }
        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
    }


    //댓글 이미지 등록
    public void Imagerecycle_comment(ArrayList<ImgFormMulti> imglist){

        commentimgbox.setVisibility(View.VISIBLE);

        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false); //가로일때
        CommentimgRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgAdapterMulti ImgAdapterMulti = new ImgAdapterMulti(this, oActivity); //내가만든 어댑터 선언

        ImgAdapterMulti.settype(3); //댓글 등록 이미지
        ImgAdapterMulti.setRecycleList(imglist); //arraylist 연결
        CommentimgRecyclerView.setAdapter(ImgAdapterMulti); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        ImgAdapterMulti.setOnItemClickListener(new ImgAdapterMulti.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(View v, int picturepos, int type, String imgidx) {

                Log.d("type", String.valueOf(type)); //업로드인지 1, 삭제인지 2 알려줌
                Log.d("position", String.valueOf(picturepos)); //이미지 위치를 알려줌
                // Log.d("clickpos", String.valueOf(clickpos)); //alert dialog 버튼 순서를 알려줌

                if(type == 2){ //2 삭제
                    imguploadlist_uri.remove(picturepos);
                    Imagerecycle_comment(imguploadlist_uri);

                    if(imguploadlist_uri.size() == 0){
                        commentimgbox.setVisibility(View.GONE);
                    }

                    //SelectImgDeletelist.add(imgidx); //이미지 삭제 리스트 생성

                    //사진 갯수 변경
                    //picturenum.setText(imguploadlist_uri.size()+"/10");
                }
            }
        }) ;

    }

    //bitmap으로 uri로 변경하는 함수
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //이미지 파일 복사후 반환하는 작업. (업로드시 uri파일이 file로 변환이 안되는 문제)
    public static String getFilePathFromURI(Context context, Uri contentUri) {
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(context.getFilesDir()+ File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }
    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    //게시글 본문 이미지를 다중 리사이클러뷰로 생성
    public void Imagerecycle_problem(ArrayList<Uri> imglist){

        LinearLayoutManager linearManager = new LinearLayoutManager(oContext, RecyclerView.HORIZONTAL, false); //가로일때
        problemimgRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgViewAdapter ImgViewAdapter = new ImgViewAdapter(oContext, oActivity); //내가만든 어댑터 선언
        ImgViewAdapter.setRecycleList(imglist); //arraylist 연결
        problemimgRecyclerView.setAdapter(ImgViewAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        ImgViewAdapter.setOnItemClickListener(new ImgViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int picturepos, Uri imguri, ImageView sharedImageView) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.

                Intent intent = new Intent(oContext, ImgbigsizeActivity.class);
                intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(imguri));
                // intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        oActivity,
                        sharedImageView,
                        Objects.requireNonNull(ViewCompat.getTransitionName(sharedImageView)));

                oActivity.startActivity(intent, options.toBundle());
            }
        }) ;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Probleminfo.clear();
        GetProbleminfo();

        Commentlist.clear();
        GetCommentlist(limitnum, 0);
    }
}