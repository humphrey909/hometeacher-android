package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.conferenceUserForm;
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

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class ConferenceScreenRecyclerAdapter extends RecyclerView.Adapter<ConferenceScreenRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<conferenceUserForm> list;
    ArrayList<ArrayList<String>> Sessionlist;

    RtcEngine mRtcEngine;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<JSONObject> list) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private ConferenceScreenRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public void setdata(RtcEngine mRtcEngine){
        this.mRtcEngine = mRtcEngine;
    }

    public ConferenceScreenRecyclerAdapter(Context context, Activity activity){
        oContext = context;
        oActivity = activity;
    }
    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<conferenceUserForm> list){
        this.list = list;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conferencescreenitem, parent, false);

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
       // TextView nameinfo, chatdocument, noreadcount, usercount, timeinfo;
       // RecyclerView NboardimgRecyclerView;
        //ImageView imgView_item;
        //LinearLayout requestclasslinear;

        FrameLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.remote_video_view_container);

//            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
//            usercount = (TextView) itemView.findViewById(R.id.usercount);
//            timeinfo = (TextView) itemView.findViewById(R.id.timeinfo);
//            chatdocument = (TextView) itemView.findViewById(R.id.chatdocument);
//            noreadcount = (TextView) itemView.findViewById(R.id.noreadcount);
//            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
//            requestclasslinear = (LinearLayout) itemView.findViewById(R.id.requestclasslinear);

//            requestclasslinear.setOnClickListener (new View.OnClickListener () {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition ();
//                    if (position!=RecyclerView.NO_POSITION){
//
//                        if (mListener != null) {
//                            try {
//                                mListener.onItemClick(view, position, list) ; //리스너 통해서 값을 activity로 전달
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
////
////                        Intent intent = new Intent(view.getContext(), Nboardview.class);
////                        try {
////                            Log.d("adapter 게시글 리스트 이다.", "게시글 리스트 idx"+String.valueOf(list.get(position).get("idx")));
////                            intent.putExtra("nid", String.valueOf(list.get(position).get("idx")));
////                            intent.putExtra("maincategorey", String.valueOf(list.get(position).get("maincategorey")));
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                        view.getContext().startActivity(intent);
//                    }
//                }
//            });
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(conferenceUserForm item) throws JSONException, ParseException {


            //if(item.getlifemod() == 1){
                Log.d("--item--", String.valueOf(item));
                //JSONObject jobj = new JSONObject(String.valueOf(item));
                //int rtcuid = Integer.parseInt(String.valueOf(jobj.get("rtcuid")));
                int rtcuid = item.getrtcuid();
                Log.d("--item--", String.valueOf(rtcuid));
                //상대방의 화면을 보이게 해주는 부분
                //FrameLayout container = itemView.findViewById(R.id.remote_video_view_container);
                SurfaceView surfaceView = RtcEngine.CreateRendererView(oContext);
                surfaceView.setZOrderMediaOverlay(true);
                container.addView(surfaceView);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, rtcuid));

           // }

//            nameinfo.setText(String.valueOf(item.get("roomname")));
//            usercount.setText(String.valueOf(item.get("joinusercount")+"/"+String.valueOf(item.get("maxnum")))); //최대인원수에서 몇명이 참가했는지 나타냄.
//
//
//            Log.d("--myclasssetinfo--", String.valueOf(item.get("myclass_setinfo")));
//            //설정 값이 없는 경우
//            if(String.valueOf(item.get("myclass_setinfo")).equals("null")){
//
//            }else {
//                JSONArray setinfo = new JSONArray(String.valueOf(item.get("myclass_setinfo")));
//                //Log.d("--myclasssetinfo--", String.valueOf(setinfo.get(0)));
//                JSONObject setinfoobj = new JSONObject(String.valueOf(setinfo.get(0)));
//
//                if(!String.valueOf(setinfoobj.get("basicuri")).equals("")){
//                    imgView_item.setBackgroundColor(Color.argb(0,0,0,0));
//                    Uri imageUri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+String.valueOf(setinfoobj.get("basicuri"))+String.valueOf(setinfoobj.get("src")));
//                    Picasso.get()
//                            .load(imageUri) // string or uri 상관없음
//                            .resize(200, 200)
//                            .centerCrop()
//                            .into(imgView_item);
//                }
//            }
//
//            if(!String.valueOf(item.get("currentmsgregdate")).equals("null")) {
//
//                //날짜 시간을 변형한다.
//                String dateStr = String.valueOf(item.get("currentmsgregdate")); //2022-08-09 05:53:17
//                String[] dateStr_split = dateStr.split(" ");
//                String[] dateStr_date = dateStr_split[0].split("-");
//                String[] dateStr_time = dateStr_split[1].split(":");
//                System.out.println("second : " + String.valueOf(dateStr_date[0]));
//                System.out.println("second : " + String.valueOf(dateStr_time[0]));
//
//                //String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
//                String ampm = "";
//                if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
//                    ampm = "오후";
//                }else{ //오전
//                    ampm = "오전";
//                }
//                String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
//                timeinfo.setText(timemake);
//
//            }
//
//
//            if(!String.valueOf(item.get("currentmsg")).equals("null")){
//                if(String.valueOf(item.get("noreadchk")).equals("1")){
//                    if(String.valueOf(item.get("currentimgchk")).equals("1")) { //이미지 일때
//                        String imgalert = "사진을 보냈습니다.";
//                        SpannableStringBuilder spannable = new SpannableStringBuilder(imgalert);
//                        spannable.setSpan(
//                                new ForegroundColorSpan(Color.RED),
//                                // new StyleSpan(Typeface.BOLD),
//                                0, imgalert.length(),
//                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                        );
//                        chatdocument.setText(spannable);
//
//                        noreadcount.setText(String.valueOf(item.get("noreadcount")));
//                    }else{ //문자열 일때
//                        SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("currentmsg")));
//                        spannable.setSpan(
//                                new ForegroundColorSpan(Color.RED),
//                                // new StyleSpan(Typeface.BOLD),
//                                0, String.valueOf(item.get("currentmsg")).length(),
//                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                        );
//                        chatdocument.setText(spannable);
//
//                        noreadcount.setText(String.valueOf(item.get("noreadcount")));
//                    }
//
//                }else{
//                    noreadcount.setText("");
//                    if(String.valueOf(item.get("currentimgchk")).equals("1")) { //이미지 일때
//                        chatdocument.setText("사진을 보냈습니다.");
//                    }else{ //문자열 일때
//                        if(String.valueOf(item.get("currentmsg")).length() >= 20) {
//                            String cutchar = String.valueOf(item.get("currentmsg")).substring(0, 20) + "...";
//                            chatdocument.setText(cutchar);
//                        }else{
//                            chatdocument.setText(String.valueOf(item.get("currentmsg")));
//                        }
//                    }
//
//                }
//
//            }else{
//                noreadcount.setText("");
//                chatdocument.setText("");
//                timeinfo.setText("");
//            }


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
}
