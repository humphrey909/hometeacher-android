package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class AlertRecyclerAdapter extends RecyclerView.Adapter<AlertRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<JSONObject> list;
    ArrayList<ArrayList<String>> Sessionlist;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private AlertRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public AlertRecyclerAdapter(Context context, Activity activity){
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
    //public void setlisttype(int type){
    //    this.listType = type;
    //}

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alertitem, parent, false);
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
        TextView document, regdate;

        //RecyclerView CommentimgRecyclerView, CommentnestedRecyclerView;
//
//        Button menubtn;
//        ImageView mainprofileimg, commentimg, commentnestedimg, likeimg;
        LinearLayout totalLinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            document = (TextView) itemView.findViewById(R.id.document);
            regdate = (TextView) itemView.findViewById(R.id.regdate);
            totalLinear = (LinearLayout) itemView.findViewById(R.id.totalLinear);



//            timeinfo = (TextView) itemView.findViewById(R.id.timeinfo);
//            likenum = (TextView) itemView.findViewById(R.id.likenum);
//            commentnestedimg = (ImageView) itemView.findViewById(R.id.commentnestedimg);
//            likeimg = (ImageView) itemView.findViewById(R.id.likeimg);
//            commentnestednum = (TextView) itemView.findViewById(R.id.commentnestednum);
//
//
//
//            nboarditem = (LinearLayout) itemView.findViewById(R.id.nboarditem);
//
//            mainprofileimg = (ImageView) itemView.findViewById(R.id.mainprofileimg);
//            menubtn = (Button) itemView.findViewById(R.id.menubtn);
//
//            CommentnestedRecyclerView = (RecyclerView) itemView.findViewById(R.id.CommentnestedRecyclerView);


                //알림 클릭시
                totalLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            if (mListener != null) {
                                Log.d("", String.valueOf(position));
                                //type = 2 답글쓰기
                                try {
                                    mListener.onItemClick(view, 0, position, list); //리스너 통해서 값을 activity로 전달
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

            String click = String.valueOf(item.get("click")); //등록 시간

            //클릭 전 색 표시
            if(click.equals("1")){
                //totalLinear.setTextColor(Color.parseColor("#FAFAFA"));
                totalLinear.setBackground(ContextCompat.getDrawable(oContext, R.color.greybtn3));
            }else{ //클릭 후 색 삭제
                //totalLinear.setTextColor(Color.parseColor("#FAFAFA"));
                totalLinear.setBackground(ContextCompat.getDrawable(oContext, R.color.white));
            }



            // 현재시간에서 지난 시간을 jsonobject를 추가한다.
            String currenttime = Makecurrenttime();//현재시간 불러오기
            String regtime = String.valueOf(item.get("regdate")); //등록 시간
            //Log.d("onResponse ? ", "게시글 현재시간 ------------: " + String.valueOf(currenttime));
            //Log.d("onResponse ? ", "게시글 시간 ------------: " + String.valueOf(regtime));



            Date dt1 = timeFormat.parse(currenttime); //현재시간
            Date dt2 = timeFormat.parse(regtime); //등록시간

            long diff = dt1.getTime() - dt2.getTime();
            long diffsec = diff / 1000; // 초 구하기
            long diffmin = diff / (60000); //분 구하기
            long diffhour = diff /  (3600000); // 시간 구하기
            long diffdays = diffsec /  (24*60*60); //일 구하기

            //  int nboardpagenum = (pagenum * limitnum) + i; //페이징에 맞게 저장할 부분의 순서를 조정해줌

            String time = null;
            String timechar = null;
            //맨뒤에 추가해서 넣어줄것
            // 60초까지는 몇초전으로 진행
            // 60초가 넘어가면 1분 전으로 진행
            // 60분이 넘어가면 1시간 전으로 진행
            // 24시간이 넘어가면 1day전으로 진행
            if(diffsec < 60){
                //Nboardlist.get(nboardpagenum).put("time", diffsec);
                //Nboardlist.get(nboardpagenum).put("timechar", "초 전");

                if(diffsec<5){
                    time = "";
                    timechar = "방금 전";
                }else{
                    time = String.valueOf(diffsec);
                    timechar = "초 전";
                }

            }else{ //60초 넘으면
                if(diffmin < 60){
                    //Nboardlist.get(nboardpagenum).put("time", diffmin);
                    //Nboardlist.get(nboardpagenum).put("timechar", "분 전");
                    time = String.valueOf(diffmin);
                    timechar = "분 전";
                }else{ //60분 넘으면
                    if(diffhour < 24){
                        //Nboardlist.get(nboardpagenum).put("time", diffhour);
                        //Nboardlist.get(nboardpagenum).put("timechar", "시간 전");
                        time = String.valueOf(diffhour);
                        timechar = "시간 전";
                    }else{ //12시간이 넘으면
                        // Nboardlist.get(nboardpagenum).put("time", diffdays);
                        // Nboardlist.get(nboardpagenum).put("timechar", "일 전");
                        time = String.valueOf(diffdays);
                        timechar = "일 전";
                    }
                }
            }
            regdate.setText(time+" "+timechar);

            document.setText(String.valueOf(item.get("alertdocu")));
            //regdate.setText(String.valueOf(item.get("regdate")));


//            if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
//                //해당 댓글의 uid와 로그인 한 id가 같으면 띄울 것
//                if (String.valueOf(Sessionlist.get(1).get(0)).equals(String.valueOf(item.get("uid")))) {
//                    menubtn.setVisibility(View.VISIBLE);
//                } else {
//                    menubtn.setVisibility(View.GONE);
//                }
//            }else{
//                menubtn.setVisibility(View.GONE);
//            }
//
//            writeinfo.setText(item.get("name") + " ("+usertype_replace(String.valueOf(item.get("usertype")))+")");
//            documentinfo.setText(String.valueOf(item.get("document")));
//
//
//            //내가 체크한 좋아요 출력
//            if(item.get("commentlikechk").equals("true")){
//                likeimg.setImageResource(R.drawable.like_chk);
//            }else{ //false
//                likeimg.setImageResource(R.drawable.like2);
//            }
//
//            //댓글의 좋아요 갯수 출력
//            likenum.setText(String.valueOf(item.get("commentliketotalnum")));
        }
    }





    //유저타입 문자열로 변환
    public String usertype_replace(String subject){
        if(subject.equals("1")){
            return "선생님";
        }else if(subject.equals("2")){
            return "학생";
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

    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}
