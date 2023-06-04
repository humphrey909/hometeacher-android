package com.example.hometeacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.hometeacher.ArraylistForm.Pen;

import java.util.ArrayList;

public class ThumbnailDrawCanvas  extends View {


    Paint paint;                //펜
    ArrayList<Pen> drawCommandList = new ArrayList<>();
    public Bitmap bitmapimg; //bitmap이미지를 저장하는 부분
    public int image_access = 0; //이미지를 사용할지 말지 결정하는 변수 0. 이미지 사용x, 1. 이미지 사용


    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void sendlist(ArrayList<Pen> drawCommandList) {
        this.drawCommandList = drawCommandList;
    }

    public ThumbnailDrawCanvas(Context context) {
        super(context);
    }

    public ThumbnailDrawCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbnailDrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThumbnailDrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) { //그려질때 두번째실행 그릴때마다 작동함

        //canvas.drawColor(Color.WHITE); //배경색을 변경한다.

        //이미지 허용일때만 이미지 생성
        if(image_access == 1){
            // int w = bitmapimg.getWidth();
            // int h = bitmapimg.getHeight();
            //Log.d("--w--",String.valueOf(w));
            //Log.d("--h--",String.valueOf(h));

            @SuppressLint("DrawAllocation")
            //Rect dst = new Rect(0, 0, 400 + w / 2, 800 + h / 2);

            //Bitmap resize = Bitmap.createScaledBitmap(bitmapimg, (int) 250, (int) 250, true);
            //canvas.drawBitmap(resizedBmp, null, dst, null);

            Bitmap resize = resizeBitmapImage(bitmapimg, 300);
            canvas.drawBitmap(resize, 0, 0, null);
        }else{
            canvas.drawColor(Color.WHITE);
        }

        //선을 그린다.
        if(drawCommandList.size() > 0){
            for (int i = 0; i < drawCommandList.size(); i++) {
                Pen p = drawCommandList.get(i);
                Log.d("x",String.valueOf(p.getx()));
                Log.d("y",String.valueOf(p.gety()));
                Log.d("moveStatus",String.valueOf(p.getmoveStatus()));
                Log.d("color",String.valueOf(p.getcolor()));
                Log.d("size",String.valueOf(p.getsize()));

                //if(String.valueOf(p.getradius()).equals("0")) { //선이라 판
                paint.setColor(p.getcolor());
                paint.setStrokeWidth(p.getsize());

                if (p.getmoveStatus() == 1) {
                    Pen prevP = drawCommandList.get(i - 1);

                    canvas.drawLine(prevP.getx() / 3, prevP.gety() / 8, p.getx() / 3, p.gety() / 8, paint);

                }
//                        }else{// 원일때
//                            paint.setStyle(Paint.Style.STROKE);
//                            paint.setStrokeWidth(3);
//                            paint.setColor(p.getcolor());
//
//                            if (p.isMove()) {
//                                canvas.drawCircle(p.getx()/3, p.gety()/8, p.getradius()/8, paint);
//                            }
//                        }
            }
        }
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
