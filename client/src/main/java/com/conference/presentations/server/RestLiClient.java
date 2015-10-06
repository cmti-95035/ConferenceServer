package com.conference.presentations.server;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Random;
import java.util.Collections;

import com.linkedin.common.callback.FutureCallback;
import com.linkedin.common.util.None;
import com.linkedin.data.template.IntegerArray;
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


public class RestLiClient {
    public static String CONFERENCE_AUTHORIZATION_HEADER = "conference-authorization";
    public static String FAKE_CONFERENCE_TOKEN = "abc";
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

    /**
     * This stand-alone app demos the client-side Pegasus API.
     * To see the demo, run RestLiFortuneServer, then start the client
     */
    public static void main(String[] args) throws Exception {
        // Create an HttpClient and wrap it in an abstraction layer
        final HttpClientFactory http = new HttpClientFactory();
        Client r2Client = new TransportClientAdapter(
                http.getClient(Collections.<String, String>emptyMap()));


        // Create a RestClient to talk to the specified server IP:port
        RestClient restClient = new RestClient(r2Client, "http://localhost:7777/");
//
//        ResearchFieldArray fieldArray = getAllFields(restClient);
//        for(ResearchField researchField : fieldArray){
//            System.out.println("\ngetAllFields returns:" + researchField);
//        }
//        IntegerArray integers = new IntegerArray();
//        integers.add(1);
//        integers.add(5);
//        String email = String.valueOf(random.nextInt()) + "@EmaiL.Com";
//        Integer userId = createUser(restClient, email, String.valueOf(random.nextInt()), "abc", String.valueOf(random.nextInt()), "USA", String.valueOf(random.nextInt()), integers);
//
//        User user = getUser(restClient, userId);
//        System.out.println("\ngetUser via id returns:" + user);
//
//        user.setPassword("xyz");
//        updateUser(restClient, user, userId);
//
//        User user2 = getUserProfile(restClient, email.toUpperCase());
//        System.out.println("\ngetUser via email returns:" + user2);
//
//        UserArray userArray = getAllUsers(restClient);
//        for(User u : userArray){
//            System.out.println("\ngetAllUsers returns:" + u);
//        }
////        deleteUser(restClient, userId);
//
//        Conference conference = new Conference()
//                .setConferenceTime("12/23/2014-12/27/2014")
//                .setVenue("Boston")
//                .setName("ACS Material Science Conference")
//                .setEmails("abc@gmail.com,xyz@yahoo.com")
//                .setOrganizer("ACS")
//                .setFields(integers);
//        Integer conferenceId = createConference(restClient, conference);
//
//        conference = getConference(restClient, conferenceId);
//        System.out.println("\nget conference: " + conference);
//        conference.setWebsite("www.acs.org");
//
//        updateConference(restClient, conference, conferenceId);
//
//        Conference newConference = getConference(restClient, conferenceId);
//
//        System.out.println("\nget conference: " + newConference);
//
//        ConferenceArray conferenceArray = getAllConferences(restClient);
//        for(Conference c : conferenceArray){
//            System.out.println("getAllConferences returns : " + c);
//        }
//
//        Presentation presentation = new Presentation()
//                .setAbs("first presentation")
//                .setAuthors("abc;xyz")
//                .setConference(newConference)
//                .setTitle("best practice")
//                .setIsPrivate(false)
//                .setUser(user);
//
//        Integer presentationId = createPresentation(restClient, presentation);
//
//        presentation = getPresentation(restClient, presentationId);
//        System.out.println("\ngetPresentation returns: " + presentation);
//
//        presentation.setFileName("report.pdf");
//        updatePresentation(restClient, presentation, presentationId);
//
//        Presentation newPresentation = getPresentation(restClient, presentationId);
//        System.out.println("\nafter update it returns: " + newPresentation);
//
//        PresentationArray presentationArray = getAllPresentations(restClient);
//        for(Presentation p : presentationArray){
//            System.out.println("getAllPresentations returns : " + p);
//        }

        UnicefRequest unicefRequest = new UnicefRequest()
                .setName("Jason")
                .setDetail("Needs Vaccine")
                .setIdentifier("4089999999")
                .setStatus(RequestState.INCOMING);

        Integer requestId = createUnicefRequest(restClient, unicefRequest);

        UnicefRequest unicefRequest2 = getUnicefRequest(restClient, requestId);
        System.out.println("\n getUnicefRequest returns: " + unicefRequest2);

        unicefRequest2.setLatitude(36.11947);
        unicefRequest2.setLongitute(-115.160692);
        updateUnicefRequest(restClient, unicefRequest2, requestId);

        unicefRequest2 = getUnicefRequest(restClient, requestId);
        System.out.println("\n after update getUnicefRequest returns: " + unicefRequest2);

        UnicefRequestArray unicefRequestArray = getAllUnicefRequests(restClient);
        for(UnicefRequest u : unicefRequestArray){
            System.out.println("\ngetAllUnicefRequests returns: " + u);
        }
        restClient.shutdown(new FutureCallback<None>());
        http.shutdown(new FutureCallback<None>());
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


    public static Integer createUser(RestClient restClient, String email, String name, String password, String address, String country, String phoneNumber, List<Integer> fields) {
        try {
            // Construct a request for the specified fortune
            UserCreateRequestBuilder rb = new UserRequestBuilders().create();
            CreateIdRequest<Integer, User> createReq = rb.input(new User().setEmail(email).setPassword(password).setPhoneNumber(phoneNumber).setName(name).setAddress(address).setFields(new IntegerArray(fields)).setCountry(country)).build();

                    System.out.println("\ncreate user request: " + createReq);
            // Send the request and wait for a response
            final ResponseFuture<IdResponse<Integer>> getFuture = restClient.sendRequest(createReq);
            final Response<IdResponse<Integer>> resp = getFuture.getResponse();

            Integer userId = resp.getEntity().getId();
            // Print the response
            System.out.println("\ncreate user returns: " + userId);
            return userId;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            System.out.println("\ncreate user failed!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static User getUser(RestClient restClient, Integer userId) {
        GetRequest<User> getReq = new UserRequestBuilders().get().id(userId).addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

        System.out.println("\nget user request: " + getReq);
        // Send the request and wait for a response
        final ResponseFuture<User> getFuture = restClient.sendRequest(getReq);
        final Response<User> resp;
        try {
            resp = getFuture.getResponse();
            User user = resp.getEntity();

            return user;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateUser(RestClient restClient, final User newUser, final Integer userId) {
        // Creating the profile update request builder
        UserUpdateRequestBuilder updateRequestBuilder = new UserRequestBuilders().update();

        UpdateRequest updateReq = updateRequestBuilder.id(userId).input(newUser)
                .addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

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

    public static void deleteUser(RestClient restClient, Integer userId) {
        try {
            UserDeleteRequestBuilder rb = new UserRequestBuilders().delete();
            DeleteRequest<User> deleteRequest = rb.id(userId).build();

            final ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            final Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println("\ndeleteUser returns: " + response.getStatus());
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }
    }

    public static User getUserProfile(RestClient restClient, final String email) {
        // Construct a request for the specified fortune
        UserDoGetUserFromEmailRequestBuilder rb = new UserRequestBuilders().actionGetUserFromEmail().emailParam(email);
        ActionRequest<User> getReq = rb.build();

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

    public static UserArray getAllUsers(RestClient restClient){
        // Construct a request for the specified fortune
        UserDoGetAllUsersRequestBuilder rb = new UserRequestBuilders().actionGetAllUsers();
        ActionRequest<UserArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<UserArray> getFuture = restClient.sendRequest(getReq);
        final Response<UserArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResearchFieldArray getAllFields(RestClient restClient){
        // Construct a request for the specified fortune
        UserDoGetAllResearchFieldsRequestBuilder rb = new UserRequestBuilders().actionGetAllResearchFields();
        ActionRequest<ResearchFieldArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<ResearchFieldArray> getFuture = restClient.sendRequest(getReq);
        final Response<ResearchFieldArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer createConference(RestClient restClient, Conference conference) {
        try {
            // Construct a request for the specified fortune
            ConferenceCreateRequestBuilder rb = new ConferenceRequestBuilders().create();
            CreateIdRequest<Integer, Conference> createReq = rb.input(conference).build();

            System.out.println("\ncreate conference request: " + createReq);
            // Send the request and wait for a response
            final ResponseFuture<IdResponse<Integer>> getFuture = restClient.sendRequest(createReq);
            final Response<IdResponse<Integer>> resp = getFuture.getResponse();

            Integer conferenceId = resp.getEntity().getId();
            // Print the response
            System.out.println("\ncreate conference returns: " + conferenceId);
            return conferenceId;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            System.out.println("\ncreate conference failed!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Conference getConference(RestClient restClient, Integer conferenceId) {
        GetRequest<Conference> getReq = new ConferenceRequestBuilders().get().id(conferenceId).addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

        System.out.println("\nget conference request: " + getReq);
        // Send the request and wait for a response
        final ResponseFuture<Conference> getFuture = restClient.sendRequest(getReq);
        final Response<Conference> resp;
        try {
            resp = getFuture.getResponse();
            Conference conference = resp.getEntity();

            return conference;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateConference(RestClient restClient, final Conference newConference, final Integer conferenceId) {
        // Creating the profile update request builder
        ConferenceUpdateRequestBuilder updateRequestBuilder = new ConferenceRequestBuilders().update();

        UpdateRequest updateReq = updateRequestBuilder.id(conferenceId).input(newConference)
                .addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

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

    public static void deleteConference(RestClient restClient, Integer conferenceId) {
        try {
            ConferenceDeleteRequestBuilder rb = new ConferenceRequestBuilders().delete();
            DeleteRequest<Conference> deleteRequest = rb.id(conferenceId).build();

            final ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            final Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println("\ndeleteConference returns: " + response.getStatus());
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }
    }

    public static ConferenceArray getAllConferences(RestClient restClient){
        // Construct a request for the specified fortune
        ConferenceDoGetAllConferencesRequestBuilder rb = new ConferenceRequestBuilders().actionGetAllConferences();
        ActionRequest<ConferenceArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<ConferenceArray> getFuture = restClient.sendRequest(getReq);
        final Response<ConferenceArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Integer createPresentation(RestClient restClient, Presentation presentation) {
        try {
            // Construct a request for the specified fortune
            PresentationCreateRequestBuilder rb = new PresentationRequestBuilders().create();
            CreateIdRequest<Integer, Presentation> createReq = rb.input(presentation).build();

            System.out.println("\ncreate presentation request: " + createReq);
            // Send the request and wait for a response
            final ResponseFuture<IdResponse<Integer>> getFuture = restClient.sendRequest(createReq);
            final Response<IdResponse<Integer>> resp = getFuture.getResponse();

            Integer presentationId = resp.getEntity().getId();
            // Print the response
            System.out.println("\ncreate presentation returns: " + presentationId);
            return presentationId;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            System.out.println("\ncreate presentation failed!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Presentation getPresentation(RestClient restClient, Integer presentationId) {
        GetRequest<Presentation> getReq = new PresentationRequestBuilders().get().id(presentationId).addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

        System.out.println("\nget presentation request: " + getReq);
        // Send the request and wait for a response
        final ResponseFuture<Presentation> getFuture = restClient.sendRequest(getReq);
        final Response<Presentation> resp;
        try {
            resp = getFuture.getResponse();
            Presentation presentation = resp.getEntity();

            return presentation;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updatePresentation(RestClient restClient, final Presentation newPresentation, final Integer presentationId) {
        // Creating the profile update request builder
        PresentationUpdateRequestBuilder updateRequestBuilder = new PresentationRequestBuilders().update();

        UpdateRequest updateReq = updateRequestBuilder.id(presentationId).input(newPresentation)
                .addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

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

    public static void deletePresentation(RestClient restClient, Integer presentationId) {
        try {
            PresentationDeleteRequestBuilder rb = new PresentationRequestBuilders().delete();
            DeleteRequest<Presentation> deleteRequest = rb.id(presentationId).build();

            final ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            final Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println("\ndeletePresentation returns: " + response.getStatus());
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }
    }

    public static PresentationArray getAllPresentations(RestClient restClient){
        // Construct a request for the specified fortune
        PresentationDoGetAllPresentationsRequestBuilder rb = new PresentationRequestBuilders().actionGetAllPresentations();
        ActionRequest<PresentationArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<PresentationArray> getFuture = restClient.sendRequest(getReq);
        final Response<PresentationArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer createUnicefRequest(RestClient restClient, UnicefRequest unicefRequest) {
        try {
            // Construct a request for the specified fortune
            UnicefRequestCreateRequestBuilder rb = new UnicefRequestRequestBuilders().create();
            CreateIdRequest<Integer, UnicefRequest> createReq = rb.input(unicefRequest).build();

            System.out.println("\ncreate unicefRequest request: " + createReq);
            // Send the request and wait for a response
            final ResponseFuture<IdResponse<Integer>> getFuture = restClient.sendRequest(createReq);
            final Response<IdResponse<Integer>> resp = getFuture.getResponse();

            Integer unicefRequestId = resp.getEntity().getId();
            // Print the response
            System.out.println("\ncreate unicefRequest returns: " + unicefRequestId);
            return unicefRequestId;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            System.out.println("\ncreate unicefRequest failed!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static UnicefRequest getUnicefRequest(RestClient restClient, Integer unicefRequestId) {
        GetRequest<UnicefRequest> getReq = new UnicefRequestRequestBuilders().get().id(unicefRequestId).addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

        System.out.println("\nget unicefRequest request: " + getReq);
        // Send the request and wait for a response
        final ResponseFuture<UnicefRequest> getFuture = restClient.sendRequest(getReq);
        final Response<UnicefRequest> resp;
        try {
            resp = getFuture.getResponse();
            UnicefRequest unicefRequest = resp.getEntity();

            return unicefRequest;
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateUnicefRequest(RestClient restClient, final UnicefRequest newUnicefRequest, final Integer unicefRequestId) {
        // Creating the profile update request builder
        UnicefRequestUpdateRequestBuilder updateRequestBuilder = new UnicefRequestRequestBuilders().update();

        UpdateRequest updateReq = updateRequestBuilder.id(unicefRequestId).input(newUnicefRequest)
                .addHeader(CONFERENCE_AUTHORIZATION_HEADER, FAKE_CONFERENCE_TOKEN).build();

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

    public static void deleteUnicefRequest(RestClient restClient, Integer unicefRequestId) {
        try {
            UnicefRequestDeleteRequestBuilder rb = new UnicefRequestRequestBuilders().delete();
            DeleteRequest<UnicefRequest> deleteRequest = rb.id(unicefRequestId).build();

            final ResponseFuture<EmptyRecord> responseFuture = restClient.sendRequest(deleteRequest);
            final Response<EmptyRecord> response = responseFuture.getResponse();

            System.out.println("\ndeleteUnicefRequest returns: " + response.getStatus());
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
        }
    }

    public static UnicefRequestArray getAllUnicefRequests(RestClient restClient){
        // Construct a request for the specified fortune
        UnicefRequestDoGetAllUnicefRequestsRequestBuilder rb = new UnicefRequestRequestBuilders().actionGetAllUnicefRequests();
        ActionRequest<UnicefRequestArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<UnicefRequestArray> getFuture = restClient.sendRequest(getReq);
        final Response<UnicefRequestArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UnicefRequestArray getAllUnicefIncomingRequests(RestClient restClient){
        // Construct a request for the specified fortune
        UnicefRequestDoGetAllUnicefRequestsRequestBuilder rb = new UnicefRequestRequestBuilders().actionGetAllUnicefRequests();
        ActionRequest<UnicefRequestArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<UnicefRequestArray> getFuture = restClient.sendRequest(getReq);
        final Response<UnicefRequestArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UnicefRequestArray getAllUnicefInprogressRequests(RestClient restClient){
        // Construct a request for the specified fortune
        UnicefRequestDoGetAllUnicefRequestsRequestBuilder rb = new UnicefRequestRequestBuilders().actionGetAllUnicefRequests();
        ActionRequest<UnicefRequestArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<UnicefRequestArray> getFuture = restClient.sendRequest(getReq);
        final Response<UnicefRequestArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UnicefRequestArray getAllUnicefCompletedRequests(RestClient restClient){
        // Construct a request for the specified fortune
        UnicefRequestDoGetAllUnicefRequestsRequestBuilder rb = new UnicefRequestRequestBuilders().actionGetAllUnicefRequests();
        ActionRequest<UnicefRequestArray> getReq = rb.build();

        // Send the request and wait for a response
        final ResponseFuture<UnicefRequestArray> getFuture = restClient.sendRequest(getReq);
        final Response<UnicefRequestArray> resp;
        try {
            resp = getFuture.getResponse();
            return resp.getEntity();
        } catch (RemoteInvocationException e) {
            e.printStackTrace();
            return null;
        }
    }

}

