package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;

    public static String EXTRA_ANIMAL_ITEM = "imguri";

    ArrayList<JSONObject> list;
    ArrayList<ArrayList<String>> Sessionlist;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //int listType = 0; // 1 : 성격리스트,  2: 과목리스트

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<JSONObject> list) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private ReviewRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public ReviewRecyclerAdapter(Context context, Activity activity){
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profilereviewitem, parent, false);

        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.onBind(list.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar professional_star, lecturepower_star, lectureready_star, lectureontime_star;
        TextView documentinfo, writername, paymentinfo, startdateinfo, regdateinfo;
        RecyclerView NboardimgRecyclerView;

        ImageView mainprofileimg;
        LinearLayout nboarditem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            professional_star = (RatingBar) itemView.findViewById(R.id.professional_star);
            lecturepower_star = (RatingBar) itemView.findViewById(R.id.lecturepower_star);
            lectureready_star = (RatingBar) itemView.findViewById(R.id.lectureready_star);
            lectureontime_star = (RatingBar) itemView.findViewById(R.id.lectureontime_star);
            documentinfo = (TextView) itemView.findViewById(R.id.documentinfo);
            mainprofileimg = (ImageView) itemView.findViewById(R.id.mainprofileimg);
            writername = (TextView) itemView.findViewById(R.id.writername); //작성자 이름


            paymentinfo = (TextView) itemView.findViewById(R.id.paymentinfo);
            startdateinfo = (TextView) itemView.findViewById(R.id.startdateinfo);
            regdateinfo = (TextView) itemView.findViewById(R.id.regdateinfo);

//            commentnum = (TextView) itemView.findViewById(R.id.commentnum);
//            NboardimgRecyclerView = (RecyclerView) itemView.findViewById(R.id.NboardimgRecyclerView);
//
//            nboarditem = (LinearLayout) itemView.findViewById(R.id.nboarditem);
//
//            nboarditem.setOnClickListener (new View.OnClickListener () {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition ();
//                    if (position!=RecyclerView.NO_POSITION){
//
//                        if (mListener != null) {
//                            mListener.onItemClick(view, position, list) ; //리스너 통해서 값을 activity로 전달
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
        void onBind(JSONObject item) throws JSONException, ParseException {

            writername.setText(String.valueOf(item.get("name")));
            documentinfo.setText(String.valueOf(item.get("reviewtext")));
            professional_star.setRating(Float.parseFloat(String.valueOf(item.get("professional"))));
            lecturepower_star.setRating(Float.parseFloat(String.valueOf(item.get("lecturepower"))));
            lectureready_star.setRating(Float.parseFloat(String.valueOf(item.get("lectureready"))));
            lectureontime_star.setRating(Float.parseFloat(String.valueOf(item.get("lectureontime"))));

            paymentinfo.setText(String.valueOf("월 수업료 : "+item.get("payment")));

            //classstartdate //수업 시작 날짜
            String classstartdatestr = String.valueOf(item.get("classstartdate")); //2022-08-09 05:53:17
            String[] classstartdatestr_split = classstartdatestr.split(" ");
            String[] classstartdatestr_date = classstartdatestr_split[0].split("-");
            String classstartdatestrdatemake = classstartdatestr_date[0]+"년 "+classstartdatestr_date[1]+"월 "+classstartdatestr_date[2]+"일 ";
            startdateinfo.setText(String.valueOf("수업 시작 : "+classstartdatestrdatemake));


            //리뷰 작성일
            String regdatestr = String.valueOf(item.get("regdate")); //2022-08-09 05:53:17
            String[] regdatestr_split = regdatestr.split(" ");
            String[] regdatestr_date = regdatestr_split[0].split("-");
            String regdatestr_datedatemake = regdatestr_date[0]+"년 "+regdatestr_date[1]+"월 "+regdatestr_date[2]+"일 ";
            regdateinfo.setText(regdatestr_datedatemake);


            //프로필 이미지 변경
            Uri imageUri_profile = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+item.get("basicuri")+item.get("src"));
            Picasso.get()
                    .load(imageUri_profile) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(mainprofileimg);




//
//
//            //날짜 시간을 변형한다.
//            String dateStr = String.valueOf(item.get("regdate")); //2022-08-09 05:53:17
//            String[] dateStr_split = dateStr.split(" ");
//            String[] dateStr_date = dateStr_split[0].split("-");
//            String[] dateStr_time = dateStr_split[1].split(":");
//            System.out.println("second : " + String.valueOf(dateStr_date[0]));
//            System.out.println("second : " + String.valueOf(dateStr_time[0]));
//
//            String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";
//
//            String ampm = "";
//            if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
//                ampm = "오후";
//            }else{ //오전
//                ampm = "오전";
//            }
//            String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
//            timeinfo.setText(datemake+timemake);
//
//
//
//            writeinfo.setText(" · "+String.valueOf(item.get("name"))+" · ");
//            commentnum.setText("댓글 "+ String.valueOf(item.get("commenttotalnum")));
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
