package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.ChattingForm;
import com.example.hometeacher.ImgViewPagerActivity;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class RequestChatRecyclerAdapter extends RecyclerView.Adapter<RequestChatRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<ChattingForm> list;
    ArrayList<ArrayList<String>> Sessionlist;

    String backcolor;
    String type; //1. 과외문의, 2. 내과외

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<ChattingForm> list, int type) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private RequestChatRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public RequestChatRecyclerAdapter(Context context, Activity activity){
        oContext = context;
        oActivity = activity;
    }
    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }
    public void setColor(String backcolor) {
        this.backcolor = backcolor;
    }
    public void setType(String type) {
        this.type = type;
    }


    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ChattingForm> list){
        this.list = list;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(getViewSrc(viewType), parent, false);
        return new ViewHolder(view, viewType);
    }

    //뷰홀더의 타입을 변경해줌
    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getwho().equals("1") && !list.get(position).getimgchk().equals("1") && list.get(position).getsendrid().equals("0")){ //나, 보내는
            return TYPE_SENT_MESSAGE;
        } else if(list.get(position).getwho().equals("0") && !list.get(position).getimgchk().equals("1") && list.get(position).getsendrid().equals("0")) { //다른사람, 받는
            return TYPE_RECEIVED_MESSAGE;
        }else if(list.get(position).getwho().equals("2")){ //전체공지
            return TYPE_NOTICE_MESSAGE;
        }else if(list.get(position).getwho().equals("1") && list.get(position).getimgchk().equals("1")){ //나, 보내는 / IMG
            return TYPE_IMG_SENT_MESSAGE;
        }else if(list.get(position).getwho().equals("0") && list.get(position).getimgchk().equals("1")) { //다른사람, 받는 / IMG
            return TYPE_IMG_RECEIVED_MESSAGE;
        }else if(list.get(position).getwho().equals("1") && !list.get(position).getsendrid().equals("0")){ //나, 보내는 / 질문
            return TYPE_Q_SENT_MESSAGE;
        }else if(list.get(position).getwho().equals("0") && !list.get(position).getsendrid().equals("0")){ //다른사람, 받는 / 질문
                return TYPE_Q_RECEIVED_MESSAGE;
        }else{
            return 0;
        }
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
        private int viewType;

        TextView chatdocument, chatregdate, noticedocument, readnum, name, yesbtn, nobtn, endview;
        ImageView profileimg_item, chatimg_item;
        LinearLayout requestclasslinear;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            //받은 메세지, 질문 타입
            if(viewType == TYPE_Q_RECEIVED_MESSAGE){
                endview = itemView.findViewById(R.id.endview); //종료됐을때 나타나는 view

                yesbtn = itemView.findViewById(R.id.yesbtn); //승인
                yesbtn.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition ();
                        if (position!=RecyclerView.NO_POSITION) {
                            //Log.d("----tag----", "승인" + list.get(position).getsendrid());

                            int type = 1;
                            if (mListener != null) {
                                mListener.onItemClick(view, position, list, type) ; //리스너 통해서 값을 activity로 전달
                            }
                        }
                    }
                });

                nobtn = itemView.findViewById(R.id.nobtn); //거절
                nobtn.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition ();
                        if (position!=RecyclerView.NO_POSITION) {
                            //Log.d("----tag----", "거절" + list.get(position).getsendrid());

                            int type = 0;
                            if (mListener != null) {
                                mListener.onItemClick(view, position, list, type) ; //리스너 통해서 값을 activity로 전달
                            }
                        }
                    }
                });

//                gomyclassbtn = itemView.findViewById(R.id.gomyclassbtn); //내 과외 로 이동하는 버튼
//                //종료 후 과외방 이동하는 버튼
//                gomyclassbtn.setOnClickListener (new View.OnClickListener () {
//                    @Override
//                    public void onClick(View view) {
//                        int position = getAdapterPosition ();
//                        if (position!=RecyclerView.NO_POSITION) {
//                            //Log.d("----tag----", "거절" + list.get(position).getsendrid());
//
//                            int type = 2;
//                            if (mListener != null) {
//                                mListener.onItemClick(view, position, list, type) ; //리스너 통해서 값을 activity로 전달
//                            }
//                        }
//                    }
//                });
            }

//            requestclasslinear = (LinearLayout) itemView.findViewById(R.id.requestclasslinear);
//            requestclasslinear.setOnClickListener (new View.OnClickListener () {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition ();
//                    if (position!=RecyclerView.NO_POSITION){
//
////                        if (mListener != null) {
////                            mListener.onItemClick(view, position, list) ; //리스너 통해서 값을 activity로 전달
////                        }
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
        void onBind(ChattingForm item) throws JSONException, ParseException {
            if (viewType==TYPE_SENT_MESSAGE){
                bindSendMessage(item);
            } else if(viewType==TYPE_RECEIVED_MESSAGE) {
                bindReceivedMessage(item);
            } else if(viewType==TYPE_NOTICE_MESSAGE) {
                bindNoticeMessage(item);
            } else if(viewType==TYPE_IMG_SENT_MESSAGE) {
                bindImgSendMessage(item);
            } else if(viewType==TYPE_IMG_RECEIVED_MESSAGE) {
                bindImgReceivedMessage(item);
            } else if(viewType==TYPE_Q_SENT_MESSAGE) {
                bindQSendMessage(item);
            } else if(viewType==TYPE_Q_RECEIVED_MESSAGE) {
                bindQReceivedMessage(item);
            }


        }

        //보내는 메세지
        private void bindSendMessage(ChattingForm item){
            chatdocument = itemView.findViewById(R.id.chatdocument); //내용
            chatdocument.setText(item.getmessage());

            chatregdate = itemView.findViewById(R.id.chatregdate); //날짜
            //날짜 시간을 변형한다.
            String dateStr = String.valueOf(item.getregdate()); //2022-08-09 05:53:17
            String[] dateStr_split = dateStr.split(" ");
            String[] dateStr_date = dateStr_split[0].split("-");
            String[] dateStr_time = dateStr_split[1].split(":");
            System.out.println("second : " + String.valueOf(dateStr_date[0]));
            System.out.println("second : " + String.valueOf(dateStr_time[0]));

            String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
            String ampm = "";
            if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
                ampm = "오후";
            }else{ //오전
                ampm = "오전";
            }
            String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
            chatregdate.setText(datemake+timemake);

            //chatregdate.setText(item.getregdate());

            readnum = itemView.findViewById(R.id.readnum); //읽음 숫자 체크
            if(!item.getnoreadnum().equals("0")){
                readnum.setText(item.getnoreadnum());
            }else{
                readnum.setText("");
            }

            Log.d("----backcolor-----",backcolor);
            if(type.equals("2")){ //내 과외 일때만
                //배경색에 맞는 폰트 색 변경
                if(backcolor.equals("#818b9c") || backcolor.equals("#10374a") || backcolor.equals("#404372") || backcolor.equals("#5b4d49") || backcolor.equals("#525252")){
                    chatregdate.setTextColor(Color.parseColor("#ffffff"));
                    readnum.setTextColor(Color.parseColor("#ffffff"));
                }else{
                    chatregdate.setTextColor(Color.parseColor("#000000"));
                    readnum.setTextColor(Color.parseColor("#000000"));
                }
            }

        }

        //받는 메세지
        private void bindReceivedMessage(ChattingForm item){
            chatdocument = itemView.findViewById(R.id.chatdocument); //내용
            chatdocument.setText(item.getmessage());

            chatregdate = itemView.findViewById(R.id.chatregdate); //날짜
            //날짜 시간을 변형한다.
            String dateStr = String.valueOf(item.getregdate()); //2022-08-09 05:53:17
            String[] dateStr_split = dateStr.split(" ");
            String[] dateStr_date = dateStr_split[0].split("-");
            String[] dateStr_time = dateStr_split[1].split(":");
            System.out.println("second : " + String.valueOf(dateStr_date[0]));
            System.out.println("second : " + String.valueOf(dateStr_time[0]));

            String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
            String ampm = "";
            if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
                ampm = "오후";
            }else{ //오전
                ampm = "오전";
            }
            String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
            chatregdate.setText(datemake+timemake);
            //chatregdate.setText(item.getregdate());

            name = itemView.findViewById(R.id.name);//상대 이름
            name.setText(item.getname());

            readnum = itemView.findViewById(R.id.readnum); //읽음 숫자 체크
            if(!item.getnoreadnum().equals("0")){
                readnum.setText(item.getnoreadnum());
            }else{
                readnum.setText("");
            }


            if(type.equals("2")) { //내 과외 일때만
                //배경색에 맞는 폰트 색 변경
                if (backcolor.equals("#818b9c") || backcolor.equals("#10374a") || backcolor.equals("#404372") || backcolor.equals("#5b4d49") || backcolor.equals("#525252")) {
                    chatregdate.setTextColor(Color.parseColor("#ffffff"));
                    readnum.setTextColor(Color.parseColor("#ffffff"));
                    name.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    chatregdate.setTextColor(Color.parseColor("#000000"));
                    readnum.setTextColor(Color.parseColor("#000000"));
                    name.setTextColor(Color.parseColor("#000000"));
                }
            }

            profileimg_item = (ImageView) itemView.findViewById(R.id.profileimg_item); //프로필 이미지
            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.getprofileimg();
            Uri imageUri = Uri.parse(imagestring);

            Picasso.get()
                    .load(imageUri) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(profileimg_item);
        }

        //공지사항
        private void bindNoticeMessage(ChattingForm item){
            noticedocument = itemView.findViewById(R.id.noticedocument);
            noticedocument.setText(item.getmessage());

            if(type.equals("2")) { //내 과외 일때만
                //배경색에 맞는 폰트 색 변경
                if (backcolor.equals("#818b9c") || backcolor.equals("#10374a") || backcolor.equals("#404372") || backcolor.equals("#5b4d49") || backcolor.equals("#525252")) {
                    noticedocument.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    noticedocument.setTextColor(Color.parseColor("#000000"));
                }
            }
        }

        //보내는 이미지
        private void bindImgSendMessage(ChattingForm item){
            chatimg_item = itemView.findViewById(R.id.chatimg_item); //이미지
            String imagestring_msg = RetrofitService.MOCK_SERVER_FIRSTURL+item.getmessage();
            Uri imageUri_msg = Uri.parse(imagestring_msg);

            Picasso.get()
                    .load(imageUri_msg) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(chatimg_item);

            chatregdate = itemView.findViewById(R.id.chatregdate); //날짜
            //날짜 시간을 변형한다.
            String dateStr = String.valueOf(item.getregdate()); //2022-08-09 05:53:17
            String[] dateStr_split = dateStr.split(" ");
            String[] dateStr_date = dateStr_split[0].split("-");
            String[] dateStr_time = dateStr_split[1].split(":");
            System.out.println("second : " + String.valueOf(dateStr_date[0]));
            System.out.println("second : " + String.valueOf(dateStr_time[0]));

            String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
            String ampm = "";
            if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
                ampm = "오후";
            }else{ //오전
                ampm = "오전";
            }
            String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
            chatregdate.setText(datemake+timemake);
            //chatregdate.setText(item.getregdate());

            readnum = itemView.findViewById(R.id.readnum); //읽음 숫자 체크
            if(!item.getnoreadnum().equals("0")){
                readnum.setText(item.getnoreadnum());
            }else{
                readnum.setText("");
            }

            if(type.equals("2")) { //내 과외 일때만
                //배경색에 맞는 폰트 색 변경
                if (backcolor.equals("#818b9c") || backcolor.equals("#10374a") || backcolor.equals("#404372") || backcolor.equals("#5b4d49") || backcolor.equals("#525252")) {
                    chatregdate.setTextColor(Color.parseColor("#ffffff"));
                    readnum.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    chatregdate.setTextColor(Color.parseColor("#000000"));
                    readnum.setTextColor(Color.parseColor("#000000"));
                }
            }


            //이미지 클릭시 전체화면으로 보여주기
            ArrayList<String> imglist = new ArrayList<>();
            imglist.add(imagestring_msg);

            chatimg_item.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(oContext, ImgViewPagerActivity.class);
                    intent.putExtra("sendimg",  String.valueOf(imglist));
                    intent.putExtra("position",  imglist.size());
                    oActivity.startActivity(intent);
                }
            });
        }

        //받는 이미지
        private void bindImgReceivedMessage(ChattingForm item){
            chatimg_item = itemView.findViewById(R.id.chatimg_item); //이미지
            String imagestring_msg = RetrofitService.MOCK_SERVER_FIRSTURL+item.getmessage();
            Uri imageUri_msg = Uri.parse(imagestring_msg);

            Picasso.get()
                    .load(imageUri_msg) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(chatimg_item);

            chatregdate = itemView.findViewById(R.id.chatregdate); //날짜
            //날짜 시간을 변형한다.
            String dateStr = String.valueOf(item.getregdate()); //2022-08-09 05:53:17
            String[] dateStr_split = dateStr.split(" ");
            String[] dateStr_date = dateStr_split[0].split("-");
            String[] dateStr_time = dateStr_split[1].split(":");
            System.out.println("second : " + String.valueOf(dateStr_date[0]));
            System.out.println("second : " + String.valueOf(dateStr_time[0]));

            String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
            String ampm = "";
            if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
                ampm = "오후";
            }else{ //오전
                ampm = "오전";
            }
            String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
            chatregdate.setText(datemake+timemake);
           // chatregdate.setText(item.getregdate());

            readnum = itemView.findViewById(R.id.readnum); //읽음 숫자 체크
            if(!item.getnoreadnum().equals("0")){
                readnum.setText(item.getnoreadnum());
            }else{
                readnum.setText("");
            }

            if(type.equals("2")) { //내 과외 일때만
                //배경색에 맞는 폰트 색 변경
                if (backcolor.equals("#818b9c") || backcolor.equals("#10374a") || backcolor.equals("#404372") || backcolor.equals("#5b4d49") || backcolor.equals("#525252")) {
                    chatregdate.setTextColor(Color.parseColor("#ffffff"));
                    readnum.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    chatregdate.setTextColor(Color.parseColor("#000000"));
                    readnum.setTextColor(Color.parseColor("#000000"));
                }
            }

            profileimg_item = (ImageView) itemView.findViewById(R.id.profileimg_item); //프로필 이미지
            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.getprofileimg();
            Uri imageUri = Uri.parse(imagestring);

            Picasso.get()
                    .load(imageUri) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(profileimg_item);

            //이미지 클릭시 전체화면으로 보여주기
            ArrayList<String> imglist = new ArrayList<>();
            imglist.add(imagestring_msg);

            chatimg_item.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(oContext, ImgViewPagerActivity.class);
                    intent.putExtra("sendimg",  String.valueOf(imglist));
                    intent.putExtra("position",  imglist.size());
                    oActivity.startActivity(intent);
                }
            });
        }

        //보내는 질문 폼
        @SuppressLint("SetTextI18n")
        private void bindQSendMessage(ChattingForm item) {
            chatdocument = itemView.findViewById(R.id.chatdocument); //내용
            chatdocument.setText(item.getmessage());

            chatregdate = itemView.findViewById(R.id.chatregdate); //날짜
            chatregdate.setText(item.getregdate());

            readnum = itemView.findViewById(R.id.readnum); //읽음 숫자 체크
            if(!item.getnoreadnum().equals("0")){
                readnum.setText(item.getnoreadnum());
            }else{
                readnum.setText("");
            }
        }

        //받는 질문 폼
        @SuppressLint("SetTextI18n")
        private void bindQReceivedMessage(ChattingForm item) {
            chatdocument = itemView.findViewById(R.id.chatdocument); //내용
            chatdocument.setText(item.getmessage());

            chatregdate = itemView.findViewById(R.id.chatregdate); //날짜
            chatregdate.setText(item.getregdate());

            name = itemView.findViewById(R.id.name);//상대 이름
            name.setText(item.getname());

            readnum = itemView.findViewById(R.id.readnum); //읽음 숫자 체크
            if(!item.getnoreadnum().equals("0")){
                readnum.setText(item.getnoreadnum());
            }else{
                readnum.setText("");
            }

            profileimg_item = (ImageView) itemView.findViewById(R.id.profileimg_item); //프로필 이미지
            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.getprofileimg();
            Uri imageUri = Uri.parse(imagestring);

            Picasso.get()
                    .load(imageUri) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(profileimg_item);

            //2. 예, 3. 아니요
            if(item.getavailablilty().equals("2")) {
                yesbtn.setVisibility(View.GONE);
                nobtn.setVisibility(View.GONE);
                endview.setVisibility(View.VISIBLE);
                endview.setText("종료된 질문");
                //gomyclassbtn.setVisibility(View.VISIBLE);
                //gomyclassbtn.setText("과외 방으로 이동 | ");
            }else if(item.getavailablilty().equals("3")){
                yesbtn.setVisibility(View.GONE);
                nobtn.setVisibility(View.GONE);
                endview.setVisibility(View.VISIBLE);
                endview.setText("종료된 질문");
                //gomyclassbtn.setVisibility(View.GONE);

            }else if(item.getavailablilty().equals("1")){
                yesbtn.setVisibility(View.VISIBLE);
                nobtn.setVisibility(View.VISIBLE);
                endview.setVisibility(View.GONE);
                //gomyclassbtn.setVisibility(View.GONE);
            }else{
                yesbtn.setVisibility(View.GONE);
                nobtn.setVisibility(View.GONE);
                //gomyclassbtn.setVisibility(View.GONE);
                endview.setVisibility(View.VISIBLE);
                endview.setText("잘못된 경로");
            }

        }


    }


    // view type
    private int TYPE_SENT_MESSAGE = 101; //보내는 메세지 문자
    private int TYPE_RECEIVED_MESSAGE = 102; //받는 메세지 문자
    private int TYPE_NOTICE_MESSAGE = 103; //공지
    private int TYPE_IMG_SENT_MESSAGE = 104; //보내는 메세지 이미지
    private int TYPE_IMG_RECEIVED_MESSAGE = 105; //받는 메세지 이미지
    private int TYPE_Q_SENT_MESSAGE = 106; //보내는 메세지 질문
    private int TYPE_Q_RECEIVED_MESSAGE = 107; //받는 메세지 질문
    private int getViewSrc(int viewType){
        if (viewType==TYPE_RECEIVED_MESSAGE){
            return R.layout.requestchatitem_receive;
        } else if(viewType==TYPE_SENT_MESSAGE){
            return R.layout.requestchatitem_send;
        } else if(viewType==TYPE_NOTICE_MESSAGE){
            return R.layout.requestchatitem_notice;
        } else if(viewType==TYPE_IMG_SENT_MESSAGE){
            return R.layout.requestchatimgitem_send;
        } else if(viewType==TYPE_IMG_RECEIVED_MESSAGE){
            return R.layout.requestchatimgitem_receive;
        } else if(viewType==TYPE_Q_SENT_MESSAGE){
            return R.layout.requestchatquestionitem_send;//
        } else if(viewType==TYPE_Q_RECEIVED_MESSAGE){
            return R.layout.requestchatquestionitem_receive; //
        }else{
            return 0;
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
}
