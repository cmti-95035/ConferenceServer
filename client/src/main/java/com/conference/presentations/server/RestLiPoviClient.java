package com.conference.presentations.server;/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

import com.linkedin.common.callback.FutureCallback;
import com.linkedin.common.util.None;
import com.linkedin.data.ByteString;
import com.linkedin.data.DataMap;
import com.linkedin.data.template.SetMode;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.ActionRequest;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.DeleteRequest;
import com.linkedin.restli.client.FindRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.client.UpdateRequest;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.common.IdResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class RestLiPoviClient {
    public static String POVI_AUTHORIZATION_HEADER = "povi-authorization";
    public static String FAKE_POVI_TOKEN = "abc";
    public static String COMMENTID = "comment_id";
    public static String HASH = "abcdefgh";
    public static String KIDS_NAME = "小男孩";
    public static String KIDS_NAME2 = "小さな男の子";
    private static final String IMAGE_FILE="/Users/jianli/Downloads/Wilmington-grass.jpg";
    private static final String CIRCLE_NAME="grandparents";
    private static final String CIRCLE_NAME2="playdatefriends";
    private static final String COMMENTER_EMAIL="J@B.COM";
    private static final String COMMENTER_EMAIL_HASH="5bf102bf686899186abca81462fd2ec29575f7f5";

    private static Random random = new Random();
    private static Connection connect = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    private static List<Comment> comments = new ArrayList<Comment>();

    /**
     * This stand-alone app demos the client-side Pegasus API.
     * To see the demo, run RestLiFortuneServer, then start the client
     */
    public static void main(String[] args) throws Exception {
        // Create an HttpClient and wrap it in an abstraction layer
        final HttpClientFactory http = new HttpClientFactory();
        Client r2Client = new TransportClientAdapter(
                http.getClient(Collections.<String, String>emptyMap()));

        System.out.println(new User().setEmail("abc@example.com"));
        System.out.println(new Comment());
        System.out.println(new Child());
        System.out.println(new ChildImage());
        System.out.println(new VoiceComment());
        System.out.println(new ParentingTip());
        System.out.println(new ParentingTipId());
        System.out.println(new Event());

        // Create a RestClient to talk to the specified server IP:port
        RestClient restClient = new RestClient(r2Client, "http://localhost:8080/");
//        RestClient restClient = new RestClient(r2Client, "http://54.183.228.194:8080/");
//
//        for(int i = 1; i<147; i++){
//            getCommentsShared(restClient, new ParentingTipId().setTipResourceId(2).setTipSequenceId(i), "224dc663df3ee96235644576139b6564647561ec", 0, 5, null);
//        }
//        boolean hasMore = true;
//        int start = 0, count = 5;
//        Long lastCommentId = null;
//        while(hasMore) {
//            Integer last = getCommentsShared(restClient, new ParentingTipId().setTipResourceId(2).setTipSequenceId(100), "1fccc6cc26a948b04e9cfd568457d933d45c491a", start, count, lastCommentId);
//            if(last != null)
//                lastCommentId = Integer.toUnsignedLong(last);
//            if (last != null)
//                System.out.println("lastCommentId: " + last);
//            if(comments.size() != count)
//                hasMore = false;
//            else
//                start += count;
//        }

        long startTime = System.nanoTime();
        String token = createUser(restClient, String.valueOf(random.nextInt()) + "@EmaiL.Com");
        long delta = System.nanoTime() - startTime;
        System.out.println("createUser takes " + delta + " nano seconds");
        System.out.println("token returned from new user: " + token);

        User user = getUser(restClient, token);
        startTime = System.nanoTime();
        delta = System.nanoTime() - startTime;
        System.out.println("getUser takes " + delta + " nano seconds");

//        createUser(restClient, user.getEmail());
        String name = String.valueOf(random.nextDouble());

        startTime = System.nanoTime();
//        updateProfile(restClient, token, user.getEmail(), user.getEmail(), user.getHash(), name, user.getPhone(), user.getAddress(), new Date().getTime());
        updateProfile(restClient, token, user.getEmail(), (random.nextInt()) + "@email.COM", user.getHash(), name, user.getPhone(), user.getAddress(), new Date().getTime());
        delta = System.nanoTime() - startTime;
        System.out.println("updateProfile takes " + delta + " nano seconds");

        startTime = System.nanoTime();
        User newUser = getUser(restClient, token);
        delta = System.nanoTime() - startTime;
        System.out.println("getUser takes " + delta + " nano seconds");

        startTime = System.nanoTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        if (registerNewChild(restClient, newUser.getEmail(), KIDS_NAME, "male", sdf.parse("03/04/2003").getTime(), token)) {
            byte[] imageData = readFromFile(IMAGE_FILE);
            delta = System.nanoTime() - startTime;
            System.out.println("registerNewChild takes " + delta + " nano seconds");

            startTime = System.nanoTime();
            addChildImage(restClient, newUser.getEmail(), KIDS_NAME, imageData, IMAGE_FILE, token);
            delta = System.nanoTime() - startTime;
            System.out.println("addChildImage takes " + delta + " nano seconds");


            startTime = System.nanoTime();
            ChildImage childImage = getChildImage(restClient, newUser.getEmail(), KIDS_NAME, token);
            delta = System.nanoTime() - startTime;
            System.out.println("getChildImage takes " + delta + " nano seconds");

            System.out.println("++++++++++++++++ChildImage file content size: " + childImage.getFileContent().length());
            System.out.println("++++++++++++++++original file size: " + imageData.length);

            Date newdate = sdf.parse("03/04/2010");
            startTime = System.nanoTime();
            if (updateChild(restClient, newUser.getEmail(), KIDS_NAME, KIDS_NAME2, "female", newdate.getTime(), token)){
                delta = System.nanoTime() - startTime;
                System.out.println("updateChild takes " + delta + " nano seconds");

                childImage = getChildImage(restClient, newUser.getEmail(), KIDS_NAME2, token);
                System.out.println("++++++++++++++++++++After child update, ChildImage file content size: " + childImage.getFileContent().length());
            }
            else
                System.out.println("failed to update child profile!");
        } else
            System.out.println("failed to add child");
//        for(int i=1; i<=148;i++){
//            insertComments(restClient,newUser.getEmail(), KIDS_NAME2, 1, token, i, 2, "Blah");
//        }
        startTime = System.nanoTime();
        ParentingTip[] tips = getTips(restClient, token, newUser.getEmail(), null);
        delta = System.nanoTime() - startTime;
        System.out.println("getTips takes " + delta + " nano seconds");

        // try another login, as one registered user might use multiple device with Povi
        String newToken = loginEmail(restClient, newUser.getEmail().toUpperCase(), HASH);
        startTime = System.nanoTime();
        logout(restClient, token);
        delta = System.nanoTime() - startTime;
        System.out.println("logout takes " + delta + " nano seconds");

//        loginEmail(restClient, user.getEmail(), HASH);
        token = loginEmail(restClient, newUser.getEmail().toUpperCase(), HASH);
        delta = System.nanoTime() - startTime;
        System.out.println("loginEmail takes " + delta + " nano seconds");

        System.out.println("weblink: " + getWebLink(restClient, token));
        System.out.println("obtain weblink: " + obtainWebLink(restClient, token));
//        resetPassword(restClient, user.getEmail());

//        sendInviteEmail(restClient, newUser.getEmail(), "cmti95035@gmail.com");

        // circle and circle members
        Long circleId = addCircle(restClient, token, newUser.getEmail(), CIRCLE_NAME);
        Long circleMemberId = addCircleMember(restClient, token, random.nextInt() + "email.com", String.valueOf(random.nextInt()), circleId);
        addCircleMember(restClient, token, COMMENTER_EMAIL, "fan", circleId);
        updateCircleMember(restClient, token, random.nextInt() + "email.com", String.valueOf(random.nextInt()), circleMemberId, circleId);
        String memberEmail = random.nextInt() + "email.com";
        String memberName = String.valueOf(random.nextInt());
        System.out.println("\n Create new circle member with " + memberEmail + " , " + memberName);
        Long circleMemberId2 = addCircleMember(restClient, token, memberEmail, memberName, circleId);
        CircleMember circleMember = getCircleMember(restClient, token, circleMemberId2);
        CircleMember[] circleMembers = getCircleMembers(restClient, token, circleId);
        Long circleId2 = addCircle(restClient, token, newUser.getEmail(), CIRCLE_NAME2);
        getCircle(restClient, token, circleId2);
        updateCircle(restClient, token, newUser.getEmail(), CIRCLE_NAME2 + "_2", circleId2);
        getCircles(restClient, token, newUser.getEmail());
        deleteCircleMember(restClient, token, circleId, circleMemberId2);
        getCircle(restClient, token, circleId);
        deleteCircle(restClient, token, circleId2);
        getCircles(restClient, token, newUser.getEmail());

        if(tips != null && tips.length > 0) {
            System.out.println("getTips returns:..............................");
            for(ParentingTip tip : tips){
                System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size()>0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer")+ " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
            }
            startTime = System.nanoTime();
            // assume this is from another device with a different token from the latest login it should still work
            insertComments(restClient, newUser.getEmail(), KIDS_NAME2, 1, newToken, tips[0].getTipId().getTipSequenceId(), tips[0].getTipId().getTipResourceId(), tips[0].getTipDetail());
            delta = System.nanoTime() - startTime;
            System.out.println("insertComments takes " + delta + " nano seconds");

            // insert another comment
            insertComments(restClient, newUser.getEmail(), KIDS_NAME2, 1, newToken, tips[0].getTipId().getTipSequenceId(), tips[0].getTipId().getTipResourceId(), tips[0].getTipDetail());
            startTime = System.nanoTime();
            Integer lastCommentId = getCommentsPaged(restClient, newUser.getEmail(), KIDS_NAME2, newToken, 0, 5, null);
            delta = System.nanoTime() - startTime;
            System.out.println("getCommentsPaged takes " + delta + " nano seconds");

            if(comments != null && comments.size() > 0) {
                Comment comment = comments.get(0);

                // use another login and there shouldn't be any shared content yet so it should return empty results
                String token2 = loginEmail(restClient, COMMENTER_EMAIL, COMMENTER_EMAIL_HASH);
                getCommentsShared(restClient, new ParentingTipId().setTipResourceId(comment.getResourceId()).setTipSequenceId(comment.getTipId()), token2, 0, 5, null);

                // re-populate the comments list
                getCommentsPaged(restClient, newUser.getEmail(), KIDS_NAME2, newToken, 0, 5, null);

                startTime = System.nanoTime();
                // make this comment public
                updateComment(restClient, comment.getUserId(), comment.getChildName(), comment.getTipId(), comment.getTipString(), comment.getTimestamp(), "new comment", comment.isLikeStatus(), token, true, 6);
                delta = System.nanoTime() - startTime;
                System.out.println("updateComment takes " + delta + " nano seconds");

                // before adding any beat comments
                getBeatComments(restClient, newToken, comment.getCommentId());
                getBeatCommentsWithLikeStatus(restClient, newToken, comment.getCommentId());

                // share the other comment to circle, which includes the COMMENTER_EMAIL
                addCommentSharing(restClient, token, comments.get(1).getCommentId(), circleId);

                // set like to this comment
                setLikeStatus(restClient, comments.get(1).getCommentId(), false, true, token);
                setLikeStatus(restClient, comment.getCommentId(), false, false, token);

                // try to get the comments again and the update should take effect
                getCommentsPaged(restClient, newUser.getEmail(), KIDS_NAME2, newToken, 0, 5, null);

                getCommentsShared(restClient, new ParentingTipId().setTipResourceId(comment.getResourceId()).setTipSequenceId(comment.getTipId()), newToken, 0, 5, null);

                Long beatCommentId = addBeatComment(restClient, newToken, comment.getCommentId(), COMMENTER_EMAIL, "blah");
                Long beatCommentId2 = addBeatComment(restClient, newToken, comment.getCommentId(), COMMENTER_EMAIL, "blah2");
                getBeatComments(restClient, newToken, comment.getCommentId());
                getBeatCommentsWithLikeStatus(restClient, newToken, comment.getCommentId());

                // try to remove like
                setLikeStatus(restClient, comment.getCommentId(), true, false, token);

//                addBeatLike(restClient, newToken, comment.getCommentId());
                setLikeStatus(restClient, comment.getCommentId(), true, true, token);
                getBeatCommentsWithLikeStatus(restClient, newToken, comment.getCommentId());

                setLikeStatus(restClient, comment.getCommentId(), true, false, token);

                deleteBeatComment(restClient, newToken, beatCommentId);
                getBeatComments(restClient, newToken, comment.getCommentId());
                getBeatCommentsWithLikeStatus(restClient, token, comment.getCommentId());

                getCommentsPaged(restClient, newUser.getEmail(), KIDS_NAME2, newToken, 0, 5, null);
                getCommentsShared(restClient, new ParentingTipId().setTipResourceId(comment.getResourceId()).setTipSequenceId(comment.getTipId()), newToken, 0, 5, null);

                setLikeStatus(restClient, comment.getCommentId(), true, false, token);
                // try to get the shared comments for COMMENTER_EMAIL for the given tip, expect to get 2 of them, one from the public and one from circle
                Integer lastCid = getCommentsShared(restClient, new ParentingTipId().setTipResourceId(comment.getResourceId()).setTipSequenceId(comment.getTipId()), token2, 0, 1, null);
                getCommentsShared(restClient, new ParentingTipId().setTipResourceId(comment.getResourceId()).setTipSequenceId(comment.getTipId()), token2, 1, 5, lastCid.longValue());
                setLikeStatus(restClient, comment.getCommentId(), true, true, token2);
                getBeatCommentsWithLikeStatus(restClient, token2, comment.getCommentId());
            }
        }
//        lastCommentId = getCommentsPaged(restClient, 5, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 2nd page: " + ((lastCommentId == null) ? "null" : lastCommentId));
//        lastCommentId = getCommentsPaged(restClient, 10, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 3rd page: " + ((lastCommentId == null) ? "null" : lastCommentId));
//        lastCommentId = getCommentsPaged(restClient, 15, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 3rd page: " + ((lastCommentId == null) ? "null" : lastCommentId));
//        lastCommentId = getCommentsPaged(restClient, 20, 5, lastCommentId.longValue());
//        System.out.println("lastCommentId after 4th page: " + ((lastCommentId == null) ? "null" : lastCommentId));

        for(int i=0; i<5;i++) {
            tips = getRefreshTips(restClient, token, newUser.getEmail());
            if (tips != null && tips.length > 0) {
                System.out.println("getRefreshTips returns:-----------------------------------");
                for (ParentingTip tip : tips) {
                    System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size() > 0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer") + " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
                }
            }
        }

        tips = getTipsSelectedDay(restClient, token, newUser.getEmail(), "07/15/2015");
        System.out.println("getTipsSelectedDay returns:================================");
        for (ParentingTip tip : tips) {
            System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size() > 0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer") + " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
        }

        tips = getTips(restClient, token, newUser.getEmail(), true);
        if(tips != null && tips.length > 0) {
            System.out.println("call getTips again it returns:*********************");
            for (ParentingTip tip : tips) {
                System.out.println("ageGroup: " + tip.getTipAgeGroups() + " resourceId: " + tip.getTipId().getTipResourceId() + ", sequenceId: " + tip.getTipId().getTipSequenceId() + ", content: " + tip.getTipDetail() + ", category: " + tip.getTipCategory() + " , sampleAnswers: " + (tip.getSampleAnswers().size() > 0 ? tip.getSampleAnswers().get(0).getAnswerString() : "none sample answer") + " like count: " + tip.getLikeCount() + " comment count: " + tip.getCommentCount());
            }
        }

        createEvent(restClient, token, newUser.getEmail(), "event triggered from notification", EventType.NOTIFICATION);

        restClient.shutdown(new FutureCallback<None>());
        http.shutdown(new FutureCallback<None>());
    }

    public static Long createEvent(RestClient restClient, String token, String userId, String details, EventType eventType)
    {
        try{
            PoviEvent event = new PoviEvent().setEmail(userId).setEventType(eventType).setDetails(details);
            CreateIdRequest<Long, PoviEvent> createIdRequest = new PoviEventRequestBuilders().create().input(event).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            ResponseFuture<IdResponse<Long>> responseFuture = restClient.sendRequest(createIdRequest);
            Response<IdResponse<Long>> response = responseFuture.getResponse();

            return response.getEntity().getId();
        }catch (RemoteInvocationException ex)
        {
            System.out.println("Encountered error when fetching tips from selected days " + ex.getMessage() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            for(StackTraceElement element : ex.getStackTrace()){
                System.out.println(element);
            }
        }

        return null;
    }
    public static ParentingTip[] getTipsSelectedDay(RestClient restClient, String token, String userId, String dateStr)
    {
        try
        {
            ActionRequest<ParentingTipArray> actionRequest = new ParentingTipRequestBuilders().actionGetTipsSelectedDay().userIdParam(userId).dateStrParam(dateStr).countParam(3).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            System.out.println("get tip selected day request: " + actionRequest);
            ResponseFuture<ParentingTipArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<ParentingTipArray> response = responseFuture.getResponse();

            return response.getEntity().toArray(new ParentingTip[3]);
        }catch (RemoteInvocationException ex)
        {
            System.out.println("Encountered error when fetching tips from selected days " + ex.getMessage() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            for(StackTraceElement element : ex.getStackTrace()){
                System.out.println(element);
            }
        }

        return new ParentingTip[0];
    }

    public static ParentingTip[] getRefreshTips(RestClient restClient, String token, String userId)
    {
        try
        {
            ActionRequest<ParentingTipArray> actionRequest = new ParentingTipRequestBuilders().actionGetRefreshTips().userIdParam(userId).countParam(3).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            System.out.println("get refresh tip request: " + actionRequest);
            ResponseFuture<ParentingTipArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<ParentingTipArray> response = responseFuture.getResponse();
            return response.getEntity().toArray(new ParentingTip[3]);
        }catch (RemoteInvocationException ex)
        {
            System.out.println("Encountered error getting refresh tips: " + ex.getMessage() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ex.printStackTrace();
        }
        return new ParentingTip[0];
    }

    public static String getWebLink(RestClient restClient, String token){
        ParentingTipDoGetWebLinkRequestBuilder requestBuilder = new ParentingTipRequestBuilders().actionGetWebLink();
        ActionRequest<String> actionRequest = requestBuilder.addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        System.out.println("get web link request: " + actionRequest);
        final ResponseFuture<String> responseFuture = restClient.sendRequest(actionRequest);
        try
        {
            return responseFuture.getResponse().getEntity();
        }catch (RemoteInvocationException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static WebLink obtainWebLink(RestClient restClient, String token){
        ParentingTipDoObtainWebLinkRequestBuilder requestBuilder = new ParentingTipRequestBuilders().actionObtainWebLink();
        ActionRequest<WebLink> actionRequest = requestBuilder.addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        System.out.println("obtain weblink request: " + actionRequest);
        final ResponseFuture<WebLink> responseFuture = restClient.sendRequest(actionRequest);
        try
        {
            return responseFuture.getResponse().getEntity();
        }catch (RemoteInvocationException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean resetPassword(RestClient restClient, String email){
        PoviActionsDoResetPasswordRequestBuilder poviActionsDoResetPasswordRequestBuilder = new PoviActionsRequestBuilders().actionResetPassword();
        ActionRequest<Boolean> validateReq = poviActionsDoResetPasswordRequestBuilder.emailParam(email).build();
        System.out.println("reset password request: " + validateReq);
        final ResponseFuture<Boolean> getFuture = restClient.sendRequest(validateReq);
        final Response<Boolean> resp;
        try {
            resp = getFuture.getResponse();
            if(resp.getEntity())
                System.out.println("successfully reset password");
            else
                System.out.println("failed to reset password");

            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendInviteEmail(RestClient restClient, String invitorEmail, String inviteeEmail){
        PoviActionsDoSendInviteEmailRequestBuilder poviActionsDoSendInviteEmailRequestBuilder = new PoviActionsRequestBuilders().actionSendInviteEmail();
        ActionRequest<Boolean> validateReq = poviActionsDoSendInviteEmailRequestBuilder.inviteeParam(inviteeEmail).invitorParam(invitorEmail).build();
        System.out.println("sendInviteEmail request: " + validateReq);
        final ResponseFuture<Boolean> getFuture = restClient.sendRequest(validateReq);
        final Response<Boolean> resp;
        try {
            resp = getFuture.getResponse();
            if(resp.getEntity())
                System.out.println("successfully sent invite email to " + inviteeEmail);
            else
                System.out.println("failed to sent invite email to " + inviteeEmail);

            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ParentingTip getTip(RestClient restClient, ParentingTipId tipId, String token){
        ParentingTipGetRequestBuilder parentingTipGetRequestBuilder = new ParentingTipRequestBuilders().get();
        GetRequest<ParentingTip> getRequest = parentingTipGetRequestBuilder.id(new ComplexResourceKey<ParentingTipId, Account>(tipId, new Account().setAccountId(123).setName("lll").setIdentity(new Identity()))).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        System.out.println("get tip request: " + getRequest);
        final ResponseFuture<ParentingTip> responseFuture = restClient.sendRequest(getRequest);
        try {
            final Response<ParentingTip> response = responseFuture.getResponse();

            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static byte[] readFromFile(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean registerNewChild(RestClient restClient, final String userEmail, final String childName, final String gender, long birthdate, String token) {
        // Construct a request for the specified fortune
        ChildCreateRequestBuilder rb = new ChildRequestBuilders().create();
        CreateIdRequest<ComplexResourceKey<ChildId, ChildId>, Child> registerReq = rb.input(new Child().setUser_id(userEmail).setName(childName).setBirthdate(birthdate).setGender(gender)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        System.out.println("register kid request: " + registerReq);
        // Send the request and wait for a response
        final ResponseFuture<IdResponse<ComplexResourceKey<ChildId, ChildId>>> getFuture = restClient.sendRequest(registerReq);
        final Response<IdResponse<ComplexResourceKey<ChildId, ChildId>>> resp;
        try {
            resp = getFuture.getResponse();
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateChild(RestClient restClient, final String userEmail, final String origName, final String childName, final String gender, long birthdate, String token) {
        // Construct a request for the specified fortune
        ChildId keyId = new ChildId().setUser_id(userEmail).setChild_Id(origName);
        ComplexResourceKey<ChildId, ChildId> key = new ComplexResourceKey<ChildId, ChildId>(keyId, keyId);
        ChildUpdateRequestBuilder rb = new ChildRequestBuilders().update();
        UpdateRequest<Child> request = rb.id(key)
                .input(new Child().setBirthdate(birthdate).setGender(gender).setUser_id(userEmail).setName(childName)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        System.out.println("update kid request: " + request);
        // Send the request and wait for a response
        final ResponseFuture getFuture = restClient.sendRequest(rb);
        final Response resp;
        try {
            resp = getFuture.getResponse();
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean logout(RestClient restClient, final String token) {
        PoviActionsDoLogoutRequestBuilder rb = new PoviActionsRequestBuilders().actionLogout().tokenParam(token);
        ActionRequest<Boolean> validateReq = rb.addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        System.out.println("logout request: " + validateReq);
        final ResponseFuture<Boolean> getFuture = restClient.sendRequest(validateReq);
        final Response<Boolean> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("successfully logged out");
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String loginEmail(RestClient restClient, final String email, final String hash) {
        // Construct a request for the specified fortune
        PoviActionsDoLoginEmailRequestBuilder rb = new PoviActionsRequestBuilders().actionLoginEmail().emailParam(email).hashParam(hash);
        ActionRequest<String> registerReq = rb.build();

        System.out.println("login email request: " + registerReq);
        // Send the request and wait for a response
        final ResponseFuture<String> getFuture = restClient.sendRequest(registerReq);
        final Response<String> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("successfully logged in");
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ParentingTip[] getTips(RestClient restClient, String token, String userId, Boolean isAndroid) {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String dateStr = sdf.format(date);
            ParentingTipDoGetTipsRequestBuilder parentingTipDoGetTipsRequestBuilder = new ParentingTipRequestBuilders().actionGetTips();
            if(isAndroid != null)
                parentingTipDoGetTipsRequestBuilder.isAndroidParam(isAndroid);
            ActionRequest<ParentingTipArray> actionRequest = parentingTipDoGetTipsRequestBuilder.userIdParam(userId).dateStrParam(dateStr).countParam(3).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
            System.out.println("get tips request: " + actionRequest);
            ResponseFuture<ParentingTipArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<ParentingTipArray> response = responseFuture.getResponse();

            return response.getEntity().toArray(new ParentingTip[3]);
        } catch (RemoteInvocationException ex) {
//            System.out.println("Encountered error doing registerAccount: " + ex.getMessage() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        return new ParentingTip[0];
    }

    //    public static List<Comment> getComments()
    public static void insertComments(RestClient restClient, String email, String childName, int count, String token, int tipId, int resourceId, String tipString) throws Exception {
        for (int i = 0; i < count; i++) {
            createComment(restClient, email, childName, tipId, resourceId, new Date().getTime(), "comment" + i, false, tipString, token);
            Thread.sleep(1);
        }
    }

    public static void insertVoiceComments(RestClient restClient, String email, String childName, byte[] data, long timestamp, String token) throws Exception {
        createVoiceComment(restClient, email, childName, timestamp, "abc", data, token);
    }

    public static boolean addChildImage(RestClient restClient, String email, String childName, byte[] data, String fileName, String token){
        ChildImageCreateRequestBuilder childImageCreateRequestBuilder = new ChildImageRequestBuilders().create();
        CreateIdRequest<ComplexResourceKey<ChildId, ChildId>, ChildImage> createReq = childImageCreateRequestBuilder.input(new ChildImage()
        .setFileName(fileName).setFileContent(ByteString.copy(data)).setEmail(email).setChildName(childName)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        final ResponseFuture<IdResponse<ComplexResourceKey<ChildId, ChildId>>> getFuture =
                restClient.sendRequest(createReq);

        // If you get some response, then the comment has been entered in the table
        final Response<IdResponse<ComplexResourceKey<ChildId, ChildId>>> resp;
        try {
            resp = getFuture.getResponse(30000, TimeUnit.MILLISECONDS);
           System.out.println("Successfully added image!");
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static ChildImage getChildImage(RestClient restClient, String email, String childName, String token){
        ChildImageGetRequestBuilder childImageGetRequestBuilder = new ChildImageRequestBuilders().get();
        ChildId keyId = new ChildId().setUser_id(email).setChild_Id(childName);
        ComplexResourceKey<ChildId, ChildId> key = new ComplexResourceKey<ChildId, ChildId>(keyId, keyId);
        GetRequest<ChildImage> getRequest = childImageGetRequestBuilder.id(key).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        final ResponseFuture<ChildImage> getFuture = restClient.sendRequest(getRequest);
        try{
            final Response<ChildImage> getResponse = getFuture.getResponse();
            return getResponse.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateComment(RestClient restClient, final String userEmail, final String childName,
                                        final int tipId, final String tipString,
                                        final long timestamp, final String commentText,
                                        final boolean likeStatus, final String token, boolean isPublic, int contentGroups) {

        // Setting up the commentId key which is used to update the record
        CommentId keyId = new CommentId().setUser_id(userEmail).
                setChild_Id(childName).
                setTimestamp(timestamp);

        ComplexResourceKey<CommentId, CommentId> key = new ComplexResourceKey<CommentId, CommentId>(keyId, keyId);

        // Creating the Comment Delete request builder
        TipCommentUpdateRequestBuilder update_requestBuilder =
                new TipCommentRequestBuilders().update();

        UpdateRequest updateReq = update_requestBuilder.id(key).input(new Comment().
                setUserId(userEmail).
                setTipId(tipId).
                setTipString(tipString).
                setTimestamp(timestamp).
                setCommentText(commentText).
                setLikeStatus(likeStatus).
                setChildName(childName).
                setIsPublic(isPublic).
                setContentGroups(contentGroups)).
                addHeader(POVI_AUTHORIZATION_HEADER,
                        token).build();

        System.out.println("update comment request: " + updateReq);
        // Send the request and wait for a response
        final ResponseFuture getFuture = restClient.sendRequest(updateReq);

        // If you get an OK response, then the comment has been updated in the table
        final Response resp;
        try {
            resp = getFuture.getResponse();

//            System.out.println("status: " + resp.getStatus());
            if (resp.getStatus() == HttpStatus.S_200_OK.getCode()) {
                return true;
            }
        } catch (RemoteInvocationException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static void deleteAllComments(String userId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Setup the connection with the DB
        try {
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/povi_schema?"
                            + "user=povi&password=povi");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement = connect.prepareStatement("select `hash` from `povi_schema`.`users` where `email`=?;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            preparedStatement.setString(1, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Close connection
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connect != null) {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static Integer getCommentsPaged(RestClient restClient, String email, String childName, String token, int start, int count, Long lastTimestamp) {
        TipCommentFindByGetCommentsPagedRequestBuilder tipCommentFindByGetCommentsPagedRequestBuilder = new TipCommentRequestBuilders().findByGetCommentsPaged();

        if (lastTimestamp != null)
            tipCommentFindByGetCommentsPagedRequestBuilder.lastTimestampParam(lastTimestamp);

        FindRequest<Comment> findRequest = tipCommentFindByGetCommentsPagedRequestBuilder.paginate(start, count).userIdParam(email).childNameParam(childName).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        System.out.println("getCommentsPaged request: " + findRequest);
        ResponseFuture<CollectionResponse<Comment>> getFutureComments = restClient.
                sendRequest(findRequest);

        // Start collecting the results of the response in commentResp
        Response<CollectionResponse<Comment>> commentResp;
        try {
            commentResp = getFutureComments.getResponse();
            CollectionResponse<Comment> response = commentResp.getEntity();
            DataMap dataMap = response.getMetadataRaw();
            comments = response.getElements();
            System.out.println("\nRetrieved comments paged---------------------------------------" + (comments == null ? "null" : comments.size()));
            for (Comment comment : comments)
                System.out.println(comment.toString());

            if (dataMap != null && dataMap.containsKey(COMMENTID))
                return dataMap.getInteger(COMMENTID);
            else
                return null;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Integer getCommentsShared(RestClient restClient, ParentingTipId tipId, String token, int start, int count, Long lastCommentId) {
        TipCommentFindByGetCommentsSharedRequestBuilder tipBuilder = new TipCommentRequestBuilders().findByGetCommentsShared();

        if (lastCommentId != null)
            tipBuilder.lastCommentIdParam(lastCommentId);

        FindRequest<Comment> findRequest = tipBuilder.tipIdParam(tipId).paginate(start, count).addHeader(POVI_AUTHORIZATION_HEADER, token).build();
        System.out.println("getCommentsShared request: " + findRequest);
        ResponseFuture<CollectionResponse<Comment>> getFutureComments = restClient.
                sendRequest(findRequest);

        // Start collecting the results of the response in commentResp
        Response<CollectionResponse<Comment>> commentResp;
        try {
            commentResp = getFutureComments.getResponse();
            CollectionResponse<Comment> response = commentResp.getEntity();
            DataMap dataMap = response.getMetadataRaw();
            comments = response.getElements();
            System.out.println("\nRetrieved shared comments---------------------------------------");
            for (Comment comment : comments)
                System.out.println(comment.toString());

            if (dataMap != null && dataMap.containsKey(COMMENTID))
                return dataMap.getInteger(COMMENTID);
            else
                return null;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean createComment(RestClient restClient, final String userEmail, final String childName,
                                        final int tipId, final int resourceId, final long timestamp,
                                        final String commentText, final boolean likeStatus, final String tipString, final String token) {

        // Initialize a create comment request builder
        TipCommentCreateRequestBuilder create_requestBuilder =
                new TipCommentRequestBuilders().create();


        // Initialize a create request
        CreateIdRequest<ComplexResourceKey<CommentId, CommentId>, Comment> createReq =
                create_requestBuilder.input(new Comment().setTipString(tipString).
                        setUserId(userEmail).
                        setTipId(tipId).
                        setResourceId(resourceId).
                        setTimestamp(timestamp).
                        setCommentText(commentText).
                        setLikeStatus(likeStatus).
                        setChildName(childName)).
                        addHeader(POVI_AUTHORIZATION_HEADER,
                                token).build();

        System.out.println("create comment request: " + createReq);
        // Send the request and wait for a response
        final ResponseFuture<IdResponse<ComplexResourceKey<CommentId, CommentId>>> getFuture =
                restClient.sendRequest(createReq);

        // If you get some response, then the comment has been entered in the table
        final Response<IdResponse<ComplexResourceKey<CommentId, CommentId>>> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("commentId: " + resp.getEntity().getId().getKey().getComment_id());
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createVoiceComment(RestClient restClient, final String userEmail, final String childName,
                                        final long timestamp,
                                        final String fileName, final byte[] content, final String token) {

        VoiceCommentCreateRequestBuilder voiceCommentCreateRequestBuilder = new VoiceCommentRequestBuilders().create();

        // Initialize a create request
        CreateIdRequest<ComplexResourceKey<CommentId, CommentId>, VoiceComment> createReq =
                voiceCommentCreateRequestBuilder.input(new VoiceComment().
                        setEmail(userEmail).
                        setTimestamp(timestamp).
                        setFileContent(ByteString.copy(content)).
                        setChildName(childName).setFileName("1.png")).
                        addHeader(POVI_AUTHORIZATION_HEADER,
                                token).build();

        // Send the request and wait for a response
        final ResponseFuture<IdResponse<ComplexResourceKey<CommentId, CommentId>>> getFuture =
                restClient.sendRequest(createReq);

        // If you get some response, then the comment has been entered in the table
        final Response<IdResponse<ComplexResourceKey<CommentId, CommentId>>> resp;
        try {
            resp = getFuture.getResponse();
//            System.out.println("Successfully added file!");
            return true;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String createUser(RestClient restClient, String email) {
        try {
            // Construct a request for the specified fortune
            UserCreateRequestBuilder rb = new UserRequestBuilders().create();
            CreateIdRequest<String, User> registerReq = rb.input(new User().setEmail(email).setHash(HASH).setName("Bepi Caena").setPhone("555555555").setBirthdate(0)).build();

            System.out.println("create user request: " + registerReq);
            // Send the request and wait for a response
            final ResponseFuture<IdResponse<String>> getFuture = restClient.sendRequest(registerReq);
            final Response<IdResponse<String>> resp = getFuture.getResponse();

            String token = resp.getEntity().getId();
            // Print the response
            System.out.println("create user returns: " + token);
            return token;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            System.out.println("create user failed!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static User getUser(RestClient restClient, String token) {
        GetRequest<User> getReq = new UserRequestBuilders().get().id(token).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        System.out.println("get user request: " + getReq);
        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
            User user = resp.getEntity();
//            System.out.println("email: " + user.getEmail());
//            System.out.println("hash: " + user.getHash());

            return user;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateProfile(RestClient restClient, final String token, final String oldEmail, final String email, final String hash, final String name, final String phone, final String address, long birthdate) {
        // Creating the profile update request builder
        UserUpdateRequestBuilder updateRequestBuilder = new UserRequestBuilders().update();

        UpdateRequest updateReq = updateRequestBuilder.id(oldEmail).input(new User().
                setEmail(email)
                .setHash(hash)
                .setName(name)
                .setPhone(phone)
                .setAddress(address, SetMode.IGNORE_NULL)
                .setNickName("old man", SetMode.IGNORE_NULL)
                .setBirthdate(birthdate, SetMode.IGNORE_NULL))
                .addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        System.out.println("update profile request: " + updateReq.getResourceSpec());
        System.out.println("update profile request: " + updateReq.toSecureString());
        // Send the request and wait for a response
        final ResponseFuture getFuture = restClient.sendRequest(updateReq);

        // If you get an OK response, then the comment has been updated in the table
        final Response resp;
        try {
            resp = getFuture.getResponse();
            if (resp.getStatus() == HttpStatus.S_200_OK.getCode()) {
                return true;
            }
        } catch (RemoteInvocationException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static void deleteUser(RestClient restClient, String token) {
        try {
            UserDeleteRequestBuilder rb = new UserRequestBuilders().delete();
            DeleteRequest<User> deleteRequest = rb.id(token).build();

            final ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            final Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println(response.getStatus());
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }
    }

    public static User getUserProfile(RestClient restClient, final String token) {
        // Construct a request for the specified fortune
        UserGetRequestBuilder rb = new UserRequestBuilders().get().id(token);
        GetRequest<User> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Child> getChildren(RestClient restClient, final String token) {
        if (token == null)
            return null;

        // Get current user from token
        // Construct a request for the specified fortune
        UserGetRequestBuilder rb = new UserRequestBuilders().get().id(token);
        GetRequest<User> getReq = rb.addHeader(POVI_AUTHORIZATION_HEADER, FAKE_POVI_TOKEN).build();

        System.out.println("get children request: " + getReq);
        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }

        User user = resp.getEntity();

        ChildFindByGetChildrenRequestBuilder crb = new ChildRequestBuilders().findByGetChildren().user_idParam(user.getEmail());
        FindRequest<Child> findReq = crb.addHeader(POVI_AUTHORIZATION_HEADER, FAKE_POVI_TOKEN).build();
        ResponseFuture<CollectionResponse<Child>> getFutureChildren = restClient.sendRequest(findReq);
        Response<CollectionResponse<Child>> childResp;
        try {
            childResp = getFutureChildren.getResponse();
            return childResp.getEntity().getElements();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long addCircle(RestClient restClient, final String token, final String ownerEmail, final String circleName) {
        CircleCreateRequestBuilder circleCreateRequestBuilder = new CircleRequestBuilders().create();

        CreateIdRequest<Long, Circle> createIdRequest = circleCreateRequestBuilder.input(new Circle().setCircleName(circleName).setOwnerEmail(ownerEmail)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<IdResponse<Long>> responseFuture = restClient.sendRequest(createIdRequest);
            Response<IdResponse<Long>> response = responseFuture.getResponse();

            Long circleId = response.getEntity().getId();
            System.out.println("\naddCircle generates circleId: " + circleId);

            return circleId;
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long addCircleMember(RestClient restClient, final String token, final String memberEmail, final String memberName, final Long circleId) {
        CircleDoAddCircleMemberRequestBuilder circleDoAddCircleMemberRequestBuilder = new CircleRequestBuilders().actionAddCircleMember();

        ActionRequest<Long> actionRequest = circleDoAddCircleMemberRequestBuilder.circleMemberParam(new CircleMember().setMemberName(memberName).setMemberEmail(memberEmail).setCircleId(circleId)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Long> responseFuture = restClient.sendRequest(actionRequest);
            Response<Long> response = responseFuture.getResponse();

            Long circleMemberId = response.getEntity();
            System.out.println("\naddCircleMember generates memberId: " + circleMemberId);

            return circleMemberId;
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean updateCircle(RestClient restClient, final String token, final String ownerEmail, final String circleName, final Long circleId){
        CircleUpdateRequestBuilder circleUpdateRequestBuilder = new CircleRequestBuilders().update();
        UpdateRequest<Circle> updateRequest = circleUpdateRequestBuilder.id(circleId).input(new Circle().setCircleName(circleName).setOwnerEmail(ownerEmail)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(updateRequest);
            Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println("\n updateCircle " + (response.getStatus() == HttpStatus.S_200_OK.getCode() ? "succeeded" : "failed"));
            return response.getStatus() == HttpStatus.S_200_OK.getCode() ? true : false;
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean updateCircleMember(RestClient restClient, final String token, final String memberEmail, final String memberName, final Long memberId, Long circleId){
        CircleDoUpdateCircleMemberRequestBuilder circleDoUpdateCircleMemberRequestBuilder = new CircleRequestBuilders().actionUpdateCircleMember();
        ActionRequest<Boolean> actionRequest = circleDoUpdateCircleMemberRequestBuilder.memberIdParam(memberId).circleMemberParam(new CircleMember().setMemberName(memberName).setMemberEmail(memberEmail).setCircleId(circleId)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Boolean> responseFuture = restClient.sendRequest(actionRequest);
            Response<Boolean> response = responseFuture.getResponse();

            System.out.println("\n updateCircleMember " + (response.getEntity() ? "succeeded" : "failed"));
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean deleteCircle(RestClient restClient, final String token, final Long circleId){
        CircleDeleteRequestBuilder circleDeleteRequestBuilder = new CircleRequestBuilders().delete();
        DeleteRequest<Circle> deleteRequest = circleDeleteRequestBuilder.id(circleId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println("\n deleteCircle " + (response.getStatus() == HttpStatus.S_200_OK.getCode() ? "succeeded" : "failed"));
            return response.getStatus() == HttpStatus.S_200_OK.getCode() ? true : false;
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean deleteCircleMember(RestClient restClient, final String token, final Long circleId, final Long memberId){
        CircleDoDeleteCircleMemberRequestBuilder circleDoDeleteCircleMemberRequestBuilder = new CircleRequestBuilders().actionDeleteCircleMember();
        ActionRequest<Boolean> actionRequest = circleDoDeleteCircleMemberRequestBuilder.circleMemberParam(new CircleMember().setCircleId(circleId).setMemberId(memberId)).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Boolean> responseFuture = restClient.sendRequest(actionRequest);
            Response<Boolean> response = responseFuture.getResponse();

            System.out.println("\n deleteCircleMember " + (response.getEntity() ? "succeeded" : "failed"));
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Circle getCircle(RestClient restClient, final String token, final Long circleId){
        CircleGetRequestBuilder circleGetRequestBuilder = new CircleRequestBuilders().get();
        GetRequest<Circle> getRequest = circleGetRequestBuilder.id(circleId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Circle> responseFuture = restClient.sendRequest(getRequest);
            Response<Circle> response = responseFuture.getResponse();

            System.out.println("\n getCircle returns: " + response.getEntity());
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Circle[] getCircles(RestClient restClient, final String token, final String ownerEmail){
        CircleDoGetCirclesRequestBuilder circleDoGetCirclesRequestBuilder = new CircleRequestBuilders().actionGetCircles();
        ActionRequest<CircleArray> actionRequest = circleDoGetCirclesRequestBuilder.ownerEmailParam(ownerEmail).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<CircleArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<CircleArray> response = responseFuture.getResponse();

            System.out.println("\n getCircles returns: " + response.getEntity().size());
            return response.getEntity().toArray(new Circle[response.getEntity().size()]);
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CircleMember getCircleMember(RestClient restClient, final String token, final Long memberId){
        CircleDoGetCircleMemberRequestBuilder circleDoGetCircleMemberRequestBuilder = new CircleRequestBuilders().actionGetCircleMember();
        ActionRequest<CircleMember> actionRequest = circleDoGetCircleMemberRequestBuilder.memberIdParam(memberId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<CircleMember> responseFuture = restClient.sendRequest(actionRequest);
            Response<CircleMember> response = responseFuture.getResponse();

            System.out.println("\n getCircleMember returns: " + response.getEntity());
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CircleMember[] getCircleMembers(RestClient restClient, final String token, final Long circleId){
        CircleDoGetCircleMembersRequestBuilder circleDoGetCircleMembersRequestBuilder = new CircleRequestBuilders().actionGetCircleMembers();
        ActionRequest<CircleMemberArray> actionRequest = circleDoGetCircleMembersRequestBuilder.circleIdParam(circleId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<CircleMemberArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<CircleMemberArray> response = responseFuture.getResponse();

            System.out.println("\n getCircleMembers returns: " + response.getEntity().size());
            return response.getEntity().toArray(new CircleMember[response.getEntity().size()]);
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long addBeatComment(RestClient restClient, final String token, final Long commentId, final String commenterEmail, final String commentText){
        TipCommentDoAddBeatCommentRequestBuilder tipCommentDoAddBeatCommentRequestBuilder = new TipCommentRequestBuilders().actionAddBeatComment();
        ActionRequest<Long> actionRequest = tipCommentDoAddBeatCommentRequestBuilder.commenterEmailParam(commenterEmail).commentIdParam(commentId).commentTextParam(commentText).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Long> responseFuture = restClient.sendRequest(actionRequest);
            Response<Long> response = responseFuture.getResponse();

            System.out.println("\n addBeatComment returns: " + response.getEntity());
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean addBeatLike(RestClient restClient, final String token, final Long commentId) {
        TipCommentDoAddBeatLikeRequestBuilder tipCommentDoAddBeatLikeRequestBuilder = new TipCommentRequestBuilders().actionAddBeatLike();
        ActionRequest<Boolean> actionRequest = tipCommentDoAddBeatLikeRequestBuilder.commentIdParam(commentId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Boolean> responseFuture = restClient.sendRequest(actionRequest);
            Response<Boolean> response = responseFuture.getResponse();

            System.out.println("\n addBeatComment returns: " + response.getEntity());
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BeatComment[] getBeatComments(RestClient restClient, final String token, final Long commentId) {
        TipCommentDoGetBeatCommentsRequestBuilder tipCommentDoGetBeatCommentsRequestBuilder = new TipCommentRequestBuilders().actionGetBeatComments();
        ActionRequest<BeatCommentArray> actionRequest = tipCommentDoGetBeatCommentsRequestBuilder.commentIdParam(commentId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<BeatCommentArray> responseFuture = restClient.sendRequest(actionRequest);
            Response<BeatCommentArray> response = responseFuture.getResponse();

            System.out.println("\n getBeatComments returns: " + response.getEntity().size());
            for(BeatComment beatComment : response.getEntity()){
                System.out.println(beatComment);
            }
            return response.getEntity().toArray(new BeatComment[response.getEntity().size()]);
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BeatComments getBeatCommentsWithLikeStatus(RestClient restClient, final String token, final Long commentId) {
        TipCommentDoGetBeatCommentsWithLikeStatusRequestBuilder tipCommentDoGetBeatCommentsWithLikeStatusRequestBuilder = new TipCommentRequestBuilders().actionGetBeatCommentsWithLikeStatus();
        ActionRequest<BeatComments> actionRequest = tipCommentDoGetBeatCommentsWithLikeStatusRequestBuilder.commentIdParam(commentId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<BeatComments> responseFuture = restClient.sendRequest(actionRequest);
            Response<BeatComments> response = responseFuture.getResponse();

            System.out.println("\n getBeatCommentsWithLikeStatus returns: " + response.getEntity().getBeatComments().size());
            System.out.println("\n getBeatCommentsWithLikeStatus returns: " + response.getEntity().isLikeStatus());
            for(BeatComment beatComment : response.getEntity().getBeatComments()){
                System.out.println(beatComment);
            }
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean deleteBeatComment(RestClient restClient, final String token, final Long beatCommentId){
        TipCommentDoDeleteBeatCommentRequestBuilder tipCommentDoDeleteBeatCommentRequestBuilder = new TipCommentRequestBuilders().actionDeleteBeatComment();
        ActionRequest<Boolean> actionRequest = tipCommentDoDeleteBeatCommentRequestBuilder.beatCommentIdParam(beatCommentId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Boolean> responseFuture = restClient.sendRequest(actionRequest);
            Response<Boolean> response = responseFuture.getResponse();

            System.out.println("\n deleteBeatComment " + (response.getEntity() ? "succeeded" : "failed"));
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long addCommentSharing(RestClient restClient, final String token, final Long commentId, final Long circleId){
        TipCommentDoAddCommentSharingRequestBuilder tipCommentDoAddCommentSharingRequestBuilder = new TipCommentRequestBuilders().actionAddCommentSharing();
        ActionRequest<Long> actionRequest = tipCommentDoAddCommentSharingRequestBuilder.commentIdParam(commentId).circleIdParam(circleId).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Long> responseFuture = restClient.sendRequest(actionRequest);
            Response<Long> response = responseFuture.getResponse();

            System.out.println("\n addCommentSharing returns: " + response.getEntity());
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean setLikeStatus(RestClient restClient, final Long commentId, final Boolean isBeat, final Boolean isLike, final String token){
        TipCommentDoSetLikeStatusRequestBuilder tipCommentDoSetLikeStatusRequestBuilder = new TipCommentRequestBuilders().actionSetLikeStatus();
        ActionRequest<Boolean> actionRequest = tipCommentDoSetLikeStatusRequestBuilder.commentIdParam(commentId).isBeatParam(isBeat).isLikeParam(isLike).addHeader(POVI_AUTHORIZATION_HEADER, token).build();

        try{
            ResponseFuture<Boolean> responseFuture = restClient.sendRequest(actionRequest);
            Response<Boolean> response = responseFuture.getResponse();

            System.out.println("\n setLikeStatus returns: " + response.getEntity());
            return response.getEntity();
        }catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }
}

