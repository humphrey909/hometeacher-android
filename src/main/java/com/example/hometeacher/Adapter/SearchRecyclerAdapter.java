package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;


    ArrayList<JSONObject> list;
    ArrayList<ArrayList<String>> Sessionlist;

    int type = 0;
    String searchtext = "";

    View view;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<JSONObject> list) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private SearchRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public SearchRecyclerAdapter(Context context){
        oContext = context;
    }

    //type 0. 과외찾기, 1. 게시판, 2. 사람
    public void needdata(int type, String searchtext){
        this.type = type;
        this.searchtext = searchtext;
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

        if(type == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailsearchmode_classsearchitem, parent, false);
        }else if(type == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailsearchmode_nboarditem, parent, false);
        }else if(type == 2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailsearchmode_useritem, parent, false);
        }



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

        TextView subcateinfo, titleinfo, documentinfo, writeinfo, timeinfo, likenum, usertypeinfo, commentnum;
       // RecyclerView NboardimgRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Linearclickbtn = (LinearLayout) itemView.findViewById(R.id.Linearclickbtn);
            if(type == 0){
                nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
                schoolinfo = (TextView) itemView.findViewById(R.id.schoolinfo);
                subjectinfo = (TextView) itemView.findViewById(R.id.subjectinfo);
                imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
                profileitemLinear = (LinearLayout) itemView.findViewById(R.id.profileitemLinear);
                searchword = (TextView) itemView.findViewById(R.id.searchword);
            }else if(type == 1){
                subcateinfo = (TextView) itemView.findViewById(R.id.subcateinfo);
                titleinfo = (TextView) itemView.findViewById(R.id.titleinfo);
                documentinfo = (TextView) itemView.findViewById(R.id.documentinfo);
                writeinfo = (TextView) itemView.findViewById(R.id.writeinfo);
                timeinfo = (TextView) itemView.findViewById(R.id.timeinfo);
                likenum = (TextView) itemView.findViewById(R.id.likenum);
                commentnum = (TextView) itemView.findViewById(R.id.commentnum);
            }else if(type == 2){
                imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
                nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
                usertypeinfo = (TextView) itemView.findViewById(R.id.usertypeinfo);
            }

            Linearclickbtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {


                        if(type == 0) {
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
                        }else if(type == 1){
                            if (mListener != null) {
                                mListener.onItemClick(view, position, list) ; //리스너 통해서 값을 activity로 전달
                            }
                        }else if(type == 2){


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
                }
            });
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(JSONObject item) throws JSONException, ParseException {

            if(type == 0){ //과외찾기 리스트

                if(item.get("usertype").equals("1")){ //선생
                    //검색어 단어 위치 찾기.
                    int wordlocal = String.valueOf(item.get("subjectdocument")).indexOf(searchtext);
                    if(wordlocal != -1) { //찾는 값이 있을때

                        SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("subjectdocument")));
                        spannable.setSpan(
                                new StyleSpan(Typeface.BOLD),
                                wordlocal, wordlocal + searchtext.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        searchword.setText(spannable); //검색한부분 보여주기
                    }else{ //없을때
                        searchword.setText(String.valueOf(item.get("subjectdocument"))); //검색한부분 보여주기
                    }

                    //searchword.setText(String.valueOf(item.get("subjectdocument"))); //검색한부분 보여주기
                    nameinfo.setText(String.valueOf(item.get("name")));
                    schoolinfo.setText(item.get("university")+" "+item.get("universmajor")+" "+item.get("studentid")+"학번");
                }else{ //학생

                    //검색어 단어 위치 찾기.
                    int wordlocal = String.valueOf(item.get("introduce")).indexOf(searchtext);
                   // Log.d("------wordlocal------", String.valueOf(searchtext));
                   // Log.d("------wordlocal------", String.valueOf(item.get("introduce")));
                   // Log.d("------wordlocal------", String.valueOf(wordlocal));
                    if(wordlocal != -1) { //찾는 값이 있을때
                        SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("introduce")));
                        spannable.setSpan(
                                new StyleSpan(Typeface.BOLD),
                                wordlocal, wordlocal+searchtext.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        searchword.setText(spannable); //검색한부분 보여주기
                    }else{ //없을때
                        searchword.setText(String.valueOf(item.get("introduce"))); //검색한부분 보여주기
                    }

                    nameinfo.setText(String.valueOf(item.get("name")));
                    String studentagechar = studentage_replace(String.valueOf(item.get("studentages")));//학생 학년 문자열로 변환
                    String genderchar = gender_replace(String.valueOf(item.get("gender")));
                    schoolinfo.setText(studentagechar+" "+genderchar);
                }


                //과목 변환
                String subjectsub = String.valueOf(item.get("majorsubject")).substring(0,String.valueOf(item.get("majorsubject")).length()-1); //마지막 문자 삭제
                String subjectsub2 = subjectsub.substring(1); //첫번째 문자 삭제
                String subjectsub3 = subjectsub2.replaceAll(" ", ""); //모든 공백 제거
                String[] subjectarr = subjectsub3.split(",");

                String Subjectcomplete = "";
                for(int i=0;i<subjectarr.length;i++){
                    Subjectcomplete += subject_replace(subjectarr[i])+",";
                }
                String Subjectcomplete_ = Subjectcomplete.substring(0,Subjectcomplete.length()-1); //마지막 문자 삭제

                subjectinfo.setText(Subjectcomplete_);
               // Log.d("adapter 유저 리스트 이다.", String.valueOf(item));
              //  Log.d("adapter 유저 리스트 이다.", String.valueOf(item.get("basicuri")));
               // Log.d("adapter 유저 리스트 이다.", String.valueOf(item.get("src")));

                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.get("basicuri")+item.get("src");

                Uri imageUri = Uri.parse(imagestring);
                Picasso.get()
                        .load(imageUri) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(imgView_item);

            }else if(type == 1){ //게시판 리스트

                //검색어 단어 위치 찾기.
//                int wordlocal = String.valueOf(item.get("introduce")).indexOf(searchtext);
//                // Log.d("------wordlocal------", String.valueOf(searchtext));
//                // Log.d("------wordlocal------", String.valueOf(item.get("introduce")));
//                // Log.d("------wordlocal------", String.valueOf(wordlocal));
//
//                SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("introduce")));
//                spannable.setSpan(
//                        new StyleSpan(Typeface.BOLD),
//                        wordlocal, wordlocal+searchtext.length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                );

                int wordlocal_title = String.valueOf(item.get("title")).indexOf(searchtext);
                Log.d("------wordlocal------", String.valueOf(searchtext));
                Log.d("------wordlocal------", String.valueOf(item.get("title")));
                Log.d("------wordlocal------", String.valueOf(wordlocal_title));

                if(wordlocal_title != -1){ //찾는 값이 있을때
                    SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("title")));
                    spannable.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            wordlocal_title, wordlocal_title+searchtext.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    titleinfo.setText(spannable);
                }else{ //찾는 값이 없을때
                    titleinfo.setText(String.valueOf(item.get("title")));
                }



                writeinfo.setText(String.valueOf(item.get("name")));
                if (item.get("maincategorey").equals("0")) { //멘토링
                    //item.get("subcategorey")
                    String subcatechar = mentoring_replace(String.valueOf(item.get("subcategorey")));
                    subcateinfo.setText(subcatechar);
                }else{//선생님 토론
                    String subcatechar = teacherdedate_replace(String.valueOf(item.get("subcategorey")));
                    subcateinfo.setText(subcatechar);
                }



                int wordlocal_document = String.valueOf(item.get("document")).indexOf(searchtext);
                if(wordlocal_document != -1){ //찾는 값이 있을때
                    SpannableStringBuilder spannable = new SpannableStringBuilder(String.valueOf(item.get("document")));
                    spannable.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            wordlocal_document, wordlocal_document+searchtext.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                  //  Log.d("-----------spannable.length()------------", String.valueOf(String.valueOf(item.get("document")).length()));
                   // Log.d("-----------spannable.length()------------", String.valueOf(spannable.length()));

                    if(spannable.length() >= 100){
                        String cutchar = String.valueOf(spannable).substring(0, 100)+"...";
                        documentinfo.setText(cutchar);

                        Log.d("-----------cut document------------", String.valueOf(cutchar));

                    }else{
                        documentinfo.setText(spannable);
                    }
                }else{ //없을때
                    if(String.valueOf(item.get("document")).length() >= 100){
                        String cutchar = String.valueOf(item.get("document")).substring(0, 100)+"...";
                        documentinfo.setText(cutchar);

                        Log.d("-----------cut document------------", String.valueOf(cutchar));

                    }else{
                        documentinfo.setText(String.valueOf(item.get("document")));
                    }
                }

//                if(String.valueOf(item.get("document")).length() >= 100){
//                    String cutchar = String.valueOf(item.get("document")).substring(0, 100)+"...";
//                    documentinfo.setText(cutchar);
//
//                    Log.d("-----------cut document------------", String.valueOf(cutchar));
//
//                }else{
//                    documentinfo.setText(String.valueOf(item.get("document")));
//                }


                // 현재시간에서 지난 시간을 jsonobject를 추가한다.
                String currenttime = Makecurrenttime();//현재시간 불러오기
                String regtime = String.valueOf(item.get("regdate")); //등록 시간
                Log.d("onResponse ? ", "게시글 현재시간 ------------: " + String.valueOf(currenttime));

                Log.d("onResponse ? ", "게시글 시간 ------------: " + String.valueOf(regtime));



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

                    time = String.valueOf(diffsec);
                    timechar = "초 전";
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
                timeinfo.setText(time+" "+timechar);


                commentnum.setText(String.valueOf(item.get("commenttotalnum")));
                if(!item.get("liketotalnum").equals("null")){
                    likenum.setText(String.valueOf(item.get("liketotalnum")));
                    //Log.d("adapter 게시글 리스트 이다.", "게시글 좋아요 수다"+String.valueOf(item.get("liketotalnum")));
                }else{
                    likenum.setText("0");
                }

            }else if(type == 2) { //유저 닉네임 검색 리스트
                nameinfo.setText(String.valueOf(item.get("name")) + " #" + String.valueOf(item.get("nicname")));
                usertypeinfo.setText(usertype_replace(String.valueOf(item.get("usertype"))));

                String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.get("basicuri")+item.get("src");

                Uri imageUri = Uri.parse(imagestring);
                Picasso.get()
                        .load(imageUri) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(imgView_item);
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
