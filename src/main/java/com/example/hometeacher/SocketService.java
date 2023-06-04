package com.example.hometeacher;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telecom.Conference;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.hometeacher.ArraylistForm.ServerChatSendForm;
import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.shared.GlobalClass;
import com.example.hometeacher.shared.Session;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SocketService extends Service {


    private Handler mHandler;
    private Handler errHandler;
    public static Socket socket;

    //private String ip = "192.168.0.116"; // m cafe
    //private String ip = "192.168.10.103"; // 회사 네트워크

    //private String ip = "172.20.10.3"; // 할리스에서 휴대폰 연결
    private String ip = "121.183.211.198"; // 맥북IP 주소
    //private String ip = "192.168.10.70"; // 윈도우 IP 주소
    private int port = 1509; // PORT번호
    private int timeout = 3000; //연결 시도 시 timeout 체크

    SocketAddress socketAddress = null;

    String Senddata = "";
    BufferedReader input = null;
    PrintWriter outwriter;

    Thread getthread; //수신받는 스레드
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    Session oSession; //자동로그인을 위한 db
    Context mContext;
    ArrayList<ArrayList<String>> Sessionlist;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String myuid;

    SocketSend oSocketSend;

    boolean logoutclosed = false;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    public class LocalBinder extends Binder {
        public SocketService getService() {
            Log.d("-----mBound-----","getService");
            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        //소켓서비스 글로벌 변수로 저장
        //SocketService oSocketService = new SocketService();
        com.example.hometeacher.shared.GlobalClass.mService = this;
        logoutclosed = false;

        mContext = this;
        oSession = new Session(this);
        //자동 로그인 하기
        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication();
        //글로벌 클래스 선언
        mHandler = new Handler();
        //채팅 수신받은 스레드의 핸들러

        //Toast.makeText(this, "service start", Toast.LENGTH_SHORT).show();

        errHandler = new Handler();
        MethoderrHandler();

        oSocketSend = new SocketSend(GlobalClass);

        Log.d("onCreate","logoutclosed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logoutclosed = false;
        Log.d("onStartCommand","logoutclosed");

        Sessionlist = oSession.Getoneinfo("0");
        if (!Sessionlist.isEmpty()) { //로그인 상태일때만 실행
            SocketConnect();
        }
        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d("StartService","onBind()");
        return null;
    }

    //소켓 연결하는 부분
    public void SocketConnect(){
        if(socket == null) {
            if (!Sessionlist.isEmpty()) { //로그인 상태일때만 실행
                myuid = Sessionlist.get(1).get(0);
                Log.d("myuid",myuid);
            }
        }
        //----------이부분을 지금은 들어올때마다 스레드를 만들고 연결을 다시 하지만, 데이터가 있는 상태라면 굳이 연결을 다시 하지 않도록 하면 되지 않을까? -----------//

        //채팅 연결, 수신 받는 스레드 실행
        getthread = new Thread(){
            public void run(){

                //소켓 생성
                System.out.println("Socket Thread Running");
                try {
                    socket = new Socket();

                    //timeout 설정 3000
                    socketAddress = new InetSocketAddress(ip, port);
                    //socket.setSoTimeout(timeout); //소켓연결 후에 응답이 없을때의 TimeOut 설정
                    socket.connect(socketAddress, timeout); //소켓 Connect 연결할 때 TimeOut 설정

                    //글로벌 클래스에 넣어서 관리한다.
                    GlobalClass.setsocket(socket); //소켓만 사용

                } catch (SocketTimeoutException e){ //연결 시간이 초과한 경우 연결을 끊고 재시도 함.
                    Log.d("d","----------timeout------------");
                    Log.d("d", "connect check : "+String.valueOf(socket.isConnected())); // false

                    repeatsocketconnect(1);
                } catch (SocketException e) { //서버가 꺼졋을때 응답
                    Log.d("d","----------server no connect------------");
                    Log.d("d","----------server no connect------------ 서버 다운");
                    Log.d("d", "connect check : "+String.valueOf(socket.isConnected())); // false

                    repeatsocketconnect(1);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

                //Log.d("d", "connect check_222 : "+String.valueOf(socket.isConnected())); // false
                //연결이 된 후에만 메시지 보내고, 서버로 부터 응답받는 스레드를 실행한다.
                try {
                    receptionconnect(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        getthread.start();
    }

    //서버 연결이 확인된 후 서버에 로그인한다는 데이터 전송과 수신받는 스레드를 실행한다.
    //type 1. 서버가 켜진 상태에서 한번에 연결된 경우 2. 연결이 안되어서 에러로 갔다가 재연결이 된 경우
    public void receptionconnect(int type) throws IOException {
        if(socket.isConnected()){
            Log.d("d", "connect check : "+String.valueOf(socket.isConnected())); // false

            //명령어조건, 방고유번호, 방 인원수, 유저고유번호, 문자내용, 시간
            ServerChatSendForm oServerChatSendForm = new ServerChatSendForm("LOGIN", "","",myuid, "", "", "","", "", "", "");

            JSONObject Sendobj = new JSONObject();
            try {
                outwriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Sendobj.put("Sendinfo", oServerChatSendForm);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            //방에 묶여있던 클라이언트를 변경해줌
            outwriter.println(new Gson().toJson(Sendobj));
            //outwriter.write( "LOGIN> > >"+myuid+"> > "+"\n");
            outwriter.flush();


            //2. 연결이 안되어서 에러로 갔다가 재연결이 된 경우
            if(type == 2){
                //que에 있던 데이터 다시 전송하는 부분
                if(!GlobalClass.QueSavelist_whiteboard.isEmpty()){
                    for(int que = 0;que<GlobalClass.QueSavelist_whiteboard.size();que++){
                        //oSocketSend.SendSocketData_whiteboard("WBSEND", roomidx, Sessionlist.get(1).get(0), "2", String.valueOf(whiteboarddata_temp), String.valueOf(drawCanvas.usetoolmode), String.valueOf(drawcount), String.valueOf(SelectDocumentCount), String.valueOf(SelectDocumentIdx));
                        oSocketSend.SendSocketData_whiteboard("WBSEND", GlobalClass.QueSavelist_whiteboard.get(que).getrid(), Sessionlist.get(1).get(0), "2", GlobalClass.QueSavelist_whiteboard.get(que).getinformationdata(), GlobalClass.QueSavelist_whiteboard.get(que).gettooltype(), GlobalClass.QueSavelist_whiteboard.get(que).getdrawcount(), GlobalClass.QueSavelist_whiteboard.get(que).getdocumentcount(), GlobalClass.QueSavelist_whiteboard.get(que).getdocumentidx());
                    }

                    //전부 전송 후 초기화 해주기
                    GlobalClass.QueSavelist_whiteboard.clear();
                }
            }

            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Log.d("d",String.valueOf(input.readLine()));
                if(input.readLine() == null){ //null일 경우 예외 처리
                    throw new Exception();
                }else{
                    while(true) {
                        if(!socket.isClosed()){
                            //데이터 수신
                            String read = input.readLine();
                            mHandler.post(new msgUpdate(read));
                        }
                    }
                }



                //연결 된 후 도중에 끊겼을 경우 여기로 빠진다.
            } catch (SocketTimeoutException e){
                Log.d("d","----------timeout------------");
            } catch (SocketException e) { //서버가 연결중에 끊겼을때 작동한다.

                //인터넷 끊은 경우 : 재연결
                //로그아웃 될 경우

                //문제 1. 인터넷이 끊겼을때 서버에서 끊어지지 않는다.
                //다시 연결시 끊어지지 않고 새로운 클라이언트 소켓이 연결됨.
                //그리고 또다른 소켓이 새로 연결이 됨. 이 시간이 5초 정도 걸림
                //그 이후 인터넷을 껏을때 다시 정상작동함.

                //와이파이를 끊고 다시 켰을때 새 소켓이 생성되고 연결이 되었어.
                //근데 갑자기 연결이 끊겨서 재연결하는 상황이 된거야..

                Log.d("d","----------server down------------");//??????
                Log.d("d","----------server down------------" + logoutclosed);//??????
                //30초 후에 이놈이 뜨고, 서버로 소켓을 연결했어??? 어떻게??

                //로그아웃 닫기 변수 : true 로그아웃으로 닫기, false 다른 상태일때 닫아진 경우(인터넷이 끊긴경우)
                if(!logoutclosed) { //인터넷이 끊긴 경우
                    input.close(); //전송 닫음
                    outwriter.close(); //출력 닫음
                    GlobalClass.getsocket().close();

                    //연결이 끊고 socket null표시
                    socket = null;
                    GlobalClass.setsocket(socket);

                    //Log.d("d", "----------logoutclosed err------------");
                    Log.d("d", "----------logoutclosed err------------"+logoutclosed);
                    Log.d("d", "----------logoutclosed err------------"+socket);
                    //Log.d("d", "----------logoutclosed err------------");

                    repeatsocketconnect(2);
                }else{ //로그아웃으로 닫은 경우
                    //연결이 끊고 socket null표시
                    socket = null;
                    GlobalClass.setsocket(socket);

                    //서비스를 해제한다. 삭제..한다.
                    stopSelf();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

                    //소켓서버 다운시킨경우
                Log.d("d", "----------client err------------");

                    input.close(); //전송 닫음
                    outwriter.close(); //출력 닫음
                    GlobalClass.getsocket().close();

                    //연결을 끊고 socket null표시
                    socket = null;
                    GlobalClass.setsocket(socket);

                if(!logoutclosed) {
                    repeatsocketconnect(2);
                }
            } finally {
            }
        }
    }
    //type 1.처음 연결하는데 연결이 안될 경우 2. 연결중에 끊긴경우
    public void repeatsocketconnect(int type){

        //연결 재시도!
        while(true){
            try {
                //연결이 안되었을때만 연결시도 하고, 연결되면 연결시도 멈춤
                if(socket == null || !socket.isConnected()){ //연결이 안된 경우

                    if(GlobalClass.popnum == 0){
                        //핸들러로 연결
                        Message msg = errHandler.obtainMessage();
                        msg.what = 1;
                        msg.arg1 = 0; //인터넷 연결 안되었을 경우
                        errHandler.sendMessage(msg);
                        GlobalClass.popnum = 1;
                    }

                    //소켓 연결 시도
                    socket = new Socket();
                    socketAddress = new InetSocketAddress(ip, port);
                    socket.connect(socketAddress, timeout); //소켓 Connect 연결할 때 TimeOut 설정

                    //Log.d("d", "----------sleep err------------ 재연결중");
                    Log.d("d", "----------sleep err------------ 재연결중"+ socket.isConnected());
                    //Log.d("d", "----------sleep err------------ what?");
                    Thread.sleep(3000);



                }else{ //연결 된 경우
                    //Log.d("d", "----------sleep err------------ 연결됨"+ socket.isConnected());
                    //Log.d("d", "----------sleep err------------ 연결됨");

                    //핸들러로 연결
                    Message msg = errHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = 1;
                    errHandler.sendMessage(msg);

                    //글로벌 클래스에 넣어서 관리한다.
                    GlobalClass.setsocket(socket); //소켓만 사용

                    //연결중 끊긴경우
                    if(type == 2){
                        receptionconnect(2);
                    }
                    break; //여기서 연결이 되면 와일문에서 빠져나와야한다.
                }
            } catch (InterruptedException | IOException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }


    //에러를 보여주기위한 핸들러
    public void MethoderrHandler() {

        //광고 이미지 변경 핸들러
        errHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO : process device message.

                if (msg.what == 1) {
                    //arg1
                    //팝업띄우기
                  //  Toast.makeText(GlobalClass, "네트워크가 불안정하여 재연결 중입니다. ", Toast.LENGTH_SHORT).show();

                    if (msg.arg1 == 0) { //연결 안됨
                        //데이터 담아서 팝업(액티비티) 호출
                        Intent intent = new Intent(mContext, PopupActivity.class);

                        //mContext.startActivityForResult(intent, 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("data", "네트워크가 불안정하여 재연결 중입니다. ");
                        mContext.startActivity(intent);

                          //Toast.makeText(GlobalClass, "네트워크가 불안정하여 재연결 중입니다. ", Toast.LENGTH_SHORT).show();

//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setTitle("네트워크 연결");
//                        builder.setMessage("네트워크가 불안정하여 재연결 중입니다.");
//                        builder.show();


                    }else{ //연결 됨
                        Toast.makeText(GlobalClass, "네트워크가 연결되었습니다. ", Toast.LENGTH_SHORT).show();
                        GlobalClass.popnum = 0;

                        //((PopupActivity) PopupActivity.oActivity_popup).mOnClose();
                    }
                }
            }
        };
    }




    // 채팅 받은 메시지 출력
    class msgUpdate implements Runnable {
        private String msg;
        public msgUpdate(String str) {
            this.msg = str;
        }
        public void run() {

                // Log.d("msg", String.valueOf(msg));
                 Log.d("============= 화면출력 : ", msg);
                // Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

                //받아온 채팅 내용 정보를 json으로파싱한다
                JsonElement jelement = new JsonParser().parse(msg);
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jarray_chatdata = jobject.getAsJsonArray("Sendinfo");


                //Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(0))); //commend
//            Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(1))); //roomid
//            Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(2))); //userid
//            Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(3))); //msg
//            Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(4))); //regdate
//            Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(5))); //프로필 이미지
//            Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(6))); //유저 수
                //Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(7))); //안읽음 갯수
                //Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(8))); //채팅 총 갯수
                //Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(9))); //유저 이름
                //Log.d("============= 화면출력 : ", String.valueOf(jarray_chatdata.get(10))); //이미지 체크

                String commend = String.valueOf(jarray_chatdata.get(0)).substring(1);
                String commend_sub = commend.substring(0, commend.length() - 1);

                String uid = String.valueOf(jarray_chatdata.get(2)).substring(1);
                String uid_sub = uid.substring(0, uid.length() - 1);


                //게시글 좋아요, 댓글입력, 댓글 좋아요 시 해당 클라이언트로 메세지를 줄 것이다.
                if (commend_sub.equals("NOTISEND")) {

                    createNotification(commend_sub, jarray_chatdata);

                    if (GlobalClass.mainnavigation_num != 0) { //프레그먼트에 남아 있을때만 실행
                        //알림 아이콘의 이미지를 변경해줄 것.....
                        ((MainActivity) MainActivity.oActivity_main).NotificationChk(GlobalClass.mainnavigation_num);
                    }

                } else if (commend_sub.equals("CROOM") || commend_sub.equals("EROOM") || commend_sub.equals("CHATSEND") || commend_sub.equals("REXIT") || commend_sub.equals("INVITEROOM")) { //채팅일때
                    String roomtype = String.valueOf(jarray_chatdata.get(12)).substring(1);
                    String roomtype_sub = roomtype.substring(0, roomtype.length() - 1);

//                if (GlobalClass.mainnavigation_num != 0) { //프레그먼트에 남아 있을때만 실행
//                    //알림 아이콘의 이미지를 변경해줄 것.....
//                    ((MainActivity) MainActivity.oActivity_main).NotificationChk(GlobalClass.mainnavigation_num);
//                }


                    Log.d("============= 화면출력  chatroomaccess : ", String.valueOf(GlobalClass.chatroomaccess));
                    Log.d("============= 화면출력 myclasschatroomaccess : ", String.valueOf(GlobalClass.myclasschatroomaccess));


                    //데이터를 받아서 방에 넘겨주자.
                    //채팅방에 입장했을때만 접근해서 채팅 리스트에 추가해준다.
                    if (GlobalClass.chatroomaccess == 1 && GlobalClass.myclasschatroomaccess == 0) { //과외문의 방에 연결


                        if (roomtype_sub.equals("1")) { //과외문의
                            try {
                                ((Classinquiryroomactivity) Classinquiryroomactivity.oActivity_classinquiryroom).recievemessage(jarray_chatdata);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (GlobalClass.chatroomaccess == 0 && GlobalClass.myclasschatroomaccess == 1) { //내과외 방 연결

                        if (roomtype_sub.equals("2")) { //내과외
                            try {
                                ((Myclassroomactivity) Myclassroomactivity.oActivity_myclassroom).recievemessage(jarray_chatdata);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else { //채팅방이 아닌 외부에 있을 때
                        Log.d("============= 화면출력 : ", "3");

                        //채팅일때만 알람을띄움 내 아이디가 아닐때만 띄움 // 방에 접근이 안되어있을때만 띄움 방에 접근한 상태면 띄우지 않음.
                        if (commend_sub.equals("CHATSEND") && !uid_sub.equals(Sessionlist.get(1).get(0))) {
                            //메세지를 받았을때 알림을 띄운다.
                            createNotification(commend_sub, jarray_chatdata);
                        } else if (commend_sub.equals("CROOM") && !uid_sub.equals(Sessionlist.get(1).get(0))) {
                            //메세지를 받았을때 알림을 띄운다.
                            createNotification(commend_sub, jarray_chatdata);
                        }
                    }


                    //메인 프레그먼트 페이지 위치를 글로벌 변수에서 알려줌. 과외 문의, 내과외 일때만 작동
                    if (GlobalClass.mainnavigation_num == 3 || GlobalClass.mainnavigation_num == 4) {
                        Log.d("============= 화면출력 : ", " 전달????");
                        //main activity 접근
                        ((MainActivity) MainActivity.oActivity_main).ConnectFragment(GlobalClass.mainnavigation_num);
                    }

                } else if (commend_sub.equals("CONNECTROOM")) {
                    if (GlobalClass.chatroomaccess == 1 && GlobalClass.myclasschatroomaccess == 0) { //과외문의 방에 연결
                        try {
                            ((Classinquiryroomactivity) Classinquiryroomactivity.oActivity_classinquiryroom).recievemessage(jarray_chatdata);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (GlobalClass.chatroomaccess == 0 && GlobalClass.myclasschatroomaccess == 1) { //내과외 방 연결
                        try {
                            ((Myclassroomactivity) Myclassroomactivity.oActivity_myclassroom).recievemessage(jarray_chatdata);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (commend_sub.equals("WBSEND")) {
                    //WBSEND

                    if (GlobalClass.conferenceroomaccess == 1 && GlobalClass.myclasschatroomaccess == 0) { //화상 수업 방 연결
                        //회의 페이지에서만 반응하도록 할 것.
                        try {
                            ((ConferenceRoomPage) ConferenceRoomPage.oActivity_conferenceroom).recievemessage(jarray_chatdata);
                        } catch (JSONException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
           // }
        }
    };

    //데이터를 받았을때 알람을 띄움
    //알람을 생성한다.
    @SuppressLint("WrongConstant")
    private void createNotification(String commend_sub, JsonArray jarray_chatdata) {

        String sendtitle = null;
        String senddocument = null;

        String uid = String.valueOf(jarray_chatdata.get(2)).substring(1);
        String uid_sub = uid.substring(0, uid.length() - 1);

        //메세지 내용 가져오기
        String msg = String.valueOf(jarray_chatdata.get(3)).substring(1);
        String msg_sub = msg.substring(0, msg.length() - 1);

        String regdate = String.valueOf(jarray_chatdata.get(4)).substring(1);
        String regdate_sub = regdate.substring(0, regdate.length() - 1);

        String nid = String.valueOf(jarray_chatdata.get(5)).substring(1);
        String nid_sub = nid.substring(0, nid.length() - 1);

        int curTime = (int) System.currentTimeMillis()/1000;
        PendingIntent mPendingIntent = null;
        if(commend_sub.equals("NOTISEND")){

            sendtitle = "알림";
            senddocument = msg_sub;

            //알림을 클릭했을때 이동하는 부분
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("MoveValue", "1");
            intent.putExtra("nid", nid_sub);
            intent.putExtra("uid", uid_sub);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //알림을 클릭했을때 이동하는 부분
//            Intent intent = new Intent(getApplicationContext(), Nboardview.class);
//            intent.putExtra("nid", nid_sub);
//            intent.putExtra("uid", uid_sub);


            //세개 넣어야 클릭 가능
            //nidval = intent.getExtras().getString("nid");
            //nbaorduid = intent.getExtras().getString("uid");


            mPendingIntent = PendingIntent.getActivity(
                    this,
                    curTime,
                    // 보통 default값 0을 삽입
                    intent,
                    PendingIntent.FLAG_MUTABLE);


        }else if(commend_sub.equals("CROOM") || commend_sub.equals("CHATSEND")) { //채팅일때
            String chatcount = String.valueOf(jarray_chatdata.get(8)).substring(1);
            String chatcount_sub = chatcount.substring(0, chatcount.length() - 1);
            if(chatcount_sub.equals("")){
                chatcount_sub = "0";
            }

            String username = String.valueOf(jarray_chatdata.get(9)).substring(1);
            String username_sub = username.substring(0, username.length() - 1);

            String imgchk = String.valueOf(jarray_chatdata.get(10)).substring(1);
            String imgchk_sub = imgchk.substring(0, imgchk.length() - 1);

            sendtitle = username_sub+ " " +regdate_sub;
            if(imgchk_sub.equals("1")){
                senddocument = "사진을 전송했습니다.";
            }else{
                senddocument = msg_sub;
            }

            String roomtype = String.valueOf(jarray_chatdata.get(12)).substring(1);
            String roomtype_sub = roomtype.substring(0, roomtype.length() - 1);

            String rid = String.valueOf(jarray_chatdata.get(1)).substring(1);
            String rid_sub = rid.substring(0, rid.length() - 1);

            //알림을 클릭했을때 이동하는 부분
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            if(roomtype_sub.equals("1")){ //과외문의
                intent.putExtra("MoveValue", "2"); //과외 문의
            }else{ //내과외
                intent.putExtra("MoveValue", "3"); //내 과외
            }

            intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
            intent.putExtra("roomidx", rid_sub);
            intent.putExtra("Tchatcount", chatcount_sub); //이 값은 jdbc에서 만들어서 보내줄 것.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //알림을 클릭했을때 이동하는 부분
            //Intent intent = new Intent(getApplicationContext(),Classinquiryroomactivity.class);
            //intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
            //intent.putExtra("roomidx", rid_sub);
            //intent.putExtra("Tchatcount", chatcount_sub); //이 값은 jdbc에서 만들어서 보내줄 것.

            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mPendingIntent = PendingIntent.getActivity(
                    this,
                    curTime,
                    // 보통 default값 0을 삽입
                    intent,
                    PendingIntent.FLAG_MUTABLE);
        }

        CharSequence name = getString(R.string.channel_name);

        //알림을 띄우기위해 만드는 부분
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "requestclass");

        builder.setSmallIcon(R.drawable.alerticon);
        builder.setContentTitle(sendtitle);
        builder.setContentText(senddocument);
        builder.setWhen(curTime);
        builder.setContentIntent(mPendingIntent); // 상세보기를 누르지 않더라도 mPendingIntent 효과 발동
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);

       // builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("requestclass", name, NotificationManager.IMPORTANCE_HIGH));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        //id를 변경해 주어서 알림이 중복되지 않도록 설정

        notificationManager.notify(curTime, builder.build());
    }

    //알람타이머로 재실행 해주는 부분
    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);

        Intent intent = new Intent(this, AlarmRecever.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,PendingIntent.FLAG_IMMUTABLE);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

    //자동으로 꺼질때
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!logoutclosed) { //로그아웃 아닐때만 까지만 동작한다.
            setAlarmTimer();
        }
    }
    //로그아웃시 변수 변경
    public void LogoutClosed(){
        logoutclosed = true;
        Log.d("logoutclosed",String.valueOf(logoutclosed));
    }
}


