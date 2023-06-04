package com.example.hometeacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hometeacher.shared.Session;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;

public class MyclassVodview extends AppCompatActivity {
    Context oContext;
    Activity oActivity;

    Session oSession;
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    ArrayList<ArrayList<String>> Sessionlist;

    TextView toolbartitle;
    ImageView closetab;
    String vodidx;
    String roomidx;
    String voduri;
    String vodregdate;

    final String BASE_URL = RetrofitService.MOCK_SERVER_URL;
    RetrofitService retrofitService;
    Call<String> call;

    private PlayerView playerView;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclass_vodview);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.toolbarbox);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //뒤로가기 지우기


        division();


    }

    public void division() {

        GlobalClass = (com.example.hometeacher.shared.GlobalClass) getApplication(); //글로벌 클래스 선언

        oSession = new Session(this);
        Sessionlist = oSession.Getoneinfo("0");

        oActivity = this;
        oContext = this;

        //데이터 받음
        Intent intent = getIntent();
        vodidx = intent.getExtras().getString("vodidx"); // vod idx
        roomidx = intent.getExtras().getString("rid"); // 방 idx
        voduri = intent.getExtras().getString("voduri"); // voduri
        vodregdate = intent.getExtras().getString("vodregdate"); // vodregdate

        Log.d("-------vodidx-------", String.valueOf(vodidx));
        Log.d("-------roomidx-------", String.valueOf(roomidx));
        Log.d("-------voduri-------", String.valueOf(voduri));
        Log.d("-------vodregdate-------", String.valueOf(vodregdate));

        toolbartitle = (TextView) this.findViewById(R.id.toolbartitle);
        //날짜 시간을 변형한다.
        String dateStr = vodregdate; //2022-08-09 05:53:17
        String[] dateStr_split = dateStr.split(" ");
        String[] dateStr_date = dateStr_split[0].split("-");
        String[] dateStr_time = dateStr_split[1].split(":");
        System.out.println("second : " + String.valueOf(dateStr_date[0]));
        System.out.println("second : " + String.valueOf(dateStr_time[0]));

        String datemake = dateStr_date[0]+"년 "+dateStr_date[1]+"월 "+dateStr_date[2]+"일 ";

        String ampm = "";
        if(Integer.parseInt(dateStr_time[0]) > 12){ //오후
            ampm = "오후";
        }else{ //오전
            ampm = "오전";
        }
        String timemake = ampm+" "+dateStr_time[0]+":"+dateStr_time[1];
        toolbartitle.setText(datemake+timemake+" VOD ");





        //게시글 닫기
        closetab = (ImageView) this.findViewById(R.id.closetab);
        closetab.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                finish();
            }
        });
        Uri uri = Uri.parse(RetrofitService.MOCK_SERVER_FIRSTURL+voduri);
        MediaItem mediaItem = MediaItem.fromUri(uri);

        playerView = (PlayerView) findViewById(R.id.playerView);
        // Instantiate the player.
        player = new ExoPlayer.Builder(oContext).build();
// Attach player to the view.
        playerView.setPlayer(player);
// Set the media item to be played.
        player.setMediaItem(mediaItem);
// Prepare the player.
        player.prepare();
        player.play();

    }


}