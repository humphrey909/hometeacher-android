package com.example.hometeacher;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.ConferenceDocumentlistRecyclerAdapter;
import com.example.hometeacher.Adapter.ConferenceParticipantlistRecyclerAdapter;
import com.example.hometeacher.Adapter.ConferenceScreenRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.CamerachkForm;
import com.example.hometeacher.ArraylistForm.ConferenceDocumentForm;
import com.example.hometeacher.ArraylistForm.ImgFormMulti_2;
import com.example.hometeacher.ArraylistForm.MikechkForm;
import com.example.hometeacher.ArraylistForm.Pen;
import com.example.hometeacher.ArraylistForm.Pen_STEP;
import com.example.hometeacher.ArraylistForm.conferenceUserForm;
import com.example.hometeacher.shared.Session;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.google.gson.JsonArray;
import com.hbisoft.hbrecorder.HBRecorder;
import com.hbisoft.hbrecorder.HBRecorderCodecInfo;
import com.hbisoft.hbrecorder.HBRecorderListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.ImageType;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.hbisoft.hbrecorder.Constants.MAX_FILE_SIZE_REACHED_ERROR;
import static com.hbisoft.hbrecorder.Constants.SETTINGS_ERROR;

public class ConferenceRoomPage extends AppCompatActivity implements HBRecorderListener {

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    static Activity oActivity_conferenceroom;
    Session oSession; //자동로그인을 위한 db
    Context oContext;
    ArrayList<ArrayList<String>> Sessionlist;
    SocketSend oSocketSend;

    TextView titlename, recordtime;
    String myuid, usertype, roomidx, roomname;
    int Myrtcuid;
    ImageView camerabtn, fbcircle, mikebtn, spearkerbtn, menubtn, whiteboardbtn;
    RecyclerView screenRecyclerView, participantRecyclerView, documentRecyclerView;
    LinearLayout documentbox, totalcheckonofflinear;
    ImageView addbtn, editbtn;
    Button totalcheckonoffbtn;
   // ImageButton backbtn, backbtn_pop;
    DrawerLayout drawer;

    int PageMode = 0;
    int cameracount = 0; //내 카메라 on off 여부 1. 카메라 끄기 0. 카메라 켜기
    int mikecount = 0; //내 마이크 on off 여부 1. 마이크 끄기 0. 마이크 켜기
    int speakercount = 0; //내 스피커 on off 여부 1. 스피커 끄기 0. 스피커 켜기

    //getjoinuserlist_total 이 함수가 실행될때마다 변경해줄 예정이다.
    //remoted와 local에서 카메라 마이크 뮤트 언뮤트시 작동할때 커버변경하기 위한 변수이다.
    int videoremotecovertype = 0; //1. 영상보기 클릭시, 2. local change, 3. remote change
    int rtcuid_temporary = 0;
    boolean muted_temporary = true;


    // Java
    // Fill the App ID of your project generated on Agora Console.
    private String appId = "20dd029e85414fc598e663eba7377ddc";
    //private String appId = "4afe106fbbd94ff0b84da5d1ed58a317";
    // Fill the channel name.
    private String channelName = "hometeacher3";
    //private String channelName = "hometeacher";
    // Fill the temp token generated on Agora Console.
    private String token = "";
    //private String token = "0064afe106fbbd94ff0b84da5d1ed58a317IAAdHUtmZuzO5arsjGYqEZWplPwa2LHh8zm6YnboS7KZX+sowbkAAAAAEADZka5f2O/+YgEAAQDY7/5i";

    private RtcEngine mRtcEngine;

    FrameLayout container;
    FrameLayout container2;
    FrameLayout container3;
    FrameLayout container4;
    FrameLayout container5;

    //상대가 방에 입장하면 어떤방에 어떤 uid로 이방했는지를 체크해줘야한다.
    ArrayList<conferenceUserForm> roomtouserlist = new ArrayList<>(); //영상보기에서 사용하는 유저 리스트
    ArrayList<conferenceUserForm> totaltouserlist = new ArrayList<>(); //참여자 보기에서 사용하는 유저 리스트


    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int Threadstart = 0; //스레드 처음에만 실행시키기 위해 처음을 알기위한 변수
    int RecoderTimer = 0; // 스레드로 인해 변화되는 타이머 변수
    int RecoderComplete = 0; // 녹화 인증 완료여부
    Handler RecoderHandler; // 녹화 핸들러
    Thread Recoderthread; // 녹화 스레드

    //hbrecord-------
    //Permissions
    private static final int SCREEN_RECORD_REQUEST_CODE = 777;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = PERMISSION_REQ_ID_RECORD_AUDIO + 1;
    private boolean hasPermissions = false;

    //Declare HBRecorder
    private HBRecorder hbRecorder;

    ImageView recordimg;
    //Start/Stop Button
  //  private Button startbtn;

    //HD/SD quality
    //private RadioGroup radioGroup;

    //Should record/show audio/notification
    //private CheckBox recordAudioCheckBox;

    //Reference to checkboxes and radio buttons
    boolean wasHDSelected = true; //true HBRecorder to HD, false HBRecorder to SD
    boolean isAudioEnabled = true; //오디오 녹음 여부
    ArrayList<MultipartBody.Part> imguploadlist_multipart_vod = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요 - vod저장용
    //hbrecord-------

    //whitboard-------
    private DrawCanvas drawCanvas;

    ImageView fbPen, fbEraser, fbbackwardsgobtn, fbforwardgobtn, fbresetbtn;

    Pen Pen;
    private LinearLayout canvasLinear;       //캔버스 root view
    ArrayList<Pen> drawCommandList = new ArrayList<>();//그리기 경로가 기록된 리스트 _그리기위한 리스트
    ArrayList<Pen_STEP> drawCommandList_Total_STEP = new ArrayList<>();//그리기 경로가 기록된 리스트 _전체를 담는 리스트 - 문서별로 스탬 나눠주기.

    ArrayList<JSONObject> whiteboarddatajson = new ArrayList<JSONObject>(); //서버에 전송하기 위해 담는 데이터

    int WhiteboardFirstType; //화이트 보드만 접근이면 0, 새문서 등록 1 , 서버에서 접근 2

    int WhiteboardFirstNum = 0; //접근할때마다 +1
    //int WhiteboardFirstAccess = 0; //처음 접근 0, 두번이상 접근이면 1

    int beforetool_total; //전에 사용한 도구 _전체
    int beforecolor_total; //전에 사용한 색상 _전체

    //whitboard-------


    //document data-----------
    ArrayList<ConferenceDocumentForm> Documentdatalist = new ArrayList<>(); //문서 리스트
    int SelectDocumentCount = 0; //선택한 페이지
    String SelectDocumentIdx = ""; //선택한 문서의 고유번호
    int edittype = 0; // 0. 일반 1. : 편집모드 리스트 편집모드 변환시 사용
    int totalselect = 0; //전체 선택 or 해제 변수
    ArrayList<String> DeleteDocumentlist = new ArrayList<>(); //삭제할 문서 idx 데이터
    ArrayList<String> DeleteDocumentCountlist = new ArrayList<>(); //삭제할 문서의 순서 데이터

    //ProgressBar progressBar;
    //NestedScrollView nestedScrollView;
    int pagenum = 0; //페이징시 두번째 페이지 부터 불러온다.
    int limitnum = 10;

    float nextX = 0;
    float nextY = 0;
    //document data-----------

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기
    final static int SELECT_FILE = 3; //pdf 열기


    ArrayList<MultipartBody.Part> imguploadlist_multipart_document = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요 - 문서 이미지 저장용
    ArrayList<ImgFormMulti_2> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용

    ConferenceDocumentlistRecyclerAdapter oConferenceDocumentlistRecyclerAdapter;

    ArrayList<Integer> documentclicklist = new ArrayList<>(); //문서 클릭 리스트(최대 두개씩 업데이트함)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_room_page);


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기

        //hbrecord-------
        hbrecodinit();

        division();
        PageMode = 0;

        Startagraengin();

        changeView(PageMode);

        //핸들러 생성
        MakeHandler();
    }

    //agora 시작 엔진
    private void Startagraengin(){
        try {
            //상대방 상태 응답 받을때 사용하는 함수
            mRtcEngine = RtcEngine.create(oContext, appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        //**내 스크린 보여주기 **
        mRtcEngine.enableVideo();
    }

    // Java
    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

//    private boolean checkSelfPermission(String permission, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, permission) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
//            return false;
//        }
//        return true;
//    }



    //상대 카메라 보여주는 부분
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int rtcuid, int elapsed) { //상대방이 방에 입장시
            Log.d("alertval", "onUserJoined");
            Log.d("alertval", String.valueOf(rtcuid));
            Log.d("elapsed", String.valueOf(elapsed));
           // Toast.makeText(GlobalClass, String.valueOf(rtcuid), Toast.LENGTH_SHORT).show();


            //내가 입장한 것이 먼저 처리가 되고 그다음 상대방 것이 처리가 된다.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            checkuserinroom(0,0, rtcuid); //유저가 해당된 방에 존재 하는지 확인하는 부분

            Log.d("ontest",String.valueOf("uid"));
            if(PageMode == 0){ //영상보기
                //상대방이 방에 입장후 db에 저장 했으니 다른 디바이스에서는 서버에서 가져와서 확인후 영상에 띄운다.

            }else if(PageMode == 1){ //화이트보드 보기

            }else{ //참여자보기
                //서버에서 전체 유저 리스트를 불러온다.
                totaltouserlist.clear();
                getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴
            }
        }

        @Override
        //로컬 사용자 내가 지정된 채널에 참여할 때 발생합니다.
        public void onJoinChannelSuccess(String channel, int Rtcuid, int elapsed) {
            super.onJoinChannelSuccess(channel, Rtcuid, elapsed);
            Log.d("alertval", "onJoinChannelSuccess");
            Log.d("channel", String.valueOf(channel));
            Log.d("elapsed", String.valueOf(elapsed));


            Myrtcuid = Rtcuid;


            Log.d("----onJoinChannelSuccess----", String.valueOf(channel));
            Log.d("----onJoinChannelSuccess----", String.valueOf(Rtcuid));
            Log.d("----onJoinChannelSuccess----", String.valueOf(elapsed));

      //      Toast.makeText(GlobalClass, String.valueOf("내 스크린 저장 "), Toast.LENGTH_SHORT).show();
//            //내가 연결이 되면 해당 uid를 방idx, 이름으로 매칭을해서 리스트를 만든다.
//            conferenceUserForm oconferenceUserForm = new conferenceUserForm(myuid, roomidx, Rtcuid);
//            totaluserlist.add(oconferenceUserForm);
//
//            for (int i = 0;i<totaluserlist.size();i++){
//                Log.d("----totaluserlist----", String.valueOf(totaluserlist.get(i).getrtcuid()));
//                Log.d("----totaluserlist----", String.valueOf(totaluserlist.get(i).getuid()));
//                Log.d("----totaluserlist----", String.valueOf(totaluserlist.get(i).getroomidx()));
//            }

            //회의 참여자 저장 :
            joinuserinconference(Rtcuid);
        }
        @Override
        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onRejoinChannelSuccess(channel, uid, elapsed);

            Log.d("alertval", "onRejoinChannelSuccess");

        }


        @Override
        public void onLeaveChannel(RtcStats stats) {// 내가 채널을 떠날 때 발생합니다.
            super.onLeaveChannel(stats);

            Log.d("alertval", "onLeaveChannel");

            deleteuserinconference();
        }



        @Override
        public void onUserOffline(int rtcuid, int reason) { //상대방이 방에서 나갔을시
            super.onUserOffline(rtcuid, reason);
            //Toast.makeText(GlobalClass, String.valueOf(rtcuid)+"님 께서 퇴장하셨습니다. ", Toast.LENGTH_SHORT).show();
            Log.d("alertval", "onUserOffline");

            rtcuid_temporary = rtcuid;

            if(PageMode == 0){ //영상보기
                //Log.d("ontest",String.valueOf("uid0"));

                //상대 frame view 비우는 작업을 한다.
                getoutremote_cover(rtcuid_temporary);

            }else if(PageMode == 1){ //화이트보드 보기
               // Log.d("ontest",String.valueOf("uid1"));
            }else{ //참여자보기
                totaltouserlist.clear();
                getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴
            }
        }

        @Override
        //원격 사용자가 비디오 스트림 전송을 중지/다시 시작할 때 발생합니다.
        public void onUserMuteVideo(int rtcuid, boolean muted) {
            super.onUserMuteVideo(rtcuid, muted);
            Log.d("alertval", "onUserMuteVideo");
            Log.d("alertval", String.valueOf(muted)); //true 카메라 끔, false 카메라 켬
            Log.d("alertval", String.valueOf(rtcuid)); //true 카메라 끔, false 카메라 켬

            String existuid = "0";
            for(int i = 0; i<camerachkdata.size();i++){
                if(camerachkdata.get(i).getrtcuid() == rtcuid){ //값이 이미 존재한다면 저장하지 않는다.
                    camerachkdata.get(i).setmuted(muted);
                    existuid = "1";
                }
            }

            if(existuid.equals("0")){//존재하지 않으면
                CamerachkForm oCamerachkForm = new CamerachkForm(rtcuid, muted);
                camerachkdata.add(oCamerachkForm);
            }

            //확인용
//            for(int i = 0; i<camerachkdata.size();i++){
//                if(camerachkdata.get(i).getrtcuid() == rtcuid){ //값이 이미 존재한다면 저장하지 않는다.
//                    Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(i).getrtcuid()));
//                    Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(i).getmuted()));
//
//                }
//            }


            videoremotecovertype = 3; //1. 영상보기 클릭시, 2. local change, 3. remote change

            totaltouserlist.clear();
            getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴

            //임시로 원격 rtcuid와 mute 저장
            rtcuid_temporary = rtcuid;
            muted_temporary = muted;
            
        }



        @Override
        //원격 사용자가 오디오 스트림 전송을 중지/다시 시작할 때 발생합니다.
        public void onUserMuteAudio(int uid, boolean muted) {
            super.onUserMuteAudio(uid, muted);
            Log.d("alertval", "onUserMuteAudio");

            //여기서 마이크 설정!!!
            String existuid = "0";
            for(int i = 0; i<mikechkdata.size();i++){
                if(mikechkdata.get(i).getrtcuid() == uid){ //값이 이미 존재한다면 저장하지 않는다.
                    mikechkdata.get(i).setmuted(muted);
                    existuid = "1";
                }
            }

            if(existuid.equals("0")){//존재하지 않으면
                MikechkForm oMikechkForm = new MikechkForm(uid, muted);
                mikechkdata.add(oMikechkForm);
            }

            //확인용
//            for(int i = 0; i<mikechkdata.size();i++){
//                if(mikechkdata.get(i).getrtcuid() == uid){ //값이 이미 존재한다면 저장하지 않는다.
//                    Log.d("alertval mikechkdata", String.valueOf(mikechkdata.get(i).getrtcuid()));
//                    Log.d("alertval mikechkdata", String.valueOf(mikechkdata.get(i).getmuted()));
//
//                }
//            }

            totaltouserlist.clear();
            getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴


        }

        @Override
        public void onUserEnableVideo(int uid, boolean enabled) {
            super.onUserEnableVideo(uid, enabled);
            Log.d("alertval", "onUserEnableVideo");

        }


        //상대방에 의해서 내 카메라 상태가 변경될 때 실행
        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);

            Log.d("alertval", "onRemoteVideoStateChanged");
            Log.d("onRemoteVideoStateChanged",String.valueOf(uid));
        }
    };
    

    //원격 카메라 마이크 변경시 커버 변경해주는 함수
   public void remotecamera_cover(int rtcuid, boolean muted){
        for (int i = 0; i<roomtouserlist.size();i++) {
           // Log.d("----onUserOffline----", String.valueOf(roomtouserlist.get(i).getrtcuid()));
           // Log.d("----onUserOffline----", String.valueOf(rtcuid));

            //카메라 on off 체크한 유저의 화면만 변경한다.
            if (String.valueOf(roomtouserlist.get(i).getrtcuid()).equals(String.valueOf(rtcuid))) {
                int screenlocation = roomtouserlist.get(i).getscreenlocation();

                //각 위치에 영상 보여주기
                if (screenlocation == 1) {
                    if(muted){
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container.addView(iv);
                    }else{
                        setupRemoteVideo1(Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid())));
                    }
                } else if (screenlocation == 2) {
                    if(muted){
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container2.addView(iv);
                    }else{
                        setupRemoteVideo2(Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid())));
                    }
                } else if (screenlocation == 3) {
                    if(muted){
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container3.addView(iv);
                    }else{
                        setupRemoteVideo3(Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid())));
                    }
                } else if (screenlocation == 4) {
                    if(muted){
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container4.addView(iv);
                    }else{
                        setupRemoteVideo4(Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid())));
                    }
                } else if (screenlocation == 5) {
                    if(muted){
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container5.addView(iv);
                    }else{
                        setupRemoteVideo5(Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid())));
                    }
                }
            }
        }
    }

    //상대방이 나갔을때 커버를 변경하는 함수이다.
    public void getoutremote_cover(int rtcuid){
        for (int i = 0; i<roomtouserlist.size();i++){
            Log.d("----onUserOffline----", String.valueOf(roomtouserlist.get(i).getrtcuid()));
            Log.d("----onUserOffline----", String.valueOf(rtcuid));

            //나간 유저 체크
            if(String.valueOf(roomtouserlist.get(i).getrtcuid()).equals(String.valueOf(rtcuid))){
                //Toast.makeText(GlobalClass, roomtouserlist.get(i).getname()+"님 께서 퇴장하셨습니다. ", Toast.LENGTH_SHORT).show();
                Log.d("----outuserchk----", String.valueOf(roomtouserlist.get(i).getrtcuid()));
                Log.d("----outuserchk----", String.valueOf(rtcuid));

                //삭제하기 전에 영상부터 꺼주자 - !! 상대방 나갔을때 안보이게 처리하는것 하기!!!
                int screenlocation = roomtouserlist.get(i).getscreenlocation();

                //각 위치에 영상 보여주기
                if(screenlocation == 1){
                    container.removeAllViews();
                }else if(screenlocation == 2){
                    container2.removeAllViews();
                }else if(screenlocation == 3){
                    container3.removeAllViews(); //잘 되는데 지우고 이미지를 넣고싶어.
                }else if(screenlocation == 4){
                    container4.removeAllViews(); //잘 되는데 지우고 이미지를 넣고싶어.
                }else if(screenlocation == 5){
                    container5.removeAllViews(); //잘 되는데 지우고 이미지를 넣고싶어.
                }

                //roomtouserlist.get(i).setlifemod(0); //살아있는 여부를 0으로 체크
                roomtouserlist.remove(roomtouserlist.get(i));


            }
        }


    }

    //리스트들을 다시 불러온다.
    /*
    public void Resetlist(){
        //Toast.makeText(GlobalClass, String.valueOf(PageMode), Toast.LENGTH_SHORT).show();
        //상대방이 나갓을때 체크해줄것.
        if(PageMode == 0){ //영상보기
            //Makejoinuserscreenrecycle(roomtouserlist);
        }else if(PageMode == 1){ //화이트보드 보기

        }else{ //참여자보기
            //서버에서 전체 유저 리스트를 불러온다.
            totaltouserlist.clear();
            getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴


            Log.d("ontest",String.valueOf("uid"));
        }
    }*/


    //내 카메라 회의 참여
    private void initializeAndJoinChannel() {
//        try {
//            //상대방 상태 응답 받을때 사용하는 함수
//            mRtcEngine = RtcEngine.create(oContext, appId, mRtcEventHandler);
//        } catch (Exception e) {
//            throw new RuntimeException("Check the error.");
//        }
//
//        // By default, video is disabled, and you need to call enableVideo to start a video stream.
//        //**내 스크린 보여주기 **
//        mRtcEngine.enableVideo();

        if(PageMode == 0) { //영상보기
            container = findViewById(R.id.local_video_view_container); //선생자리
            container2 = findViewById(R.id.local_video_view_container2); //학생 자리
            container3 = findViewById(R.id.local_video_view_container3); //학생 자리
            container4 = findViewById(R.id.local_video_view_container4); //학생 자리
            container5 = findViewById(R.id.local_video_view_container5); //학생 자리

            if(cameracount == 0){ //내 카메라 켠 경우
               // SurfaceView surfaceView;
                if(usertype.equals("1")){ //선생
                    // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
                   // surfaceView = RtcEngine.CreateRendererView(oContext);
                   // container.addView(surfaceView);

                    setupLocalVideo1(Myrtcuid);
                }else{ //학생
                    // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
                   // surfaceView = RtcEngine.CreateRendererView(oContext);
                  //  container2.addView(surfaceView);

                    setupLocalVideo2(Myrtcuid);
                }

                // Pass the SurfaceView object to Agora so that it renders the local video.
                //mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, Myrtcuid));
            }else{ //내 카메라 끈 경우
                if(usertype.equals("1")){ //선생
                    ImageView iv = new ImageView(oContext);
                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                    container.addView(iv);
                }else{ //학생
                    ImageView iv = new ImageView(oContext);
                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                    container2.addView(iv);
                }
            }
        }



        Log.d("Myrtcuid", String.valueOf(Myrtcuid));

        // Join the channel with a token.
        mRtcEngine.joinChannel(token, channelName, "", 0);



        //영상 유저 리스트에 값을 다시 불러온다. 입장 후 리셋했을 경우 동작한다.
        Log.d("roomtouserlist", String.valueOf(roomtouserlist.size()));
        for (int j = 0; j < roomtouserlist.size(); j++) { //나빼고 참여자 리스트

            //Log.d("roomtouserlist", String.valueOf(roomtouserlist.get(j).getuid()));
            //Log.d("roomtouserlist", String.valueOf(roomtouserlist.get(j).getrtcuid()));
            //Log.d("roomtouserlist", String.valueOf(roomtouserlist.get(j).getscreenlocation()));


            //Log.d("roomtouserlist_getcamerachk", String.valueOf(roomtouserlist.get(j).getcamerachk()));

            int screenlocation = roomtouserlist.get(j).getscreenlocation();
            int rtcuid = roomtouserlist.get(j).getrtcuid();
            String uid = roomtouserlist.get(j).getuid();


            //내것을 제외한 나머지만 여기서 리셋함
            //다시 불러올때 카메라 켠 여부도 체크함.
            if(!uid.equals(myuid)){
                //각 위치에 영상 보여주기
                if(screenlocation == 1){
                    if(!roomtouserlist.get(j).getcamerachk()){ //끄기
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container.addView(iv);
                    }else{ //켜기
                        setupRemoteVideo1(Integer.parseInt(String.valueOf(rtcuid)));
                    }
                }else if(screenlocation == 2){
                    if(!roomtouserlist.get(j).getcamerachk()){ //끄기
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container2.addView(iv);
                    }else { //켜기
                        setupRemoteVideo2(Integer.parseInt(String.valueOf(rtcuid)));
                    }
                }else if(screenlocation == 3){
                    if(!roomtouserlist.get(j).getcamerachk()){ //끄기
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container3.addView(iv);
                    }else { //켜기
                        setupRemoteVideo3(Integer.parseInt(String.valueOf(rtcuid)));
                    }
                }else if(screenlocation == 4){
                    if(!roomtouserlist.get(j).getcamerachk()){ //끄기
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container4.addView(iv);
                    }else { //켜기
                        setupRemoteVideo4(Integer.parseInt(String.valueOf(rtcuid)));
                    }
                }else if(screenlocation == 5){
                    if(!roomtouserlist.get(j).getcamerachk()){ //끄기
                        ImageView iv = new ImageView(oContext);
                        iv.setImageResource(R.drawable.close4); // 이미지 리소스
                        container5.addView(iv);
                    }else { //켜기
                        setupRemoteVideo5(Integer.parseInt(String.valueOf(rtcuid)));
                    }
                }
            }

        }

//        ImageView iv = new ImageView(oContext);
//        iv.setImageResource(R.drawable.circle); // 이미지 리소스
//        container5.addView(iv);


       // mRtcEngine.enableLocalVideo(false); //자기 화면을 상대 나 둘다 뮤트시킴
        //mRtcEngine.muteLocalAudioStream(false);
       // mRtcEngine.muteLocalVideoStream(true); //나는 보이지만 상대방화면에서 뮤트 시킴

        //방장이 상대방 오디오 비디오 제어할때 사용
        //mRtcEngine.muteRemoteAudioStream(uid, true);
        //mRtcEngine.muteRemoteVideoStream(uid, true);

        //스피커 소리 제어
        //mRtcEngine.setEnableSpeakerphone(true);

        //스피커가 활성화 되어있는지 확인
       // mRtcEngine.isSpeakerphoneEnabled();


        //!!
        //int volume = 50;
        //mRtcEngine.adjustPlaybackSignalVolume(volume); //자기 오디오 볼륨 설정




        //mRtcEngine.adjustUserPlaybackSignalVolume(uid, volume); //상대 오디오 볼륨 설정


//        // Enables in-ear monitoring.
//        mRtcEngine.enableInEarMonitoring(true);
//        int volume = 50;
//// Sets the in-ear monitoring volume.
//        mRtcEngine.setInEarMonitoringVolume(volume);
    }

//   // Java **상대 스크린 보여주기 **
    private void setupRemoteVideo1(int rtcuid) {

        container = findViewById(R.id.local_video_view_container); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));

    }
    private void setupRemoteVideo2(int rtcuid) {

        container2 = findViewById(R.id.local_video_view_container2); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container2.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }
    private void setupRemoteVideo3(int rtcuid) {
        container3 = findViewById(R.id.local_video_view_container3); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container3.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }
    private void setupRemoteVideo4(int rtcuid) {
        container4 = findViewById(R.id.local_video_view_container4); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container4.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }
    private void setupRemoteVideo5(int rtcuid) {
        container5 = findViewById(R.id.local_video_view_container5); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container5.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }

    private void setupLocalVideo1(int rtcuid) {

        container = findViewById(R.id.local_video_view_container); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));

    }
    private void setupLocalVideo2(int rtcuid) {

        container2 = findViewById(R.id.local_video_view_container2); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container2.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }
    private void setupLocalVideo3(int rtcuid) {
        container3 = findViewById(R.id.local_video_view_container3); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container3.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }
    private void setupLocalVideo4(int rtcuid) {
        container4 = findViewById(R.id.local_video_view_container4); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container4.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }
    private void setupLocalVideo5(int rtcuid) {
        container5 = findViewById(R.id.local_video_view_container5); //선생자리
        SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
        surfaceView.setZOrderMediaOverlay(true);
        container5.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));
    }

    // Java
    protected void onDestroy() {
        super.onDestroy();

        //해당 내 유저도 삭제할 것
        deleteuserinconference();

        //나간 사람의 좌표를 전부 널처리한다. 상대방 디바이스에서
        outroomorder();

        //선생님인지 확인 후
        if(Sessionlist.get(1).get(2).equals("1")){ //선생
            teacherroomout();
        }

        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();

        //디바이스 캐시 삭제하기
        clearCache();
    }


    public void division() {
        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언
        oSocketSend = new SocketSend(GlobalClass);

        //oActivity = this;
        oContext = this;
        oSession = new Session(oContext);
        Sessionlist = oSession.Getoneinfo("0");
        oActivity_conferenceroom = this;

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        myuid = intent.getExtras().getString("uid"); //유저 idx
        usertype = intent.getExtras().getString("usertype"); //유저 type
        roomidx = intent.getExtras().getString("roomidx"); //방 고유번호
        roomname = intent.getExtras().getString("roomname"); //방 이름
        Log.d("-------------myuid------------",myuid);
        Log.d("-------------usertype------------",usertype);
        Log.d("-------------roomidx------------",roomidx);
        Log.d("-------------roomname------------",roomname);

        recordtime = (TextView) findViewById(R.id.recordtime);

        camerabtn = (ImageView) findViewById(R.id.camerabtn);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cameracount==0){ //카메라 켜기
                    cameracount = 1;
                    camerabtn.setImageResource(R.drawable.cameraside_off);

                   // mRtcEngine.disableVideo();
                    mRtcEngine.enableLocalVideo(false); //자기 화면을 상대 나 둘다 뮤트시킴
                    mRtcEngine.muteLocalVideoStream(true); //나는 보이지만 상대방화면에서 뮤트 시킴


                    String existuid = "0";
                    for(int i = 0; i<camerachkdata.size();i++){
                        if(camerachkdata.get(i).getrtcuid() == Myrtcuid){ //값이 이미 존재한다면 저장하지 않는다.
                            camerachkdata.get(i).setmuted(true);
                            existuid = "1";
                        }
                    }

                    if(existuid.equals("0")){//존재하지 않으면
                        CamerachkForm oCamerachkForm = new CamerachkForm(Myrtcuid, true);
                        camerachkdata.add(oCamerachkForm);
                    }
                }else{ //카메라 끄기
                    cameracount = 0;
                    camerabtn.setImageResource(R.drawable.cameraside_on);

                   // mRtcEngine.enableVideo();
                    mRtcEngine.enableLocalVideo(true); //자기 화면을 상대 나 둘다 뮤트시킴
                    mRtcEngine.muteLocalVideoStream(false); //나는 보이지만 상대방화면에서 뮤트 시킴



                    String existuid = "0";
                    for(int i = 0; i<camerachkdata.size();i++){
                        if(camerachkdata.get(i).getrtcuid() == Myrtcuid){ //값이 이미 존재한다면 저장하지 않는다.
                            camerachkdata.get(i).setmuted(false);
                            existuid = "1";
                        }
                    }

                    if(existuid.equals("0")){//존재하지 않으면
                        CamerachkForm oCamerachkForm = new CamerachkForm(Myrtcuid, false);
                        camerachkdata.add(oCamerachkForm);
                    }
                }
                videoremotecovertype = 2; //1. 영상보기 클릭시, 2. local change, 3. remote change

                totaltouserlist.clear();
                getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴

                //cameracount 1. 카메라 끄기 0. 카메라 켜기
                Log.d("cameracount***", String.valueOf(cameracount));

                //영상보기 일때만 내 화면을 변경한다.
                if(PageMode == 0) { //영상보기
                    //내 카메라를 제어한다.
                    for (int j = 0; j < roomtouserlist.size(); j++) { //영상보기 리스트

                        Log.d("roomtouserlist", String.valueOf(roomtouserlist.get(j).getuid()));
                        Log.d("roomtouserlist", String.valueOf(roomtouserlist.get(j).getrtcuid()));
                        Log.d("roomtouserlist", String.valueOf(roomtouserlist.get(j).getscreenlocation()));

                        int screenlocation = roomtouserlist.get(j).getscreenlocation();
                        int rtcuid = roomtouserlist.get(j).getrtcuid();
                        String uid = roomtouserlist.get(j).getuid();

                        //내 id일때만 동작
                        if (uid.equals(myuid)) {
                            //각 위치에 영상 보여주기
                            if (screenlocation == 1) {

                                if (cameracount == 1) { //끄기
                                    ImageView iv = new ImageView(oContext);
                                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                                    container.addView(iv);
                                } else { //켜기
                                    setupLocalVideo1(Integer.parseInt(String.valueOf(rtcuid)));
                                }


                            } else if (screenlocation == 2) {
                                if (cameracount == 1) { //끄기
                                    ImageView iv = new ImageView(oContext);
                                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                                    container2.addView(iv);
                                } else { //켜기
                                    setupLocalVideo2(Integer.parseInt(String.valueOf(rtcuid)));
                                }

                            } else if (screenlocation == 3) {
                                if (cameracount == 1) { //끄기
                                    ImageView iv = new ImageView(oContext);
                                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                                    container3.addView(iv);
                                } else { //켜기
                                    setupLocalVideo3(Integer.parseInt(String.valueOf(rtcuid)));
                                }

                            } else if (screenlocation == 4) {
                                if (cameracount == 1) { //끄기
                                    ImageView iv = new ImageView(oContext);
                                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                                    container4.addView(iv);
                                } else { //켜기
                                    setupLocalVideo4(Integer.parseInt(String.valueOf(rtcuid)));
                                }

                            } else if (screenlocation == 5) {
                                if (cameracount == 1) { //끄기
                                    ImageView iv = new ImageView(oContext);
                                    iv.setImageResource(R.drawable.close4); // 이미지 리소스
                                    container5.addView(iv);
                                } else { //켜기
                                    setupLocalVideo5(Integer.parseInt(String.valueOf(rtcuid)));
                                }

                            }
                        }
                    }
                }

            }
        });

        mikebtn = (ImageView) findViewById(R.id.mikebtn);
        mikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mikecount == 0){
                    mikecount = 1;
                    mikebtn.setImageResource(R.drawable.mike_off);

                    mRtcEngine.enableLocalAudio(false);
                    mRtcEngine.muteLocalAudioStream(true);


                    String existuid = "0";
                    for(int i = 0; i<mikechkdata.size();i++){
                        if(mikechkdata.get(i).getrtcuid() == Myrtcuid){ //값이 이미 존재한다면 저장하지 않는다.
                            mikechkdata.get(i).setmuted(true);
                            existuid = "1";
                        }
                    }

                    if(existuid.equals("0")){//존재하지 않으면
                        MikechkForm oMikechkForm = new MikechkForm(Myrtcuid, true);
                        mikechkdata.add(oMikechkForm);
                    }

                }else{
                    mikecount = 0;
                    mikebtn.setImageResource(R.drawable.mike_on);

                    mRtcEngine.enableLocalAudio(true);
                    mRtcEngine.muteLocalAudioStream(false);


                    String existuid = "0";
                    for(int i = 0; i<mikechkdata.size();i++){
                        if(mikechkdata.get(i).getrtcuid() == Myrtcuid){ //값이 이미 존재한다면 저장하지 않는다.
                            mikechkdata.get(i).setmuted(false);
                            existuid = "1";
                        }
                    }

                    if(existuid.equals("0")){//존재하지 않으면
                        MikechkForm oMikechkForm = new MikechkForm(Myrtcuid, false);
                        mikechkdata.add(oMikechkForm);
                    }
                }

                totaltouserlist.clear();
                getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴

            }
        });

        spearkerbtn = (ImageView) findViewById(R.id.spearkerbtn);
        spearkerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speakercount==0){
                    speakercount = 1;
                    spearkerbtn.setImageResource(R.drawable.speaker_off);

                    //스피커 소리 제어
                    mRtcEngine.setEnableSpeakerphone(false); //이어피스로 사용
                }else{
                    speakercount = 0;
                    spearkerbtn.setImageResource(R.drawable.speaker_on);

                    //스피커 소리 제어
                    mRtcEngine.setEnableSpeakerphone(true); //스피커폰으로 사용
                }


            }
        });

        menubtn = (ImageView) findViewById(R.id.menubtn);
        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                PopupMenu poup = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
                }
                else {
                    poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
                }

                getMenuInflater().inflate(R.menu.conferencetransmenu, poup.getMenu());
                poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getApplicationContext(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                        String popup_tittle = item.getTitle().toString();
                        //TODO ==== [메뉴 선택 동작 처리] ====
                        // if(popup_tittle.contains("초대 취소")){

                        //  }
                        if(popup_tittle.contains("영상보기")){
                            Log.d("","영상보기");
                            PageMode = 0;
                            changeView(PageMode);

                        }else if(popup_tittle.contains("화이트보드")){
                            PageMode = 1;
                            changeView(PageMode);

                            Log.d("","화이트보드");
                        }else if(popup_tittle.contains("참여자보기")){
                            PageMode = 2;
                            changeView(PageMode);

                            Log.d("","참여자보기");
                        }
                        return false;
                    }
                });
                poup.show(); // 메뉴를 띄우기
            }
        });

        //녹화버튼
        recordimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    //first check if permissions was granted
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)) {
                        hasPermissions = true;
                    }
                    if (hasPermissions) {
                        //check if recording is in progress
                        //and stop it if it is
                        if (hbRecorder.isBusyRecording()) {

                            hbRecorder.stopScreenRecording();

                            recordimg.setImageResource(R.drawable.recordoff);

                            //타이머 종료
                            RecoderComplete = 1;
                        }
                        //else start recording
                        else {

                            startRecordingScreen();

                        }
                    }
                } else {
                    showLongToast("This library requires API 21>");
                }
            }
        });
    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.conferencemenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.closeroom: //방 닫기


                //1. 나혼자 나갈건지
                //2. 모두 나가게 할 것인지.
                Log.d("","--------closeroom---------");
                //다이어그램 띄우기
                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                builder.setTitle("나가기").setMessage("수업을 종료하시겠습니까?");

                //삭제 실시onUserOffline
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //녹화기능 자동 중지 시키기
                        if (hbRecorder.isBusyRecording()) { //녹화 켜져있는 상태
                            //녹화중입니다. 녹화 종료 후 나가주세요.
                            Toast.makeText(GlobalClass, "녹화중입니다. 녹화 종료 후 나가주시기 바랍니다. ", Toast.LENGTH_SHORT).show();
                        }else{
                            deleteuserinconference();//유저 삭제하기
                            outroomorder();//나간 사람의 좌표를 전부 널처리한다. 상대방 디바이스에서
                            finish();
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //뒤로가기
        Log.d("onBackPressed","onBackPressed");
        //다이어그램 띄우기
        AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

        builder.setTitle("나가기").setMessage("수업을 종료하시겠습니까?");

        //삭제 실시
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                //녹화기능 자동 중지 시키기
                if (hbRecorder.isBusyRecording()) { //녹화 켜져있는 상태
                    //녹화중입니다. 녹화 종료 후 나가주세요.
                    Toast.makeText(GlobalClass, "녹화중입니다. 녹화 종료 후 나가주시기 바랍니다. ", Toast.LENGTH_SHORT).show();
                }else{
                    deleteuserinconference(); //유저 삭제하기
                    outroomorder();//나간 사람의 좌표를 전부 널처리한다. 상대방 디바이스에서
                    finish();
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


    /**
     * usetoolmode = 8로 전송.
     * 해당 uid를 갖은 좌표는 전부 수정할 수 없도록 null처리를 한다.
     * **/
    //방을 나갔을때 좌표, 전체리스트는 닉네임을 널로, restpenlist는 해당 id 삭제,
    public void outroomorder(){

        //drawCommandList_Total_STEP는 전체의 리스트 정보를 담고 있다.
        //그냥 상대방한테 이놈에 대한 데이터의 id값을 전부 0으로 변경하라고 지시하면?
        drawCanvas.usetoolmode = 8;
        oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", "", String.valueOf(drawCanvas.usetoolmode), String.valueOf(0), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));

    }

    /**
     * usetoolmode = 9로 전송.
     * 해당 uid를 갖은 좌표는 전부 수정할 수 없도록 null처리를 한다.
     * **/
    //선생님이 나가면 학생들 전부 퇴장 시키기
    public void teacherroomout(){

        //drawCommandList_Total_STEP는 전체의 리스트 정보를 담고 있다.
        //그냥 상대방한테 이놈에 대한 데이터의 id값을 전부 0으로 변경하라고 지시하면?
        drawCanvas.usetoolmode = 9;
        oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", "", String.valueOf(drawCanvas.usetoolmode), String.valueOf(0), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));

    }


    //참여자 유저 리스트 카메라를 보여준다.
/*
    public void Makejoinuserscreenrecycle(ArrayList<conferenceUserForm> roomtouserlist) {
        Log.d("Makejoinuserscreenrecycle", String.valueOf(roomtouserlist));

        //참여자 리스트를 리사이클러뷰로 생성
        //screenRecyclerView = (RecyclerView) findViewById(R.id.screenRecyclerView);
        GridLayoutManager GridlayoutManager = new GridLayoutManager(oContext, 2); //그리드 매니저 선언
        ConferenceScreenRecyclerAdapter oConferenceScreenRecyclerAdapter = new ConferenceScreenRecyclerAdapter(getApplicationContext(), oActivity_conferenceroom); //내가만든 어댑터 선언
        screenRecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        //선택된 유저를 보낼 것. AddUserlist
        oConferenceScreenRecyclerAdapter.setdata(mRtcEngine);
        oConferenceScreenRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oConferenceScreenRecyclerAdapter.setRecycleList(roomtouserlist); //arraylist 연결
        screenRecyclerView.setAdapter(oConferenceScreenRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

    }*/


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

                    if (urlget2.equals("1")) { //회의 참가하는 유저 정보를 저장한다
                        Log.d("onResponse ? ", "회의 참가하는 유저 정보를 저장한다 " + resultlist);

                        JSONObject jobj = null;
                        //JSONArray jarray = null;
                        try {
                            jobj = new JSONObject(resultlist);
                           // Log.d("onResponse ? ","userdata  : " + String.valueOf(jobj.get("userdata")));


                            JSONArray jarray = new JSONArray(String.valueOf(jobj.get("userdata")));
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject jobj_ = new JSONObject(String.valueOf(jarray.get(i)));
                                //Log.d("onResponse ? ", "jobj_ : " + String.valueOf(jobj_));

                                //내정보를 저장하고 가져오면 roomtouserlist에 저장한다. 순서는 선생이면 1 학생이면 roomtouserlist에서 그다음 큰수 입력

                                int screenlocation = 0;
                                //유저 타입 설정
                                if(String.valueOf(jobj_.get("usertype")).equals("1")){ //선생
                                    screenlocation = 1; //선생이면 1번 자리
                                }else{ //학생

                                    //현재 디바이스에 입장한 참가자

                                    if(roomtouserlist.size() == 0){ //처음 입장하고 학생이면 2번으로 고정
                                        screenlocation = 2;
                                    }else{ //한명 이상 입장한 상태라면.

                                        ArrayList<Integer> restlocation_student = new ArrayList<>(); //남는 위치 찾는 변수
                                        restlocation_student.add(2);
                                        restlocation_student.add(3);
                                        restlocation_student.add(4);
                                        restlocation_student.add(5);

                                        //영상 전체 유저 리스트
                                        for(int uc = 0; uc<roomtouserlist.size();uc++){
                                            //학생만 뽑아서
                                            if(roomtouserlist.get(uc).getusertype().equals("2")){

                                                //해당 객체를 삭제한다.
                                                restlocation_student.remove(new Integer(roomtouserlist.get(uc).getscreenlocation()));
                                            }
                                        }
                                        //가장 작은 수
                                        screenlocation = Collections.min(restlocation_student);
                                    }
                                }

                                String profileimg = String.valueOf(jobj_.get("basicuri"))+String.valueOf(jobj_.get("src"));
                                conferenceUserForm oconferenceUserForm = new conferenceUserForm(String.valueOf(jobj_.get("uid")), String.valueOf(jobj_.get("rid")), Integer.parseInt(String.valueOf(jobj_.get("rtcuid"))), 1, String.valueOf(jobj_.get("name")), String.valueOf(jobj_.get("usertype")), profileimg, true, true, screenlocation);
                                roomtouserlist.add(oconferenceUserForm);

                                Log.d("onResponse ? ", "-screenlocation- : " + String.valueOf(screenlocation));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //test
//                        Log.d("onResponse ? ", "-roomtouserlist- : " + String.valueOf(roomtouserlist.size()));
//                        for(int j = 0;j<roomtouserlist.size();j++){
//                            //가져온 uid와 가지고 있던 uid가 중복된다면 삭제해준다.
//                            //if(String.valueOf(jobj.get("uid")).equals(roomtouserlist.get(j).getuid())){
//                               // roomtouserlist.remove(roomtouserlist.get(j));
//                            //}
//
//                            Log.d("onResponse ? ", "-roomtouserlist- : " + String.valueOf(roomtouserlist.get(j).getuid()));
//                            Log.d("onResponse ? ", "-roomtouserlist- : " + String.valueOf(roomtouserlist.get(j).getscreenlocation()));
//                        }


                    }else if(urlget2.equals("2")){ //유저가 같은 방에 존재하는지 확인하는 함수 -> 체크후 추가 또는 삭제
                        Log.d("onResponse ? ", "유저가 같은 방에 존재하는지 확인 " + resultlist);

                        //Toast.makeText(GlobalClass, String.valueOf("유저가 같은 방에 존재하는지 확인"), Toast.LENGTH_SHORT).show();

                        //값이 있으면 존재 하는 거니 유저를 추가, 값이 없다면 유저가 방에 포함되지  않으니 유저 추가나 다른 반응이 없어도 됨
                        if(resultlist.length() > 2){ //값이 있을때만 추가한다. 값이 없으면 아무일도 일어나지 않음.

                            JSONArray jarray = null;
                            try {
                                jarray = new JSONArray(resultlist);

                                for (int i = 0; i < jarray.length(); i++) {
                                    Log.d("onResponse ? ", "유저가 같은 방에 존재하는지 확인 : " + String.valueOf(jarray.get(i)));

                                    JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                    Log.d("onResponse ? ", "-유저가 같은 방에 존재하는지 확인- : " + String.valueOf(jobj));
                                    Log.d("onResponse ? ", "-유저가 같은 방에 존재하는지 확인- : " + String.valueOf(jobj.get("uid")));
                                    Log.d("onResponse ? ", "-유저가 같은 방에 존재하는지 확인- : " + String.valueOf(jobj.get("rtcuid")));
                                    Log.d("onResponse ? ", "-유저가 같은 방에 존재하는지 확인- : " + String.valueOf(jobj.get("name")));

                                    //만약에 가져온값이 저장된 값이라면 삭제하고 저장한다.
                                    for(int j = 0;j<roomtouserlist.size();j++){
                                        //가져온 uid와 가지고 있던 uid가 중복된다면 삭제해준다.
                                        if(String.valueOf(jobj.get("uid")).equals(roomtouserlist.get(j).getuid())){
                                            roomtouserlist.remove(roomtouserlist.get(j));
                                        }
                                    }
                                    int screenlocation = 0;
                                    //유저 타입 설정
                                    if(String.valueOf(jobj.get("usertype")).equals("1")){ //선생
                                        screenlocation = 1; //선생이면 1번 자리
                                    }else{ //학생
                                        if(roomtouserlist.size() == 0){
                                            screenlocation = 2;
                                        }else{
                                            ArrayList<Integer> restlocation_student = new ArrayList<>(); //남는 위치 찾는 변수
                                            restlocation_student.add(2);
                                            restlocation_student.add(3);
                                            restlocation_student.add(4);
                                            restlocation_student.add(5);

                                            //영상 전체 유저 리스트
                                            for(int uc = 0; uc<roomtouserlist.size();uc++){
                                                //학생만 뽑아서
                                                if(roomtouserlist.get(uc).getusertype().equals("2")){

                                                    //해당 객체를 삭제한다.
                                                    restlocation_student.remove(new Integer(roomtouserlist.get(uc).getscreenlocation()));
                                                }
                                            }
                                            screenlocation = Collections.min(restlocation_student);
                                        }
                                    }

                                    //roomtouserlist 이 갯수를 계산해서 선생님이면 1
                                    //학생은 순서대로 2 3 4 5 이렇게 입력한다.
                                    //선생님도 넣을 것.

                                    String profileimg = String.valueOf(jobj.get("basicuri"))+String.valueOf(jobj.get("src"));
                                    conferenceUserForm oconferenceUserForm = new conferenceUserForm(String.valueOf(jobj.get("uid")), String.valueOf(jobj.get("rid")), Integer.parseInt(String.valueOf(jobj.get("rtcuid"))), 1, String.valueOf(jobj.get("name")), String.valueOf(jobj.get("usertype")), profileimg, true, true, screenlocation);
                                    roomtouserlist.add(oconferenceUserForm);

                                    Log.d("onResponse ? ", "-screenlocation- : " + String.valueOf(screenlocation));

                                    if(PageMode == 0) {
                                        //각 위치에 영상 보여주기
                                        if (screenlocation == 1) {
                                            setupRemoteVideo1(Integer.parseInt(String.valueOf(jobj.get("rtcuid"))));
                                        } else if (screenlocation == 2) {
                                            setupRemoteVideo2(Integer.parseInt(String.valueOf(jobj.get("rtcuid"))));
                                        } else if (screenlocation == 3) {
                                            setupRemoteVideo3(Integer.parseInt(String.valueOf(jobj.get("rtcuid"))));
                                        } else if (screenlocation == 4) {
                                            setupRemoteVideo4(Integer.parseInt(String.valueOf(jobj.get("rtcuid"))));
                                        } else if (screenlocation == 5) {
                                            setupRemoteVideo5(Integer.parseInt(String.valueOf(jobj.get("rtcuid"))));
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //리사이클러뷰로 만들기
                            Log.d("onResponse ? ", "-유저가 같은 방에 존재하는지 확인- : " + String.valueOf(roomtouserlist));
                            Log.d("onResponse ? ", "-유저가 같은 방에 존재하는지 확인- : " + String.valueOf(roomtouserlist.size()));

                        }
                    }else if(urlget2.equals("3")) { //회의 vod를 저장한다.
                        Log.d("onResponse ? ", "회의 vod를 저장한다.  " + resultlist);

                    }else if(urlget2.equals("4")){ //회의 참여자 리스트 - 전체
                        Log.d("onResponse ? ", "회의 참여자 리스트 - 전체 " + resultlist);

                        //Toast.makeText(GlobalClass, String.valueOf("회의 참여자 리스트 - 전체 확인"), Toast.LENGTH_SHORT).show();

                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "회의 참여자 리스트 - 전체: " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                //Log.d("onResponse ? ", "-회의 참여자 리스트 - 전체- : " + String.valueOf(jobj.get("uid")));
                                //Log.d("onResponse ? ", "-회의 참여자 리스트 - 전체- : " + String.valueOf(jobj.get("rtcuid")));
                                //Log.d("onResponse ? ", "-회의 참여자 리스트 - 전체- : " + String.valueOf(jobj.get("name")));

                                //전체 유저 리스트를 생성한다.
                                String profileimg = String.valueOf(jobj.get("basicuri"))+String.valueOf(jobj.get("src"));
                                conferenceUserForm oconferenceUserForm = new conferenceUserForm(String.valueOf(jobj.get("uid")), String.valueOf(jobj.get("rid")), Integer.parseInt(String.valueOf(jobj.get("rtcuid"))), 1, String.valueOf(jobj.get("name")), String.valueOf(jobj.get("usertype")), profileimg, true, true, 0);

                                //카메라 on off 여부를 체크
                                for(int k = 0; k<camerachkdata.size();k++){
                                    if(camerachkdata.get(k).getrtcuid() == Integer.parseInt(String.valueOf(jobj.get("rtcuid")))){ //값이 있으면
                                        Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getrtcuid()));
                                        Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getmuted()));
                                        if(camerachkdata.get(k).getmuted()){ //true
                                            oconferenceUserForm.setcamerachk(false);
                                        }else{ //false
                                            oconferenceUserForm.setcamerachk(true);
                                        }
                                    }

                                    Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getrtcuid()));
                                    Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getmuted()));

                                }
                                
                                //마이크 on off 여부를 체크
                                for(int k = 0; k<mikechkdata.size();k++){
                                    if(mikechkdata.get(k).getrtcuid() == Integer.parseInt(String.valueOf(jobj.get("rtcuid")))){ //값이 있으면
                                        Log.d("alertval mikechkdata", String.valueOf(mikechkdata.get(k).getrtcuid()));
                                        Log.d("alertval mikechkdata", String.valueOf(mikechkdata.get(k).getmuted()));
                                        if(mikechkdata.get(k).getmuted()){ //true
                                            oconferenceUserForm.setmikechk(false);
                                        }else{ //false
                                            oconferenceUserForm.setmikechk(true);
                                        }
                                    }
                                }

                                //camerachkdata 이 변수를 기반으로 totaltouserlist의 카메라 마이크 사항을 변경하여 만든다.
                                totaltouserlist.add(oconferenceUserForm);
                            }


                            //모든 모드에서 카메라 마이크를 on off시 영상보기 리스트에 체크해준다.
                            for (int i = 0; i<roomtouserlist.size();i++) {
                                //전체 유저 중 camera mike 상태에 맞게 roomtouserlist의 변수 변경하기
                                //카메라 on off 여부를 체크
                                for (int k = 0; k < camerachkdata.size(); k++) {
                                    if (camerachkdata.get(k).getrtcuid() == Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid()))) { //값이 있으면
                                        Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getrtcuid()));
                                        Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getmuted()));
                                        if (camerachkdata.get(k).getmuted()) { //true
                                            roomtouserlist.get(i).setcamerachk(false); //끄기
                                        } else { //false
                                            roomtouserlist.get(i).setcamerachk(true); //켜기
                                        }
                                    }

                                    Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getrtcuid()));
                                    Log.d("alertval camerachkdata", String.valueOf(camerachkdata.get(k).getmuted()));

                                }

                                //마이크 on off 여부를 체크
                                for (int k = 0; k < mikechkdata.size(); k++) {
                                    if (mikechkdata.get(k).getrtcuid() == Integer.parseInt(String.valueOf(roomtouserlist.get(i).getrtcuid()))) { //값이 있으면
                                        Log.d("alertval mikechkdata", String.valueOf(mikechkdata.get(k).getrtcuid()));
                                        Log.d("alertval mikechkdata", String.valueOf(mikechkdata.get(k).getmuted()));
                                        if (mikechkdata.get(k).getmuted()) { //true
                                            roomtouserlist.get(i).setmikechk(false); //끄기
                                        } else { //false
                                            roomtouserlist.get(i).setmikechk(true); //켜기
                                        }
                                    }
                                }
                            }

                            //화면 모드일때만 사용.
                            if(PageMode == 0) { //영상보기

                                //원격 커버 변경해주기 위한 함수.
                                //1. 영상보기 클릭시, 2. local change, 3. remote change
                                if(videoremotecovertype == 3){
                                    remotecamera_cover(rtcuid_temporary, muted_temporary);
                                }else if(videoremotecovertype == 1){

                                    //만약에 가져온값에 해당리스트의 내역이 존재하지 않다면 삭제할 것. 그 후 유저 화면을 다시 만들 것.
                                    for (int j = 0; j < roomtouserlist.size(); j++) { //영상보기에서 사용하는 유저 리스트
                                        //가져온 uid에 가지고 있던 uid가 존재하지 않다면 삭제
                                        int existchk = 0;
                                        for (int i = 0; i < jarray.length(); i++) { //전체 참여자 리스트
                                            JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));
                                            if (String.valueOf(jobj.get("uid")).equals(roomtouserlist.get(j).getuid())) { //존재
                                                existchk = 1;
                                            }
                                        }
                                        //참여자보기 리스트와 영상보기 리스트를 비교해서 없는 값은 삭제하겠다.

                                        //존재 하지 않을때
                                        if(existchk == 0){
                                            roomtouserlist.remove(roomtouserlist.get(j));
                                        }
                                    }
                                }

                                //초기화
                                videoremotecovertype = 0;
                            }else if(PageMode == 2){
                                //리사이클러뷰로 만들기
                                Log.d("onResponse ? ", "-회의 참여자 리스트 - 전체- : " + String.valueOf(totaltouserlist));
                                Log.d("onResponse ? ", "-회의 참여자 리스트 - 전체- : " + String.valueOf(totaltouserlist.size()));

                                Makejoinuserrecycle(totaltouserlist);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("5")){ //회의 참가하는 유저 정보를 삭제한다.
                        Log.d("onResponse ? ", "회의 참가하는 유저 정보를 저장한다 " + resultlist);

                    }else if(urlget2.equals("6")){ //문서를 추가 후 리스트 가져옴

                        //문서가 추가되면 리스트를 가져온다.
                        Log.d("onResponse ? ", "문서를 추가 후 리스트 가져옴. " + resultlist);

                        JSONArray jarray = null;
                        try {
                            jarray = new JSONArray(resultlist);

                            Log.d("onResponse ? ", " result : true");

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "문서를 추가 후 리스트 가져옴 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                //문서를 저장
                                ThumbnailDrawCanvas oThumbnailDrawCanvas = new ThumbnailDrawCanvas(oContext);
                                ConferenceDocumentForm oConferenceDocumentForm = new ConferenceDocumentForm(String.valueOf(jobj.get("idx")), String.valueOf(jobj.get("uid")), String.valueOf(jobj.get("rid")), String.valueOf(jobj.get("docutype")), String.valueOf(jobj.get("basicuri")), String.valueOf(jobj.get("src")), oThumbnailDrawCanvas, false, false);
                                Documentdatalist.add(oConferenceDocumentForm);
                            }

                            //그리는 횟수 공간 만들어주기 !!

                            //처음 접근 했는데 문서가 이미 있는 경우 : DraqCountFinal_list 갯수를 파악하여 0이면 처음으로 인지하고 문서 갯수만큼 0을 추가해준다.
                            //처음 접근 했는데 문서가 없는 경우 : DraqCountFinal_list 갯수를 파악하여 0이면 처음으로 인지하고 문서 갯수만큼 0을 추가해준다.
                            //문서를 추가할 경우 : 문서를 추가할 경우 : DraqCountFinal_list 갯수가 0이 아닐 것 임으로 추가로 0을 넣어준다. 하나씩 추가해주니까

                            //이것도 문제가 있다. DraqCountFinal_list이 데이터가 0이어야 처음 접근이라는 건 말이 안된다.
                            //만약 판서없이 문서를 등록한다면 처음 접근한것으로 인지를 할 것이다.
                            //처음 접근할때와 문서를 등록할때를 구분하는 것을 생각해보자.

                            //처음 접근했을때
                            if(WhiteboardFirstType == 0) { //처음 접근했을때
                                Log.d("onResponse ? ", "@@@@@@@@@@@@@@@@@@@@. " + drawCommandList_Total_STEP.size());

                                //선택된 값이 없을때만 첫번째로 등록 해줌.
                                if (SelectDocumentIdx.equals("")) {
                                    SelectDocumentIdx = Documentdatalist.get(0).getidx(); //선택한 문서idx 값 정해주기
                                }

                                //공간이 없을때만 공간 생성해줌
                                if(drawCommandList_Total_STEP.size() == 0){
                                    for (int d = 0; d < Documentdatalist.size(); d++) {

                                        //전체 좌표 값 문서 카운트에 맞게 공간 만들어줌
                                        ArrayList<Pen> totalbox = new ArrayList<>();
                                        ArrayList<Pen> restbox = new ArrayList<>();

                                        //이미지 경로, 비트맵 정보
                                        Pen_STEP Pen_STEP = new Pen_STEP(Documentdatalist.get(d).getidx(), totalbox, restbox, 0, 0);
                                        drawCommandList_Total_STEP.add(d, Pen_STEP);

                                        //이미지 있을때만
                                        if(!String.valueOf(Documentdatalist.get(SelectDocumentCount).getbasicuri()).equals("")){

                                            String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(Documentdatalist.get(SelectDocumentCount).getbasicuri())+String.valueOf(Documentdatalist.get(SelectDocumentCount).getsrc());

                                            //피카소 api를 통해 bitmap으로 변경한다.
                                            Picasso.get()
                                                    .load(imguri)
                                                    .into(new Target() {
                                                        @Override
                                                        public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                                            /* Save the bitmap or do something with it here */

                                                            //Set it in the ImageView
                                                            // theView.setImageBitmap(bitmap);
                                                            drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                            drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                            drawCanvas.invalidate();//화면을 갱신하다.
                                                        }

                                                        @Override
                                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                        }

                                                        @Override
                                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                        }
                                                    });
                                        }else{
                                            drawCanvas.image_access = 0;
                                            drawCanvas.invalidate();//화면을 갱신하다.
                                        }
                                    }
                                }else{ //공간이 이미 잡혀있는 경우

                                    //처음 접근하였을때 무조건 SelectDocumentCount은 0으로 고정되어있다.
                                    //그후 상대방이 페이지를 추가해서 선택문서가 변경되었따. 근데 그래도 0으로 고정이 되어있따.
                                    //신기한건 선택한 문서와 판서가 작성되는 것은 변경이 되어있는데 이미지가 초기값으로 고정이 된어있다는 것이다.


                                    //에러 자꾸 SelectDocumentCount 0으로 들어온다.
                                    Log.d("onResponse ? ", "@@@@@@@@@@@@@@@@@@@@_test" + drawCommandList_Total_STEP.size());
                                    Log.d("onResponse ? ", "@@@@@@@@@@@@@@@@@@@@_test" + SelectDocumentCount);
                                    Log.d("onResponse ? ", "@@@@@@@@@@@@@@@@@@@@_test" + SelectDocumentIdx);

                                    //이미지 있을때만
                                    if(!String.valueOf(Documentdatalist.get(SelectDocumentCount).getbasicuri()).equals("")){

                                        String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(Documentdatalist.get(SelectDocumentCount).getbasicuri())+String.valueOf(Documentdatalist.get(SelectDocumentCount).getsrc());

                                        //피카소 api를 통해 bitmap으로 변경한다.
                                        Picasso.get()
                                                .load(imguri)
                                                .into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                                        /* Save the bitmap or do something with it here */

                                                        //Set it in the ImageView
                                                        // theView.setImageBitmap(bitmap);
                                                        drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                        drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                        drawCanvas.invalidate();//화면을 갱신하다.
                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                });
                                    }else{
                                        drawCanvas.image_access = 0;
                                        drawCanvas.invalidate();//화면을 갱신하다.
                                    }
                                }



                            }else if(WhiteboardFirstType == 2){ //문서 추가, 삭제 후 소켓서버에서 받은 경우 (다른디바이스에서 받은 경우)

                                //추가할 경우만 데이터 추가해줌 : 가져온 데이터가 더 클 경우
                                if(drawCommandList_Total_STEP.size() < Documentdatalist.size()){
                                    //문서에 맞게 공간 생성
                                    for (int d = drawCommandList_Total_STEP.size(); d < Documentdatalist.size(); d++) {
//
                                        //전체 좌표 값 문서 카운트에 맞게 공간 만들어줌
                                        ArrayList<Pen> totalbox = new ArrayList<>();
                                        ArrayList<Pen> restbox = new ArrayList<>();
                                        Pen_STEP Pen_STEP = new Pen_STEP(Documentdatalist.get(d).getidx(), totalbox, restbox, 0, 0);
                                        drawCommandList_Total_STEP.add(d, Pen_STEP);
                                    }

                                    SelectDocumentCount = Documentdatalist.size()-1; //만든 문서를 선택으로 지정하기 -> 문서 고유값으로 조정한다면 문서 고유값을 넣어주고 선택하게 한다.
                                    SelectDocumentIdx = Documentdatalist.get(Documentdatalist.size()-1).getidx(); //선택 idx를 마지막 문서로 설정

                                    //기존것을 삭제하고 추가한 카운트를 넣어줄 것.(추가할때는 선택값을 지정 후에 접근하기에 마지막에 선택을 고정)
                                    documentclicklist.remove(0);
                                    documentclicklist.add(Documentdatalist.size()-1);

                                    //화이트보드 화면에서 가능
                                    if(PageMode == 1) {
                                            //해당 페이지에 내용을 그려준다.
                                            drawCommandList.clear();//문서 초기화한다.

                                            //해당 문서에 맞게 불러와준다
                                            for (int t = 0; t < drawCommandList_Total_STEP.size(); t++) {
                                                if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                                                    ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                                    //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                                    // int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                                    //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                                    drawCommandList.addAll(totalpenlist);
                                                }
                                            }

                                            //이미지 있을때만
                                            if (!String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getbasicuri()).equals("")) {

                                                String imguri = RetrofitService.MOCK_SERVER_FIRSTURL + String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getbasicuri()) + String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getsrc());

                                                //피카소 api를 통해 bitmap으로 변경한다.
                                                Picasso.get()
                                                        .load(imguri)
                                                        .into(new Target() {
                                                            @Override
                                                            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                /* Save the bitmap or do something with it here */

                                                                //Set it in the ImageView
                                                                // theView.setImageBitmap(bitmap);
                                                                drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                                drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                                drawCanvas.invalidate();//화면을 갱신하다.
                                                            }

                                                            @Override
                                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                            }

                                                            @Override
                                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                            }
                                                        });
                                            } else {
                                                drawCanvas.image_access = 0;
                                                drawCanvas.invalidate();//화면을 갱신하다.
                                            }
                                    }

                                }else{ //삭제의 경우 : 가져온 데이터가 더 작을 경우

                                    //전체 좌표에서 삭제할 문서 고유번호에 맞게 삭제한다.
                                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){//5
                                        int exist_data = 0;
                                        for(int d = 0; d<Documentdatalist.size();d++){//4, 2

                                            //같은게 있으면?
                                            if(Documentdatalist.get(d).getidx().equals(drawCommandList_Total_STEP.get(t).getidx())){
                                                exist_data = 1;
                                            }
                                        }

                                        //같은게 존재하지 않으면 삭제한다.
                                        if(exist_data == 0){
                                            drawCommandList_Total_STEP.remove(t);
                                            t--;
                                        }
                                    }

                                    Log.d("test3333", String.valueOf(drawCommandList_Total_STEP.size())); //0 :디바이스의 변수 리스트
                                    Log.d("test3333", String.valueOf(Documentdatalist.size())); //1 : 서버에서 가져온 문서 리스트


                                    //문서를 모두 지울 경우
                                    if(drawCommandList_Total_STEP.size() == 0){
                                        for (int d = 0; d < Documentdatalist.size(); d++) {

                                            //첫번쩨 문서로 선택
                                            SelectDocumentIdx = Documentdatalist.get(d).getidx();
                                            SelectDocumentCount = Documentdatalist.size()-1;

                                            //전체 좌표 값 문서 카운트에 맞게 공간 만들어줌
                                            ArrayList<Pen> totalbox = new ArrayList<>();
                                            ArrayList<Pen> restbox = new ArrayList<>();
                                            Pen_STEP Pen_STEP = new Pen_STEP(Documentdatalist.get(d).getidx(), totalbox, restbox, 0, 0);
                                            drawCommandList_Total_STEP.add(d, Pen_STEP);
                                        }


//                                        Log.d("test3333", String.valueOf(SelectDocumentIdx));
//
//                                        Log.d("test3333", String.valueOf(drawCommandList_Total_STEP.size())); //1 :디바이스의 변수 리스트
//                                        Log.d("test3333", String.valueOf(Documentdatalist.size())); //1 : 서버에서 가져온 문서 리스트
//
//                                        //확인용
//                                        for (int d = 0; d < drawCommandList_Total_STEP.size(); d++) {
//                                            Log.d("test3333_1", String.valueOf(drawCommandList_Total_STEP.get(d))); //1 : 서버에서 가져온 문서 리스트
//                                            Log.d("test3333_1", String.valueOf(drawCommandList_Total_STEP.get(d).getidx())); //1 : 서버에서 가져온 문서 리스트
//                                        }


                                        //화이트보드 화면에서 가능
                                        if(PageMode == 1) {
                                                //해당 페이지에 내용을 그려준다.
                                                drawCommandList.clear();//문서 초기화한다.

                                                //해당 문서에 맞게 불러와준다
                                                for (int t = 0; t < drawCommandList_Total_STEP.size(); t++) {
                                                    if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                                                        ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                                        //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                                        // int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                                        //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                                        drawCommandList.addAll(totalpenlist);
                                                    }
                                                }

                                                //이미지 있을때만
                                                if (!String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getbasicuri()).equals("")) {

                                                    String imguri = RetrofitService.MOCK_SERVER_FIRSTURL + String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getbasicuri()) + String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getsrc());

                                                    //피카소 api를 통해 bitmap으로 변경한다.
                                                    Picasso.get()
                                                            .load(imguri)
                                                            .into(new Target() {
                                                                @Override
                                                                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                    /* Save the bitmap or do something with it here */

                                                                    //Set it in the ImageView
                                                                    // theView.setImageBitmap(bitmap);
                                                                    drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                                    drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                                    drawCanvas.invalidate();//화면을 갱신하다.
                                                                }

                                                                @Override
                                                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                                }

                                                                @Override
                                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                }
                                                            });
                                                } else {
                                                    drawCanvas.image_access = 0;
                                                    drawCanvas.invalidate();//화면을 갱신하다.
                                                }
                                        }
                                    }else{ //문서를 한개이상 남겨놓은 경우

                                        if (SelectDocumentIdx.equals("")) {
                                            SelectDocumentCount = 0;
                                            SelectDocumentIdx = Documentdatalist.get(0).getidx(); //선택한 문서idx 값 정해주기 : 첫번째 문서를 기본으로 선택한다.
                                        }


                                        //내가 선택한 값 존재 여부 체크
                                        int existidx = 0;
                                        for (int d = 0; d < Documentdatalist.size(); d++) {
                                            //가져온 리스트에 내가 선택한 값이 있으면?
                                            if(Documentdatalist.get(d).getidx().equals(SelectDocumentIdx)){
                                                existidx = 1; //선택하지 않은 문서를 삭제한 경우 : 선택한 문서 그대로 유지
                                            }//선택한 문서를 삭제한 경우 : 마지막 문서를 강제 선택
                                        }

                                        //선택한 값이 가져온 리스트에 있으면 1 : 선택하지 않은 값이 삭제 된 것
                                        //가져온 리스트에 선택한 값이 없으면 0 : 선택한 값이 삭제된 것
                                        Log.d("-----@@@@@existidx@@@@@@-----",String.valueOf(existidx)); //SelectDocumentIdx 값 존재

                                        //Log.d("-----@@@@@documentclicklist@@@@@@-----",String.valueOf(documentclicklist)); //


                                        //선택된 값이 없을때만 첫번째로 등록 해줌. : 메뉴탭으로 화이트보드를 선택한 경우 선택한 아이디가 없다.
                                       // if (SelectDocumentIdx.equals("")) {
                                        //    SelectDocumentCount = 0;
                                       //     SelectDocumentIdx = Documentdatalist.get(0).getidx(); //선택한 문서idx 값 정해주기 : 첫번째 문서를 기본으로 선택한다.
                                       // }else{
                                            //선택한 값이 삭제 리스트에 존재하는지 체크후 존재하지 않으면 마지막 문서로 변경한다.
                                            if(existidx == 0){ //선택한 문서를 삭제한 경우 : 마지막 문서를 강제 선택.
                                                SelectDocumentCount = Documentdatalist.size()-1; //만든 문서를 선택으로 지정하기 -> 문서 고유값으로 조정한다면 문서 고유값을 넣어주고 선택하게 한다.
                                                SelectDocumentIdx = Documentdatalist.get(Documentdatalist.size()-1).getidx(); //선택 idx를 마지막 문서로 설정

                                                //기존것을 삭제하고 추가한 카운트를 넣어줄 것.(삭제할때 값이 없을때 마지막 선택을 고정으로 한다. 추가일때는 여기로 안들어옴)
                                                documentclicklist.remove(0);
                                                documentclicklist.add(Documentdatalist.size()-1);
                                            }else{ //선택하지 않은 문서를 삭제한 경우 : 선택한 문서 그대로 유지

                                                //SelectDocumentCount가 밀리는 경우. SelectDocumentIdx는 상관이 없음.
                                                //1 2 3 문서가 있는데 1을 선택할 경우 : 밀린다.
                                                //3을 선택할 경우 : 밀리지 않음

                                                //SelectDocumentIdx에 맞는 SelectDocumentCount를 변경한다.
                                                for(int ddl = 0;ddl<Documentdatalist.size();ddl++){
                                                    if(Documentdatalist.get(ddl).getidx().equals(SelectDocumentIdx)){
                                                        SelectDocumentCount = ddl;

                                                        //기존것을 삭제하고 변경된 위치를 넣어줄 것.
                                                        documentclicklist.remove(0);
                                                        documentclicklist.add(ddl);
                                                    }
                                                }
                                            }
                                       // }

                                        //화이트보드 화면에서 가능
                                        if(PageMode == 1) {

                                            //선택한 값이 삭제 되었을 경우와 선택하지 않고 화이트보드에 접근한 경우
                                            if(existidx == 0) {
                                                //해당 페이지에 내용을 그려준다.
                                                drawCommandList.clear();//문서 초기화한다.

                                                //해당 문서에 맞게 불러와준다
                                                for (int t = 0; t < drawCommandList_Total_STEP.size(); t++) {
                                                    if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                                                        ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                                        //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                                        // int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                                        //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                                        drawCommandList.addAll(totalpenlist);
                                                    }
                                                }

                                                Log.d("-----@@@@@existidx@@@@@@-----", String.valueOf(existidx)); //SelectDocumentIdx 값 존재

                                                //이미지 있을때만
                                                if (!String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getbasicuri()).equals("")) {

                                                    String imguri = RetrofitService.MOCK_SERVER_FIRSTURL + String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getbasicuri()) + String.valueOf(Documentdatalist.get(Documentdatalist.size() - 1).getsrc());

                                                    //피카소 api를 통해 bitmap으로 변경한다.
                                                    Picasso.get()
                                                            .load(imguri)
                                                            .into(new Target() {
                                                                @Override
                                                                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                    /* Save the bitmap or do something with it here */

                                                                    //Set it in the ImageView
                                                                    // theView.setImageBitmap(bitmap);
                                                                    drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                                    drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                                    drawCanvas.invalidate();//화면을 갱신하다.
                                                                }

                                                                @Override
                                                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                                }

                                                                @Override
                                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                }
                                                            });
                                                } else {
                                                    drawCanvas.image_access = 0;
                                                    drawCanvas.invalidate();//화면을 갱신하다.
                                                }
                                            }else{ //

                                            }
                                        }
                                    }

                                    Log.d("-----drawCommandList_Total_STEP-----",String.valueOf(drawCommandList_Total_STEP.size()));
                                    Log.d("-----drawCommandList_Total_STEP-----",String.valueOf(Documentdatalist.size()));

                                }

                            }else if(WhiteboardFirstType == 1){ //문서를 추가하는 경우(현재 디바이스에서)
                                SelectDocumentCount = Documentdatalist.size()-1; //만든 문서를 선택으로 지정하기 -> 문서 고유값으로 조정한다면 문서 고유값을 넣어주고 선택하게 한다.
                                SelectDocumentIdx = Documentdatalist.get(Documentdatalist.size()-1).getidx();//마지막 객체의 고유번호를 저장한다.

                                //기존것을 삭제하고 추가한 카운트를 넣어줄 것.
                                //SelectDocumentIdx 를 선택 리스트에 넣어주기
                                documentclicklist.remove(0);
                                documentclicklist.add(SelectDocumentCount);

                                //이미지 있을때만
                                if(!String.valueOf(Documentdatalist.get(Documentdatalist.size()-1).getbasicuri()).equals("")){

                                    String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(Documentdatalist.get(Documentdatalist.size()-1).getbasicuri())+String.valueOf(Documentdatalist.get(Documentdatalist.size()-1).getsrc());

                                    //피카소 api를 통해 bitmap으로 변경한다.
                                    Picasso.get()
                                            .load(imguri)
                                            .into(new Target() {
                                                @Override
                                                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                                    /* Save the bitmap or do something with it here */

                                                    //Set it in the ImageView
                                                    // theView.setImageBitmap(bitmap);
                                                    drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                    drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                    drawCommandList.clear();//문서 초기화한다.
                                                    drawCanvas.invalidate();//화면을 갱신하다.
                                                }

                                                @Override
                                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                }

                                                @Override
                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                }
                                            });
                                }else{
                                    drawCanvas.image_access = 0;
                                    drawCommandList.clear();//문서 초기화한다.
                                    drawCanvas.invalidate();//화면을 갱신하다.
                                }

                                //화이트보드 리셋하기
//                                drawCommandList.clear();//문서 초기화한다.
                                //drawCanvas.invalidate();//화면을 갱신하다.

                               // Log.d("onResponse ? ", "문서를 추가 후 리스트 가져옴 : " + String.valueOf(Documentdatalist.size()));
                               // Log.d("onResponse ? ", "문서를 추가 후 리스트 가져옴 : " + String.valueOf(drawcount_list.size()));

                                //문서에 맞게 공간 생성 - 기존에 그릴공간의 사이즈에서 추가된 문서만큼 공간을 준다.
                                for (int d = drawCommandList_Total_STEP.size(); d < Documentdatalist.size(); d++) {// 0 1 2 3

                                    //전체 좌표 값 문서 카운트에 맞게 공간 만들어줌
                                    ArrayList<Pen> totalbox = new ArrayList<>();
                                    ArrayList<Pen> restbox = new ArrayList<>();
                                    Pen_STEP Pen_STEP = new Pen_STEP(Documentdatalist.get(d).getidx(), totalbox, restbox, 0, 0);
                                    drawCommandList_Total_STEP.add(d, Pen_STEP);
                                }

                                Log.d("drawCommandList_Total_STEP----", String.valueOf(drawCommandList_Total_STEP.size()));

                                for(int t = 0;t<drawCommandList_Total_STEP.size();t++) {
                                    if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                                        //ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                        //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                        int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                        //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                        //새 문서를 등록할때만 활성화 되도록 해야한다.
                                        drawCanvas.usetoolmode = 4;
                                        //새문서 등록은 4, 문서 총 갯수를 전송
                                        oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", "", String.valueOf(drawCanvas.usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));
                                    }
                                }


                                Log.d("drawCommandList_Total_STEP++++", String.valueOf(drawCommandList_Total_STEP.size()));
                            }

                            //화이트보드 화면에서 가능
                            if(PageMode == 1) {
                                //좀더 빠르게 처리할 수 없을까?

                                //!!문제 : 처음에 들어오면 이걸로 설정이 되어버린다.- 해결함
                                //전에 사용한 색상 저장하기.
                                //Toast.makeText(GlobalClass, String.valueOf(WhiteboardFirstNum), Toast.LENGTH_SHORT).show();
                                if(WhiteboardFirstNum == 1){//처음접근시
                                    drawCanvas.changeToolnColor(DrawCanvas.MODE_BLACK, DrawCanvas.MODE_PEN);
                                }else{ //두번째 이후 접근시
                                    drawCanvas.changeToolnColor(beforetool_total, beforecolor_total);
                                }

                                MakeDocumentrecycle(Documentdatalist);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //이미지, pdf를 업로드 후 내부 디바이스에 있는 파일을 삭제합니다.
                        //pdf, 이미지 잘 지워지는 것 확인함.
//                        for (int i = 0; i < imguploadlist_uri.size(); i++) {
//                            if (imguploadlist_uri.get(i).isSelected()) { //true인것만 체크할 것
//                                //Log.d("innerfilenamechk", imguploadlist_uri.get(i).getinnerFilename());
//                               // Log.d("----fileName_saved----", String.valueOf(fileName_saved));
//                                File dir = oContext.getFilesDir();
//                                File file_ = new File(dir, imguploadlist_uri.get(i).getinnerFilename());
//                                boolean deleted = file_.delete();
//                            }
//                        }

                    }else if(urlget2.equals("7")){ //문서를 삭제되면 리스트를 가져온다. : 자신의 디바이스에서 삭제할 경우.

                        //문서를 삭제되면 리스트를 가져온다.
                        Log.d("onResponse ? ", "문서를 삭제 후 리스트 가져옴. " + resultlist);

                        Documentdatalist.clear();

                        JSONArray jarray = null;
                        try {
                            jarray = new JSONArray(resultlist);

                            Log.d("onResponse ? ", " result : true");

                            //삭제후 가져오는 리스트
                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "문서를 삭제 후 리스트 가져옴 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                //문서를 저장
                                ThumbnailDrawCanvas oThumbnailDrawCanvas = new ThumbnailDrawCanvas(oContext);
                                ConferenceDocumentForm oConferenceDocumentForm = new ConferenceDocumentForm(String.valueOf(jobj.get("idx")), String.valueOf(jobj.get("uid")), String.valueOf(jobj.get("rid")), String.valueOf(jobj.get("docutype")), String.valueOf(jobj.get("basicuri")), String.valueOf(jobj.get("src")), oThumbnailDrawCanvas, false, false);
                                Documentdatalist.add(oConferenceDocumentForm);
                            }
                            Log.d("onResponse ? ", "문서를 삭제 후 리스트 가져옴 : " + String.valueOf(DeleteDocumentCountlist.size()));
                            Log.d("onResponse ? ", "문서를 삭제 후 리스트 가져옴 : " + String.valueOf(DeleteDocumentlist.size()));


                            if(DeleteDocumentlist.size() > 0) {

                                //전체 좌표에서 삭제할 문서 고유번호에 맞게 삭제한다.
                                for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                                    int existdata = 0;
                                    for(int dl = 0; dl<DeleteDocumentlist.size();dl++){
                                        if(drawCommandList_Total_STEP.get(t).getidx().equals(DeleteDocumentlist.get(dl))){ //삭제 되는 idx인지 확인
                                            existdata = 1;
                                        }
                                    }

                                    if(existdata == 1){
                                        drawCommandList_Total_STEP.remove(t);
                                        t--;
                                    }
                                }

                                //문서를 모두 지울 경우
                                if (drawCommandList_Total_STEP.size() == 0) {
                                    for (int d = 0; d < Documentdatalist.size(); d++) {
                                        SelectDocumentCount = Documentdatalist.size()-1;
                                        SelectDocumentIdx = Documentdatalist.get(d).getidx();

                                        //전체 좌표 값 문서 카운트에 맞게 공간 만들어줌
                                        ArrayList<Pen> totalbox = new ArrayList<>();
                                        ArrayList<Pen> restbox = new ArrayList<>();
                                        Pen_STEP Pen_STEP = new Pen_STEP(Documentdatalist.get(d).getidx(), totalbox, restbox, 0, 0);
                                        drawCommandList_Total_STEP.add(d, Pen_STEP);
                                    }
                                }else{ //한개 이상 문서가 남아 있는 경우
                                    //삭제후 선택했던것이 지운 리스트에 있다면? 마지막 선택으로 변경할 것!
                                    //삭제 리스트에 선택한 idx가 있는지 찾는다.
                                    int extdocu = 0;
                                    for (int dl = 0; dl < DeleteDocumentlist.size(); dl++) {
                                        if (DeleteDocumentlist.get(dl).equals(SelectDocumentIdx)) {
                                            extdocu = 1; //선택한 문서가 삭제 된 경우
                                        }
                                       //선택한 문서가 삭제되지 않은 경우
                                    }

                                    //Log.d("--SelectDocumentIdx--", String.valueOf(SelectDocumentIdx));
                                    Log.d("--extdocu--", String.valueOf(extdocu));

                                    //삭제 리스트에 선택한 idx가 있다면? 선택한 문서를 변경해준다.
                                    if (extdocu == 1) { //선택한 문서가 삭제 된 경우
                                        SelectDocumentIdx = Documentdatalist.get(Documentdatalist.size() - 1).getidx(); //마지막 idx로 변경
                                        SelectDocumentCount = Documentdatalist.size() - 1;

                                        //기존것을 삭제하고 추가한 카운트를 넣어줄 것.
                                        documentclicklist.remove(0);
                                        documentclicklist.add(Documentdatalist.size()-1);
                                    }else{ //선택한 문서가 삭제되지 않은 경우
                                        for(int ddl = 0;ddl<Documentdatalist.size();ddl++){
                                            if(Documentdatalist.get(ddl).getidx().equals(SelectDocumentIdx)){
                                                SelectDocumentCount = ddl;

                                                //기존것을 삭제하고 변경된 위치를 넣어줄 것.
                                                documentclicklist.remove(0);
                                                documentclicklist.add(ddl);
                                            }
                                        }
                                    }
                                }
                            }

                            Log.d("documentclicklist", String.valueOf(documentclicklist));

                            drawCommandList.clear();

                            //해당 문서에 맞게 불러와준다
                            for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                                if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                                    ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                    //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                    // int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                    //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                    drawCommandList.addAll(totalpenlist);
                                }
                            }

                            //이미지 있는지 체크
                            for(int d = 0; d<Documentdatalist.size();d++){

                                if(Documentdatalist.get(d).getidx().equals(SelectDocumentIdx)){ //선택한 문서의 데이터
                                    if(!String.valueOf(Documentdatalist.get(d).getbasicuri()).equals("")){
                                        String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(Documentdatalist.get(d).getbasicuri())+String.valueOf(Documentdatalist.get(d).getsrc());

                                        //피카소 api를 통해 bitmap으로 변경한다.
                                        Picasso.get()
                                                .load(imguri)
                                                .into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                                        /* Save the bitmap or do something with it here */

                                                        //Set it in the ImageView
                                                        // theView.setImageBitmap(bitmap);
                                                        drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                                        drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                                        drawCanvas.invalidate();//화면을 갱신하다.
                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                });
                                    }else{ //이미지 없는 경우
                                        drawCanvas.image_access = 0;
                                        drawCanvas.invalidate();//화면을 갱신하다.
                                    }
                                }
                            }

                            MakeDocumentrecycle(Documentdatalist);


                            //삭제 데이터가 있을때만 전송함.
                            if(DeleteDocumentCountlist.size() > 0){
                                drawCanvas.usetoolmode = 5;
                                //문서 삭제 5, 문서 총 갯수를 전송
                                //oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", "", String.valueOf(drawCanvas.usetoolmode), String.valueOf(drawcount_list.get(SelectDocumentCount)), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));
                                oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", "", String.valueOf(drawCanvas.usetoolmode), String.valueOf(Documentdatalist.size()-1), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));
                            }

                            //전에 선택한 펜 자동 선택
                            drawCanvas.changeToolnColor(beforetool_total, beforecolor_total);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    /*
                    else if(urlget2.equals("8")){//문서 리스트 페이징 시 가져오는 부분
                        progressBar.setVisibility(View.GONE);

                        //문서 리스트를 가져온다.
                        Log.d("onResponse ? ", "문서 리스트 페이징 " + resultlist);

                        //데이터를 잘 가져옴. 가져온 데이터를 리스트에 추가로 뿌릴 것.!!!!

                        JSONArray jarray = null;
                        try {
                            jarray = new JSONArray(resultlist);

                            //Log.d("onResponse ? ", " result : true");

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "문서 리스트 페이징 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                //문서를 저장
                                ThumbnailDrawCanvas oThumbnailDrawCanvas = new ThumbnailDrawCanvas(oContext);
                                ConferenceDocumentForm oConferenceDocumentForm = new ConferenceDocumentForm(String.valueOf(jobj.get("idx")), String.valueOf(jobj.get("uid")), String.valueOf(jobj.get("rid")), String.valueOf(jobj.get("docutype")), String.valueOf(jobj.get("basicuri")), String.valueOf(jobj.get("src")), oThumbnailDrawCanvas, false, false);
                                Documentdatalist.add(oConferenceDocumentForm);


                                //전체 좌표 값 문서 카운트에 맞게 공간 만들어줌
                                ArrayList<Pen> totalbox = new ArrayList<>();
                                ArrayList<Pen> restbox = new ArrayList<>();
                                Pen_STEP Pen_STEP = new Pen_STEP(String.valueOf(jobj.get("idx")), totalbox, restbox, 0, 0);
                                drawCommandList_Total_STEP.add(d, Pen_STEP);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //데이터를 바로 출력시킬것
                        MakeDocumentrecycle(Documentdatalist);
                    }*/
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //화상회의 유저를 저장
    public void joinuserinconference(int Rtcuid){

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), myuid);
        requestMap.put("uid", uid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);
        RequestBody rtcuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Rtcuid));
        requestMap.put("rtcuid", rtcuid);
        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        call = retrofitService.joinuserinconference(
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }
    //화상회의 유저를 삭제
    public void deleteuserinconference(){

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), myuid);
        requestMap.put("uid", uid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);

        call = retrofitService.deleteuserinconference(
                5,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //상대방 입장시에만 동작한다.
    //화상회의 유저 리스트 가져오기 - 해당 rtcuid를 찾아서 값이 있으면 그 데이터를 넣어주는 것.
    public void checkuserinroom(int limit, int offset, int rtcuid){ // 회원을 리스트에 추가한다.

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);
        RequestBody rtcuid_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(rtcuid));
        requestMap.put("rtcuid", rtcuid_);

        call = retrofitService.checkuserinroom(
                limit,
                offset,
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }


    //참여자 리스트 만들때 사용
    public void getjoinuserlist_total(int limit, int offset){ //회원리스트를 다시 불러온다.

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid);

        call = retrofitService.getconferencejoinuserlist(
                limit,
                offset,
                4,
                requestMap
        );
        RestapiResponse(); //응답
    }





    // 문서 -> 6 저장 7 삭제 8 리스트
    //화상회의 문서 추가
    //docutype 1. 새페이지, 2. 이미지
    //firsttype 0.화이트보드에 입장시, 1. 새페이지 등록시, 2. 서버에서 접근
    public void saveconferencedocument(int docutype, int firsttype){
        WhiteboardFirstType = firsttype;

        imguploadlist_multipart_document.clear();

        for (int i = 0; i < imguploadlist_uri.size(); i++) {

            if(imguploadlist_uri.get(i).isSelected()){ //true인것만 체크할 것

                Log.d("innerfilenamechk", imguploadlist_uri.get(i).getinnerFilename());

                String path = imguploadlist_uri.get(i).getcopyUri().getPath();

                Log.d("uridata_ path", String.valueOf(path));
                //String path = String.valueOf(imguploadlist_uri.get(i).getUri());
                File file = new File(path);

                Log.d("img upload file link", imguploadlist_uri.get(i).toString());
                // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
                // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), imguploadlist_uri.get(i).toString());
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // 사진 파일 이름
                String fileName = "documentimg" + i + ".jpg";
                // RequestBody로 Multipart.Part 객체 생성
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file"+i, fileName, requestFile);


                // 추가
                imguploadlist_multipart_document.add(filePart);
            }
        }



        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), myuid);
        requestMap.put("uid", uid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);
        RequestBody docutype_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(docutype));
        requestMap.put("docutype", docutype_);
        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        //첫번째 타입 : 화이트보드에 접근시에는 0, 새문서로 접근시에는 1을 넘겨준다.
        RequestBody firsttype_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(firsttype));
        requestMap.put("firsttype", firsttype_);

        //페이지 할 수를 가져올 것. : firsttype = 0일때만 설정

        //처음 접근했거나, 모드를 변경할때 마다 실행함.
//        if(firsttype == 0){
//            //몇개의 리스트를 가져올지 설정한다.
//            //초기값은 무조건 0이고, 몇개를 가져올지 정하는 것.
//
//            int limitfix = 0;
//            if(pagenum == 0){ //초기값 : 페이징하지않음, 문서의 갯수가 10개 미만
//                limitfix = limitnum; // 10 기본값으로 고정
//            }else{ //페이징을 진행함, 갯수가 10개 이상
//                limitfix = pagenum*limitnum;
//            }
//            RequestBody objcount = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(limitfix));
//            requestMap.put("objcount", objcount);
//        }


        call = retrofitService.saveconferencedocument(
                6,
                requestMap,
                imguploadlist_multipart_document
        );
        RestapiResponse(); //응답
    }


    //화상회의 문서 삭제
    public void deleteconferencedocument(ArrayList<String> DeleteDocumentlist){

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), myuid);
        requestMap.put("uid", uid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);
        RequestBody DeleteDocumentlist_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(DeleteDocumentlist));
        requestMap.put("DeleteDocumentlist", DeleteDocumentlist_);


        //전부 삭제했을때 새페이지 하나 만들어주기 위함
        RequestBody docutype_ = RequestBody.create(MediaType.parse("text/plain"), "1"); //새페이지
        requestMap.put("docutype", docutype_);
        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);



        call = retrofitService.deleteconferencedocument(
                7,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //문서 리스트 페이징하여 가져올때 사용한다.
    /*
    public void GetDocuemntlist(int limit, int offset) {

        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), myuid);
        requestMap.put("uid", uid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getconferencedocumentlist(
                limit,
                offset,
                8,
                requestMap
        );
        RestapiResponse(); //응답
    }*/

    public void setPermission(File file) throws IOException{
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);

        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);

        Files.setPosixFilePermissions(file.toPath(), perms);
    }



    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }


    public void hbrecodinit(){
        initViews();

        recordimg = findViewById(R.id.recordimg);
        //setOnClickListeners();
       // setRecordAudioCheckBoxListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Init HBRecorder
            hbRecorder = new HBRecorder(this, this);

            //When the user returns to the application, some UI changes might be necessary,
            //check if recording is in progress and make changes accordingly
            if (hbRecorder.isBusyRecording()) {
               // startbtn.setText(R.string.stop_recording);
                recordimg.setImageResource(R.drawable.recordon);
            }
        }

        // Examples of how to use the HBRecorderCodecInfo class to get codec info
        HBRecorderCodecInfo hbRecorderCodecInfo = new HBRecorderCodecInfo();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int mWidth = hbRecorder.getDefaultWidth();
            int mHeight = hbRecorder.getDefaultHeight();
            String mMimeType = "video/avc";
            int mFPS = 30;
            if (hbRecorderCodecInfo.isMimeTypeSupported(mMimeType)) {
                String defaultVideoEncoder = hbRecorderCodecInfo.getDefaultVideoEncoderName(mMimeType);
                boolean isSizeAndFramerateSupported = hbRecorderCodecInfo.isSizeAndFramerateSupported(mWidth, mHeight, mFPS, mMimeType, ORIENTATION_PORTRAIT);
                Log.e("EXAMPLE", "THIS IS AN EXAMPLE OF HOW TO USE THE (HBRecorderCodecInfo) TO GET CODEC INFO:");
                Log.e("HBRecorderCodecInfo", "defaultVideoEncoder for (" + mMimeType + ") -> " + defaultVideoEncoder);
                Log.e("HBRecorderCodecInfo", "MaxSupportedFrameRate -> " + hbRecorderCodecInfo.getMaxSupportedFrameRate(mWidth, mHeight, mMimeType));
                Log.e("HBRecorderCodecInfo", "MaxSupportedBitrate -> " + hbRecorderCodecInfo.getMaxSupportedBitrate(mMimeType));
                Log.e("HBRecorderCodecInfo", "isSizeAndFramerateSupported @ Width = "+mWidth+" Height = "+mHeight+" FPS = "+mFPS+" -> " + isSizeAndFramerateSupported);
                Log.e("HBRecorderCodecInfo", "isSizeSupported @ Width = "+mWidth+" Height = "+mHeight+" -> " + hbRecorderCodecInfo.isSizeSupported(mWidth, mHeight, mMimeType));
                Log.e("HBRecorderCodecInfo", "Default Video Format = " + hbRecorderCodecInfo.getDefaultVideoFormat());

                HashMap<String, String> supportedVideoMimeTypes = hbRecorderCodecInfo.getSupportedVideoMimeTypes();
                for (Map.Entry<String, String> entry : supportedVideoMimeTypes.entrySet()) {
                    Log.e("HBRecorderCodecInfo", "Supported VIDEO encoders and mime types : " + entry.getKey() + " -> " + entry.getValue());
                }

                HashMap<String, String> supportedAudioMimeTypes = hbRecorderCodecInfo.getSupportedAudioMimeTypes();
                for (Map.Entry<String, String> entry : supportedAudioMimeTypes.entrySet()) {
                    Log.e("HBRecorderCodecInfo", "Supported AUDIO encoders and mime types : " + entry.getKey() + " -> " + entry.getValue());
                }

                ArrayList<String> supportedVideoFormats = hbRecorderCodecInfo.getSupportedVideoFormats();
                for (int j = 0; j < supportedVideoFormats.size(); j++) {
                    Log.e("HBRecorderCodecInfo", "Available Video Formats : " + supportedVideoFormats.get(j));
                }
            }else{
                Log.e("HBRecorderCodecInfo", "MimeType not supported");
            }

        }
    }

    //Create Folder
    //Only call this on Android 9 and lower (getExternalStoragePublicDirectory is deprecated)
    //This can still be used on Android 10> but you will have to add android:requestLegacyExternalStorage="true" in your Manifest
    private void createFolder() {
        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "HBRecorder");
        if (!f1.exists()) {
            if (f1.mkdirs()) {
                Log.i("Folder ", "created");
            }
        }
    }

    //Init Views
    private void initViews() {
        //startbtn = findViewById(R.id.button_start);
        //radioGroup = findViewById(R.id.radio_group);
        //recordAudioCheckBox = findViewById(R.id.audio_check_box);
        //custom_settings_switch = findViewById(R.id.custom_settings_switch);
    }

//    //Start Button OnClickListener
//    private void setOnClickListeners() {
//        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    //first check if permissions was granted
//                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)) {
//                        hasPermissions = true;
//                    }
//                    if (hasPermissions) {
//                        //check if recording is in progress
//                        //and stop it if it is
//                        if (hbRecorder.isBusyRecording()) {
//                            hbRecorder.stopScreenRecording();
//                            startbtn.setText(R.string.start_recording);
//                        }
//                        //else start recording
//                        else {
//                            startRecordingScreen();
//                        }
//                    }
//                } else {
//                    showLongToast("This library requires API 21>");
//                }
//            }
//        });
//    }



    //Check if audio should be recorded
//    private void setRecordAudioCheckBoxListener() {
//        recordAudioCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                //Enable/Disable audio
//                isAudioEnabled = isChecked;
//            }
//        });
//    }
    //녹화 시작
    @Override
    public void HBRecorderOnStart() {
        Log.e("HBRecorder", "HBRecorderOnStart called");
    }

    @Override
    public void HBRecorderOnComplete() { //녹화 종료
        recordimg.setImageResource(R.drawable.recordoff);
       // startbtn.setText(R.string.start_recording);
        showLongToast("vod가 저장되었습니다.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Update gallery depending on SDK Level
            if (hbRecorder.wasUriSet()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
                    updateGalleryUri();

                    updatevodServer();
                } else {
                    refreshGalleryFile();
                }
            }else{
                refreshGalleryFile();
            }
        }

    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {
        // Error 38 happens when
        // - the selected video encoder is not supported
        // - the output format is not supported
        // - if another app is using the microphone

        //It is best to use device default

        if (errorCode == SETTINGS_ERROR) {
            showLongToast(getString(R.string.settings_not_supported_message));
        } else if ( errorCode == MAX_FILE_SIZE_REACHED_ERROR) {
            showLongToast(getString(R.string.max_file_size_reached_message));
        } else {
            showLongToast(getString(R.string.general_recording_error_message));
            Log.e("HBRecorderOnError", reason);
        }

        recordimg.setImageResource(R.drawable.recordoff);
        //startbtn.setText(R.string.start_recording);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void refreshGalleryFile() {
        MediaScannerConnection.scanFile(this,
                new String[]{hbRecorder.getFilePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);

                    }
               });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void updateGalleryUri(){ //갤러리에 저장

        contentValues.clear();
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
        getContentResolver().update(mUri, contentValues, null, null);
    }





    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void updatevodServer(){ //vod 서버로 전송

        imguploadlist_multipart_vod.clear();

        String getDirectory = Environment.getExternalStorageDirectory() + "/Movies/HBRecorder/"+hbRecorder.getFileName();
        //Toast.makeText(GlobalClass, getDirectory, Toast.LENGTH_SHORT).show();
        File file = new File(getDirectory);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // 사진 파일 이름
        String fileName = "vod" + hbRecorder.getFileName() + ".jpg";
        // RequestBody로 Multipart.Part 객체 생성
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile);
        imguploadlist_multipart_vod.add(filePart);


        //겔러리의 해당 경로 알아내서 그 파일을 서버로 전송한다. 전송한 파일을 서버에 저장하고, 경로를 db에 저장한다, 끝
        //서버에 등록한다.


        RestapiStart(); //레트로핏 빌드
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), myuid);
        requestMap.put("uid", uid);
        RequestBody rid = RequestBody.create(MediaType.parse("text/plain"), roomidx);
        requestMap.put("rid", rid);
        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);

        call = retrofitService.saveconferencevod(
                3,
                requestMap,
                imguploadlist_multipart_vod
        );
        RestapiResponse(); //응답
    }



    //Start recording screen
    //It is important to call it like this
    //hbRecorder.startScreenRecording(data); should only be called in onActivityResult
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecordingScreen() {
//        if (custom_settings_switch.isChecked()) {
//            //WHEN SETTING CUSTOM SETTINGS YOU MUST SET THIS!!!
//            hbRecorder.enableCustomSettings();
//            customSettings();
//            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//            Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
//            startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
//        } else {
            quickSettings();
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
            startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void customSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Is audio enabled
        boolean audio_enabled = prefs.getBoolean("key_record_audio", true);
        hbRecorder.isAudioEnabled(audio_enabled);

        //Audio Source
        String audio_source = prefs.getString("key_audio_source", null);
        if (audio_source != null) {
            switch (audio_source) {
                case "0":
                    hbRecorder.setAudioSource("DEFAULT");
                    break;
                case "1":
                    hbRecorder.setAudioSource("CAMCODER");
                    break;
                case "2":
                    hbRecorder.setAudioSource("MIC");
                    break;
            }
        }

        //Video Encoder
        String video_encoder = prefs.getString("key_video_encoder", null);
        if (video_encoder != null) {
            switch (video_encoder) {
                case "0":
                    hbRecorder.setVideoEncoder("DEFAULT");
                    break;
                case "1":
                    hbRecorder.setVideoEncoder("H264");
                    break;
                case "2":
                    hbRecorder.setVideoEncoder("H263");
                    break;
                case "3":
                    hbRecorder.setVideoEncoder("HEVC");
                    break;
                case "4":
                    hbRecorder.setVideoEncoder("MPEG_4_SP");
                    break;
                case "5":
                    hbRecorder.setVideoEncoder("VP8");
                    break;
            }
        }

        //NOTE - THIS MIGHT NOT BE SUPPORTED SIZES FOR YOUR DEVICE
        //Video Dimensions
        String video_resolution = prefs.getString("key_video_resolution", null);
        if (video_resolution != null) {
            switch (video_resolution) {
                case "0":
                    hbRecorder.setScreenDimensions(426, 240);
                    break;
                case "1":
                    hbRecorder.setScreenDimensions(640, 360);
                    break;
                case "2":
                    hbRecorder.setScreenDimensions(854, 480);
                    break;
                case "3":
                    hbRecorder.setScreenDimensions(1280, 720);
                    break;
                case "4":
                    hbRecorder.setScreenDimensions(1920, 1080);
                    break;
            }
        }

        //Video Frame Rate
        String video_frame_rate = prefs.getString("key_video_fps", null);
        if (video_frame_rate != null) {
            switch (video_frame_rate) {
                case "0":
                    hbRecorder.setVideoFrameRate(60);
                    break;
                case "1":
                    hbRecorder.setVideoFrameRate(50);
                    break;
                case "2":
                    hbRecorder.setVideoFrameRate(48);
                    break;
                case "3":
                    hbRecorder.setVideoFrameRate(30);
                    break;
                case "4":
                    hbRecorder.setVideoFrameRate(25);
                    break;
                case "5":
                    hbRecorder.setVideoFrameRate(24);
                    break;
            }
        }

        //Video Bitrate
        String video_bit_rate = prefs.getString("key_video_bitrate", null);
        if (video_bit_rate != null) {
            switch (video_bit_rate) {
                case "1":
                    hbRecorder.setVideoBitrate(12000000);
                    break;
                case "2":
                    hbRecorder.setVideoBitrate(8000000);
                    break;
                case "3":
                    hbRecorder.setVideoBitrate(7500000);
                    break;
                case "4":
                    hbRecorder.setVideoBitrate(5000000);
                    break;
                case "5":
                    hbRecorder.setVideoBitrate(4000000);
                    break;
                case "6":
                    hbRecorder.setVideoBitrate(2500000);
                    break;
                case "7":
                    hbRecorder.setVideoBitrate(1500000);
                    break;
                case "8":
                    hbRecorder.setVideoBitrate(1000000);
                    break;
            }
        }

        //Output Format
        String output_format = prefs.getString("key_output_format", null);
        if (output_format != null) {
            switch (output_format) {
                case "0":
                    hbRecorder.setOutputFormat("DEFAULT");
                    break;
                case "1":
                    hbRecorder.setOutputFormat("MPEG_4");
                    break;
                case "2":
                    hbRecorder.setOutputFormat("THREE_GPP");
                    break;
                case "3":
                    hbRecorder.setOutputFormat("WEBM");
                    break;
            }
        }

    }

    //Get/Set the selected settings
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void quickSettings() {
        hbRecorder.setAudioBitrate(128000);
        hbRecorder.setAudioSamplingRate(44100);
        hbRecorder.recordHDVideo(wasHDSelected);
        hbRecorder.isAudioEnabled(isAudioEnabled);
        //Customise Notification
        hbRecorder.setNotificationSmallIcon(drawable2ByteArray(R.drawable.icon));
        hbRecorder.setNotificationTitle(getString(R.string.stop_recording_notification_title));
        hbRecorder.setNotificationDescription(getString(R.string.stop_recording_notification_message));
    }

    //Check if permissions was granted
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    //Handle permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                } else {
                    hasPermissions = false;
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                }
                break;
            case PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermissions = true;
                    //Permissions was provided
                    //Start screen recording
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startRecordingScreen();
                    }
                } else {
                    hasPermissions = false;
                    showLongToast("No permission for " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //이미지, pdf를 업로드 후 내부 디바이스에 있는 파일을 삭제합니다. : 새로운 걸 만들때 그 전에 파일을 내부 디바이스에서 삭제해준다.
        //pdf, 이미지 잘 지워지는 것 확인함.

        if(imguploadlist_uri.size() != 0){
            for (int i = 0; i < imguploadlist_uri.size(); i++) {
                if (imguploadlist_uri.get(i).isSelected()) { //true인것만 체크할 것
                    //Log.d("innerfilenamechk", imguploadlist_uri.get(i).getinnerFilename());
                    // Log.d("----fileName_saved----", String.valueOf(fileName_saved));
                    File dir = oContext.getFilesDir();
                    File file_ = new File(dir, imguploadlist_uri.get(i).getinnerFilename());
                    boolean deleted = file_.delete();
                }
            }
        }
        imguploadlist_uri.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == SCREEN_RECORD_REQUEST_CODE) { //녹화 시작
                if (resultCode == RESULT_OK) {

                    //녹화 확인을 눌렀을때만 진행이 된다.

                    //Set file path or Uri depending on SDK version
                    setOutputPath();
                    //Start screen recording
                    hbRecorder.startScreenRecording(data, resultCode, this);


                    recordimg.setImageResource(R.drawable.recordon);

                    //녹화 타이머를 시작한다.
                    RecoderComplete = 0;

                    if(Threadstart == 0){ //처음에만 스레드 실행
                        RecoderTimerThread();
                        Threadstart = 1;
                    }else{ //변수로 체크하여 멈추는 것 처럼 보이게 할 것
                        RecoderTimer = 0;
                    }
                }
            }else if(requestCode == REQUEST_TAKE_PHOTO){
                if (resultCode == RESULT_OK) {
                    // Bundle로 데이터를 입력
                    Bundle extras = data.getExtras(); //카메라 촬영후 가져오는 데이터

                    // Bitmap으로 컨버전
                    Bitmap imageBitmap = (Bitmap) extras.get("data"); //bitmab으로 변경

                    //외장 저장소에 저장한다.
                    Uri uridata = getImageUri(this, imageBitmap, "img_"); //bitmap을 uri로 변경

                    //Log.d("uridata! - getCacheDir", String.valueOf(uridata));

                    //이미지 선택후 크롭화면 나오도록 설정
                    CropImage.activity(uridata).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                            .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                            .start(ConferenceRoomPage.this);
                }

            }else if(requestCode == OPEN_GALLERY){
                if (resultCode == RESULT_OK) {

                    Log.d("uridata", String.valueOf(data.getExtras()));
                    Log.d("uridata", String.valueOf(data.getData()));
                    Log.d("uridata", String.valueOf(data.getClipData()));


                    // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함

                    if(data.getData() != null){ //한개씩 업로드
                        Log.d("uridata", String.valueOf("하나!"));

                        String dataStr = String.valueOf(data.getData());
                        String copyUri = getFilePathFromURI(oContext, data.getData());

                        Log.d("uridata_ copyUri", String.valueOf(copyUri));

                        //내부 디바이스에 저장한 파일 이름만 가져온다.
                        String fileName_saved = getFileName(Uri.parse(copyUri));
                        ImgFormMulti_2 imgForm = new ImgFormMulti_2(Uri.parse(dataStr), Uri.parse(copyUri),"-", fileName_saved, true);
                        imguploadlist_uri.add(imgForm);

                    } else if(data.getClipData() != null) { //여러개 업로드 할때때
                        Log.d("uridata", String.valueOf("여러개!"));

                        ClipData clipData = data.getClipData();
                        Log.d("uridata", String.valueOf(clipData));
                        Log.i("clipdata", String.valueOf(clipData.getItemCount()));

                        if (clipData.getItemCount() > 10){
                            Toast.makeText(ConferenceRoomPage.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }   // 멀티 선택에서 하나만 선택했을 경우
                        else if (clipData.getItemCount() == 1) {
                            String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                            Log.i("2. clipdata choice", String.valueOf(clipData.getItemAt(0).getUri()));
                            Log.i("2. single choice", clipData.getItemAt(0).getUri().getPath());

                            //업로드 하기 위해 캐쉬에 저장후 uri 가져옴
                            String copyUri = getFilePathFromURI(oContext, clipData.getItemAt(0).getUri());
                            Log.i("2. ----------copyUri-----------", copyUri);

                            //내부 디바이스에 저장한 파일 이름만 가져온다.
                            String fileName_saved = getFileName(Uri.parse(copyUri));

                            //uri 전용 arraylist에 저장
                            ImgFormMulti_2 imgForm = new ImgFormMulti_2(Uri.parse(dataStr), Uri.parse(copyUri),"-", fileName_saved, true);
                            if(imguploadlist_uri.size() == 0){
                                imguploadlist_uri.add(imgForm);
                            }else{
                                imguploadlist_uri.add(0, imgForm);
                            }
                        } else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 10) {

                            //순서가 이상함. 마지막게 처음으로 나옴..
                            //순서를 반대로 뿌려준다면? ok
                            for (int i = clipData.getItemCount()-1; i >= 0; i--) {
                                Log.d("uridata", String.valueOf("두개이상 !"));
                                Log.i("3. single choice", String.valueOf(clipData.getItemAt(i).getUri()));

                                String dataStr = String.valueOf(clipData.getItemAt(i).getUri());
                                String copyUri = getFilePathFromURI(oContext, clipData.getItemAt(i).getUri());

                                //내부 디바이스에 저장한 파일 이름만 가져온다.
                                String fileName_saved = getFileName(Uri.parse(copyUri));

                                //uri 전용 arraylist에 저장
                                ImgFormMulti_2 imgForm = new ImgFormMulti_2(Uri.parse(dataStr),Uri.parse(copyUri),"-", fileName_saved, true);
                                if(imguploadlist_uri.size() == 0){
                                    imguploadlist_uri.add(imgForm);
                                }else if(imguploadlist_uri.size() < 10){
                                    imguploadlist_uri.add(0, imgForm);
                                }
                            }
                        }
                    }
                    Documentdatalist.clear();
                    //파라미터 정보 : 2. 이미지, 1. 새 페이지 등록
                    saveconferencedocument(2, 1);


                    //디바이스 캐시 삭제하기 : 전부 지웟더니 아고라에서 연결이 끊김...
                    //clearCache();

                    //data/data/패키지/경로에 있는 파일 삭제해주기.
                    //oContext.getFilesDir();
                }
            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){ //크롭
                Log.d("crop!!1", String.valueOf(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE));
                Log.d("crop!!2", String.valueOf( CropImage.getActivityResult(data)));

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {

                    File fileCacheDir = getCacheDir();
                    String getCacheDir = fileCacheDir.getPath();

                    Log.d("crop! - getCacheDir", getCacheDir);


                    Log.d("crop!!3", String.valueOf(result.getUri()));
                    Log.d("사진에서 선택한 사진 경로", result.getUri().getPath());

                    //내부 디바이스에 저장한 파일 이름만 가져온다.
                    String fileName_saved = getFileName(result.getUri());

                    //uri 전용 arraylist에 저장
                    ImgFormMulti_2 imgForm = new ImgFormMulti_2(result.getUri(),result.getUri(),"-", fileName_saved, true);
                    if(imguploadlist_uri.size() == 0){
                        imguploadlist_uri.add(imgForm);
                    }else if(imguploadlist_uri.size() < 10){
                        imguploadlist_uri.add(0, imgForm);
                    }

                    //ImageSend();
                    Documentdatalist.clear();
                    //파라미터 정보 : 2. 이미지, 1. 새 페이지 등록
                    saveconferencedocument(2, 1);

                }
            }else if(requestCode == SELECT_FILE) { //문서 선택 후 서버 전송
                if (resultCode == RESULT_OK) {

                    //가져온 파일 uri
                    Uri uri = data.getData();
                    Log.d("----uri----", String.valueOf(uri));
                    //content://com.android.externalstorage.documents/document/primary%3ADCIM%2Ftest.pdf

                    //외부저장소에 있던 파일을 내부 저장소에 pdf를 저장하는 법
                    String fileName = getFileName(uri);
                    if (!TextUtils.isEmpty(fileName)) {
                        File copyFile = new File(oContext.getFilesDir()+ File.separator + fileName);
                        copy(oContext, uri, copyFile);
                        Log.d("----getAbsolutePath----", String.valueOf(copyFile.getAbsolutePath()));
                        // /data/user/0/com.example.hometeacher/files/test.pdf
                        Log.d("----fileName----", String.valueOf(fileName));
                    }

                    try {
                        PDFtoJPG(fileName); //pdf를 jpg로 변환
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Documentdatalist.clear();
                    //파라미터 정보 : 2. 이미지, 1. 새 페이지 등록
                    saveconferencedocument(2, 1); //pdf 저장
                }
            }
        }
    }


    //pdf 파일을 jpg 파일로 변환한다.
    public void PDFtoJPG(String fileName) throws Exception
    {
        String filename_copy = fileName.split("\\.")[0];
        Log.d("filename_copy", filename_copy);

        //내부 저장소에 있는 데이터만 수정이 가능.
        File file = new File(getFilesDir() + "/" + fileName);

        //pdfbox를 이용해서 pdf파일을 이미지로 생성한다.
        PDDocument document = PDDocument.load(file);

        int pageCount = document.getNumberOfPages(); //페이지 수 생성

        PDFRenderer pdfRenderer = new PDFRenderer(document); //pdf 랜더링
        Log.d("----pageCount----", String.valueOf(pageCount));
        Log.d("----pdfRenderer----", String.valueOf(pdfRenderer));


        PDFBoxResourceLoader.init(oContext); //에러시 추가

        for(int page = 0;page<pageCount;page++){
            // 페이지를 JPG파일로 저장
            Bitmap imagebitmap = pdfRenderer.renderImageWithDPI(page, 100, ImageType.RGB);

            //외부저장소에 이미지를 저장한다.
            Uri imageuri = getImageUri(oContext, imagebitmap, "img_"+page); // bitmap으로 uri 변경

            //내부저장소에 이미지를 저장한다.
            String copyUri = getFilePathFromURI(oContext, imageuri); //내부 저장소에 이미지가 캐시로 저장되어 있어야 서버로 전송할때 문제가 없음.

            Log.d("----imageuri----", String.valueOf(imageuri));
            Log.d("----result----", String.valueOf(copyUri));

            //내부 디바이스에 저장한 파일 이름만 가져온다.
            String fileName_saved = getFileName(imageuri);

            //uri 전용 arraylist에 저장
            ImgFormMulti_2 imgForm = new ImgFormMulti_2(Uri.parse(copyUri),Uri.parse(copyUri),"-", fileName_saved, true);
            imguploadlist_uri.add(imgForm);

            //사진첩(외부 저장소)에 저장된 이미지를 삭제한다.
            oContext.getContentResolver().delete(imageuri, null, null);
        }


        if (document != null) {
            document.close();
        }
    }


    private void clearCache() {
        final File cacheDirFile = this.getCacheDir();
        if (null != cacheDirFile && cacheDirFile.isDirectory()) {
            clearSubCacheFiles(cacheDirFile);
        }
    }
    private void clearSubCacheFiles(File cacheDirFile) {
        if (null == cacheDirFile || cacheDirFile.isFile()) {
            return;
        }
        for (File cacheFile : cacheDirFile.listFiles()) {
            if (cacheFile.isFile()) {
                if (cacheFile.exists()) {
                    cacheFile.delete();
                }
            } else {
                clearSubCacheFiles(cacheFile);
            }
        }
    }

//    public static String getCurrentTime(String timeFormat){
//        return new SimpleDateFormat(timeFormat).format(System.currentTimeMillis());
//    }

    //pdf를 뷰어로 열때 사용한다. !!! - 여기도 내부 파일만 사용 가능
    void openPdf(String fileName) {
        copyFileFromAssets(fileName);

        /** PDF reader code */
        File file = new File(getFilesDir() + "/" + fileName);
        //File file = new File(fileName);

        Uri uri = null;
        if(!fileName.startsWith("http")) {
            uri = FileProvider.getUriForFile(this, "hometeacherpdf", file);
        } else {
            uri = Uri.parse(fileName);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFileFromAssets(String fileName) {
        AssetManager assetManager = this.getAssets();

        //앱 내의 파일폴더에 저장
        File cacheFile = new File( getFilesDir() + "/" + fileName );
        InputStream in = null;
        OutputStream out = null;
        try {
            if ( cacheFile.exists() ) {
                return;
            } else {
                in = assetManager.open(fileName);
                out = new FileOutputStream(cacheFile);
                copyFile(in, out);

                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
    //pdf를 뷰어로 열때 사용한다. !!!








    //bitmap으로 uri로 변경하는 함수 - 외부저장소에 저장후 그 경로를 통해 uri를 가져오는 것
    private Uri getImageUri(Context context, Bitmap inImage, String filename) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, filename, null); //외부 파일에 저장한다.
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

    //For Android 10> we will pass a Uri to HBRecorder
    //This is not necessary - You can still use getExternalStoragePublicDirectory
    //But then you will have to add android:requestLegacyExternalStorage="true" in your Manifest
    //IT IS IMPORTANT TO SET THE FILE NAME THE SAME AS THE NAME YOU USE FOR TITLE AND DISPLAY_NAME
    ContentResolver resolver;
    ContentValues contentValues;
    Uri mUri;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setOutputPath() {
        String filename = generateFileName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver = getContentResolver();
            contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "HBRecorder");
            contentValues.put(MediaStore.Video.Media.TITLE, filename);
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            mUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            //FILE NAME SHOULD BE THE SAME
            hbRecorder.setFileName(filename);
            hbRecorder.setOutputUri(mUri);
        }else{
            createFolder();
            hbRecorder.setOutputPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) +"/HBRecorder");
        }
    }

    //Generate a timestamp to be used as a file name
    private String generateFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis());
        return formatter.format(curDate).replace(" ", "");
    }

    //Show Toast
    private void showLongToast(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //drawable to byte[]
    private byte[] drawable2ByteArray(@DrawableRes int drawableId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    //framelayout로 페이지 변경
    private void changeView(int index) {
        // LayoutInflater 초기화.
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout frame = (FrameLayout) findViewById(R.id.framebox) ;
        if (frame.getChildCount() > 0) {
            // FrameLayout에서 뷰 삭제.
            frame.removeViewAt(0);
        }

        // XML에 작성된 레이아웃을 View 객체로 변환.
        View view = null ;
        switch (index) {
            case 0 :
                view = inflater.inflate(R.layout.conference_screenview, frame, false) ; //화면보기
                break ;
            case 1 :
                view = inflater.inflate(R.layout.conference_whiteboardview, frame, false) ; //화이트보드
                break ;
            case 2 :
                view = inflater.inflate(R.layout.conference_participantview, frame, false) ; //참여자보기
                break ;
        }

        // FrameLayout에 뷰 추가.
        if (view != null) {
            frame.addView(view) ;
        }


        changepageview(index);
    }

    //페이지 변경시 필요한 데이터
    public void changepageview(int index){
        switch (index) {
            case 0 : //화면보기

                //입장후 카메라 실행
                // If all the permissions are granted, initialize the RtcEngine object and join a channel.
                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
                    initializeAndJoinChannel(); //내화면 생성, 화상 시작
                }

                //영상 리스트와 참여자보기 리스트를 다시 불러온다.
                if(roomtouserlist.size()>0){
                    videoremotecovertype = 1; //1. 영상보기 클릭시, 2. local change, 3. remote change

                    totaltouserlist.clear();
                    getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴
                }

                //영상보기에 있을때 상대방이 화이트보드를 작성하면 보이지 않는 문제를 해결한다. 미리 공간을 생성해준다.
                Documentdatalist.clear();
                saveconferencedocument(1, 0); //1. 새페이지 서버에 저장한다.
                drawCanvas = new DrawCanvas(this); //캔버스 클래스 선언

                //처음 접근했을때 빈값으로 되어있을 것이다. 빈값이면 0을 추가해준다. 빈값이 아니면 아무것도 해주지않는다.
                if(documentclicklist.size() == 0){
                    documentclicklist.add(0);
                }

                break ;
            case 1 ://화이트보드

                //처음 입장할때는 페이지가 없을때만 한개를 등록하고 이미 한개 이상 존재하면 저장하지 않는다.
                Documentdatalist.clear();
                saveconferencedocument(1, 0); //1. 새페이지 서버에 저장한다.

                //클릭시 1씩 증가 처음을 가리기 위함.
                WhiteboardFirstNum++;

                findId();
                canvasLinear.addView(drawCanvas);
                setOnClickListener();

                documentbox = (LinearLayout) findViewById(R.id.documentbox);

                //전체 선택, 해제 버튼
                totalcheckonofflinear = (LinearLayout) findViewById(R.id.totalcheckonofflinear);
                totalcheckonofflinear.setVisibility(View.GONE);
                totalcheckonoffbtn = (Button) findViewById(R.id.totalcheckonoffbtn);

                //처음 입장시
                if(totalselect == 1) {
                    totalcheckonoffbtn.setText("전체해제");
                }else{
                    totalcheckonoffbtn.setText("전체선택");
                }

                addbtn = (ImageView) findViewById(R.id.addbtn);
                editbtn = (ImageView) findViewById(R.id.editbtn);
                whiteboardbtn = (ImageView) findViewById(R.id.whiteboardbtn);
                drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                //drawlayout 열기 닫기 시 실행
                DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        fbPen.setEnabled(false);
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        fbPen.setEnabled(true);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                };
                drawer.setDrawerListener(myDrawerListener);


                //추가버튼
                addbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edittype  == 0){ //기본모드
                            //추가를 누르면 새문서,이미지,문서 로 메뉴 띄움
                            PopupMenu poup = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                                poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
                            }
                            else {
                                poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
                            }

                            getMenuInflater().inflate(R.menu.documentaddmenu, poup.getMenu());

                            poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    String popup_tittle = item.getTitle().toString();

                                    if(popup_tittle.contains("새페이지")){

                                        //클릭시 1씩 증가 처음을 가리기 위함.
                                        WhiteboardFirstNum++;

                                        Documentdatalist.clear();
                                        saveconferencedocument(1, 1); //1. 새페이지 서버에 저장한다.

                                    }else if(popup_tittle.contains("이미지")){

                                        //클릭시 1씩 증가 처음을 가리기 위함.
                                        WhiteboardFirstNum++;

                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(oContext);

                                        builder.setTitle("문서 사진 업로드");

                                        builder.setItems(R.array.imgckarray, new DialogInterface.OnClickListener(){
                                            @SuppressLint("IntentReset")
                                            @Override
                                            public void onClick(DialogInterface dialog, int pos)
                                            {
                                                if(pos == 0){ //사진첩
                                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                    intent.setType("image/*");
                                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                    //intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), OPEN_GALLERY);
                                                }else{ //카메라
                                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename));
                                                    oActivity_conferenceroom.startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
                                                }
                                            }
                                        });
                                        android.app.AlertDialog alertDialog = builder.create();

                                        alertDialog.show();

                                    }else if(popup_tittle.contains("문서")){
                                        //파일이 위치한 폴더를 지정

                                        //클릭시 1씩 증가 처음을 가리기 위함.
                                        WhiteboardFirstNum++;

//                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS ).getPath() + "/하위폴드명칭/");
//
//
//
//                                    //안드로이드 버전 체크 (Android 8 버전이하와 이상으로 구분)
//                                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
//                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                        Uri uri = Uri.fromFile(file);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//
//                                        try {
//                                            startActivityForResult(Intent.createChooser(intent, "File Select"), SELECT_FILE);
//                                        } catch (ActivityNotFoundException ex) {
//                                            //Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("application/pdf");
                                        startActivityForResult(intent, SELECT_FILE);
                                        // }
                                    }


                                    return false;
                                }
                            });
                            poup.show(); // 메뉴를 띄우기
                        }else if(edittype == 1){ //편집모드 - 휴지통 버튼

                            //삭제할 문서가 있으면
                            if(DeleteDocumentlist.size() > 0){
                                //받아온 데이터를 삭제하는 부분
                                deleteconferencedocument(DeleteDocumentlist);

                                totalcheckonofflinear.setVisibility(View.GONE); //전체 선택 or 전체 해제
                                editbtn.setImageResource(R.drawable.white_chkbtn);
                                addbtn.setImageResource(R.drawable.white_addbtn_2);
                                addbtn.setBackgroundResource(R.drawable.btndesign2);
                                edittype = 0;


                                Log.d("--DeleteDocumentlist--",String.valueOf(DeleteDocumentlist));
                                Log.d("--DeleteDocumentCountlist--",String.valueOf(DeleteDocumentCountlist));
                            }else{
                                Toast.makeText(GlobalClass, "삭제할 문서를 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                //편집버튼
                editbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //편집은 문서를 편집모드로 변경한다.
                        //그리고 편집 아이콘은 휴지통 아이콘으로 변경한다.

                        if(edittype == 0){ //편집 모드로 전환
                            totalcheckonofflinear.setVisibility(View.VISIBLE); //전체 선택 or 전체 해제
                            editbtn.setImageResource(R.drawable.white_close);
                            addbtn.setImageResource(R.drawable.white_trashbox);
                            addbtn.setBackgroundResource(R.drawable.btndesign4);

                            for(int i = 0;i < Documentdatalist.size();i++){
                                Documentdatalist.get(i).setChecked(false);
                            }

                            edittype = 1;
                            DeleteDocumentlist.clear();
                            DeleteDocumentCountlist.clear();

                            MakeDocumentrecycle(Documentdatalist);

                            //전체 삭제 or 해제 나타나게하는 조건문
                            if(DeleteDocumentlist.size() > 0){ //삭제할 값이 한개라도 있을때
                                if(DeleteDocumentlist.size() == Documentdatalist.size()){ //삭제할 문서의 갯수가 전체 문서의 갯수와 같으면 = 전체 선택이 되어 있을때
                                    totalcheckonoffbtn.setText("전체해제");
                                    totalselect = 1;
                                }else{
                                    totalcheckonoffbtn.setText("전체선택");
                                    totalselect = 0;
                                }
                            }else{ //전체 해제가 되어 있을때
                                totalcheckonoffbtn.setText("전체선택");
                                totalselect = 0;
                            }


                        }else{ //기본 모드로 전환
                            totalcheckonofflinear.setVisibility(View.GONE); //전체 선택 or 전체 해제
                            editbtn.setImageResource(R.drawable.white_chkbtn);
                            addbtn.setImageResource(R.drawable.white_addbtn_2);
                            addbtn.setBackgroundResource(R.drawable.btndesign2);


                            edittype = 0;

                            //삭제할 데이터 초기화 하기 - 삭제 안하고 전환만
                            DeleteDocumentlist.clear();
                            DeleteDocumentCountlist.clear();
                            deleteconferencedocument(DeleteDocumentlist);
                        }


                        Log.d("--DeleteDocumentlist--",String.valueOf(DeleteDocumentlist));
                        Log.d("--DeleteDocumentCountlist--",String.valueOf(DeleteDocumentCountlist));
                    }
                });

                //화이트보드 미리보기 리스트 열기
                whiteboardbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                            drawer.openDrawer(Gravity.LEFT) ;

                            edittype = 0;

                            totalcheckonofflinear.setVisibility(View.GONE); //전체 선택 or 전체 해제
                            editbtn.setImageResource(R.drawable.white_chkbtn);
                            addbtn.setImageResource(R.drawable.white_addbtn_2);
                            addbtn.setBackgroundResource(R.drawable.btndesign2);

                            DeleteDocumentlist.clear();
                            DeleteDocumentCountlist.clear();

                            MakeDocumentrecycle(Documentdatalist);
                        }


                        Log.d("--DeleteDocumentlist--",String.valueOf(DeleteDocumentlist));
                        Log.d("--DeleteDocumentCountlist--",String.valueOf(DeleteDocumentCountlist));
                    }
                });



                //전체 선택 or 전체 해제 버튼
                totalcheckonoffbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //한개라도 선택되어잇는 경우 -> 전체 선택하기
                        //전체 선택되어있는 경우 -> 전체 해제하기
                        //전체 해제되어있는 경우 -> 전체 선택하기

                        if(totalselect == 1){ // 1
                           // totalcheckonoffbtn.setText("전체선택");
                            for(int i = 0;i < Documentdatalist.size();i++){
                                Documentdatalist.get(i).setChecked(false);

                                DeleteDocumentlist.clear();
                                DeleteDocumentCountlist.clear();
                            }
                        }else{ // 0
                           // totalcheckonoffbtn.setText("전체해제");
                            for(int i = 0;i < Documentdatalist.size();i++){
                                Documentdatalist.get(i).setChecked(true);

                                //삭제 리스트에 값이 없는 데이터만 저장하기
                                int existdelete = 0;
                                for(int d = 0; d<DeleteDocumentlist.size();d++){
                                    if(Documentdatalist.get(i).getidx().equals(DeleteDocumentlist.get(d))){ //이미 삭제 리스트에 있음
                                        existdelete = 1;
                                    }
                                }
                                if(existdelete != 1){
                                    DeleteDocumentlist.add(Documentdatalist.get(i).getidx());
                                    DeleteDocumentCountlist.add(String.valueOf(i));
                                }
                            }
                        }
                        //isSelected
                        MakeDocumentrecycle(Documentdatalist);

                        Log.d("--DeleteDocumentlist--",String.valueOf(DeleteDocumentlist));
                        Log.d("--DeleteDocumentCountlist--",String.valueOf(DeleteDocumentCountlist));

                        //전체 삭제 or 해제 나타나게하는 조건문
                        if(DeleteDocumentlist.size() > 0){ //삭제할 값이 한개라도 있을때
                            if(DeleteDocumentlist.size() == Documentdatalist.size()){ //삭제할 문서의 갯수가 전체 문서의 갯수와 같으면 = 전체 선택이 되어 있을때
                                totalcheckonoffbtn.setText("전체해제");
                                totalselect = 1;
                            }else{
                                totalcheckonoffbtn.setText("전체선택");
                                totalselect = 0;
                            }
                        }else{ //전체 해제가 되어 있을때
                            totalcheckonoffbtn.setText("전체선택");
                            totalselect = 0;
                        }
                    }
                });




                break ;
            case 2 ://참여자보기

                //서버에서 전체 유저 리스트를 불러온다.
                totaltouserlist.clear();
                getjoinuserlist_total(0,0); //전체 참여자리스트를 불러옴

                break;
        }
    }


    //참여자 리스트에 정보를 보여준다.
    public void Makejoinuserrecycle(ArrayList<conferenceUserForm> totaltouserlist) {
        Log.d("Makejoinuserrecycle", String.valueOf(totaltouserlist));

        titlename = (TextView) findViewById(R.id.titlename);
        titlename.setText(roomname+" : 참여자("+totaltouserlist.size()+")");

        //참여자 리스트를 리사이클러뷰로 생성
        participantRecyclerView = (RecyclerView) findViewById(R.id.participantRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        ConferenceParticipantlistRecyclerAdapter oConferenceParticipantlistRecyclerAdapter = new ConferenceParticipantlistRecyclerAdapter(getApplicationContext()); //내가만든 어댑터 선언
        participantRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        //선택된 유저를 보낼 것. AddUserlist
        //oConferenceParticipantlistRecyclerAdapter.setdata(mRtcEngine);
        oConferenceParticipantlistRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oConferenceParticipantlistRecyclerAdapter.setRecycleList(totaltouserlist); //arraylist 연결
        participantRecyclerView.setAdapter(oConferenceParticipantlistRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

    }



    //문서의 리스트를 생성한다.
    public void MakeDocumentrecycle(ArrayList<ConferenceDocumentForm> docuementlist) {
        Log.d("MakeDocumentrecycle", String.valueOf(docuementlist));

        //문서 리스트를 리사이클러뷰로 생성
        documentRecyclerView = (RecyclerView) findViewById(R.id.documentRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(oContext); //그리드 매니저 선언
        oConferenceDocumentlistRecyclerAdapter = new ConferenceDocumentlistRecyclerAdapter(getApplicationContext(), oActivity_conferenceroom); //내가만든 어댑터 선언
        documentRecyclerView.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        documentRecyclerView.scrollToPosition(SelectDocumentCount);
        documentRecyclerView.smoothScrollToPosition(SelectDocumentCount); // 부드럽게

        //선택된 유저를 보낼 것. AddUserlist
        oConferenceDocumentlistRecyclerAdapter.setdata(SelectDocumentIdx, edittype, drawCommandList_Total_STEP);
        oConferenceDocumentlistRecyclerAdapter.setSessionval(Sessionlist); //arraylist 연결
        oConferenceDocumentlistRecyclerAdapter.setRecycleList(docuementlist); //arraylist 연결
        documentRecyclerView.setAdapter(oConferenceDocumentlistRecyclerAdapter); //리사이클러뷰 위치에 어답터 세팅

        //문서클릭시 반응
        oConferenceDocumentlistRecyclerAdapter.setOnItemClickListener(new ConferenceDocumentlistRecyclerAdapter.OnItemClickListener() {
            @Override
            // 문서 선택 1, 편집에서 체크박스 체크 2, 편집에서 문서 박스 클릭 3
            public void onItemClick(View v, int position, ArrayList<ConferenceDocumentForm> list, int type, CheckBox documentcheckbox) throws JSONException {


                if(type == 1){ //문서 선택
                    //저장해놓고 새문서로 변경
                    SelectDocumentCount = position; //선택한 문서 번호를 저장한다. -> 문서의 고유값으로 변경
                    SelectDocumentIdx = list.get(position).getidx();

                    Log.d("-----@@@@@documentclicklist@@@@@@-----", String.valueOf(documentclicklist));


                    //두개까지만 저장이 되며 두개째가 되면 마지막 position의 데이터를 변경한다.
                    //첫번째 지우고 새걸 저장.
                    if(documentclicklist.size() == 2){ // 0 1
                        documentclicklist.remove(0);
                        documentclicklist.add(position);
                    }else{
                        documentclicklist.add(position);
                    }

                    Log.d("-----@@@@@documentclicklist@@@@@@-----", String.valueOf(documentclicklist));


                    //선택된 문서를 새로고침 한다.
                    oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist, drawCommandList_Total_STEP);


                    drawCommandList.clear();

                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                        if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                            ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                            drawCommandList.addAll(totalpenlist);
                        }
                    }

                    //이미지 있을때만
                    if(!String.valueOf(list.get(position).getbasicuri()).equals("")){

                        String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(list.get(position).getbasicuri())+String.valueOf(list.get(position).getsrc());

                        //피카소 api를 통해 bitmap으로 변경한다.
                        Picasso.get()
                                .load(imguri)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                        /* Save the bitmap or do something with it here */

                                        //Set it in the ImageView
                                       // theView.setImageBitmap(bitmap);
                                        drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                        drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                        drawCanvas.invalidate();//화면을 갱신하다.
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }else{
                        drawCanvas.image_access = 0;
                        drawCanvas.invalidate();//화면을 갱신하다.
                    }


                }else if(type == 2){ // 편집에서 체크박스 체크 2
                    Log.d("--position--",String.valueOf(position));
                    Log.d("--position--",String.valueOf(list.get(position).getidx()));
                    Log.d("--position--",String.valueOf(list.get(position).isCheckeded()));

                    if(list.get(position).isCheckeded()){ //true 선택
                       // if(documentcheckbox.isChecked()){ //true 선택
                        //선택시 저장
                        //삭제할 데이터 담아서 지우기. -> 삭제 idx만 담지 말고 해당 순서도 담자.
                        DeleteDocumentlist.add(String.valueOf(list.get(position).getidx())); //삭제할 idx 담는 공간 (서버로 전송)
                        DeleteDocumentCountlist.add(String.valueOf(position)); //삭제한 위치 담는 공간
                    }else{ //false 해제
                        //해제시 삭제
                        DeleteDocumentlist.remove(String.valueOf(list.get(position).getidx()));
                        DeleteDocumentCountlist.remove(String.valueOf(position));
                    }
                    Log.d("--DeleteDocumentlist--",String.valueOf(DeleteDocumentlist));
                    Log.d("--DeleteDocumentCountlist--",String.valueOf(DeleteDocumentCountlist));


                    //전체 삭제 or 해제 나타나게하는 조건문
                    if(DeleteDocumentlist.size() > 0){ //삭제할 값이 한개라도 있을때
                        if(DeleteDocumentlist.size() == Documentdatalist.size()){ //삭제할 문서의 갯수가 전체 문서의 갯수와 같으면 = 전체 선택이 되어 있을때
                            totalcheckonoffbtn.setText("전체해제");
                            totalselect = 1;
                        }else{
                            totalcheckonoffbtn.setText("전체선택");
                            totalselect = 0;
                        }
                    }else{ //전체 해제가 되어 있을때
                        totalcheckonoffbtn.setText("전체선택");
                        totalselect = 0;
                    }


                }else if(type == 3){// 편집에서 문서 박스 클릭시
                    Log.d("--position--",String.valueOf(position));
                    Log.d("--position--",String.valueOf(list.get(position).getidx()));
                    Log.d("--position--",String.valueOf(list.get(position).isCheckeded()));

                    if(list.get(position).isCheckeded()){ //true 선택
                        // if(documentcheckbox.isChecked()){ //true 선택
                        //선택시 저장
                        //삭제할 데이터 담아서 지우기. -> 삭제 idx만 담지 말고 해당 순서도 담자.
                        DeleteDocumentlist.add(String.valueOf(list.get(position).getidx())); //삭제할 idx 담는 공간 (서버로 전송)
                        DeleteDocumentCountlist.add(String.valueOf(position)); //삭제한 위치 담는 공간
                    }else{ //false 해제
                        //해제시 삭제
                        DeleteDocumentlist.remove(String.valueOf(list.get(position).getidx()));
                        DeleteDocumentCountlist.remove(String.valueOf(position));
                    }
                    Log.d("--DeleteDocumentlist--",String.valueOf(DeleteDocumentlist));
                    Log.d("--DeleteDocumentCountlist--",String.valueOf(DeleteDocumentCountlist));


                    //전체 삭제 or 해제 나타나게하는 조건문
                    if(DeleteDocumentlist.size() > 0){ //삭제할 값이 한개라도 있을때
                        if(DeleteDocumentlist.size() == Documentdatalist.size()){ //삭제할 문서의 갯수가 전체 문서의 갯수와 같으면 = 전체 선택이 되어 있을때
                            totalcheckonoffbtn.setText("전체해제");
                            totalselect = 1;
                        }else{
                            totalcheckonoffbtn.setText("전체선택");
                            totalselect = 0;
                        }
                    }else{ //전체 해제가 되어 있을때
                        totalcheckonoffbtn.setText("전체선택");
                        totalselect = 0;
                    }





                    SelectDocumentCount = position; //선택한 문서 번호를 저장한다. -> 문서의 고유값으로 변경
                    SelectDocumentIdx = list.get(position).getidx();

                    Log.d("-----@@@@@documentclicklist@@@@@@-----", String.valueOf(documentclicklist));


                    //두개까지만 저장이 되며 두개째가 되면 마지막 position의 데이터를 변경한다.
                    //첫번째 지우고 새걸 저장.
                    if(documentclicklist.size() == 2){ // 0 1
                        documentclicklist.remove(0);
                        documentclicklist.add(position);
                    }else{
                        documentclicklist.add(position);
                    }

                    Log.d("-----@@@@@documentclicklist@@@@@@-----", String.valueOf(documentclicklist));


                    //선택된 문서를 새로고침 한다.
                    oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist, drawCommandList_Total_STEP);


                    drawCommandList.clear();

                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                        if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                            ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                            drawCommandList.addAll(totalpenlist);
                        }
                    }

                    //이미지 있을때만
                    if(!String.valueOf(list.get(position).getbasicuri()).equals("")){

                        String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(list.get(position).getbasicuri())+String.valueOf(list.get(position).getsrc());

                        //피카소 api를 통해 bitmap으로 변경한다.
                        Picasso.get()
                                .load(imguri)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                        /* Save the bitmap or do something with it here */

                                        //Set it in the ImageView
                                        // theView.setImageBitmap(bitmap);
                                        drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                        drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                        drawCanvas.invalidate();//화면을 갱신하다.
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }else{
                        drawCanvas.image_access = 0;
                        drawCanvas.invalidate();//화면을 갱신하다.
                    }
                }
            }
        });
    }


    /**
     * jhChoi - 201124
     * View Id를 셋팅합니다.
     */
    private void findId() {
        canvasLinear = findViewById(R.id.lo_canvas);
        drawCanvas = new DrawCanvas(this);

        fbPen = findViewById(R.id.penbtn);
        fbcircle = findViewById(R.id.circlebtn);
        fbEraser = findViewById(R.id.eraserbtn);
        fbbackwardsgobtn = findViewById(R.id.backwardsgobtn);
        fbforwardgobtn = findViewById(R.id.forwardgobtn);
        fbresetbtn = findViewById(R.id.resetbtn);

        //nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
       // progressBar = (ProgressBar) findViewById(R.id.progress_bar);
       // progressBar.setVisibility(View.GONE);

        //스크롤뷰의 밑바당에 부딛힐때마다 프로그레스바가 돌고 데이터를 새로 가져온다. 열개씩 가져올 것.
        /*nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {

                    //처음 입장할때만 10개까지만 가져오고 페이징으로 더 가져온다.
                    //그 이후에 가져올때는 페이징 되었던 부분까지 가져와야한다.

                    pagenum++;
                    progressBar.setVisibility(View.VISIBLE);

                    int offsetnum = limitnum*pagenum;

                    //Log.d("카테고리 선택 리스트 - db", String.valueOf(SearchSubcategorey.isEmpty()));


                    Log.d("pagenum-----", String.valueOf(limitnum));
                    Log.d("pagenum-----", String.valueOf(offsetnum));
                    Log.d("pagenum-----", String.valueOf(pagenum));

                    //문서 불러오기
                    GetDocuemntlist(limitnum, offsetnum);

                }
           }
        });
*/

        //처음 접근시 기본 세팅이다.
//        fbPen.setImageResource(R.drawable.pen_ck);
//        fbcircle.setImageResource(R.drawable.circledraw);
//        fbEraser.setImageResource(R.drawable.eraser);

        //Toast.makeText(GlobalClass, String.valueOf("test1"), Toast.LENGTH_SHORT).show(); WhiteboardFirstNum
    }

    /**
     * jhChoi - 201124
     * OnClickListener Setting
     */
    //int penuse = 0;
    //int eraseruse = 0;
    private void setOnClickListener() {
       //펜으로 변경
       fbPen.setOnClickListener((v)->{

           PopupMenu poup = null;
           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
               poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
           }
           else {
               poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
           }

           getMenuInflater().inflate(R.menu.penmenu, poup.getMenu());

           poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   String popup_tittle = item.getTitle().toString();

                   if(popup_tittle.contains("검정")){
                       drawCanvas.changeToolnColor(DrawCanvas.MODE_PEN, DrawCanvas.MODE_BLACK);
                   }else if(popup_tittle.contains("빨강")){
                       drawCanvas.changeToolnColor(DrawCanvas.MODE_PEN, DrawCanvas.MODE_RED);
                   }else if(popup_tittle.contains("파랑")){
                       drawCanvas.changeToolnColor(DrawCanvas.MODE_PEN, DrawCanvas.MODE_BLUE);
                   }else if(popup_tittle.contains("노랑")){
                       drawCanvas.changeToolnColor(DrawCanvas.MODE_PEN, DrawCanvas.MODE_YELLO);
                   }else if(popup_tittle.contains("초록")){
                       drawCanvas.changeToolnColor(DrawCanvas.MODE_PEN, DrawCanvas.MODE_GREEN);
                   }

                   return false;
               }
           });
           poup.show(); // 메뉴를 띄우기
       });

        //원으로 변경
        fbcircle.setOnClickListener((v)->{
            drawCanvas.changeToolnColor(DrawCanvas.MODE_CIRCLE, DrawCanvas.MODE_BLACK);
        });

        //지우개로 변경
       fbEraser.setOnClickListener((v)->{
           PopupMenu poup = null;
           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
               poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
           }
           else {
               poup = new PopupMenu(ConferenceRoomPage.this, v); //TODO 일반 사용
           }

           getMenuInflater().inflate(R.menu.erasermenu, poup.getMenu());

           poup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   String popup_tittle = item.getTitle().toString();

                   if(popup_tittle.contains("선지우개")){
                       drawCanvas.changeToolnColor(DrawCanvas.MODE_ERASER, DrawCanvas.MODE_WHITE);
                   }else if(popup_tittle.contains("전체지우개")){ //자기것만 지우기
                       drawCanvas.usetoolmode = 6; //내거 전부 삭제

                       drawCanvas.pageinit();//문서 초기화한다.
                       drawCanvas.invalidate();//화면을 갱신하다.

                       //전에 사용한 도구와 색상으로 변경
                       drawCanvas.changeToolnColor(beforetool_total, beforecolor_total);
                   }
                   return false;
               }
           });
           poup.show(); // 메뉴를 띄우기
       });

//        //저장
//        fbSave.setOnClickListener((v)->{
//            drawCanvas.invalidate();//문서 초기화한다.
//            Bitmap saveBitmap = drawCanvas.getCurrentCanvas(); //비트맵으로 변경
//            CanvasIO.saveBitmap(this, saveBitmap); //canvas에 해당 문서 저장한다.
//        });
//
//        //새문서 열기 - 저장한건 사라지지 않음
//        fbOpen.setOnClickListener((v)->{
//            drawCanvas.init();//문서 초기화한다.
//            drawCanvas.loadDrawImage = CanvasIO.openBitmap(this); //저장한 내용을 불러옴
//            drawCanvas.invalidate();//화면을 갱신하다.
//        });
//
//        //문서 리셋
//        fbReset.setOnClickListener((v)->{
//            //drawCanvas.init();//문서 초기화한다.
//            //drawCanvas.loadDrawImage = CanvasIO.openBitmap(this); //저장한 내용을 불러옴
//            drawCanvas.invalidate();//화면을 갱신하다.
//        });

        /**
        *뒤로가기
        * 뒤로가기 할때는 내건 전체에서 지우고 rest에 넣는다. 상대방은 지울 카운트 순서를 전체 지운다.
        **/
        fbbackwardsgobtn.setOnClickListener((v)->{
            drawCanvas.usetoolmode = 2;

                for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                    if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                        ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                        ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                        int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                        int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                        if(drawcount > 0) {
                            for (int p = 0; p < totalpenlist.size(); p++) {
                                if (totalpenlist.get(p).getwriteuid().equals(myuid)) {
                                    if (totalpenlist.get(p).getdrawcount() == drawcount) {
                                        restpenlist.add(totalpenlist.get(p)); //나머지 리스트에 저장
                                        totalpenlist.remove(totalpenlist.get(p)); //전체 리스트에 삭제제
//                                        drawCommandList_Total_STEP.get(t).setTotalPen();
                                        p--;
                                    }
                                }
                            }
                            drawCommandList.clear();
                            drawCommandList.addAll(totalpenlist);


                            Log.d("drawcount _ backtest",String.valueOf(drawcount));
                            Log.d("drawcountFinal _ backtest",String.valueOf(drawcountFinal));

//                            for (int p = 0; p < totalpenlist.size(); p++) {
//                                //Log.d("totalpenlist_getdrawcount",String.valueOf(totalpenlist.get(p).getdrawcount()));
//                            }
//                            for (int p = 0; p < restpenlist.size(); p++) {
//                                //Log.d("restpenlist_getdrawcount",String.valueOf(restpenlist.get(p).getdrawcount()));
//                            }

                            //카운트 숫자만 전송하여 해당 순서의 판서 좌표만을 삭제한다.
                            //소켓으로 전송
                            oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", "", String.valueOf(drawCanvas.usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));


                            drawCommandList_Total_STEP.get(t).setdrawcount(drawcount - 1);

                            drawCanvas.invalidate();//화면을 갱신하다.
                        }
                    }
                }
                //소켓으로 보내고 전 툴로 변경한다.
            drawCanvas.changeToolnColor(beforetool_total, beforecolor_total);

        });

        //앞으로가기
        fbforwardgobtn.setOnClickListener((v)->{
            drawCanvas.usetoolmode = 3;

            whiteboarddatajson.clear();

            for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                    ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                    ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                    int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                    int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();


                    Log.d("drawcount _ fronttest",String.valueOf(drawcount));
                    Log.d("drawcountFinal _ fronttest",String.valueOf(drawcountFinal));


                    //현재 그린 좌표 순서 보다 전체 좌표 순서가 큰 경우
                    if(drawcount < drawcountFinal){
                        drawCommandList_Total_STEP.get(t).setdrawcount(drawcount + 1);
                        drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();

                        Log.d("drawcount",String.valueOf(drawcount));

                        //나머지 리스트 에서 내가 그린것에 한해서, 순서가 맞으면
                        for (int p = 0; p < restpenlist.size(); p++) {
                            if (restpenlist.get(p).getwriteuid().equals(myuid)) {
                                if (restpenlist.get(p).getdrawcount() == drawcount) { //3

                                    totalpenlist.add(restpenlist.get(p));

                                    Log.d("restpenlist_getdrawcount",String.valueOf(restpenlist.get(p).getdrawcount())); //3

                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("tool", String.valueOf(restpenlist.get(p).gettools()));
                                        data.put("x", String.valueOf(restpenlist.get(p).getx()));
                                        data.put("y", String.valueOf(restpenlist.get(p).gety()));
                                        data.put("radius", String.valueOf(restpenlist.get(p).getradius()));
                                        data.put("state", String.valueOf(restpenlist.get(p).getmoveStatus()));
                                        data.put("color", String.valueOf(restpenlist.get(p).getcolor()));
                                        data.put("size", String.valueOf(restpenlist.get(p).getsize()));
                                        data.put("drawcount", String.valueOf(restpenlist.get(p).getdrawcount()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    //Log.d("getdrawcount_test",String.valueOf(data)); //카운트에 맞는 값만 전송함.
                                    //왜 자꾸 5번을 같이 보내지????
                                    whiteboarddatajson.add(data); //현재 그린것에 대해서만 전달한다. 손을떼면 바로 초기화함.

                                    restpenlist.remove(restpenlist.get(p));


                                    p--;
                                }
                            }
                        }
                        drawCommandList.clear();
                        drawCommandList.addAll(totalpenlist);

                        //Log.d("String.valueOf(whiteboarddatajson)", String.valueOf(whiteboarddatajson));
                        Log.d("String.valueOf(drawcount)", String.valueOf(drawcount));

                        //순서에 맞게 한 카운트의 좌표 만큼 전송한다.
                        //소켓으로 전송
                        Log.d("whiteboarddatajson_test",String.valueOf(whiteboarddatajson)); //카운트에 맞는 값만 전송함.
                        oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", String.valueOf(whiteboarddatajson), String.valueOf(drawCanvas.usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));

                        drawCanvas.invalidate();//화면을 갱신하다.

                    }

                    //소켓으로 보내고 전 툴로 변경한다.
                    drawCanvas.changeToolnColor(beforetool_total, beforecolor_total);

                }
            }
        });

        //새로고침
        fbresetbtn.setOnClickListener((v)->{
            drawCommandList.clear();
            //해당 문서에 맞게 불러와준다
            for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                    ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                    //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                    // int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                    //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                    drawCommandList.addAll(totalpenlist);
                }
            }

            //이미지 있을때만
            if (!String.valueOf(Documentdatalist.get(SelectDocumentCount).getbasicuri()).equals("")) {

                String imguri = RetrofitService.MOCK_SERVER_FIRSTURL + String.valueOf(Documentdatalist.get(SelectDocumentCount).getbasicuri()) + String.valueOf(Documentdatalist.get(SelectDocumentCount).getsrc());

                //피카소 api를 통해 bitmap으로 변경한다.
                Picasso.get()
                        .load(imguri)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                /* Save the bitmap or do something with it here */

                                //Set it in the ImageView
                                // theView.setImageBitmap(bitmap);
                                drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                drawCanvas.invalidate();//화면을 갱신하다.
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            } else {
                drawCanvas.image_access = 0;
                drawCanvas.invalidate();//화면을 갱신하다.
            }

            Toast.makeText(GlobalClass, "새로고침 되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * jhChoi - 201124
     * 그림이 그려질 canvas view
     */
    class DrawCanvas extends View {

        public static final int MODE_ERASER = 0;                  //모드 (지우개)
        public static final int MODE_PEN = 1;                     //모드 (펜)
        public static final int MODE_CIRCLE = 7;                  //모드 (원)

        //색상 모드 1 검정, 2 빨강, 3 파랑, 4 노랑 5 초록
        public static final int MODE_WHITE = 0;
        public static final int MODE_BLACK = 1;
        public static final int MODE_RED = 2;
        public static final int MODE_BLUE = 3;
        public static final int MODE_YELLO = 4;
        public static final int MODE_GREEN = 5;

        final int PEN_SIZE = 7;                                   //펜 사이즈
        final int ERASER_SIZE = 30;                               //지우개 사이즈

        Paint paint;                                              //펜
        Bitmap loadDrawImage;                                     //호출된 이전 그림
        int color;                                                //현재 펜 색상
        int size;                                                 //현재 펜 크기

        int StateTouch = 0; //터치 여부

        //펜과 지우개는 적용 후 뒤로가기나 앞으로 가기 후에 바로 사용할 수 있기에 고정적용으로 Basictoolmode를 사용한다.
        int usetoolmode = 1; //기본 툴 펜으로 지정  0.지우개, 1.펜, 2.뒤로가기, 3.앞으로가기, 4. 문서 추가, 5. 문서 삭제, 6. 내거 전부 지우기, 7. 원, 8. 내 uid 초기화

        Bitmap bitmapimg; //bitmap이미지를 저장하는 부분
        int image_access = 0; //이미지를 사용할지 말지 결정하는 변수 0. 이미지 사용x, 1. 이미지 사용

        int left_size = 0;
        int top_size = 0;

        //원그릴때 사용하는 변수
        private float initX, initY, radius;

        //선 그릴때 움직일때마다 찍히는 좌표
       // private float firstX, firstY, nextX = 0, nextY = 0;

        public DrawCanvas(Context context) {
            super(context);
            init();

           // Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
          //  canvas = new Canvas(bitmap);
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        /**
         * jhChoi - 201124
         * 그리기에 필요한 요소를 초기화 합니다.
         */
        private void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            drawCommandList.clear();
            loadDrawImage = null;

            //Toast.makeText(GlobalClass, String.valueOf(usetoolmode), Toast.LENGTH_SHORT).show();
            //Toast.makeText(GlobalClass, String.valueOf(SelectDocumentCount), Toast.LENGTH_SHORT).show();

            //전체 좌표 데이터에서 현재문서의 좌표를 그려준다.
            for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){
                    ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                    drawCommandList.addAll(totalpenlist);
                }
            }
        }

        //페이지 초기화할때 - 자기것만 지우기
        private void pageinit(){
            loadDrawImage = null;
            drawCommandList.clear();
            DeletePage_data();

            whiteboarddatajson.clear();

        }

        //해당 페이지의 좌표데이터를을 삭제한다.
        public void DeletePage_data(){  //- 자기것만 지우기
            //전체 draw리스트에서 삭제한 페이지의 draw데이터를 삭제한다.
            for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                    ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                    ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                    int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                    //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                    drawCommandList_Total_STEP.get(t).setdrawcount(0); //초기화

                    for(int p = 0; p < totalpenlist.size();p++){
                        if(totalpenlist.get(p).getwriteuid().equals(myuid)){
                            restpenlist.add(totalpenlist.get(p));
                            totalpenlist.remove(totalpenlist.get(p));
                            p--;
                        }
                    }

                    drawCommandList.addAll(totalpenlist);

                    oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", String.valueOf(whiteboarddatajson), String.valueOf(usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));
                }
            }
        }

        /**
         * jhChoi - 201124
         * 현재까지 그린 그림을 Bitmap으로 반환합니다.
         */
        public Bitmap getCurrentCanvas() {
            Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
            return bitmap;
        }

        //도구와 색상을 제어한다. 아이콘도 변경한다.
        private void changeToolnColor(int toolMode, int color){

            //전에 사용한 툴과 색상 저장하기
            beforetool_total = toolMode;
            beforecolor_total = color;

            usetoolmode = toolMode; //현재 툴 지정
            if (toolMode == MODE_PEN) {
                size = PEN_SIZE;

                if(color == MODE_BLACK){
                    this.color = Color.BLACK;
                    paint.setColor(Color.BLACK);
                    fbPen.setImageResource(R.drawable.pen_ck);
                }else if(color == MODE_RED){
                    this.color = Color.RED;
                    paint.setColor(Color.RED);
                    fbPen.setImageResource(R.drawable.penred_ck);
                }else if(color == MODE_BLUE){
                    this.color = Color.BLUE;
                    paint.setColor(Color.BLUE);
                    fbPen.setImageResource(R.drawable.penblue_ck);
                }else if(color == MODE_YELLO){
                    this.color = Color.YELLOW;
                    paint.setColor(Color.YELLOW);
                    fbPen.setImageResource(R.drawable.penyellow_ck);
                }else if(color == MODE_GREEN){
                    this.color = Color.GREEN;
                    paint.setColor(Color.GREEN);
                    fbPen.setImageResource(R.drawable.pengreen_ck);
                }

                //나머지 비활성화
                fbEraser.setImageResource(R.drawable.eraser);
                fbcircle.setImageResource(R.drawable.circledraw);

            }else if(toolMode == MODE_CIRCLE){ //원
                this.color = Color.BLACK;
                paint.setColor(Color.BLACK);
                size = PEN_SIZE;
                fbcircle.setImageResource(R.drawable.circledraw_ck);

                //나머지 비활성화
                fbEraser.setImageResource(R.drawable.eraser);
                fbPen.setImageResource(R.drawable.pen);
            }else if(toolMode == MODE_ERASER){ //선 지우개
                //펜을 흰색으로 작성하는 것으로 했는데 판서의 좌표를 지우는걸로 변경할 것이다.
                //어떻게 해야할까?
                //터치를 했을때 해당반경에 좌표가 존재한다면? 그 좌표만을 삭제하는 것이다.


                if(color == MODE_WHITE) {
                    this.color = Color.WHITE;
                    paint.setColor(Color.WHITE);
                    size = ERASER_SIZE;
                    fbEraser.setImageResource(R.drawable.eraser_ck);

                    //나머지 비활성화
                    fbPen.setImageResource(R.drawable.pen);
                    fbcircle.setImageResource(R.drawable.circledraw);
                }
            }
        }




        @Override
        protected void onDraw(Canvas canvas) { //그려질때 두번째실행 그릴때마다 작동함

            //Toast.makeText(GlobalClass, "onDraw", Toast.LENGTH_SHORT).show();
            canvas.drawColor(Color.WHITE); //배경색을 변경한다.

//            //캔버스에 이미지 추가 - 이대로 이미지를 변경해주면 된다!! - 가운데로 배치해서 넣을 것!!!
            //Resources r = this.getResources();
            //Bitmap image1 = BitmapFactory.decodeResource(r, R.drawable.allow2);

            int width_linear = canvasLinear.getWidth(); //1080
            int hight_linear = canvasLinear.getHeight(); // 1523 // 1843
            left_size = width_linear/6;
            top_size = hight_linear/10;
            Log.d("--width_linear--",String.valueOf(width_linear));
            Log.d("--hight_linear--",String.valueOf(hight_linear));
            Log.d("--hight_linear--**",String.valueOf(hight_linear*0.1));
            Log.d("--left_size--",String.valueOf(left_size));
            Log.d("--top_size--",String.valueOf(top_size));

            //이미지 허용일때만 이미지 생성
            if(image_access == 1){
                //int w = bitmapimg.getWidth();
               // int h = bitmapimg.getHeight();
               //Log.d("--width_linear--",String.valueOf(width_linear));
               // Log.d("--hight_linear--",String.valueOf(hight_linear));

                @SuppressLint("DrawAllocation")
                //Rect dst = new Rect(400, 800, 400 + w / 2, 800 + h / 2);
               // Rect dst = new Rect(0, 0, 400 + w / 2, 800 + h / 2);
                //Rect dst = new Rect(400, 1200, 400 + w / 2, 1200 + h / 2);

                Bitmap resize = resizeBitmapImage(bitmapimg, 1000);
                canvas.drawBitmap(resize, left_size, top_size, null);
            }

            //저장된 좌표를 그려주는 부분 - 사용안함
            if (loadDrawImage != null) {
                canvas.drawBitmap(loadDrawImage, 0, 0, null);
            }

            //원일때 그리는 과정이 보이도록 설정
            if(usetoolmode == 7) { //원
                if (StateTouch != 0) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(initX, initY, radius, paint);
                }
            }

            //좌표 리스트에 있는 내용을 그린다.
            for (int i = 0; i < drawCommandList.size(); i++) {
                Pen p = drawCommandList.get(i);
//                Log.d("tool", String.valueOf(p.gettools()));
//                Log.d("x", String.valueOf(p.getx()));
//                Log.d("y", String.valueOf(p.gety()));
//                Log.d("radius", String.valueOf(p.getradius()));
//                Log.d("moveStatus", String.valueOf(p.getmoveStatus()));
//                Log.d("color", String.valueOf(p.getcolor()));
//                Log.d("size", String.valueOf(p.getsize()));

                if(String.valueOf(p.gettools()).equals("7")){ //원
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(p.getcolor());

                    if (p.isMove()) {
                        canvas.drawCircle(p.getx(), p.gety(), p.getradius(), paint);
                    }
                }else{ //선
                    paint.setStrokeWidth(p.getsize());
                    paint.setColor(p.getcolor());

                  //  paint.setStrokeCap(Paint.Cap.ROUND);
                   // paint.setStrokeJoin(Paint.Join.ROUND);
                   // paint.setAntiAlias(true);

                    paint.setAntiAlias(true); // enable anti aliasing
                    paint.setDither(true); // enable dithering
                    paint.setStrokeJoin(Paint.Join.ROUND); // set the join to round you want
                    paint.setStrokeCap(Paint.Cap.ROUND);  // set the paint cap to round too
                    //paint.setPathEffect(new CornerPathEffect(getStrokeWidth())); // set the path effect when they join.


                    if (p.isMove()) { //움직이고 있을때만 표시하겠다.
                        Pen prevP = drawCommandList.get(i - 1);

                        canvas.drawLine(prevP.getx(), prevP.gety(), p.getx(), p.gety(), paint);
                    }else if(p.isStart()){ //시작할때 표시

                        //비율 다시 맞추기. 화면 크기를 따져서 비율을 줄 것.
                        canvas.drawLine(p.getx()-4, p.gety()-4, p.getx(), p.gety(), paint);
                    }
                }
            }

            //그릴때마다 뒤로가기 앞으로가기 설정
            if(drawCommandList.size() >= 0){

                for(int t = 0;t<drawCommandList_Total_STEP.size();t++) {
                    if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                        //ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                        //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                        int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                        int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                       // Log.d("drawcount-------------", String.valueOf(drawcount));
                       // Log.d("drawcountFinal-------------", String.valueOf(drawcountFinal));

                        //앞으로가기 : 앞으로 갈 데이터가 있을때
                        if(String.valueOf(drawcount).equals(String.valueOf(drawcountFinal))){
                            fbforwardgobtn.setImageResource(R.drawable.forwardgo);
                        }else {
                            fbforwardgobtn.setImageResource(R.drawable.forwardgo_exist);
                        }

                        //뒤로가기 : 내가 작성한게 있을때
                        if(drawcount > 0){
                            fbbackwardsgobtn.setImageResource(R.drawable.backwardsgo_exist);
                        }else{
                            fbbackwardsgobtn.setImageResource(R.drawable.backwardsgo);
                        }
                        //Log.d("drawcount----",String.valueOf(drawcount));
                        //Log.d("drawcountFinal----",String.valueOf(drawcountFinal));
                    }
                }

            }else{
                fbbackwardsgobtn.setImageResource(R.drawable.backwardsgo);
            }
        }



        //한번 터치하고 땔떼까지를 하나의 객체로 묵을 것.
        @Override
        public boolean onTouchEvent(MotionEvent e) { //터치할때 먼저실행 그릴때마다 작동함
           // Toast.makeText(GlobalClass, "onTouchEvent", Toast.LENGTH_SHORT).show();

            int action = e.getAction();

            //시작할때 0, 움직일때 1
            int state = action == MotionEvent.ACTION_DOWN ? Pen.STATE_START : Pen.STATE_MOVE;


            if(usetoolmode == 0 || usetoolmode == 1){ // 펜 지우개

                switch( e.getActionMasked() )
                {
                    // 화면에서 손가락을 땜 // 사실상 이벤트의 끝
                    case MotionEvent.ACTION_UP:

                        //좌표 초기화
                        //firstX = 0;
                        //firstY = 0;
                        //nextX = 0;
                        //nextY = 0;

                        StateTouch = 0;
                        //Log.d("StateTouch",  String.valueOf(StateTouch));
                        whiteboarddatajson.clear(); //손가락 떼면 보낼 데이터 리스트 초기화

                        //Log.d("DraqCountFinal_list",  String.valueOf(drawcount_list.get(SelectDocumentCount)));
                        //Log.d("DraqCountFinal_list",  String.valueOf(DraqCountFinal_list.get(SelectDocumentCount)));

                        //앞으로 가기 : 앞으로 갈 데이터가 있으면 사용가능 표시
                        for(int t = 0;t<drawCommandList_Total_STEP.size();t++) {
                            if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                                ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount(); //현재 그린 순서
                                int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal(); //그려진 총 순서

                                //앞으로가기 아이콘 변경
                                if(drawcount == drawcountFinal){
                                    fbforwardgobtn.setImageResource(R.drawable.forwardgo);
                                }else {
                                    fbforwardgobtn.setImageResource(R.drawable.forwardgo_exist);
                                }

                                //뒤로가기 리스트에 데이터 삭제하기
                                for (int r = 0;r<restpenlist.size();r++){
                                    if(restpenlist.get(r).getdrawcount() >= drawcount){
                                        restpenlist.remove(restpenlist.get(r));
                                        r--;
                                    }
                                }

                                //손을 떼었을때 그린 좌표를 전부 소켓전송한다.
                                for (int p = 0; p < totalpenlist.size(); p++) {
                                    if (totalpenlist.get(p).getwriteuid().equals(myuid)) {
                                        if (totalpenlist.get(p).getdrawcount() == drawcount) {

                                            //Log.d("totalpenlist_getdrawcount",String.valueOf(totalpenlist.get(p).getdrawcount()));
                                            //Log.d("totalpenlist_p",String.valueOf(p));

                                            JSONObject data = new JSONObject();
                                            try {
                                                data.put("tool", String.valueOf(totalpenlist.get(p).gettools()));
                                                data.put("x", String.valueOf(totalpenlist.get(p).getx()));
                                                data.put("y", String.valueOf(totalpenlist.get(p).gety()));
                                                data.put("radius", String.valueOf(totalpenlist.get(p).getradius()));
                                                data.put("state", String.valueOf(totalpenlist.get(p).getmoveStatus()));
                                                data.put("color", String.valueOf(totalpenlist.get(p).getcolor()));
                                                data.put("size", String.valueOf(totalpenlist.get(p).getsize()));
                                                data.put("drawcount", String.valueOf(totalpenlist.get(p).getdrawcount()));
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }

                                            whiteboarddatajson.add(data); //현재 그린것에 대해서만 전달한다. 손을떼면 바로 초기화함.
                                        }
                                    }
                                }
                                //소켓으로 전송
                                oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", String.valueOf(whiteboarddatajson), String.valueOf(usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));
                            }
                        }
                        break;

                    // 화면에 손가락이 닿음 // 모든 이벤트의 출발점
                    case MotionEvent.ACTION_DOWN:
                        StateTouch = 1;
                        //Log.d("StateTouch",  String.valueOf(StateTouch));
                        //처음 좌표 찍기.
                        //firstX = e.getX();
                        //firstY = e.getY();

                        for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                            if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                                //  ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                               // ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                //그린 횟수 조정하는 부분
                                //그리기전에 전체횟수와 현재횟수가 같다면 +1
                                //그리기전에 전체횟수보다 현재횟수가 작다면? 같게
                                if(drawcount == drawcountFinal){
                                    drawCommandList_Total_STEP.get(t).setdrawcountFinal(drawcountFinal+1); //플러스
                                    drawCommandList_Total_STEP.get(t).setdrawcount(drawcount+1); //플러스
                                }else if(drawcount < drawcountFinal){
                                    drawcount = drawcount+1;
                                    drawCommandList_Total_STEP.get(t).setdrawcount(drawcount); //플러스
                                    drawCommandList_Total_STEP.get(t).setdrawcountFinal(drawcount); //동일하게
                                }
                            }
                        }

                        nextX = e.getX();
                        nextY = e.getY();

                        break;
                    // 화면에 손가락이 닿은 채로 움직이고 있음(움직일때마다 호출됨)
                    case MotionEvent.ACTION_MOVE:
                        StateTouch = 2;
                        //Log.d("StateTouch",  String.valueOf(StateTouch));
                        //다음 좌표 찍기기
//                       if(nextX != 0){
//                            firstX = nextX;
//                            firstY = nextY;
//                        }
//                        //처음 좌표 찍기.
                        nextX = e.getX();
                        nextY = e.getY();

                        //여기서 데이터좌표가 저장이 되고 예시로 표현이 되어야한다.
                       // Log.d( "TAG", "first 좌표 : (" + firstX +", " + firstY + ")" );
                        //Log.d( "TAG", "움직이는 좌표 : (" + nextX +", " + nextY + ")" );

                        //선지우개로 그릴때 이미지를 생성해주는 쪽으로 하려고했는데...
                        //실패




                        break;
                }


                /*
                * 터치하고 움직일때마다 좌표를 저장한다.
                * 터치할때부터 데이터를 저장하는 이유는 다음 좌표와 연결하기 위함이다.
                * */
                if(StateTouch != 0){ //1, 2 터치 시작, 움직일때 좌표를 저장
                    // Log.d("---state---", String.valueOf(state));

                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++) {
                        if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                            //ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                           // ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                            int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                           // int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                            //터치할때마다 리스트에 추가가 된다.
                            drawCommandList.add(new Pen(String.valueOf(usetoolmode), e.getX(), e.getY(), 0, state, color, size, drawcount, SelectDocumentCount, SelectDocumentIdx, myuid)); //해당 내용을 list에 저장한다.
                            drawCommandList_Total_STEP.get(t).addPen(new Pen(String.valueOf(usetoolmode), e.getX(), e.getY(), 0, state, color, size, drawcount, SelectDocumentCount, SelectDocumentIdx, myuid));

                            invalidate(); //새로고침
                        }
                    }




                    if(usetoolmode == 0){
                        //Log.d( "TAG", "first 좌표 : (" + firstX +", " + firstY + ")" );

                        Log.d( "TAG", "지우개 움직이는 좌표 : (" + nextX +", " + nextY + ")" );

                        //전체 좌표리스트에서 검색
                        for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                            if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                                ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                                ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                                // int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                                //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                                // drawCommandList.addAll(totalpenlist);

                                //선택한 문서에 전체 좌표를 for문으로 돌림
                                for(int to = 0; to<totalpenlist.size();to++){

                                    Log.d( "TAG", "작성된 좌표리스트 : (" + totalpenlist.get(to).getx() +", " + totalpenlist.get(to).gety() + ")" );

                                    //nextX nextY와 같은게 있으면?
                                    if(String.valueOf(totalpenlist.get(to).getx()).equals(String.valueOf(nextX)) && String.valueOf(totalpenlist.get(to).gety()).equals(String.valueOf(nextY))){
                                        Log.d( "TAG", "겹친 좌표는 : (" + nextX +", " + nextY + ")" );
                                        // Log.d( "TAG", "겹친 좌표는? : (" + totalpenlist.get(to).getx() +", " + totalpenlist.get(to).gety() + ")" );

//                                        totalpenlist.remove()
                                        //totalpenlist.remove(totalpenlist.get(to));
                                    }
                                }
                            }
                        }
                    }
                }

            }else if(usetoolmode == 7){  //원 그리기

                //int action = e.getAction();
                if (action == MotionEvent.ACTION_MOVE) { //움직일때
                    StateTouch = 2;

                    float x = e.getX();
                    float y = e.getY();

                    radius = (float) Math.sqrt(Math.pow(x - initX, 2) + Math.pow(y - initY, 2));

                } else if (action == MotionEvent.ACTION_DOWN) { //처음 터치 시
                    StateTouch = 1;

                    initX = e.getX();
                    initY = e.getY();
                    radius = 1;


                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                        if(drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)){

                            //  ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                            ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                            int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                            int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                            //뒤로가기 리스트에 데이터 삭제하기
                            for (int r = 0;r<restpenlist.size();r++){
                                if(restpenlist.get(r).getdrawcount() >= drawcount){
                                    restpenlist.remove(restpenlist.get(r));
                                    r--;
                                }
                            }

                            //그린 횟수 조정하는 부분
                            //그리기전에 전체횟수와 현재횟수가 같다면 +1
                            //그리기전에 전체횟수보다 현재횟수가 작다면? 같게
                            if(drawcount == drawcountFinal){
                                drawCommandList_Total_STEP.get(t).setdrawcountFinal(drawcountFinal+1); //플러스
                                drawCommandList_Total_STEP.get(t).setdrawcount(drawcount+1); //플러스
                            }else if(drawcount < drawcountFinal){
                                drawcount = drawcount+1;
                                drawCommandList_Total_STEP.get(t).setdrawcount(drawcount); //플러스
                                drawCommandList_Total_STEP.get(t).setdrawcountFinal(drawcount); //동일하게
                            }
                        }
                    }

                } else if (action == MotionEvent.ACTION_UP) { //손을 떼었을때
                    StateTouch = 0;

                    whiteboarddatajson.clear(); //손가락 떼면 보낼 데이터 리스트 초기화


                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++) {
                        if (drawCommandList_Total_STEP.get(t).getidx().equals(SelectDocumentIdx)) {

                            //ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                            //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                            int drawcount = drawCommandList_Total_STEP.get(t).getdrawcount();
                            //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                            //터치할때마다 리스트에 추가가 된다.
                            drawCommandList.add(new Pen(String.valueOf(usetoolmode), initX, initY, radius, state, color, size, drawcount, SelectDocumentCount, SelectDocumentIdx, myuid)); //해당 내용을 list에 저장한다.

                            drawCommandList_Total_STEP.get(t).addPen(new Pen(String.valueOf(usetoolmode), initX, initY, radius, state, color, size, drawcount, SelectDocumentCount, SelectDocumentIdx, myuid));

                            //소켓으로 전송할 데이터를 만든다.
                            JSONObject data = new JSONObject();
                            try {
                                data.put("tool", String.valueOf(usetoolmode));
                                data.put("x", String.valueOf(initX));
                                data.put("y", String.valueOf(initY));
                                data.put("radius", String.valueOf(radius));
                                data.put("state", String.valueOf(state));
                                data.put("color", String.valueOf(color));
                                data.put("size", String.valueOf(size));
                                data.put("drawcount", String.valueOf(drawcount));

                                whiteboarddatajson.add(data); //현재 그린것에 대해서만 전달한다. 손을떼면 바로 초기화함.

                               // Log.d("whiteboarddatajson",String.valueOf(whiteboarddatajson));//111111
                               // Log.d("-----drawcount-----",String.valueOf(drawcount));

                                //그린 모든 값이 arraylist에 존재하는것이다. 움직일때마다 좌표를 찍고 그것을 가지고 있고 바로바로 보여주는 것
                                //이 리스트만 소켓으로 전송해서 보여주면 되는거 아님? 그릴때마다.

                                //소켓으로 전송
                                oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", String.valueOf(whiteboarddatajson), String.valueOf(usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));


                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }
                    }
                }

                invalidate();
            }
            return true;
        }
    }

    //비트맵 리사이즈
    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height)
        {
            if(maxResolution < width)
            {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < height)
            {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    //스레드가 먼저 실행
    public void RecoderTimerThread(){
        Recoderthread = new Thread(){
            public void run(){
                while(true){
                    try{

                        //sleep함수를 막으면 전부 먹통이 되어서 타이머만 0으로 보이게 하였다.
                        if(RecoderComplete == 1) { //녹화 종료시
                            RecoderTimer = 0;
                        }else{ //녹화 시작시
                            RecoderTimer++;
                        }

                        //메서드로 획득한 메시지 객체에 보내고자 하는 데이터를 채우는 것
                        //메시지의 target이 핸들러 자신으로 지정된 Message 객체 리턴
                        Message msg = RecoderHandler.obtainMessage();
                        msg.what = 1;
                        // msg.obj = "timer";
                        msg.arg1 = RecoderTimer;
                        //msg.arg2 = CertifiComplete;
                        RecoderHandler.sendMessage(msg);

                        Thread.sleep(1000);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Recoderthread.start();
    }


    public void MakeHandler() {

        //광고 이미지 변경 핸들러
        RecoderHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO : process device message.

                if (msg.what == 1) {
//                    if(msg.arg2 == 2){ //인증번호가 틀렸을때
//
//                        //안먹었음...
//                        Log.d("444", Integer.toString(msg.arg2));
//                        certifichktext.setVisibility(View.VISIBLE);
//                        certifichktext.setText("인증번호가 맞지 않습니다.");
//                    }else{
//                        certifichktext.setVisibility(View.GONE);
//                    }

//                    emailsendbtn.setText("재전송");
//                    certifibox.setVisibility(View.VISIBLE); //인증박스 보이게 처리

                    String timerval = timertrans(msg.arg1); //변환된 타이머 문자 가져옴

                    //idtext.setText("이메일 "+timerval);
                    recordtime.setText(timerval);
                    Log.d("타이머 숫자", timerval);
                }else if(msg.what == 2){
                    String timerval = timertrans(0);
                    recordtime.setText(timerval);
                    //emailsendbtn.setText("전송");
                    //certifibox.setVisibility(View.GONE); //인증박스 안보이게 처리
                }else if(msg.what == 3){
                    //emailsendbtn.setText("인증 완료");
                    //emailsendbtn.setBackgroundResource(R.drawable.btndesign1);
                    //certifibox.setVisibility(View.GONE); //인증박스 안보이게 처리
                }
            }
        };
    }

    //타이머 변환기
    public String timertrans(Integer timernum){
        int minute = timernum/60;
        int second = timernum%60;

        String minute_tr = null;
        String second_tr = null;
        if(minute<10){
            minute_tr = "0"+minute;
        }else{
            minute_tr = String.valueOf(minute);
        }
        if(second<10){
            second_tr = "0"+second;
        }else{
            second_tr = String.valueOf(second);
        }
        return minute_tr+":"+second_tr;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //과외문의 연결 변수를 0으로 변경한다. stop에서 변경이 늦게 되는 경우를 잡기위해
        GlobalClass.conferenceroomaccess = 1;
        GlobalClass.myclasschatroomaccess = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();

        GlobalClass.conferenceroomaccess = 0;
    }


    //카메라 마이크 onoff리스트
    ArrayList<CamerachkForm> camerachkdata = new ArrayList<>();
    ArrayList<MikechkForm> mikechkdata = new ArrayList<>();


    //소켓을 통해 받은 메세지를 서비스에서 이 함수를 불러와 처리하는 부분
    public void recievemessage(JsonArray jarray_chatdata) throws JSONException, InterruptedException {

        int SelectDocumentCount_remote;

        String commend = String.valueOf(jarray_chatdata.get(0)).substring(1);
        String commend_sub = commend.substring(0, commend.length() - 1);

        String roomid = String.valueOf(jarray_chatdata.get(1)).substring(1);
        String roomid_sub = roomid.substring(0, roomid.length() - 1);

        String uid = String.valueOf(jarray_chatdata.get(2)).substring(1);
        String uid_sub = uid.substring(0, uid.length() - 1);

        String roomtype = String.valueOf(jarray_chatdata.get(3)).substring(1);
        String roomtype_sub = roomtype.substring(0, roomtype.length() - 1);

        String whiteboarddata = String.valueOf(jarray_chatdata.get(4)).substring(1);
        String whiteboarddata_sub = whiteboarddata.substring(0, whiteboarddata.length() - 1);

        String tooltype = String.valueOf(jarray_chatdata.get(5)).substring(1);
        String tooltype_sub = tooltype.substring(0, tooltype.length() - 1);

        String drawcount = String.valueOf(jarray_chatdata.get(6)).substring(1);
        String drawcount_sub = drawcount.substring(0, drawcount.length() - 1);

        String documentcount = String.valueOf(jarray_chatdata.get(7)).substring(1);
        String documentcount_sub = documentcount.substring(0, documentcount.length() - 1);

        String documentidx = String.valueOf(jarray_chatdata.get(8)).substring(1);
        String documentidx_sub = documentidx.substring(0, documentidx.length() - 1);


        //해당 방이 맞는지 체크한다.
        //roomid_sub 전송해온 방 idx와 내가 있는 방이 맞을때만 실행시킬 것.!!!
        if (roomid_sub.equals(roomidx)) {

            if(commend_sub.equals("WBSEND")){

                //Log.d("============= 화면출력 : ", " 전달????");
                //Log.d("============= 화면출력whiteboarddata  : ", whiteboarddata);
                //Log.d("============= 화면출력whiteboarddata  : ", whiteboarddata_sub);
                //Log.d("onResponse ? ", "화면출력whiteboarddata : " + String.valueOf(whiteboarddata_sub.length()));



                //Log.d("onResponse ? drawCanvas.getdrawcount()", String.valueOf(drawcount_list.get(SelectDocumentCount)));
                //Log.d("onResponse ? drawCanvas.DraqCountFinal", String.valueOf(DraqCountFinal_list.get(SelectDocumentCount)));

//                Log.d("onResponse ? SelectDocumentCount !!!!!!!!!!!  ", String.valueOf(SelectDocumentCount));
//                Log.d("onResponse ? documentcount_sub !!!!!!!!!!!  ", String.valueOf(documentcount_sub));
//                Log.d("onResponse ? documentidx_sub !!!!!!!!!!!  ", String.valueOf(documentidx_sub));
//

               // Log.d("onResponse ? drawCanvas.getdrawcount()_sub", String.valueOf(drawcount_sub));


                //SelectDocumentCount_remote = Integer.parseInt(documentcount_sub);
                if(tooltype_sub.equals("2")){ // 뒤로가기
                    Log.d("onResponse ? 저장된 값 !!!!!!!!!!!  ", String.valueOf(SelectDocumentCount));
                    Log.d("onResponse ? 저장된 값 !!!!!!!!!!!  ", String.valueOf(SelectDocumentIdx));
                    Log.d("onResponse ? 보내온 값 !!!!!!!!!!!  ", String.valueOf(documentcount_sub));
                    Log.d("onResponse ? 보내온 값 !!!!!!!!!!!  ", String.valueOf(documentidx_sub));


                    //전체 좌표 리스트에서 해당 문서에 그린순서에 해당하는 좌표를 삭제한다.
                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                        if(drawCommandList_Total_STEP.get(t).getidx().equals(documentidx_sub)){ //해당 문서를

                            ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                           // ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                            //int drawcount_ = drawCommandList_Total_STEP.get(t).getdrawcount();
                            //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                            for(int p = 0; p < totalpenlist.size();p++){
                                if(totalpenlist.get(p).getwriteuid().equals(uid_sub)){ //그린 사람
                                    if (totalpenlist.get(p).getdrawcount() == Integer.parseInt(drawcount_sub)) { //그린 순서에 맞는
                                        totalpenlist.remove(totalpenlist.get(p)); //좌표 삭제
                                        p--;
                                    }
                                }
                            }

                            //선택한 문서와 전송받은 문서가 같을때만 변경함
                            if(documentidx_sub.equals(SelectDocumentIdx)){
                                drawCommandList.clear(); //그릴 좌표 초기화
                                drawCommandList.addAll(totalpenlist); //그릴 좌표에 좌표 추가
                                //Log.d("onResponse ? totalpenlist", String.valueOf("전체 리스트"));
                                //Log.d("onResponse ? totalpenlist", String.valueOf(totalpenlist));

                                drawCanvas.invalidate();//화면을 갱신하다.
                            }
                        }
                    }

                    if(PageMode == 1){
                        //그릴때마다 문서 리스트의 좌표를 갱신한다.
                        ArrayList<Integer> documentclicklist_temp = new ArrayList<>();
                        documentclicklist_temp.add(Integer.parseInt(documentcount_sub));

                        //문서 전체 업로드
                        oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist_temp, drawCommandList_Total_STEP);
                    }
                }else if(tooltype_sub.equals("3")){ //앞으로가기

                   /// Log.d("onResponse ? documentidx_sub", String.valueOf(documentidx_sub));
                   /// Log.d("onResponse ? SelectDocumentIdx", String.valueOf(SelectDocumentIdx));

                        if (whiteboarddata_sub.length() > 2) {
                            //String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
                            String match = "\\\\";
                            String changedata = whiteboarddata_sub.replaceAll(match, "");
                            //Log.d("onResponse ? ", "changedata : " + changedata);


                            JSONArray jsonArray = new JSONArray(changedata);
                            //Log.d("============= whiteboarddata jsonArray ", String.valueOf(jsonArray));

                            //가져온 화이트보드 정보를 여기서 그려준다.
                            for (int wb = 0; wb < jsonArray.length(); wb++) {
                                //Log.d("============= whiteboarddata jsonArray ", String.valueOf(jsonArray.get(wb)));
                                JSONObject jobj = new JSONObject(String.valueOf(jsonArray.get(wb)));
//                                Log.d("============= whiteboarddata tool ", String.valueOf(jobj.get("tool")));
//                                Log.d("============= whiteboarddata x ", String.valueOf(jobj.get("x")));
//                                Log.d("============= whiteboarddata y ", String.valueOf(jobj.get("y")));
//                                Log.d("============= whiteboarddata radius ", String.valueOf(jobj.get("radius")));
//                                Log.d("============= whiteboarddata state ", String.valueOf(jobj.get("state")));
//                                Log.d("============= whiteboarddata color ", String.valueOf(jobj.get("color")));
//                                Log.d("============= whiteboarddata size ", String.valueOf(jobj.get("size")));
//                                Log.d("============= whiteboarddata drawcount ", String.valueOf(jobj.get("drawcount")));

                                //소켓으로 전송할 데이터를 만든다.
                               // JSONObject data = new JSONObject();
                                try {
//                                    data.put("tool", String.valueOf(jobj.get("tool")));
//                                    data.put("x", String.valueOf(jobj.get("x")));
//                                    data.put("y", String.valueOf(jobj.get("y")));
//                                    data.put("radius", String.valueOf(jobj.get("radius")));
//                                    data.put("state", String.valueOf(jobj.get("state")));
//                                    data.put("color", String.valueOf(jobj.get("color")));
//                                    data.put("size", String.valueOf(jobj.get("size")));
//                                    data.put("drawcount", String.valueOf(jobj.get("drawcount")));

                                    //같은 페이지 일때만 그릴 것
                                    if (documentidx_sub.equals(SelectDocumentIdx)) {
                                        drawCommandList.add(new Pen(String.valueOf(jobj.get("tool")), Float.parseFloat(String.valueOf(jobj.get("x"))), Float.parseFloat(String.valueOf(jobj.get("y"))), Float.parseFloat(String.valueOf(jobj.get("radius"))), Integer.parseInt(String.valueOf(jobj.get("state"))), Integer.parseInt(String.valueOf(jobj.get("color"))), Integer.parseInt(String.valueOf(jobj.get("size"))), Integer.parseInt(String.valueOf(jobj.get("drawcount"))), Integer.parseInt(documentcount_sub), documentidx_sub, uid_sub)); //해당 내용을 list에 저장한다.
                                    }

                                    //전체 좌표 리스트에 저장
                                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                                        if(drawCommandList_Total_STEP.get(t).getidx().equals(documentidx_sub)){
                                            drawCommandList_Total_STEP.get(t).addPen(new Pen(String.valueOf(jobj.get("tool")), Float.parseFloat(String.valueOf(jobj.get("x"))), Float.parseFloat(String.valueOf(jobj.get("y"))),Float.parseFloat(String.valueOf(jobj.get("radius"))), Integer.parseInt(String.valueOf(jobj.get("state"))), Integer.parseInt(String.valueOf(jobj.get("color"))), Integer.parseInt(String.valueOf(jobj.get("size"))), Integer.parseInt(String.valueOf(jobj.get("drawcount"))), Integer.parseInt(documentcount_sub), documentidx_sub, uid_sub));
                                        }
                                    }

                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }
                        }

                        Log.d("============= whiteboarddata drawcount ", "통과 ~~");

                        //같은 페이지이고 화이트 보드 모드일때 동작
                        if(PageMode == 1 && documentidx_sub.equals(SelectDocumentIdx)) {
                            drawCanvas.invalidate();//화면을 갱신하다.
                        }

                        if(PageMode == 1){
                            //그릴때마다 문서 리스트의 좌표를 갱신한다.
                            ArrayList<Integer> documentclicklist_temp = new ArrayList<>();
                            documentclicklist_temp.add(Integer.parseInt(documentcount_sub));

                            //문서 전체 업로드
                            oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist_temp, drawCommandList_Total_STEP);
                        }
                }else if(tooltype_sub.equals("4")) { //새 문서 추가

                        Log.d("onResponse ? drawCanvas.tooltype_sub", String.valueOf(tooltype_sub));
                        Log.d("onResponse ? drawCanvas.getdrawcount()_sub", String.valueOf(drawcount_sub));

                        SelectDocumentIdx = documentidx_sub; //새로 추가된 문서를 선택으로 고정한다.
                        Documentdatalist.clear(); //문서 리스트 삭제
                        saveconferencedocument(1, 2); //1. 새페이지 서버에 저장한다.

                    if(PageMode == 1){
                        //클릭시 1씩 증가 처음을 가리기 위함.
                        WhiteboardFirstNum++;

                        //문서의 리스트를 다시 불러온다.
                        MakeDocumentrecycle(Documentdatalist);
                    }
                }else if(tooltype_sub.equals("5")){ //문서 삭제

                        Log.d("onResponse ? drawCanvas.tooltype_sub", String.valueOf(tooltype_sub));
                        Log.d("onResponse ? drawCanvas.getdrawcount()_sub", String.valueOf(drawcount_sub));

                        Documentdatalist.clear(); //문서 리스트 삭제
                        saveconferencedocument(1, 2); //1. 새페이지 서버에 저장한다.

                    if(PageMode == 1){
                        //클릭시 1씩 증가 처음을 가리기 위함.
                        WhiteboardFirstNum++;

                        //문서의 리스트를 다시 불러온다.
                        MakeDocumentrecycle(Documentdatalist);
                    }
                }else if(tooltype_sub.equals("6")){ //상대 전부 지우기

                        //상대가 작성한것 전부 지우기, 객체 지우기

                        //전체 리스트에서 보내온 상대 uid에 대해 지우기
                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                        if(drawCommandList_Total_STEP.get(t).getidx().equals(documentidx_sub)){ //해당 문서를

                            ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                            //ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                            //int drawcount_ = drawCommandList_Total_STEP.get(t).getdrawcount();
                            //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                            for(int p = 0; p < totalpenlist.size();p++){
                                if(totalpenlist.get(p).getwriteuid().equals(uid_sub)){ //그린 사람
                                    totalpenlist.remove(totalpenlist.get(p));
                                    p--;
                                }
                            }

                            //선택한 문서와 전송받은 문서가 같을때만 변경함
                            if(documentidx_sub.equals(SelectDocumentIdx)) {
                                drawCommandList.clear();
                                drawCommandList.addAll(totalpenlist);

                                drawCanvas.invalidate();//화면을 갱신하다.
                            }
                        }
                    }
                    if(PageMode == 1){
                        //그릴때마다 문서 리스트의 좌표를 갱신한다.
                        ArrayList<Integer> documentclicklist_temp = new ArrayList<>();
                        documentclicklist_temp.add(Integer.parseInt(documentcount_sub));

                        //문서 전체 업로드
                        oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist_temp, drawCommandList_Total_STEP);
                    }

                }else if(tooltype_sub.equals("1") || tooltype_sub.equals("0")){ //펜, 지우개

                    Log.d("onResponse ? 저장된 값 !!!!!!!!!!!  ", String.valueOf(SelectDocumentCount));
                    Log.d("onResponse ? 저장된 값 !!!!!!!!!!!  ", String.valueOf(SelectDocumentIdx));
                    Log.d("onResponse ? 보내온 값 !!!!!!!!!!!  ", String.valueOf(documentcount_sub));
                    Log.d("onResponse ? 보내온 값 !!!!!!!!!!!  ", String.valueOf(documentidx_sub));

                    whiteboarddatajson.clear();
                        try {
                            if (whiteboarddata_sub.length() > 2) {
                                //String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
                                String match = "\\\\";
                                String changedata = whiteboarddata_sub.replaceAll(match, "");
                               // Log.d("onResponse ? ", "changedata : " + changedata);


                                JSONArray jsonArray = new JSONArray(changedata);
                              //  Log.d("============= whiteboarddata jsonArray ", String.valueOf(jsonArray));

                                //가져온 화이트보드 정보를 여기서 그려준다.
                                for (int wb = 0; wb < jsonArray.length(); wb++) {
                                   // Log.d("============= whiteboarddata jsonArray ", String.valueOf(jsonArray.get(wb)));
                                    JSONObject jobj = new JSONObject(String.valueOf(jsonArray.get(wb)));
//                                    Log.d("============= whiteboarddata tool ", String.valueOf(jobj.get("tool")));
//                                    Log.d("============= whiteboarddata x ", String.valueOf(jobj.get("x")));
//                                    Log.d("============= whiteboarddata y ", String.valueOf(jobj.get("y")));
//                                    Log.d("============= whiteboarddata radius ", String.valueOf(jobj.get("radius")));
//                                    Log.d("============= whiteboarddata state ", String.valueOf(jobj.get("state")));
//                                    Log.d("============= whiteboarddata color ", String.valueOf(jobj.get("color")));
//                                    Log.d("============= whiteboarddata size ", String.valueOf(jobj.get("size")));
//                                    Log.d("============= whiteboarddata drawcount ", String.valueOf(jobj.get("drawcount")));

                                    //소켓으로 전송할 데이터를 만든다.
                                   // JSONObject data = new JSONObject();
                                    try {
//                                        data.put("tool", String.valueOf(jobj.get("tool")));
//                                        data.put("x", String.valueOf(jobj.get("x")));
//                                        data.put("y", String.valueOf(jobj.get("y")));
//                                        data.put("radius", String.valueOf(jobj.get("radius")));
//                                        data.put("state", String.valueOf(jobj.get("state")));
//                                        data.put("color", String.valueOf(jobj.get("color")));
//                                        data.put("size", String.valueOf(jobj.get("size")));
//                                        data.put("drawcount", String.valueOf(jobj.get("drawcount")));


                                        //전체 좌표 리스트에 해당 문서안에 좌표를 저장한다.
                                        for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                                            if(drawCommandList_Total_STEP.get(t).getidx().equals(documentidx_sub)) { //해당 문서를
                                                drawCommandList_Total_STEP.get(t).addPen(new Pen(String.valueOf(jobj.get("tool")), Float.parseFloat(String.valueOf(jobj.get("x"))), Float.parseFloat(String.valueOf(jobj.get("y"))), Float.parseFloat(String.valueOf(jobj.get("radius"))), Integer.parseInt(String.valueOf(jobj.get("state"))), Integer.parseInt(String.valueOf(jobj.get("color"))), Integer.parseInt(String.valueOf(jobj.get("size"))), Integer.parseInt(String.valueOf(jobj.get("drawcount"))), Integer.parseInt(documentcount_sub), documentidx_sub, uid_sub));
                                            }
                                        }

                                        //같은 페이지를 보고 있을때 저장한다.
                                        if (documentidx_sub.equals(SelectDocumentIdx)) { //같은 페이지 일때만 그릴 것
                                            drawCommandList.add(new Pen(String.valueOf(jobj.get("tool")), Float.parseFloat(String.valueOf(jobj.get("x"))), Float.parseFloat(String.valueOf(jobj.get("y"))), Float.parseFloat(String.valueOf(jobj.get("radius"))),  Integer.parseInt(String.valueOf(jobj.get("state"))), Integer.parseInt(String.valueOf(jobj.get("color"))), Integer.parseInt(String.valueOf(jobj.get("size"))), Integer.parseInt(String.valueOf(jobj.get("drawcount"))), Integer.parseInt(documentcount_sub), documentidx_sub, uid_sub)); //해당 내용을 list에 저장한다.
                                        }

                                    } catch (JSONException jsonException) {
                                        jsonException.printStackTrace();
                                    }
                                }

                                //화이트보드 모드, 같은 페이지일때 변경
                                if(PageMode == 1 && documentidx_sub.equals(SelectDocumentIdx)) {
                                    drawCanvas.invalidate();//화면을 갱신하다.
                                }

                                if(PageMode == 1) { //화이트 보드 모드일 때만
                                    //그릴때마다 문서 리스트의 좌표를 갱신한다.
                                    ArrayList<Integer> documentclicklist_temp = new ArrayList<>();
                                    documentclicklist_temp.add(Integer.parseInt(documentcount_sub));

                                    //문서 전체 업로드
                                    oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist_temp, drawCommandList_Total_STEP);
                                }
                            } else { //문서 초기화 하면

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }else if(tooltype_sub.equals("7")){ //원 그리기
                    whiteboarddatajson.clear();
                    try {
                        if (whiteboarddata_sub.length() > 2) {
                            //String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
                            String match = "\\\\";
                            String changedata = whiteboarddata_sub.replaceAll(match, "");
                            Log.d("onResponse ? ", "changedata : " + changedata);


                            JSONArray jsonArray = new JSONArray(changedata);
                            Log.d("============= whiteboarddata jsonArray ", String.valueOf(jsonArray));

                            //가져온 화이트보드 정보를 여기서 그려준다.
                            for (int wb = 0; wb < jsonArray.length(); wb++) {
                              //  Log.d("============= whiteboarddata jsonArray ", String.valueOf(jsonArray.get(wb)));
                                JSONObject jobj = new JSONObject(String.valueOf(jsonArray.get(wb)));
//                                Log.d("============= whiteboarddata tool ", String.valueOf(jobj.get("tool")));
//                                Log.d("============= whiteboarddata x ", String.valueOf(jobj.get("x")));
//                                Log.d("============= whiteboarddata y ", String.valueOf(jobj.get("y")));
//                                Log.d("============= whiteboarddata radius ", String.valueOf(jobj.get("radius")));
//                                Log.d("============= whiteboarddata state ", String.valueOf(jobj.get("state")));
//                                Log.d("============= whiteboarddata color ", String.valueOf(jobj.get("color")));
//                                Log.d("============= whiteboarddata size ", String.valueOf(jobj.get("size")));
//                                Log.d("============= whiteboarddata drawcount ", String.valueOf(jobj.get("drawcount")));

                                //소켓으로 전송할 데이터를 만든다.
                                //JSONObject data = new JSONObject();
                                try {
//                                    data.put("tool", String.valueOf(jobj.get("tool")));
//                                    data.put("x", String.valueOf(jobj.get("x")));
//                                    data.put("y", String.valueOf(jobj.get("y")));
//                                    data.put("radius", String.valueOf(jobj.get("radius")));
//                                    data.put("state", String.valueOf(jobj.get("state")));
//                                    data.put("color", String.valueOf(jobj.get("color")));
//                                    data.put("size", String.valueOf(jobj.get("size")));
//                                    data.put("drawcount", String.valueOf(jobj.get("drawcount")));

                                    //전체 좌표 리스트에 해당 문서안에 좌표를 저장한다.
                                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                                        if(drawCommandList_Total_STEP.get(t).getidx().equals(documentidx_sub)){ //해당 문서를
                                            drawCommandList_Total_STEP.get(t).addPen(new Pen(String.valueOf(jobj.get("tool")), Float.parseFloat(String.valueOf(jobj.get("x"))), Float.parseFloat(String.valueOf(jobj.get("y"))), Float.parseFloat(String.valueOf(jobj.get("radius"))),  Integer.parseInt(String.valueOf(jobj.get("state"))), Integer.parseInt(String.valueOf(jobj.get("color"))), Integer.parseInt(String.valueOf(jobj.get("size"))), Integer.parseInt(String.valueOf(jobj.get("drawcount"))), Integer.parseInt(documentcount_sub), documentidx_sub, uid_sub));
                                        }
                                    }

                                    //같은 페이지를 보고 있을때 저장한다.
                                    if (documentidx_sub.equals(SelectDocumentIdx)) {
                                        drawCommandList.add(new Pen(String.valueOf(jobj.get("tool")), Float.parseFloat(String.valueOf(jobj.get("x"))), Float.parseFloat(String.valueOf(jobj.get("y"))), Float.parseFloat(String.valueOf(jobj.get("radius"))),  Integer.parseInt(String.valueOf(jobj.get("state"))), Integer.parseInt(String.valueOf(jobj.get("color"))), Integer.parseInt(String.valueOf(jobj.get("size"))), Integer.parseInt(String.valueOf(jobj.get("drawcount"))), Integer.parseInt(documentcount_sub), documentidx_sub, uid_sub)); //해당 내용을 list에 저장한다.
                                    }

                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }

                            }

                            //화이트보드 모드, 같은 페이지일때 변경
                            if(PageMode == 1 && documentidx_sub.equals(SelectDocumentIdx)) {
                                drawCanvas.invalidate();//화면을 갱신하다.
                            }

                            if(PageMode == 1) {
                                //그릴때마다 문서 리스트의 좌표를 갱신한다.
                                ArrayList<Integer> documentclicklist_temp = new ArrayList<>();
                                documentclicklist_temp.add(Integer.parseInt(documentcount_sub));

                                //문서 전체 업로드
                                oConferenceDocumentlistRecyclerAdapter.notifyItemChanged_(documentclicklist_temp, drawCommandList_Total_STEP);
                            }
                        } else { //문서 초기화 하면

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(tooltype_sub.equals("8")){ //나간회원의 좌표 uid null 처리하기
                    //Log.d("onResponse ? 저장된 값 !!!!!!!!!!!  ", String.valueOf(SelectDocumentCount));
                    //Log.d("onResponse ? 저장된 값 !!!!!!!!!!!  ", String.valueOf(SelectDocumentIdx));
                    //Log.d("onResponse ? 보내온 값 !!!!!!!!!!!  ", String.valueOf(documentcount_sub));
                    //Log.d("onResponse ? 보내온 값 !!!!!!!!!!!  ", String.valueOf(documentidx_sub));

                    //전체 좌표 리스트에서 작성자의 좌표를 uid항목을 null처리한다.
                    for(int t = 0;t<drawCommandList_Total_STEP.size();t++){

                        ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();
                        // ArrayList<Pen> restpenlist = drawCommandList_Total_STEP.get(t).getRestPen();
                        //int drawcount_ = drawCommandList_Total_STEP.get(t).getdrawcount();
                        //int drawcountFinal = drawCommandList_Total_STEP.get(t).getdrawcountFinal();

                        //전체 문서에서 나간 유저가 작성한 좌표를 찾아낸다.
                        for(int p = 0; p < totalpenlist.size();p++){
                            if(totalpenlist.get(p).getwriteuid().equals(uid_sub)){ //그린 사람
                                //해당좌표를 전부 null로 처리.
                                totalpenlist.get(p).setwriteuid("0");
                            }
                        }
                    }
                }else if(tooltype_sub.equals("9")){ //선생님이 나갓을시 나머지 참가자 퇴장시키기
                    Toast.makeText(GlobalClass, "선생님이 방을 퇴장하여 3초 후 수업이 종료됩니다. ", Toast.LENGTH_SHORT).show();

                    Thread.sleep(3000);

                    deleteuserinconference();//유저 삭제하기
                    finish();
                }
            }
        }
    }
}