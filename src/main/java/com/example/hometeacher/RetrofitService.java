package com.example.hometeacher;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    //해당 링크로 헤더와 파라미터를 전송한다.
    //@GET("control/account.php/")
//    Call<String> getPosts(
//       @Query("idx") String idx
//    );
   // String MOCK_SERVER_URL	= "http://49.247.27.186/index.php/"; // 통신 할 서버 baseUrl
    String MOCK_SERVER_URL	= "http://13.209.80.115/index.php/"; // 통신 할 서버 baseUrl
    String MOCK_SERVER_FIRSTURL	= "http://13.209.80.115"; // 통신 할 서버 baseUrl

        //이메일 전송 type = 1
        //http://49.247.27.186/index.php/join/accountmail?type=1
//        @FormUrlEncoded
//        @POST("join/accountmail/")
//        Call<String> emailsender(@Query("type") Integer type, @Field("toemail") String toemail);

        @Multipart
        @POST("join/accountmail/")
        Call<String> emailsender(
                @Query("type") Integer type,
                @PartMap Map<String, RequestBody> params);



        //회원가입 진행 type = 3
        //http://49.247.27.186/index.php/join/joinstart?type=1
        @FormUrlEncoded
        @POST("join/joinstart/")
        Call<String> joinstart(
                @Query("type") Integer type,
                @Field("usertype") Integer usertype,
                @Field("pid") Integer pid,
                @Field("name") String name,
                @Field("nicname") String nicname,
                @Field("email") String email,
                @Field("password") String password
        );


        //로그인 진행 type = 1
        @FormUrlEncoded
        @POST("login/loginstart/")
        Call<String> loginstart(
                @Query("type") Integer type,
                @Field("email") String email,
                @Field("password") String password
        );

        //내 정보 가져오기
        @FormUrlEncoded
        @POST("login/selectme/")
        Call<String> selectme(
                @Query("type") Integer type,
                @Field("idx") String uid
        );


        //성격 리스트 가져오기 type == 1
        //과목 리스트 가져오기 type == 2
        //@GET("categorey/getdata/0/0/")
        @Multipart
        @POST("categorey/getdata/0/0/")
        Call<String> categoreylist(
                @Query("type") Integer type,
                @PartMap Map<String, RequestBody> params
        );


        //멘토링 카테고리 리스트 가져오기 type == 1
        //선생님 토론 카테고리 리스트 가져오기 type == 2
        @Multipart
        @POST("nboardcategorey/getdata/0/0/")
        Call<String> nboardcategoreylist(
                @Query("type") Integer type,
                @PartMap Map<String, RequestBody> params
        );

        //프로필 이미지, 문자열 데이터 서버로 전송 - 추가
        @Multipart
        @POST("profile/profilesavebox/0/0/")
        Call<String> profileupload(
                @Query("type") Integer type,
                @PartMap Map<String, RequestBody> params,
                @Part ArrayList<MultipartBody.Part> file
        );
    //프로필 이미지(이미지파일 다중전송), 문자열 데이터 서버로 전송 - 수정
    @Multipart
    @POST("profile/profileupdatebox/0/0/")
    Call<String> profileupload_edit(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //프로필 정보 하나씩 수정하기. - 데이터 하나씩 (예 마이페이지 구함정도 )
    @Multipart
    @POST("profile/profileupdateone/0/0/")
    Call<String> profileupdateone(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //메인 프로필 이미지(이미지파일 단일전송) - 변경
    @Multipart
    @POST("profile/mainprofilesavebox/0/0/")
    Call<String> profilemainimgupload(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part file
    );

    //프로필 정보 가져오기
    @Multipart
    @POST("profile/getdatainfoone/0/0/")
    Call<String> profilegetlist(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //프로필 이미지 가져오기
    @Multipart
    @POST("profile/getdataimg/0/0/")
    Call<String> profilegetimglist(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //유저 리스트 가져오기
    @Multipart
    @POST("profile/userlist/{limit}/{offset}/")
    Call<String> getuserlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //유저 정보 변경
    @Multipart
    @POST("profile/setaccount/0/0/")
    Call<String> updateaccount(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //유저 패스워드 변경
    @Multipart
    @POST("profile/changepw/0/0/")
    Call<String> updatepassword(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //nicname 중복체크
    @Multipart
    @POST("profile/nicnameoverlab/0/0/")
    Call<String> nicnameoverlabchk(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //프로필 좋아요 추가
    @Multipart
    @POST("profile/likeadd/0/0/")
    Call<String> likeaddbtn(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );
    //프로필 좋아요 리스트
    @Multipart
    @POST("profile/likelist/0/0/")
    Call<String> likeaddlist(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //프로필 좋아요 조인 리스트
    @Multipart
    @POST("profile/likelistjoin/{limit}/{offset}/")
    Call<String> likeadduserjoinlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //게시물 이미지, 문자열 데이터 서버로 전송 - 추가
    @Multipart
    @POST("nboard/nboardsavebox/0/0/")
    Call<String> nboardupload(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //게시물 이미지(이미지파일 다중전송), 문자열 데이터 서버로 전송 - 수정
    @Multipart
    @POST("nboard/nboardupdatebox/0/0/")
    Call<String> nboardupload_edit(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //게시물 리스트
    @Multipart
    @POST("nboard/nboardlist/{limit}/{offset}/")
    Call<String> getnboardlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );
    //내 게시물 리스트
    @Multipart
    @POST("nboard/nboardlist_mypage/{limit}/{offset}/")
    Call<String> getnboardlist_mypage(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );


    //게시글 좋아요 추가
    @Multipart
    @POST("nboard/likeadd/0/0/")
    Call<String> likeaddbtn_nboard(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //게시글 삭제
    @Multipart
    @POST("nboard/getdatadelete/0/0/")
    Call<String> delete_nboard(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //유저 검색 리스트 가져오기 - 닉네임 검색을 위함.
    @Multipart
    @POST("profile/usersearchlist/{limit}/{offset}/")
    Call<String> getusersearchlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //게시물 댓글 이미지, 문자열 데이터 서버로 전송 - 추가
    @Multipart
    @POST("comment/commentsavebox/0/0/")
    Call<String> commentupload(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //게시물 댓글 이미지, 문자열 데이터 수정
    @Multipart
    @POST("comment/commentupdatebox/0/0/")
    Call<String> commentupload_edit(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //댓글 삭제
    @Multipart
    @POST("comment/getdatadelete/0/0/")
    Call<String> delete_comment(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //댓글 리스트
    @Multipart
    @POST("comment/commentlist/{limit}/{offset}/")
    Call<String> getcommentlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내 댓글 리스트
    @Multipart
    @POST("comment/commentlist_mypage/{limit}/{offset}/")
    Call<String> getcommentlist_mypage(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );


    //게시물 대댓글 이미지, 문자열 데이터 서버로 전송 - 추가
    @Multipart
    @POST("commentnested/commentsavebox/0/0/")
    Call<String> commentnestedupload(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //게시물 대댓글 이미지, 문자열 데이터 수정
    @Multipart
    @POST("commentnested/commentupdatebox/0/0/")
    Call<String> commentnestedupload_edit(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //대댓글 리스트
    @Multipart
    @POST("commentnested/commentlist/{limit}/{offset}/")
    Call<String> getcommentnestedlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내 대댓글 리스트
    @Multipart
    @POST("commentnested/commentlist_mypage/{limit}/{offset}/")
    Call<String> getcommentnestedlist_mypage(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //대댓글 삭제
    @Multipart
    @POST("commentnested/getdatadelete/0/0/")
    Call<String> delete_commentnested(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //댓글 좋아요 추가
    @Multipart
    @POST("commentnested/likeadd/0/0/")
    Call<String> likeaddbtn_commentnested(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //댓글 좋아요 추가
    @Multipart
    @POST("comment/likeadd/0/0/")
    Call<String> likeaddbtn_comment(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //과외문의 리스트
    @Multipart
    @POST("classrequestroom/roomlist/{limit}/{offset}/")
    Call<String> getrequestclasslist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //과외문의 방 생성
    @Multipart
    @POST("classrequestroom/makeroom/0/0/")
    Call<String> makerequestclassroom(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );



    //과외문의 정보 가져오기
    @Multipart
    @POST("classrequestroom/classrequestroominfo/{limit}/{offset}/")
    Call<String> getrequestclassinfo(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //과외문의 방 수정
    @Multipart
    @POST("classrequestroom/editroom/0/0/")
    Call<String> editrequestclassroom(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //과외문의 방 삭제
    @Multipart
    @POST("classrequestroom/deleteroom/0/0/")
    Call<String> deleterequestclassroom(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //과외문의 질문 타입 메세지의 값을 변경
    @Multipart
    @POST("classrequestchat/setquestionmessage/0/0/")
    Call<String> setquestionmessageck(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );




    //과외문의 채팅 리스트
    @Multipart
    @POST("classrequestchat/chatlist/{limit}/{offset}/")
    Call<String> getrequestclassmsg(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );


    //과외문의 방의 채팅 내용 삭제
    @Multipart
    @POST("classrequestchat/deletemsg/0/0/")
    Call<String> deleterequestclassmsg(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

//    //과외문의 방의 채팅 읽음 체크 목록 삭제
//    @Multipart
//    @POST("classrequestchat/deletemsg_readchk/0/0/")
//    Call<String> deletemsgreadchk(
//            @Query("type") int type,
//            @PartMap Map<String, RequestBody> params
//    );


//    //과외문의 방의 fcm 알림 요청하기
//    @Multipart
//    @POST("notification/send/0/0/")
//    Call<String> sendfcmnotification(
//            @Query("type") int type,
//            @PartMap Map<String, RequestBody> params
//    );

//    //fcm 토큰 저장하기
//    @Multipart
//    @POST("notification/savetoken/0/0/")
//    Call<String> savefcm(
//            @Query("type") int type,
//            @PartMap Map<String, RequestBody> params
//    );


    //과외문의 채팅 이미지만 저장
    @Multipart
    @POST("classrequestchat/onlyimgupload/0/0/")
    Call<String> onlyimgupload(
            @Query("type") Integer type,
            @Part ArrayList<MultipartBody.Part> file
    );

    //알림 리스트
    @Multipart
    @POST("alertcontrol/getlist/{limit}/{offset}/")
    Call<String> getalertlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );
    //알림 리스트
    @Multipart
    @POST("alertcontrol/getcount/{limit}/{offset}/")
    Call<String> getalertcount(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //알림 정보 변경
    @Multipart
    @POST("alertcontrol/updatebox/0/0/")
    Call<String> editalert(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );



    //내 과외 방 생성
    @Multipart
    @POST("myclassroom/makeroom/0/0/")
    Call<String> makemyclassroom(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내 과외 방 업데이트
    @Multipart
    @POST("myclassroom/editroom/0/0/")
    Call<String> editmyclassroom(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //내 과외 여러개 생성
    @Multipart
    @POST("myclassroom/makeroom_adduser/0/0/")
    Call<String> makemyclassroom_adduser(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내 과외 방 정보
    @Multipart
    @POST("myclassroom/myclassroominfo/{limit}/{offset}/")
    Call<String> getmyclassroominfo(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내 과외 방 정보 세팅 할때 기본정보만 가져오기
    @Multipart
    @POST("myclassroom/myclassroominfo_set/{limit}/{offset}/")
    Call<String> getmyclassroominfo_set(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );



    //내 과외 방 리스트
    @Multipart
    @POST("myclassuser/roomlist/{limit}/{offset}/")
    Call<String> getmyclassuserlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //과외문의 채팅 리스트
    @Multipart
    @POST("myclasschat/chatlist/{limit}/{offset}/")
    Call<String> getmyclassmsg(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내과외 채팅 이미지만 저장
    @Multipart
    @POST("myclasschat/onlyimgupload/0/0/")
    Call<String> onlyimgupload_myclasschat(
            @Query("type") Integer type,
            @Part ArrayList<MultipartBody.Part> file
    );


    //내과외 방 - 유저 삭제
    @Multipart
    @POST("myclassuser/deleteuser/0/0/")
    Call<String> editmyclassroom_user(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내과외 방 삭제
    @Multipart
    @POST("myclassroom/deleteroom/0/0/")
    Call<String> deletemyclassroom(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //내과외 채팅 삭제
    @Multipart
    @POST("myclasschat/deletemsg/0/0/")
    Call<String> deletemyclasschat(
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );





    //내과외 과제 : 이미지, 문자열 데이터 서버로 전송 - 추가
    @Multipart
    @POST("problem/problemsavebox/0/0/")
    Call<String> problemadd(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //내과외 과제 : 이미지, 문자열 데이터 서버로 전송 - 수정
    @Multipart
    @POST("problem/problemupdatebox/0/0/")
    Call<String> problemedit(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //내과외 과제 리스트
    @Multipart
    @POST("problem/problemlist/{limit}/{offset}/")
    Call<String> getproblemlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );
    //내과외 과제 삭제
    @Multipart
    @POST("problem/getdatadelete/0/0/")
    Call<String> problemdelete(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //내과외 문제 댓글 이미지, 문자열 데이터 서버로 전송 - 추가
    @Multipart
    @POST("problemcomment/commentsavebox/0/0/")
    Call<String> problemcommentadd(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );
    ///내과외 문제 댓글 수정
    @Multipart
    @POST("problemcomment/commentupdatebox/0/0/")
    Call<String> problemcommentedit(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //내과외 문제 댓글 리스트
    @Multipart
    @POST("problemcomment/commentlist/{limit}/{offset}/")
    Call<String> getproblemcommentlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );
    //내과외 문제 댓글 삭제
    @Multipart
    @POST("problemcomment/getdatadelete/0/0/")
    Call<String> delete_problemcomment(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //리뷰 리스트
    @Multipart
    @POST("userreview/reviewlist/{limit}/{offset}/")
    Call<String> getreviewlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );
    //리뷰 추가
    @Multipart
    @POST("userreview/reviewsave/0/0/")
    Call<String> reviewsave(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );
    //리뷰 업데이트
    @Multipart
    @POST("userreview/reviewupdate/0/0/")
    Call<String> reviewupdate(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );
    //리뷰 삭제
    @Multipart
    @POST("userreview/getdatadelete/0/0/")
    Call<String> reviewdelete(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //회의 참여 유저 저장
    @Multipart
    @POST("conferencejoinuser/joinuser/0/0/")
    Call<String> joinuserinconference(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //회의 참여 유저 삭제
    @Multipart
    @POST("conferencejoinuser/deleteuser/0/0/")
    Call<String> deleteuserinconference(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //회의 참여 유저 리스트
    @Multipart
    @POST("conferencejoinuser/joinuserlist/{limit}/{offset}/")
    Call<String> getconferencejoinuserlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //유저가 같은 방에 존재하는지 확인하는 함수
    @Multipart
    @POST("conferencejoinuser/checkuserinroom/{limit}/{offset}/")
    Call<String> checkuserinroom(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );


    //회의 vod 저장
    @Multipart
    @POST("conferencevod/savebox/0/0/")
    Call<String> saveconferencevod(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );

    //회의 vod 리스트
    @Multipart
    @POST("conferencevod/vodlist/{limit}/{offset}/")
    Call<String> getconferencevodlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //회의 문서 저장
    @Multipart
    @POST("conferencedocument/savebox/0/0/")
    Call<String> saveconferencedocument(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params,
            @Part ArrayList<MultipartBody.Part> file
    );


    /*
    *     //게시물 리스트
    @Multipart
    @POST("nboard/nboardlist/{limit}/{offset}/")
    Call<String> getnboardlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );
    * */

    //회의 문서 삭제
    @Multipart
    @POST("conferencedocument/deletebox/0/0/")
    Call<String> deleteconferencedocument(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //회의 문서 리스트
    @Multipart
    @POST("conferencedocument/documentlist/{limit}/{offset}/")
    Call<String> getconferencedocumentlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //결제 리스트
    @Multipart
    @POST("paymentdata/paymentlist/{limit}/{offset}/")
    Call<String> getpaymentlist(
            @Path("limit") int limit,
            @Path("offset") int offset,
            @Query("type") int type,
            @PartMap Map<String, RequestBody> params
    );

    //결제 정보 저장
    @Multipart
    @POST("paymentdata/savebox/0/0/")
    Call<String> savepaymentdata(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //결제 정보 수정
    @Multipart
    @POST("paymentdata/updatebox/0/0/")
    Call<String> updatepaymentdata(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );

    //결제 정보 삭제
    @Multipart
    @POST("paymentdata/deletebox/0/0/")
    Call<String> deletepaymentdata(
            @Query("type") Integer type,
            @PartMap Map<String, RequestBody> params
    );


    //Bootpay 토큰 가져오기
    @Multipart
    @POST("request/token") //https://api.bootpay.co.kr/
    Call<String> getTokenbootpay(
            @PartMap Map<String, RequestBody> params
    );


    //Bootpay 결제 검증하기
    @GET("receipt/{receipt_id}") //https://api.bootpay.co.kr/
    Call<String> receipt_payment(
            @Path("receipt_id") String receipt_id,
            @Header("Authorization") String Authorization
    );

    //Bootpay 결제 취소하기
    @Multipart
    @POST("cancel.json") //https://api.bootpay.co.kr/cancel.json
    Call<String> cancle_payment(
            @PartMap Map<String, RequestBody> params,
            @Header("Authorization") String Authorization
    );

}


