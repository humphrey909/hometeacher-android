package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class RequestClassRecyclerAdapter extends RecyclerView.Adapter<RequestClassRecyclerAdapter.ViewHolder> {
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
    private RequestClassRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public RequestClassRecyclerAdapter(Context context, Activity activity){
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestclassitem, parent, false);

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
        TextView nameinfo, profileinfo, chatdocument, noreadcount;
        RecyclerView NboardimgRecyclerView;
        ImageView imgView_item;
        LinearLayout requestclasslinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
            profileinfo = (TextView) itemView.findViewById(R.id.profileinfo);
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
            //Log.d("adapter 게시글 리스트 이다.", "게시글 리스트"+String.valueOf(item.get("viewuserprofile")));

            //프로필 정보 입력
            JSONArray jarrayprofile = new JSONArray(String.valueOf(item.get("viewuserprofile")));
            JSONObject jprofileobj = new JSONObject(String.valueOf(jarrayprofile.get(0)));
            Log.d("adapter 과외문의 리스트 이다.", "프로필 정보 --- "+String.valueOf(jprofileobj));
            if(item.get("viewusertype").equals("1")){ //선생님
                Log.d("adapter 과외문의 리스트 이다.", "프로필 정보 --- "+String.valueOf(jprofileobj.get("idx")));

                profileinfo.setText(jprofileobj.get("university")+" "+jprofileobj.get("universmajor")+" "+jprofileobj.get("studentid")+" 학번");
            }else{ //학생
                profileinfo.setText(studentage_replace(String.valueOf(jprofileobj.get("studentages")))+" 학생");
            }
            //nameinfo.setText(String.valueOf(item.get("viewusername"))+" : "+item.get("noreadchk"));
            //nameinfo.setText(String.valueOf(item.get("viewusername"))+" : "+item.get("idx"));
            nameinfo.setText(String.valueOf(item.get("viewusername")));


            //item.get("totalchatcount")  이 값은 채팅의 갯수를 나타낸다.



            //이미지가 null이 아닌 값만 적용 시킴
            if(!item.get("viewuserimg").equals("null")){
                JSONArray jarrayimg = new JSONArray(String.valueOf(item.get("viewuserimg")));
                JSONObject jimgobj = new JSONObject(String.valueOf(jarrayimg.get(0)));

                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jimgobj.get("basicuri")+jimgobj.get("src");
                Uri imageUri = Uri.parse(imagestring);

                Picasso.get()
                        .load(imageUri) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(imgView_item);
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
                        if(String.valueOf(item.get("currentmsg")).length() >= 18) {
                            String cutchar = String.valueOf(item.get("currentmsg")).substring(0, 18) + "...";
                            chatdocument.setText(cutchar);
                        }else{
                            chatdocument.setText(String.valueOf(item.get("currentmsg")));
                        }
                    }

                }

            }else{
                chatdocument.setText("");
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
}
