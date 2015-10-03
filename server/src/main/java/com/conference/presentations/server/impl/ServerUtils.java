package com.conference.presentations.server.impl;

import com.conference.presentations.server.ds.ConferenceDataService;
import com.conference.presentations.server.ds.ConferenceDataServiceImpl;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.RestLiServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class ServerUtils {

    private static Logger _log = LoggerFactory.getLogger(ServerUtils.class);
    public static String SEPERATOR = ":";
    public static String CONFERENCE_AUTHORIZATION_HEADER = "conference-authorization";
    public static String ALPHABETES = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789_#?!&*";

    private static String conferenceDbName = "conference_schema";
    private static String dbUrl = "localhost";
    private static String conferenceUserName = "conference";
    private static String conferencePassword = "conference";

    private static Random random = new Random();

    /**
     * generate a random password with numeric alphabets to a specified length
     * @param length
     * @return
     */
    public static String generateRandomPassword(int length)
    {
        if(length<=0)
            length = 8;

        StringBuilder sb = new StringBuilder(length);

        for(int i=0; i<length; i++){
            sb.append(ALPHABETES.charAt(random.nextInt(ALPHABETES.length())));
        }

        return sb.toString();
    }

    public static String generateToken(String email) {
        String data = email.toLowerCase() + Long.toString(System.currentTimeMillis());
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new BigInteger(1, digest.digest()).toString(16);
    }

    /**
     * method to generate hash based on user's email and password
     * @param email
     * @param password
     * @return
     */
    public static String generateHash(String email, String password){
        String data = email + password;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hash = new BigInteger(1, digest.digest()).toString(16);

        return hash;
    }
    /**
     * method to make sure a request has the conference-authorization header
     *
     * @param headerMap
     * @param scenario
     */
    public static String checkHeader(Map<String, String> headerMap, String scenario) {
        //TODO: may need to evaluate the token passed in with the request
        // represents a valid session
        if (headerMap == null || !headerMap.containsKey(CONFERENCE_AUTHORIZATION_HEADER)) {
            _log.warn("missing " + CONFERENCE_AUTHORIZATION_HEADER + " in the headers for " + scenario );
            return null;
//            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST,
//                    CONFERENCE_AUTHORIZATION_HEADER + " header must present in request!");
        }
        else {
            String token = headerMap.get(CONFERENCE_AUTHORIZATION_HEADER);
            _log.debug(scenario + "+++" + CONFERENCE_AUTHORIZATION_HEADER + ": " + token + " from IP: " + getIPFromHeader(headerMap));
            return token;
        }
    }

    private static String getIPFromHeader(Map<String, String> headerMap){
            String ip = headerMap.get("x-forwarded-for");
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headerMap.get("Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headerMap.get("WL-Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = "";
            }
            return ip;
    }
    public static ConferenceDataService initPoviDataService(ConferenceDataService ds) {
        if (ds == null) {
            _log.debug("initialize ConferenceDataService");
            ds = new ConferenceDataServiceImpl(dbUrl, conferenceDbName, conferenceUserName, conferencePassword);
        }

        return ds;
    }

    public static Long convertFromDateString(String dateStr) {
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        long timestamp = 0;
        try {
            timestamp = fromUser.parse(dateStr).getTime();
        } catch (ParseException pex) {
            return null;
        }

        return timestamp;
    }

    public static String currentTimestamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
