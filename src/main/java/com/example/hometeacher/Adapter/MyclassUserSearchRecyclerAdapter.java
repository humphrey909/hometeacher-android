package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.Profile.Loginpage;
import com.example.hometeacher.Profile.Profileview;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MyclassUserSearchRecyclerAdapter extends RecyclerView.Adapter<MyclassUserSearchRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;


    ArrayList<JSONObject> list;
    ArrayList<JSONObject> selectuser;
    ArrayList<JSONObject> selectuser_invite;
    ArrayList<ArrayList<String>> Sessionlist;
    String accesstype; // 1. 방생성할때 접근, 2. 방 생성 후 접근

    //int type = 0;
    String searchtext = "";

    View view;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<JSONObject> list, Boolean user_checkbox) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private MyclassUserSearchRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public MyclassUserSearchRecyclerAdapter(Context context){
        oContext = context;
    }

    //type 0. 과외찾기, 1. 게시판, 2. 사람
//    public void needdata(int type, String searchtext){
//        this.type = type;
//        this.searchtext = searchtext;
//    }
    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<JSONObject> list){
        this.list = list;
    }
    public void setSelectUser(ArrayList<JSONObject> selectuser, ArrayList<JSONObject> selectuser_invite, String accesstype){
        this.selectuser = selectuser;
        this.selectuser_invite = selectuser_invite;
        this.accesstype = accesstype; // 1. 방생성할때 접근, 2. 방 생성 후 접근
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myclass_useritem, parent, false);

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
        TextView nameinfo, schoolinfo, subjectinfo, searchword;
        ImageView imgView_item;
        LinearLayout profileitemLinear, Linearclickbtn;
        CheckBox user_checkbox;

        TextView subcateinfo, titleinfo, documentinfo, writeinfo, timeinfo, likenum, usertypeinfo, commentnum;
       // RecyclerView NboardimgRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Linearclickbtn = (LinearLayout) itemView.findViewById(R.id.Linearclickbtn);
            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
            usertypeinfo = (TextView) itemView.findViewById(R.id.usertypeinfo);
            user_checkbox = (CheckBox)itemView.findViewById(R.id.user_checkbox);

            user_checkbox.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, position, list, user_checkbox.isChecked()) ; //리스너 통해서 값을 activity로 전달
                        }
//                        Toast.makeText(oContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(oContext, String.valueOf(user_checkbox.isChecked()), Toast.LENGTH_SHORT).show();
//
//
//                        //true
//                        if(user_checkbox.isChecked()){
//
//                        }else{ //false
//
//                        }
                    }
                }
            });

            Linearclickbtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                            if(Sessionlist.isEmpty()){ //빈값 = 로그아웃상태
                                Intent intent = new Intent(view.getContext(), Loginpage.class);
                                view.getContext().startActivity(intent);
                            }else{
                                Intent intent = new Intent(view.getContext(), Profileview.class);

                                //Log.d("----divisionlikelistchk-----",String.valueOf(divisionlikelistchk));
                                try {
                                    intent.putExtra("sendtype", "2"); //다른 유저 idx
                                    //if(divisionlikelistchk == 1){
                                    //    intent.putExtra("senduid", String.valueOf(list.get(position).get("uid"))); //
                                    //}else{
                                    intent.putExtra("senduid", String.valueOf(list.get(position).get("idx"))); //
                                    // }

                                    intent.putExtra("sendusertype", String.valueOf(list.get(position).get("usertype"))); //
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                view.getContext().startActivity(intent);
                            }
                    }
                }
            });
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(JSONObject item) throws JSONException, ParseException {

            //방 생성 후 학생 재추가 할 때 이미 추가된 리스트는 disabled 시킬 것.
            if(accesstype.equals("2")) {


                Log.d("----- 선택데이터갯수 -----",String.valueOf(selectuser.size()));
                if(selectuser_invite.size() > 0){
                    for(int i = 0; i<selectuser_invite.size();i++){
                        Log.d("----- 선택데이터!!!!!!!!!!!!!!! -----",String.valueOf(selectuser_invite.get(i)));

                        Log.d("----- 선택데이터 -----",String.valueOf(selectuser_invite.get(i).get("idx")));
                        Log.d("----- 모든데이터 -----",String.valueOf(item.get("idx")));
                        if(String.valueOf(selectuser_invite.get(i).get("idx")).equals(String.valueOf(item.get("idx")))){
                            Log.d("----- 모든데이터중 고름 -----",String.valueOf(item.get("idx")));

                            user_checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#f0f0f0")));//이미 초대된 상태의 유저는 클릭 안함
                            user_checkbox.setEnabled(false);
                        }
                    }
                }
            }


            //if(accesstype.equals("1")){
                //선택된 유저 리스트의 값과 전체 유저리스트의 값이 같으면 체크해준다.
                if(selectuser.size() > 0){
                    for(int i = 0; i<selectuser.size();i++){
                        if(String.valueOf(selectuser.get(i)).equals(String.valueOf(item))){
                            user_checkbox.setChecked(true);
                        }
                    }
                }
           // }
            nameinfo.setText(String.valueOf(item.get("name")));
            // usertypeinfo.setText(usertype_replace(String.valueOf(item.get("usertype"))));

            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.get("basicuri")+item.get("src");

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
