package com.example.hometeacher.shared;

import android.app.Application;
import android.content.Intent;

import com.example.hometeacher.ArraylistForm.ServerWhiteboardSendForm;
import com.example.hometeacher.SocketService;

import java.net.Socket;
import java.util.ArrayList;


public class GlobalClass extends Application {

    //글로벌 클래스를 활용하여 소켓 연결한 정보를 담아두었음. 엑티비티가 사라져도 소켓은 남아있는 상태임.
    public static Socket socket;
    //public static Intent iService;
    public static SocketService mService;
    //public boolean mBound = false;

    public int chatroomaccess = 0; //문의하기 채팅방에 접근했을때 1로 변경: 1일때만 채팅 메세지를 바로 띄우도록 함
    public int myclasschatroomaccess = 0; //내 과외 채팅방에 접근했을때 1로 변경: 1일때만 채팅 메세지를 바로 띄우도록 함
    public int conferenceroomaccess = 0; //화상회의 수업 접근했을때 1로 변경: 1일때만 채팅 메세지를 바로 띄우도록 함


    public int mainnavigation_num = 0; // 메인 프레그먼트 위치를 나타냄

    public ArrayList<ServerWhiteboardSendForm> QueSavelist_whiteboard = new ArrayList<ServerWhiteboardSendForm>(); //서버에 전송하기 위해 담는 데이터

    public int popnum = 0;
   // public String FcmToken = "";


    // public static PrintWriter outwriter; //사용하지 않음.
   // OAuthLogin mOAuthLoginModule; //네이버 로그인 api
   // GoogleSignInClient mGoogleSignInClient; //구글 로그인 api

    @Override   //오버라이딩 해서 onCreate()를 만들어 줍니다. 여느 클래스와 똑같이요 ㅎㅎ
    public void onCreate() {
        super.onCreate();
       // socket = "";
       // outwriter = "";
       // OHTERID = "";


        //카카오 로그인 api 실행 - 변순선언 없이 사용 가능
        //KakaoSdk.init(this, getString(R.string.kakao_key));
    }

    @Override  //이건 선택사항인데 일단 추가해 줍니다. 데이터 공간 낭비를 방지하기 위해 추가하고 나중에 필요하면 선언하세요.
    public void onTerminate() {
        super.onTerminate();
        //instance = null;
    }

    // 초기화 함수입니다. 처음 선언을 해주면 안정적으로 초기화 되서 변수가 안정적입니다.
    public void Init() {
       // COUPLEKEY = "";
       // MYID = "";
       // OHTERID = "";

    }

    //클래스를 선언한 뒤, 다른 액티비티에서 사용될 함수 입니다. 이건 verdiosn이라는 글로벌 변수에 flag값을 넣게다는 뜻입니다.
//다른 액티비티에서 선언 방법은 밑에 써드릴게요
    public void setsocket(Socket value){
        socket = value;}

    //이것은 저장된 값을 불러오는 함수입니다.
    public Socket getsocket(){return socket;}

    //public void setService(Intent value){
    //    iService = value;}
    //이것은 저장된 값을 불러오는 함수입니다.
   // public Intent getService(){return iService;}

    //public void setBound(boolean value){this.mBound = value;}
    //이것은 저장된 값을 불러오는 함수입니다.
  //  public boolean getBound(){return this.mBound;}

    //public ArrayList<ServerWhiteboardSendForm> getquelist(){return this.QueSavelist_whiteboard;}

   // public void setoutwriter(PrintWriter value){this.outwriter = value;}

    //이것은 저장된 값을 불러오는 함수입니다.
    //public PrintWriter getoutwriter(){return this.outwriter;}
//
//
//    public void setotherid(String value){this.OHTERID = value;}
//
//    //이것은 저장된 값을 불러오는 함수입니다.
//    public String getotherid(){return OHTERID;}

    //네이버 로그인 api 글로벌 변수로 선언
   // public void setnaveroauth(OAuthLogin mOAuthLoginModule){this.mOAuthLoginModule = mOAuthLoginModule; }
    //public OAuthLogin getnaveroauth(){return mOAuthLoginModule; }

    //구글 로그인 api 글로벌 변수로 선언
    //public void setgoogleoauth(GoogleSignInClient mGoogleSignInClient){this.mGoogleSignInClient = mGoogleSignInClient; }
   // public GoogleSignInClient getgoogleoauth(){return mGoogleSignInClient; }


}
