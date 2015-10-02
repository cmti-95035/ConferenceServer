package com.conference.presentations.server.utils.aws;

import java.io.IOException;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.conference.presentations.server.db.mysql.DBUtilities;
import com.conference.presentations.server.db.mysql.SymmetricEncryptionUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsEmailClient {
    private static Logger _log = LoggerFactory.getLogger(AwsEmailClient.class);
    static final String FROM = "noreply@povi.me";

    static final String inviteEmailBody = "Hi,\n" +
            "I am getting bored of asking my kid, \"How's your day?\" or \"How's school?\" everyday, and getting a \"Fine\" or \"ok\" from their replies.\n" +
            "Povi Family Connect app (%s) sends me inspirations daily like \"Did you help anyone today?\" \"Why is it nice to give people compliments?\" I get really fun answers from my kid like \"I helped a friend in school chase bees away by throwing soccer ball at them\" that I have recorded in their daily journal.\n" +
            "Perhaps you want to give Povi Family Connect app (%s) a try too?\n" +
            "Thank you.\n" +
            "%s";
    static final String inviteEmailSubject = "I am using Povi Family Connect app";
    static final String playStoreDownloadLink = "";
    static final String appleStoreDownloadLink = "";

    public static boolean sendInviteEmail(String toAddress, String invitorName, Boolean isAndroid) throws IOException{
        String link = isAndroid ? playStoreDownloadLink : appleStoreDownloadLink;
        String body = String.format(inviteEmailBody, link, link, invitorName);

        return sendEmail(toAddress, inviteEmailSubject, body);
    }

    public static boolean sendEmail(String toAddress, String subject, String body) throws IOException {    	
                
        // Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(toAddress);
        
        // Create the subject and body of the message.
        Content contentSubject = new Content().withData(subject);
        Content textBody = new Content().withData(body); 
        Body contentBody = new Body().withText(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(contentSubject).withBody(contentBody);
        
        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);

        try
        {
            _log.debug("Attempting to send an email through Amazon SES by using the AWS SDK for Java to: " + toAddress);
        
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(SymmetricEncryptionUtility.decrypt(AwsConstants.AWS_ACCESS_KEY), SymmetricEncryptionUtility.decrypt(AwsConstants.AWS_SECRET_KEY));
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCreds);

            Region REGION = Region.getRegion(Regions.US_EAST_1);
            client.setRegion(REGION);
       
            // Send the email.
            client.sendEmail(request);  
            _log.debug("Email sent successfully to " + toAddress);
            
            return true;
        }
        catch (Exception ex) 
        {
            _log.error("The email was not sent to " + toAddress + " with error: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
            
            return false;
        }
    }
}
