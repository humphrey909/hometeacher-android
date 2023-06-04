package com.example.hometeacher.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyclassSelectImgAdapter extends RecyclerView.Adapter<MyclassSelectImgAdapter.ViewHolder> {

    Context oContext;
    Activity oActivity;

    ArrayList<JSONObject> userlist;

    //1. 방만들기 페이능 , 2. 유저 추가 페이지
    int type = 0;

    //각 실행을 하고 마지막에 연결된 메소드 오버라이딩할때 return해주는 값

    public MyclassSelectImgAdapter(Context context, Activity activity){
        oContext = context;
        oActivity = activity;
    }

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int picturepos, ArrayList<JSONObject> imgidx) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void setdata(int type){
        this.type = type;
    }
    public void setRecycleList(ArrayList<JSONObject> list){
        this.userlist = list;

        Log.d("this.userlist", String.valueOf(userlist));
    }


    @NonNull
    @Override
    public MyclassSelectImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(type == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myclassselectuser_item_view, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myclassselectuser_item, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyclassSelectImgAdapter.ViewHolder holder, int position) {
        try {
            holder.onBind(userlist.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        //if(this.userlist == null) {
        //    return 1;
        //}else{
            return this.userlist.size();
       // }
    }




    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView_item;
        ImageView deletebtn;
        TextView nameedit;

        //ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           // itemView.setOnCreateContextMenuListener(this);

            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            deletebtn = (ImageView) itemView.findViewById(R.id.deletebtn);
            nameedit = (TextView) itemView.findViewById(R.id.nameedit);
            //리스트 중 아이템 하나 클릭시 작동


            if(type == 2) {
                //삭제 버튼 클릭시
                deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onItemClick(view, position, userlist); //리스너 통해서 값을 activity로 전달
                            }

                        }
                    }
                });
            }
        }

        void onBind(JSONObject item) throws JSONException {
                //Log.d("onResponse ? ", "onResponse 프로필 이미지 서버 데이터만 : " + String.valueOf(item.getUri()));
            nameedit.setText(String.valueOf(item.get("name")));

            String imagestring = RetrofitService.MOCK_SERVER_FIRSTURL+item.get("basicuri")+item.get("src");

            Uri imageUri = Uri.parse(imagestring);
            Picasso.get()
                    .load(imageUri) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(imgView_item);

        }
    }
}
