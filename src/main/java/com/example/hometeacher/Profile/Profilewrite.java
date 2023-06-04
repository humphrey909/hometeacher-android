package com.example.hometeacher.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hometeacher.Adapter.CharacterRecyclerAdapter;
import com.example.hometeacher.Adapter.SubjectRecyclerAdapter;
import com.example.hometeacher.Adapter.ImgAdapter;
import com.example.hometeacher.ArraylistForm.CharacterForm;
import com.example.hometeacher.ArraylistForm.ImgForm;
import com.example.hometeacher.ArraylistForm.SubjectForm;
import com.example.hometeacher.MainActivity;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.shared.Session;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Profilewrite extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Session oSession;
    ArrayList<ArrayList<String>> Sessionlist;

    TextView toolbartitle, nicnameoverlapchktext, studentagetext, minpayguidelinetext, maxpayguidelinetext, payguidelinereasontext, charactertitle, subjecttitle, schooltitle, capmusareatitle, schoolsubjecttitle, studentidtitle, onelineintroducetitle, availabletimetitle, classdocumenttitle, classstyletitle, skillappealtitle, forteachertalktitle;
    EditText nicnameedit, minpayguideline,maxpayguideline, payguidelinereason, capmusareaedit, schoolsubjectedit, studentidedit, onelineintroduceedit, availabletimeedit, classdocumentedit, classstyleedit, skillappealedit, forteachertalkedit, schooledit;
    LinearLayout schoollayout;

    RadioButton sex1, sex2;
    RadioButton.OnClickListener clickListener;
    String sextype = "0";

    RecyclerView CharacterRecyclerView, SubjectRecyclerView, imagesRecyclerView;

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    int ImgPosition = 0; //갤러리, 카메라 선택시 위치를 저장

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    Spinner schoolchkspinner, studentagespinner;
    ArrayAdapter<CharSequence> spinneradapter;

    Activity activity;
    int RESULTCODE = 1;

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgForm> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용

    ArrayList<CharacterForm> Characterlist; //전체 성격 리스트
    ArrayList<SubjectForm> Subjectlist; //전체 과목 리스트

    ArrayList<String> SelectCharacterlist; //선택된 성격 리스트 - db에 저장하기 위함
    ArrayList<String> SelectSubjectlist; //선택된 과목 리스트 - db에 저장하기 위함
    ArrayList<String> SelectImgDeletelist; //db 이미지 항목중에 삭제된 idx리스트 - db에서 삭제하기 위함

    ArrayList<JSONObject> ProfileInfo; //서버에서 가져온 프로필 데이터를 저장.
    ArrayList<JSONObject> ProfileImgInfo; //서버에서 가져온 프로필 이미지 데이터를 저장.

    int addtype; // 1. 추가, 2. 수정
    String addprofileidx; //프로필 고유번호

    int spinnerselectSchoolchk;
    int spinnerselectStudentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilewrite);

        oSession = new Session(this);
        this.activity = this;
       // this.RESULTCODE = RESULTCODE;

        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        addtype = intent.getExtras().getInt("type");
        if(addtype == 2){
            addprofileidx = intent.getExtras().getString("profileidx");
        }
        //Log.d("addtype", "----------------------건너온 정보들------------------------");
        //Log.d("addtype", String.valueOf(addtype));
        //Log.d("addprofileidx", String.valueOf(addprofileidx));

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        if (addtype == 1) { //추가
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기
        }else{ //수정
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 지우기
        }


        //학생, 선생님 구분분
        Sessionlist = oSession.Getoneinfo("0");
       // Log.d("Sessionlist - 0",String.valueOf(Sessionlist));
        division(Sessionlist.get(1));


        Uri emptyUri = Uri.parse("empty");

        ImgForm imgForm = new ImgForm(emptyUri,"-",true);
        imguploadlist_uri.add(imgForm);
        //이미지 정보 가져오는 부분
        Imagerecycle(imguploadlist_uri);
    }

    //이미지 리스트 만듬
    public void Imagerecycle(ArrayList<ImgForm> imglist) {

        imagesRecyclerView = (RecyclerView)findViewById(R.id.imagesRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false); //가로일때
        imagesRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgAdapter imgAdapter = new ImgAdapter(this); //내가만든 어댑터 선언

        imgAdapter.settype(1); //프로필 등록 이미지
        imgAdapter.setRecycleList(imglist); //arraylist 연결
        imagesRecyclerView.setAdapter(imgAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        imgAdapter.setOnItemClickListener(new ImgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int picturepos, int clickpos, int type, String imgidx) {
                // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.


//                //권한 요청
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //안드로이드 버전확인
//                    //권한 허용이 됐는지 확인
//                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                    } else { //권한 허용 요청
//                        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    }
//                }
//                //퍼미션 체크하는 부분 / -1 퍼미션 안되어있음
//                int permissionCheck = ContextCompat.checkSelfPermission(Profilewrite.this, Manifest.permission.WRITE_CALENDAR);
//                Log.d("permissionCheck", String.valueOf(permissionCheck));


                Log.d("type", String.valueOf(type)); //업로드인지 1, 삭제인지 2 알려줌
                Log.d("position", String.valueOf(picturepos)); //이미지 위치를 알려줌
                Log.d("clickpos", String.valueOf(clickpos)); //alert dialog 버튼 순서를 알려줌

                if(type == 1){ //업로드
                    ImgPosition = picturepos; //이미지 순서
                    if(clickpos == 0){ //사진첩
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                       // intent.putExtra("crop", true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //멀티로 선택할 수 있는 기능

                        activity.startActivityForResult(intent, OPEN_GALLERY);

                    }else{ //카메라
                        //captureCamera();
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);

                    }
                }else if(type == 2){ //2 삭제
                    imguploadlist_uri.remove(picturepos);
                    Imagerecycle(imguploadlist_uri);

                    SelectImgDeletelist.add(imgidx); //이미지 삭제 리스트 생성
                }
            }
        }) ;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
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
                        Bundle extras = intent.getExtras(); //카메라 촬영후 가져오는 데이터

                        // Bitmap으로 컨버전
                        Bitmap imageBitmap = (Bitmap) extras.get("data"); //bitmab으로 변경
                        Uri uridata = getImageUri(this, imageBitmap); //bitmap을 uri로 변경

                        //이미지 선택후 크롭화면 나오도록 설정
                        CropImage.activity(uridata).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                                .start(Profilewrite.this);
                    }
                    break;

                case OPEN_GALLERY:
                    if (resultCode == RESULT_OK) {
                        //이미지 선택후 크롭화면 나오도록 설정
                        CropImage.activity(intent.getData()).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
                                .start(Profilewrite.this);
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

                        //uri 전용 arraylist에 저장
                        ImgForm imgForm = new ImgForm(result.getUri(),"-", true);
                        imguploadlist_uri.add(1, imgForm);
                        //imguploadlist_uri.add(1, result.getUri());
                        Imagerecycle(imguploadlist_uri); //리사이클러뷰에 출력
                    }

                    break;
            }
        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
    }

    public void Characterrecycle(ArrayList<CharacterForm> Characterjsonarray){
        Log.d("성격 리스트 이다.", String.valueOf(Characterjsonarray));


        GridLayoutManager GridlayoutManager = new GridLayoutManager(getApplicationContext(), 3); //그리드 매니저 선언
        CharacterRecyclerAdapter CharacterAdapter = new CharacterRecyclerAdapter(this); //내가만든 어댑터 선언
        CharacterRecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

       // CharacterAdapter.setlisttype(1); //arraylist 연결
        CharacterAdapter.setRecycleList(Characterjsonarray); //arraylist 연결
       // WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        CharacterRecyclerView.setAdapter(CharacterAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        CharacterAdapter.setOnItemClickListener(new CharacterRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SelectCharacterlist = new ArrayList<>();

                //characterval = String.valueOf(position);
                //Log.d("tag", String.valueOf(Characterlist));
                for(int i = 0; i<Characterlist.size();i++){
                   // Log.d("Characterlist", String.valueOf(Characterlist.get(i).getName()));
                    //Log.d("Characterlist", String.valueOf(Characterlist.get(i).isSelected()));

                    if(Characterlist.get(i).isSelected()){ //true인것만
                        SelectCharacterlist.add(String.valueOf(i));
                    }
                }
            }
        });

    }
    public void Subjectrecycle(ArrayList<SubjectForm> Subjectjsonarray){
        Log.d("과목 리스트 이다.", String.valueOf(Subjectjsonarray));


        GridLayoutManager GridlayoutManager = new GridLayoutManager(getApplicationContext(), 3); //그리드 매니저 선언
        SubjectRecyclerAdapter SubjectAdapter = new SubjectRecyclerAdapter(this); //내가만든 어댑터 선언
        SubjectRecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        SubjectAdapter.setlisttype(1); //arraylist 연결
        SubjectAdapter.setRecycleList(Subjectjsonarray); //arraylist 연결
        //WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        SubjectRecyclerView.setAdapter(SubjectAdapter); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        SubjectAdapter.setOnItemClickListener(new SubjectRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SelectSubjectlist = new ArrayList<>();

                //Log.d("tag", String.valueOf(Subjectlist));
                //Log.d("tag", String.valueOf(Subjectlist));
                for(int i = 0; i<Subjectlist.size();i++){
                   // Log.d("Characterlist", String.valueOf(Subjectlist.get(i).getName()));
                    //Log.d("Characterlist", String.valueOf(Subjectlist.get(i).isSelected()));

                    if(Subjectlist.get(i).isSelected()){ //true인것만
                        SelectSubjectlist.add(String.valueOf(i));
                    }
                }

                Log.d("과목 선택 리스트 - db", String.valueOf(SelectSubjectlist));
                Log.d("과목 전체 리스트", String.valueOf(Subjectlist));
            }
        });
    }
    
    
    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profilewritemenu, menu);
        return true;
    }
    //action tab 버튼 클릭시
    // @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.completebtn: //프로필 작성완료

                //이미지 파일을 보낼 수 있는 형태로 변환해주는 부분
                // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
                for (int i = 0; i < imguploadlist_uri.size(); i++) {
                    //String path = imguploadlist_uri.get(i).getPath();

                    if(imguploadlist_uri.get(i).isSelected()){ //true인것만 체크할 것
                        String path = imguploadlist_uri.get(i).getUri().getPath();
                        File file = new File(path);

                        //첫번째 인수에 가짜 데이터가 들어가있어서 0번째는 제외해줌. 가짜값을 만든이유는 첫번째 인수를 이미지 업로드 버튼으로 만들기 위함
                        if(i != 0){
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
                }

                //입력 데이터 전부가져오기 - 학생 선생님 나눠서 처리하기
                //보낼 데이터 - 학생

                //프로필 정보 한번에 넘기기
                HashMap<String, RequestBody> requestMap = new HashMap<>();
                if(Sessionlist.get(1).get(2).equals("1")){ //선생님

                    RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                    RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(2)));
                    RequestBody nicname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nicnameedit.getText()));
                    RequestBody sextypeval = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sextype));
                    RequestBody minpayguide = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(minpayguideline.getText()));
                    RequestBody payguidelinereasonval = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(payguidelinereason.getText()));
                    RequestBody characterlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectCharacterlist));
                    RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectSubjectlist));
                    RequestBody schoolname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(schooledit.getText()));
                    RequestBody schoolchk = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(spinnerselectSchoolchk));
                    RequestBody capmusarea = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(capmusareaedit.getText()));
                    RequestBody schoolsubject = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(schoolsubjectedit.getText()));
                    RequestBody studentid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(studentidedit.getText()));
                    RequestBody onelineintroduce = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(onelineintroduceedit.getText()));
                    RequestBody availabletime = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(availabletimeedit.getText()));
                    RequestBody subjectdocument = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(classdocumentedit.getText()));
                    RequestBody classstyle = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(classstyleedit.getText()));
                    RequestBody skillappeal = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(skillappealedit.getText()));

                    requestMap.put("uid", uid);
                    requestMap.put("usertype", usertype);
                    requestMap.put("nicname", nicname);
                    requestMap.put("sextypeval", sextypeval);
                    requestMap.put("minpayguide", minpayguide);
                    requestMap.put("payguidelinereason", payguidelinereasonval);
                    requestMap.put("character", characterlist);
                    requestMap.put("subjectlist", subjectlist);
                    requestMap.put("university", schoolname);
                    requestMap.put("universitychk", schoolchk);
                    requestMap.put("campusaddress", capmusarea);
                    requestMap.put("universmajor", schoolsubject);
                    requestMap.put("studentid", studentid);
                    requestMap.put("onelineintroduce", onelineintroduce);
                    requestMap.put("availabletime", availabletime);
                    requestMap.put("subjectdocument", subjectdocument);
                    requestMap.put("classstyle", classstyle);
                    requestMap.put("skillappeal", skillappeal);


                }else{ //학생

                    RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                    RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(2)));
                    RequestBody nicname = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nicnameedit.getText()));
                    RequestBody studentages = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(spinnerselectStudentage));
                    RequestBody sextypeval = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sextype));
                    RequestBody onelineintroduce = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(onelineintroduceedit.getText()));

                    RequestBody subjectlist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectSubjectlist));
                    RequestBody maxpayguide = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(maxpayguideline.getText()));
                    RequestBody availabletime = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(availabletimeedit.getText()));
                    RequestBody forteachertalk = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(forteachertalkedit.getText()));

                    requestMap.put("uid", uid);
                    requestMap.put("usertype", usertype);
                    requestMap.put("nicname", nicname);
                    requestMap.put("studentages", studentages);
                    requestMap.put("sextypeval", sextypeval);
                    requestMap.put("onelineintroduce", onelineintroduce);
                    requestMap.put("subjectlist", subjectlist);
                    requestMap.put("maxpayguide", maxpayguide);
                    requestMap.put("availabletime", availabletime);
                    requestMap.put("infotalk", forteachertalk);
                }

                if(addtype == 1){ //추가
                    //Intent intent = new Intent(getApplicationContext(), Profilewrite.class);
                    //startActivity(intent);
                    //여기서 이미지 데이터를 서버로전송할 것

                    RestapiStart(); //레트로핏 빌드

                    call = retrofitService.profileupload(
                            5, requestMap, imguploadlist_multipart
                    );
                    RestapiResponse(); //응답

                }else{ //수정

                    RequestBody imgdeletelist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectImgDeletelist)); //이미지 삭제 리스트
                    requestMap.put("imgdeletelist", imgdeletelist);
                    RequestBody pidx = RequestBody.create(MediaType.parse("text/plain"), addprofileidx); //프로젝트 idx - 수정일때 없데이트하기위함
                    requestMap.put("pidx", pidx);

                    //여기서 이미지 데이터를 서버로전송할 것
                    RestapiStart(); //레트로핏 빌드

                    call = retrofitService.profileupload_edit(
                            3, requestMap, imguploadlist_multipart
                    );
                    RestapiResponse(); //응답
                }
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

    //레트로핏 데이터 응답 부분 : 인증번호 가져옴
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



                    if(urlget2.equals("1")){ //성격 리스트
                        try {

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            if(jarray.get(0) != null){
                                //Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(obj));
                                //Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(obj.get(0)));

                                Characterlist = new ArrayList<>(); //전체 성격 리스트

                               // ArrayList<ArrayList<String>> Characterlist = new ArrayList<>();
                                for(int i = 0; i<jarray.length(); i++){
                                    JSONObject jobj = new JSONObject(jarray.get(i).toString());


                                    Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(jobj.get("charactername")));

                                    //arraylist를 데이터 형식에 맞게 넣는 방법
                                    CharacterForm Item = new CharacterForm(jobj.get("charactername").toString(), false);
                                    Characterlist.add(i, Item);
                                }

                                Log.d("onResponse ? ","onResponse 성격 ArrayList  : " + String.valueOf(Characterlist));
                                //성격 리스트 가져오는 부분
                                Characterrecycle(Characterlist);

                                Log.d("json 파싱", "성격 데이터 업로드 성공");

                            }else{
                                Log.d("json 파싱", "성격 데이터 업로드  실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("2")){ //과목 리스트
                        try {
                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            if(jarray.get(0) != null){
                                //Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(obj));
                                //Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(obj.get(0)));


                                Subjectlist = new ArrayList<>();
                                for(int i = 0; i<jarray.length(); i++){
                                    JSONObject jobj = new JSONObject(jarray.get(i).toString());


                                    Log.d("onResponse ? ","onResponse 성격 리스트 : " + String.valueOf(jobj.get("subjectname")));

                                    //arraylist를 데이터 형식에 맞게 넣는 방법
                                    SubjectForm SubjectForm = new SubjectForm(jobj.get("subjectgroup").toString(), jobj.get("subjectname").toString(), false);
                                    Subjectlist.add(i, SubjectForm);
                                }

                                Log.d("onResponse ? ","onResponse 과목 ArrayList  : " + String.valueOf(Subjectlist));
                                //과목 리스트 가져오는 부분
                                Subjectrecycle(Subjectlist);

                                Log.d("json 파싱", "성격 데이터 업로드 성공");

                            }else{
                                Log.d("json 파싱", "성격 데이터 업로드  실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //과목을 가져오고 그다음 프로필을 가져오도록 만들었음.
                        if(addtype == 2){ //수정일때만
                            profilegetdata(); //프로필 내용 가져오기 위해 서버 통신
                        }
                    }else if(urlget2.equals("3")){ // 프로필 데이터 저장
                        Log.d("json 파싱", "프로필 데이터 수정한다.");

                        //전 activity로 값을 리턴
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("completeval", "1");
                        activity.setResult(RESULTCODE, resultIntent);
                        activity.finish();

                        finish();

                    }else if(urlget2.equals("4")){ // 프로필 데이터 가져오기 - 수정할때 사용
                        Log.d("json 파싱", "프로필 데이터 잘 가져왔니?");

                        try {

                            ProfileInfo = new ArrayList<>();

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray));
                            // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                            if (jarray.get(0) != null) {

                                for(int i = 0; i<jarray.length(); i++){
                                    JSONObject tempJson = jarray.getJSONObject(i);
                                    //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                    // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                    //jsonobject형식으로 데이터를 저장한다.
                                    ProfileInfo.add(tempJson);
                                }

                                //object형식으로 arraylist를 만든것
                                Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(ProfileInfo));

                                //가져온 데이터를 출력하는 부분
                                datachange();
                                Log.d("json 파싱", "프로필 데이터 가져오기 성공");

                            } else {
                                Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("5")) { // 프로필 데이터 저장
                        Log.d("json 파싱", "프로필 데이터 추가한다.");

                        //전 activity로 값을 리턴
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("MoveValue", "0");
                        //intent.putExtra("nid", nid_sub);
                        //intent.putExtra("uid", uid_sub);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                        startActivity(intent);

                        finish();
                    }else if(urlget2.equals("6")){
                        Log.d("json 파싱", "프로필 이미지 리스트를 가져온다.");
                        Log.d("onResponse ? ","onResponse 프로필 이미지 데이터 : " + String.valueOf(resultlist));
                        try {

                            ProfileImgInfo = new ArrayList<>();

                            //jsonarray, JSONObject로 문자열로 온 형태의 json을 풀어주는 것.
                            JSONArray jarray = new JSONArray(resultlist);
                            Log.d("onResponse ? ","onResponse 프로필 이미지 데이터 : " + String.valueOf(jarray));
                            // Log.d("onResponse ? ","onResponse 프로필 데이터 : " + String.valueOf(jarray.get(0)));
                            if (jarray.get(0) != null) {

                                for(int i = 0; i<jarray.length(); i++){
                                    JSONObject tempJson = jarray.getJSONObject(i);
                                    //JSONObject jobj = new JSONObject(jarray.get(0).toString());
                                    // Log.d("onResponse ? ", "onResponse 프로필 리스트 : " + String.valueOf(tempJson));

                                    //jsonobject형식으로 데이터를 저장한다.
                                    ProfileImgInfo.add(tempJson);
                                }

                                //object형식으로 arraylist를 만든것
                                Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 : " + String.valueOf(ProfileImgInfo));

                                imgdatachange();
                                Log.d("json 파싱", "프로필 이미지 가져오기 성공");

                            } else {
                                Log.d("json 파싱", "프로필 데이터 가져오기 실패");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(urlget2.equals("7")){ //닉네임 오버랩 체크하는 부분
                        Log.d("json 파싱", "닉네임 오버랩 체크를 가져온다.");
                        Log.d("onResponse ? ","onResponse 닉네임 오버랩 체크 : " + String.valueOf(resultlist));
                        try {
                            JSONObject obj = new JSONObject(resultlist);
                            Log.d("onResponse ? ","onResponse 닉네임 오버랩 체크 : " + String.valueOf(obj.get("result")));

                            if(obj.get("result").equals(true)){
                                nicnameoverlapchktext.setVisibility(View.VISIBLE);
                            }else{
                                nicnameoverlapchktext.setVisibility(View.GONE);
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


    //서버에 있는 이미지 리스트를 불러와서 합친다.
    public void imgdatachange() throws JSONException {

        for (int i=0;i<ProfileImgInfo.size();i++){
            Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 - write : " + String.valueOf(ProfileImgInfo.get(i)));

            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+ProfileImgInfo.get(i).get("basicuri")+ProfileImgInfo.get(i).get("src");
            Log.d("onResponse ? ", "onResponse 프로필 이미지 리스트 - write : " + String.valueOf(imagestring));

            Uri imageUri = Uri.parse(imagestring);
            ImgForm imgForm = new ImgForm(imageUri, String.valueOf(ProfileImgInfo.get(i).get("idx")), false);
            imguploadlist_uri.add(imgForm);

        }


        Imagerecycle(imguploadlist_uri);

    }
    
    
    
    //bitmap으로 uri로 변경하는 함수
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //선생님, 학생 보여줄 부분 구분하기
    public void division(ArrayList<String> usertype){
        toolbartitle = (TextView) findViewById(R.id.toolbartitle); //툴바 타이틀
        if(addtype == 1){ //추가
            toolbartitle.setText("프로필 작성");
        }else{ //수정
            toolbartitle.setText("프로필 수정");
        }
       
        
        
        studentagetext = (TextView) findViewById(R.id.studentagetext); //학생의 연력/학년
        minpayguidelinetext = (TextView) findViewById(R.id.minpayguidelinetext); //최소 페이 (월 기준)
        maxpayguidelinetext = (TextView) findViewById(R.id.maxpayguidelinetext); //최대예산 (월 기준)
        payguidelinereasontext = (TextView) findViewById(R.id.payguidelinereasontext); //자세한 수업료 기준
        charactertitle = (TextView) findViewById(R.id.charactertitle); //성격
        subjecttitle = (TextView) findViewById(R.id.subjecttitle); //과목 리스트
        schooltitle = (TextView) findViewById(R.id.schooltitle); //학교
        capmusareatitle = (TextView) findViewById(R.id.capmusareatitle); //소속캠퍼스 지역
        schoolsubjecttitle = (TextView) findViewById(R.id.schoolsubjecttitle); //학과
        studentidtitle = (TextView) findViewById(R.id.studentidtitle); //학번
        onelineintroducetitle = (TextView) findViewById(R.id.onelineintroducetitle); //한줄 소개
        availabletimetitle = (TextView) findViewById(R.id.availabletimetitle); //과외 가능 요일/시간
        classdocumenttitle = (TextView) findViewById(R.id.classdocumenttitle); //과목별 수업내용
        classstyletitle = (TextView) findViewById(R.id.classstyletitle); //과외 스타일
        skillappealtitle = (TextView) findViewById(R.id.skillappealtitle); //실력 어필
        forteachertalktitle = (TextView) findViewById(R.id.forteachertalktitle); //학생의 상황, 과외선생님께 바라는 것
        nicnameoverlapchktext = (TextView) findViewById(R.id.nicnameoverlapchktext); //닉네임 오버랩 알림
       
        

        nicnameedit = (EditText) findViewById(R.id.nicnameedit); //닉네임
        minpayguideline = (EditText) findViewById(R.id.minpayguideline); //최소 페이 (월 기준)
        maxpayguideline = (EditText) findViewById(R.id.maxpayguideline); //최대예산 (월 기준)
        payguidelinereason = (EditText) findViewById(R.id.payguidelinereason); //자세한 수업료 기준
        capmusareaedit = (EditText) findViewById(R.id.capmusareaedit); //소속캠퍼스 지역
        schoolsubjectedit = (EditText) findViewById(R.id.schoolsubjectedit); //학과
        studentidedit = (EditText) findViewById(R.id.studentidedit); //학번
        onelineintroduceedit = (EditText) findViewById(R.id.onelineintroduceedit); //한줄 소개
        availabletimeedit = (EditText) findViewById(R.id.availabletimeedit); //과외 가능 요일/시간
        classdocumentedit = (EditText) findViewById(R.id.classdocumentedit); //과목별 수업내용
        classstyleedit = (EditText) findViewById(R.id.classstyleedit); //과외 스타일
        skillappealedit = (EditText) findViewById(R.id.skillappealedit); //실력 어필
        forteachertalkedit = (EditText) findViewById(R.id.forteachertalkedit); //학생의 상황, 과외선생님께 바라는 것
        schooledit = (EditText) findViewById(R.id.schooledit); //학교 명



        schoolchkspinner = (Spinner) findViewById(R.id.schoolchkspinner); //학교 재학 여부
        studentagespinner = (Spinner) findViewById(R.id.studentagespinner); //학생 나이/학년

        schoollayout = (LinearLayout) this.findViewById(R.id.schoollayout); // 학교 레이아웃

        CharacterRecyclerView = (RecyclerView)findViewById(R.id.characterlistRecyclerView); //리사이클러뷰 위치 선언
        SubjectRecyclerView = (RecyclerView)findViewById(R.id.subjcetlistRecyclerView); //리사이클러뷰 위치 선언

        Log.d("Sessionlist", String.valueOf(usertype.get(2)));
        if(usertype.get(2).equals("1")){ //선생님
            studentagetext.setVisibility(View.GONE);
            studentagespinner.setVisibility(View.GONE);
            minpayguidelinetext.setVisibility(View.VISIBLE);
            minpayguideline.setVisibility(View.VISIBLE);
            maxpayguidelinetext.setVisibility(View.GONE);
            maxpayguideline.setVisibility(View.GONE);
            payguidelinereasontext.setVisibility(View.VISIBLE);
            payguidelinereason.setVisibility(View.VISIBLE);
            charactertitle.setVisibility(View.VISIBLE);
            subjecttitle.setVisibility(View.VISIBLE);
            schooltitle.setVisibility(View.VISIBLE);
            schoollayout.setVisibility(View.VISIBLE);
            capmusareatitle.setVisibility(View.VISIBLE);
            capmusareaedit.setVisibility(View.VISIBLE);
            schoolsubjecttitle.setVisibility(View.VISIBLE);
            schoolsubjectedit.setVisibility(View.VISIBLE);
            studentidtitle.setVisibility(View.VISIBLE);
            studentidedit.setVisibility(View.VISIBLE);
            onelineintroducetitle.setVisibility(View.VISIBLE);
            onelineintroduceedit.setVisibility(View.VISIBLE);
            classdocumenttitle.setVisibility(View.VISIBLE);
            classdocumentedit.setVisibility(View.VISIBLE);
            classstyletitle.setVisibility(View.VISIBLE);
            classstyleedit.setVisibility(View.VISIBLE);
            skillappealtitle.setVisibility(View.VISIBLE);
            skillappealedit.setVisibility(View.VISIBLE);
            forteachertalktitle.setVisibility(View.GONE);
            forteachertalkedit.setVisibility(View.GONE);

            CharacterRecyclerView.setVisibility(View.VISIBLE);

           

            //학교 재학 여부
            spinneradapter = ArrayAdapter.createFromResource(this, R.array.schoolchk_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            schoolchkspinner.setAdapter(spinneradapter);
            schoolchkspinner.setOnItemSelectedListener(this);

            GetCharacterlist(); //성격리스트 가져오기

            schoolchkspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //스피너 응답 부분
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerselectSchoolchk = position;
                    Log.d("spinner 선택 정보", String.valueOf(id));
                    //Log.d("선택 정보", String.valueOf(position));
                    //Log.d("선택 정보", String.valueOf(parent));
                    //  Log.d("", String.valueOf(parent));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            SelectCharacterlist = new ArrayList<>();
            SelectSubjectlist = new ArrayList<>();
        }else{ //학생
            studentagetext.setVisibility(View.VISIBLE);
            studentagespinner.setVisibility(View.VISIBLE);
            minpayguidelinetext.setVisibility(View.GONE);
            minpayguideline.setVisibility(View.GONE);
            maxpayguidelinetext.setVisibility(View.VISIBLE);
            maxpayguideline.setVisibility(View.VISIBLE);
            payguidelinereasontext.setVisibility(View.GONE);
            payguidelinereason.setVisibility(View.GONE);
            charactertitle.setVisibility(View.GONE);
            subjecttitle.setVisibility(View.VISIBLE);
            schooltitle.setVisibility(View.GONE);
            schoollayout.setVisibility(View.GONE);
            capmusareatitle.setVisibility(View.GONE);
            capmusareaedit.setVisibility(View.GONE);
            schoolsubjecttitle.setVisibility(View.GONE);
            schoolsubjectedit.setVisibility(View.GONE);
            studentidtitle.setVisibility(View.GONE);
            studentidedit.setVisibility(View.GONE);
            onelineintroducetitle.setVisibility(View.VISIBLE);
            onelineintroduceedit.setVisibility(View.VISIBLE);
            classdocumenttitle.setVisibility(View.GONE);
            classdocumentedit.setVisibility(View.GONE);
            classstyletitle.setVisibility(View.GONE);
            classstyleedit.setVisibility(View.GONE);
            skillappealtitle.setVisibility(View.GONE);
            skillappealedit.setVisibility(View.GONE);
            forteachertalktitle.setVisibility(View.VISIBLE);
            forteachertalkedit.setVisibility(View.VISIBLE);

            CharacterRecyclerView.setVisibility(View.GONE);

            //학생 나이/학년
            spinneradapter = ArrayAdapter.createFromResource(this, R.array.studentage_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            studentagespinner.setAdapter(spinneradapter);
            studentagespinner.setOnItemSelectedListener(this);

            studentagespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //스피너 응답 부분
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerselectStudentage = position;
                    Log.d("spinner 선택 정보", String.valueOf(id));
                    //Log.d("선택 정보", String.valueOf(position));
                    //Log.d("선택 정보", String.valueOf(parent));
                    //  Log.d("", String.valueOf(parent));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            SelectSubjectlist = new ArrayList<>();
        }
        SelectImgDeletelist = new ArrayList<>();

        GetSubjectlist(); //과목리스트 가져오기

        //nicnameedit 중복체크
        nicnameoverlapchktext.setVisibility(View.GONE);
        nicnameedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력난에 변화가 있을때

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을 때
                String nicname = charSequence.toString();
                Log.d("입력한 nicname", nicname);


                //보낼값 - nicname
                HashMap<String, RequestBody> requestMap = new HashMap<>();
                RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                RequestBody writenicname = RequestBody.create(MediaType.parse("text/plain"), nicname);

                requestMap.put("uid", uid);
                requestMap.put("writenicname", writenicname);

                RestapiStart(); //레트로핏 빌드
                call = retrofitService.nicnameoverlabchk(
                        7, requestMap
                );
                RestapiResponse(); //응답


                //nicnameoverlapchktext
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에

            }
        });


        //프로필 이미지 리스트 가져오는 부분
        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        RequestBody imgtype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));

        requestMap.put("uid", uid);
        requestMap.put("imgtype", imgtype);

        if(addtype == 2){ //수정일때만
            RestapiStart(); //레트로핏 빌드
            call = retrofitService.profilegetimglist(
                    6, requestMap
            );
            RestapiResponse(); //응답
        }



        sex1 = (RadioButton) this.findViewById(R.id.sex1);
        sex2 = (RadioButton) this.findViewById(R.id.sex2);
        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sex1:
                        Log.d("","남");
                        sextype = "1"; //품목 탭 입력
                        break;
                    case R.id.sex2:
                        Log.d("","여");
                        sextype = "2"; //품목 탭 입력
                        break;
                }
            }
        };
        sex1.setOnClickListener(clickListener);
        sex2.setOnClickListener(clickListener);



    }

    //성격 카테고리 리스트
    public void GetCharacterlist(){

            //보낼값
            HashMap<String, RequestBody> requestMap = new HashMap<>();
            RequestBody catetype = RequestBody.create(MediaType.parse("text/plain"), "1");
            requestMap.put("catetype", catetype);

            //성격 카테고리 정보 가져오는 부분
            RestapiStart(); //레트로핏 빌드
            call = retrofitService.categoreylist(
                    1, requestMap
            );
            RestapiResponse(); //응답


    }

    //과목 카테고리 리스트
    public void GetSubjectlist(){

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody catetype = RequestBody.create(MediaType.parse("text/plain"), "2");
        requestMap.put("catetype", catetype);

        //과목 카테고리 정보 가져오는 부분
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.categoreylist(
                2, requestMap
        );
        RestapiResponse(); //응답

    }


    public void profilegetdata(){
        //프로필 정보 가져오는 서버통신
        RestapiStart(); //레트로핏 빌드

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(2)));

        requestMap.put("uid", uid);
        requestMap.put("usertype", usertype);


        call = retrofitService.profilegetlist(
                4, requestMap
        );
        RestapiResponse(); //응답
    }

    //프로필 데이터를 가져올때 사용한다. ProfileInfo에 값이 있어야 프로필의 정보가 보여진다.
    //서버 통신은 처음 페이지에 왔을때, 다시 돌아왔을때만 서버통신을 함. oncreate, onstart
    public void datachange() throws JSONException {
        Log.d("onResponse ? ", "onResponse 프로필 리스트 -3 : " + String.valueOf(ProfileInfo));

        if(Sessionlist.get(1).get(2).equals("1")){ //선생님

            //닉네임
            nicnameedit.setText(String.valueOf(ProfileInfo.get(0).get("nicname")));

            //성별
            if(String.valueOf(ProfileInfo.get(0).get("gender")).equals("1")){ //남
                sex1.setChecked(true);
                sextype = "1";
            }else{ //여
                sex2.setChecked(true);
                sextype = "2";
            }

            minpayguideline.setText(String.valueOf(ProfileInfo.get(0).get("minpay")));
            payguidelinereason.setText(String.valueOf(ProfileInfo.get(0).get("detailpaystandard")));

            //선택된 성격 변환
            String charactersub = String.valueOf(ProfileInfo.get(0).get("character")).substring(0,String.valueOf(ProfileInfo.get(0).get("character")).length()-1); //마지막 문자 삭제
            String charactersub2 = charactersub.substring(1); //첫번째 문자 삭제
            String charactersub3 = charactersub2.replaceAll(" ", ""); //모든 공백 제거
            String[] characterarr = charactersub3.split(",");

            //전체 과목에서 선택된 과목 분류함. 분류한 목록을 true로 변경
            for(int i = 0; i<Characterlist.size();i++){
                for(int j=0;j<characterarr.length;j++){
                    //Log.d("선택된 과목 리스트",characterarr[j]);

                    //선택된 값이 존재한다면 True로 변경한다.
                    if(String.valueOf(i).equals(characterarr[j])){
                        //characterlist.get(i).add
                        Log.d("체크된 성격 리스트", String.valueOf(i));
                        Log.d("체크된 성격 리스트", String.valueOf(Characterlist.get(i).getName()));
                        Log.d("체크된 성격 리스트", String.valueOf(Characterlist.get(i).isSelected()));

                        Characterlist.get(i).setSelected(true);

                        //서버로 넘길 데이터 만들기
                        SelectCharacterlist.add(String.valueOf(i));
                    }
                }
            }
            Characterrecycle(Characterlist);


            //선택된 과목 변환
            String subjectsub = String.valueOf(ProfileInfo.get(0).get("majorsubject")).substring(0,String.valueOf(ProfileInfo.get(0).get("majorsubject")).length()-1); //마지막 문자 삭제
            String subjectsub2 = subjectsub.substring(1); //첫번째 문자 삭제
            String subjectsub3 = subjectsub2.replaceAll(" ", ""); //모든 공백 제거
            String[] subjectarr = subjectsub3.split(",");

            //전체 과목에서 선택된 과목 분류함. 분류한 목록을 true로 변경
            for(int i = 0; i<Subjectlist.size();i++){
                for(int j=0;j<subjectarr.length;j++){
                    //Log.d("선택된 과목 리스트",subjectarr[j]);

                    //선택된 값이 존재한다면 True로 변경한다.
                    if(String.valueOf(i).equals(subjectarr[j])){
                        //Subjectlist.get(i).add
                        Log.d("체크된 과목 리스트", String.valueOf(i));
                        Log.d("체크된 과목 리스트", String.valueOf(Subjectlist.get(i).getName()));
                        Log.d("체크된 과목 리스트", String.valueOf(Subjectlist.get(i).isSelected()));

                        Subjectlist.get(i).setSelected(true);

                        //서버로 넘길 데이터 만들기
                        SelectSubjectlist.add(String.valueOf(i));
                    }
                }
            }
            Subjectrecycle(Subjectlist);

            schooledit.setText(String.valueOf(ProfileInfo.get(0).get("university")));

            //학생 학년 강제 선택
            Integer schoolchknum = Integer.valueOf((String.valueOf(ProfileInfo.get(0).get("universitychk"))));
            schoolchkspinner.setSelection(schoolchknum);

            capmusareaedit.setText(String.valueOf(ProfileInfo.get(0).get("campusaddress")));
            schoolsubjectedit.setText(String.valueOf(ProfileInfo.get(0).get("universmajor")));
            studentidedit.setText(String.valueOf(ProfileInfo.get(0).get("studentid")));
            onelineintroduceedit.setText(String.valueOf(ProfileInfo.get(0).get("introduce")));
            availabletimeedit.setText(String.valueOf(ProfileInfo.get(0).get("availabletime")));
            classdocumentedit.setText(String.valueOf(ProfileInfo.get(0).get("subjectdocument")));
            classstyleedit.setText(String.valueOf(ProfileInfo.get(0).get("classstyle")));
            skillappealedit.setText(String.valueOf(ProfileInfo.get(0).get("skillappeal")));

        }else{ //학생
            //닉네임
            nicnameedit.setText(String.valueOf(ProfileInfo.get(0).get("nicname")));

            //학생 학년 강제 선택
            Integer studentagenum = Integer.valueOf((String.valueOf(ProfileInfo.get(0).get("studentages"))));
            studentagespinner.setSelection(studentagenum);

            //성별
            if(String.valueOf(ProfileInfo.get(0).get("gender")).equals("1")){ //남
                sex1.setChecked(true);
            }else{ //여
                sex2.setChecked(true);
            }

            //선택된 과목 변환
            String subjectsub = String.valueOf(ProfileInfo.get(0).get("majorsubject")).substring(0,String.valueOf(ProfileInfo.get(0).get("majorsubject")).length()-1); //마지막 문자 삭제
            String subjectsub2 = subjectsub.substring(1); //첫번째 문자 삭제
            String subjectsub3 = subjectsub2.replaceAll(" ", ""); //모든 공백 제거
            String[] subjectarr = subjectsub3.split(",");

            //전체 과목에서 선택된 과목 분류함. 분류한 목록을 true로 변경
            for(int i = 0; i<Subjectlist.size();i++){
                for(int j=0;j<subjectarr.length;j++){
                    //Log.d("선택된 과목 리스트",subjectarr[j]);

                    //선택된 값이 존재한다면 True로 변경한다.
                    if(String.valueOf(i).equals(subjectarr[j])){
                        //Subjectlist.get(i).add
                        Log.d("체크된 과목 리스트", String.valueOf(i));
                        Log.d("체크된 과목 리스트", String.valueOf(Subjectlist.get(i).getName()));
                        Log.d("체크된 과목 리스트", String.valueOf(Subjectlist.get(i).isSelected()));

                        Subjectlist.get(i).setSelected(true);

                        //서버로 넘길 데이터 만들기
                        SelectSubjectlist.add(String.valueOf(i));
                    }
                }
            }
            Subjectrecycle(Subjectlist);

            onelineintroduceedit.setText(String.valueOf(ProfileInfo.get(0).get("introduce")));
            maxpayguideline.setText(String.valueOf(ProfileInfo.get(0).get("maxpay")));
            availabletimeedit.setText(String.valueOf(ProfileInfo.get(0).get("availabletime")));
            forteachertalkedit.setText(String.valueOf(ProfileInfo.get(0).get("infotalk")));
        }
    }


    //사용안함.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}