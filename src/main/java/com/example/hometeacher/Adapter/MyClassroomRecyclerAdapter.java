package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MyClassroomRecyclerAdapter extends RecyclerView.Adapter<MyClassroomRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<JSONObject> list;
    ArrayList<ArrayList<String>> Sessionlist;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<JSONObject> list) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private MyClassroomRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public MyClassroomRecyclerAdapter(Context context, Activity activity){
        oContext = context;
        oActivity = activity;
    }
    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<JSONObject> list){
        this.list = list;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myclassroomitem, parent, false);

        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.onBind(list.get(position));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameinfo, chatdocument, noreadcount, usercount, timeinfo;
        RecyclerView NboardimgRecyclerView;
        ImageView imgView_item;
        LinearLayout requestclasslinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
            usercount = (TextView) itemView.findViewById(R.id.usercount);
            timeinfo = (TextView) itemView.findViewById(R.id.timeinfo);
            chatdocument = (TextView) itemView.findViewById(R.id.chatdocument);
            noreadcount = (TextView) itemView.findViewById(R.id.noreadcount);
            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            requestclasslinear = (LinearLayout) itemView.findViewById(R.id.requestclasslinear);

            requestclasslinear.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){

                        if (mListener != null) {
                            try {
                                mListener.onItemClick(view, position, list) ; //리스너 통해서 값을 activity로 전달
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
//
//                        Intent intent = new Intent(view.getContext(), Nboardview.class);
//                        try {
//                            Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
//                            intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
//                            intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        view.getContext().startActivity(intent);
                    }
                }
            });
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(JSONObject item) throws JSONException, ParseException {

            nameinfo.setText(String.valueOf(item.get("roomname")));
            usercount.setText(String.valueOf(item.get("joinusercount")+"/"+String.valueOf(item.get("maxnum")))); //최대인원수에서 몇명이 참가했는지 나타냄.


            Log.d("--myclasssetinfo--", String.valueOf(item.get("myclass_setinfo")));
            //설정 값이 없는 경우
            if(String.valueOf(item.get("myclass_setinfo")).equals("null")){

            }else {
                JSONArray setinfo = new JSONArray(String.valueOf(item.get("myclass_setinfo")));
                //Log.d("--myclasssetinfo--", String.valueOf(setinfo.get(0)));
                JSONObject setinfoobj = new JSONObject(String.valueOf(setinfo.get(0)));

                if(!String.valueOf(setinfoobj.get("basicuri")).equals("")){
                    imgView_item.setBackgroundColor(Color.argb(0,0,0,0));
                    Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(setinfoobj.get("basicuri"))+String.valueOf(setinfoobj.get("src")));
                    Picasso.get()
                            .load(imageUri) // string or uri 상관없음
                            .resize(200, 200)
                            .centerCrop()
                            .into(imgView_item);
                }
            }

            if(!String.valueOf(item.get("currentmsgregdate")).equals("null")) {

                String currenttime = Makecurrenttime();//현재시간 불러오기
                Date dt1 = timeFormat.parse(currenttime); //현재시간

                //날짜 시간을 변형한다.
                String dateStr = String.valueOf(item.get("currentmsgregdate")); //2022-08-09 05:53:17
                Date dt2 = timeFormat.parse(dateStr); //등록시간

                long diff = dt1.getTime() - dt2.getTime();
                long diffsec = diff / 1000; // 초 구하기
                long diffmin = diff / (60000); //분 구하기
                long diffhour = diff /  (3600000); // 시간 구하기
                long diffdays = diffsec /  (24*60*60); //일 구하기
                System.out.println("diffdays : " + String.valueOf(diffdays));

                String[] dateStr_split = dateStr.split(" ");
                String[] dateStr_date = dateStr_split[0].split("-");
                String[] dateStr_time = dateStr_split[1].split(":");

                System.out.println("second : " + String.valueOf(dateStr_date[0]));
                System.out.println("second : " + String.valueOf(dateStr_time[0]));

                if(diffdays == 0){
                    String ampm = "";
                    if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
                        ampm = "오후";
                    }else{ //오전
                        ampm = "오전";
                    }
                    String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
                    timeinfo.setText(timemake);
                }else{
                    String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
                    System.out.println("datemake : " + String.valueOf(datemake));

                    timeinfo.setText(datemake);
                }
            }


            if(!String.valueOf(item.get("currentmsg")).equals("null")){
                if(String.valueOf(item.get("noreadchk")).equals("1")){
                    if(String.valueOf(item.get("currentimgchk")).equals("1")) { //이미지 일때
                        String imgalert = "사진을 보냈습니다.";
                        SpannableStringBuilder spannable = new SpannableStringBuilder(imgalert);
                        spannable.setSpan(
                                new ForegroundColorSpan(Color.RED),
                                // new StyleSpan(Typeface.BOLD),
                                0, imgalert.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        chatdocument.setText(spannable);

                        noreadcount.setText(String.valueOf(item.get("noreadcount")));
                    }else{ //문자열 일때
                        SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("currentmsg")));
                        spannable.setSpan(
                                new ForegroundColorSpan(Color.RED),
                                // new StyleSpan(Typeface.BOLD),
                                0, String.valueOf(item.get("currentmsg")).length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        chatdocument.setText(spannable);

                        noreadcount.setText(String.valueOf(item.get("noreadcount")));
                    }

                }else{
                    noreadcount.setText("");
                    if(String.valueOf(item.get("currentimgchk")).equals("1")) { //이미지 일때
                        chatdocument.setText("사진을 보냈습니다.");
                    }else{ //문자열 일때
                        if(String.valueOf(item.get("currentmsg")).length() >= 20) {
                            String cutchar = String.valueOf(item.get("currentmsg")).substring(0, 20) + "...";
                            chatdocument.setText(cutchar);
                        }else{
                            chatdocument.setText(String.valueOf(item.get("currentmsg")));
                        }
                    }

                }

            }else{
                noreadcount.setText("");
                chatdocument.setText("");
                timeinfo.setText("");
            }


        }
    }

    //과목 문자열로 변환
    public String subject_replace(String subject){
        if(subject.equals("0")){
            return "국어";
        }else if(subject.equals("1")){
            return "영어";
        }else if(subject.equals("2")){
            return "수학";
        }else if(subject.equals("3")){
            return "사회";
        }else if(subject.equals("4")){
            return "과학";
        }else if(subject.equals("5")){
            return "자격증";
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
    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
    //이미지 두개 합치기
    private Bitmap mergeMultiple(Bitmap[] parts){

        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }
        return result;
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
