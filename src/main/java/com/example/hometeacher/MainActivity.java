package com.example.hometeacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.hometeacher.Nboard.Nboardview;
import com.example.hometeacher.navigation.HomeFragment;
import com.example.hometeacher.navigation.LoginFragment;
import com.example.hometeacher.navigation.MyclassFragment;
import com.example.hometeacher.navigation.MypageFragment;
import com.example.hometeacher.navigation.RequestclassFragment;
import com.example.hometeacher.navigation.SearchclassFragment;
import com.example.hometeacher.shared.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private Fragment LoginFragment, HomeFragment, SearchclassFragment, RequestclassFragment, MyclassFragment, MypageFragment;

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    Session oSession; //자동로그인을 위한 db
    Context mContext_main;
    public static Activity oActivity_main;
    ArrayList<ArrayList<String>> Sessionlist;

    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //public int REQUESTCODE = 100;// 100 101 102
    //public int RESULTCODE1 = 1;

    BottomNavigationView bottomView;

    ArrayList<String> firsttype = new ArrayList<>(Arrays.asList("0", "0", "0", "0", "0")); //프레그먼트로 처음접근했을때와 그 이후를 구분해주기 위함

   // final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
   // RetrofitService retrofitService;
   // Call<String> call;

    Intent serviceintent;

    String MoveValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication(); //글로벌 클래스 선언

        createNotificationChannel();


        mContext_main = this;
        oActivity_main = this;
        oSession = new Session(this);





        //자동 로그인 하기
        Sessionlist = oSession.Getoneinfo("0");

        FrameLayout frameLayout = findViewById(R.id.framebox);
        bottomView = findViewById(R.id.bottommenubox);

        bottomView.setOnNavigationItemSelectedListener(listener);

        LoginFragment = new LoginFragment();
        HomeFragment = new HomeFragment();
        SearchclassFragment = new SearchclassFragment();
        RequestclassFragment = new RequestclassFragment();
        MyclassFragment = new MyclassFragment();
        MypageFragment = new MypageFragment();




        //알림 데이터가 있는지를 구분하여 intent가 null이면 그냥 띄우고 값이 존재하면 한번 더 이동을 시킨다.
        //인텐트 데이터를 받는다.
        Intent getintent = getIntent();
        //Log.d("------------getintent----------", String.valueOf(getintent.getExtras()));
        //Log.d("------------getintent----------", String.valueOf(getintent.getData()));

        //Toast.makeText(GlobalClass, String.valueOf(getintent.getData()), Toast.LENGTH_SHORT).show();  //둘다 null
        //Toast.makeText(GlobalClass, String.valueOf(getintent.getExtras()), Toast.LENGTH_SHORT).show();  //데이터 사이즈가 64 -> 164? 사이즈 변화
        //Toast.makeText(GlobalClass, String.valueOf(getintent.getDataString()), Toast.LENGTH_SHORT).show();  //둘다 null
        //Toast.makeText(GlobalClass, String.valueOf(getintent.getAction()), Toast.LENGTH_SHORT).show();  //알림으로 전송 : null이 뜬다.
//
//        if(getintent.getAction() == null){ //알림으로 받아온 데이터라는 것
//            Toast.makeText(GlobalClass, "받아온 데이타", Toast.LENGTH_SHORT).show();
//        }

        //로그인했을때 에러 NULL이 뜬다.

        if(getintent.getAction() == null){
            MoveValue = getintent.getExtras().getString("MoveValue");

            Log.d("------------getintent----------", String.valueOf(MoveValue));
            //알림으로 온 값이 있다면
            if(MoveValue.equals("1")) { //게시글 보기로 이동
                getSupportFragmentManager().beginTransaction().replace(R.id.framebox, HomeFragment).commit();

                String nid = getintent.getExtras().getString("nid");
                String uid = getintent.getExtras().getString("uid");

                Log.d("------------getintent----------", String.valueOf(nid));
                Log.d("------------getintent----------", String.valueOf(uid));

                //알림을 클릭했을때 이동하는 부분
                Intent intent = new Intent(getApplicationContext(), Nboardview.class);
                intent.putExtra("nid", nid);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }else if(MoveValue.equals("2")) { //과외문의하기 채팅방으로 이동
                getSupportFragmentManager().beginTransaction().replace(R.id.framebox, RequestclassFragment).commit();
                String roommaketype = getintent.getExtras().getString("roommaketype");
                String roomidx = getintent.getExtras().getString("roomidx");
                String Tchatcount = getintent.getExtras().getString("Tchatcount");

                Log.d("------------getintent----------", String.valueOf(roommaketype));
                Log.d("------------getintent----------", String.valueOf(roomidx));
                Log.d("------------getintent----------", String.valueOf(Tchatcount));

                //알림을 클릭했을때 이동하는 부분
                Intent intent = new Intent(getApplicationContext(), Classinquiryroomactivity.class);
                intent.putExtra("roommaketype", roommaketype);
                intent.putExtra("roomidx", roomidx);
                intent.putExtra("Tchatcount", Tchatcount);
                startActivity(intent);
            }else if(MoveValue.equals("3")) { //내 과외 채팅방으로 이동

                getSupportFragmentManager().beginTransaction().replace(R.id.framebox, MyclassFragment).commit();
                String roommaketype = getintent.getExtras().getString("roommaketype");
                String roomidx = getintent.getExtras().getString("roomidx");
                String Tchatcount = getintent.getExtras().getString("Tchatcount");

                Log.d("------------getintent----------", String.valueOf(roommaketype));
                Log.d("------------getintent----------", String.valueOf(roomidx));
                Log.d("------------getintent----------", String.valueOf(Tchatcount));

                //알림을 클릭했을때 이동하는 부분
                Intent intent = new Intent(getApplicationContext(), Myclassroomactivity.class);
                intent.putExtra("roommaketype", roommaketype);
                intent.putExtra("roomidx", roomidx);
                intent.putExtra("Tchatcount", Tchatcount);
                startActivity(intent);
            }else if((MoveValue.equals("0"))){ // 로그인했을때
                getSupportFragmentManager().beginTransaction().replace(R.id.framebox, HomeFragment).commit();
            }
        }else{ //바로 입장 했을때
            getSupportFragmentManager().beginTransaction().replace(R.id.framebox, HomeFragment).commit();
        }


        //getSupportFragmentManager().beginTransaction().replace(R.id.framebox, HomeFragment).commit();

        //Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //권한이 부여되면 PERMISSION_GRANTED 거부되면 PERMISSION_DENIED 리턴//권한 요청 할 필요가 있는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {

                Log.d("", "permissionCheck");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);


            }
        }

        //퍼미션 체크하는 부분 / -1 퍼미션 안되어있음
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR);
        Log.d("permissionCheck", String.valueOf(permissionCheck));


        //글로벌 변수에 값이 null일때만 소켓 연결을 시도한다.
        //null이 되는 순간은? 앱을 다시 깔았다거나, 끊겼을때 -> 끊겼다는것은 더 이상 해당 클라이언트 주소를 모른다는 것.
        if(GlobalClass.getsocket() == null){
            if (!Sessionlist.isEmpty()) { //로그인 상태일때만 실행

                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
                boolean isWhiteListing = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
                }
                if (!isWhiteListing) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                }

                //소켓을 연결한다.
                serviceintent =  new Intent(getApplicationContext(), SocketService.class);
                startService(serviceintent);
            }
        }
        Log.d("getsocket-------", String.valueOf(GlobalClass.getsocket()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("permissions11111", String.valueOf(requestCode));

    }


    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ArrayList<ArrayList<String>> Sessionlist = oSession.Getoneinfo("0");
            //Log.d("Sessionlist", String.valueOf(Sessionlist));
            switch (item.getItemId()) {
                case R.id.hometab:
                    getSupportFragmentManager().beginTransaction().replace(R.id.framebox, HomeFragment).commit();

                    if(firsttype.get(0).equals("1")){
                        HomeFragment tfhome = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
                        tfhome.upscroll();
                    }
                    GlobalClass.mainnavigation_num = 1;

                    return true;
                case R.id.searchclasstab:
                    getSupportFragmentManager().beginTransaction().replace(R.id.framebox, SearchclassFragment).commit();

                    if(firsttype.get(1).equals("1")){
                         SearchclassFragment tfsearch = (SearchclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
                         tfsearch.upscroll();
                    }
                    GlobalClass.mainnavigation_num = 2;

                    return true;
                case R.id.classrequesttab:

                    if (!Sessionlist.isEmpty()) { //쉐어드에 값이 있다면

                        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, RequestclassFragment).commit();

                        if(firsttype.get(2).equals("1")){
                            RequestclassFragment tfsearch = (RequestclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
                            tfsearch.upscroll();
                        }
                        GlobalClass.mainnavigation_num = 3;

                        return true;
                    } else { //값이 없으면
                        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, LoginFragment).commit();
                        return true;
                    }



                case R.id.myclasstab:

                    if (!Sessionlist.isEmpty()) { //쉐어드에 값이 있다면

                        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, MyclassFragment).commit();

                        if(firsttype.get(3).equals("1")){
                            MyclassFragment tfsearch = (MyclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
                            tfsearch.upscroll();
                        }
                        GlobalClass.mainnavigation_num = 4;
                        return true;
                    } else { //값이 없으면
                        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, LoginFragment).commit();
                        return true;
                    }

                case R.id.mypagetab:

                    if (!Sessionlist.isEmpty()) { //쉐어드에 값이 있다면
                        GlobalClass.mainnavigation_num = 5;
                        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, MypageFragment).commit();
                        return true;
                    } else { //값이 없으면
                        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, LoginFragment).commit();
                        return true;
                    }

            }
            return false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        // Toast myToast = Toast.makeText(getApplicationContext(), "리다이렉트.", Toast.LENGTH_SHORT);
        // myToast.show();
        Log.d("-----onStart-----","onStart");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //main 위치 파괴한다.
        GlobalClass.mainnavigation_num = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-----onResume-----","onResume");

        //소켓 연결하는 부분 - 글로벌 변수에 값이 없는경우, 로그인 되어있는 경우에만 연결한다.
        if(GlobalClass.getsocket() == null){
            //if (GlobalClass.getBound()) {
                if (!Sessionlist.isEmpty()) { //로그인 상태일때만 실행
                    //Log.d("----getBound3-----",String.valueOf(GlobalClass.getBound()));

                    //ArrayList<ArrayList<String>> Sessionlist = oSession.Getoneinfo("0");
                   // GlobalClass.getService().SocketConnect();

                }
            //}
        }
    }

    //처음 접근시 체크해주는 함수
    public boolean Firstaccesschk(int type){
        GlobalClass.mainnavigation_num = type;
        Log.d("firsttype", String.valueOf(firsttype));
       // Toast myToast = Toast.makeText(getApplicationContext(), "activity로 전송."+ type, Toast.LENGTH_SHORT);
       // myToast.show();


        //클릭한거 외에 0으로 변경하기
        for(int i=0;i<firsttype.size();i++){ // 0 1 2 3 4
            int val = i+1;

            if(type != val){
                firsttype.set(i, "0");
            }
        }

        //클릭한 데이터 1로 변경
        switch (type) {
            case 1:
                firsttype.set(0, "1");
                return true;
            case 2:
                firsttype.set(1, "1");
                return true;
            case 3:
                firsttype.set(2, "1");
                return true;
            case 4:
                firsttype.set(3, "1");
                return true;
            case 5:
                firsttype.set(4, "1");
                return true;
        }
        return false;
    }

    //fragment에서 mainpage 접근하여 함수 사용
    public void replaceFragment() {
        //강제로 로그인 페이지로 이동
        getSupportFragmentManager().beginTransaction().replace(R.id.framebox, LoginFragment).commit();
    }


    //알림이 온것을 체트하여 메뉴 아이콘을 변경하기 위함
    public void NotificationChk(int value){

        if(value == 1){
            HomeFragment tf = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.ChangeToolbarmenu();
        }else if(value == 2){
            SearchclassFragment tf = (SearchclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.ChangeToolbarmenu();
        }else if(value == 3){
            RequestclassFragment tf = (RequestclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.ChangeToolbarmenu();
        }else if(value == 4){
            MyclassFragment tf = (MyclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.ChangeToolbarmenu();
        }else if(value == 5){
            MypageFragment tf = (MypageFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.ChangeToolbarmenu();
        }

    }
    //과외문의 채팅 방 데이터를 다시 불러와주기 위함
    public void ConnectFragment(int value){

        if(value == 1){
        }else if(value == 2){

        }else if(value == 3){
            RequestclassFragment tf = (RequestclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.InitRoomlist();
        }else if(value == 4){
            MyclassFragment tf = (MyclassFragment) getSupportFragmentManager().findFragmentById(R.id.framebox);
            tf.InitRoomlist();
        }else if(value == 5){

        }
    }
    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;

    //뒤로가기 버튼 눌렀을 때
    @Override
    public void onBackPressed() { //뒤로가기 두번클릭시 앱이 지워짐.
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }


    //안드로이드 푸쉬 알림 채널 개설
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("requestclass", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}