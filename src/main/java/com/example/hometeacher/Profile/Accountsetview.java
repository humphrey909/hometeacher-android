package com.example.hometeacher.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.SocketSend;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.SocketService;
import com.example.hometeacher.shared.Session;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Accountsetview extends AppCompatActivity {

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ImageView cameraimg, profileimg;
    ImageButton nameeditbtn, emaileditbtn, passwordeditbtn, accountdeleteeditbtn;
    TextView nametext, emailtext, passwordtext;

    Session oSession; //자동로그인을 위한 db
    ArrayList<ArrayList<String>> Sessionlist;
    Context oContext;

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    final public int REQUESTCODE_EDIT = 100;// 100 101 102
    final public int RESULTCODE_EDIT = 1;

    int RESULTCODE = 1;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetview);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기



        division();

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        int addtype = intent.getExtras().getInt("type");
       // addprofileidx = intent.getExtras().getString("profileidx");

        Log.d("addtype", "----------------------건너온 정보들------------------------");
        Log.d("addtype", String.valueOf(addtype));


        //Log.d("addprofileidx", String.valueOf(addprofileidx));

    }

    public void division(){
        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언

        this.activity = this;
        oContext = this;
        oSession = new Session(oContext);

        Sessionlist = oSession.Getoneinfo("0");


        profileimg = (ImageView) this.findViewById(R.id.profileimg);
        cameraimg = (ImageView) this.findViewById(R.id.cameraimg);
        nameeditbtn = (ImageButton) this.findViewById(R.id.nameeditbtn);
        emaileditbtn = (ImageButton) this.findViewById(R.id.emaileditbtn);
        passwordeditbtn = (ImageButton) this.findViewById(R.id.passwordeditbtn);
        accountdeleteeditbtn = (ImageButton) this.findViewById(R.id.accountdeleteeditbtn);
        nametext = (TextView) this.findViewById(R.id.nametext);
        emailtext = (TextView) this.findViewById(R.id.emailtext);
        passwordtext = (TextView) this.findViewById(R.id.passwordtext);



        //Sessionlist
       // Log.d("--------Sessionlist-----------", String.valueOf(Sessionlist));
        //Log.d("--------Sessionlist-----------", String.valueOf(Sessionlist.get(1).get(4)));
        nametext.setText(Sessionlist.get(1).get(3));
        emailtext.setText(Sessionlist.get(1).get(1));
        passwordtext.setText("******");

        //nameeditbtn, emaileditbtn, passwordeditbtn, accountdeleteeditbtn;
        //이름변경 버튼
        nameeditbtn = (ImageButton) findViewById(R.id.nameeditbtn);
        nameeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(getApplicationContext(), ChangeAccountinfo.class);
//                intent.putExtra("edittype", "name");
//                intent.putExtra("value", nametext.getText());
//                startActivity(intent);


                Intent intent = new Intent(getApplicationContext(), ChangeAccountinfo.class);
                intent.putExtra("edittype", "name");
                intent.putExtra("value", nametext.getText());
                startActivityForResult(intent, REQUESTCODE_EDIT);
            }
        });

        //이메일변경 버튼
        emaileditbtn = (ImageButton) findViewById(R.id.emaileditbtn);
        emaileditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(oContext, "이메일은 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), ChangeAccountinfo.class);
                //intent.putExtra("edittype", "이메일");
                //intent.putExtra("value", emailtext.getText());
                //startActivity(intent);
            }
        });

        //패스워드 변경 버튼
        passwordeditbtn = (ImageButton) findViewById(R.id.passwordeditbtn);
        passwordeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Pwchange_loginstate.class);
                //intent.putExtra("edittype", "password");
                startActivity(intent);
            }
        });

        //계정 탈퇴 버튼
        accountdeleteeditbtn = (ImageButton) findViewById(R.id.accountdeleteeditbtn);
        accountdeleteeditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Intent intent = new Intent(getApplicationContext(), ChangeAccountinfo.class);
               // startActivity(intent);
            }
        });

        //문자열 변환
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("uid", uid);
        RequestBody imgtype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));
        requestMap.put("imgtype", imgtype);

        //계정 이미지 가져오는 통신
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.profilegetimglist(
                2, requestMap
        );
        RestapiResponse(); //응답


        //카메라 버튼
        cameraimg = (ImageView) findViewById(R.id.cameraimg);
        cameraimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);

                builder.setTitle("프로필 사진 업로드");

                builder.setItems(R.array.imgckarray, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        //String[] items = getResources().getStringArray(R.array.imgckarray);
                        //Log.d("items", String.valueOf(items));
                        Log.d("pos", String.valueOf(pos));

                        if(pos == 0){ //사진첩
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            // intent.putExtra("crop", true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //멀티로 선택할 수 있는 기능

                            startActivityForResult(intent, OPEN_GALLERY);
                        }else{ //카메라
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);

                        }
                       // if (mListener != null) {
                        //    int type = 1;
                        //    mListener.onItemClick(view, position, pos, type, "-") ; //리스너 통해서 값을 activity로 전달
                        //}
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                // Intent intent = new Intent(getApplicationContext(), Accountsetview.class); //선생일때
               // startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {
                        // Bundle로 데이터를 입력
                        Bundle extras = intent.getExtras(); //카메라 촬영후 가져오는 데이터

                        // Bitmap으로 컨버전
                        Bitmap imageBitmap = (Bitmap) extras.get("data"); //bitmab으로 변경
                        Uri uridata = getImageUri(this, imageBitmap); //bitmap을 uri로 변경

                        //이미지 선택후 크롭화면 나오도록 설정
                        CropImage.activity(uridata).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                                .start(Accountsetview.this);
                    }
                    break;

                case OPEN_GALLERY:
                    if (resultCode == RESULT_OK) {
                        //이미지 선택후 크롭화면 나오도록 설정
                        CropImage.activity(intent.getData()).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                                .start(Accountsetview.this);
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: //크롭한 후 이미지 받아오는 부분
                    Log.d("crop!!1", String.valueOf(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE));
                    Log.d("crop!!2", String.valueOf( CropImage.getActivityResult(intent)));

                    CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                    if (resultCode == Activity.RESULT_OK) {

                        File fileCacheDir = getCacheDir();
                        String getCacheDir = fileCacheDir.getPath();

                        Log.d("crop! - getCacheDir", getCacheDir);


                        Log.d("crop!!3", String.valueOf(result.getUri()));
                        Log.d("사진에서 선택한 사진 경로", result.getUri().getPath());

                        //profileimg.setImageURI(result.getUri());
                        //가져온 데이터 하나를 바로 출력
                        Picasso.get()
                                .load(result.getUri()) // string or uri 상관없음
                                .resize(200, 200)
                                .centerCrop()
                                .into(profileimg);

                        //이미지 변환
                        String path = result.getUri().getPath();
                        File file = new File(path);
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        String fileName = "photomain.jpg";
                        // RequestBody로 Multipart.Part 객체 생성
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile);


                        //문자열 변환
                        HashMap<String, RequestBody> requestMap = new HashMap<>();
                        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                        requestMap.put("uid", uid);

                        //서버에 바로 저장한다.
                        RestapiStart(); //레트로핏 빌드
                        call = retrofitService.profilemainimgupload(
                                1, requestMap, filePart
                        );
                        RestapiResponse(); //응답
                    }

                    //여기서 캐쉬에 저장을 해야하는 군!.... 이미 하고 있었어..

                    break;

                case REQUESTCODE_EDIT:
                    if (resultCode == RESULTCODE_EDIT) {
                        Log.d("-----------requestCode----------", String.valueOf(requestCode));
                        Log.d("-----------resultCode----------", String.valueOf(resultCode));

                        division();

                        //쉐어드값만 다시 불러와주면?

                    }
                    break;
            }
        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
    }


    //레트로핏 라이브러리를 사용해 보낼 양식 만들어 놓음
    public void RestapiStart(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);//restapi 전송시 필요한 정보 모아서 전송

    }
    //레트로핏 데이터 응답 부분
    public void RestapiResponse(){
        //데이터 응답
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if(response.isSuccessful()){
                    Log.d("onResponse ? ","onResponse 성공" + response);
                    // Log.d("onResponse ? ","onResponse 성공" + response.errorBody());
                    // Log.d("onResponse ? ","onResponse 성공" + response.message());
                    // Log.d("onResponse ? ","onResponse 성공" + response.code());
                    Log.d("onResponse ? ","onResponse 성공" + response.raw().request().url());

                    String url = response.raw().request().url().toString();
                    String urlget = url.split("/")[8];
                    String urlget2 = urlget.split("=")[1];
                    //Log.d("onResponse ? ","onResponse 성공" + Arrays.toString(urlget));
                    Log.d("onResponse ? ","onResponse 성공" + urlget2);

                    String resultlist = response.body(); //받아온 데이터
                    resultlist = resultlist.trim(); //전송된 데이터, 띄어쓰기 삭제
                    Log.d("onResponse ? ","onResponse 로그인 여부 : " + resultlist);



                    if(urlget2.equals("1")){ //이미지 저장
                        try {

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            if(jarray.get(0) != null){
                                //Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(obj));
                                //Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(obj.get(0)));

                               Log.d("json 파싱", "이미지 데이터 업로드 성공");

                            }else{
                                Log.d("json 파싱", "이미지 데이터 업로드  실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //단일 이미지 가져오기 (프로필 메인 이미지)
                        try {

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            if(jarray.get(0) != null){
                                JSONObject jsonobj = jarray.getJSONObject(0);
                                Log.d("onResponse ? ","프로필 이미지 list 리스트 : " + String.valueOf(jarray));
                                Log.d("onResponse ? ","onResponse 이미지 리스트 : " + String.valueOf(jsonobj.get("basicuri")));

                                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jsonobj.get("basicuri")+jsonobj.get("src");

                                Log.d("onResponse ? ","onResponse 이미지 리스트 !!! : " + String.valueOf(imagestring));
                                Uri imageUri = Uri.parse(imagestring);
                                //가져온 데이터 하나를 바로 출력
                                Picasso.get()
                                        .load(imageUri) // string or uri 상관없음
                                        .resize(200, 200)
                                        .centerCrop()
                                        .into(profileimg);



                                Log.d("json 파싱", "프로필 이미지 list 성공");

                            }else{
                                Log.d("json 파싱", "프로필 이미지 list  실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    //통신 실패
                    Log.d("onResponse ? ","onResponse 실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //통신 실패
                Log.d("onFailure",t.getMessage());
            }
        });
    }

    //bitmap으로 uri로 변경하는 함수
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.accountsetmenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기

                //전 activity로 값을 리턴
                Intent resultIntent = new Intent();
                resultIntent.putExtra("backval", "1");
                setResult(RESULTCODE, resultIntent);
                finish();
                //돌려보내기


                //finish();
                break;
            case R.id.logoutbtn: //로그아웃 버튼

                //로그아웃시 소켓을 닫음. 재실행하지 않음.
                GlobalClass.mService.LogoutClosed();

                //소켓을 종료시킨다.
                try {
                    GlobalClass.getsocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                initdelete();//로컬 db 전체 초기화

                //MypageFragment로 돌아가며 처리된 값을 준다.
                Intent resultIntent2 = new Intent();
                resultIntent2.putExtra("backval", "2");
                setResult(RESULTCODE, resultIntent2);
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //전체를 초기화한다.
    public void initdelete() {
        oSession.Init(); //db 초기화
    }

//    public void doUnbindService() throws IOException {
//
//        //바인드 서비스를 해제한다.
//        if (GlobalClass.getBound()) {
//            unbindService(GlobalClass.connection);
//            GlobalClass.mBound = false;
//        }
//
//
//        //클라이언트 소켓을 닫는다.
//        GlobalClass.getService().socketclose();
//    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("-----onStart-----","onStart");
        // Bind to LocalService
        //Intent intent = new Intent(this, SocketService.class);
       // bindService(intent, GlobalClass.connection, Context.BIND_AUTO_CREATE);
    }
}