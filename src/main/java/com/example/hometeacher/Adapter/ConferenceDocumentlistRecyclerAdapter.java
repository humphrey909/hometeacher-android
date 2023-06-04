package com.example.hometeacher.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.ConferenceDocumentForm;
import com.example.hometeacher.ArraylistForm.Pen;
import com.example.hometeacher.ArraylistForm.Pen_STEP;
import com.example.hometeacher.ArraylistForm.conferenceUserForm;
import com.example.hometeacher.ConferenceRoomPage;
import com.example.hometeacher.Profile.Profileview;
import com.example.hometeacher.R;
import com.example.hometeacher.RetrofitService;
import com.example.hometeacher.ThumbnailDrawCanvas;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConferenceDocumentlistRecyclerAdapter extends RecyclerView.Adapter<ConferenceDocumentlistRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity_conferenceroom;
    //int RESULTCODE;


    ArrayList<ConferenceDocumentForm> list;
    ArrayList<ArrayList<String>> Sessionlist;
    String SelectDocumentIdx;
    int edittype; // 0. 일반 1. : 편집모드 리스트 편집모드 변환시 사용
    ArrayList<Pen_STEP> drawCommandList_Total_STEP;

    View view;

    //삭제할 리스트
    ArrayList<String> DeleteDocumentlist; //삭제할 idx 리스트
    ArrayList<String> DeleteDocumentCountlist; //삭제할 문서 순서 리스트

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //리스너를 통해서 activity로 값을 전달함.
    public interface OnItemClickListener {
        void onItemClick(View v, int position, ArrayList<ConferenceDocumentForm> list, int type, CheckBox documentcheckbox) throws JSONException;
    }
    // 리스너 객체 참조를 저장하는 변수
    private ConferenceDocumentlistRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    public ConferenceDocumentlistRecyclerAdapter(Context context, Activity oActivity_conferenceroom){
        oContext = context;
        this.oActivity_conferenceroom = oActivity_conferenceroom;
    }

    public void setSessionval(ArrayList<ArrayList<String>> Sessionlist) {
        this.Sessionlist = Sessionlist;

    }
    public void setdata(String SelectDocumentIdx, int edittype, ArrayList<Pen_STEP> drawCommandList_Total_STEP){
        this.SelectDocumentIdx = SelectDocumentIdx;
        this.edittype = edittype;
        this.drawCommandList_Total_STEP = drawCommandList_Total_STEP;
    }

    //한개만 업데이트
    public void notifyItemChanged_(ArrayList<Integer> documentclicklist, ArrayList<Pen_STEP> drawCommandList_Total_STEP){

        this.drawCommandList_Total_STEP = drawCommandList_Total_STEP;

        for(int i = 0; i<documentclicklist.size();i++){
            notifyItemChanged(documentclicklist.get(i), "1");

            //Log.d("---documentclicklist----", String.valueOf(documentclicklist.get(i)));

        }
    }

//    //여러개 범위별로 업데이트
//    public void notifyItemRangeChanged_(int position, ArrayList<Pen_STEP> drawCommandList_Total_STEP){
//
//        this.drawCommandList_Total_STEP = drawCommandList_Total_STEP;
//
//        notifyItemRangeChanged(0, list.size(), "1");
//    }



    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ConferenceDocumentForm> list){
        this.list = list;
    }


    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(edittype == 0){ // 편집타입 0
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conference_documentitem, parent, false);
        }else{ // 편집 타입 1
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conference_documentitem_chk, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, List<Object> payloads) {
        try {
            holder.onBind(list.get(position), payloads);
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
        TextView count;
        LinearLayout documentpageLinear;
        CheckBox documentcheckbox;
        //ImageView imgView_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentpageLinear = (LinearLayout) itemView.findViewById(R.id.documentpageLinear);
            count = (TextView) itemView.findViewById(R.id.count);
            documentcheckbox = (CheckBox) itemView.findViewById(R.id.documentcheckbox);
            //imgView_item = (ImageView) itemView.findViewById(R.id.imgView_item);

            //문서 클릭시 해당 좌표 보여줌
            if(edittype == 0) { // 일반 타입 0

                //이미지 상자 클릭
                documentpageLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴
                            if (mListener != null) {
                                try {
                                    mListener.onItemClick(view, position, list, 1, null); //리스너 통해서 값을 activity로 전달
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            // setMultipleSelection(position);
                            SelectDocumentIdx = list.get(position).getidx();
                            // documentpageLinear.setBackgroundResource(R.drawable.btndesign10);
                        }
                    }
                });
            }

            //체크박스 체크할때 사용
            if(edittype == 1) { // 편집 타입 1
                //체크버튼 클릭
                documentcheckbox.setOnClickListener(new CheckBox.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            //클릭시 체크, 체크해제 반복
                            if(list.get(position).isCheckeded()){
                                list.get(position).setChecked(false);
                            }else{
                                list.get(position).setChecked(true);
                            }


                            // Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴
                            if (mListener != null) {
                                try {
                                    mListener.onItemClick(view, position, list, 2, documentcheckbox); //리스너 통해서 값을 activity로 전달


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });


                //이미지 상자 클릭
                //클릭시 이미지가 변경되게 할 것. 하지만 일반모드로 돌아갔을땐 전에 선택되었던 걸로 돌아가도록한다.
                documentpageLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            //클릭시 체크, 체크해제 반복
                            if(list.get(position).isCheckeded()){
                                list.get(position).setChecked(false);
                            }else{
                                list.get(position).setChecked(true);
                            }


                            //documentcheckbox.setChecked(true);

                            // Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴
                            if (mListener != null) {
                                try {
                                    mListener.onItemClick(view, position, list, 3, documentcheckbox); //리스너 통해서 값을 activity로 전달
                                   // mListener.onItemClick(view, position, list, 3, documentcheckbox); //리스너 통해서 값을 activity로 전달
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            // setMultipleSelection(position);
                            SelectDocumentIdx = list.get(position).getidx();
                            // documentpageLinear.setBackgroundResource(R.drawable.btndesign10);

                            //
                        }
                    }
                });
            }
        }

        //안에 들어갈 정보 변경하는 부분
        @SuppressLint("SetTextI18n")
        void onBind(ConferenceDocumentForm item, List<Object> payloads) throws JSONException, ParseException {
            int position = getAdapterPosition();


           // Log.d("----position-----",String.valueOf(position));
            //Log.d("----payloads-----",String.valueOf(payloads.size()));
            //Log.d("----payloads-----",String.valueOf(payloads));

            if(edittype == 1){
                //체크박스 전부 강제 체크
                if(item.isCheckeded()){
                    documentcheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#fbb033")));//이미 초대된 상태의 유저는 클릭 안함
                    documentcheckbox.setChecked(true);
                }else{ //체크박스 전부 강제 해제
                    documentcheckbox.setChecked(false);
                }
            }

            DrawCanvas_ drawCanvas = new DrawCanvas_(oContext);

            if(payloads.size() != 0){//포지션 별로 새로고침 요청한 경우
                if(payloads.get(0).equals("1")){//새로고침요청
                   // Toast.makeText(oContext, String.valueOf(payloads.get(0)), Toast.LENGTH_SHORT).show();
                   // Log.d("----payloads!!!!!-----",String.valueOf(payloads.get(0)));


                    //캔버스 삭제후 새로 추가
                    documentpageLinear.removeAllViews();
                    documentpageLinear.addView(drawCanvas);

                    // Log.d("---documentclicklist number----", String.valueOf(position));
                    // Log.d("---SelectDocumentIdx----", String.valueOf(SelectDocumentIdx));
                     //Log.d("---isSelected----", String.valueOf(item.isSelected()));

                    //다시 생각
                    //선택한 문서를 지정
                    if(item.getidx().equals(SelectDocumentIdx)){
                        documentpageLinear.setBackgroundResource(R.drawable.btndesign10); //선택
                    }else{
                        //선택, 미선택 색상
                        //if(item.isSelected()){
                        //    documentpageLinear.setBackgroundResource(R.drawable.btndesign10);//선택
                       // }else{
                            documentpageLinear.setBackgroundResource(R.drawable.btndesign9);//미선택
                       // }
                    }
                }
            }else{ //처음불러온 경우
                if(edittype == 0) { //편집타입 0
                    count.setText(String.valueOf(position + 1));
                }

               // Log.d("getbasicuri",String.valueOf(position));
               // Log.d("getbasicuri",list.get(position).getbasicuri());

                documentpageLinear.removeAllViews();
                documentpageLinear.addView(drawCanvas);

                //선택한 문서를 지정
                if(item.getidx().equals(SelectDocumentIdx)){
                    documentpageLinear.setBackgroundResource(R.drawable.btndesign10);
                }else{
                    //선택, 미선택 색상
                   // if(item.isSelected()){
                   //     documentpageLinear.setBackgroundResource(R.drawable.btndesign10);
                   // }else{
                        documentpageLinear.setBackgroundResource(R.drawable.btndesign9);
                   // }
                }
            }
            drawCanvas.init();
            drawCanvas.senddata(item);

            //문서에 맞게 그린 좌표를 찍어주는 부분
                for(int t = 0;t<drawCommandList_Total_STEP.size();t++){
                    if(item.getidx().equals(drawCommandList_Total_STEP.get(t).getidx())){

                        ArrayList<Pen> totalpenlist = drawCommandList_Total_STEP.get(t).getTotalPen();

                        ArrayList<Pen> drawCommandList = new ArrayList<>();
                        drawCommandList.addAll(totalpenlist);

                        drawCanvas.sendlist(drawCommandList);

                        //drawCanvas.invalidate();

                    }
                }

           // Log.d("getidx",String.valueOf(list.get(position).getidx()));
           // Log.d("getbasicuri",String.valueOf(list.get(position).getbasicuri()));
           // Log.d("getsrc",String.valueOf(list.get(position).getsrc()));

            //미리보기 구현
                if(!item.getbasicuri().equals("")) {

                    String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+item.getbasicuri()+item.getsrc();


                   // Log.d("------imguri-------",String.valueOf(imguri));

                    //drawCanvas.image_access = 1;
                    //drawCanvas.invalidate();

                    Picasso.get()
                            .load(imguri)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                    /* Save the bitmap or do something with it here */

                                    //Set it in the ImageView
                                    // theView.setImageBitmap(bitmap);
                                    drawCanvas.bitmapimg = bitmap; //bitmap이미지를 저장하는 부분
                                    drawCanvas.image_access = 1; //이미지를 사용할지 말지 결정하는 변수

                                    drawCanvas.invalidate();//화면을 갱신하다.


                                    //여기서 bitmap과 image_acceess가 빈값이 나온다...
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                }else{
                    drawCanvas.image_access = 0;
                    drawCanvas.invalidate();
                }

        }

        class DrawCanvas_ extends View {
            Paint paint;                //펜
            ArrayList<Pen> drawCommandList = new ArrayList<>();
            Bitmap bitmapimg; //bitmap이미지를 저장하는 부분
            int image_access = 0; //이미지를 사용할지 말지 결정하는 변수 0. 이미지 사용x, 1. 이미지 사용

            String idx;
            String imguri;
            private void senddata(ConferenceDocumentForm item) {
                this.idx = item.getidx();


                String imguri = RetrofitService.MOCK_SERVER_FIRSTURL+item.getbasicuri()+item.getsrc();
                this.imguri = imguri;
            }


            private void init() {
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }

            private void sendlist(ArrayList<Pen> drawCommandList) {
                this.drawCommandList = drawCommandList;
            }

            public DrawCanvas_(Context context) {
                super(context);
            }

            public DrawCanvas_(Context context, @Nullable AttributeSet attrs) {
                super(context, attrs);
            }

            public DrawCanvas_(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            public DrawCanvas_(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }

            @Override
            protected void onDraw(Canvas canvas) { //그려질때 두번째실행 그릴때마다 작동함

                //canvas.drawColor(Color.WHITE); //배경색을 변경한다.

//                Log.d("canvasdata",String.valueOf(idx));
//                Log.d("canvasdata",String.valueOf(image_access));
//                Log.d("canvasdata",String.valueOf(imguri));
//                Log.d("canvasdata",String.valueOf(bitmapimg));



                //이미지 허용일때만 이미지 생성
                if(image_access == 1){
                   // int w = bitmapimg.getWidth();
                   // int h = bitmapimg.getHeight();
                    //Log.d("--w--",String.valueOf(w));
                    //Log.d("--h--",String.valueOf(h));

                    //이미지 비율 적용 하기 !!!

//                    사이즈 135dp 에서 중앙으로 맞춤

                    @SuppressLint("DrawAllocation")
                    Bitmap resize = resizeBitmapImage(bitmapimg, 200);
                    canvas.drawBitmap(resize, 40, 0, null);
                }else{
                    canvas.drawColor(Color.WHITE);
                }

                //선을 그린다.
                if(drawCommandList.size() > 0){
                    for (int i = 0; i < drawCommandList.size(); i++) {
                        Pen p = drawCommandList.get(i);
//                        Log.d("tool",String.valueOf(p.gettools()));
//                        Log.d("x",String.valueOf(p.getx()));
//                        Log.d("y",String.valueOf(p.gety()));
//                        Log.d("moveStatus",String.valueOf(p.getmoveStatus()));
//                        Log.d("color",String.valueOf(p.getcolor()));
//                        Log.d("size",String.valueOf(p.getsize()));

                        if(String.valueOf(p.gettools()).equals("7")) { // 원일때
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(3);
                            paint.setColor(p.getcolor());

                            if (p.isMove()) {
                                canvas.drawCircle(p.getx()/5, p.gety()/8, p.getradius()/8, paint);
                            }
                        }else{//선이나 지우개, 뒤로가기 앞으로가기 ?
                            paint.setColor(p.getcolor());
                            paint.setStrokeWidth(p.getsize());

                            paint.setAntiAlias(true); // enable anti aliasing
                            paint.setDither(true); // enable dithering
                            paint.setStrokeJoin(Paint.Join.ROUND); // set the join to round you want
                            paint.setStrokeCap(Paint.Cap.ROUND);  // set the paint cap to round too

                            if (p.isMove()) {
                                Pen prevP = drawCommandList.get(i - 1);
                                canvas.drawLine(prevP.getx() / 5, prevP.gety() / 8, p.getx() / 5, p.gety() / 8, paint);
                            }else if(p.isStart()){ //시작할때 표시
                                canvas.drawLine((p.getx()-4)/5, (p.gety()-4)/8, p.getx()/ 5, p.gety()/ 8, paint);
                            }
                        }
                    }
                }
            }
        }


        //단일 선택 기능
        @SuppressLint("NotifyDataSetChanged")
        private void setMultipleSelection(int adapterPosition){

            if(list.get(adapterPosition).isSelected()){ //true
               // list.get(adapterPosition).setSelected(false);
            }else{ //false
                for(int i = 0; i<list.size();i++){
                    if(i == adapterPosition){
                        list.get(i).setSelected(true);
                    }else{
                        if(list.get(i).isSelected()){ //true
                            list.get(i).setSelected(false);
                        }
                    }
                }

               // notifyDataSetChanged();
            }
            //데이터 적용
           // listViewItemList.clear()
            //listViewItemList.addAll(newlist)

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

    //비트맵 리사이즈
    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height)
        {
            if(maxResolution < width)
            {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < height)
            {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }
}
