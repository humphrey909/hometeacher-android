package com.example.hometeacher.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ImgViewPagerActivity;
import com.example.hometeacher.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImgViewAdapter extends RecyclerView.Adapter<ImgViewAdapter.ViewHolder> {

    Context oContext;

    ArrayList<Uri> oImgbox;
    //각 실행을 하고 마지막에 연결된 메소드 오버라이딩할때 return해주는 값


    //카메라 열고 임시 경로
    private String mCurrentPhotoPath;
    //저장시 파일 이름
    private String SavePicturePath; //경로
    private String SavePictureName; //이름



    Activity oActivity;

    public ImgViewAdapter(Context context , Activity activity){
        oContext = context;
        oActivity = activity;
    }

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int picturepos, Uri imguri, ImageView ivProfile) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }



    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<Uri> list){
        this.oImgbox = list;

        Log.d("this.oImgbox", String.valueOf(oImgbox));

    }


    @NonNull
    @Override
    public ImgViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewimg_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgViewAdapter.ViewHolder holder, int position) {
        //if(this.oImgbox != null) {
            holder.onBind(oImgbox.get(position));
       // }
    }

    @Override
    public int getItemCount() {
        //if(this.oImgbox == null) {
        //    return 1;
        //}else{
            return this.oImgbox.size();
       // }
    }




    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView_item;
        TextView paypriceview;

        //ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           // itemView.setOnCreateContextMenuListener(this);

            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);

            //리스트 중 아이템 하나 클릭시 작동
            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){


                        Intent intent = new Intent(oContext, ImgViewPagerActivity.class);
                        intent.putExtra("sendimg",  String.valueOf(oImgbox));
                        intent.putExtra("position",  position);
                        oActivity.startActivity(intent);

//                        if (mListener != null) {
//
//                            //Profileview activity에서 클릭시 작동
//                            mListener.onItemClick(view, position, oImgbox.get(position), imgView_item) ; //리스너 통해서 값을 activity로 전달
//                        }
                    }
                }
            });
        }

        void onBind(Uri item){

//            CircleTransform transForm = new CircleTransform();
//            Picasso.get()
//                    .load(item)
//                    .transform(transForm)
//                    .into(imgView_item);
           
            Picasso.get()
                        .load(item) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(imgView_item);
        }
    }
}
