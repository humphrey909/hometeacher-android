package com.example.hometeacher;

import com.example.hometeacher.ArraylistForm.CurrentsearchForm;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

public class RegdateComparator implements Comparator<CurrentsearchForm> {
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int compare(CurrentsearchForm a1, CurrentsearchForm a2) {

        Date regdate1 = null;
        Date regdate2 = null;
        try {
            regdate1 = timeFormat.parse(String.valueOf(a1.regdate));
            regdate2 = timeFormat.parse(String.valueOf(a2.regdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //앞에 변수가 크면 1, 작으면 -1, 같으면 0
        int result1 = regdate1.compareTo(regdate2);
        int result2 = regdate2.compareTo(regdate1);
        // Log.d("", String.valueOf(result1));
        if(result1 > 0) {


            // Log.d("", regdate1 + ">" + regdate1);
            return 1;
        }else if(result1 < 0){
            //Log.d("", regdate1 + "<" + regdate1);
            return -1;
        }else{
            //Log.d("", regdate1 + "=" + regdate1);
            return 0;
        }
    }
}

