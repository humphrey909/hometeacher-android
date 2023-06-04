package com.example.hometeacher.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.ColorForm;
import com.example.hometeacher.ImgViewPagerActivity;
import com.example.hometeacher.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ColorViewAdapter extends RecyclerView.Adapter<ColorViewAdapter.ViewHolder> {

    Context oContext;
    ArrayList<ArrayList<String>> Sessionlist;

    ArrayList<ColorForm> list;
    //각 실행을 하고 마지막에 연결된 메소드 오버라이딩할때 return해주는 값


    //카메라 열고 임시 경로
    private String mCurrentPhotoPath;
    //저장시 파일 이름
    private String SavePicturePath; //경로
    private String SavePictureName; //이름



    Activity oActivity;

    public ColorViewAdapter(Context context , Activity activity){
        oContext = context;
        oActivity = activity;
    }

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int picturepos, ArrayList<ColorForm> list) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ColorForm> list){
        this.list = list;

        Log.d("this.oImgbox", String.valueOf(list));

    }


    @NonNull
    @Override
    public ColorViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.colorview_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewAdapter.ViewHolder holder, int position) {
        //if(this.oImgbox != null) {
            holder.onBind(list.get(position));
       // }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder{
        TextView color_item;
       // TextView paypriceview;

        //ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           // itemView.setOnCreateContextMenuListener(this);

            color_item = (TextView) itemView.findViewById(R.id.color_item);

            //리스트 중 아이템 하나 클릭시 작동
            color_item.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){

                        setSingleSelection(getAdapterPosition());
                        if (mListener != null) {
                            //Profileview activity에서 클릭시 작동
                            mListener.onItemClick(view, position, list) ; //리스너 통해서 값을 activity로 전달
                        }
                    }
                }
            });
        }

        void onBind(ColorForm item){
//            Log.d("SelectColorinServer", SelectColorinServer);
//            Log.d("getColor", item.getColor());
//            if(SelectColorinServer.equals(item.getColor())){
//                color_item.setText("선택");
//            }



            if(item.isSelected()){ //true
                color_item.setText("선택");
                color_item.setBackgroundColor(Color.parseColor(item.getColor()));
            }else{ //false
                color_item.setText("");
                color_item.setBackgroundColor(Color.parseColor(item.getColor()));
            }
        }

        //단일 선택 기능
        private void setSingleSelection(int adapterPosition){

            //선택된 값은 수정 금지
            if(list.get(adapterPosition).isSelected()){
                //list.get(adapterPosition).setSelected(false);
            }else{
                for(int i = 0; i<list.size();i++){
                    if(adapterPosition != i){
                        list.get(i).setSelected(false);
                    }else{
                        list.get(i).setSelected(true);
                    }
                }
            }
            //데이터 적용
            notifyDataSetChanged();
        }
    }
}
