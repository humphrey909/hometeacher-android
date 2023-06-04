package com.example.hometeacher;

import android.util.Log;

import com.example.hometeacher.ArraylistForm.ServerChatSendForm;
import com.example.hometeacher.ArraylistForm.ServerWhiteboardSendForm;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

public class SocketSend {
    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String Senddata = "";
    BufferedReader input = null;
    PrintWriter outwriter;

    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    //ArrayList<ServerWhiteboardSendForm> QueSavelist_whiteboard = new ArrayList<ServerWhiteboardSendForm>(); //서버에 전송하기 위해 담는 데이터

    public SocketSend(com.example.hometeacher.shared.GlobalClass GlobalClass){
        this.GlobalClass = GlobalClass;
    }

    //소켓에 데이터를 전송한다.
    public void SendSocketData(String commandtype, String roomidx, String participantnum, String myuid, String Senddata, String imgchk, String nid, String roomtype, String chattype, String sendrid) {

        //소켓이 서버와 연결 되었을때만 작동한다.
        if(GlobalClass.getsocket() != null){

        //채팅 전송시 스레드로 전송
        Thread thread = new Thread(){
            public void run(){
                try {
                    String currenttime = Makecurrenttime();//현재시간 불러오기
                    outwriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(GlobalClass.getsocket().getOutputStream())), true);


                    //명령어조건, 방고유번호, 방 인원수, 유저고유번호, 문자내용, 시간
                    ServerChatSendForm oServerChatSendForm = new ServerChatSendForm(commandtype, roomidx,participantnum,myuid, Senddata, currenttime, imgchk, nid, roomtype, chattype, sendrid);

                    JSONObject Sendobj = new JSONObject();
                    try {
                        Sendobj.put("Sendinfo", oServerChatSendForm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //방에 묶여있던 클라이언트를 변경해줌
                    outwriter.println(new Gson().toJson(Sendobj));
                    // outwriter.write( commandtype+ ">"+roomidx+">"+participantnum+">"+myuid+">" +Senddata+">"+currenttime+ "\n");
                    outwriter.flush();

                    // outwriter.write( "CHATSEND"+ ">"+ChatRoominfo.get(0).get("roomname")+">"+ Sessionlist.get(1).get(4) + ">" +Senddata+ "\n");
                    //outwriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        }
    }

    //소켓에 데이터를 화이트보드 모드로 전송한다.
    public void SendSocketData_whiteboard(String commandtype, String roomidx, String myuid, String roomtype, String whiteboarddata, String tooltype, String drawcount, String SelectDocumentCount, String SelectDocumentIdx) {

        //소켓이 서버와 연결 되었을때만 작동한다.
        if(GlobalClass.getsocket() != null) {
            //채팅 전송시 스레드로 전송
            Thread thread = new Thread() {
                public void run() {

                    try {
                        //String currenttime = Makecurrenttime();//현재시간 불러오기
                        outwriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(GlobalClass.getsocket().getOutputStream())), true);


                        //명령어조건, 방고유번호, 유저고유번호, 방 타입, 화이트보드내용
                        ServerWhiteboardSendForm oServerWhiteboardSendForm = new ServerWhiteboardSendForm(commandtype, roomidx, myuid, roomtype, whiteboarddata, tooltype, drawcount, SelectDocumentCount, SelectDocumentIdx);

                        JSONObject Sendobj = new JSONObject();
                        try {
                            Sendobj.put("Sendinfo", oServerWhiteboardSendForm);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //방에 묶여있던 클라이언트를 변경해줌
                        outwriter.println(new Gson().toJson(Sendobj));
                        // outwriter.write( commandtype+ ">"+roomidx+">"+participantnum+">"+myuid+">" +Senddata+">"+currenttime+ "\n");
                        outwriter.flush();

                        // outwriter.write( "CHATSEND"+ ">"+ChatRoominfo.get(0).get("roomname")+">"+ Sessionlist.get(1).get(4) + ">" +Senddata+ "\n");
                        //outwriter.flush();

                    } catch (SocketTimeoutException e){
                        Log.d("d","----------timeout------------");
                    } catch (SocketException e) { //서버가 연결중에 끊겼을때 작동한다.
                        Log.d("d","----------server down------------");
                        Log.d("d","----------err test------------");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                        //에러가 낫을때
                        //에러 낫을때 보낸 데이터를 변수에 저장하고 있는다. 순차적으로 저장.
                        //계속 재전송시도
                        //2초에 한번씩 계속 보낸다. 에러가 안날때 까지.

                        //데이터 끊겼을때 데이터가 여러번 전송되도록 한다면?
                        //인터넷을 껏을때 데이터 전송을 몇번하고 재연결을 하게 되면


                    }
                }
            };

            thread.start();

        }else{ // null이면? 값을 큐 arraylist에 넣어서 갖고 있다가 한번에 전송하자.
            ServerWhiteboardSendForm oServerWhiteboardSendForm = new ServerWhiteboardSendForm(commandtype, roomidx, myuid, roomtype, whiteboarddata, tooltype, drawcount, SelectDocumentCount, SelectDocumentIdx);

            GlobalClass.QueSavelist_whiteboard.add(oServerWhiteboardSendForm);

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
}
