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

public class CustomDialog_school extends Dialog {
    private Context oContext;
    private CustomDialogClickListener customDialogClickListener;

    EditText schooledit;
    Button paysearchcomplete, resetbtn;

    String Selectschool;

    public CustomDialog_school(@NonNull Context context) {
        super(context);
        this.oContext = context;
    }

    public void setdata(String Selectschool){
        this.Selectschool = Selectschool;
    }


    public void CunstomMenuDialogStart(CustomDialogClickListener customDialogClickListener) {
        this.customDialogClickListener = customDialogClickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            setContentView(R.layout.customdialog_school);


        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        schooledit = (EditText) findViewById(R.id.schooledit);
        schooledit.setText(Selectschool);


        paysearchcomplete = (Button) findViewById(R.id.paysearchcomplete);
        paysearchcomplete.setOnClickListener(v -> {
            // 저장버튼 클릭
            try {
                this.customDialogClickListener.paysearchcompleteCK(String.valueOf(schooledit.getText()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dismiss();
        });
        resetbtn = (Button) findViewById(R.id.resetbtn);
        resetbtn.setOnClickListener(v -> {
            schooledit.setText("");
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
        void paysearchcompleteCK (String school) throws ParseException;
       // void resetCK (String school) throws ParseException;
       // void paysearchcomplete2CK (String minpay, String maxpay) throws ParseException;

        //  void usecatebtnCK() throws ParseException;
        //void pricecatebtnCK() throws ParseException;
    }
}
