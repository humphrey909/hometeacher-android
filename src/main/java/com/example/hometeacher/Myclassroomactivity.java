package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.MyclassChatUserlistRecyclerAdapter;
import com.example.hometeacher.Adapter.MyclassUserSearchRecyclerAdapter;
import com.example.hometeacher.Adapter.RequestChatRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.ChattingForm;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.Nboard.Commentedit;
import com.example.hometeacher.Nboard.Commentnestedactivity;
import com.example.hometeacher.shared.Session;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Myclassroomactivity extends AppCompatActivity {
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    Session oSession; //자동로그인을 위한 db
    Context oContext;
    static Activity oActivity_myclassroom;
    ArrayList<ArrayList<String>> Sessionlist;

    LinearLayout addlinear, listLinear2, assignmentlinear4, vodlinear;
    //NestedScrollView nestedscrollbox;
    ProgressBar progressBar;
    RecyclerView MyclassChatRecyclerView, peopleRecyclerView;
    ImageButton backbtn;
    TextView profilename;
    TextView usercount;
    DrawerLayout drawer;
    ImageView camerabtn, outbtn, setbtn, profileimg;
    EditText chattingedit;
    Button writereview, conferencestart, gopayment;

    String roomname = "";
    String Senddata = "";

    int pagenum = 1; //처음 페이징 값을 지정
    int limitnum = 20;

    SocketSend oSocketSend;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    String Sendroommaketype; //방 입장 타입
    String Sendroomidx; //방 고유번호
    String SendTchatcount; //총 채팅갯수
    String FixPayment; //결제 금액


    ArrayList<JSONObject> JoinUserlist; //참여자 리스트
    ArrayList<JSONObject> ChatRoominfo; //내 과외 정보
    ArrayList<ChattingForm> Chatmessagelist = new ArrayList<>(); //채팅 리스트

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    String roomtype = "2"; //내 과외 방
    ImageView chattingwritebtn;

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgFormMulti> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;

    String popuptitle = "";

    String BackColor; //채팅 배경색을 지정한다.
    String TeacherUid; //방의 선생님 고유번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclassroomactivity);

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
        oSocketSend = new SocketSend(GlobalClass);

        oActivity_myclassroom = this;
        oContext = this;
        oSession = new Session(oContext);
        Sessionlist = oSession.Getoneinfo("0");

        backbtn = (ImageButton) findViewById(R.id.backbtn);
        profilename = (TextView) findViewById(R.id.profilename);
        usercount = (TextView) findViewById(R.id.usercount);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        MyclassChatRecyclerView = (RecyclerView) findViewById(R.id.MyclassChatRecyclerView) ;
        peopleRecyclerView = (RecyclerView) findViewById(R.id.peopleRecyclerView) ;
        chattingwritebtn = (ImageView) findViewById(R.id.chattingwritebtn);
        profileimg = (ImageView) findViewById(R.id.profileimg);
        chattingedit = (EditText) findViewById(R.id.chattingedit);



        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        Sendroommaketype = intent.getExtras().getString("roommaketype"); //1. 방 처음 만들때 2. 만들어진 방에 들어올때
        Sendroomidx = intent.getExtras().getString("roomidx"); //방 고유번호
        Log.d("-------------roommaketype------------",Sendroommaketype);
        Log.d("-------------roomidx------------",Sendroomidx);

        if(Sendroommaketype.equals("2") || Sendroommaketype.equals("3")){
            SendTchatcount = intent.getExtras().getString("Tchatcount"); //총 채팅 갯수 : 이 변수로 채팅 페이징 처리를 할 것
            Log.d("-------------SendTchatcount------------",SendTchatcount);
        }

        //뒤로가기 버튼
        backbtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                finish();
            }
        });

        //스크롤뷰의 밑바당에 부딛힐때마다 프로그레스바가 돌고 데이터를 새로 가져온다. 열개씩 가져올 것.
        MyclassChatRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(Sendroommaketype.equals("2") || Sendroommaketype.equals("3")) {
                    if (!MyclassChatRecyclerView.canScrollVertically(-1)) { //맨위로 올렸을때 작동
                        if (Integer.parseInt(SendTchatcount) > limitnum) { // 채팅 갯수가 제한 수 보다 클때만 작동

                            pagenum++;


                            int offsetnum = limitnum * pagenum; // 10 20 ... 90 100
                            int offset = Integer.parseInt(SendTchatcount) - offsetnum;  //시작 위치

                            //시작 위치가 0보다 클때만 새 페이지를 가져온다.
                            if (offset >= 0) {
                                progressBar.setVisibility(View.VISIBLE);
                                Getmyclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
                                Log.d("pagenum-----", String.valueOf(pagenum));
                            } else { //시작 위치가 - 값 일때
                                //시작위치가 제한 값보다 클때 - 보여줄 데이터가 남아있다는 뜻
                                if (Integer.parseInt(SendTchatcount) - offsetnum > -limitnum) { // 115 - 100 = 15 115 - 110 = 5 115 - 120 = -5
                                    progressBar.setVisibility(View.VISIBLE);
                                    Log.d("------pagenum-----", String.valueOf(Integer.parseInt(SendTchatcount) - offsetnum));
                                    Log.d("------pagenum-----", String.valueOf(limitnum));

                                    //시작 위치 0으로 고정하고 제한값을 남은 갯수로 하여 가져옴
                                    Getmyclassmsglist(-(Integer.parseInt(SendTchatcount) - offsetnum), 0); //채팅 리스트 가져온다.
                                }
                            }
                        }
                        // Log.i("TAG", "Top of list");
                    } else if (!MyclassChatRecyclerView.canScrollVertically(1)) {
                        // Log.i("TAG", "End of list");
                    } else {
                        //  Log.i("TAG", "idle");
                    }
                }
            }
        });

//        if(Sendroommaketype.equals("2") || Sendroommaketype.equals("3")) {
//            //paging 수 보다 크면 페이징처리를 하고
//            if (Integer.parseInt(SendTchatcount) > limitnum) {
//                int offset = Integer.parseInt(SendTchatcount) - limitnum;
//                Getmyclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
//            } else { //paging 수 보다 작으면 그 수에 맞춰서 보여주면 됨
//                Getmyclassmsglist(limitnum, 0); //채팅 리스트 가져온다.
//            }
//        }else{
//            progressBar.setVisibility(View.GONE);
//        }



        //카메라 클릭시
        camerabtn  = (ImageView) this.findViewById(R.id.camerabtn);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(oContext);

                builder.setTitle("내 과외 사진 업로드");

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
                            oActivity_myclassroom.startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);

                        }
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

        //방 나가기 버튼
        outbtn  = (ImageView) this.findViewById(R.id.outbtn);
        outbtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                builder.setTitle("채팅방 나가기").setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                        //참여자 리스트에서 나가기 누른 uid를 삭제한다.
                        //JoinUserlist 유저 수가 마지막이면 채팅 내용 전부 삭제
                        try {
                            Log.d("JoinUserlist", String.valueOf(JoinUserlist.size()));
                            if(JoinUserlist.size() > 1){ //한명이상이면
                                editmyclassroom_user();
                            }else{ //한명이면

                                //방 삭제, 채팅 삭제
                                editmyclassroom_user();
                                deletemyclassroom();
                                deletemyclassmessage();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //소켓에서 방을 내보내기
                        try {
                            oSocketSend.SendSocketData("REXIT", String.valueOf(ChatRoominfo.get(0).get("idx")), "",Sessionlist.get(1).get(0), "방을 나갔습니다.", "0","", roomtype, "1", "0");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        finish();
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
            }
        });

        //세팅 버튼
        setbtn  = (ImageView) this.findViewById(R.id.setbtn);
        setbtn.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                 Intent intent = new Intent(getApplicationContext(), MyclassSetting.class);
                 intent.putExtra("roomidx", Sendroomidx); //룸 고유번호
                 startActivity(intent);



                // intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
                // intent.putExtra("roomidx", String.valueOf(list.get(position).get("idx"))); //룸 고유번호


                //try {
                //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
                // intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
                // intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                // intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
                //startActivityForResult(intent, REQUESTCODE);
            }
        });

        //과제 버튼
        assignmentlinear4  = (LinearLayout) this.findViewById(R.id.assignmentlinear4);
        assignmentlinear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyclassProblemlist.class);
                intent.putExtra("roomidx", Sendroomidx); //룸 고유번호
                startActivity(intent);



                // intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
                // intent.putExtra("roomidx", String.valueOf(list.get(position).get("idx"))); //룸 고유번호


                //try {
                //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
                // intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
                // intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                // intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
                //startActivityForResult(intent, REQUESTCODE);
            }
        });

        //과제 리스트
        vodlinear  = (LinearLayout) this.findViewById(R.id.vodlinear);
        vodlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyclassVodlist.class);
                intent.putExtra("roomidx", Sendroomidx); //룸 고유번호
                startActivity(intent);
            }
        });


        //리뷰 작성 버튼
        writereview = (Button) findViewById(R.id.writereview);
        writereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyclassReviewwrite.class);
                intent.putExtra("type", "1"); //1. 추가, 2. 수정
                intent.putExtra("roomidx", Sendroomidx); //룸 고유번호
                intent.putExtra("teacheruid", TeacherUid); //룸의 선생님
                //선생님 고유번호 같이 보내기

                startActivity(intent);
            }
        });

        //디바이스 테스트
//        devicetest = (Button) findViewById(R.id.devicetest);
//        devicetest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), DeviceTestPage.class);
//                startActivity(intent);
//            }
//        });


        //회의 시작
        conferencestart = (Button) findViewById(R.id.conferencestart);

        //학생은 수업입장
        //선생은 수업시작
        if(String.valueOf(Sessionlist.get(1).get(2)).equals("1")){
            conferencestart.setText("수업 시작"); //선생 1
        }else{
            conferencestart.setText("수업 입장"); //학생 2
        }

        //수업 시작 버튼
        conferencestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //선생님이 입장한 상태에서만 입장하도록 한다.

                if(String.valueOf(Sessionlist.get(1).get(2)).equals("2")) { // 학생 : 2
                    GetClassjoinusercheck(0, 0);
                }else{ //선생 : 1
                    Intent intent = new Intent(getApplicationContext(), ConferenceRoomPage.class);

                    intent.putExtra("uid", String.valueOf(Sessionlist.get(1).get(0))); //uid
                    intent.putExtra("usertype", String.valueOf(Sessionlist.get(1).get(2))); //유저 타입
                    intent.putExtra("roomidx", Sendroomidx); //룸 고유번호
                    intent.putExtra("roomname", roomname); //룸 제목

                    //중복 클릭 방지를 위해 설정
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });


        //결제하기 버튼
        gopayment = (Button) findViewById(R.id.gopayment);
        if(String.valueOf(Sessionlist.get(1).get(2)).equals("1")){
            gopayment.setVisibility(View.GONE);
        }else{
            gopayment.setVisibility(View.VISIBLE);
        }

        gopayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oContext, ClassRequestformactivity.class); //선생일때
                intent.putExtra("type", "2");  //1. 과외문의 2. 내과외
                intent.putExtra("teacheruid", TeacherUid);  //신청할 과외 쌤 uid
                intent.putExtra("fixpayment", FixPayment);  //결제 금액
                intent.putExtra("roomidx", Sendroomidx);  //결제 금액
                startActivity(intent);
            }
        });

        ChatRoominfo = new ArrayList<>();
        JoinUserlist = new ArrayList<>();
        Getroominfo_first(0,0);
    }

    //내과외의 유저 삭제
    public void editmyclassroom_user() throws JSONException {

        String useridx_myclass = "";
        for(int i = 0; i<JoinUserlist.size();i++){
            Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i)));
            if(String.valueOf(JoinUserlist.get(i).get("uid")).equals(String.valueOf(Sessionlist.get(1).get(0)))){
                useridx_myclass = String.valueOf(JoinUserlist.get(i).get("idx"));
            }
        }

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody useridx_myclass_ = RequestBody.create(MediaType.parse("text/plain"), useridx_myclass);
        requestMap.put("useridx_myclass", useridx_myclass_);


        call = retrofitService.editmyclassroom_user(
                4,
                requestMap
        );
        RestapiResponse(); //응답
    }
    //내과외 방 삭제
    public void deletemyclassroom(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 방 만들 사람 id, 방 요청 할 id, 내 닉네임 id
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        call = retrofitService.deletemyclassroom(
                5,
                requestMap
        );
        RestapiResponse(); //응답
    }
    //내과외 해당 방에 대해서 채팅 내용 전부 삭제
    public void deletemyclassmessage(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값 - 방 만들 사람 id, 방 요청 할 id, 내 닉네임 id
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        call = retrofitService.deletemyclasschat(
                6,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //내과외의 다른 유저 삭제
    public void editmyclassroom_otheruser(String useridx_myclass) throws JSONException {

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody useridx_myclass_ = RequestBody.create(MediaType.parse("text/plain"), useridx_myclass);
        requestMap.put("useridx_myclass", useridx_myclass_);


        call = retrofitService.editmyclassroom_user(
                7,
                requestMap
        );
        RestapiResponse(); //응답
    }



    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.myclasschatmenu, menu);
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
            case R.id.drawertab: //채팅 방 메뉴 열기 탭
                Log.d("","--------drawertab---------");


//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer) ;
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.openDrawer(Gravity.RIGHT) ;
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //해당 채팅방 정보를 가져옴
    public void Getroominfo_first(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);


        //리뷰값 가져오기 위한 데이터
        RequestBody writeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("writeuid", writeuid);


        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getmyclassroominfo(
                limit,
                offset,
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //채팅 리스트 가져오기
    public void Getmyclassmsglist(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0))); //읽음 데이터를 삭제하기 위해 보낸다.
        requestMap.put("uid", uid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getmyclassmsg(
                limit,
                offset,
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }
    //해당 채팅방 정보를 가져옴
    public void Getroominfo(int limit, int offset) {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        //리뷰값 가져오기 위한 데이터
        RequestBody writeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("writeuid", writeuid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getmyclassroominfo(
                limit,
                offset,
                8,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //해당 채팅방 정보를 가져옴
    public void GetClassjoinusercheck(int limit, int offset) {

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("rid", rid);
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid);

//        //보낼값
//        HashMap<String, RequestBody> requestMap = new HashMap<>();
//        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
//        requestMap.put("roomidx", roomidx);
//
//        //리뷰값 가져오기 위한 데이터
//        RequestBody writeuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
//        requestMap.put("writeuid", writeuid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getconferencejoinuserlist(
                limit,
                offset,
                9,
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

                    if (urlget2.equals("1")) { //채팅방 정보를 가져온다. - 처음에만 사용
                        JoinUserlist.clear();
                        ChatRoominfo.clear();

                        Log.d("onResponse ? ", "onResponse 채팅방 정보 : " + resultlist);

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
                            InputMyClassinfo(ChatRoominfo, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //채팅 리스트
                        Log.d("onResponse ? ", "onResponse 채팅 리스트 가져오기 : " + resultlist);
                        progressBar.setVisibility(View.GONE);
                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "채팅 리스트 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "채팅 리스트 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                //noreaduid
                                //JSONArray uidjson = new JSONArray(jobj.get("noreaduid").toString());
                                //JSONObject uidobj = new JSONObject(String.valueOf(uidjson));
                                //Log.d("onResponse ? ", "----------------안읽은 유저들---------------- : " + String.valueOf(uidjson));

                                ChattingForm ChattingForm;

                                //0. 일반채팅, 1. 공지사항을 나타냄
                                if(jobj.get("type").toString().equals("1")) {
                                    ChattingForm = new ChattingForm("2",jobj.get("idx").toString(), jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), "", jobj.get("noreadnum").toString().toString(), "",jobj.get("imgchk").toString().toString(), "0","", false);
                                }else{//일반채팅
                                    String profileimg = String.valueOf(jobj.get("profilebasicuri"))+String.valueOf(jobj.get("profilesrc"));
                                    if(String.valueOf(Sessionlist.get(1).get(0)).equals(String.valueOf(jobj.get("uid")))){ //나
                                        ChattingForm = new ChattingForm("1",jobj.get("idx").toString(), jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), profileimg, jobj.get("noreadnum").toString().toString(), jobj.get("noreaduid").toString(),jobj.get("imgchk").toString().toString(),"0", "",false);
                                    }else{//상대방
                                        ChattingForm = new ChattingForm("0",jobj.get("idx").toString(), jobj.get("uid").toString(), jobj.get("name").toString(), jobj.get("message").toString(), jobj.get("regdate").toString(), profileimg, jobj.get("noreadnum").toString().toString(), jobj.get("noreaduid").toString(),jobj.get("imgchk").toString().toString(),"0", "",false);
                                    }
                                }
                                Chatmessagelist.add(i, ChattingForm);
                            }

                            Log.d("onResponse ? ", "채팅 리스트 : all" + String.valueOf(Chatmessagelist)); //info

                            //데이터를 바로 출력시킬것
                            MakeRequestClassChatrecycle(Chatmessagelist);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else if(urlget2.equals("3")){ //채팅 이미지 업로드 후 받아오는 부분
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
                            oSocketSend.SendSocketData("CHATSEND", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("maxnum")), Sessionlist.get(1).get(0), imguri, "1","", roomtype, "0","0");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        imguploadlist_uri.clear();
                    }else if(urlget2.equals("4")){ //내과외 방 수정
                        Log.d("onResponse ? ", "내과외 방 수정 : " + resultlist);


                    }else if(urlget2.equals("5")){ //내과외 방 삭제
                        Log.d("onResponse ? ", "내과외 방 삭제 : " + resultlist);


                    }else if(urlget2.equals("6")){ //내과외 해당 방에 대해서 채팅 내용 전부 삭제
                        Log.d("onResponse ? ", "내과외 해당 방에 대해서 채팅 내용 전부 삭제 : " + resultlist);


                    }else if(urlget2.equals("7")){ //내과외 다른 유저 삭제하기
                        Log.d("onResponse ? ", "내과외 다른 유저 삭제하기 : " + resultlist);
                    }else if(urlget2.equals("8")){ //채팅방 정보를 가져온다. - onresume에서 계속 사용
                        JoinUserlist.clear();
                        ChatRoominfo.clear();

                        Log.d("onResponse ? ", "onResponse 채팅방 정보 : " + resultlist);

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
                            InputMyClassinfo(ChatRoominfo, 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("9")){ //회의에 참가한 리스트를 가져온다.: 학생일때만 실행한다.
                        Log.d("onResponse ? ", "onResponse 회의방 참가자들 : " + resultlist);

                        JSONArray jarray = null;

                        int joinstring = 0;
                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "회의방 참가자들 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            if(jarray.length() != 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    Log.d("onResponse ? ", "회의방 참가자들 : " + String.valueOf(jarray.get(i)));

                                    JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));


                                    //Log.d("onResponse ? ", "회의방 참가자들 : " + String.valueOf(jobj.get("usertype")));

                                    //참가자들 중에 선생님이 있으면? 입장 가능
                                    if (jobj.get("usertype").equals("1")) {
                                        joinstring = 1;
                                    }
                                }
                            }else{ //참가한 사람이 없으면
                                joinstring = 0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //선생님이 수업에 입장하여 있을때 입장 가능 : 학생일때만
                        if(joinstring == 1){
                            Intent intent = new Intent(getApplicationContext(), ConferenceRoomPage.class);
                            intent.putExtra("uid", String.valueOf(Sessionlist.get(1).get(0))); //uid
                            intent.putExtra("usertype", String.valueOf(Sessionlist.get(1).get(2))); //유저 타입
                            intent.putExtra("roomidx", Sendroomidx); //룸 고유번호
                            intent.putExtra("roomname", roomname); //룸 제목
                            startActivity(intent);
                        }else{
                            Toast.makeText(GlobalClass, "아직 수업이 시작되지 않았습니다. ", Toast.LENGTH_SHORT).show();
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


        RequestChatRecyclerAdapter RequestChatRecyclerAdapter = new RequestChatRecyclerAdapter(getApplicationContext(), oActivity_myclassroom); //내가만든 어댑터 선언
        MyclassChatRecyclerView.setLayoutManager(LinearLayoutManager);

        if(pagenum == 1){
            MyclassChatRecyclerView.scrollToPosition(Chatlistjsonarray.size()-1); //스크롤이 맨밑으로 이동하도록 처리
        }else{
            //값을 10개씩 가져온다고 했을때 가져온 갯수에 10씩 뺀값을 위치로 하여 지정한다.
            int offsetnum = limitnum*(pagenum-1);// 10 20 30 40 위치
            MyclassChatRecyclerView.scrollToPosition(Chatlistjsonarray.size()-offsetnum); //스크롤이 중간위치로 이동하도록 처리
        }

        //LinearLayoutManager.setReverseLayout(true);
        //LinearLayoutManager.setStackFromEnd(true);
      //  Log.d("----BackColor-----",BackColor);


        RequestChatRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        RequestChatRecyclerAdapter.setColor(BackColor);
        RequestChatRecyclerAdapter.setType("2"); //내과외
        RequestChatRecyclerAdapter.setRecycleList(Chatlistjsonarray); //arraylist 연결
        MyclassChatRecyclerView.setAdapter(RequestChatRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        RequestChatRecyclerAdapter.setOnItemClickListener(new RequestChatRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<ChattingForm> list, int type) {



                // Intent intent = new Intent(getApplicationContext(), Classinquiryroomactivity.class);
                // intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
                // intent.putExtra("roomidx", String.valueOf(list.get(position).get("idx"))); //룸 고유번호


                //try {
                //  Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
                // intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
                // intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
                // intent.putExtra("uid", String.valueOf(list.get(position).get("uid")));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
                //startActivityForResult(intent, REQUESTCODE);
            }
        });
    }

    //유저 리스트를 만든다
    public void MakeUserrecycle(ArrayList<JSONObject> Userlistjsonarray) throws JSONException {
        Log.d("유저 리스트 이다.", String.valueOf(Userlistjsonarray));

        //방장이면? 내가 방장인지 체크한다.
        int superuser = 0;
        for(int i = 0; i<Userlistjsonarray.size();i++){
            if(String.valueOf(Userlistjsonarray.get(i).get("type")).equals("1")){ //방장

                if(String.valueOf(Userlistjsonarray.get(i).get("uid")).equals(Sessionlist.get(1).get(0))){ //내 uid가 방장이라면
                    superuser = 1;
                    Log.d("----superuser-----",String.valueOf(Userlistjsonarray.get(i).get("uid")));
                    Log.d("----superuser-----",String.valueOf(Userlistjsonarray.get(i).get("uid")));
                    Log.d("----superuser-----",String.valueOf(superuser));
                }
            }
        }


        //참여자 리스트를 리사이클러뷰로 생성
        peopleRecyclerView = (RecyclerView) findViewById(R.id.peopleRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        MyclassChatUserlistRecyclerAdapter oMyclassChatUserlistRecyclerAdapter = new MyclassChatUserlistRecyclerAdapter(getApplicationContext()); //내가만든 어댑터 선언
        peopleRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        //선택된 유저를 보낼 것. AddUserlist
        oMyclassChatUserlistRecyclerAdapter.setSelectUser(JoinUserlist, superuser);
        oMyclassChatUserlistRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oMyclassChatUserlistRecyclerAdapter.setRecycleList(Userlistjsonarray); //arraylist 연결
        peopleRecyclerView.setAdapter(oMyclassChatUserlistRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oMyclassChatUserlistRecyclerAdapter.setOnItemClickListener(new MyclassChatUserlistRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<JSONObject> list) throws JSONException {
               // Log.d("------list------", String.valueOf(list.get(position).get("invitechk")));

                //초대중이면 초대취소 로 선택하게 나오고, 초대 취소 클릭시 소켓, db에서 삭제 시키기
                //참여중이면 강제퇴장 으로 선택하게 하고, 클릭시 소켓, db에서 삭제 시키기


                PopupMenu poup = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    poup = new PopupMenu(Myclassroomactivity.this, v); //TODO 일반 사용
                }
                else {
                    poup = new PopupMenu(Myclassroomactivity.this, v); //TODO 일반 사용
                }


                if(String.valueOf(list.get(position).get("invitechk")).equals("1")){ //초대됨
                    getMenuInflater().inflate(R.menu.chatusermenu_invite, poup.getMenu());
                    popuptitle = "초대취소";
                }else if(String.valueOf(list.get(position).get("invitechk")).equals("2") || String.valueOf(list.get(position).get("invitechk")).equals("3")){ //참여완료
                    getMenuInflater().inflate(R.menu.chatusermenu_join, poup.getMenu());
                    popuptitle = "강제퇴장";
                }


                poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        String popup_tittle = item.getTitle().toString();
                        //TODO ==== [메뉴 선택 동작 처리] ====
                       // if(popup_tittle.contains("초대 취소")){

                      //  }
                        if(popup_tittle.contains(popuptitle)){
                            //다이어그램 띄우기
                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                            builder.setTitle(popuptitle).setMessage("정말로 "+popuptitle+" 하시겠습니까?");

                            //삭제 실시
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {


                                    //db에서 삭제시키기기
                                    try {
                                        editmyclassroom_otheruser(String.valueOf(list.get(position).get("idx")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //소켓에서 방을 내보내기
                                    try {
                                        oSocketSend.SendSocketData("REXIT", String.valueOf(ChatRoominfo.get(0).get("idx")), "",String.valueOf(list.get(position).get("uid")), popuptitle+"되었습니다. ", "0","", roomtype, "1", "0");
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
        });
    }
    //내 과외 정보에 맞게 보여준다.
    public void InputMyClassinfo(ArrayList<JSONObject> MyclassRoominfojsonarray, int continueuse) throws JSONException {

        FixPayment = String.valueOf(MyclassRoominfojsonarray.get(0).get("payment"));


        //리뷰버튼 조건 - //한번 작성하면 뜨지 말 것. //학생에게만 뜨도록 할 것.
        if(Sessionlist.get(1).get(2).equals("2")){ //학생

            if(!String.valueOf(MyclassRoominfojsonarray.get(0).get("reviewcount")).equals("0")){ //리뷰 작성함
                writereview.setVisibility(View.GONE);
            }else{ //작성 안함.
                writereview.setVisibility(View.VISIBLE);
            }
        }else{ //선생
            writereview.setVisibility(View.GONE);
        }

        listLinear2 = (LinearLayout) findViewById(R.id.listLinear2);


        //Log.d("--myclasssetinfo--", String.valueOf(MyclassRoominfojsonarray.get(0).get("myclass_setinfo")));
        //룸 설정 값이 있는경우 배경과 폰트를 변경한다.
        //설정 값이 없는 경우
        if(String.valueOf(MyclassRoominfojsonarray.get(0).get("myclass_setinfo")).equals("null")){
            //SelectColorinServer = "#d3d5d0"; //선택한 배경색
            BackColor = "#d3d5d0";
            listLinear2.setBackgroundColor(Color.parseColor(BackColor));
        }else{
            JSONArray setinfo = new JSONArray(String.valueOf(MyclassRoominfojsonarray.get(0).get("myclass_setinfo")));
            //Log.d("--myclasssetinfo--", String.valueOf(setinfo.get(0)));
            JSONObject setinfoobj = new JSONObject(String.valueOf(setinfo.get(0)));
            BackColor = String.valueOf(setinfoobj.get("backcolor"));
            listLinear2.setBackgroundColor(Color.parseColor(BackColor));

            if(!String.valueOf(setinfoobj.get("basicuri")).equals("")){
                profileimg.setBackgroundColor(Color.argb(0,0,0,0));
                Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(setinfoobj.get("basicuri"))+String.valueOf(setinfoobj.get("src")));
                Picasso.get()
                        .load(imageUri) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(profileimg);
            }
        }


        addlinear = (LinearLayout) findViewById(R.id.addlinear);

        roomname = String.valueOf(MyclassRoominfojsonarray.get(0).get("roomname")); //전체 변수로 지정
        profilename.setText(String.valueOf(MyclassRoominfojsonarray.get(0).get("roomname")));

        //유저의 네임만 가져와서 뿌려주기!!!
        JSONArray jarray = null;
        jarray = new JSONArray(String.valueOf(MyclassRoominfojsonarray.get(0).get("userlist")));

        Log.d("----------userlist-------------", String.valueOf(jarray));
        //Log.d("----------userlist-------------", String.valueOf(jarray.get(0)));

        for (int i = 0; i<jarray.length();i++){
            JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));
            JoinUserlist.add(jobj);
            Log.d("----------userlist!!!-------------", String.valueOf(jarray.get(i)));
            Log.d("----------userlist!!!type-------------", String.valueOf(jobj.get("type")));

            //방장일때
            if(String.valueOf(jobj.get("type")).equals("1")){
                //방장 uid 저장하기
                TeacherUid = String.valueOf(jobj.get("uid"));

                //uid가 로그인한 uid와 같다면
                if(String.valueOf(jobj.get("uid")).equals(Sessionlist.get(1).get(0))){
                    addlinear.setVisibility(View.VISIBLE);
                }else{
                    addlinear.setVisibility(View.GONE);
                }
            }
        }
        usercount.setText(jarray.length()+"/"+String.valueOf(MyclassRoominfojsonarray.get(0).get("maxnum")));





        //참여중인 유저를 보여줌
        MakeUserrecycle(JoinUserlist);

        Log.d("------RestapiResponse------","onResume");
        Log.d("------RestapiResponse------",String.valueOf(jarray.length()));
        Log.d("------RestapiResponse------","onResume");

        //학생 추가 버튼
        addlinear.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                Intent intent = new Intent(oContext, MyclassUseradd.class);
                intent.putExtra("accesstype", "2");// 1. 방생성할때 접근, 2. 방 생성 후 접근
                intent.putExtra("AddUserlist", String.valueOf(JoinUserlist));
                try {
                    int studentmaxnum = Integer.parseInt((String) MyclassRoominfojsonarray.get(0).get("maxnum"))-1;
                    intent.putExtra("maxnum", String.valueOf(studentmaxnum));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("rid_myclass", Sendroomidx);

                startActivity(intent);

            }
        });

        MakeRequestClassChatrecycle(Chatmessagelist);

        if(continueuse == 0){ //한번만 사용
            if(Sendroommaketype.equals("1")){ //소켓에 연결을 한다. 소켓에 방을 만든다.

                //방을 처음 만드는 부분 - 방을 만들고 해당 로그인한 닉네임으로 소켓연결 후 방을 만듬.
                try {
                    oSocketSend.SendSocketData("CROOM", String.valueOf(MyclassRoominfojsonarray.get(0).get("idx")), String.valueOf(MyclassRoominfojsonarray.get(0).get("maxnum")),Sessionlist.get(1).get(0), "방을 생성 합니다.", "0","", roomtype, "0","0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(Sendroommaketype.equals("2")){ //db에서 해당방의 인원이 들어왔는지 체크하고 인원이 부족하면 소켓을 연결하고 db 수정한다.
                RoomConnect(); //방 접속 확인
            }else if(Sendroommaketype.equals("3")){ //방에 입장할때
                oSocketSend.SendSocketData("EROOM", String.valueOf(MyclassRoominfojsonarray.get(0).get("idx")), String.valueOf(MyclassRoominfojsonarray.get(0).get("maxnum")),Sessionlist.get(1).get(0), "방에 참여하였습니다.", "0","", roomtype, "1","0");
                RoomConnect(); //방 접속 확인
            }


            SendChat(); //전송 가능

            if(Sendroommaketype.equals("2") || Sendroommaketype.equals("3")) {
                //paging 수 보다 크면 페이징처리를 하고
                if (Integer.parseInt(SendTchatcount) > limitnum) {
                    int offset = Integer.parseInt(SendTchatcount) - limitnum;
                    Getmyclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
                } else { //paging 수 보다 작으면 그 수에 맞춰서 보여주면 됨
                    Getmyclassmsglist(limitnum, 0); //채팅 리스트 가져온다.
                }
            }else{
                progressBar.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Sendroommaketype.equals("2") || Sendroommaketype.equals("3")){
            RoomConnect(); //방 접속
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //과외문의 연결 변수를 0으로 변경한다. stop에서 변경이 늦게 되는 경우를 잡기위해
        GlobalClass.myclasschatroomaccess = 1;
        GlobalClass.chatroomaccess = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();

        RoomnoConnect();
        GlobalClass.myclasschatroomaccess = 0;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("------onResume------","onResume");

        //이미지를 가져올때, 유저를 등록할때 에서 이게 다시 실행된다. 이미지는 방정보가 없으면 전송을 할 수 없기때문에 초기화를 이미지 전송후 하도록 한다.
        Getroominfo(0,0); //방정보와 유저 정보를 가져온다.


//        if(Sendroommaketype.equals("2") || Sendroommaketype.equals("3")) {
//            //paging 수 보다 크면 페이징처리를 하고
//            if (Integer.parseInt(SendTchatcount) > limitnum) {
//                int offset = Integer.parseInt(SendTchatcount) - limitnum;
//                Getmyclassmsglist(limitnum, offset); //채팅 리스트 가져온다.
//            } else { //paging 수 보다 작으면 그 수에 맞춰서 보여주면 됨
//                Getmyclassmsglist(limitnum, 0); //채팅 리스트 가져온다.
//            }
//        }else{
//            progressBar.setVisibility(View.GONE);
//        }
    }


    //채팅방에 접속한것 체크하는 부분 : 룸 접속
    public void RoomConnect(){
        //채팅 전송 버튼

        try {
            oSocketSend.SendSocketData("CONNECTROOM", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("maxnum")), Sessionlist.get(1).get(0), "", "0","", roomtype, "0","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //채팅방에 접속한것 체크하는 부분 : 룸 미접속
    public void RoomnoConnect(){
        //채팅 전송 버튼

        try {
            oSocketSend.SendSocketData("NOCONNECTROOM", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("maxnum")), Sessionlist.get(1).get(0), "", "0","", roomtype, "0","");
        } catch (JSONException e) {
            e.printStackTrace();
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
                        oSocketSend.SendSocketData("CHATSEND", String.valueOf(ChatRoominfo.get(0).get("idx")), String.valueOf(ChatRoominfo.get(0).get("maxnum")), Sessionlist.get(1).get(0), Senddata, "0","", roomtype, "0","0");
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
                //Log.d("-------------채팅전송----------","wefwefwefwefwef");

                //맥스유저
                String maxnum = String.valueOf(jarray_chatdata.get(6)).substring(1);
                String maxnum_sub = maxnum.substring(0, maxnum.length() - 1);

                //안읽음 갯수
                // String noreadnum = String.valueOf(jarray_chatdata.get(7)).substring(1);
                // String noreadnum_sub = noreadnum.substring(0, noreadnum.length() - 1);

                //이미지 여부
                String imgchk = String.valueOf(jarray_chatdata.get(10)).substring(1);
                String imgchk_sub = imgchk.substring(0, imgchk.length() - 1);

                //안읽은 uid 리스트
                String noreaduid = String.valueOf(jarray_chatdata.get(11)).substring(1);
                String noreaduid_sub = noreaduid.substring(0, noreaduid.length() - 1);
                JSONArray noreaduidarray = new JSONArray(noreaduid_sub);
                // Log.d("------------noreaduidarray----------",String.valueOf(noreaduidarray));
                //Log.d("------------noreaduidarray----------",String.valueOf(noreaduidarray.get(0)));


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

                //채팅 고유값
                String chatidx = String.valueOf(jarray_chatdata.get(14)).substring(1);
                String chatidx_sub = chatidx.substring(0, chatidx.length() - 1);

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm(who, chatidx_sub, uid_sub, username_sub, msg_sub, regdate_sub, profileimg_sub, String.valueOf(noreaduidarray.length()), String.valueOf(noreaduidarray), imgchk_sub,"0","", false);
                Chatmessagelist.add(ChattingForm);

                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            } else if (commend_sub.equals("REXIT") || commend_sub.equals("INVITEROOM")) { //방나가기

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm("2", "", uid_sub, username_sub, msg_sub, regdate_sub, "", "", "", "0","0","", false);
                Chatmessagelist.add(ChattingForm);

                //방정보를 다시 가져옴 : 다른 사람이 나가기 버튼 누를때 판별하기 위함

                //나간 유저를 리스트에서 삭제해준다.
                for(int i = 0; i<JoinUserlist.size();i++) {
                    if (String.valueOf(JoinUserlist.get(i).get("uid")).equals(uid_sub)) {
                        Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i)));
                        Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i).get("uid")));
                        Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i).get("invitechk")));
                        JoinUserlist.remove(i);
                    }
                }

                usercount.setText(JoinUserlist.size()+"/"+String.valueOf(ChatRoominfo.get(0).get("maxnum")));


                MakeUserrecycle(JoinUserlist);

                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            } else if (commend_sub.equals("CROOM")) { //방 만들기
                //  String readnum = String.valueOf(jarray_chatdata.get(6)).substring(1);
                //  String readnum_sub = readnum.substring(0, readnum.length() - 1);

                //채팅 고유값
                String chatidx = String.valueOf(jarray_chatdata.get(14)).substring(1);
                String chatidx_sub = chatidx.substring(0, chatidx.length() - 1);

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm("2", chatidx_sub, uid_sub, username_sub, msg_sub, regdate_sub, "", "", "", "0", "0", "", false);
                Chatmessagelist.add(ChattingForm);

                //데이터를 바로 출력시킬것
                MakeRequestClassChatrecycle(Chatmessagelist);
            }else if(commend_sub.equals("EROOM")){ //초대된 유저 방에 입장


                //채팅 고유값
                String chatidx = String.valueOf(jarray_chatdata.get(14)).substring(1);
                String chatidx_sub = chatidx.substring(0, chatidx.length() - 1);
//
//                Log.d("------EROOM------",chatidx_sub);
//                Log.d("------EROOM------",uid_sub);
//                Log.d("------EROOM------",username_sub);
//                Log.d("------EROOM------",msg_sub);

                ChattingForm ChattingForm;
                ChattingForm = new ChattingForm("2", chatidx_sub, uid_sub, username_sub, msg_sub, regdate_sub, "", "", "", "0", "0", "", false);
                Chatmessagelist.add(ChattingForm);


                //입장한 유저의 invitechk를 2로 변경한다.
                for(int i = 0; i<JoinUserlist.size();i++){
                    if(String.valueOf(JoinUserlist.get(i).get("uid")).equals(uid_sub)){
                        Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i)));
                        Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i).get("uid")));
                        Log.d("------JoinUserlist------", String.valueOf(JoinUserlist.get(i).get("invitechk")));

                        JSONObject jobj = new JSONObject();
                        jobj.put("idx", JoinUserlist.get(i).get("idx"));
                        jobj.put("rid", JoinUserlist.get(i).get("rid"));
                        jobj.put("uid", JoinUserlist.get(i).get("uid"));
                        jobj.put("type", JoinUserlist.get(i).get("type"));
                        jobj.put("invitechk", "2");
                        jobj.put("regdate", JoinUserlist.get(i).get("regdate"));
                        jobj.put("usertype", JoinUserlist.get(i).get("usertype"));
                        jobj.put("email", JoinUserlist.get(i).get("email"));
                        jobj.put("name", JoinUserlist.get(i).get("name"));
                        jobj.put("nicname", JoinUserlist.get(i).get("nicname"));
                        jobj.put("imgidx", JoinUserlist.get(i).get("imgidx"));
                        jobj.put("profilebasicuri", JoinUserlist.get(i).get("profilebasicuri"));
                        jobj.put("profilesrc", JoinUserlist.get(i).get("profilesrc"));
                        jobj.put("profiletype", JoinUserlist.get(i).get("profiletype"));

                        JoinUserlist.set(i, jobj);
                    }
                }
                MakeUserrecycle(JoinUserlist);

            } else if (commend_sub.equals("CONNECTROOM")) { //방 접속 표시

                //안읽음 데이터를 수정해주는 부분이다.
                for (int i = 0; i < Chatmessagelist.size(); i++) {

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
                                .start(Myclassroomactivity.this);
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
                                Toast.makeText(Myclassroomactivity.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
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

    //이미지 업로드 한다.
    public void chatimgupload(){
        RestapiStart(); //레트로핏 빌드

        call = retrofitService.onlyimgupload_myclasschat(
                3, imguploadlist_multipart
        );
        RestapiResponse(); //응답
    }
}