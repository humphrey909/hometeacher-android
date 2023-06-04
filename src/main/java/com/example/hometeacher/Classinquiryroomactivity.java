package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.ImgAdapterMulti;
import com.example.hometeacher.Adapter.RequestChatRecyclerAdapter;
import com.example.hometeacher.Adapter.RequestClassRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.CategoreyForm;
import com.example.hometeacher.ArraylistForm.ChattingForm;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.ArraylistForm.SubjectForm;
import com.example.hometeacher.Profile.Profileview;
import com.example.hometeacher.shared.GlobalClass;
import com.example.hometeacher.shared.Session;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Classinquiryroomactivity extends AppCompatActivity {

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    Session oSession; //자동로그인을 위한 db
    Context oContext;
    public static Activity oActivity_classinquiryroom;
    ArrayList<ArrayList<String>> Sessionlist;

    NestedScrollView nestedscrollbox;
    ProgressBar progressBar;
    RecyclerView RequestChatRecyclerView;

    int pagenum = 1; //처음 페이징 값을 지정
    int limitnum = 20;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    ImageButton backbtn;
    ImageView profileimg;
    TextView profilename;
    TextView profileinfo;
    EditText chattingedit;
    ImageView chattingwritebtn;
    ImageView camerabtn;
    Button requestbtn;
    LinearLayout requestlinear;

    String Senddata = "";
   // PrintWriter outwriter;

    String Sendroommaketype; //방 입장 타입
    String Sendroomidx; //방 고유번호
    String SendTchatcount; //총 채팅갯수

    ArrayList<JSONObject> ChatRoominfo; //과외문의 정보

    ArrayList<ChattingForm> Chatmessagelist = new ArrayList<>(); //채팅 리스트

    String MyuidLocate; //내 uid가 pid1인지 pid2인지 위치를 알려줌

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgFormMulti> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용
    //ArrayList<String> SelectImgDeletelist; //db 이미지 항목중에 삭제된 idx리스트 - db에서 삭제하기 위함

    String otheruid = ""; //상대 uid

    SocketSend oSocketSend;

    String roomtype = "1"; //과외문의방

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classinquiryroomactivity);


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기

        division();
    }

    public void division() {


        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication(); //글로벌 클래스 선언
        oSocketSend = new SocketSend(GlobalClass);

        oActivity_classinquiryroom = this;
        oContext = this;
        oSession = new Session(oContext);

        requestlinear = (LinearLayout) findViewById(R.id.requestlinear);
        nestedscrollbox = (NestedScrollView) findViewById(R.id.scroll_view);
        RequestChatRecyclerView = (RecyclerView) findViewById(R.id.RequestChatRecyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        backbtn = (ImageButton) findViewById(R.id.backbtn);
        profileimg = (ImageView) findViewById(R.id.profileimg);
        profilename = (TextView) findViewById(R.id.profilename);
        profileinfo = (TextView) findViewById(R.id.profileinfo);
        chattingedit = (EditText) findViewById(R.id.chattingedit);
        chattingwritebtn = (ImageView) findViewById(R.id.chattingwritebtn);

        //자동 로그인 하기
        Sessionlist = oSession.Getoneinfo("0");
        //String.valueOf(Sessionlist.get(1).get(3)) // 이름

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        Sendroommaketype = intent.getExtras().getString("roommaketype"); //1. 방 처음 만들때 2. 만들어진 방에 들어올때
        Sendroomidx = intent.getExtras().getString("roomidx"); //방 고유번호

        Log.d("-------------Sendroommaketype------------",Sendroommaketype);


        if(Sendroommaketype.equals("2")){
            SendTchatcount = intent.getExtras().getString("Tchatcount"); //총 채팅 갯수 : 이 변수로 채팅 페이징 처리를 할 것
            Log.d("-------------SendTchatcount------------",SendTchatcount);
        }


        Log.d("-------------roommaketype------------",Sendroommaketype);
        Log.d("-------------roomidx------------",Sendroomidx);



        //방이름을 만든사람 닉네임 + 만든 숫자 humph_1
        //방 만들때 타입을 구분
//        if(Sendroommaketype.equals("1")){ //소켓에 연결을 한다. 소켓에 방을 만든다.
//
//        }else if(Sendroommaketype.equals("2")){ //db에서 해당방의 인원이 들어왔는지 체크하고 인원이 부족하면 소켓을 연결하고 db 수정한다.
//
//            //만들어진 방에 들어와도 소켓연결된 자인지 안된자인지 구분이 필요하다.+
//
//
//        }


        //뒤로가기 버튼
        backbtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                finish();
            }
        });



        ChatRoominfo = new ArrayList<>();
        Getroominfo(0,0); //방정보와 유저 정보를 가져온다.


        //스크롤뷰의 밑바당에 부딛힐때마다 프로그레스바가 돌고 데이터를 새로 가져온다. 열개씩 가져올 것.
        RequestChatRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(Sendroommaketype.equals("2")) {
                    if (!RequestChatRecyclerView.canScrollVertically(-1)) { //맨위로 올렸을때 작동
                        if (Integer.parseInt(SendTchatcount) > limitnum) { // 채팅 갯수가 제한 수 보다 클때만 작동

                            pagenum++;


                            int offsetnum = limitnum * pagenum; // 10 20 ... 90 100
                            int offset = Integer.parseInt(SendTchatcount) - offsetnum;  //시작 위치

                            //시작 위치가 0보다 클때만 새 페이지를 가져온다.
                            if (offset >= 0) {
                                progressBar.setVisibility(View.VISIBLE);
                                Getrequestclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
                                Log.d("pagenum-----", String.valueOf(pagenum));
                            } else { //시작 위치가 - 값 일때
                                //시작위치가 제한 값보다 클때 - 보여줄 데이터가 남아있다는 뜻
                                if (Integer.parseInt(SendTchatcount) - offsetnum > -limitnum) { // 115 - 100 = 15 115 - 110 = 5 115 - 120 = -5
                                    progressBar.setVisibility(View.VISIBLE);
                                    Log.d("------pagenum-----", String.valueOf(Integer.parseInt(SendTchatcount) - offsetnum));
                                    Log.d("------pagenum-----", String.valueOf(limitnum));

                                    //시작 위치 0으로 고정하고 제한값을 남은 갯수로 하여 가져옴
                                    Getrequestclassmsglist(-(Integer.parseInt(SendTchatcount) - offsetnum), 0); //채팅 리스트 가져온다.
                                }
                            }
                        }
                        // Log.i("TAG", "Top of list");
                    } else if (!RequestChatRecyclerView.canScrollVertically(1)) {
                        // Log.i("TAG", "End of list");
                    } else {
                        //  Log.i("TAG", "idle");
                    }
                }
            }
        });


        //채팅방을 클릭을 하면 채팅의 총 갯수를 보내준다. -> 그 채팅갯수로 페이징 처리를 한다.
        //offset을 총갯수에서 10개를 뺀 값 넣어주고
        //limit 10넣어준다면? 페이징 가능!

        if(Sendroommaketype.equals("2")) {
            Chatmessagelist.clear();
            progressBar.setVisibility(View.VISIBLE);

            //paging 수 보다 크면 페이징처리를 하고
            if (Integer.parseInt(SendTchatcount) > limitnum) {
                int offset = Integer.parseInt(SendTchatcount) - limitnum;
                Getrequestclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
            } else { //paging 수 보다 작으면 그 수에 맞춰서 보여주면 됨
                Getrequestclassmsglist(limitnum, 0); //채팅 리스트 가져온다.
            }
        }


        //카메라 클릭시
        camerabtn  = (ImageView) this.findViewById(R.id.camerabtn);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(oContext);

                builder.setTitle("게시판 사진 업로드");

                builder.setItems(R.array.imgckarray, new DialogInterface.OnClickListener(){
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){ //사진첩

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                            // intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent,"Select Picture"), OPEN_GALLERY);
                        }else{ //카메라
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename));
                            oActivity_classinquiryroom.startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);

                        }
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });
    }

    //해당 채팅방 정보를 가져옴
    public void Getroominfo(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
//        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
//        requestMap.put("uid", uid);
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);


        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getrequestclassinfo(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }



    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.requestchatmenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기

//
//                //전 activity로 값을 리턴
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("completeval", "0"); //뷰에서 뒤로가기
//                oActivity_classinquiryroom.setResult(RESULTCODE, resultIntent);
//                oActivity_classinquiryroom.finish();

                finish();

                break;
            case R.id.outtab: //방 나가기 - 소켓에서 방삭제, db에서 방 삭제 - 진행중
                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                builder.setTitle("채팅방 나가기").setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                    //String.valueOf(ChatRoominfo.get(0).get("pid2out"))
                    //pid1 pid2 가 둘다 0이면 수정만 해주고, 둘중 하나가 1이 되어 있는 상태라면 방을 삭제한다.
                    try {
                        //이유 1. 한 사람이 나가고 데이터가 변경이 안되어있고 둘다 0 0으로 가져온상태임. 한사람이 나가면 데이터를 다시 불러와야함,


                        //둘다 0일 경우 나간 유저를 수정
                        if(String.valueOf(ChatRoominfo.get(0).get("pid1out")).equals("0") && String.valueOf(ChatRoominfo.get(0).get("pid2out")).equals("0")){
                            //db에서 해당 out 유저에 대해서 1로 변경 - 채팅방의 내용을 고치는 것
                            editrequestclassroom();
                        }else{// 나간사람이 하나 있는 경우 방 삭제
                            //마지막 나간사람이기에 방을 삭제함.
                            deleterequestclassroom();

                            //채팅방 메세지 전부 지우기
                            deleterequestclassmessage();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //소켓에서 방을 내보내기
                    try {
                        oSocketSend.SendSocketData("REXIT", String.valueOf(ChatRoominfo.get(0).get("idx")), "",Sessionlist.get(1).get(0), "", "0","", roomtype, "0", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    finish();


                        //Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                    }
                });


                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        GlobalClass.chatroomaccess = 1;
        GlobalClass.myclasschatroomaccess = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();


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

                    if (urlget2.equals("1")) { //채팅방 정보를 가져온다.
                        Log.d("onResponse ? ", "onResponse 채팅방 정보 : " + resultlist);
                        //pid1, pid2정보도 같이 가져옴.

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "채팅방 정보들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "채팅방 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                ChatRoominfo.add(jobj);
                            }

                            Log.d("onResponse ? ", "채팅방 정보들 : all" + String.valueOf(ChatRoominfo)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //데이터 구분하여 출력
                        try {
                            InputRequestClassinfo(ChatRoominfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }else if(urlget2.equals("2")){ //방 정보 수정
                        Log.d("onResponse ? ", "onResponse 채팅방 정보 수정 : " + resultlist);


                    }else if(urlget2.equals("3")) { //채팅 리스트 가져오기
                        Log.d("onResponse ? ", "onResponse 채팅 리스트 가져오기 : " + resultlist);
                        progressBar.setVisibility(View.GONE);
                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "채팅방 정보들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "채팅방 정보들 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                //noreaduid
                                //JSONArray uidjson = new JSONArray(jobj.get("noreaduid").toString());
                                //JSONObject uidobj = new JSONObject(String.valueOf(uidjson));
                                //Log.d("onResponse ? ", "----------------안읽은 유저들---------------- : " + String.valueOf(uidjson));

                                ChattingForm ChattingForm;

                                //0. 일반채팅, 1. 공지사항을 나타냄 2. 질문
                                if(jobj.get("type").toString().equals("1")) {
                                    ChattingForm = new ChattingForm("2", jobj.get("idx").toString(), jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), "", jobj.get("noreadnum").toString().toString(), "",jobj.get("imgchk").toString().toString(), jobj.get("rid_myclass").toString().toString(),jobj.get("availablilty").toString().toString(), false);
                               // }else if(jobj.get("type").toString().equals("2")){
                                  //  ChattingForm = new ChattingForm("3", jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), "", jobj.get("noreadnum").toString().toString(), "",jobj.get("imgchk").toString().toString(), jobj.get("rid_myclass").toString().toString(),false);
                                }else{//일반채팅
                                    String profileimg = String.valueOf(jobj.get("profilebasicuri"))+String.valueOf(jobj.get("profilesrc"));
                                    if(String.valueOf(Sessionlist.get(1).get(0)).equals(String.valueOf(jobj.get("uid")))){ //나
                                        ChattingForm = new ChattingForm("1", jobj.get("idx").toString(), jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), profileimg, jobj.get("noreadnum").toString().toString(), jobj.get("noreaduid").toString(),jobj.get("imgchk").toString().toString(), jobj.get("rid_myclass").toString().toString(), jobj.get("availablilty").toString().toString(),false);
                                    }else{//상대방
                                        ChattingForm = new ChattingForm("0", jobj.get("idx").toString(), jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), profileimg, jobj.get("noreadnum").toString().toString(), jobj.get("noreaduid").toString(),jobj.get("imgchk").toString().toString(), jobj.get("rid_myclass").toString().toString(),jobj.get("availablilty").toString().toString(), false);
                                    }
                                }
                                Chatmessagelist.add(i, ChattingForm);
                            }

                            Log.d("onResponse ? ", "채팅방 정보들 : all" + String.valueOf(Chatmessagelist)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //데이터를 바로 출력시킬것
                        MakeRequestClassChatrecycle(Chatmessagelist);

                    }else if(urlget2.equals("4")) { //채팅방 삭제
                        Log.d("onResponse ? ", "onResponse 채팅방 정보 삭제 : " + resultlist);

                    }else if(urlget2.equals("5")) { //채팅방 메세지 삭제
                        Log.d("onResponse ? ", "onResponse 채팅방 메세지 삭제 : " + resultlist);

                    }else if(urlget2.equals("6")) { //채팅방 메세지의 읽음 체크 삭제
                        Log.d("onResponse ? ", "onResponse 채팅방 메세지의 읽음 체크 삭제 : " + resultlist);

                    }else if(urlget2.equals("7")){ //Fcm 노티피케이션 실행
                        //현재 사용 x

                       // Log.d("onResponse ? ", "onResponse Fcm 노티피케이션 실행 : " + resultlist);

                    }else if(urlget2.equals("8")){ //채팅 이미지 업로드 후 받아오는 부분
                        Log.d("onResponse ? ", "채팅 이미지 업로드 후 받아오는 부분 : " + resultlist);

                       String imguri = "";

                        JSONObject jobj = null;
                        try {
                             jobj = new JSONObject(String.valueOf(resultlist));
                            Log.d("onResponse ? ", "채팅 이미지 업로드 : " + String.valueOf(jobj.get("result")));

                            imguri = String.valueOf(jobj.get("result"));
                            } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        try {
                            oSocketSend.SendSocketData("CHATSEND", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("participantnum")), Sessionlist.get(1).get(0), imguri, "1","", roomtype, "0", "0");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        imguploadlist_uri.clear();
                    }else if(urlget2.equals("9")){ //질문 메세지에서 예 아니요 클릭
                        Log.d("onResponse ? ", "질문 메세지에서 예 아니요 클릭 : " + resultlist);

                        JSONObject jobj = null;
                        try {
                            jobj = new JSONObject(String.valueOf(resultlist));
                            Log.d("onResponse ? ", "질문 메세지에서 예 아니요 클릭 : " + String.valueOf(jobj.get("result")));
                            Log.d("onResponse ? ", "질문 메세지에서 예 아니요 클릭 : " + String.valueOf(jobj.get("cktype")));


                            if(String.valueOf(jobj.get("cktype")).equals("1")){//승인
                                //조건
                                //입장 시킬 조건! 입장은 소켓서버에 내 uid가 입장이라는 것을 알려주기 위함임.
                                //소켓서버에 내 uid의 invitechk를 1 -> 2로 변경해주면 됨.

                                //채팅 다시 불러오기
                                Chatmessagelist.clear();
                                if (Integer.parseInt(SendTchatcount) > limitnum) {
                                    int offset = Integer.parseInt(SendTchatcount) - limitnum;
                                    Getrequestclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
                                } else { //paging 수 보다 작으면 그 수에 맞춰서 보여주면 됨
                                    Getrequestclassmsglist(limitnum, 0); //채팅 리스트 가져온다.
                                }

                                //과외방으로 이동
                                Intent intent = new Intent(oContext, Myclassroomactivity.class);
                                intent.putExtra("roommaketype", "3"); //1. 방처음 만들때 2. 만들어진 방에 들어올때 3. 방에 입장할때
                                intent.putExtra("roomidx", String.valueOf(jobj.get("rid_myclass"))); //룸 고유번호
                                intent.putExtra("Tchatcount", String.valueOf(jobj.get("myclass_chatcount"))); //총 채팅 갯수
                                startActivity(intent);

                            }else{ //거절



                                //채팅 다시 불러오기
                                Chatmessagelist.clear();
                                if (Integer.parseInt(SendTchatcount) > limitnum) {
                                    int offset = Integer.parseInt(SendTchatcount) - limitnum;
                                    Getrequestclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
                                } else { //paging 수 보다 작으면 그 수에 맞춰서 보여주면 됨
                                    Getrequestclassmsglist(limitnum, 0); //채팅 리스트 가져온다.
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //과외문의 채팅리스트 만들기
    public void MakeRequestClassChatrecycle(ArrayList<ChattingForm> Chatlistjsonarray){

        Log.d("과외문의 리스트 이다.", String.valueOf(Chatlistjsonarray));
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getApplicationContext()); //그리드 매니저 선언
        //LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getApplicationContext()); //그리드 매니저 선언
       // LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);


        RequestChatRecyclerAdapter RequestChatRecyclerAdapter = new RequestChatRecyclerAdapter(getApplicationContext(), oActivity_classinquiryroom); //내가만든 어댑터 선언
        RequestChatRecyclerView.setLayoutManager(LinearLayoutManager);

        if(pagenum == 1){
            RequestChatRecyclerView.scrollToPosition(Chatlistjsonarray.size()-1); //스크롤이 맨밑으로 이동하도록 처리
        }else{
            //값을 10개씩 가져온다고 했을때 가져온 갯수에 10씩 뺀값을 위치로 하여 지정한다.
            int offsetnum = limitnum*(pagenum-1);// 10 20 30 40 위치
            RequestChatRecyclerView.scrollToPosition(Chatlistjsonarray.size()-offsetnum); //스크롤이 중간위치로 이동하도록 처리
        }

        //LinearLayoutManager.setReverseLayout(true);
        //LinearLayoutManager.setStackFromEnd(true);

        RequestChatRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        RequestChatRecyclerAdapter.setColor(""); //배경색 없음
        RequestChatRecyclerAdapter.setType("1"); //과외문의
        RequestChatRecyclerAdapter.setRecycleList(Chatlistjsonarray); //arraylist 연결
        RequestChatRecyclerView.setAdapter(RequestChatRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        RequestChatRecyclerAdapter.setOnItemClickListener(new RequestChatRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<ChattingForm> list, int type) {

                //질문 메세지에서 예 아니요 클릭시
                if(type == 1){ //승인
                    Log.d("----tag----", "승인" + list.get(position).getsendrid());
                    //승인시 muclassuserlist에 uid와 rid가 연결된 데이터의 invitechk를 2로 변경할 것
                    //선택시 메세지의 유효성도 없앨 것.
                    //availablilty 0 으로 변경해여 사용 못하게 할 것.

                    //예 클릭시 방으로 접속을 할 것.
                    SetQuestionmessageck(type, list.get(position).getchatidx(), list.get(position).getsendrid());
                }else if(type == 0){ //거절
                    Log.d("----tag----", "거절" + list.get(position).getsendrid());
                    //승인시 muclassuserlist에 uid와 rid가 연결된 데이터의 invitechk를 3로 변경할 것
                    //아니요 클릭시 유효성을 없애고 방접근을 막을 것.
                    //availablilty 0 으로 변경해여 사용 못하게 할 것.
                    SetQuestionmessageck(type, list.get(position).getchatidx(), list.get(position).getsendrid());
                }
            }
        });

    }

    //해당 채팅방 정보를 가져옴
    public void SetQuestionmessageck(int cktype, String chatidx, String rid_myclass) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid);

        //과외 문의 방 rid
        //RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        //requestMap.put("roomidx", roomidx);

        //내 과외 방 rid
        RequestBody rid_myclass_ = RequestBody.create(MediaType.parse("text/plain"), rid_myclass);
        requestMap.put("rid_myclass", rid_myclass_);

        RequestBody cktype_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(cktype));
        requestMap.put("cktype", cktype_);
        RequestBody chatidx_ = RequestBody.create(MediaType.parse("text/plain"), chatidx);
        requestMap.put("chatidx", chatidx_);


        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.setquestionmessageck(
                9,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //과외문의 정보에 맞게 보여준다.
    public void InputRequestClassinfo(ArrayList<JSONObject> RequestchatRoominfpjsonarray) throws JSONException {
        if(Sendroommaketype.equals("2")){
            RoomConnect(); //방 접속
        }


        String othername = "";
        String othernicname = "";
        String otherusertype = "";
        String otherprofileimg = "";
        //유저가 pid1인지 pid2인지 체크한다.
        //상대 정보를 채팅방 상단에 보여준다.
        if(String.valueOf(Sessionlist.get(1).get(0)).equals(String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1")))){
            MyuidLocate = "pid1out";

            otheruid = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2"));
            othername = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2username"));
            othernicname = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2usernicname"));
            otherusertype = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2usertype"));
            otherprofileimg = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2userimg"));
           // Log.d("onResponse ? ", "--------pid2username----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2username"))); //info
            //Log.d("onResponse ? ", "--------pid2usernicname----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2usernicname"))); //info
            //Log.d("onResponse ? ", "--------pid2usertype----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2usertype"))); //info
            //Log.d("onResponse ? ", "--------pid2userimg----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid2userimg"))); //info
        }else{
            MyuidLocate = "pid2out";

            otheruid = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1"));
            othername = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1username"));
            othernicname = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1usernicname"));
            otherusertype = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1usertype"));
            otherprofileimg = String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1userimg"));
//            Log.d("onResponse ? ", "--------pid1username----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1username"))); //info
//            Log.d("onResponse ? ", "--------pid1usernicname----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1usernicname"))); //info
//            Log.d("onResponse ? ", "--------pid1usertype----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1usertype"))); //info
//            Log.d("onResponse ? ", "--------pid1userimg----------" + String.valueOf(RequestchatRoominfpjsonarray.get(0).get("pid1userimg"))); //info
        }

        //내가 학생일때, 상대방이 학생일때 안보이게 하기
        if(otherusertype.equals("2")){
            requestlinear.setVisibility(View.GONE);
        }


        profilename.setText(othername);
        profileinfo.setText(usertype_replace(otherusertype));


        Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+otherprofileimg);
        Picasso.get()
                .load(imageUri) // string or uri 상관없음
                .resize(200, 200)
                .centerCrop()
                .into(profileimg);
        //Log.d("onResponse ? ", "--------otheruid----------" + String.valueOf(otheruid)); //info



        if(Sendroommaketype.equals("1")){ //소켓에 연결을 한다. 소켓에 방을 만든다.

            //방을 처음 만드는 부분 - 방을 만들고 해당 로그인한 닉네임으로 소켓연결 후 방을 만듬.
            try {
                oSocketSend.SendSocketData("CROOM", String.valueOf(RequestchatRoominfpjsonarray.get(0).get("idx")), String.valueOf(RequestchatRoominfpjsonarray.get(0).get("participantnum")),Sessionlist.get(1).get(0), "", "","", roomtype, "0", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(Sendroommaketype.equals("2")){ //db에서 해당방의 인원이 들어왔는지 체크하고 인원이 부족하면 소켓을 연결하고 db 수정한다.

        }


        SendChat(); //전송 가능
        gopaypage(); //결제 페이지로 이동
    }

    public void gopaypage(){
        //과외신청하기 버튼
        requestbtn  = (Button) this.findViewById(R.id.requestbtn);
        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oContext, ClassRequestformactivity.class); //선생일때
                intent.putExtra("type", "1");  //1. 과외문의 2. 내과외
                intent.putExtra("teacheruid", otheruid);  //신청할 과외 쌤 uid
                startActivity(intent);
            }
        });
    }


    //과외문의 방 수정
    public void editrequestclassroom(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 방 만들 사람 id, 방 요청 할 id, 내 닉네임 id
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid);
        RequestBody MyuidLocate_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(MyuidLocate));
        requestMap.put("MyuidLocate", MyuidLocate_);


        call = retrofitService.editrequestclassroom(
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //과외문의 방 삭제
    public void deleterequestclassroom(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 방 만들 사람 id, 방 요청 할 id, 내 닉네임 id
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        call = retrofitService.deleterequestclassroom(
                4,
                requestMap
        );
        RestapiResponse(); //응답
    }
    //과외문의 해당 방에 대해서 채팅 내용 전부 삭제
    public void deleterequestclassmessage(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 방 만들 사람 id, 방 요청 할 id, 내 닉네임 id
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        call = retrofitService.deleterequestclassmsg(
                5,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //과외문의 해당 방에 대해서 채팅 내용 전부 삭제
//    public void deletereadchk(String chatidx){
//        //프로필 정보 가져오는 서버통신
//        RestapiStart(); //레트로핏 빌드
//        HashMap<String, RequestBody> requestMap = new HashMap<>();
//        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
//        requestMap.put("roomidx", roomidx);
//        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0))); //읽음 데이터를 삭제하기 위해 보낸다.
//        requestMap.put("uid", uid);
//        RequestBody chatidx_ = RequestBody.create(MediaType.parse("text/plain"), chatidx); //읽음 데이터를 삭제하기 위해 보낸다.
//        requestMap.put("chatidx", chatidx_);
//
//        call = retrofitService.deletemsgreadchk(
//                6,
//                requestMap
//        );
//        RestapiResponse(); //응답
//    }

    //채팅 리스트 가져오기
    public void Getrequestclassmsglist(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0))); //읽음 데이터를 삭제하기 위해 보낸다.
        requestMap.put("uid", uid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getrequestclassmsg(
                limit,
                offset,
                3,
                requestMap
        );
        RestapiResponse(); //응답
    }
    

    //소켓을 통해 받은 메세지를 서비스에서 이 함수를 불러와 처리하는 부분
    public void recievemessage(JsonArray jarray_chatdata) throws JSONException {

        String commend = String.valueOf(jarray_chatdata.get(0)).substring(1);
        String commend_sub = commend.substring(0, commend.length() - 1);

        String roomid = String.valueOf(jarray_chatdata.get(1)).substring(1);
        String roomid_sub = roomid.substring(0, roomid.length() - 1);

        String uid = String.valueOf(jarray_chatdata.get(2)).substring(1);
        String uid_sub = uid.substring(0, uid.length() - 1);
        String msg = String.valueOf(jarray_chatdata.get(3)).substring(1);
        String msg_sub = msg.substring(0, msg.length() - 1);
        String regdate = String.valueOf(jarray_chatdata.get(4)).substring(1);
        String regdate_sub = regdate.substring(0, regdate.length() - 1);
        //유저 이름
        String username = String.valueOf(jarray_chatdata.get(9)).substring(1);
        String username_sub = username.substring(0, username.length() - 1);
        //해당 방이 맞는지 체크한다.
        //roomid_sub 전송해온 방 idx와 내가 있는 방이 맞을때만 실행시킬 것.!!!
        if(roomid_sub.equals(Sendroomidx)) {

            if (commend_sub.equals("CHATSEND")) { //채팅전송
                //맥스유저
                String maxnum = String.valueOf(jarray_chatdata.get(6)).substring(1);
                String maxnum_sub = maxnum.substring(0, maxnum.length() - 1);

                //안읽음 갯수
                // String noreadnum = String.valueOf(jarray_chatdata.get(7)).substring(1);
                // String noreadnum_sub = noreadnum.substring(0, noreadnum.length() - 1);


                //이미지 여부
                String imgchk = String.valueOf(jarray_chatdata.get(10)).substring(1);
                String imgchk_sub = imgchk.substring(0, imgchk.length() - 1);

                //안읽은 uid
                String noreaduid = String.valueOf(jarray_chatdata.get(11)).substring(1);
                String noreaduid_sub = noreaduid.substring(0, noreaduid.length() - 1);
                JSONArray noreaduidarray = new JSONArray(noreaduid_sub);

                //프로필 이미지
                String profileimg = String.valueOf(jarray_chatdata.get(5)).substring(1);
                String profileimg_sub = profileimg.substring(0, profileimg.length() - 1);

                //받아온 문자가 내문자라면?
                String who = "";
                if (uid_sub.equals(String.valueOf(Sessionlist.get(1).get(0)))) {//나
                    who = "1";
                } else { //상대방
                    who = "0";
                }

                //이 메세지를 보내준 방의 idx
                String sendrid = String.valueOf(jarray_chatdata.get(13)).substring(1);
                String sendrid_sub = sendrid.substring(0, sendrid.length() - 1);

                //채팅 고유값
                String chatidx = String.valueOf(jarray_chatdata.get(14)).substring(1);
                String chatidx_sub = chatidx.substring(0, chatidx.length() - 1);

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm(who, chatidx_sub, uid_sub, username_sub, msg_sub, regdate_sub, profileimg_sub, String.valueOf(noreaduidarray.length()), String.valueOf(noreaduidarray), imgchk_sub, sendrid_sub,"1", false);
                Chatmessagelist.add(ChattingForm);

//                Log.d("onResponse ? ", "데이터 받앗니?! : " + chatidx_sub);
//                Log.d("onResponse ? ", "데이터 받앗니?! : " + chatidx_sub);
//                Log.d("onResponse ? ", "데이터 받앗니?! : " + chatidx_sub);

                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            } else if (commend_sub.equals("REXIT")) { //방나가기

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm("2", "", uid_sub, username_sub, msg_sub, regdate_sub, "", "", "", "0", "", "1",false);
                Chatmessagelist.add(ChattingForm);

                //방정보를 다시 가져옴 : 다른 사람이 나가기 버튼 누를때 판별하기 위함
                ChatRoominfo = new ArrayList<>();
                Getroominfo(0, 0); //방정보와 유저 정보를 가져온다.

                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            } else if (commend_sub.equals("CROOM")) { //방 만들기

                //이 메세지를 보내준 방의 idx
                String sendrid = String.valueOf(jarray_chatdata.get(13)).substring(1);
                String sendrid_sub = sendrid.substring(0, sendrid.length() - 1);

                //채팅 고유값
                String chatidx = String.valueOf(jarray_chatdata.get(14)).substring(1);
                String chatidx_sub = chatidx.substring(0, chatidx.length() - 1);

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm("2", chatidx_sub, uid_sub, username_sub, msg_sub, regdate_sub, "", "", "", "0",sendrid_sub, "1",false);
                Chatmessagelist.add(ChattingForm);

                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            } else if (commend_sub.equals("CONNECTROOM")) { //방 접속 표시

                //안읽음 데이터를 수정해주는 부분이다. http통신하지 않는다.
                for (int i = 0; i < Chatmessagelist.size(); i++) {

                    Log.d("=============getnoreaduidarr=============== : ", String.valueOf(Chatmessagelist.get(i).getnoreaduidarr()));

                    //안읽음 uid가 빈값이 아닐때
                    if (String.valueOf(Chatmessagelist.get(i).getnoreaduidarr()).length() > 2) {
                        Log.d("============= getnoreaduidarr : ", String.valueOf(Chatmessagelist.get(i).getnoreaduidarr()));
                        JSONArray jarray = null;
                        //try {
                        jarray = new JSONArray(String.valueOf(Chatmessagelist.get(i).getnoreaduidarr()));
                        Log.d("============= getnoreaduidarr : ", String.valueOf(jarray));
                        //Log.d("============= getnoreaduidarr : ", String.valueOf(jarray.get(0)));

                        //안읽음 리스트에 내 유저 idx가 있으면 -1처리할 것
                        Log.d("============= uid_sub : ", String.valueOf(uid_sub + "전달!!!!!!!!!!!"));

                        //접속한 uid가 메세지의 안읽은 목록에 있다면 안읽음 수를 -1한다.
                        for (int j = 0; j < jarray.length(); j++) {
                            if (String.valueOf(jarray.get(j)).equals(uid_sub)) {

                                int resuntnum = Integer.parseInt(Chatmessagelist.get(i).getnoreadnum()) - 1;
                                Chatmessagelist.get(i).setnoreadnum(String.valueOf(resuntnum));

                                //읽은 uid를 삭제하고 다시 기입해줌
                                jarray.remove(j);
                                //Log.d("============= 변경된 값 : ", String.valueOf(jarray));
                                //Log.d("============= 변경된 값 : ", String.valueOf(jarray.length()));
                                Chatmessagelist.get(i).setnoreaduidarr(String.valueOf(jarray));
                            }
                        }
                    }
                }
                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            }
        }
    }

    public void SendChat(){
        //채팅 전송 버튼
        chattingwritebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!String.valueOf(chattingedit.getText()).equals("")) {
                    Senddata = String.valueOf(chattingedit.getText());


                    //소켓 전송
                    try {
                        oSocketSend.SendSocketData("CHATSEND", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("participantnum")), Sessionlist.get(1).get(0), Senddata, "0","", roomtype, "0", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //에러때문에 edittext 초기화
                    if (chattingedit.length() > 0) {
                        chattingedit.getText().clear();
                    }
                }
            }
        });
    }

    //채팅방에 접속한것 체크하는 부분 : 룸 접속
    public void RoomConnect(){
        //채팅 전송 버튼

        try {
            oSocketSend.SendSocketData("CONNECTROOM", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("participantnum")), Sessionlist.get(1).get(0), "", "","", roomtype, "0", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //채팅방에 접속한것 체크하는 부분 : 룸 미접속
    public void RoomnoConnect(){
        //채팅 전송 버튼

        try {
            oSocketSend.SendSocketData("NOCONNECTROOM", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("participantnum")), Sessionlist.get(1).get(0), "", "","", roomtype, "0", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        RoomnoConnect();
        GlobalClass.chatroomaccess = 0;
    }

    //유저타입 문자열로 변환
    public String usertype_replace(String subject){
        if(subject.equals("1")){
            return "선생님";
        }else if(subject.equals("2")){
            return "학생";
        }else{
            return "-";
        }
    }
    //학생 학년 문자열로 변환
    public String studentage_replace(String subject){
        if(subject.equals("0")){
            return "사회인";
        }else if(subject.equals("1")){
            return "대학생";
        }else if(subject.equals("2")){
            return "n수생";
        }else if(subject.equals("3")){
            return "고3";
        }else if(subject.equals("4")){
            return "고2";
        }else if(subject.equals("5")){
            return "고1";
        }else if(subject.equals("6")){
            return "중3";
        }else if(subject.equals("7")){
            return "중2";
        }else if(subject.equals("8")){
            return "중1";
        }else if(subject.equals("9")){
            return "초6";
        }else if(subject.equals("10")){
            return "초5";
        }else if(subject.equals("11")){
            return "초4";
        }else if(subject.equals("12")){
            return "초3";
        }else if(subject.equals("13")){
            return "초2";
        }else if(subject.equals("14")){
            return "초1";
        }else{
            return "-";
        }
    }





    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("picturepos!!!", String.valueOf(intent.hasExtra("data"))); //false
//        Log.d("picturepos!!!", String.valueOf(intent.getData())); // 선택한 이미지 데이터
//        Log.d("picturepos!!!", String.valueOf(requestCode)); //2 = 사진첩
//        Log.d("picturepos!!!", String.valueOf(resultCode)); //-1
//        Log.d("picturepos!!!", String.valueOf(RESULT_OK)); //-1
//        Log.d("picturepos!!!", String.valueOf(intent.getExtras())); //null
//        Log.d("ImgPosition!!!", String.valueOf(ImgPosition)); //이미지 순서
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
                                .start(Classinquiryroomactivity.this);
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
                                Toast.makeText(Classinquiryroomactivity.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
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

                               // Imagerecycle_chatimg(imguploadlist_uri); //리사이클러뷰에 출력
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

                               // Imagerecycle_chatimg(imguploadlist_uri); //리사이클러뷰에 출력
                            }
                            ImageSend();
                        }
//                        //사진 갯수 변경
//                        picturenum.setText(imguploadlist_uri.size()+"/10");
//
//                        if(imguploadlist_uri.size() == 10){
//                            Toast.makeText(Nboardwrite.this, "사진 갯수 10개를 초과하였습니다.", Toast.LENGTH_SHORT).show();
//                        }


                        //Log.d("----------imguploadlist_uri----------", String.valueOf(imguploadlist_uri.get(0).getUri()));


//                        //이미지 선택후 크롭화면 나오도록 설정
//                        CropImage.activity(intent.getData()).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
//                                .start(Nboardwrite.this);




                        //이 시점에서 이미지 파일을 전송한다.



                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: //크롭한 후 이미지 받아오는 부분 - 여기서 사용 안함
                    Log.d("crop!!1", String.valueOf(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE));
                    Log.d("crop!!2", String.valueOf( CropImage.getActivityResult(data)));

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == Activity.RESULT_OK) {

                        File fileCacheDir = getCacheDir();
                        String getCacheDir = fileCacheDir.getPath();

                        Log.d("crop! - getCacheDir", getCacheDir);


                        Log.d("crop!!3", String.valueOf(result.getUri()));
                        Log.d("사진에서 선택한 사진 경로", result.getUri().getPath());

                        //uri 전용 arraylist에 저장
                        ImgFormMulti imgForm = new ImgFormMulti(result.getUri(),result.getUri(),"-", true);
                        if(imguploadlist_uri.size() == 0){
                            imguploadlist_uri.add(imgForm);
                        }else if(imguploadlist_uri.size() < 10){
                            imguploadlist_uri.add(0, imgForm);
                        }

                        //Imagerecycle_chatimg(imguploadlist_uri); //리사이클러뷰에 출력


                        ImageSend();
                    }

                    break;
            }
        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
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




    //이미지를 전송한다. : http 통신으로 이미지를 저장하고 경로와 이름만 받아온다. 받아온 이름과 경로를 소켓으로 전송한다. 끝
    public void ImageSend(){

//        여기서 이미지 전송 처리도 할 것,
//        imguploadlist_uri
//        이미지 파일을 보낼 수 있는 형태로 변환해주는 부분
//         파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
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

        chatimgupload();
    }

    public void chatimgupload(){
        RestapiStart(); //레트로핏 빌드

        call = retrofitService.onlyimgupload(
                8, imguploadlist_multipart
        );
        RestapiResponse(); //응답
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Sendroommaketype.equals("2")){
            RoomConnect(); //방 접속
        }
    }
}

