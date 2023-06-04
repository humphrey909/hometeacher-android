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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MyclassChatUserlistRecyclerAdapter extends RecyclerView.Adapter<MyclassChatUserlistRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;


    ArrayList<JSONObject> list;
    ArrayList<JSONObject> selectuser;
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
    private MyclassChatUserlistRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public MyclassChatUserlistRecyclerAdapter(Context context){
        oContext = context;
    }

    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<JSONObject> list){
        this.list = list;
    }
    public void setSelectUser(ArrayList<JSONObject> selectuser, int superuser){
        this.selectuser = selectuser;
        this.superuser = superuser;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myclasschat_useritem, parent, false);

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
        ImageView imgView_item;
        LinearLayout Linearclickbtn;
        Button menubtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Linearclickbtn = (LinearLayout) itemView.findViewById(R.id.Linearclickbtn);
            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
            usertype = (TextView) itemView.findViewById(R.id.usertype);
            invitechk = (TextView) itemView.findViewById(R.id.invitechk);
            menubtn = (Button) itemView.findViewById(R.id.menubtn);

            Linearclickbtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        Intent intent = new Intent(view.getContext(), Profileview.class);
                        try {
                            intent.putExtra("sendtype", "2"); //다른 유저 idx
                            intent.putExtra("senduid", String.valueOf(list.get(position).get("uid")));
                            intent.putExtra("sendusertype", String.valueOf(list.get(position).get("usertype")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        view.getContext().startActivity(intent);
                    }
                }
            });


            //메뉴버튼 -- 방장만 컨트롤 할 수 있게 할 것.
            menubtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            try {
                                mListener.onItemClick(view, position, list); //리스너 통해서 값을 activity로 전달
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(JSONObject item) throws JSONException, ParseException {


            //내가 방장이라면 안보이게 처리 //자기 자신은 안보이게 처리
            if(superuser == 1){ //방장
                if(String.valueOf(item.get("uid")).equals(Sessionlist.get(1).get(0))){ //내 아이디일때
                    menubtn.setVisibility(View.GONE);
                }else{ //다른 사람 아이디 일때
                    if(String.valueOf(item.get("invitechk")).equals("3")){ //거절이면
                        menubtn.setVisibility(View.VISIBLE);
                    }else{
                        menubtn.setVisibility(View.VISIBLE); //초대중, 참여완료
                    }
                }
            }else{ //참여자
                menubtn.setVisibility(View.GONE);
            }


            nameinfo.setText(String.valueOf(item.get("name")));
            usertype.setText(usertype_replace(String.valueOf(item.get("usertype"))));
            invitechk.setText(invitechk_replace(String.valueOf(item.get("invitechk"))));





            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.get("profilebasicuri")+item.get("profilesrc");

            Uri imageUri = Uri.parse(imagestring);
            Picasso.get()
                    .load(imageUri) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(imgView_item);

        }


        //초대 여부 체크 문자열로 변환
        public String invitechk_replace(String invitechk){
            if(invitechk.equals("1")){ //초대중
                return "초대됨";
            }else if(invitechk.equals("2")){ //초대완료
                return "";
            }else{ //거절
               return "거절";
            }
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
