package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hometeacher.Profile.Profileview;
import com.squareup.picasso.Picasso;

public class ImgbigsizeActivity extends AppCompatActivity {

    ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgbigsize);


        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        //인텐트 데이터를 받는다.
        Intent intent = getIntent();
        String imageTransitionuri = intent.getExtras().getString(Profileview.EXTRA_ANIMAL_ITEM);
       // String imageTransitionName = intent.getExtras().getString(Profileview.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);

       // ivProfile.setTransitionName(imageTransitionName);

        //Log.d("------imageTransitionuri----",imageTransitionuri);
        Picasso.get()
                .load(imageTransitionuri) // string or uri 상관없음
                .resize(200, 200)
                .centerCrop()
                .into(ivProfile);


        //이미지 클릭시
        ivProfile.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}