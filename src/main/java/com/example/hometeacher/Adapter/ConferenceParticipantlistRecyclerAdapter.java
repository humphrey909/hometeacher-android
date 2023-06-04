package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.conferenceUserForm;
import com.example.hometeacher.Profile.Profileview;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ConferenceParticipantlistRecyclerAdapter extends RecyclerView.Adapter<ConferenceParticipantlistRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;


    ArrayList<conferenceUserForm> list;
    ArrayList<ArrayList<String>> Sessionlist;
    int superuser = 0; // 방장이면 1로 표시 아니면 0

    String searchtext = "";

    View view;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<JSONObject> list) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private ConferenceParticipantlistRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public ConferenceParticipantlistRecyclerAdapter(Context context){
        oContext = context;
    }

    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<conferenceUserForm> list){
        this.list = list;
    }
//    public void setSelectUser(ArrayList<JSONObject> selectuser, int superuser){
//        this.selectuser = selectuser;
//        this.superuser = superuser;
//    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conference_participantitem, parent, false);

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
        TextView nameinfo, usertype, invitechk;
        ImageView imgView_item, mikebtn, camerabtn;
        LinearLayout Linearclickbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Linearclickbtn = (LinearLayout) itemView.findViewById(R.id.Linearclickbtn);
            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            mikebtn = (ImageView) itemView.findViewById(R.id.mikebtn);
            camerabtn = (ImageView) itemView.findViewById(R.id.camerabtn);
            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
            usertype = (TextView) itemView.findViewById(R.id.usertype);
            invitechk = (TextView) itemView.findViewById(R.id.invitechk);

            //이름클릭시
            nameinfo.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        Intent intent = new Intent(view.getContext(), Profileview.class);
                        intent.putExtra("sendtype", "2"); //다른 유저 idx
                        intent.putExtra("senduid", String.valueOf(list.get(position).getuid()));
                        intent.putExtra("sendusertype", String.valueOf(list.get(position).getusertype()));

                        view.getContext().startActivity(intent);
                    }
                }
            });

            //마이크 버튼
            mikebtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });

            //카메라 버튼
            camerabtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(conferenceUserForm item) throws JSONException, ParseException {

            nameinfo.setText(String.valueOf(item.getname()));

            //Sessionlist.get(1).get(0)
            if(item.getuid().equals(Sessionlist.get(1).get(0))){ //현재 디바이스와 매칭되는 유저라면 나라고 표시
                usertype.setText(usertype_replace(String.valueOf(item.getusertype()))+" (나)");
            }else{
                usertype.setText(usertype_replace(String.valueOf(item.getusertype())));
            }


            //카메라 on off 보여줌
            if(item.getcamerachk()){
                camerabtn.setImageResource(R.drawable.cameraside_on);
            }else{
                camerabtn.setImageResource(R.drawable.cameraside_off);
            }

            //마이크 on off 보여줌
            if(item.getmikechk()){
                mikebtn.setImageResource(R.drawable.mike_on);
            }else{
                mikebtn.setImageResource(R.drawable.mike_off);
            }


            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.getprofileimg();
            Uri imageUri = Uri.parse(imagestring);
            Picasso.get()
                    .load(imageUri) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(imgView_item);

        }


        //유저타입 문자열로 변환
        public String usertype_replace(String usertype){
            if(usertype.equals("1")){
                return "선생님";
            }else if(usertype.equals("2")){
                return "학생";
            }else{
                return "-";
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
        //성별 문자열로 변환
        public String gender_replace(String gender){
            if(gender.equals("1")){
                return "남";
            }else{
                return "여";
            }
        }

        //멘토링 서브카테고리 문자열로 변환
        public String mentoring_replace(String subject){
            if(subject.equals("0")){
                return "공부상담";
            }else if(subject.equals("1")){
                return "문제풀이";
            }else if(subject.equals("2")){
                return "고민상담";
            }else if(subject.equals("3")){
                return "입시질문";
            }else{
                return "-";
            }
        }

        //선생님토론 서브카테고리 문자열로 변환
        public String teacherdedate_replace(String subject){
            if(subject.equals("0")){
                return "자유로운 얘기";
            }else if(subject.equals("1")){
                return "과외성사";
            }else if(subject.equals("2")){
                return "이용방법";
            }else if(subject.equals("3")){
                return "수업교재";
            }else if(subject.equals("4")){
                return "수업료";
            }else if(subject.equals("5")){
                return "문제질문";
            }else if(subject.equals("6")){
                return "수업내용";
            }else if(subject.equals("7")){
                return "수업진행";
            }else if(subject.equals("8")){
                return "정보공유";
            }else if(subject.equals("9")){
                return "기타";
            }else{
                return "-";
            }
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
