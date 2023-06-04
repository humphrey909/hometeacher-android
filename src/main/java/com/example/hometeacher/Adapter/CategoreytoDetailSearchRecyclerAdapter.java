package com.example.hometeacher.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.CategoreyForm;
import com.example.hometeacher.R;

import org.json.JSONException;

import java.util.ArrayList;

public class CategoreytoDetailSearchRecyclerAdapter extends RecyclerView.Adapter<CategoreytoDetailSearchRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;


    ArrayList<CategoreyForm> list;
    int SelectType;

    //int listType = 0; // 1 : 성격리스트,  2: 과목리스트

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, Button btnname) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private CategoreytoDetailSearchRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public CategoreytoDetailSearchRecyclerAdapter(Context context){
        oContext = context;
       // oAccountBook = new AccountBook(oContext);
      //  oUseKindsList = new UseKindsList(oContext);
    }

    public void setneeddata(int SelectType){
        this.SelectType = SelectType;
    }
    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<CategoreyForm> list){
        this.list = list;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoreyrecyclerbuttonrow, parent, false);

        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.onBind(list.get(position), position);
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
        Button rowname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowname = (Button) itemView.findViewById(R.id.rowname);

//            int position = getAdapterPosition ();
//            if(position == 0){
//                if(SelectType == 1){
//
//                    rowname.setTextColor(Color.parseColor("#FAFAFA"));
//                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
//                }else if(SelectType == 2){
//                    rowname.setTextColor(Color.parseColor("#FAFAFA"));
//                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
//                }
//            }




            //리스트 중 아이템 하나 클릭시 작동
            rowname.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    //Selectlist.add(position, );

                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴

                      //  setMultipleSelection(position);
//
                        if (mListener != null) {
                            try {
                                mListener.onItemClick(view, position, rowname) ; //리스너 통해서 값을 activity로 전달
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

//
//                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(oContext);
//                        builder.setTitle("카테고리 선택");
//
//                        builder.setItems(R.array.imgckarray, new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int pos)
//                            {
//
//                            }
//                        });
//
//                        AlertDialog alertDialog = builder.create();
//
//                        alertDialog.show();
//



                    }
                }
            });

        }

        //안에 들어갈 정보 변경하는 부분
        void onBind(CategoreyForm item, int pos) throws JSONException {
            if(pos == 0){
                if(SelectType == 1){
                    rowname.setText("선생님찾기");
                    rowname.setTextColor(Color.parseColor("#FAFAFA"));
                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
                }else if(SelectType == 2){
                    rowname.setText("학생찾기");
                    rowname.setTextColor(Color.parseColor("#FAFAFA"));
                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
                }else{
                    rowname.setText(item.getName());
                    //선택, 미선택 색상
                    if(item.isSelected()){
                        rowname.setTextColor(Color.parseColor("#FAFAFA"));
                        rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
                    }else{
                        rowname.setTextColor(Color.parseColor("#000000"));
                        rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign4));
                    }
                }
            }else{
                rowname.setText(item.getName());
                //선택, 미선택 색상
                if(item.isSelected()){
                    rowname.setTextColor(Color.parseColor("#FAFAFA"));
                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
                }else{
                    rowname.setTextColor(Color.parseColor("#000000"));
                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign4));
                }
            }







//            //선생님 리스트
//            if(SelectType == 1){
//
//
//            }else if(SelectType == 2){ //학생 리스트
//
//            }
        }

        //단일 선택 기능
        private void setMultipleSelection(int adapterPosition){

            if(list.get(adapterPosition).isSelected()){
                list.get(adapterPosition).setSelected(false);
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
