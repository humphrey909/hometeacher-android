package com.example.hometeacher.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.ImgForm;
import com.example.hometeacher.ArraylistForm.ImgFormMulti;
import com.example.hometeacher.ImgbigsizeActivity;
import com.example.hometeacher.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ImgAdapterMulti extends RecyclerView.Adapter<ImgAdapterMulti.ViewHolder> {

    Context oContext;
    Activity oActivity;

    ArrayList<ImgFormMulti> oImgbox;
    int type;
    public static String EXTRA_ANIMAL_ITEM = "imguri";

    //각 실행을 하고 마지막에 연결된 메소드 오버라이딩할때 return해주는 값

    public ImgAdapterMulti(Context context, Activity activity){
        oContext = context;
        oActivity = activity;
    }

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int picturepos, int type, String imgidx) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    //1. 프로필 등록, 2. 게시물 등록 에서 사용, 3. 댓글 등록
    //프로필등록은 이걸 사용안하고 있음/
    public void settype(int type){
        this.type = type;
    }
    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ImgFormMulti> list){
        this.oImgbox = list;

        Log.d("this.oImgbox", String.valueOf(oImgbox));

    }


    @NonNull
    @Override
    public ImgAdapterMulti.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.writeimg_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgAdapterMulti.ViewHolder holder, int position) {
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
        ImageView deletebtn;

        //ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           // itemView.setOnCreateContextMenuListener(this);

            imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);
            deletebtn = (ImageView) itemView.findViewById(R.id.deletebtn);

            //리스트 중 아이템 하나 클릭시 작동

            //삭제 버튼 클릭시
            deletebtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if(type == 1) { //프로필 등록
//                            if (position == 0) { //사진첩, 카메라 중 선택
//                                //Log.d("",position+" 클릭!!!!!!!!!!!!");
//                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
//
//                                builder.setTitle("프로필 사진 업로드");
//
//                                builder.setItems(R.array.imgckarray, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int pos) {
//                                        //String[] items = view.getResources().getStringArray(R.array.imgckarray);
//
//                                        if (mListener != null) {
//                                            int type = 1;
//                                            mListener.onItemClick(view, position, pos, type, "-"); //리스너 통해서 값을 activity로 전달
//                                        }
//                                    }
//                                });
//                                AlertDialog alertDialog = builder.create();
//
//                                alertDialog.show();
//
//                            } else { //삭제 시도
//                                //다이어그램 띄우기
//                                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
//
//                                builder.setTitle("프로필 사진 삭제").setMessage("사진을 삭제합니다");
//
//                                //삭제 실시
//                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        //Toast.makeText(view.getContext(), "OK Click"+ id, Toast.LENGTH_SHORT).show();
//                                        //Toast.makeText(view.getContext(), "OK Click"+ oImgbox.get(position).getImgidx(), Toast.LENGTH_SHORT).show();
//
//                                        if (mListener != null) {
//                                            int type = 2;
//                                            mListener.onItemClick(view, position, id, type, oImgbox.get(position).getImgidx()); //리스너 통해서 값을 activity로 전달
//                                        }
//                                    }
//                                });
//
//                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        // Toast.makeText(view.getContext(), "Cancel Click"+id, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                                AlertDialog alertDialog = builder.create();
//
//                                alertDialog.show();
//                            }
                        }else if(type == 2){ //게시판 이미지
                            //다이어그램 띄우기
//                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
//
//                            builder.setTitle("게시판 사진 삭제").setMessage("사진을 삭제합니다");
//
//                            //삭제 실시
//                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //Toast.makeText(view.getContext(), "OK Click"+ id, Toast.LENGTH_SHORT).show();
//                                    //Toast.makeText(view.getContext(), "OK Click"+ oImgbox.get(position).getImgidx(), Toast.LENGTH_SHORT).show();
//
//
//                                }
//                            });
//
//                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // Toast.makeText(view.getContext(), "Cancel Click"+id, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            AlertDialog alertDialog = builder.create();
//
//                            alertDialog.show();

                            if (mListener != null) {
                                int type = 2;
                                mListener.onItemClick(view, position, type, oImgbox.get(position).getImgidx()); //리스너 통해서 값을 activity로 전달
                            }
                        }else if(type == 3){ //댓글 이미지 등록
//                            //다이어그램 띄우기
//                            AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
//
//                            builder.setTitle("댓글 사진 삭제").setMessage("사진을 삭제합니다");
//
//                            //삭제 실시
//                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //Toast.makeText(view.getContext(), "OK Click"+ id, Toast.LENGTH_SHORT).show();
//                                    //Toast.makeText(view.getContext(), "OK Click"+ oImgbox.get(position).getImgidx(), Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//
//                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // Toast.makeText(view.getContext(), "Cancel Click"+id, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            AlertDialog alertDialog = builder.create();
//
//                            alertDialog.show();

                            if (mListener != null) {
                                int type = 2;
                                mListener.onItemClick(view, position, type, oImgbox.get(position).getImgidx()); //리스너 통해서 값을 activity로 전달
                            }
                        }
                    }
                }
            });


            //이미지 뷰페이저 사용
            imgView_item.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        //사진 크게 보기, 뷰 페이저

                        Intent intent = new Intent(oContext, ImgbigsizeActivity.class);
                        intent.putExtra(EXTRA_ANIMAL_ITEM,  String.valueOf(oImgbox.get(position).getoutputUri()));
                        // intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                oActivity,
                                imgView_item,
                                Objects.requireNonNull(ViewCompat.getTransitionName(imgView_item)));

                        oActivity.startActivity(intent, options.toBundle());


//                        if (mListener != null) {
//
//                            int type = 1;
//                            //Profileview activity에서 클릭시 작동
//                            mListener.onItemClick(view, position, 0, type, oImgbox.get(position).getImgidx()) ; //리스너 통해서 값을 activity로 전달
//                        }
                    }
                }
            });
        }

        void onBind(ImgFormMulti item){
//            if(type == 1) { //프로필 등록
//                if (item.isSelected()) { //true
//                    String str = item.getoutputUri().toString();
//                    if (str.equals("empty")) {
//                        imgView_item.setImageResource(R.drawable.imgbox);
//                    } else {
//                        //Uri itemuri = Uri.parse(item);
//                        //imgView_item.setImageURI(item.getUri());
//                        Picasso.get()
//                                .load(item.getoutputUri()) // string or uri 상관없음
//                                .resize(200, 200)
//                                .centerCrop()
//                                .into(imgView_item);
//                    }
//
//                } else { //false
//
//                    //Log.d("onResponse ? ", "onResponse 프로필 이미지 서버 데이터만 : " + String.valueOf(item.getUri()));
//                    Picasso.get()
//                            .load(item.getoutputUri()) // string or uri 상관없음
//                            .resize(200, 200)
//                            .centerCrop()
//                            .into(imgView_item);
//
//                }
//            }else{ //게시물 등록
                //Log.d("onResponse ? ", "onResponse 프로필 이미지 서버 데이터만 : " + String.valueOf(item.getUri()));
                Picasso.get()
                        .load(item.getoutputUri()) // string or uri 상관없음
                        .resize(200, 200)
                        .centerCrop()
                        .into(imgView_item);
           // }
        }
    }
}
