package com.example.hometeacher.Nboard;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.CommentRecyclerAdapter;
import com.example.hometeacher.Adapter.ImgAdapterMulti;
import com.example.hometeacher.Adapter.ImgViewAdapter;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.ImgbigsizeActivity;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.SocketSend;
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

public class Commentnestedactivity extends AppCompatActivity {

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스
    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    Context oContext;
    Activity oActivity;

    ImageView mainprofileimg, camerabtn, commentwritebtn, likebtn;
    TextView writeinfo, timeinfo, documentinfo, commentnum, liketext;
    EditText commentedit;
    LinearLayout commentimgbox;
    RecyclerView CommentnestedimgRecyclerView, CommentNestedRecyclerView, CommentimgRecyclerView; // 대댓글 추가할대 이미지 리스트, 대댓글 리스트
    NestedScrollView nestedscrollbox;
    ProgressBar progressBar;

    String cidval;
    String nidval;

    int RESULTCODE = 1;
    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    int pagenum = 0;
    int limitnum = 10;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgFormMulti> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용

    ArrayList<JSONObject> CommentNestedlist; //대댓글 리스트
    final  public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;
    
    public static String EXTRA_ANIMAL_ITEM = "imguri";

    String Commentwriteuid; //게시글 작성자 uid

    SocketSend oSocketSend;

    String roomtype = "2"; //내과외방

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentnestedactivity);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        try {
            division();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void division() throws ParseException {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication(); //글로벌 클래스 선언


        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");
        oActivity = this;
        oContext = this;

        oSocketSend = new SocketSend(GlobalClass);


        mainprofileimg = (ImageView) findViewById(R.id.mainprofileimg);
        writeinfo = (TextView) findViewById(R.id.writeinfo);
        timeinfo = (TextView) findViewById(R.id.timeinfo);
        documentinfo = (TextView) findViewById(R.id.documentinfo);

        CommentnestedimgRecyclerView = (RecyclerView) findViewById(R.id.CommentnestedimgRecyclerView);
        CommentNestedRecyclerView = (RecyclerView) findViewById(R.id.CommentNestedRecyclerView);
        CommentimgRecyclerView = (RecyclerView) findViewById(R.id.CommentimgRecyclerView);
        commentedit = (EditText) findViewById(R.id.commentedit);
        commentimgbox = (LinearLayout) findViewById(R.id.commentimgbox);
        commentimgbox.setVisibility(View.GONE);
        commentnum = (TextView) findViewById(R.id.commentnum);
        liketext = (TextView) findViewById(R.id.liketext);
        likebtn = (ImageView) findViewById(R.id.likebtn);
        camerabtn = (ImageView) findViewById(R.id.camerabtn);
        commentwritebtn = (ImageView) findViewById(R.id.commentwritebtn);

        nestedscrollbox = (NestedScrollView) findViewById(R.id.nestedscrollbox);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        cidval = intent.getExtras().getString("cid");
        nidval = intent.getExtras().getString("nid");

        Log.d("cidval", "----------------------건너온 정보들------------------------");
        Log.d("cidval", String.valueOf(cidval));
        Log.d("nidval", String.valueOf(nidval));

        CommentNestedlist = new ArrayList<>();


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

                    GetCommentnestedlist(limitnum, offsetnum);
                    Log.d("pagenum-----", String.valueOf(pagenum));

                }
            }
        });

        //댓글 전송 버튼
        commentwritebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SetCommentnestedinfo();

            }
        });



        GetCommentInfoone(0, 0); //댓글 정보
        GetCommentnestedlist(limitnum, 0); //대댓글 리스트
    }


    //댓글 좋아요 추가 함수
    public void likeadd_comment(String cid, int liketype){
        if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
            //프로필 정보 가져오는 서버통신
            RestapiStart(); //레트로핏 빌드

            //보낼값 - 클릭한 자, 클릭받은자, 좋아요 안좋아요 타입(좋아요하면 데이터 추가, 안좋아요하면 데이터 삭제)
            HashMap<String, RequestBody> requestMap = new HashMap<>();
            RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
            RequestBody cid_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cid));
            RequestBody liketype_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(liketype));
            RequestBody Commentwriteuid_ = RequestBody.create(MediaType.parse("text/plain"), Commentwriteuid); //게시글 작성자 uid
            RequestBody myname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(3))); //댓글 작성자 uid
            RequestBody nidval_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nidval)); //게시글 idx
            String currenttime = Makecurrenttime();//현재시간 불러오기
            RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));


            requestMap.put("uid", uid);
            requestMap.put("cid", cid_);
            requestMap.put("liketype", liketype_);
            requestMap.put("Commentwriteuid", Commentwriteuid_);
            requestMap.put("myname", myname);
            requestMap.put("nid", nidval_);
            requestMap.put("currenttime", currenttime_);


            call = retrofitService.likeaddbtn_comment(
                    5, requestMap
            );
            RestapiResponse(); //응답
        }else{
            Toast.makeText(oContext, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //대댓글 리스트 가져오기
    public void GetCommentnestedlist(int limit, int offset) {

        HashMap<String, RequestBody> requestMap = new HashMap<>();
        if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
            RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
            requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
        }else{
            RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("null"));
            requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
        }

        //RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        RequestBody nid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nidval));
        RequestBody cid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cidval));

        //requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
        requestMap.put("nid", nid);
        requestMap.put("cid", cid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getcommentnestedlist(
                limit,
                offset,
                3,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //대댓글 저장
    public void SetCommentnestedinfo(){
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
                RequestBody nid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nidval));
                RequestBody cid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cidval));
                RequestBody documentedit_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(commentedit.getText()));
                String currenttime = Makecurrenttime();//현재시간 불러오기
                RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
                RequestBody Commentwriteuid_ = RequestBody.create(MediaType.parse("text/plain"), Commentwriteuid); //게시글 작성자 uid
                RequestBody myname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(3))); //댓글 작성자 uid
                RequestBody nidval_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nidval)); //게시글 idx


                requestMap.put("uid", uid);
                requestMap.put("nid", nid);
                requestMap.put("cid", cid);
                requestMap.put("document", documentedit_);
                requestMap.put("currenttime", currenttime_);
                requestMap.put("Commentwriteuid", Commentwriteuid_);
                requestMap.put("myname", myname);
                requestMap.put("nid", nidval_);


                RestapiStart(); //레트로핏 빌드

                call = retrofitService.commentnestedupload(
                        2, requestMap, imguploadlist_multipart
                );
                RestapiResponse(); //응답
            }
        }else{
            Toast.makeText(oContext, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }

    }
    
    //해당 메인댓글 정보 가져오기
    public void GetCommentInfoone(int limit, int offset) {

        HashMap<String, RequestBody> requestMap = new HashMap<>();

        if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
            RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
            requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
        }else{
            RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("null"));
            requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
        }


        RequestBody nid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nidval));
        requestMap.put("nid", nid);
        RequestBody cid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cidval));
        requestMap.put("cid", cid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getcommentlist(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
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
    public void RestapiResponse() {
        //데이터 응답
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    String url = response.raw().request().url().toString();
                    String urlget = url.split("/")[8];
                    String urlget2 = urlget.split("=")[1];

                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제

                    if (urlget2.equals("1")) { //댓글 정보 하나 가져오기
                        Log.d("onResponse ? ","댓글 info ----- : " + String.valueOf(resultlist));

                        ArrayList<JSONObject> CommentInfo = new ArrayList<>();

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                CommentInfo.add(jobj);
                            }

                            Log.d("onResponse ? ", "게시글 정보들 : all" + String.valueOf(CommentInfo)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        
                        //댓글 정보 출력
                        try {
                            SetCommentinfo(CommentInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }else if(urlget2.equals("2")){ // 대댓글 저장
                        Log.d("onResponse ? ","onResponse 대댓글 등록 : " + String.valueOf(resultlist));


                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","onResponse 게시글 좋아요, 해제  : " + String.valueOf(jobj.get("alertuid")));
                            Log.d("onResponse ? ","onResponse 게시글 좋아요, 해제  : " + String.valueOf(jobj.get("alertdocu")));

                            if(!String.valueOf(jobj.get("alertuid")).equals("null")){
                                //자기거는 알림x
                                if(!String.valueOf(jobj.get("alertuid")).equals(String.valueOf(Sessionlist.get(1).get(0)))) {
                                    //전송할 상대를 uid에 입력해준다.
                                    oSocketSend.SendSocketData("NOTISEND", "-", "-", String.valueOf(jobj.get("alertuid")), String.valueOf(jobj.get("alertdocu")), "0", nidval, "", "0","");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        commentimgbox.setVisibility(View.GONE);
                        commentedit.setText("");
                        CommentNestedlist.clear();
                        pagenum = 0;
                        GetCommentnestedlist(limitnum, 0);
                        GetCommentInfoone(0, 0);



                        //이미지 초기화 할 것.
                        imguploadlist_uri.clear();


                    }else if(urlget2.equals("3")){// 대댓글 리스트
                        Log.d("onResponse ? ","onResponse 대댓글 리스트 : " + String.valueOf(resultlist));
                        progressBar.setVisibility(View.GONE);

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "대댓글 리스트 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "대댓글 리스트 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                CommentNestedlist.add(jobj);
                            }

                            Log.d("onResponse ? ", "대댓글 리스트 : all" + String.valueOf(CommentNestedlist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        MakeCommentNestedrecycle(CommentNestedlist);


                    }else if(urlget2.equals("4")){ //대댓글 좋아요 체크
                        Log.d("onResponse ? ","onResponse 대댓글 좋아요, 해제 : " + String.valueOf(resultlist));

                        try {
                            JSONObject jobj = new JSONObject(resultlist);
                            //JSONObject jobj2 = new JSONObject((String) jobj.get("alertdata"));

                            Log.d("onResponse ? ","onResponse 게시글 좋아요, 해제  : " + String.valueOf(jobj.get("alertuid")));
                            Log.d("onResponse ? ","onResponse 게시글 좋아요, 해제  : " + String.valueOf(jobj.get("alertdocu")));

                            if(!String.valueOf(jobj.get("alertuid")).equals("null")){
                                //자기거는 알림x
                                if(!String.valueOf(jobj.get("alertuid")).equals(String.valueOf(Sessionlist.get(1).get(0)))) {
                                    //전송할 상대를 uid에 입력해준다.
                                    oSocketSend.SendSocketData("NOTISEND", "-", "-", String.valueOf(jobj.get("alertuid")), String.valueOf(jobj.get("alertdocu")), "0", nidval, "", "0","");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //댓글 리스트를 다시 불러오기
                        CommentNestedlist.clear();
                        pagenum = 0;
                        GetCommentnestedlist(limitnum, 0);

                    }else if(urlget2.equals("5")) { //댓글 본문 좋아요 체크
                        GetCommentInfoone(0, 0);
                    }else if(urlget2.equals("6")) { //대댓글 삭제
                        Log.d("onResponse ? ","onResponse 대댓글 삭제 : " + String.valueOf(resultlist));

                        //댓글 리스트를 다시 불러오기
                        CommentNestedlist.clear();
                        pagenum = 0;
                        GetCommentnestedlist(limitnum, 0);
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    //대댓글 좋아요 추가 함수
    public void likeadd_commentnested(String cnid, String Commentnestedwriteuid, int liketype){
        if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임

            //프로필 정보 가져오는 서버통신
            RestapiStart(); //레트로핏 빌드

            //보낼값 - 클릭한 자, 클릭받은자, 좋아요 안좋아요 타입(좋아요하면 데이터 추가, 안좋아요하면 데이터 삭제)
            HashMap<String, RequestBody> requestMap = new HashMap<>();
            if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
                RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
            }else{
                RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("null"));
                requestMap.put("uid", uid); //uid 보내는 이유 : 댓글 좋아요 체크하기 위함
            }




            RequestBody cnid_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cnid));
            RequestBody liketype_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(liketype));
            RequestBody Commentnestedwriteuid_ = RequestBody.create(MediaType.parse("text/plain"), Commentnestedwriteuid); //게시글 작성자 uid
            RequestBody myname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(3))); //댓글 작성자 uid
            RequestBody nidval_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nidval)); //게시글 idx
            String currenttime = Makecurrenttime();//현재시간 불러오기
            RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));

            requestMap.put("cnid", cnid_);
            requestMap.put("liketype", liketype_);
            requestMap.put("Commentnestedwriteuid", Commentnestedwriteuid_);
            requestMap.put("myname", myname);
            requestMap.put("nid", nidval_);
            requestMap.put("currenttime", currenttime_);

            call = retrofitService.likeaddbtn_commentnested(
                    4, requestMap
            );
            RestapiResponse(); //응답

        }else{
            Toast.makeText(oContext, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }




    //대댓글 리스트를 만든다.
    public void MakeCommentNestedrecycle(ArrayList<JSONObject> CommentNestedlistjsonarray){
        Log.d("게시판 리스트 이다.", String.valueOf(CommentNestedlistjsonarray));

        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언

        CommentRecyclerAdapter CommentAdapter = new CommentRecyclerAdapter(oContext.getApplicationContext(), oActivity); //내가만든 어댑터 선언
        CommentNestedRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        CommentAdapter.setneeddata(2);
        CommentAdapter.setSessionval(Sessionlist); //arraylist 연결
        CommentAdapter.setRecycleList(CommentNestedlistjsonarray); //arraylist 연결
        CommentNestedRecyclerView.setAdapter(CommentAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        CommentAdapter.setOnItemClickListener(new CommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) throws JSONException {

                //type = 1 좋아요
                if(type == 1){

                    Log.d("댓글 좋아요 체크", String.valueOf(list.get(position).get("idx"))); //cid
                    Log.d("댓글 좋아요 체크", String.valueOf(list.get(position).get("commentlikechk"))); //cid

                    int liketype;
                    if(list.get(position).get("commentlikechk").equals("true")){
                        liketype = 0;
                    }else{ // false
                        liketype = 1;
                    }

                    //리스트마다 false true로 지정해서 당사자가 좋아요 클릭여부를 체크할 수 있도록 해야한다. 그 체크 내용이 서버에 던져지도록 변경할 것!!!!!!!!!!!!!!!!!!!!!!!!!
                    likeadd_commentnested(String.valueOf(list.get(position).get("idx")), String.valueOf(list.get(position).get("uid")), liketype);

                }else if(type == 3){ // type = 3 댓글 메뉴를 연다
                    PopupMenu poup = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        poup = new PopupMenu(Commentnestedactivity.this, v); //TODO 일반 사용
                    }
                    else {
                        poup = new PopupMenu(Commentnestedactivity.this, v); //TODO 일반 사용
                    }
                    getMenuInflater().inflate(R.menu.nboardviewmenu, poup.getMenu());
                    poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                            String popup_tittle = item.getTitle().toString();
                            //TODO ==== [메뉴 선택 동작 처리] ====
                            if(popup_tittle.contains("수정하기")){
                                Intent intent = new Intent(getApplicationContext(), Commentedit.class);
                                try {
                                    intent.putExtra("cnid", String.valueOf(list.get(position).get("idx")));
                                    intent.putExtra("cid", String.valueOf(list.get(position).get("cid")));
                                    intent.putExtra("nid", String.valueOf(list.get(position).get("nid")));
                                    intent.putExtra("type", "2"); //대댓글
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
    public void setcommentdelete(String cnid){
        if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
            //프로필 정보 가져오는 서버통신
            RestapiStart(); //레트로핏 빌드

            HashMap<String, RequestBody> requestMap = new HashMap<>();
            RequestBody cnid_ = RequestBody.create(MediaType.parse("text/plain"), cnid);
            requestMap.put("cnid", cnid_);

            call = retrofitService.delete_commentnested(
                    6, requestMap
            );
            RestapiResponse(); //응답

        }else{
            Toast.makeText(oContext, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //댓글의 정보를 출력해준다. 
    public void SetCommentinfo(ArrayList<JSONObject> CommentInfo) throws JSONException {

        Commentwriteuid = String.valueOf(CommentInfo.get(0).get("uid")); //게시글 작성자 uid


        //----------정보 입력-------------//

//        //이미지크게보기
//        commentimgview.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Uri imageUri_comment = null;
//                try {
//                    imageUri_comment = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+CommentInfo.get(0).get("basicuri")+CommentInfo.get(0).get("src"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                Intent intent = new Intent(Commentnestedactivity.this, ImgbigsizeActivity.class);
//                intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(imageUri_comment));
//
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        Commentnestedactivity.this,
//                        commentimgview,
//                        Objects.requireNonNull(ViewCompat.getTransitionName(commentimgview)));
//                startActivity(intent, options.toBundle());
//
//            }
//        });
                
        //댓글 좋아요 버튼
        likebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int liketype = 0;
                try {
                    if(CommentInfo.get(0).get("commentlikechk").equals("true")){
                        liketype = 0;
                        likebtn.setImageResource(R.drawable.like_chk);
                    }else{ // false
                        liketype = 1;
                        likebtn.setImageResource(R.drawable.like2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                likeadd_comment(cidval, liketype);
            }
        });
        
        //내가 체크한 좋아요 출력
        if(CommentInfo.get(0).get("commentlikechk").equals("true")){
            likebtn.setImageResource(R.drawable.like_chk);
        }else{ //false
            likebtn.setImageResource(R.drawable.like2);
        }
        //댓글의 좋아요 갯수 출력
        liketext.setText(String.valueOf(CommentInfo.get(0).get("commentliketotalnum")));

        //댓글 갯수
        commentnum.setText(String.valueOf(CommentInfo.get(0).get("commentnestedtotalnum")));

        //프로필 이미지 변경
        Uri imageUri_profile = null;
        try {
            writeinfo.setText(String.valueOf(CommentInfo.get(0).get("name")));
            documentinfo.setText(String.valueOf(CommentInfo.get(0).get("document")));


            imageUri_profile = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+CommentInfo.get(0).get("profilebasicuri")+CommentInfo.get(0).get("profilesrc"));



        } catch (JSONException e) {
            e.printStackTrace();
        }
        Picasso.get()
                .load(imageUri_profile) // string or uri 상관없음
                .resize(200, 200)
                .centerCrop()
                .into(mainprofileimg);


        String currenttime = Makecurrenttime();//현재시간 불러오기
        String regtime = null; //등록 시간
        try {
            regtime = String.valueOf(CommentInfo.get(0).get("regdate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //타임 설정
        Date dt1 = null; //현재시간
        Date dt2 = null; //등록시간
        try {
            dt1 = timeFormat.parse(currenttime);
            dt2 = timeFormat.parse(regtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long diff = dt1.getTime() - dt2.getTime();
        long diffsec = diff / 1000; // 초 구하기
        long diffmin = diff / (60000); //분 구하기
        long diffhour = diff /  (3600000); // 시간 구하기
        long diffdays = diffsec /  (24*60*60); //일 구하기

        String timechar = "";
        //맨뒤에 추가해서 넣어줄것
        // 60초까지는 몇초전으로 진행
        // 60초가 넘어가면 1분 전으로 진행
        // 60분이 넘어가면 1시간 전으로 진행
        // 24시간이 넘어가면 1day전으로 진행
        if(diffsec < 60){
            timechar = diffsec + " 초 전";
        }else{ //60초 넘으면
            if(diffmin < 60){
                timechar = diffmin + " 분 전";
            }else{ //60분 넘으면
                if(diffhour < 24){
                    timechar = diffhour + " 시간 전";
                }else{ //12시간이 넘으면
                    timechar = diffdays + " 일 전";
                }
            }
        }
        timeinfo.setText(timechar);


//        //댓글 이미지 등록
//        try {
//            if(!CommentInfo.get(0).get("basicuri").equals("") && !CommentInfo.get(0).get("src").equals("")){
//                Uri imageUri_comment = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+CommentInfo.get(0).get("basicuri")+CommentInfo.get(0).get("src"));
//                Picasso.get()
//                        .load(imageUri_comment) // string or uri 상관없음
//                        .resize(200, 200)
//                        .centerCrop()
//                        .into(commentimgview);
//            }else{
//                commentimgview.setVisibility(View.GONE);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        //Log.d("----------게시글이미지- ------------",String.valueOf(NboardInfojsonarray.get(0).get("commentimg")));
        if(!String.valueOf(CommentInfo.get(0).get("commentimg")).equals("null")){
            JSONArray jarray_img = new JSONArray(String.valueOf(CommentInfo.get(0).get("commentimg")));
            //게시글 이미지 출력
            ArrayList<Uri> nboardinimgarr = new ArrayList<>();
            for(int i = 0; i < jarray_img.length(); i++){
                JSONObject jobimg = jarray_img.getJSONObject(i);
                //게시글의 idx와 img의 nid가 같은 이미지 정보만 모을것
                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jobimg.get("basicuri")+jobimg.get("src");
                Uri imageUri_nboard = Uri.parse(imagestring);
                nboardinimgarr.add(imageUri_nboard);
            }

            Imagerecycle_comment(nboardinimgarr);
        }


    }
    //게시글 본문 이미지를 다중 리사이클러뷰로 생성
    public void Imagerecycle_comment(ArrayList<Uri> imglist){

        LinearLayoutManager linearManager = new LinearLayoutManager(oContext, RecyclerView.HORIZONTAL, false); //가로일때
        CommentimgRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgViewAdapter ImgViewAdapter = new ImgViewAdapter(oContext, oActivity); //내가만든 어댑터 선언
        ImgViewAdapter.setRecycleList(imglist); //arraylist 연결
        CommentimgRecyclerView.setAdapter(ImgViewAdapter); //리사이클러뷰 위치에 어답터 세팅


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
                                .start(Commentnestedactivity.this);
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
                                Toast.makeText(Commentnestedactivity.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
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

                                Imagerecycle_commentnested(imguploadlist_uri); //리사이클러뷰에 출력
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

                                Imagerecycle_commentnested(imguploadlist_uri); //리사이클러뷰에 출력
                            }


//                            ClipData clipData = data.getClipData();
//                            Log.d("uridata", String.valueOf(clipData));
//                            Log.i("clipdata", String.valueOf(clipData.getItemCount()));
//
//                            if (clipData.getItemCount() > 10){
//                                Toast.makeText(Commentnestedactivity.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
//                                return;
//                            }   // 멀티 선택에서 하나만 선택했을 경우
//                            else if (clipData.getItemCount() == 1) {
//                                String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
//                                Log.i("2. clipdata choice", String.valueOf(clipData.getItemAt(0).getUri()));
//                                Log.i("2. single choice", clipData.getItemAt(0).getUri().getPath());
//                                //imageList.add(dataStr);
//
//                                //댓글 이미지 등록은 하나만 등록 가능하다.
//                                if(imguploadlist_uri.size() == 1){
//                                    imguploadlist_uri.clear();
//                                }
//
//
//                                //업로드 하기 위해 캐쉬에 저장후 uri 가져옴
//                                String copyUri = getFilePathFromURI(oContext, clipData.getItemAt(0).getUri());
//                                Log.i("2. ----------copyUri-----------", copyUri);
//
//                                //uri 전용 arraylist에 저장
//                                ImgFormMulti imgForm = new ImgFormMulti(Uri.parse(dataStr), Uri.parse(copyUri), "-", true);
//                                if (imguploadlist_uri.size() == 0) {
//                                    imguploadlist_uri.add(imgForm);
//                                } else {
//                                    imguploadlist_uri.add(0, imgForm);
//                                }
//
//                                Imagerecycle_commentnested(imguploadlist_uri); //리사이클러뷰에 출력
//
//                            }
                        }
                    }
                    break;
                case REQUESTCODE:
                    //대댓글 수정후
                    if (resultCode == RESULTCODE1) {
                        String completeval = data.getStringExtra("completeval");
                        Log.d("뒤로가기 리턴값!!!! ", completeval);

                        if(completeval.equals("1")){

                            CommentNestedlist.clear();
                            pagenum = 0;
                            GetCommentnestedlist(limitnum, 0);
                            //nboardgetdata(0, 0);
                        }

                    }

                    break;
            }
        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
    }


    //대댓글 이미지 등록
    public void Imagerecycle_commentnested(ArrayList<ImgFormMulti> imglist){

        commentimgbox.setVisibility(View.VISIBLE);

        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false); //가로일때
        CommentnestedimgRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgAdapterMulti ImgAdapterMulti = new ImgAdapterMulti(this, oActivity); //내가만든 어댑터 선언

        ImgAdapterMulti.settype(3); //댓글 등록 이미지
        ImgAdapterMulti.setRecycleList(imglist); //arraylist 연결
        CommentnestedimgRecyclerView.setAdapter(ImgAdapterMulti); //리사이클러뷰 위치에 어답터 세팅


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
                    Imagerecycle_commentnested(imguploadlist_uri);

                    commentimgbox.setVisibility(View.GONE);

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


                //전 activity로 값을 리턴
                Intent resultIntent = new Intent();
                resultIntent.putExtra("completeval", "1"); //뷰에서 뒤로가기
                oActivity.setResult(RESULTCODE, resultIntent);
                oActivity.finish();

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        //Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        //Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}