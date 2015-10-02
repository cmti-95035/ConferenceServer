package com.conference.presentations.server.utils.aws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.conference.presentations.server.db.mysql.DBUtilities;
import com.conference.presentations.server.db.mysql.SymmetricEncryptionUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsS3Client {
    private static Logger _log = LoggerFactory.getLogger(AwsS3Client.class);
    private static String bucketName     = "povifamilyconnectchildrenimages";

    public static boolean upload(String remoteFile, String uploadFileName) throws IOException {
        boolean result = false;
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            _log.error("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            s3client.putObject(new PutObjectRequest(
                    bucketName, remoteFile, file));

            result = true;
        } catch (AmazonServiceException ase) {
            _log.error("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            _log.error("Error Message:    " + ase.getMessage());
            _log.error("HTTP Status Code: " + ase.getStatusCode());
            _log.error("AWS Error Code:   " + ase.getErrorCode());
            _log.error("Error Type:       " + ase.getErrorType());
            _log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            _log.error("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            _log.error("Error Message: " + ace.getMessage());
        }

        return result;
    }
    
    public static boolean download(String remoteFile, String localFile) throws IOException {
        boolean result = false;

        try {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(SymmetricEncryptionUtility.decrypt(AwsConstants.AWS_ACCESS_KEY), SymmetricEncryptionUtility.decrypt(AwsConstants.AWS_SECRET_KEY));
            AmazonS3 s3Client = new AmazonS3Client(awsCreds);
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucketName, remoteFile));

            InputStream initialStream = s3object.getObjectContent();
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            File targetFile = new File(localFile);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);

            result = true;
        } catch (AmazonServiceException ase) {
            _log.error("Caught an AmazonServiceException, which" +
                    " means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            _log.error("Error Message:    " + ase.getMessage());
            _log.error("HTTP Status Code: " + ase.getStatusCode());
            _log.error("AWS Error Code:   " + ase.getErrorCode());
            _log.error("Error Type:       " + ase.getErrorType());
            _log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            _log.error("Caught an AmazonClientException, which means" +
                    " the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            _log.error("Error Message: " + ace.getMessage());
        }catch (Exception ex){
            _log.error("Error Message: " + ex.getMessage());
            DBUtilities.printStackTrace(_log, ex.getStackTrace());
        }

        return result;
    }
}
