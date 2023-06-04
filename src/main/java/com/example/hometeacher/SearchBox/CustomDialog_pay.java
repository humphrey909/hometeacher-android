package com.example.hometeacher.SearchBox;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hometeacher.R;

import java.text.ParseException;
import java.util.Objects;

public class CustomDialog_pay extends Dialog {
    private Context oContext;
    private CustomDialogClickListener customDialogClickListener;

    TextView alerttext;
    EditText minpayedit, maxpayedit;
    Button paysearchcomplete, resetbtn;

    String Selectmin;
    String Selectmax;

    public CustomDialog_pay(@NonNull Context context) {
        super(context);
        this.oContext = context;
    }

    public void setdata(String Selectmin, String Selectmax){
        this.Selectmin = Selectmin;
        this.Selectmax = Selectmax;
    }

    public void CunstomMenuDialogStart(CustomDialogClickListener customDialogClickListener) {
        this.customDialogClickListener = customDialogClickListener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.customdialog_pay);

        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alerttext = (TextView) findViewById(R.id.alerttext);
        minpayedit = (EditText) findViewById(R.id.minpayedit);
        maxpayedit = (EditText) findViewById(R.id.maxpayedit);

        minpayedit.setText(Selectmin);
        maxpayedit.setText(Selectmax);

        alerttext.setVisibility(View.GONE);

        paysearchcomplete = (Button) findViewById(R.id.paysearchcomplete);
        paysearchcomplete.setOnClickListener(v -> {

            if(!String.valueOf(minpayedit.getText()).equals("") && !String.valueOf(maxpayedit.getText()).equals("")){//둘다 빈칸이 아닌 경우
                if(Integer.parseInt(String.valueOf(minpayedit.getText())) < Integer.parseInt(String.valueOf(maxpayedit.getText()))){
                    alerttext.setVisibility(View.GONE);
                    // 저장버튼 클릭
                    try {
                        this.customDialogClickListener.paysearchcompleteCK(String.valueOf(minpayedit.getText()), String.valueOf(maxpayedit.getText()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }else{
                    alerttext.setVisibility(View.VISIBLE);
                    Log.d("mmm ", "최소가격이 너무 큼");
                }
            }else{
                // 저장버튼 클릭
                try {
                    this.customDialogClickListener.paysearchcompleteCK(String.valueOf(minpayedit.getText()), String.valueOf(maxpayedit.getText()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });




        resetbtn = (Button) findViewById(R.id.resetbtn);
        resetbtn.setOnClickListener(v -> {
            minpayedit.setText("");
            maxpayedit.setText("");
        });


//        usecatebtn.setOnClickListener(v -> {
//            // 취소버튼 클릭
//            try {
//                this.customDialogClickListener.usecatebtnCK();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            dismiss();
//        });
//        pricecatebtn.setOnClickListener(v -> {
//            // 취소버튼 클릭
//            try {
//                this.customDialogClickListener.pricecatebtnCK();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            dismiss();
//        });
/*
        peoplecatebtn = (ImageButton)findViewById(R.id.peoplecatebtn);
        usecatebtn = (ImageButton)findViewById(R.id.usecatebtn);
        pricecatebtn = (ImageButton)findViewById(R.id.pricecatebtn);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.peoplecatebtn:
                        MonthCategorytab = 1;
                        Log.d("", String.valueOf(MonthCategorytab));


                        dismiss();
                        break;
                    case R.id.usecatebtn:
                        MonthCategorytab = 2;
                        Log.d("", String.valueOf(MonthCategorytab));
                        dismiss();
                        break;

                    case R.id.pricecatebtn:
                        MonthCategorytab = 3;
                        Log.d("", String.valueOf(MonthCategorytab));
                        dismiss();
                        break;
                }
            }
        };
        peoplecatebtn.setOnClickListener(clickListener);
        usecatebtn.setOnClickListener(clickListener);
        pricecatebtn.setOnClickListener(clickListener);
        */
    }

    public interface CustomDialogClickListener {
        void paysearchcompleteCK (String minpay, String maxpay) throws ParseException;
      //  void resetCK (String minpay, String maxpay) throws ParseException;
       // void paysearchcomplete2CK (String minpay, String maxpay) throws ParseException;

        //  void usecatebtnCK() throws ParseException;
        //void pricecatebtnCK() throws ParseException;
    }
}
