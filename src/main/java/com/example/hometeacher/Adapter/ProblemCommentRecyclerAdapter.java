package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

public class ProblemCommentRecyclerAdapter extends RecyclerView.Adapter<ProblemCommentRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<JSONObject> list;
    ArrayList<ArrayList<String>> Sessionlist;

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int type = 0; // 1. 댓글 2. 대댓글

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int type, int position, ArrayList<JSONObject> list) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private ProblemCommentRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public ProblemCommentRecyclerAdapter(Context context, Activity activity){
        oContext = context;
        oActivity = activity;
    }

    //type 1. 댓글 2. 대댓글
    public void setneeddata(int type){
        this.type = type;
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
//        if(type == 1){ //댓글
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentitem, parent, false);
//        }else{ //대댓글
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentnesteditem, parent, false);
//        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.problemcommentitem, parent, false);

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
        TextView documentinfo, writeinfo, timeinfo;
        RecyclerView CommentimgRecyclerView, CommentnestedRecyclerView;

        Button menubtn;
        ImageView mainprofileimg, commentimg;
        LinearLayout nboarditem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            documentinfo = (TextView) itemView.findViewById(R.id.documentinfo);
            writeinfo = (TextView) itemView.findViewById(R.id.writeinfo);
            timeinfo = (TextView) itemView.findViewById(R.id.timeinfo);

            nboarditem = (LinearLayout) itemView.findViewById(R.id.nboarditem);

            mainprofileimg = (ImageView) itemView.findViewById(R.id.mainprofileimg);
            menubtn = (Button) itemView.findViewById(R.id.menubtn);

            CommentnestedRecyclerView = (RecyclerView) itemView.findViewById(R.id.CommentnestedRecyclerView);

//            //댓글 이미지 크게보기
//            commentimg.setOnClickListener (new View.OnClickListener () {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//
//                        Uri imageUri_comment = null;
//                        try {
//                            imageUri_comment = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+list.get(position).get("basicuri")+list.get(position).get("src"));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        // Log.d("------profilemainimglist------------",String.valueOf(profilemainimglist));
//                        // Log.d("------profilemainimglist------------",String.valueOf(profilemainimglist.get(0)));
//                        Intent intent = new Intent(oActivity, ImgbigsizeActivity.class);
//                        intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(imageUri_comment));
//
//                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                oActivity,
//                                commentimg,
//                                Objects.requireNonNull(ViewCompat.getTransitionName(commentimg)));
//                        oActivity.startActivity(intent, options.toBundle());
//
//                    }
//                }
//            });


            //메뉴 버튼 클릭시 수정, 삭제
            menubtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            Log.d("", String.valueOf(position));
                            //type = 2 답글쓰기
                            try {
                                mListener.onItemClick(view, 3, position, list); //리스너 통해서 값을 activity로 전달
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

            if(!Sessionlist.isEmpty()) { //값이 있으면 = 회원 상태임
                //해당 댓글의 uid와 로그인 한 id가 같으면 띄울 것
                if (String.valueOf(Sessionlist.get(1).get(0)).equals(String.valueOf(item.get("uid")))) {
                    menubtn.setVisibility(View.VISIBLE);
                } else {
                    menubtn.setVisibility(View.GONE);
                }
            }else{
                menubtn.setVisibility(View.GONE);
            }

            writeinfo.setText(item.get("name") + " ("+usertype_replace(String.valueOf(item.get("usertype")))+")");
            documentinfo.setText(String.valueOf(item.get("document")));



            if(type == 1){ // 댓글리스트

                //commentnestedimg.setVisibility(View.VISIBLE);


//                //대댓글 이미지가 있을때만
//                if(!String.valueOf(item.get("commentnestedlist")).equals("null")){
//
//
//                    JSONArray jarray = null;
//
//                    try {
//                        jarray = new JSONArray(String.valueOf(item.get("commentnestedlist")));
//
//                        Log.d("onResponse ? ", "대댓글 정보들-------------- : " + String.valueOf(jarray.length()));
//                        ArrayList<JSONObject> commentnestedarraylist = new ArrayList<>(); // 조건에 맞는 이미지만 이중리사이클러뷰에 넣기
//                        for (int i = 0; i < jarray.length(); i++) {
//                            Log.d("onResponse ? ", "대댓글 정보들-------------- : " + String.valueOf(jarray.get(i)));
//
//                            JSONObject jobj = new JSONObject(String.valueOf(jarray.get(i)));
//
//                            //이중 리사이클러뷰
//                            commentnestedarraylist.add(jobj);
//                        }
//
//
//
//
//
//                       // Imagerecycle(commentnestedarraylist);
//                       // Log.d("onResponse ? ", "댓글 정보들 : all" + String.valueOf(Commentlist)); //info
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }



            }else{ //대댓글 리스트
                //commentnestedimg.setVisibility(View.GONE);
               // commentnestednum.setVisibility(View.GONE);
            }


                //프로필 이미지 변경
                Uri imageUri_profile = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+item.get("profilebasicuri")+item.get("profilesrc"));
                Picasso.get()
                        .load(imageUri_profile) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(mainprofileimg);


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
            timeinfo.setText(time+" "+timechar);



            //이미지가 null이 아닌 값만 적용 시킴
            if(!item.get("commentimg").equals("null")){
                JSONArray jarrayimg = new JSONArray(String.valueOf(item.get("commentimg")));
                //Log.d("adapter 게시글 리스트 이다.", "게시글 이미지 정보다"+String.valueOf(item.get("commentimg")));
                // Log.d("adapter 게시글 리스트 이다.", "게시글 이미지 정보다"+String.valueOf(jarrayimg));

                //이중 리사이클러뷰 생성
                ArrayList<Uri> commentinimgarr = new ArrayList<>(); // 조건에 맞는 이미지만 이중리사이클러뷰에 넣기
                for(int i = 0; i < jarrayimg.length(); i++){

                    JSONObject jimgobj = new JSONObject(String.valueOf(jarrayimg.get(i)));
                    //게시글의 idx와 img의 nid가 같은 이미지 정보만 모을것

                    //type에 맞게 변경함.
                    if(type == 1) { //댓글
                        if(jimgobj.get("cid").equals(String.valueOf(item.get("idx")))){
                            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jimgobj.get("basicuri")+jimgobj.get("src");
                            Uri imageUri = Uri.parse(imagestring);
                            commentinimgarr.add(imageUri);
                        }
                    }else{
                        if(jimgobj.get("cnid").equals(String.valueOf(item.get("idx")))){
                            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+jimgobj.get("basicuri")+jimgobj.get("src");
                            Uri imageUri = Uri.parse(imagestring);
                            commentinimgarr.add(imageUri);
                        }
                    }
                }
                //Log.d("adapter 게시글 리스트 이다.", "게시글 이미지리스트 array"+String.valueOf(nboardinimgarr));

                CommentimgRecyclerView = (RecyclerView) itemView.findViewById(R.id.CommentimgRecyclerView);
                Imagerecycle(commentinimgarr);


            }
        }

        //이미지를 다중 리사이클러뷰로 생성
        public void Imagerecycle(ArrayList<Uri> commentnestedarraylist){
            LinearLayoutManager linearManager = new LinearLayoutManager(oContext, RecyclerView.HORIZONTAL, false); //가로일때
            CommentimgRecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식
            ImgViewAdapter ImgViewAdapter = new ImgViewAdapter(oContext, oActivity); //내가만든 어댑터 선언
            ImgViewAdapter.setRecycleList(commentnestedarraylist); //arraylist 연결
            CommentimgRecyclerView.setAdapter(ImgViewAdapter); //리사이클러뷰 위치에 어답터 세팅

            //리스너를 통해서 recyclerview에서 클릭한 이벤트 정보 값을 activity에서 받음.
            ImgViewAdapter.setOnItemClickListener(new ImgViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int picturepos, Uri imguri, ImageView sharedImageView) {

                }
            }) ;
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
