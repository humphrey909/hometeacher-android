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

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;

   // AccountBook oAccountBook;
   // UseKindsList oUseKindsList;

    ArrayList<JSONObject> list;
    ArrayList<ArrayList<String>> Sessionlist;

    int divisionlikelistchk; //찜한 목록 체크시 1로 표시 하여 보내줌.  - 찜한목록은 리스트가 다르기때문에 분류해줌.

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, String pidx) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private UserRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public UserRecyclerAdapter(Context context){
        oContext = context;
       // oAccountBook = new AccountBook(oContext);
      //  oUseKindsList = new UseKindsList(oContext);
    }
    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist, int divisionlikelistchk) {
        this.Sessionlist = Sessionlist;
        this.divisionlikelistchk = divisionlikelistchk;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersearchitem, parent, false);

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
        }
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameinfo, schoolinfo, subjectinfo;
        ImageView imgView_item;
        LinearLayout profileitemLinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameinfo = (TextView) itemView.findViewById(R.id.nameinfo);
            schoolinfo = (TextView) itemView.findViewById(R.id.schoolinfo);
            subjectinfo = (TextView) itemView.findViewById(R.id.subjectinfo);
            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            profileitemLinear = (LinearLayout) itemView.findViewById(R.id.profileitemLinear);

            //리스트 중 아이템 하나 클릭시 작동 - 이미지 클릭시
            imgView_item.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    //Selectlist.add(position, );

                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴

                        if(Sessionlist.isEmpty()){ //빈값 = 로그아웃상태
                            Intent intent = new Intent(view.getContext(), Loginpage.class);
                            view.getContext().startActivity(intent);
                        }else{
                            Intent intent = new Intent(view.getContext(), Profileview.class);

                            Log.d("----divisionlikelistchk-----",String.valueOf(divisionlikelistchk));
                            try {
                                intent.putExtra("sendtype", "2"); //다른 유저 idx
                                if(divisionlikelistchk == 1){
                                    intent.putExtra("senduid", String.valueOf(list.get(position).get("uid"))); //
                                }else{
                                    intent.putExtra("senduid", String.valueOf(list.get(position).get("idx"))); //
                                }

                                intent.putExtra("sendusertype", String.valueOf(list.get(position).get("usertype"))); //
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            view.getContext().startActivity(intent);
                        }
                       // if (mListener != null) {
                        //    mListener.onItemClick(view, position) ; //리스너 통해서 값을 activity로 전달
                       // }
                    }
                }
            });

            //아이템 클릭시 이동
            profileitemLinear.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    //Selectlist.add(position, );

                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴

                        if(Sessionlist.isEmpty()){ //빈값 = 로그아웃상태
                            Intent intent = new Intent(view.getContext(), Loginpage.class);
                            view.getContext().startActivity(intent);
                        }else{
                            Intent intent = new Intent(view.getContext(), Profileview.class);
                            try {
                                intent.putExtra("sendtype", "2"); //다른 유저 idx
                                if(divisionlikelistchk == 1){
                                    intent.putExtra("senduid", String.valueOf(list.get(position).get("uid"))); //
                                }else{
                                    intent.putExtra("senduid", String.valueOf(list.get(position).get("idx"))); //
                                }
                                intent.putExtra("sendusertype", String.valueOf(list.get(position).get("usertype"))); //
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            view.getContext().startActivity(intent);
                        }



//                         if (mListener != null) {
//                             try {
//                                 mListener.onItemClick(view, position, String.valueOf(list.get(position).get("idx"))) ; //리스너 통해서 값을 activity로 전달
//                             } catch (JSONException e) {
//                                 e.printStackTrace();
//                             }
//                         }
                    }
                }
            });

        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(JSONObject item) throws JSONException {


            if(item.get("usertype").equals("1")){ //선생
                nameinfo.setText(String.valueOf(item.get("name")));
                schoolinfo.setText(item.get("university")+" "+item.get("universmajor")+" "+item.get("studentid")+"학번");
            }else{ //학생
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
    }
}
