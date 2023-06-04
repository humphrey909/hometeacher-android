package com.example.hometeacher.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.SubjectForm;
import com.example.hometeacher.R;

import java.util.ArrayList;

public class SubjectRecyclerAdapter extends RecyclerView.Adapter<SubjectRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;

   // AccountBook oAccountBook;
   // UseKindsList oUseKindsList;

    ArrayList<SubjectForm> list;

    int listType = 0; // 1 : 프로필 수정에서 선택하게 설정,  2: 과외 찾기에서 리스트만 보이게 설정

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private SubjectRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public SubjectRecyclerAdapter(Context context){
        oContext = context;
       // oAccountBook = new AccountBook(oContext);
      //  oUseKindsList = new UseKindsList(oContext);
    }
    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<SubjectForm> list){
        this.list = list;
    }
    public void setlisttype(int type){
        this.listType = type;
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
        holder.onBind(list.get(position));
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

            //리스트 중 아이템 하나 클릭시 작동
            rowname.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    //Selectlist.add(position, );

                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴

                        //if(listType == 1){//프로필 수정에서만 사용
                            setMultipleSelection(getAdapterPosition());

                            if (mListener != null) {
                                mListener.onItemClick(view, position) ; //리스너 통해서 값을 activity로 전달
                            }
                        //}else{ //과외찾기 리스트에서 사용
//                            setMultipleSelection(getAdapterPosition());
//
//                            if (mListener != null) {
//                                mListener.onItemClick(view, position) ; //리스너 통해서 값을 activity로 전달
//                            }
                        //}
                    }
                }
            });

        }

        //안에 들어갈 정보 변경하는 부분
        void onBind(SubjectForm item){
            rowname.setText(item.getName());

            if(listType == 1){ //프로필 수정에서만 사용
                if(item.isSelected()){
                    rowname.setBackgroundColor(Color.parseColor("#90CAF9"));
                }else{
                    rowname.setBackgroundColor(Color.parseColor("#FAFAFA"));
                }
            }else{//과외찾기 리스트에서 사용
                if(item.isSelected()){
                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
                }else{
                    rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign4));
                }
                //rowname.setBackground(ContextCompat.getDrawable(oContext, R.drawable.btndesign2));
            }
        }
        //다중 선택 기능
        private void setMultipleSelection(int adapterPosition){

            //반대의 값을 넣어준다.
            if(list.get(adapterPosition).isSelected()){

                list.get(adapterPosition).setSelected(false);
            }else{
                list.get(adapterPosition).setSelected(true);
            }

            //데이터 적용
            notifyDataSetChanged();
        }
    }
}
