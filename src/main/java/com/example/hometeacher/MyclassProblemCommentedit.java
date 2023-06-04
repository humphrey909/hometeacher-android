package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometeacher.Adapter.ImgAdapterMulti;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.shared.Session;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
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
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyclassProblemCommentedit extends AppCompatActivity {
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    String commentidx;
    ImageView closetab, camerabtn;
    EditText documentedit;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    int pagenum = 0;
    int limitnum = 10;

    ArrayList<MultipartBody.Part> imguploadlist_multipart = new ArrayList<>(); //업로드할 이미지 리스트 - MultipartBody = 전송할때 변환필요
    ArrayList<ImgFormMulti> imguploadlist_uri = new ArrayList<>(); //업로드할 이미지 리스트 - Uri / 출력해줄때, 업로드할때 변환전 으로 사용 / ImgForm로 변환하여 사용
    ArrayList<String> SelectImgDeletelist; //db 이미지 항목중에 삭제된 idx리스트 - db에서 삭제하기 위함

    RecyclerView imagesRecyclerView;
    TextView picturenum;

    int Selecttype;
    String Selectpid;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ArrayList<JSONObject> Commentinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_problem_commentedit);

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

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;

        picturenum = (TextView) this.findViewById(R.id.picturenum);
        documentedit = (EditText) this.findViewById(R.id.documentedit);
        SelectImgDeletelist = new ArrayList<>();

        //데이터 받음
        Intent intent = getIntent();
        commentidx = intent.getExtras().getString("cid"); //댓글 고유값
        //Log.d("-------MainCategoreyType-------", String.valueOf(MainCategoreyType));
        Log.d("-------Selecttype-------", String.valueOf(commentidx));


        //댓글 수정 닫기
        closetab = (ImageView) this.findViewById(R.id.closetab);
        closetab.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                finish();
            }
        });

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

        Commentinfo = new ArrayList<>();
        //Selectpid에 대한 내용가져오기
        GetCommentinfo();
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
                                .start(MyclassProblemCommentedit.this);
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
                                Toast.makeText(MyclassProblemCommentedit.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
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

                                Imagerecycle(imguploadlist_uri); //리사이클러뷰에 출력
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

                                Imagerecycle(imguploadlist_uri); //리사이클러뷰에 출력
                            }

                        }
                        //사진 갯수 변경
                        picturenum.setText(imguploadlist_uri.size()+"/10");

                        if(imguploadlist_uri.size() == 10){
                            Toast.makeText(MyclassProblemCommentedit.this, "사진 갯수 10개를 초과하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        //Log.d("----------imguploadlist_uri----------", String.valueOf(imguploadlist_uri.get(0).getUri()));


//                        //이미지 선택후 크롭화면 나오도록 설정
//                        CropImage.activity(intent.getData()).setGuidelines(CropImageView.Guidelines.ON)  // 크롭 위한 가이드 열어서 크롭할 이미지 받아오기
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)            // 사각형으로 자르기
//                                .start(Nboardwrite.this);
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

                        Imagerecycle(imguploadlist_uri); //리사이클러뷰에 출력
                    }

                    break;
            }
        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
    }


    //이미지 리스트 만듬
    public void Imagerecycle(ArrayList<ImgFormMulti> imglist) {

        imagesRecyclerView = (RecyclerView)findViewById(R.id.imagesRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false); //가로일때
        imagesRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
        ImgAdapterMulti ImgAdapterMulti = new ImgAdapterMulti(this, oActivity); //내가만든 어댑터 선언

        ImgAdapterMulti.settype(2); //게시판 등록 이미지
        ImgAdapterMulti.setRecycleList(imglist); //arraylist 연결
        imagesRecyclerView.setAdapter(ImgAdapterMulti); //리사이클러뷰 위치에 어답터 세팅


        //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
        ImgAdapterMulti.setOnItemClickListener(new ImgAdapterMulti.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(View v, int picturepos, int type, String imgidx) {

                Log.d("type", String.valueOf(type)); //업로드인지 1, 삭제인지 2 알려줌
                Log.d("position", String.valueOf(picturepos)); //이미지 위치를 알려줌
                //Log.d("clickpos", String.valueOf(clickpos)); //alert dialog 버튼 순서를 알려줌

                if(type == 2){ //2 삭제
                    imguploadlist_uri.remove(picturepos);
                    Imagerecycle(imguploadlist_uri);

                    if(!imgidx.equals("-")){
                        SelectImgDeletelist.add(imgidx); //이미지 삭제 리스트 생성
                    }

                    //사진 갯수 변경
                    picturenum.setText(imguploadlist_uri.size()+"/10");
                    Log.d("******SelectImgDeletelist*****", String.valueOf(SelectImgDeletelist));

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

                    if (urlget2.equals("1")) { //댓글 수정하기
                        Log.d("onResponse ? ", "과제 수정하기 : " + resultlist);

                        finish();


                    }else if(urlget2.equals("2")){ //댓글 정보 가져오기
                        Log.d("onResponse ? ", "댓글 정보 가져오기 : " + resultlist);


                        JSONArray jarray = null;

                        try {
                            jarray = new JSONArray(resultlist);
                            //Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get(0)));
                            Log.d("onResponse ? ", "과제 정보 가져오기 : " + String.valueOf(jarray.length()));
                            // Log.d("onResponse ? ", "게시글 정보들 : " + String.valueOf(jarray.get("nboardinfo")));
                            // Log.d("onResponse ? ", "게시글 이미지 : " + String.valueOf(jarray.get("nboardimg")).length());

                            for (int i = 0; i < jarray.length(); i++) {
                                Log.d("onResponse ? ", "과제 정보 가져오기 : " + String.valueOf(jarray.get(i)));

                                JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));

                                Commentinfo.add(jobj);
                            }

                            Log.d("onResponse ? ", "과제 정보 가져오기 : all" + String.valueOf(Commentinfo)); //info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //데이터를 바로 출력시킬것
                        try {
                            SetProbleminfo(Commentinfo);
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


    //서버에서 가져온데이터 입력
    @SuppressLint("SetTextI18n")
    public void SetProbleminfo(ArrayList<JSONObject> ProblemInfojsonarray) throws JSONException {

        documentedit.setText(String.valueOf(ProblemInfojsonarray.get(0).get("document")));




        //Log.d("------subcategorey-----", String.valueOf(NboardInfojsonarray.get(0).get("subcategorey")));
        //SubCategoreyType = String.valueOf(NboardInfojsonarray.get(0).get("subcategorey")); //서브 카테고리 선택
        //categoreybtn.setText(categoreylist.get(Integer.parseInt(SubCategoreyType)));
        //titleedit.setText(String.valueOf(NboardInfojsonarray.get(0).get("title")));
        //documentedit.setText(String.valueOf(NboardInfojsonarray.get(0).get("document")));


//        JSONArray jarray_img = new JSONArray(String.valueOf(NboardInfojsonarray.get(0).get("nboardimg")));
//        //게시글 이미지 출력
//        for(int i = 0; i < jarray_img.length(); i++){
//            JSONObject jobimg = jarray_img.getJSONObject(i);
//            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jobimg.get("basicuri")+jobimg.get("src");
//            Uri imageUri_nboard = Uri.parse(imagestring);
//
//            ImgFormMulti imgForm = new ImgFormMulti(imageUri_nboard,imageUri_nboard,String.valueOf(jobimg.get("idx")), false);
//
//            imguploadlist_uri.add(imgForm);
//        }
//        picturenum.setText(imguploadlist_uri.size()+"/10");
//
//        Imagerecycle(imguploadlist_uri);


        JSONArray jarray_img = new JSONArray(String.valueOf(ProblemInfojsonarray.get(0).get("commentimg")));
        //게시글 이미지 출력
        for(int i = 0; i < jarray_img.length(); i++){
            JSONObject jobimg = jarray_img.getJSONObject(i);
            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jobimg.get("basicuri")+jobimg.get("src");
            Uri imageUri_nboard = Uri.parse(imagestring);

            ImgFormMulti imgForm = new ImgFormMulti(imageUri_nboard,imageUri_nboard,String.valueOf(jobimg.get("idx")), false);

            imguploadlist_uri.add(imgForm);
        }
        picturenum.setText(imguploadlist_uri.size()+"/10");

        Imagerecycle(imguploadlist_uri);


    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.problemwritemenu, menu);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.completebtn:

                //카테고리 선택, 제목, 글 작성시 업로드 가능하도록
                if(!String.valueOf(documentedit.getText()).equals("")) {

                    //이미지 파일을 보낼 수 있는 형태로 변환해주는 부분
                    // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
                    for (int i = 0; i < imguploadlist_uri.size(); i++) {
                        //String path = imguploadlist_uri.get(i).getPath();

                        if (imguploadlist_uri.get(i).isSelected()) { //true인것만 체크할 것
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
                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file" + i, fileName, requestFile);


                            // 추가
                            imguploadlist_multipart.add(filePart);
                        }
                    }


                    //프로필 정보 한번에 넘기기
                    HashMap<String, RequestBody> requestMap = new HashMap<>();
                    RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Sessionlist.get(1).get(0)));
                    requestMap.put("uid", uid);

                    String currenttime = Makecurrenttime();//현재시간 불러오기
                    RequestBody currenttime_ = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currenttime));
                    requestMap.put("currenttime", currenttime_);

                    RequestBody document = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(documentedit.getText()));
                    requestMap.put("document", document);


                    RequestBody cid = RequestBody.create(MediaType.parse("text/plain"), commentidx);
                    requestMap.put("cid", cid);

                    RequestBody imgdeletelist = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SelectImgDeletelist)); //이미지 삭제 리스트
                    requestMap.put("imgdeletelist", imgdeletelist);

                    RestapiStart(); //레트로핏 빌드

                    call = retrofitService.problemcommentedit(
                            1, requestMap, imguploadlist_multipart
                    );
                    RestapiResponse(); //응답
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }

    //과제 정보 가져오기
    public void GetCommentinfo() {

        //보낼값
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        RequestBody cid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(commentidx));
        requestMap.put("cid", cid);

        //유저 리스트를 불러옴
        RestapiStart(); //레트로핏 빌드
        call = retrofitService.getproblemcommentlist(
                0,
                0,
                2,
                requestMap
        );
        RestapiResponse(); //응답
    }
}