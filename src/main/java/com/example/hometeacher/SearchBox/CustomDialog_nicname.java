package com.example.hometeacher.SearchBox;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.hometeacher.R;

import java.text.ParseException;
import java.util.Objects;

public class CustomDialog_nicname extends Dialog {
    private Context oContext;
    private CustomDialogClickListener customDialogClickListener;

    EditText nicnameedit;
    Button paysearchcomplete, resetbtn;

    String Selectnicname;

    public CustomDialog_nicname(@NonNull Context context) {
        super(context);
        this.oContext = context;
    }

    public void setdata(String Selectnicname){
        this.Selectnicname = Selectnicname;
    }

    public void CunstomMenuDialogStart(CustomDialogClickListener customDialogClickListener) {
        this.customDialogClickListener = customDialogClickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.customdialog_nicname);


        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nicnameedit = (EditText) findViewById(R.id.nicnameedit);
        nicnameedit.setText(Selectnicname);

        paysearchcomplete = (Button) findViewById(R.id.paysearchcomplete);
        paysearchcomplete.setOnClickListener(v -> {
            // 저장버튼 클릭
            try {
                this.customDialogClickListener.paysearchcompleteCK(String.valueOf(nicnameedit.getText()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dismiss();
        });

        resetbtn = (Button) findViewById(R.id.resetbtn);
        resetbtn.setOnClickListener(v -> {
            nicnameedit.setText("");
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
        void paysearchcompleteCK (String nicname) throws ParseException;
       // void resetCK (String nicname) throws ParseException;
       // void paysearchcomplete2CK (String minpay, String maxpay) throws ParseException;

        //  void usecatebtnCK() throws ParseException;
        //void pricecatebtnCK() throws ParseException;
    }
}
