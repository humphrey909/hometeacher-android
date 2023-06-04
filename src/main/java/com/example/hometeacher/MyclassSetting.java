package com.example.hometeacher;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hometeacher.Adapter.ColorViewAdapter;
import com.example.hometeacher.Adapter.MyClassroomRecyclerAdapter;
import com.example.hometeacher.Adapter.RequestClassRecyclerAdapter;
import com.example.hometeacher.Adapter.UserRecyclerAdapter;
import com.example.hometeacher.ArraylistForm.ColorForm;
import com.example.hometeacher.ArraylistForm.ImgForm;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.ArraylistForm.SubjectForm;
import com.example.hometeacher.Profile.Accountsetview;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyclassSetting extends AppCompatActivity {
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    SocketSend oSocketSend;

    NestedScrollView nestedScrollView;
    RecyclerView ColorRecyclerView;

    LinearLayout paymentlinear;
    TextView paymenttext;
    View paymentline;
    EditText roomnametext, paymentedit;
    ImageView cameraimg, chatprofileimg;

    final public int REQUESTCODE_EDIT = 100;// 100 101 102
    final public int RESULTCODE_EDIT = 1;

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    String Sendroomidx; //방 고유번호
    ArrayList<JSONObject> ChatRoominfo; //내 과외 정보

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgForm> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용

    String SelectColor = "";
    ArrayList<String> colorlist = new ArrayList<>();
    String SelectColorinServer = "";

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_setting);


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기

        division();

    }

    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;
        oSocketSend = new SocketSend(GlobalClass);

        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        ColorRecyclerView = (RecyclerView) findViewById(R.id.ColorRecyclerView);
        roomnametext = (EditText) findViewById(R.id.roomnametext);
        paymentedit = (EditText) findViewById(R.id.paymentedit);
        cameraimg = (ImageView) this.findViewById(R.id.cameraimg);
        chatprofileimg = (ImageView) this.findViewById(R.id.chatprofileimg);

        paymentlinear =  (LinearLayout) findViewById(R.id.paymentlinear);
        paymenttext = (TextView) findViewById(R.id.paymenttext);
        paymentline= (View) findViewById(R.id.paymentline);

        if(String.valueOf(Sessionlist.get(1).get(2)).equals("2")){ //학생
            paymentlinear.setVisibility(View.GONE);
            paymenttext.setVisibility(View.GONE);
            paymentline.setVisibility(View.GONE);
        }else{ //선생
            paymentlinear.setVisibility(View.VISIBLE);
            paymenttext.setVisibility(View.VISIBLE);
            paymentline.setVisibility(View.VISIBLE);
        }


        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        Sendroomidx = intent.getExtras().getString("roomidx"); //방 고유번호


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



        colorlist.add("#b2c7d6");
        colorlist.add("#677bac");
        colorlist.add("#9dcdb8");
        colorlist.add("#51a5a0");
        colorlist.add("#9bb157");

        colorlist.add("#f8cd59");
        colorlist.add("#f99460");
        colorlist.add("#f68181");
        colorlist.add("#f7a2bd");
        colorlist.add("#5b4d49");

        colorlist.add("#d3d5d0");
        colorlist.add("#525252");
        colorlist.add("#404372");
        colorlist.add("#10374a");
        colorlist.add("#818b9c");

        ChatRoominfo = new ArrayList<>();
        Getroominfo();
    }

    //색상 리스트를 만든다.
    public void Colorrecycle(){

        ArrayList<ColorForm> colorlist_form = new ArrayList<>();

        for(int i = 0; i<colorlist.size();i++){
            if(SelectColorinServer.equals(colorlist.get(i))){
                ColorForm oColorForm = new ColorForm(colorlist.get(i), true);
                colorlist_form.add(oColorForm);
            }else{

                ColorForm oColorForm = new ColorForm(colorlist.get(i), false);
                colorlist_form.add(oColorForm);
            }
        }

        //Log.d("색상 리스트 이다.", String.valueOf(Colorlistjsonarray));

        ColorRecyclerView = (RecyclerView) findViewById(R.id.ColorRecyclerView);
        GridLayoutManager GridlayoutManager = new GridLayoutManager(oContext, 5); //그리드 매니저 선언
        ColorViewAdapter oColorViewAdapter = new ColorViewAdapter(getApplicationContext(), oActivity); //내가만든 어댑터 선언
        ColorRecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        oColorViewAdapter.setSessionval(Sessionlist); //arraylist 연결
        oColorViewAdapter.setRecycleList(colorlist_form); //arraylist 연결
         //WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        ColorRecyclerView.setAdapter(oColorViewAdapter); //리사이클러뷰 위치에 어답터 세팅

        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        oColorViewAdapter.setOnItemClickListener(new ColorViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, ArrayList<ColorForm> list) {
                //클릭시 이미지 해당 이미지 저장.

                SelectColor = list.get(position).getColor();

//                Intent intent = new Intent(getActivity(), Myclassroomactivity.class);
//                intent.putExtra("roommaketype", "2"); //1. 방처음 만들때 2. 만들어진 방에 들어올때
//                intent.putExtra("roomidx", String.valueOf(list.get(position).get("rid"))); //룸 고유번호
//                intent.putExtra("Tchatcount", String.valueOf(list.get(position).get("totalchatcount"))); //총 채팅 갯수
//                startActivityForResult(intent, REQUESTCODE);



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
                                .start(MyclassSetting.this);
                    }
                    break;

                case OPEN_GALLERY:
                    if (resultCode == RESULT_OK) {
                        //이미지 선택후 크롭화면 나오도록 설정
                        CropImage.activity(intent.getData()).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                                .start(MyclassSetting.this);
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
                                .into(chatprofileimg);

                        //uri 전용 arraylist에 저장
                        ImgForm imgForm = new ImgForm(result.getUri(),"-", true);
                        imguploadlist_uri.add(imgForm);



                        //이미지 변환
//                        String path = result.getUri().getPath();
//                        File file = new File(path);
//                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//                        String fileName = "photomain.jpg";
//                        // RequestBody로 Multipart.Part 객체 생성
//                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile);




//                        //문자열 변환
//                        HashMap<String, RequestBody> requestMap = new HashMap<>();
//                        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
//                        requestMap.put("uid", uid);

//                        //서버에 바로 저장한다.
//                        RestapiStart(); //레트로핏 빌드
//                        call = retrofitService.profilemainimgupload(
//                                1, requestMap, filePart
//                        );
//                        RestapiResponse(); //응답
                    }

                    //여기서 캐쉬에 저장을 해야하는 군!.... 이미 하고 있었어..

                    break;

//                case REQUESTCODE_EDIT:
//                    if (resultCode == RESULTCODE_EDIT) {
//                        Log.d("-----------requestCode----------", String.valueOf(requestCode));
//                        Log.d("-----------resultCode----------", String.valueOf(resultCode));
//
//                        //division();
//                    }
//                    break;
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



    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.myclassroomsetmenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기

//                //전 activity로 값을 리턴
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("backval", "1");
//                setResult(RESULTCODE, resultIntent);
//                finish();
//                //돌려보내기


                finish();
                break;
            case R.id.completebtn: //저장


//                initdelete();//db 전체 초기화
//
//                Intent resultIntent2 = new Intent();
//                resultIntent2.putExtra("backval", "2");
//                setResult(RESULTCODE, resultIntent2);
//


                Setroominfo();

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
                    Log.d("onResponse ? ", "onResponse 로그인 여부 : " + resultlist);


                    if (urlget2.equals("1")) { //채팅 방 정보 가져오기.
                        Log.d("onResponse ? ", "채팅 방 정보 가져오기. : " + resultlist);

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
                            InputMyClassinfo(ChatRoominfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //내 과외방 정보 수정
                        Log.d("onResponse ? ", "내 과외방 정보 수정 : " + resultlist);

                        try {
                            JSONObject jobj = new JSONObject(String.valueOf(resultlist));

                            if(String.valueOf(jobj.get("result")).equals("true")){
                               // Log.d("onResponse ? ", "내 과외방 정보 수정 : " +  jobj.get("result"));
                               // Log.d("onResponse ? ", "내 과외방 정보 수정 : " + resultlist);

                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Intent resultIntent = new Intent();
//                        resultIntent.putExtra("backval", "1");
//                        setResult(RESULTCODE, resultIntent);
//                        finish();


                       //
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    //내 과외 정보에 맞게 보여준다.
    public void InputMyClassinfo(ArrayList<JSONObject> MyclassRoominfojsonarray) throws JSONException {

        //개인 세팅 값이 있는지 확인, 후 세팅
       // Log.d("--myclasssetinfo--", String.valueOf(MyclassRoominfojsonarray.get(0).get("myclass_setinfo")));

        roomnametext.setText(String.valueOf(MyclassRoominfojsonarray.get(0).get("roomname")));
        paymentedit.setText(String.valueOf(MyclassRoominfojsonarray.get(0).get("payment")));

        //설정 값이 없는 경우
        if(String.valueOf(MyclassRoominfojsonarray.get(0).get("myclass_setinfo")).equals("null")){
            SelectColorinServer = "#d3d5d0"; //선택한 배경색

        }else{ //설정 값이 있는 경우

            //수정필요
            JSONArray setinfo = new JSONArray(String.valueOf(MyclassRoominfojsonarray.get(0).get("myclass_setinfo")));

            //Log.d("--myclasssetinfo--", String.valueOf(setinfo.get(0)));
            JSONObject setinfoobj = new JSONObject(String.valueOf(setinfo.get(0)));

            SelectColorinServer = String.valueOf(setinfoobj.get("backcolor")); //선택한 배경색

            if(!String.valueOf(setinfoobj.get("basicuri")).equals("")){
                Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(setinfoobj.get("basicuri"))+String.valueOf(setinfoobj.get("src")));
                Picasso.get()
                        .load(imageUri) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(chatprofileimg);
            }
        }

        Colorrecycle();
    }


    //해당 채팅방 정보를 가져옴
    public void Getroominfo() {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        //설정을 내걸로 저장하고 가져오기 때문에 필요
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getmyclassroominfo_set(
                1,
                requestMap
        );
        RestapiResponse(); //응답
    }

    //채팅방 정보를 변경함.
    public void Setroominfo() {

        //이미지 파일을 보낼 수 있는 형태로 변환해주는 부분
        // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
        for (int i = 0; i < imguploadlist_uri.size(); i++) {

                String path = imguploadlist_uri.get(i).getUri().getPath();
                File file = new File(path);

                Log.d("img upload file link", imguploadlist_uri.get(i).toString());
                // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
                // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), imguploadlist_uri.get(i).toString());
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // 사진 파일 이름
                String fileName = "photo" + i + ".jpg";
                // RequestBody로 Multipart.Part 객체 생성
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file" + i, fileName, requestFile);


                // 추가
                imguploadlist_multipart.add(filePart);
        }


        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody roomidx = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sendroomidx));
        requestMap.put("roomidx", roomidx);

        RequestBody roomname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(roomnametext.getText()));
        requestMap.put("roomname", roomname);

        RequestBody payment = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(paymentedit.getText()));
        requestMap.put("payment", payment);


        //설정을 내걸로 저장하고 가져오기 때문에 필요
        RequestBody myuid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        requestMap.put("myuid", myuid);


        RequestBody SelectColor_;
        if(SelectColor.equals("")){ //선택한 값이 잇을때
            SelectColor_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectColorinServer));
        }else{ //선택된 값이 없으면 가져온값으로 전송
            SelectColor_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectColor));
        }
        requestMap.put("SelectColor", SelectColor_);

        String currenttime = Makecurrenttime();//현재시간 불러오기
        RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
        requestMap.put("currenttime", currenttime_);


       //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.editmyclassroom(
                2,
                requestMap,
                imguploadlist_multipart
        );
        RestapiResponse(); //응답
    }


    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}