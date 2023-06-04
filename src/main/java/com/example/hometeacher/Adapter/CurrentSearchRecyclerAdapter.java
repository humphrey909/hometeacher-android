package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.R;
import com.example.hometeacher.ArraylistForm.CurrentsearchForm;
import com.example.hometeacher.shared.CurrentSearchCharShared;

import org.json.JSONException;

import java.util.ArrayList;

public class CurrentSearchRecyclerAdapter extends RecyclerView.Adapter<CurrentSearchRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    //int RESULTCODE;


    ArrayList<CurrentsearchForm> list;
    ArrayList<ArrayList<String>> Sessionlist;

    int type = 0;

    View view;
    CurrentSearchCharShared oCurrentSearchCharShared;

    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<CurrentsearchForm> list, int type) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private CurrentSearchRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public CurrentSearchRecyclerAdapter(Context context){
        oContext = context;
       // oAccountBook = new AccountBook(oContext);
      //  oUseKindsList = new UseKindsList(oContext);
    }

    //type 0. 과외찾기, 1. 게시판, 2. 사람
    public void needdata(int type){
        this.type = type;
    }
    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<CurrentsearchForm> list){
        this.list = list;
    }
    //public void setlisttype(int type){
    //    this.listType = type;
    //}

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentsearchrecycleritem, parent, false);

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
        TextView rowname;
        ImageView closebtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rowname = (TextView) itemView.findViewById(R.id.rowname);
            closebtn = (ImageView) itemView.findViewById(R.id.closebtn);

            oCurrentSearchCharShared = new CurrentSearchCharShared(oContext);

            //리스트 중 아이템 하나 클릭시 작동
            closebtn.setOnClickListener (new View.OnClickListener () {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴
                        Log.d("Recyclerveiw", String.valueOf(list.get(position).getKey())); //선택한 숫자를 불러옴

                        //쉐어드에서 최근검색어 값을 삭제한다.
                       //oCurrentSearchCharShared.Deleteone(String.valueOf(list.get(position).get(0).get(0)));

                        //데이터 적용
                        notifyDataSetChanged();

                        if (mListener != null) {
                            mListener.onItemClick(view, position, list, 1) ; //type = 1 : 삭제버튼
                        }
                    }
                }
            });

            //최근 검색어로 재검색
            rowname.setOnClickListener (new View.OnClickListener () {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener != null) {
                            mListener.onItemClick(view, position, list, 2) ; //type = 2 : 재검색
                        }
                    }
                }
            });

        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(CurrentsearchForm item) throws JSONException {
            rowname.setText(item.getName());
        }
    }

}
